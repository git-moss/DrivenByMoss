// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchkey.maxi.view;

import de.mossgrabers.controller.novation.launchkey.maxi.LaunchkeyMk3Configuration;
import de.mossgrabers.controller.novation.launchkey.maxi.controller.LaunchkeyMk3ColorManager;
import de.mossgrabers.controller.novation.launchkey.maxi.controller.LaunchkeyMk3ControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.AbstractView;


/**
 * The pad mode user view.
 *
 * @author Jürgen Moßgraber
 */
public class UserPadView extends AbstractView<LaunchkeyMk3ControlSurface, LaunchkeyMk3Configuration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public UserPadView (final LaunchkeyMk3ControlSurface surface, final IModel model)
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
        return LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_BLACK;
    }
}