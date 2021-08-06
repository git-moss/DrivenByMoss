// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchkey.mini.view;

import de.mossgrabers.controller.novation.launchkey.mini.LaunchkeyMiniMk3Configuration;
import de.mossgrabers.controller.novation.launchkey.mini.controller.LaunchkeyMiniMk3ColorManager;
import de.mossgrabers.controller.novation.launchkey.mini.controller.LaunchkeyMiniMk3ControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.bank.IDeviceBank;
import de.mossgrabers.framework.featuregroup.AbstractView;


/**
 * The pad mode user view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DeviceView extends AbstractView<LaunchkeyMiniMk3ControlSurface, LaunchkeyMiniMk3Configuration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public DeviceView(final LaunchkeyMiniMk3ControlSurface surface, final IModel model)
    {
        super ("Device View", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final IPadGrid padGrid = this.surface.getPadGrid ();
        padGrid.lightEx(0, 0, LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_RED_HI);
        padGrid.lightEx(1, 0, LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_ORANGE);
        padGrid.lightEx(2, 0, LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_YELLOW_HI);
        padGrid.lightEx(3, 0, LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_GREEN_HI);
        padGrid.lightEx(4, 0, LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_LIME_HI);
        padGrid.lightEx(5, 0, LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_BLUE_HI);
        padGrid.lightEx(6, 0, LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_MAGENTA_HI);
        padGrid.lightEx(7, 0, LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_PINK_HI);


        IDeviceBank mDeviceBank = this.model.getCursorDevice().getDeviceBank();
        final int deviceCount = mDeviceBank.getItemCount();

        ICursorDevice cd = this.model.getCursorDevice();
        final int deviceId = cd.getIndex();

        for(int x = 0; x < deviceCount; x++)
        {
            if (deviceId == x)
            {
                padGrid.lightEx(x, 1, LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_WHITE);
            }
            else
            {
                padGrid.lightEx(x, 1, LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_GREY_LO);
            }
        }

        for(int x = deviceCount; x < 8; x++)
        {
            padGrid.lightEx(x, 1, LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_BLACK);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        return LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_BLACK;
    }
}