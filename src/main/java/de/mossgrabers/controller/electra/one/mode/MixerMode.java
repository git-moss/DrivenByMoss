// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.electra.one.mode;

import de.mossgrabers.controller.electra.one.ElectraOneConfiguration;
import de.mossgrabers.controller.electra.one.controller.ElectraOneColorManager;
import de.mossgrabers.controller.electra.one.controller.ElectraOneControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.IProject;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.empty.EmptyTrack;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.mode.track.DefaultTrackMode;
import de.mossgrabers.framework.parameterprovider.special.CombinedParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.EmptyParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.FixedParameterProvider;
import de.mossgrabers.framework.parameterprovider.track.PanParameterProvider;
import de.mossgrabers.framework.parameterprovider.track.VolumeParameterProvider;
import de.mossgrabers.framework.utils.ButtonEvent;

import java.util.Optional;


/**
 * The mixer mode. The knobs control the volumes and panorama of the tracks on the current track
 * page.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MixerMode extends DefaultTrackMode<ElectraOneControlSurface, ElectraOneConfiguration>
{
    private static final int   FIRST_TRACK_GROUP = 501;

    private final PageCache    pageCache;
    private final ITransport   transport;
    private final IMasterTrack masterTrack;
    private final IProject     project;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public MixerMode (final ElectraOneControlSurface surface, final IModel model)
    {
        super (Modes.NAME_VOLUME, surface, model, true, ElectraOneControlSurface.KNOB_IDS);

        this.pageCache = new PageCache (0, surface);

        this.transport = this.model.getTransport ();
        this.masterTrack = this.model.getMasterTrack ();
        this.project = this.model.getProject ();

        this.setParameterProvider (new CombinedParameterProvider (
                // Row 1
                new VolumeParameterProvider (model), new FixedParameterProvider (this.masterTrack.getVolumeParameter ()),
                // Row 2
                new PanParameterProvider (model), new FixedParameterProvider (this.project.getCueVolumeParameter ()),
                // These 4 rows only contain buttons
                new EmptyParameterProvider (4 * 6)));
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int column, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        if (column == 5)
        {
            switch (row)
            {
                // Next track page
                case 2:
                    this.model.getCurrentTrackBank ().selectNextPage ();
                    break;
                // Previous track page
                case 3:
                    this.model.getCurrentTrackBank ().selectPreviousPage ();
                    break;
                // Record
                case 4:
                    this.transport.startRecording ();
                    break;
                // Play
                case 5:
                    this.transport.play ();
                    break;

                default:
                    // Not used
                    break;
            }
            return;
        }

        final Optional<ITrack> trackOpt = this.getTrack (column);
        if (trackOpt.isEmpty ())
            return;

        final ITrack track = trackOpt.get ();
        switch (row)
        {
            // Record Arm
            case 2:
                track.toggleRecArm ();
                break;
            // Mute
            case 3:
                track.toggleMute ();
                break;
            // Solo
            case 4:
                track.toggleSolo ();
                break;
            // Select
            case 5:
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
        return ElectraOneColorManager.COLOR_BUTTON_STATE_OFF;
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        for (int column = 0; column < 5; column++)
        {
            final Optional<ITrack> trackOpt = this.getTrack (column);
            final ITrack track = trackOpt.isPresent () ? trackOpt.get () : EmptyTrack.INSTANCE;
            final Boolean exists = Boolean.valueOf (track.doesExist ());

            this.pageCache.updateGroupLabel (FIRST_TRACK_GROUP + column, track.getPosition () + 1 + ": " + track.getName ());
            this.pageCache.updateValue (0, column, track.getVolume ());
            this.pageCache.updateValue (1, column, track.getPan ());

            final ColorEx color = track.getColor ();
            this.pageCache.updateElement (0, column, null, color, exists);
            this.pageCache.updateElement (1, column, null, color, exists);
            this.pageCache.updateElement (2, column, null, track.isRecArm () ? ElectraOneColorManager.REC_ARM_ON : ElectraOneColorManager.REC_ARM_OFF, exists);
            this.pageCache.updateElement (3, column, null, track.isMute () ? ElectraOneColorManager.MUTE_ON : ElectraOneColorManager.MUTE_OFF, exists);
            this.pageCache.updateElement (4, column, null, track.isSolo () ? ElectraOneColorManager.SOLO_ON : ElectraOneColorManager.SOLO_OFF, exists);
            this.pageCache.updateElement (5, column, null, track.isSelected () ? ElectraOneColorManager.SELECT_ON : ElectraOneColorManager.SELECT_OFF, exists);
        }

        // Master
        this.pageCache.updateColor (0, 5, this.masterTrack.getColor ());
        this.pageCache.updateValue (0, 5, this.masterTrack.getVolume ());
        this.pageCache.updateValue (1, 5, this.project.getCueVolume ());

        // Transport
        this.pageCache.updateColor (4, 5, this.transport.isRecording () ? ElectraOneColorManager.RECORD_ON : ElectraOneColorManager.RECORD_OFF);
        this.pageCache.updateColor (5, 5, this.transport.isPlaying () ? ElectraOneColorManager.PLAY_ON : ElectraOneColorManager.PLAY_OFF);

        this.pageCache.flush ();
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        this.pageCache.reset ();

        super.onActivate ();
    }
}