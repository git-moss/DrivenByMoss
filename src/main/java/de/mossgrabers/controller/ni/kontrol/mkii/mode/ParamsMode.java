// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.kontrol.mkii.mode;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.mossgrabers.controller.ni.kontrol.mkii.KontrolProtocolConfiguration;
import de.mossgrabers.controller.ni.kontrol.mkii.TrackType;
import de.mossgrabers.controller.ni.kontrol.mkii.controller.KontrolProtocolControlSurface;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.constants.DeviceID;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.ICursorLayer;
import de.mossgrabers.framework.daw.data.IDevice;
import de.mossgrabers.framework.daw.data.ILayer;
import de.mossgrabers.framework.daw.data.ISpecificDevice;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.IDeviceBank;
import de.mossgrabers.framework.daw.data.bank.ILayerBank;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.daw.data.bank.IParameterPageBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.featuregroup.AbstractParameterMode;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.parameterprovider.IParameterProvider;
import de.mossgrabers.framework.parameterprovider.device.BankParameterProvider;
import de.mossgrabers.framework.parameterprovider.device.SelectedLayerDeviceBankParameterProvider;
import de.mossgrabers.framework.utils.Pair;


/**
 * The parameters mode.
 *
 * @author Jürgen Moßgraber
 */
public class ParamsMode extends AbstractParameterMode<KontrolProtocolControlSurface, KontrolProtocolConfiguration, IParameter>
{
    private static final String []      BANK_NAMES               =
    {
        "Cursor Device ",
        "Track ",
        "Project ",
        "Layer"
    };

    private static final int            LAYER_INDEX              = 3;

    private final List<IParameterBank>  banks                    = new ArrayList<> (4);
    private final IParameterProvider [] providers                = new IParameterProvider [4];
    private int                         activeProviderIndex      = 0;
    private boolean                     layerSubModeEnabled      = false;
    private int                         previouslySelectedDevice = -1;
    private long                        previouslyDeviceChange   = System.currentTimeMillis ();
    private int                         currentlySelectedDevice  = -1;
    private int                         currentlySelectedTab     = -1;
    private String                      currentChainInfo         = "";
    private boolean                     pageSelectMode           = false;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     * @param controls The IDs of the knobs or faders to control this mode
     */
    public ParamsMode (final KontrolProtocolControlSurface surface, final IModel model, final List<ContinuousID> controls)
    {
        super ("Parameters", surface, model, false);

        this.setControls (controls);

        this.banks.add (model.getCursorDevice ().getParameterBank ());
        this.banks.add (model.getCursorTrack ().getParameterBank ());
        this.banks.add (model.getProject ().getParameterBank ());

        for (int i = 0; i < this.banks.size (); i++)
            this.providers[i] = new BankParameterProvider (this.banks.get (i));

        final SelectedLayerDeviceBankParameterProvider provider = new SelectedLayerDeviceBankParameterProvider (model.getCursorLayer (), model.getCursorDevice ().getParameterBank ().getPageSize ());
        this.providers[LAYER_INDEX] = provider;
        this.providers[LAYER_INDEX].addParametersObserver (() -> {

            this.banks.set (LAYER_INDEX, provider.getBank ());
            if (this.layerSubModeEnabled)
                this.selectProvider (LAYER_INDEX);

        });

        this.banks.add (provider.getBank ());

        this.selectProvider (0);
    }


    /**
     * Dis-/enable the layer sub-mode.
     *
     * @param enable True to enable
     */
    public void enableLayerSubMode (final boolean enable)
    {
        this.layerSubModeEnabled = enable;

        this.selectProvider (enable ? LAYER_INDEX : 0);
    }


    /**
     * Toggles between Device, Track and Project parameters.
     */
    public void selectNextMode ()
    {
        this.selectProvider ((this.activeProviderIndex + 1) % 3);
    }


    /** {@inheritDoc} */
    @Override
    public int getKnobValue (final int index)
    {
        // Note: Since we need multiple value (more than 8), index is the MIDI CC of the knob

        final IValueChanger valueChanger = this.model.getValueChanger ();

        final IParameterBank parameterBank = this.banks.get (this.activeProviderIndex);
        if (index >= KontrolProtocolControlSurface.CC_PARAM_VALUE_CHANGE && index < KontrolProtocolControlSurface.CC_PARAM_VALUE_CHANGE + 8)
        {
            final IParameter parameter = parameterBank.getItem (index - KontrolProtocolControlSurface.CC_PARAM_VALUE_CHANGE);
            return valueChanger.toMidiValue (parameter.getValue ());
        }

        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        // The track name needs to be updated as well for different formatting
        final ICursorLayer cursorLayer = this.model.getCursorLayer ();
        if (this.layerSubModeEnabled)
        {
            // Make sure a layer is selected
            final ILayerBank layerBank = cursorLayer.getLayerBank ();
            if (layerBank.getSelectedItem ().isEmpty ())
                layerBank.getItem (0).select ();

            for (int i = 0; i < layerBank.getPageSize (); i++)
            {
                final ILayer layer = layerBank.getItem (i);
                this.surface.sendKontrolSysEx (KontrolProtocolControlSurface.SYSEX_TRACK_AVAILABLE, TrackType.toTrackType (layer.getType ()), i);
                this.surface.sendKontrolSysEx (KontrolProtocolControlSurface.SYSEX_TRACK_SELECTED, layer.isSelected () ? 1 : 0, i);
                final String layerName = "Layer " + (layer.getPosition () + 1) + ": " + layer.getName ();
                this.surface.sendKontrolSysEx (KontrolProtocolControlSurface.SYSEX_TRACK_NAME, 0, i, layerName);
            }
        }
        else
        {
            final ITrackBank trackBank = this.model.getTrackBank ();
            for (int i = 0; i < trackBank.getPageSize (); i++)
            {
                final ITrack track = trackBank.getItem (i);
                this.surface.sendKontrolSysEx (KontrolProtocolControlSurface.SYSEX_TRACK_AVAILABLE, TrackType.toTrackType (track.getType ()), i);
                this.surface.sendKontrolSysEx (KontrolProtocolControlSurface.SYSEX_TRACK_SELECTED, track.isSelected () ? 1 : 0, i);
                this.surface.sendKontrolSysEx (KontrolProtocolControlSurface.SYSEX_TRACK_NAME, 0, i, track.getName ());
            }
        }

        this.surface.sendGlobalValues (this.model);
    }


    private Pair<Integer, String> getSelectedDeviceInfo ()
    {
        final Optional<IDevice> selectedDevice;
        final String presetName;
        if (this.layerSubModeEnabled)
        {
            final ICursorLayer cursorLayer = this.model.getCursorLayer ();
            final Optional<ISpecificDevice> selectedSpecificDevice = cursorLayer.getSelectedDevice ();
            presetName = selectedSpecificDevice.isPresent () ? selectedSpecificDevice.get ().getPresetName () : "none";
            selectedDevice = selectedSpecificDevice.isPresent () ? Optional.of (selectedSpecificDevice.get ()) : Optional.empty ();
        }
        else
        {
            final ICursorDevice cursorDevice = this.model.getCursorDevice ();
            final IDeviceBank deviceBank = cursorDevice.getDeviceBank ();
            presetName = cursorDevice.getPresetName ();
            selectedDevice = deviceBank.getSelectedItem ();
        }

        final int deviceIndex = selectedDevice.isPresent () ? selectedDevice.get ().getIndex () : 0;
        return new Pair<> (Integer.valueOf (deviceIndex), presetName);
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItem ()
    {
        this.model.getCursorDevice ().selectPrevious ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItem ()
    {
        this.model.getCursorDevice ().selectNext ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItemPage ()
    {
        // Jump to the previous 8 pages since the pages can be selected directly
        if (this.isPageSelectModeActive ())
        {
            this.banks.get (this.activeProviderIndex).getPageBank ().selectPreviousPage ();
            return;
        }

        this.banks.get (this.activeProviderIndex).scrollBackwards ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItemPage ()
    {
        // Jump to the next 8 pages since the pages can be selected directly
        if (this.isPageSelectModeActive ())
        {
            this.banks.get (this.activeProviderIndex).getPageBank ().selectNextPage ();
            return;
        }

        this.banks.get (this.activeProviderIndex).scrollForwards ();
    }


    private void selectProvider (final int index)
    {
        this.activeProviderIndex = Math.clamp (index, 0, 3);

        final IParameterBank parameterBank = this.banks.get (this.activeProviderIndex);
        this.switchBanks (parameterBank);
        this.setParameterProvider (this.providers[this.activeProviderIndex]);
        this.bindControls ();

        this.mvHelper.notifySelectedParameterPage (parameterBank, BANK_NAMES[this.activeProviderIndex]);
    }


    /**
     * The selected device has changed. If page selection mode is active, the index is the one of
     * the selected parameter page. Pressing the already selected device (or SHIFT + device) toggles
     * the page selection mode.
     *
     * @param deviceIndex The index of the selected device or parameter page
     */
    public void selectedDeviceHasChanged (final int deviceIndex)
    {
        if (!this.isTrackOrProjectMode ())
        {
            final boolean isShiftPressed = this.surface.isShiftPressed ();
            if (this.isPageSelectModeActive ())
            {
                final IParameterPageBank parameterPageBank = this.banks.get (this.activeProviderIndex).getPageBank ();
                if (isShiftPressed || deviceIndex == parameterPageBank.getSelectedItemIndex ())
                    this.setPageSelectMode (false);
                else if (parameterPageBank.getScrollPosition () + deviceIndex < parameterPageBank.getItemCount ())
                    parameterPageBank.selectPage (deviceIndex);
                return;
            }

            if (isShiftPressed || deviceIndex == this.getSelectedDeviceInfo ().getKey ().intValue ())
                this.setPageSelectMode (true);
        }

        final IDeviceBank deviceBank;
        if (this.layerSubModeEnabled)
        {
            deviceBank = this.model.getCursorLayer ().getDeviceBank ();
        }
        else
        {
            final ICursorDevice cursorDevice = this.model.getCursorDevice ();
            if (!cursorDevice.doesExist ())
                return;
            deviceBank = cursorDevice.getDeviceBank ();
        }
        final IDevice item = deviceBank.getItem (deviceIndex);
        if (item.doesExist ())
        {
            item.select ();

            if (this.layerSubModeEnabled)
            {
                this.surface.getHost ().scheduleTask (() -> {
                    ((SelectedLayerDeviceBankParameterProvider) this.providers[LAYER_INDEX]).configureCurrentBank ();
                    this.banks.set (LAYER_INDEX, ((SelectedLayerDeviceBankParameterProvider) this.providers[LAYER_INDEX]).getBank ());
                }, 100);
            }
        }
    }


    /**
     * Are layers shown?
     *
     * @return True if devices of an active layer are shown otherwise top level devices
     */
    public boolean isLayerSubModeEnabled ()
    {
        return this.layerSubModeEnabled;
    }


    private void setPageSelectMode (final boolean enable)
    {
        this.pageSelectMode = enable;

        // Force a refresh of the display buttons
        this.currentChainInfo = "";
    }


    /**
     * If page selection mode is not applicable (NKS device, device without pages) it is only
     * suspended, so that it stays active when another device gets selected.
     *
     * @return True if the display buttons show the parameter pages instead of the devices
     */
    private boolean isPageSelectModeActive ()
    {
        if (!this.pageSelectMode || this.isTrackOrProjectMode () || this.isNksDeviceSelected ())
            return false;
        final IParameterPageBank parameterPageBank = this.banks.get (this.activeProviderIndex).getPageBank ();
        return parameterPageBank != null && parameterPageBank.getItemCount () > 0;
    }


    /**
     * The NKS device is controlled by the special mode of the Kontrol, which does not notify the
     * extension.
     *
     * @return True if the selected device is the NKS device
     */
    private boolean isNksDeviceSelected ()
    {
        // A Komplete Kontrol instance inside of a layer does not use the NKS mode
        if (this.layerSubModeEnabled)
            return false;
        final ISpecificDevice nksDevice = this.model.getSpecificDevice (DeviceID.NI_KOMPLETE);
        return nksDevice.doesExist () && this.model.getCursorDevice ().getPosition () == nksDevice.getPosition ();
    }


    /**
     * Toggle between Cursor Device, Track and Project parameters.
     */
    public void updateAvailableDevices ()
    {
        String presetName = "";
        final StringBuilder chainInfoSb = new StringBuilder ();
        int deviceIndex = 0;
        int selectedTab = 0;

        final IParameterBank parameterBank = this.banks.get (this.activeProviderIndex);
        final IParameterPageBank parameterPageBank = parameterBank.getPageBank ();

        if (this.isTrackOrProjectMode ())
        {
            chainInfoSb.append (this.activeProviderIndex == 1 ? "Track\0" : "Project\0");
        }
        else
        {
            final Pair<Integer, String> pair = this.getSelectedDeviceInfo ();
            deviceIndex = pair.getKey ().intValue ();
            presetName = pair.getValue ();

            if (this.isPageSelectModeActive ())
            {
                // Show the parameter pages of the current page window instead of the devices
                final int scrollPosition = parameterPageBank.getScrollPosition ();
                for (int i = 0; i < 8 && scrollPosition + i < parameterPageBank.getItemCount (); i++)
                    chainInfoSb.append (parameterPageBank.getItem (i)).append ('\0');
                selectedTab = Math.max (0, parameterPageBank.getSelectedItemIndex ());
            }
            else
            {
                final IDeviceBank deviceBank;
                if (((ParamsMode) this.surface.getModeManager ().get (Modes.DEVICE_PARAMS)).isLayerSubModeEnabled ())
                    deviceBank = this.model.getCursorLayer ().getDeviceBank ();
                else
                    deviceBank = this.model.getCursorDevice ().getDeviceBank ();

                for (int i = 0; i < 8; i++)
                {
                    final IDevice device = deviceBank.getItem (i);
                    if (!device.doesExist ())
                        break;
                    chainInfoSb.append (device.getName ()).append ('\0');
                }
                selectedTab = deviceIndex;
            }
        }

        final String chainInfo = chainInfoSb.append ('\0').toString ();
        final boolean respectCache = this.currentChainInfo.equals (chainInfo) && this.currentlySelectedDevice == deviceIndex && this.currentlySelectedTab == selectedTab;
        this.currentlySelectedTab = selectedTab;
        if (this.currentlySelectedDevice != deviceIndex)
        {
            this.previouslyDeviceChange = System.currentTimeMillis ();
            // Blocks device switching for some time to workaround the not notified NKS mode
            this.previouslySelectedDevice = this.currentlySelectedDevice;
            this.currentlySelectedDevice = deviceIndex;
        }
        else if (System.currentTimeMillis () - this.previouslyDeviceChange > 1000)
        {
            // Free the blocked mode switching again
            this.previouslySelectedDevice = this.currentlySelectedDevice;
        }

        this.currentChainInfo = chainInfo;

        this.surface.sendKontrolSysEx (KontrolProtocolControlSurface.SYSEX_PLUGIN_SELECTED_PLUGIN, 0, selectedTab, respectCache);

        final Optional<String> selectedItem = parameterPageBank.getSelectedItem ();
        final String selectedPage = selectedItem.isPresent () ? selectedItem.get () : "";
        final int selectedPageIndex = Math.max (0, parameterPageBank.getSelectedItemIndex ());

        for (int i = 0; i < 8; i++)
        {
            final IParameter parameter = parameterBank.getItem (i);
            final String name = parameter.doesExist () ? parameter.getName () : "";
            final String info = parameter.doesExist () ? parameter.getDisplayedValue () : "";
            this.surface.sendKontrolSysEx (KontrolProtocolControlSurface.SYSEX_PLUGIN_PARAM_DISPLAY_NAME, 0, i, name, respectCache);
            this.surface.sendKontrolSysEx (KontrolProtocolControlSurface.SYSEX_PLUGIN_PARAM_DISPLAY_VALUE, 0, i, info, respectCache);
        }

        this.surface.sendKontrolSysEx (KontrolProtocolControlSurface.SYSEX_PLUGIN_PAGE_NAME, 0, 0, selectedPage, respectCache);
        this.surface.sendKontrolSysEx (KontrolProtocolControlSurface.SYSEX_PLUGIN_CHAIN_INFO, 0, 0, chainInfo, respectCache);
        this.surface.sendKontrolSysEx (KontrolProtocolControlSurface.SYSEX_PLUGIN_SELECTED_PARAM_PAGE, parameterPageBank.getItemCount (), selectedPageIndex, respectCache);
        this.surface.sendKontrolSysEx (KontrolProtocolControlSurface.SYSEX_PLUGIN_SELECTED_PRESET, 0, 0, presetName, respectCache);
        this.surface.sendKontrolSysEx (KontrolProtocolControlSurface.SYSEX_PLUGIN_SELECTED_PLUGIN, 0, selectedTab, respectCache);

        final ISpecificDevice kkDevice = this.model.getSpecificDevice (DeviceID.NI_KOMPLETE);
        final String kompleteInstanceNew = kkDevice.doesExist () ? kkDevice.getID () : "";
        this.surface.sendKontrolSysEx (KontrolProtocolControlSurface.SYSEX_TRACK_INSTANCE, 0, 0, kompleteInstanceNew, respectCache);
    }


    /**
     * Returns true if the track or project controls are active.
     *
     * @return True if the track or project controls are active.
     */
    public boolean isTrackOrProjectMode ()
    {
        return this.activeProviderIndex == 1 || this.activeProviderIndex == 2;
    }


    /**
     * Get the index of the previously selected device.
     *
     * @return The previously selected device
     */
    public int getPreviouslySelectedDevice ()
    {
        return this.previouslySelectedDevice;
    }
}
