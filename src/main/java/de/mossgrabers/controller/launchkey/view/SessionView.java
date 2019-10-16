// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchkey.view;

import de.mossgrabers.controller.launchkey.LaunchkeyMiniMk3Configuration;
import de.mossgrabers.controller.launchkey.controller.LaunchkeyMiniMk3Colors;
import de.mossgrabers.controller.launchkey.controller.LaunchkeyMiniMk3ControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ISceneBank;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.view.AbstractSessionView;
import de.mossgrabers.framework.view.SessionColor;


/**
 * The Session view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SessionView extends AbstractSessionView<LaunchkeyMiniMk3ControlSurface, LaunchkeyMiniMk3Configuration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public SessionView (final LaunchkeyMiniMk3ControlSurface surface, final IModel model)
    {
        super ("Session", surface, model, 2, 8, true);

        final SessionColor isRecording = new SessionColor (LaunchkeyMiniMk3Colors.LAUNCHKEY_COLOR_RED, LaunchkeyMiniMk3Colors.LAUNCHKEY_COLOR_RED, false);
        final SessionColor isRecordingQueued = new SessionColor (LaunchkeyMiniMk3Colors.LAUNCHKEY_COLOR_RED_LO, LaunchkeyMiniMk3Colors.LAUNCHKEY_COLOR_RED_LO, true);
        final SessionColor isPlaying = new SessionColor (LaunchkeyMiniMk3Colors.LAUNCHKEY_COLOR_GREEN_SPRING, LaunchkeyMiniMk3Colors.LAUNCHKEY_COLOR_GREEN, false);
        final SessionColor isPlayingQueued = new SessionColor (LaunchkeyMiniMk3Colors.LAUNCHKEY_COLOR_GREEN_SPRING, LaunchkeyMiniMk3Colors.LAUNCHKEY_COLOR_GREEN, true);
        final SessionColor hasContent = new SessionColor (LaunchkeyMiniMk3Colors.LAUNCHKEY_COLOR_AMBER, -1, false);
        final SessionColor noContent = new SessionColor (LaunchkeyMiniMk3Colors.LAUNCHKEY_COLOR_BLACK, -1, false);
        final SessionColor recArmed = new SessionColor (LaunchkeyMiniMk3Colors.LAUNCHKEY_COLOR_RED_LO, -1, false);
        this.setColors (isRecording, isRecordingQueued, isPlaying, isPlayingQueued, hasContent, noContent, recArmed);
    }


    /** {@inheritDoc} */
    @Override
    public void updateSceneButtons ()
    {
        final ColorManager colorManager = this.model.getColorManager ();
        final int colorScene = colorManager.getColor (AbstractSessionView.COLOR_SCENE);
        final int colorSceneSelected = colorManager.getColor (AbstractSessionView.COLOR_SELECTED_SCENE);
        final int colorSceneOff = colorManager.getColor (AbstractSessionView.COLOR_SCENE_OFF);

        final ISceneBank sceneBank = this.model.getSceneBank ();
        for (int i = 0; i < sceneBank.getPageSize (); i++)
        {
            final IScene scene = sceneBank.getItem (i);
            final int color = scene.doesExist () ? scene.isSelected () ? colorSceneSelected : colorScene : colorSceneOff;
            this.surface.updateTrigger (LaunchkeyMiniMk3ControlSurface.LAUNCHKEY_SCENE1 + i, color);
        }
    }
}