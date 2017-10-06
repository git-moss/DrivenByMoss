// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.apcmini.view;

import de.mossgrabers.apcmini.controller.APCminiControlSurface;
import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.daw.AbstractTrackBankProxy;


/**
 * The Drum view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TrackButtons
{
    private APCminiControlSurface surface;
    private Model                 model;


    /**
     * Constructor.
     *
     * @param surface The controller
     * @param model The model
     */
    public TrackButtons (final APCminiControlSurface surface, final Model model)
    {
        this.surface = surface;
        this.model = model;
    }


    /**
     * Update track button LEDs.
     */
    public void updateTrackButtons ()
    {
        final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();
        final int trackState = this.surface.getTrackState ();
        for (int i = 0; i < 8; i++)
        {
            switch (trackState)
            {
                case APCminiControlSurface.TRACK_STATE_CLIP_STOP:
                    this.surface.updateButton (APCminiControlSurface.APC_BUTTON_TRACK_BUTTON1 + i, this.surface.isPressed (APCminiControlSurface.APC_BUTTON_TRACK_BUTTON1 + i) ? APCminiControlSurface.APC_BUTTON_STATE_ON : APCminiControlSurface.APC_BUTTON_STATE_OFF);
                    break;
                case APCminiControlSurface.TRACK_STATE_SOLO:
                    this.surface.updateButton (APCminiControlSurface.APC_BUTTON_TRACK_BUTTON1 + i, tb.getTrack (i).isSolo () ? APCminiControlSurface.APC_BUTTON_STATE_ON : APCminiControlSurface.APC_BUTTON_STATE_OFF);
                    break;
                case APCminiControlSurface.TRACK_STATE_REC_ARM:
                    this.surface.updateButton (APCminiControlSurface.APC_BUTTON_TRACK_BUTTON1 + i, tb.getTrack (i).isRecArm() ? APCminiControlSurface.APC_BUTTON_STATE_ON : APCminiControlSurface.APC_BUTTON_STATE_OFF);
                    break;
                case APCminiControlSurface.TRACK_STATE_MUTE:
                    this.surface.updateButton (APCminiControlSurface.APC_BUTTON_TRACK_BUTTON1 + i, !tb.getTrack (i).isMute () ? APCminiControlSurface.APC_BUTTON_STATE_ON : APCminiControlSurface.APC_BUTTON_STATE_OFF);
                    break;
                case APCminiControlSurface.TRACK_STATE_SELECT:
                    this.surface.updateButton (APCminiControlSurface.APC_BUTTON_TRACK_BUTTON1 + i, tb.getTrack (i).isSelected () ? APCminiControlSurface.APC_BUTTON_STATE_ON : APCminiControlSurface.APC_BUTTON_STATE_OFF);
                    break;
            }
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
        switch (trackState)
        {
            case APCminiControlSurface.TRACK_STATE_CLIP_STOP:
                this.model.getCurrentTrackBank ().stop (index);
                break;
            case APCminiControlSurface.TRACK_STATE_SOLO:
                this.model.getCurrentTrackBank ().toggleSolo (index);
                break;
            case APCminiControlSurface.TRACK_STATE_REC_ARM:
                this.model.getCurrentTrackBank ().toggleArm (index);
                break;
            case APCminiControlSurface.TRACK_STATE_MUTE:
                this.model.getCurrentTrackBank ().toggleMute (index);
                break;
            case APCminiControlSurface.TRACK_STATE_SELECT:
                this.surface.getViewManager ().getActiveView ().selectTrack (index);
                break;
        }
    }
}