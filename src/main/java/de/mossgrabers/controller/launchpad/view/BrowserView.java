// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchpad.view;

import de.mossgrabers.controller.launchpad.LaunchpadConfiguration;
import de.mossgrabers.controller.launchpad.controller.LaunchpadColorManager;
import de.mossgrabers.controller.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.IBrowser;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.AbstractView;


/**
 * Navigate the browser.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class BrowserView extends AbstractView<LaunchpadControlSurface, LaunchpadConfiguration>
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
        LaunchpadColorManager.LAUNCHPAD_COLOR_WHITE,
        LaunchpadColorManager.LAUNCHPAD_COLOR_GREY_MD,
        LaunchpadColorManager.LAUNCHPAD_COLOR_GREY_LO,
        LaunchpadColorManager.LAUNCHPAD_COLOR_ROSE,
        LaunchpadColorManager.LAUNCHPAD_COLOR_SPRING,
        LaunchpadColorManager.LAUNCHPAD_COLOR_OCEAN,
        LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK,
        LaunchpadColorManager.LAUNCHPAD_COLOR_YELLOW
    };


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public BrowserView (final LaunchpadControlSurface surface, final IModel model)
    {
        super ("Browser", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final IPadGrid padGrid = this.surface.getPadGrid ();
        padGrid.light (36, LaunchpadColorManager.LAUNCHPAD_COLOR_RED);
        padGrid.light (37, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);
        for (int i = 38; i < 42; i++)
            padGrid.light (i, LaunchpadColorManager.LAUNCHPAD_COLOR_ORCHID_LO);
        padGrid.light (42, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);
        padGrid.light (43, LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN_HI);
        for (int i = 44; i < 52; i++)
            padGrid.light (i, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);

        for (int i = 52; i < 60; i++)
            padGrid.light (i, COLUMN_COLORS[i - 52]);
        for (int i = 60; i < 68; i++)
            padGrid.light (i, COLUMN_COLORS[i - 60]);
        for (int i = 68; i < 76; i++)
            padGrid.light (i, COLUMN_COLORS[i - 68]);
        for (int i = 76; i < 84; i++)
            padGrid.light (i, COLUMN_COLORS[i - 76]);

        for (int i = 84; i < 100; i++)
            padGrid.light (i, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        final IBrowser browser = this.model.getBrowser ();
        if (!browser.isActive ())
            return;

        switch (note)
        {
            // Cancel
            case 36:
                if (velocity == 0)
                    return;
                browser.stopBrowsing (false);
                this.surface.getViewManager ().restore ();
                break;

            // OK
            case 43:
                if (velocity == 0)
                    return;
                browser.stopBrowsing (true);
                this.surface.getViewManager ().restore ();
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
            default:
                // Not used
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
                    if (row == 0)
                        browser.selectNextResult ();
                    else if (row == 1)
                    {
                        for (int i = 0; i < 8; i++)
                            browser.selectNextResult ();
                    }
                    else if (row == 2)
                    {
                        for (int i = 0; i < 8; i++)
                            browser.selectPreviousResult ();
                    }
                    else if (row == 3)
                        browser.selectPreviousResult ();
                    break;

                default:
                    if (row == 0)
                        browser.selectNextFilterItem (BrowserView.COLUMN_ORDER[col]);
                    else if (row == 1)
                    {
                        for (int i = 0; i < 8; i++)
                            browser.selectNextFilterItem (BrowserView.COLUMN_ORDER[col]);
                    }
                    else if (row == 2)
                    {
                        for (int i = 0; i < 8; i++)
                            browser.selectPreviousFilterItem (BrowserView.COLUMN_ORDER[col]);
                    }
                    else if (row == 3)
                        browser.selectPreviousFilterItem (BrowserView.COLUMN_ORDER[col]);
                    break;
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public String getButtonColorID (final ButtonID buttonID)
    {
        return ColorManager.BUTTON_STATE_OFF;
    }
}