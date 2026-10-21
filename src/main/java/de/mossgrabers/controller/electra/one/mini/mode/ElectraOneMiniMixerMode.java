// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.electra.one.mini.mode;

import java.util.Optional;

import de.mossgrabers.controller.electra.one.mini.controller.ElectraOneMiniControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ISendBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.daw.data.empty.EmptyTrack;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.parameterprovider.IParameterProvider;
import de.mossgrabers.framework.parameterprovider.track.PanParameterProvider;
import de.mossgrabers.framework.parameterprovider.track.SelectedTrackParameterProvider;
import de.mossgrabers.framework.parameterprovider.track.VolumeParameterProvider;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * The mixer mode. The knobs control the volumes and panning of the tracks on the current track
 * page.
 *
 * @author Jürgen Moßgraber
 */
public class ElectraOneMiniMixerMode extends AbstractElectraOneMiniMode
{
    private static final int                     FIRST_TRACK_GROUP = 9;

    private final VolumeParameterProvider        volumeParameterProvider;
    private final PanParameterProvider           panParameterProvider;
    private final SelectedTrackParameterProvider selectedTrackParameterProvider;

    private Modes                                pushMode          = Modes.MUTE;

    private Modes                                turnMode          = Modes.VOLUME;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public ElectraOneMiniMixerMode (final ElectraOneMiniControlSurface surface, final IModel model)
    {
        super (0, Modes.NAME_VOLUME, surface, model);

        this.volumeParameterProvider = new VolumeParameterProvider (model);
        this.panParameterProvider = new PanParameterProvider (model);
        this.selectedTrackParameterProvider = new SelectedTrackParameterProvider (model);

        this.setParameterProvider (this.volumeParameterProvider);
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int column, final ButtonEvent event)
    {
        // The 4 function buttons of the Mini
        if (row == 1)
        {
            this.handleFunctionButtons (column, event);
            return;
        }

        // The push buttons of the knobs

        if (event != ButtonEvent.DOWN)
            return;

        final Optional<ITrack> trackOpt = this.getTrack (column);
        if (trackOpt.isEmpty ())
            return;

        if (this.surface.isShiftPressed ())
        {
            this.setParameterProvider (this.selectedTrackParameterProvider);
            this.bindControls ();
            this.turnMode = Modes.TRACK;
            return;
        }

        final ITrack track = trackOpt.get ();
        switch (this.pushMode)
        {
            // Record Arm
            case REC_ARM:
                track.toggleRecArm ();
                break;
            // Mute
            case MUTE:
                track.toggleMute ();
                break;
            // Solo
            case SOLO:
                track.toggleSolo ();
                break;
            // Not used
            default:
                break;
        }
    }


    private void handleFunctionButtons (final int column, final ButtonEvent event)
    {
        switch (column)
        {
            // Toggle volume/pan
            case 0:
                if (event == ButtonEvent.DOWN)
                {
                    final IParameterProvider activeParameterProvider = this.getParameterProvider ();
                    final boolean isPanMode = activeParameterProvider == this.volumeParameterProvider;
                    this.setParameterProvider (isPanMode ? this.panParameterProvider : this.volumeParameterProvider);
                    this.bindControls ();
                    this.turnMode = isPanMode ? Modes.VOLUME : Modes.PAN;
                    // TODO update button text -> is this row 5 ?
                }
                break;

            // Toggle mute/solo
            case 1:
                if (event == ButtonEvent.DOWN)
                {
                    if (this.surface.isShiftPressed ())
                        this.pushMode = Modes.REC_ARM;
                    else
                        this.pushMode = this.pushMode != Modes.MUTE ? Modes.MUTE : Modes.SOLO;
                    // TODO update button text
                }
                break;

            // Move track page left/right
            case 2:
                if (event == ButtonEvent.DOWN)
                {
                    final ITrackBank currentTrackBank = this.model.getCurrentTrackBank ();
                    if (this.surface.isShiftPressed ())
                        currentTrackBank.selectPreviousPage ();
                    else
                        currentTrackBank.selectNextPage ();
                }
                break;

            // Toggle SHIFT buttons
            case 3:
                if (event != ButtonEvent.LONG)
                    this.surface.updateShift (event == ButtonEvent.DOWN);
                break;

            // There are no more
            default:
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        for (int column = 0; column < 8; column++)
        {
            final Optional<ITrack> trackOpt = this.getTrack (column);
            final ITrack track = trackOpt.isPresent () ? trackOpt.get () : EmptyTrack.getInstance (this.model.getTrackBank ().getItem (0).getSendBank ().getPageSize ());

            final boolean trackExists = track.doesExist ();
            final Boolean exists = Boolean.valueOf (trackExists);

            int value = 0;
            String valueText = " ";
            String label = " ";
            if (trackExists)
            {
                switch (this.turnMode)
                {
                    default:
                    case VOLUME:
                        value = track.getVolume ();
                        valueText = StringUtils.optimizeName (StringUtils.fixASCII (track.getVolumeStr ()), 15);
                        label = "VOLUME";
                        break;
                    case PAN:
                        value = track.getPan ();
                        valueText = StringUtils.optimizeName (StringUtils.fixASCII (track.getPanStr ()), 15);
                        label = "PAN";
                        break;
                    case TRACK:
                        switch (column)
                        {
                            case 0:
                                value = track.getVolume ();
                                valueText = StringUtils.optimizeName (StringUtils.fixASCII (track.getVolumeStr ()), 15);
                                label = "VOLUME";
                                break;
                            case 1:
                                value = track.getPan ();
                                valueText = StringUtils.optimizeName (StringUtils.fixASCII (track.getPanStr ()), 15);
                                label = "PAN";
                                break;
                            default:
                                final ISendBank sendBank = track.getSendBank ();
                                final ISend send = sendBank.getItem (column - 2);
                                if (send.doesExist ())
                                {
                                    value = send.getValue ();
                                    valueText = send.getDisplayedValue (15);
                                    label = "S" + (column + 1) + ": " + send.getName (11);
                                }
                                break;
                        }
                        break;
                }
            }
            final String groupLabel = trackExists ? track.getPosition () + 1 + ": " + track.getName () : "";
            this.pageCache.updateValue (0, column, value, valueText);
            this.pageCache.updateElement (0, column, label, track.getColor (), exists);
            this.pageCache.updateGroupLabel (FIRST_TRACK_GROUP + column, groupLabel);
        }

        this.pageCache.flush ();
    }
}