// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchkey.mini.view;

import de.mossgrabers.controller.novation.launchkey.mini.LaunchkeyMiniMk3Configuration;
import de.mossgrabers.controller.novation.launchkey.mini.controller.LaunchkeyMiniMk3ColorManager;
import de.mossgrabers.controller.novation.launchkey.mini.controller.LaunchkeyMiniMk3ControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.IBrowser;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.bank.IDeviceBank;
import de.mossgrabers.framework.daw.data.bank.IParameterPageBank;
import de.mossgrabers.framework.featuregroup.AbstractView;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.Views;


/**
 * The pad mode user view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class BrowserView extends AbstractView<LaunchkeyMiniMk3ControlSurface, LaunchkeyMiniMk3Configuration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public BrowserView(final LaunchkeyMiniMk3ControlSurface surface, final IModel model)
    {
        super ("Browser View", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final IPadGrid padGrid = this.surface.getPadGrid ();
        for (int x = 0; x < 8; x++) {
            padGrid.lightEx(x, 0, LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_BLACK);
            padGrid.lightEx(x, 1, LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_BLACK);
        }

        padGrid.lightEx(0, 0, LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_RED_HI);
        padGrid.lightEx(1, 0, LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_AMBER_HI);
        padGrid.lightEx(2, 0, LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_YELLOW_HI);
        padGrid.lightEx(3, 0, LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_GREEN_HI);
        padGrid.lightEx(4, 0, LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_LIME_HI);
        padGrid.lightEx(5, 0, LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_BLUE_HI);
        padGrid.lightEx(6, 0, LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_MAGENTA_HI);
        padGrid.lightEx(7, 0, LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_PINK_HI);

        padGrid.lightEx(0, 1, LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_BLUE_HI);
        padGrid.lightEx(1, 1, LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_WHITE);
        padGrid.lightEx(2, 1, LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_BLUE_HI);
        padGrid.lightEx(3, 1, LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_WHITE);
        padGrid.lightEx(4, 1, LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_BLUE_HI);
        padGrid.lightEx(5, 1, LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_WHITE);
        padGrid.lightEx(6, 1, LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_BLUE_HI);
        padGrid.lightEx(7, 1, LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_WHITE);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (velocity != 0)
            return;

        final IBrowser browser = this.model.getBrowser ();
        if (!browser.isActive ())
            return;

        switch (note) {
            case 36 -> browser.previousContentType();
            case 37 -> browser.nextContentType();
            case 38 -> {
                browser.selectPreviousFilterColumn();
                browser.selectPreviousFilterColumn();
            }
            case 39 -> browser.selectNextFilterColumn();
            case 40 -> browser.selectPreviousFilterItem(browser.getSelectedFilterColumn().getIndex());
            case 41 -> browser.selectNextFilterItem(browser.getSelectedFilterColumn().getIndex());
            case 42 -> browser.selectPreviousResult();
            case 43 -> browser.selectNextResult();
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
            return LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_RED_HI;
        }

        return LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_BLACK;
    }

    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        if (!ButtonID.isSceneButton (buttonID) || event != ButtonEvent.DOWN)
            return;

        final IBrowser browser = this.model.getBrowser ();

        if (buttonID == ButtonID.SCENE1) {
            this.surface.getViewManager ().setActive (Views.DRUM);
            browser.stopBrowsing(true);
        } else if (buttonID == ButtonID.SCENE2) {
            this.surface.getViewManager ().setActive (Views.DRUM);
            browser.stopBrowsing(false);
        }
    }
}