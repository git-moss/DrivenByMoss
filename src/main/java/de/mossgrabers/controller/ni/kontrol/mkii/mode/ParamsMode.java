// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
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


/**
 * The parameters mode.
 *
 * @author Jürgen Moßgraber
 */
public class ParamsMode extends AbstractParameterMode<KontrolProtocolControlSurface, KontrolProtocolConfiguration, IParameter>
{
    private static final String []      BANK_NAMES                   =
    {
        "Cursor Device ",
        "Track ",
        "Project ",
        "Layer"
    };

    private static final int            LAYER_INDEX                  = 3;

    private final List<IParameterBank>  banks                        = new ArrayList<> (4);
    private final IParameterProvider [] providers                    = new IParameterProvider [4];
    private int                         activeProviderIndex          = 0;

    private long                        selectedDeviceHasChangedTime = -1;
    private int                         currentlySelectedDevice      = -1;
    private final Object                deviceChangeLock             = new Object ();
    private boolean                     layerSubModeEnabled          = false;


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
        this.providers[LAYER_INDEX].addParametersObserver ( () -> {

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
        this.surface.sendGlobalValues (this.model);

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

        String presetName = "";
        if (this.activeProviderIndex != 1 && this.activeProviderIndex != 2)
        {
            final Optional<IDevice> selectedDevice;
            if (this.layerSubModeEnabled)
            {
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

            // Ugly workaround to sync the state between the device selection managed on the
            // hardware and device selection changes initiated in the DAW
            synchronized (this.deviceChangeLock)
            {
                if (this.currentlySelectedDevice >= 0)
                {
                    if (deviceIndex == this.currentlySelectedDevice)
                    {
                        if (System.currentTimeMillis () - this.selectedDeviceHasChangedTime > 1000)
                            this.selectedDeviceHasChangedTime = -1;
                    }
                    else if (this.selectedDeviceHasChangedTime < 0)
                    {
                        this.currentlySelectedDevice = deviceIndex;
                        this.surface.sendKontrolSysEx (KontrolProtocolControlSurface.SYSEX_PLUGIN_SELECTED_PLUGIN, 0, deviceIndex, false);
                    }
                }
            }
        }

        final IParameterBank parameterBank = this.banks.get (this.activeProviderIndex);
        final IParameterPageBank parameterPageBank = parameterBank.getPageBank ();
        final Optional<String> selectedItem = parameterPageBank.getSelectedItem ();
        final String selectedPage = selectedItem.isPresent () ? selectedItem.get () : "";
        final int selectedPageIndex = Math.max (0, parameterPageBank.getSelectedItemIndex ());
        this.surface.sendKontrolSysEx (KontrolProtocolControlSurface.SYSEX_PLUGIN_SELECTED_PARAM_PAGE, parameterPageBank.getItemCount (), selectedPageIndex);
        this.surface.sendKontrolSysEx (KontrolProtocolControlSurface.SYSEX_PLUGIN_PAGE_NAME, 0, 0, selectedPage);

        this.surface.sendKontrolSysEx (KontrolProtocolControlSurface.SYSEX_PLUGIN_SELECTED_PRESET, 0, 0, presetName);

        for (int i = 0; i < 8; i++)
        {
            final IParameter parameter = parameterBank.getItem (i);

            final String name = parameter.doesExist () ? parameter.getName () : "";
            this.surface.sendKontrolSysEx (KontrolProtocolControlSurface.SYSEX_PLUGIN_PARAM_DISPLAY_NAME, 0, i, name);

            final String info = parameter.doesExist () ? parameter.getDisplayedValue () : "";
            this.surface.sendKontrolSysEx (KontrolProtocolControlSurface.SYSEX_PLUGIN_PARAM_DISPLAY_VALUE, 0, i, info);
        }
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
        this.banks.get (this.activeProviderIndex).scrollBackwards ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItemPage ()
    {
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
     * The selected device has changed.
     *
     * @param deviceIndex The index of the selected device
     */
    public void selectedDeviceHasChanged (final int deviceIndex)
    {
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
            synchronized (this.deviceChangeLock)
            {
                this.selectedDeviceHasChangedTime = System.currentTimeMillis ();
                this.currentlySelectedDevice = deviceIndex;
                item.select ();

                if (this.layerSubModeEnabled)
                {
                    this.surface.getHost ().scheduleTask ( () -> {
                        ((SelectedLayerDeviceBankParameterProvider) this.providers[LAYER_INDEX]).configureCurrentBank ();
                        this.banks.set (LAYER_INDEX, ((SelectedLayerDeviceBankParameterProvider) this.providers[LAYER_INDEX]).getBank ());
                    }, 100);
                }
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


    /**
     * Toggle between Cursor Device, Track and Project parameters.
     */
    public void updateAvailableDevices ()
    {
        final StringBuilder sb = new StringBuilder ();

        switch (this.activeProviderIndex)
        {
            case 1:
                sb.append ("Track\0");
                break;

            case 2:
                sb.append ("Project\0");
                break;

            default:
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
                    sb.append (device.getName (16)).append ('\0');
                }
                break;
        }

        this.surface.sendKontrolSysEx (KontrolProtocolControlSurface.SYSEX_PLUGIN_CHAIN_INFO, 0, 0, sb.append ('\0').toString ());
    }
}
