// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchkey.mini.view;

import de.mossgrabers.controller.novation.launchkey.mini.LaunchkeyMiniMk3Configuration;
import de.mossgrabers.controller.novation.launchkey.mini.controller.LaunchkeyMiniMk3ColorManager;
import de.mossgrabers.controller.novation.launchkey.mini.controller.LaunchkeyMiniMk3ControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.AbstractView;


/**
 * The pad mode user view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class UserPadView extends AbstractView<LaunchkeyMiniMk3ControlSurface, LaunchkeyMiniMk3Configuration>
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
    public int getButtonColor (final ButtonID buttonID)
    {
        return LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_BLACK;
    }
}