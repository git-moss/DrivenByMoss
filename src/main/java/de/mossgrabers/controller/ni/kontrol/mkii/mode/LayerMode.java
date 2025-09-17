// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.kontrol.mkii.mode;

import java.util.List;
import java.util.Optional;

import de.mossgrabers.controller.ni.kontrol.mkii.KontrolProtocolConfiguration;
import de.mossgrabers.controller.ni.kontrol.mkii.TrackType;
import de.mossgrabers.controller.ni.kontrol.mkii.controller.KontrolProtocol;
import de.mossgrabers.controller.ni.kontrol.mkii.controller.KontrolProtocolControlSurface;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.constants.DeviceID;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.ILayer;
import de.mossgrabers.framework.daw.data.ISpecificDevice;
import de.mossgrabers.framework.daw.data.bank.ISceneBank;
import de.mossgrabers.framework.mode.layer.DefaultLayerMode;
import de.mossgrabers.framework.parameterprovider.device.PanLayerOrDrumPadParameterProvider;
import de.mossgrabers.framework.parameterprovider.device.VolumeLayerOrDrumPadParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.CombinedParameterProvider;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * The layer mode.
 *
 * @author Jürgen Moßgraber
 */
public class LayerMode extends DefaultLayerMode<KontrolProtocolControlSurface, KontrolProtocolConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     * @param controls The IDs of the knobs or faders to control this mode
     */
    public LayerMode (final KontrolProtocolControlSurface surface, final IModel model, final List<ContinuousID> controls)
    {
        super ("Layer", surface, model, false);

        this.setControls (controls);
        final ISpecificDevice specificDevice = this.getDevice ();
        this.setParameterProvider (new CombinedParameterProvider (new VolumeLayerOrDrumPadParameterProvider (specificDevice), new PanLayerOrDrumPadParameterProvider (specificDevice)));
    }


    /** {@inheritDoc} */
    @Override
    public int getKnobValue (final int index)
    {
        // Note: Since we need multiple value (more than 8), index is the MIDI CC of the knob

        final IValueChanger valueChanger = this.model.getValueChanger ();

        if (index >= KontrolProtocolControlSurface.CC_TRACK_VOLUME && index < KontrolProtocolControlSurface.CC_TRACK_VOLUME + 8)
        {
            final ILayer layer = this.bank.getItem (index - KontrolProtocolControlSurface.CC_TRACK_VOLUME);
            return valueChanger.toMidiValue (layer.getVolume ());
        }

        if (index >= KontrolProtocolControlSurface.CC_TRACK_PAN && index < KontrolProtocolControlSurface.CC_TRACK_PAN + 8)
        {
            final ILayer layer = this.bank.getItem (index - KontrolProtocolControlSurface.CC_TRACK_PAN);
            return valueChanger.toMidiValue (layer.getPan ());
        }

        final int scrollLayersState = (this.bank.canScrollBackwards () ? 1 : 0) + (this.bank.canScrollForwards () ? 2 : 0);
        final int scrollClipsState = 0;

        final ISceneBank sceneBank = this.model.getSceneBank ();
        final int scrollScenesState = (sceneBank.canScrollBackwards () ? 1 : 0) + (sceneBank.canScrollForwards () ? 2 : 0);

        final KontrolProtocolConfiguration configuration = this.surface.getConfiguration ();

        switch (index)
        {
            case KontrolProtocolControlSurface.CC_NAVIGATE_BANKS:
                return (this.bank.canScrollPageBackwards () ? 1 : 0) + (this.bank.canScrollPageForwards () ? 2 : 0);
            case KontrolProtocolControlSurface.CC_NAVIGATE_TRACKS:
                if (configuration.isFlipTrackClipNavigation ())
                    return configuration.isFlipClipSceneNavigation () ? scrollScenesState : scrollClipsState;
                return scrollLayersState;
            case KontrolProtocolControlSurface.CC_NAVIGATE_CLIPS:
                if (configuration.isFlipTrackClipNavigation ())
                    return scrollLayersState;
                return configuration.isFlipClipSceneNavigation () ? scrollScenesState : scrollClipsState;
            default:
                return 0;
        }
    }


    /**
     * Toggles the mute state of a layer.
     *
     * @param index The index of the layer
     */
    public void toggleMute (final int index)
    {
        final ILayer layer = this.bank.getItem (index);
        if (layer.doesExist ())
            layer.toggleMute ();
    }


    /**
     * Toggles the solo state of a layer.
     *
     * @param index The index of the layer
     */
    public void toggleSolo (final int index)
    {
        final ILayer layer = this.bank.getItem (index);
        if (layer.doesExist ())
            layer.toggleSolo ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        this.surface.sendGlobalValues (this.model);

        final IValueChanger valueChanger = this.model.getValueChanger ();
        final int protocolVersion = this.surface.getProtocolVersion ();
        final ICursorDevice cursorDevice = this.model.getCursorDevice ();

        final int [] vuData = new int [16];
        for (int i = 0; i < 8; i++)
        {
            final ILayer layer = this.bank.getItem (i);

            // Track Available
            this.surface.sendKontrolSysEx (KontrolProtocolControlSurface.SYSEX_TRACK_AVAILABLE, TrackType.toTrackType (layer.getType ()), i);
            this.surface.sendKontrolSysEx (KontrolProtocolControlSurface.SYSEX_TRACK_SELECTED, layer.isSelected () ? 1 : 0, i);
            this.surface.sendKontrolSysEx (KontrolProtocolControlSurface.SYSEX_TRACK_RECARM, 0, i);
            this.surface.sendKontrolSysEx (KontrolProtocolControlSurface.SYSEX_TRACK_VOLUME_TEXT, 0, i, layer.getVolumeStr (8));
            this.surface.sendKontrolSysEx (KontrolProtocolControlSurface.SYSEX_TRACK_PAN_TEXT, 0, i, layer.getPanStr (8));
            this.surface.sendKontrolSysEx (KontrolProtocolControlSurface.SYSEX_TRACK_NAME, 0, i, this.formatLayerName (cursorDevice, layer));

            if (protocolVersion == KontrolProtocol.VERSION_4)
                this.surface.sendKontrolSysEx (KontrolProtocolControlSurface.SYSEX_TRACK_COLOR, 0, i, "#" + StringUtils.formatColor (layer.getColor ()));

            final int j = 2 * i;
            vuData[j] = valueChanger.toMidiValue (layer.getVuLeft ());
            vuData[j + 1] = valueChanger.toMidiValue (layer.getVuRight ());

            this.surface.sendKontrolSysEx (KontrolProtocolControlSurface.SYSEX_TRACK_MUTE, layer.isMute () ? 1 : 0, i);
            this.surface.sendKontrolSysEx (KontrolProtocolControlSurface.SYSEX_TRACK_SOLO, layer.isSolo () ? 1 : 0, i);
            this.surface.sendKontrolSysEx (KontrolProtocolControlSurface.SYSEX_TRACK_MUTED_BY_SOLO, 0, i);

            final Optional<ILayer> selectedLayer = this.bank.getSelectedItem ();
            this.surface.sendCommand (KontrolProtocolControlSurface.CC_SELECTED_TRACK_AVAILABLE, selectedLayer.isPresent () ? TrackType.toTrackType (selectedLayer.get ().getType ()) : 0);
            this.surface.sendCommand (KontrolProtocolControlSurface.CC_SELECTED_TRACK_MUTED_BY_SOLO, 0);
        }
        this.surface.sendKontrolSysEx (KontrolProtocolControlSurface.SYSEX_TRACK_VU, 2, 0, vuData);
    }


    /** {@inheritDoc} */
    @Override
    public void parametersAdjusted ()
    {
        // Only use the layer bank (no drum banks)
        this.switchBanks (this.firstInstrument.getLayerBank ());
        this.bindControls ();
    }


    private String formatLayerName (final ICursorDevice cursorDevice, final ILayer layer)
    {
        if (!cursorDevice.doesExist () || !layer.doesExist ())
            return "";
        final String name = layer.getName ();
        switch (this.surface.getProtocolVersion ())
        {
            case KontrolProtocol.VERSION_1:
                return name;
            case KontrolProtocol.VERSION_2:
                return "Layer " + (layer.getPosition () + 1) + "\n" + name;
            default:
                return layer.getPosition () + 1 + ": " + name;
        }
    }


    private final ISpecificDevice getDevice ()
    {
        return this.model.getSpecificDevice (DeviceID.FIRST_INSTRUMENT);
    }


    /** {@inheritDoc} */
    @Override
    protected void executeMethod (final ILayer layer)
    {
        // Not used
    }
}
