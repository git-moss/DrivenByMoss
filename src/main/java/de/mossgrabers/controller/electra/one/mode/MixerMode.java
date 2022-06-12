// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.electra.one.mode;

import de.mossgrabers.controller.electra.one.ElectraOneConfiguration;
import de.mossgrabers.controller.electra.one.ElectraOnePlayPositionParameter;
import de.mossgrabers.controller.electra.one.controller.ElectraOneColorManager;
import de.mossgrabers.controller.electra.one.controller.ElectraOneControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.mode.track.DefaultTrackMode;
import de.mossgrabers.framework.parameterprovider.special.CombinedParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.FixedParameterProvider;
import de.mossgrabers.framework.parameterprovider.track.PanParameterProvider;
import de.mossgrabers.framework.parameterprovider.track.VolumeParameterProvider;
import de.mossgrabers.framework.utils.ButtonEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;


/**
 * The mixer mode. The knobs control the volumes and panorama of the tracks on the current track
 * page.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MixerMode extends DefaultTrackMode<ElectraOneControlSurface, ElectraOneConfiguration>
{
    private static final List<ContinuousID> KNOB_IDS = ContinuousID.createSequentialList (ContinuousID.VOLUME_KNOB1, 6);
    static
    {
        KNOB_IDS.addAll (ContinuousID.createSequentialList (ContinuousID.PAN_KNOB1, 6));
    }

    private final int []    valueCache   = new int [128];
    private final String [] elementCache = new String [37];
    private final String [] groupCache   = new String [37];


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public MixerMode (final ElectraOneControlSurface surface, final IModel model)
    {
        super (Modes.NAME_VOLUME, surface, model, true, KNOB_IDS);

        final IMasterTrack masterTrack = this.model.getMasterTrack ();
        this.setParameterProvider (new CombinedParameterProvider (new VolumeParameterProvider (model), new FixedParameterProvider (masterTrack.getVolumeParameter ()), new PanParameterProvider (model), new FixedParameterProvider (new ElectraOnePlayPositionParameter (model.getValueChanger (), model.getTransport (), surface))));
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int index, final ButtonEvent event)
    {
        // These are all toggle buttons sending 127 for on and 0 for off state
        if (event == ButtonEvent.LONG)
            return;

        if (index == 5)
        {
            switch (row)
            {
                // Next track page
                case 0:
                    this.model.getCurrentTrackBank ().selectNextPage ();
                    break;
                // Previous track page
                case 1:
                    this.model.getCurrentTrackBank ().selectPreviousPage ();
                    break;
                // Record
                case 2:
                    this.model.getTransport ().startRecording ();
                    break;
                // Play
                case 3:
                    this.model.getTransport ().play ();
                    break;

                default:
                    // Not used
                    break;
            }
            return;
        }

        final Optional<ITrack> trackOpt = this.getTrack (index);
        if (trackOpt.isEmpty ())
            return;

        final ITrack track = trackOpt.get ();
        switch (row)
        {
            // Record Arm
            case 0:
                track.toggleRecArm ();
                break;
            // Mute
            case 1:
                track.toggleMute ();
                break;
            // Solo
            case 2:
                track.toggleSolo ();
                break;
            // Select
            case 3:
                track.select ();
                break;

            default:
                // Not used
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        final int row = this.getButtonRow (buttonID);
        if (row == -1)
            return ElectraOneColorManager.COLOR_BUTTON_STATE_OFF;

        final int column = this.isButtonRow (row, buttonID);
        if (column == 5)
        {
            switch (row)
            {
                // Next track page
                case 0:
                    return this.model.getCurrentTrackBank ().canScrollPageForwards () ? ElectraOneColorManager.COLOR_BUTTON_STATE_ON : ElectraOneColorManager.COLOR_BUTTON_STATE_OFF;
                // Previous track page
                case 1:
                    return this.model.getCurrentTrackBank ().canScrollPageBackwards () ? ElectraOneColorManager.COLOR_BUTTON_STATE_ON : ElectraOneColorManager.COLOR_BUTTON_STATE_OFF;
                // Record
                case 2:
                    return this.model.getTransport ().isRecording () ? ElectraOneColorManager.COLOR_BUTTON_STATE_ON : ElectraOneColorManager.COLOR_BUTTON_STATE_OFF;
                // Play
                case 3:
                    return this.model.getTransport ().isPlaying () ? ElectraOneColorManager.COLOR_BUTTON_STATE_ON : ElectraOneColorManager.COLOR_BUTTON_STATE_OFF;

                default:
                    // Not used
                    return ElectraOneColorManager.COLOR_BUTTON_STATE_OFF;
            }
        }

        final Optional<ITrack> trackOpt = this.getTrack (column);
        if (trackOpt.isEmpty ())
            return ElectraOneColorManager.COLOR_BUTTON_STATE_OFF;

        final ITrack track = trackOpt.get ();
        switch (row)
        {
            // Record Arm
            case 0:
                return track.isRecArm () ? ElectraOneColorManager.COLOR_BUTTON_STATE_ON : ElectraOneColorManager.COLOR_BUTTON_STATE_OFF;
            // Mute
            case 1:
                return track.isMute () ? ElectraOneColorManager.COLOR_BUTTON_STATE_ON : ElectraOneColorManager.COLOR_BUTTON_STATE_OFF;
            // Solo
            case 2:
                return track.isSolo () ? ElectraOneColorManager.COLOR_BUTTON_STATE_ON : ElectraOneColorManager.COLOR_BUTTON_STATE_OFF;
            // Select
            case 3:
                return track.isSelected () ? ElectraOneColorManager.COLOR_BUTTON_STATE_ON : ElectraOneColorManager.COLOR_BUTTON_STATE_OFF;

            default:
                // Not used
                return ElectraOneColorManager.COLOR_BUTTON_STATE_OFF;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final IMidiOutput output = this.surface.getMidiOutput ();

        for (int i = 0; i < 5; i++)
        {
            boolean exists = false;
            int volume = 0;
            int pan = 0;
            String title = "";
            ColorEx color = ColorEx.BLACK;

            final Optional<ITrack> trackOpt = this.getTrack (i);
            if (trackOpt.isPresent ())
            {
                final ITrack track = trackOpt.get ();
                if (track.doesExist ())
                {
                    exists = true;
                    volume = track.getVolume ();
                    pan = track.getPan ();
                    title = track.getPosition () + 1 + ": " + track.getName ();
                    color = ElectraOneColorManager.getClosestPaletteColor (track.getColor ());
                }
            }

            if (this.valueCache[ElectraOneControlSurface.ELECTRA_ONE_VOLUME1 + i] != volume)
            {
                output.sendCCEx (0, ElectraOneControlSurface.ELECTRA_ONE_VOLUME1 + i, volume);
                this.valueCache[ElectraOneControlSurface.ELECTRA_ONE_VOLUME1 + i] = volume;
            }

            if (this.valueCache[ElectraOneControlSurface.ELECTRA_ONE_PAN1 + i] != pan)
            {
                output.sendCCEx (0, ElectraOneControlSurface.ELECTRA_ONE_PAN1 + i, pan);
                this.valueCache[ElectraOneControlSurface.ELECTRA_ONE_PAN1 + i] = pan;
            }

            final int controlID = 1 + i;
            this.surface.updateElement (controlID, this.elementCache, null, color, Boolean.valueOf (exists));
            this.surface.updateElement (7 + i, this.elementCache, null, color, Boolean.valueOf (exists));
            this.surface.updateElement (13 + i, this.elementCache, null, null, Boolean.valueOf (exists));
            this.surface.updateElement (19 + i, this.elementCache, null, null, Boolean.valueOf (exists));
            this.surface.updateElement (25 + i, this.elementCache, null, null, Boolean.valueOf (exists));
            this.surface.updateElement (31 + i, this.elementCache, null, null, Boolean.valueOf (exists));
            this.surface.updateGroupTitle (controlID, this.groupCache, title);
        }

        final IMasterTrack masterTrack = this.model.getMasterTrack ();
        final int masterVolume = masterTrack.getVolume ();
        if (this.valueCache[ElectraOneControlSurface.ELECTRA_ONE_MASTER_VOLUME] != masterVolume)
        {
            output.sendCCEx (0, ElectraOneControlSurface.ELECTRA_ONE_MASTER_VOLUME, masterVolume);
            this.valueCache[ElectraOneControlSurface.ELECTRA_ONE_MASTER_VOLUME] = masterVolume;
        }

        final ColorEx masterColor = ElectraOneColorManager.getClosestPaletteColor (masterTrack.getColor ());
        this.surface.updateElement (6, this.elementCache, null, masterColor, null);
        this.surface.updateElement (12, this.elementCache, this.model.getTransport ().getBeatText (), null, null);
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        Arrays.fill (this.valueCache, -1);
        Arrays.fill (this.elementCache, null);
        Arrays.fill (this.groupCache, null);

        super.onActivate ();
    }
}