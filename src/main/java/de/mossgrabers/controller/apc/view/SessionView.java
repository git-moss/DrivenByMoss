// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.apc.view;

import de.mossgrabers.controller.apc.APCConfiguration;
import de.mossgrabers.controller.apc.controller.APCColors;
import de.mossgrabers.controller.apc.controller.APCControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractSessionView;
import de.mossgrabers.framework.view.SessionColor;


/**
 * Session view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SessionView extends AbstractSessionView<APCControlSurface, APCConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public SessionView (final APCControlSurface surface, final IModel model)
    {
        super ("Session", surface, model, 5, 8, surface.isMkII ());

        if (surface.isMkII ())
        {
            final SessionColor isRecording = new SessionColor (APCColors.APC_MKII_COLOR_RED_HI, APCColors.APC_MKII_COLOR_RED_HI, false);
            final SessionColor isRecordingQueued = new SessionColor (APCColors.APC_MKII_COLOR_RED_HI, APCColors.APC_MKII_COLOR_RED_HI, true);
            final SessionColor isPlaying = new SessionColor (APCColors.APC_MKII_COLOR_GREEN_HI, APCColors.APC_MKII_COLOR_GREEN_HI, false);
            final SessionColor isPlayingQueued = new SessionColor (APCColors.APC_MKII_COLOR_GREEN_HI, APCColors.APC_MKII_COLOR_GREEN_HI, true);
            final SessionColor hasContent = new SessionColor (APCColors.APC_MKII_COLOR_AMBER, -1, false);
            final SessionColor noContent = new SessionColor (APCColors.APC_MKII_COLOR_BLACK, -1, false);
            final SessionColor recArmed = new SessionColor (APCColors.APC_MKII_COLOR_RED_LO, -1, false);
            this.setColors (isRecording, isRecordingQueued, isPlaying, isPlayingQueued, hasContent, noContent, recArmed);
        }
        else
        {
            final SessionColor isRecording = new SessionColor (APCColors.APC_COLOR_RED, -1, false);
            final SessionColor isRecordingQueued = new SessionColor (APCColors.APC_COLOR_RED, APCColors.APC_COLOR_RED_BLINK, false);
            final SessionColor isPlaying = new SessionColor (APCColors.APC_COLOR_GREEN, -1, false);
            final SessionColor isPlayingQueued = new SessionColor (APCColors.APC_COLOR_GREEN, APCColors.APC_COLOR_GREEN_BLINK, false);
            final SessionColor hasContent = new SessionColor (APCColors.APC_COLOR_YELLOW, -1, false);
            final SessionColor noContent = new SessionColor (APCColors.APC_COLOR_BLACK, -1, false);
            final SessionColor recArmed = new SessionColor (APCColors.APC_COLOR_BLACK, -1, false);
            this.setColors (isRecording, isRecordingQueued, isPlaying, isPlayingQueued, hasContent, noContent, recArmed);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onScene (final int scene, final ButtonEvent event)
    {
        if (event == ButtonEvent.DOWN)
        {
            this.model.getCurrentTrackBank ().getSceneBank ().getItem (scene).launch ();
            this.surface.updateTrigger (APCControlSurface.APC_BUTTON_SCENE_LAUNCH_1 + scene, ColorManager.BUTTON_STATE_ON);
        }
        else if (event == ButtonEvent.UP)
            this.surface.updateTrigger (APCControlSurface.APC_BUTTON_SCENE_LAUNCH_1 + scene, ColorManager.BUTTON_STATE_OFF);
    }


    /** {@inheritDoc} */
    @Override
    public void updateSceneButtons ()
    {
        final int green = this.surface.isMkII () ? APCColors.APC_MKII_COLOR_GREEN : APCColors.APC_COLOR_GREEN;
        this.surface.updateTrigger (APCControlSurface.APC_BUTTON_SCENE_LAUNCH_1, green);
        this.surface.updateTrigger (APCControlSurface.APC_BUTTON_SCENE_LAUNCH_2, green);
        this.surface.updateTrigger (APCControlSurface.APC_BUTTON_SCENE_LAUNCH_3, green);
        this.surface.updateTrigger (APCControlSurface.APC_BUTTON_SCENE_LAUNCH_4, green);
        this.surface.updateTrigger (APCControlSurface.APC_BUTTON_SCENE_LAUNCH_5, green);
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        // No birds eye view, since Shift is used for view changes...
        this.drawSessionGrid ();
    }
}