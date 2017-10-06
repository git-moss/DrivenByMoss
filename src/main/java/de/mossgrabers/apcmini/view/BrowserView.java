// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.apcmini.view;

import de.mossgrabers.apcmini.APCminiConfiguration;
import de.mossgrabers.apcmini.controller.APCminiColors;
import de.mossgrabers.apcmini.controller.APCminiControlSurface;
import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.controller.grid.PadGrid;
import de.mossgrabers.framework.daw.BrowserProxy;
import de.mossgrabers.framework.view.AbstractView;
import de.mossgrabers.framework.view.SceneView;
import de.mossgrabers.framework.view.ViewManager;


/**
 * The Browser view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class BrowserView extends AbstractView<APCminiControlSurface, APCminiConfiguration> implements SceneView, APCminiView
{
    private static final int [] COLUMN_COLORS =
    {
        APCminiColors.APC_COLOR_GREEN,
        APCminiColors.APC_COLOR_RED,
        APCminiColors.APC_COLOR_GREEN,
        APCminiColors.APC_COLOR_RED,
        APCminiColors.APC_COLOR_GREEN,
        APCminiColors.APC_COLOR_RED,
        APCminiColors.APC_COLOR_BLACK,
        APCminiColors.APC_COLOR_YELLOW,
    };


    /**
     * Constructor.
     *
     * @param surface The controller
     * @param model The model
     */
    public BrowserView (final APCminiControlSurface surface, final Model model)
    {
        super ("Browser", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final PadGrid padGrid = this.surface.getPadGrid ();
        padGrid.light (36, APCminiColors.APC_COLOR_RED_BLINK);
        padGrid.light (37, APCminiColors.APC_COLOR_BLACK);
        for (int i = 38; i < 42; i++)
            padGrid.light (i, APCminiColors.APC_COLOR_YELLOW);
        padGrid.light (42, APCminiColors.APC_COLOR_BLACK);
        padGrid.light (43, APCminiColors.APC_COLOR_GREEN_BLINK);
        for (int i = 44; i < 52; i++)
            padGrid.light (i, APCminiColors.APC_COLOR_BLACK);

        for (int i = 52; i < 60; i++)
            padGrid.light (i, COLUMN_COLORS[i - 52]);
        for (int i = 60; i < 68; i++)
            padGrid.light (i, COLUMN_COLORS[i - 60]);
        for (int i = 68; i < 76; i++)
            padGrid.light (i, COLUMN_COLORS[i - 68]);
        for (int i = 76; i < 84; i++)
            padGrid.light (i, COLUMN_COLORS[i - 76]);

        for (int i = 84; i < 100; i++)
            padGrid.light (i, APCminiColors.APC_COLOR_BLACK);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        final BrowserProxy browser = this.model.getBrowser ();
        final ViewManager viewManager = this.surface.getViewManager ();
        if (!browser.isActive ())
        {
            if (viewManager.isActiveView (Views.VIEW_BROWSER))
                viewManager.restoreView ();
            return;
        }

        switch (note)
        {
            // Cancel
            case 0:
                if (velocity == 0)
                    return;
                this.model.getBrowser ().stopBrowsing (false);
                viewManager.restoreView ();
                break;

            // OK
            case 7:
                if (velocity == 0)
                    return;
                this.model.getBrowser ().stopBrowsing (true);
                viewManager.restoreView ();
                break;

            case 2:
                this.surface.sendMidiEvent (0x90, 48, velocity);
                break;
            case 3:
                this.surface.sendMidiEvent (0x90, 60, velocity);
                break;
            case 4:
                this.surface.sendMidiEvent (0x90, 72, velocity);
                break;
            case 5:
                this.surface.sendMidiEvent (0x90, 84, velocity);
                break;
        }

        if (velocity == 0)
            return;

        if (note >= 16 && note < 48)
        {
            final int n = note - 16;
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
                            browser.selectNextFilterItem(col);
                            break;
                        case 1:
                            for (int i = 0; i < 8; i++)
                                browser.selectNextFilterItem(col);
                            break;
                        case 2:
                            for (int i = 0; i < 8; i++)
                                browser.selectPreviousFilterItem(col);
                            break;
                        case 3:
                            browser.selectPreviousFilterItem(col);
                            break;
                    }
                    break;
            }
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
    public void onSelectTrack (final int index, final ButtonEvent event)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void updateSceneButtons ()
    {
        for (int i = 0; i < 8; i++)
        {
            this.surface.updateButton (APCminiControlSurface.APC_BUTTON_SCENE_BUTTON1 + i, APCminiControlSurface.APC_BUTTON_STATE_OFF);
            this.surface.updateButton (APCminiControlSurface.APC_BUTTON_TRACK_BUTTON1 + i, APCminiControlSurface.APC_BUTTON_STATE_OFF);
        }
    }
}