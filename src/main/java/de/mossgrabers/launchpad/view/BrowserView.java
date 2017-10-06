// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.launchpad.view;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.controller.grid.PadGrid;
import de.mossgrabers.framework.daw.BrowserProxy;
import de.mossgrabers.framework.view.AbstractView;
import de.mossgrabers.framework.view.SceneView;
import de.mossgrabers.framework.view.ViewManager;
import de.mossgrabers.launchpad.LaunchpadConfiguration;
import de.mossgrabers.launchpad.controller.LaunchpadColors;
import de.mossgrabers.launchpad.controller.LaunchpadControlSurface;


/**
 * Navigate the browser.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class BrowserView extends AbstractView<LaunchpadControlSurface, LaunchpadConfiguration> implements SceneView
{
    private static final int [] COLUMN_ORDER  =
    {
        0,
        1,
        2,
        3,
        4,
        5
    };

    private static final int [] COLUMN_COLORS =
    {
        LaunchpadColors.LAUNCHPAD_COLOR_WHITE,
        LaunchpadColors.LAUNCHPAD_COLOR_GREY_MD,
        LaunchpadColors.LAUNCHPAD_COLOR_GREY_LO,
        LaunchpadColors.LAUNCHPAD_COLOR_ROSE,
        LaunchpadColors.LAUNCHPAD_COLOR_SPRING,
        LaunchpadColors.LAUNCHPAD_COLOR_OCEAN,
        LaunchpadColors.LAUNCHPAD_COLOR_BLACK,
        LaunchpadColors.LAUNCHPAD_COLOR_YELLOW
    };


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public BrowserView (final LaunchpadControlSurface surface, final Model model)
    {
        super ("Browser", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        this.surface.setLaunchpadToPrgMode ();

        super.onActivate ();

        this.surface.scheduleTask (this::delayedUpdateArrowButtons, 150);
    }


    private void delayedUpdateArrowButtons ()
    {
        this.surface.setButton (this.surface.getSessionButton (), LaunchpadColors.LAUNCHPAD_COLOR_GREY_LO);
        this.surface.setButton (this.surface.getNoteButton (), LaunchpadColors.LAUNCHPAD_COLOR_GREY_LO);
        this.surface.setButton (this.surface.getDeviceButton (), LaunchpadColors.LAUNCHPAD_COLOR_TURQUOISE);
        this.surface.setButton (this.surface.getUserButton (), LaunchpadColors.LAUNCHPAD_COLOR_GREY_LO);
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final PadGrid padGrid = this.surface.getPadGrid ();
        padGrid.light (36, LaunchpadColors.LAUNCHPAD_COLOR_RED);
        padGrid.light (37, LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
        for (int i = 38; i < 42; i++)
            padGrid.light (i, LaunchpadColors.LAUNCHPAD_COLOR_ORCHID_LO);
        padGrid.light (42, LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
        padGrid.light (43, LaunchpadColors.LAUNCHPAD_COLOR_GREEN_HI);
        for (int i = 44; i < 52; i++)
            padGrid.light (i, LaunchpadColors.LAUNCHPAD_COLOR_BLACK);

        for (int i = 52; i < 60; i++)
            padGrid.light (i, COLUMN_COLORS[i - 52]);
        for (int i = 60; i < 68; i++)
            padGrid.light (i, COLUMN_COLORS[i - 60]);
        for (int i = 68; i < 76; i++)
            padGrid.light (i, COLUMN_COLORS[i - 68]);
        for (int i = 76; i < 84; i++)
            padGrid.light (i, COLUMN_COLORS[i - 76]);

        for (int i = 84; i < 100; i++)
            padGrid.light (i, LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        final BrowserProxy browser = this.model.getBrowser ();
        if (!browser.isActive ())
        {
            final ViewManager viewManager = this.surface.getViewManager ();
            if (viewManager.isActiveView (Views.VIEW_BROWSER))
                viewManager.restoreView ();
            return;
        }

        switch (note)
        {
            // Cancel
            case 36:
                if (velocity == 0)
                    return;
                this.model.getBrowser ().stopBrowsing (false);
                this.surface.getViewManager ().restoreView ();
                break;

            // OK
            case 43:
                if (velocity == 0)
                    return;
                this.model.getBrowser ().stopBrowsing (true);
                this.surface.getViewManager ().restoreView ();
                break;

            case 38:
                this.surface.sendMidiEvent (0x90, 48, velocity);
                break;
            case 39:
                this.surface.sendMidiEvent (0x90, 60, velocity);
                break;
            case 40:
                this.surface.sendMidiEvent (0x90, 72, velocity);
                break;
            case 41:
                this.surface.sendMidiEvent (0x90, 84, velocity);
                break;
        }

        if (velocity == 0)
            return;

        if (note >= 52 && note < 84)
        {
            final int n = note - 52;
            final int row = n / 8;
            final int col = n % 8;

            switch (col)
            {
                case 6:
                    return;

                case 7:
                    switch (row) {
                        case 0:
                            browser.selectNextResult();
                            break;
                        case 1:
                            for (int i = 0; i < 8; i++)
                                browser.selectNextResult();
                            break;
                        case 2:
                            for (int i = 0; i < 8; i++)
                                browser.selectPreviousResult();
                            break;
                        case 3:
                            browser.selectPreviousResult();
                            break;
                    }
                    break;

                default:
                    switch (row) {
                        case 0:
                            browser.selectNextFilterItem(BrowserView.COLUMN_ORDER[col]);
                            break;
                        case 1:
                            for (int i = 0; i < 8; i++)
                                browser.selectNextFilterItem(BrowserView.COLUMN_ORDER[col]);
                            break;
                        case 2:
                            for (int i = 0; i < 8; i++)
                                browser.selectPreviousFilterItem(BrowserView.COLUMN_ORDER[col]);
                            break;
                        case 3:
                            browser.selectPreviousFilterItem(BrowserView.COLUMN_ORDER[col]);
                            break;
                    }
                    break;
            }

            return;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onScene (final int scene, final ButtonEvent event)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void updateSceneButtons ()
    {
        this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE1, LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
        this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE2, LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
        this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE3, LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
        this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE4, LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
        this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE5, LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
        this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE6, LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
        this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE7, LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
        this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE8, LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
    }
}