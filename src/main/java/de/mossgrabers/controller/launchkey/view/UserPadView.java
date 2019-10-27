// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchkey.view;

import de.mossgrabers.controller.launchkey.LaunchkeyMiniMk3Configuration;
import de.mossgrabers.controller.launchkey.controller.LaunchkeyMiniMk3Colors;
import de.mossgrabers.controller.launchkey.controller.LaunchkeyMiniMk3ControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractView;
import de.mossgrabers.framework.view.SceneView;


/**
 * The pad mode user view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class UserPadView extends AbstractView<LaunchkeyMiniMk3ControlSurface, LaunchkeyMiniMk3Configuration> implements SceneView
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public UserPadView (final LaunchkeyMiniMk3ControlSurface surface, final IModel model)
    {
        super ("User Mode", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        // Drawn by the device itself
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        // Handled by the device itself
    }


    /** {@inheritDoc} */
    @Override
    public void onScene (final int sceneIndex, final ButtonEvent event)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void updateSceneButtons ()
    {
        this.surface.updateTrigger (LaunchkeyMiniMk3ControlSurface.LAUNCHKEY_SCENE1, LaunchkeyMiniMk3Colors.LAUNCHKEY_COLOR_BLACK);
        this.surface.updateTrigger (LaunchkeyMiniMk3ControlSurface.LAUNCHKEY_SCENE2, LaunchkeyMiniMk3Colors.LAUNCHKEY_COLOR_BLACK);
    }
}