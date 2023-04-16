// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.apcmini.view;

import de.mossgrabers.controller.akai.apcmini.controller.APCminiControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * The Drum view.
 *
 * @author Jürgen Moßgraber
 */
public class TrackButtons
{
    private final APCminiControlSurface surface;
    private final IModel                model;


    /**
     * Constructor.
     *
     * @param surface The controller
     * @param model The model
     */
    public TrackButtons (final APCminiControlSurface surface, final IModel model)
    {
        this.surface = surface;
        this.model = model;
    }


    /**
     * Update track button LEDs.
     *
     * @param index The index of the button
     * @return The color
     */
    public int getTrackButtonColor (final int index)
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final int trackState = this.surface.getTrackState ();
        switch (trackState)
        {
            case APCminiControlSurface.TRACK_STATE_CLIP_STOP:
                return this.surface.isPressed (ButtonID.get (ButtonID.ROW_SELECT_1, index)) ? APCminiControlSurface.APC_BUTTON_STATE_ON : APCminiControlSurface.APC_BUTTON_STATE_OFF;

            case APCminiControlSurface.TRACK_STATE_SOLO:
                return tb.getItem (index).isSolo () ? APCminiControlSurface.APC_BUTTON_STATE_ON : APCminiControlSurface.APC_BUTTON_STATE_OFF;

            case APCminiControlSurface.TRACK_STATE_REC_ARM:
                return tb.getItem (index).isRecArm () ? APCminiControlSurface.APC_BUTTON_STATE_ON : APCminiControlSurface.APC_BUTTON_STATE_OFF;

            case APCminiControlSurface.TRACK_STATE_MUTE:
                return !tb.getItem (index).isMute () ? APCminiControlSurface.APC_BUTTON_STATE_ON : APCminiControlSurface.APC_BUTTON_STATE_OFF;

            case APCminiControlSurface.TRACK_STATE_SELECT:
                return tb.getItem (index).isSelected () ? APCminiControlSurface.APC_BUTTON_STATE_ON : APCminiControlSurface.APC_BUTTON_STATE_OFF;

            default:
                // Not used
                return APCminiControlSurface.APC_BUTTON_STATE_OFF;
        }
    }


    /**
     * Execute a function of the track button.
     *
     * @param index The index
     * @param event The event
     */
    public void onSelectTrack (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final int trackState = this.surface.getTrackState ();
        final ITrack track = this.model.getCurrentTrackBank ().getItem (index);
        switch (trackState)
        {
            case APCminiControlSurface.TRACK_STATE_CLIP_STOP:
                track.stop (false);
                break;
            case APCminiControlSurface.TRACK_STATE_SOLO:
                track.toggleSolo ();
                break;
            case APCminiControlSurface.TRACK_STATE_REC_ARM:
                track.toggleRecArm ();
                break;
            case APCminiControlSurface.TRACK_STATE_MUTE:
                track.toggleMute ();
                break;
            case APCminiControlSurface.TRACK_STATE_SELECT:
                this.surface.getViewManager ().getActive ().selectTrack (index);
                break;
            default:
                // Not used
                break;
        }
    }
}