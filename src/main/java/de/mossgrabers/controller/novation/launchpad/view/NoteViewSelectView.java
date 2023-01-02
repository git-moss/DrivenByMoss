// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchpad.view;

import de.mossgrabers.controller.novation.launchpad.LaunchpadConfiguration;
import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadColorManager;
import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.AbstractView;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.view.Views;


/**
 * View to select a note view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class NoteViewSelectView extends AbstractView<LaunchpadControlSurface, LaunchpadConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public NoteViewSelectView (final LaunchpadControlSurface surface, final IModel model)
    {
        super ("View select", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final ViewManager viewManager = this.surface.getViewManager ();
        final Views previousViewId = viewManager.getPreviousID ();

        final IPadGrid padGrid = this.surface.getPadGrid ();

        for (int i = 36; i < 60; i++)
            padGrid.light (i, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);

        padGrid.light (60, previousViewId == Views.SEQUENCER ? LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN : LaunchpadColorManager.LAUNCHPAD_COLOR_AMBER_HI);
        padGrid.light (61, previousViewId == Views.POLY_SEQUENCER ? LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN : LaunchpadColorManager.LAUNCHPAD_COLOR_AMBER_LO);
        padGrid.light (62, previousViewId == Views.RAINDROPS ? LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN : LaunchpadColorManager.LAUNCHPAD_COLOR_AMBER);

        for (int i = 63; i < 76; i++)
            padGrid.light (i, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);

        padGrid.light (76, previousViewId == Views.DRUM ? LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN : LaunchpadColorManager.LAUNCHPAD_COLOR_BLUE_HI);
        padGrid.light (77, previousViewId == Views.DRUM4 ? LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN : LaunchpadColorManager.LAUNCHPAD_COLOR_BLUE);
        padGrid.light (78, previousViewId == Views.DRUM8 ? LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN : LaunchpadColorManager.LAUNCHPAD_COLOR_BLUE_LO);

        for (int i = 79; i < 92; i++)
            padGrid.light (i, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);

        padGrid.light (92, previousViewId == Views.PLAY ? LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN : LaunchpadColorManager.LAUNCHPAD_COLOR_RED_HI);
        padGrid.light (93, previousViewId == Views.PIANO ? LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN : LaunchpadColorManager.LAUNCHPAD_COLOR_RED);
        padGrid.light (94, previousViewId == Views.DRUM64 ? LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN : LaunchpadColorManager.LAUNCHPAD_COLOR_RED_LO);

        for (int i = 95; i < 100; i++)
            padGrid.light (i, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (velocity != 0)
            return;

        switch (note)
        {
            case 92:
                this.setView (Views.PLAY);
                return;
            case 93:
                this.setView (Views.PIANO);
                return;
            case 94:
                this.setView (Views.DRUM64);
                return;
            case 76:
                this.setView (Views.DRUM);
                return;
            case 77:
                this.setView (Views.DRUM4);
                return;
            case 78:
                this.setView (Views.DRUM8);
                return;
            case 60:
                this.setView (Views.SEQUENCER);
                return;
            case 61:
                this.setView (Views.POLY_SEQUENCER);
                return;
            case 62:
                this.setView (Views.RAINDROPS);
                return;
            default:
                // Not used
                break;
        }
    }


    private void setView (final Views viewID)
    {
        this.activatePreferredView (viewID);
        this.surface.getDisplay ().notify (this.surface.getViewManager ().get (viewID).getName ());
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        return LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK;
    }
}