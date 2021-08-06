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
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.daw.data.bank.IDeviceBank;
import de.mossgrabers.framework.daw.data.bank.IParameterPageBank;
import de.mossgrabers.framework.daw.data.bank.ISceneBank;
import de.mossgrabers.framework.featuregroup.AbstractView;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.Views;


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
        final int[] brightColors = {
            LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_RED_HI,
            LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_AMBER_HI,
            LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_YELLOW_HI,
            LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_GREEN_HI,
            LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_LIME_HI,
            LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_BLUE_HI,
            LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_MAGENTA_HI,
            LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_PINK_HI
        };

        final int[] dimColors = {
            LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_RED_LO,
            LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_AMBER_LO,
            LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_YELLOW_LO,
            LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_GREEN_LO,
            LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_LIME_LO,
            LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_BLUE_LO,
            LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_MAGENTA_LO,
            LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_PINK_LO
        };

        final IPadGrid padGrid = this.surface.getPadGrid ();

        IDeviceBank mDeviceBank = this.model.getCursorDevice().getDeviceBank();
        final int deviceCount = mDeviceBank.getItemCount();

        ICursorDevice cd = this.model.getCursorDevice();
        final int deviceId = cd.getIndex();

        IParameterPageBank parameterPageBank = cd.getParameterPageBank();
        final int parameterPageId = parameterPageBank.getSelectedItemIndex();

        // Draw Rainbow pads on the top row for selecting the parameter page
        for (int x = 0; x < 8; x++)
        {
            if (parameterPageId == x)
            {
                padGrid.lightEx(x, 0, brightColors[x]);
            }
            else
            {
                padGrid.lightEx(x, 0, dimColors[x]);
            }
        }

        // Draw White / Dim white pads for the Primary Device / Other Devices on the bottom row
        for (int x = 0; x < deviceCount; x++)
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

        // For the rest of the bottom row pads, turn them off
        for (int x = deviceCount; x < 8; x++)
        {
            padGrid.lightEx(x, 1, LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_BLACK);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (velocity != 0)
            return;

        if (note >= 44 && note <= 51)
        {
            // Top Row (Param Page): note is 36 - 43
            ICursorDevice cd = this.model.getCursorDevice();
            IParameterPageBank parameterPageBank = cd.getParameterPageBank();
            final int pageBankId = note - 44;
            parameterPageBank.scrollTo(pageBankId);
            this.mvHelper.notifySelectedParameterPage ();
        }
        else if (note >= 36 && note <= 43)
        {
            // Bottom Row (Device): note is 36 - 43
            IDeviceBank mDeviceBank = this.model.getCursorDevice().getDeviceBank();
            int deviceId = note - 36;
            mDeviceBank.scrollTo(deviceId);
            mDeviceBank.selectItemAtPosition(deviceId);
        }
    }

    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        if (buttonID == ButtonID.SCENE1) {
            return LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_GREEN_HI;
        }

        if (buttonID == ButtonID.SCENE2) {
            return LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_BLUE_HI;
        }

        return LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_BLACK;
    }

    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        if (!ButtonID.isSceneButton (buttonID) || event != ButtonEvent.DOWN)
            return;

        if (buttonID == ButtonID.SCENE1) {
            ICursorDevice cd = this.model.getCursorDevice();
            if (cd.doesExist()) {
                this.surface.getViewManager ().setActive (Views.BROWSER);
                this.model.getBrowser().replace(cd);
            } else {
                this.surface.getViewManager ().setActive (Views.BROWSER);
                this.model.getBrowser ().insertAfterCursorDevice ();
            }
        } else if (buttonID == ButtonID.SCENE2) {
            ICursorDevice cd = this.model.getCursorDevice();
            cd.toggleParameterPageSectionVisible();
        }
    }
}