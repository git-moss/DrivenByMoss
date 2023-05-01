// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchkey.maxi.view;

import de.mossgrabers.controller.novation.launchkey.maxi.LaunchkeyMk3Configuration;
import de.mossgrabers.controller.novation.launchkey.maxi.controller.LaunchkeyMk3ColorManager;
import de.mossgrabers.controller.novation.launchkey.maxi.controller.LaunchkeyMk3ControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.bank.IParameterPageBank;
import de.mossgrabers.framework.featuregroup.AbstractView;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * The Device configuration view.
 *
 * @author Jürgen Moßgraber
 */
public class DeviceConfigView extends AbstractView<LaunchkeyMk3ControlSurface, LaunchkeyMk3Configuration>
{
    /**
     * Constructor.
     *
     * @param surface The controller
     * @param model The model
     */
    public DeviceConfigView (final LaunchkeyMk3ControlSurface surface, final IModel model)
    {
        super ("Device Configuration", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (velocity == 0)
            return;

        final int n = note - 36;
        final int page = n < 8 ? n + 8 : n - 8;

        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        if (cursorDevice.doesExist ())
            cursorDevice.getParameterBank ().getPageBank ().selectPage (page);
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final IPadGrid padGrid = this.surface.getPadGrid ();

        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        if (!cursorDevice.doesExist ())
        {
            for (int i = 0; i < 16; i++)
                padGrid.light (36 + i, LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_BLACK);
        }

        final IParameterPageBank parameterPageBank = cursorDevice.getParameterBank ().getPageBank ();
        final int sel = parameterPageBank.getSelectedItemIndex ();
        final int lastPage = Math.min (16, parameterPageBank.getItemCount ());
        for (int i = 0; i < lastPage; i++)
            padGrid.lightEx (i % 8, i / 8, i == sel ? LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_MAGENTA_HI : LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_MAGENTA_LO);
        for (int i = lastPage; i < 16; i++)
            padGrid.lightEx (i % 8, i / 8, LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_GREY_LO);
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        switch (buttonID)
        {
            case SCENE1:
                return cursorDevice.isEnabled () ? LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_LIME_HI : LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_GREY_LO;

            case SCENE2:
                return LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_BLACK;

            case ARROW_UP:
                return cursorDevice.canSelectPrevious () ? LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_MAGENTA_LO : LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_GREY_LO;

            case ARROW_DOWN:
                return cursorDevice.canSelectNext () ? LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_MAGENTA_LO : LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_GREY_LO;

            default:
                // Not used
                return LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_BLACK;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        switch (buttonID)
        {
            case SCENE1:
                cursorDevice.toggleEnabledState ();
                break;

            case SCENE2:
                // Not used
                break;

            case ARROW_UP:
                cursorDevice.selectPrevious ();
                break;

            case ARROW_DOWN:
                cursorDevice.selectNext ();
                break;

            default:
                // Not used
                break;
        }
    }
}