// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchpad.view;

import de.mossgrabers.controller.novation.launchpad.LaunchpadConfiguration;
import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadColorManager;
import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.IApplication;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.AbstractView;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Project related settings like project navigation, layout and panes.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ProjectView extends AbstractView<LaunchpadControlSurface, LaunchpadConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public ProjectView (final LaunchpadControlSurface surface, final IModel model)
    {
        super ("Project", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final IPadGrid padGrid = this.surface.getPadGrid ();

        //////////////////////////////////////////////////////////////////////
        // First row (from bottom)

        padGrid.light (36, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);

        // Toggle Note Editor
        padGrid.light (37, LaunchpadColorManager.LAUNCHPAD_COLOR_AMBER_HI);

        // Toggle Automation Editor
        padGrid.light (38, LaunchpadColorManager.LAUNCHPAD_COLOR_RED_HI);

        padGrid.light (39, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);

        // Toggle Devices
        padGrid.light (40, LaunchpadColorManager.LAUNCHPAD_COLOR_TURQUOISE_HI);

        padGrid.light (41, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);

        // Toggle Mixer
        padGrid.light (42, LaunchpadColorManager.LAUNCHPAD_COLOR_LIME_HI);

        padGrid.light (43, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);

        //////////////////////////////////////////////////////////////////////
        // Row 2

        for (int i = 44; i < 52; i++)
            padGrid.light (i, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);

        //////////////////////////////////////////////////////////////////////
        // Row 3

        padGrid.light (52, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);
        padGrid.light (53, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);

        // Select layout 1
        padGrid.light (54, LaunchpadColorManager.LAUNCHPAD_COLOR_BLUE_LO);

        padGrid.light (55, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);

        // Select layout 2
        padGrid.light (56, LaunchpadColorManager.LAUNCHPAD_COLOR_BLUE);

        padGrid.light (57, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);

        // Select layout 3
        padGrid.light (58, LaunchpadColorManager.LAUNCHPAD_COLOR_BLUE_HI);

        padGrid.light (59, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);

        //////////////////////////////////////////////////////////////////////
        // Row 4

        for (int i = 60; i < 68; i++)
            padGrid.light (i, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);

        //////////////////////////////////////////////////////////////////////
        // Row 5

        // Toggle inspector
        padGrid.light (68, LaunchpadColorManager.LAUNCHPAD_COLOR_PINK_HI);

        for (int i = 69; i < 75; i++)
            padGrid.light (i, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);

        // Toggle browser
        padGrid.light (75, LaunchpadColorManager.LAUNCHPAD_COLOR_MAGENTA);

        //////////////////////////////////////////////////////////////////////
        // Row 6 & 7

        for (int i = 76; i < 92; i++)
            padGrid.light (i, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);

        //////////////////////////////////////////////////////////////////////
        // Row 8

        padGrid.light (92, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);

        // Select Previous Project
        padGrid.light (93, LaunchpadColorManager.LAUNCHPAD_COLOR_OCEAN_HI);
        padGrid.light (94, LaunchpadColorManager.LAUNCHPAD_COLOR_OCEAN_HI);

        final boolean isEngineActive = this.model.getApplication ().isEngineActive ();
        padGrid.light (95, isEngineActive ? LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_RED_LO);
        padGrid.light (96, isEngineActive ? LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_RED_LO);

        // Select Next Project
        padGrid.light (97, LaunchpadColorManager.LAUNCHPAD_COLOR_OCEAN_HI);
        padGrid.light (98, LaunchpadColorManager.LAUNCHPAD_COLOR_OCEAN_HI);

        padGrid.light (99, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (velocity == 0)
            return;

        switch (note)
        {
            case 37:
                this.model.getApplication ().toggleNoteEditor ();
                break;

            case 38:
                this.model.getApplication ().toggleAutomationEditor ();
                break;

            case 40:
                this.model.getApplication ().toggleDevices ();
                break;

            case 42:
                this.model.getApplication ().toggleMixer ();
                break;

            case 54:
                this.model.getApplication ().setPanelLayout (IApplication.PANEL_LAYOUT_ARRANGE);
                break;

            case 56:
                this.model.getApplication ().setPanelLayout (IApplication.PANEL_LAYOUT_MIX);
                break;

            case 58:
                this.model.getApplication ().setPanelLayout (IApplication.PANEL_LAYOUT_EDIT);
                break;

            case 68:
                this.model.getApplication ().toggleInspector ();
                break;

            case 75:
                this.model.getApplication ().toggleBrowserVisibility ();
                break;

            case 93:
            case 94:
                this.model.getProject ().previous ();
                break;

            case 95:
            case 96:
                this.model.getApplication ().toggleEngineActive ();
                break;

            case 97:
            case 98:
                this.model.getProject ().next ();
                break;

            default:
                // Not used
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        return LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK;
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        // Intentionally empty
    }
}