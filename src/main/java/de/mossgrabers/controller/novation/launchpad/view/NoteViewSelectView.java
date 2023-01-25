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

        // From bottom to top

        for (int i = 36; i < 44; i++)
            padGrid.light (i, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);

        padGrid.light (44, previousViewId == Views.CLIP_LENGTH ? LaunchpadColorManager.COLOR_VIEW_SELECTED : LaunchpadColorManager.COLOR_VIEW_CLIP);

        for (int i = 45; i < 60; i++)
            padGrid.light (i, LaunchpadColorManager.COLOR_VIEW_OFF);

        padGrid.light (60, previousViewId == Views.SEQUENCER ? LaunchpadColorManager.COLOR_VIEW_SELECTED : LaunchpadColorManager.COLOR_VIEW_NOTE_SEQUENCER);
        padGrid.light (61, previousViewId == Views.POLY_SEQUENCER ? LaunchpadColorManager.COLOR_VIEW_SELECTED : LaunchpadColorManager.COLOR_VIEW_NOTE_SEQUENCER);
        padGrid.light (62, previousViewId == Views.RAINDROPS ? LaunchpadColorManager.COLOR_VIEW_SELECTED : LaunchpadColorManager.COLOR_VIEW_NOTE_SEQUENCER);

        for (int i = 63; i < 76; i++)
            padGrid.light (i, LaunchpadColorManager.COLOR_VIEW_OFF);

        padGrid.light (76, previousViewId == Views.DRUM ? LaunchpadColorManager.COLOR_VIEW_SELECTED : LaunchpadColorManager.COLOR_VIEW_DRUM_SEQUENCER);
        padGrid.light (77, previousViewId == Views.DRUM4 ? LaunchpadColorManager.COLOR_VIEW_SELECTED : LaunchpadColorManager.COLOR_VIEW_DRUM_SEQUENCER);
        padGrid.light (78, previousViewId == Views.DRUM8 ? LaunchpadColorManager.COLOR_VIEW_SELECTED : LaunchpadColorManager.COLOR_VIEW_DRUM_SEQUENCER);

        for (int i = 79; i < 92; i++)
            padGrid.light (i, LaunchpadColorManager.COLOR_VIEW_OFF);

        padGrid.light (92, previousViewId == Views.PLAY ? LaunchpadColorManager.COLOR_VIEW_SELECTED : LaunchpadColorManager.COLOR_VIEW_PLAY);
        padGrid.light (93, previousViewId == Views.PIANO ? LaunchpadColorManager.COLOR_VIEW_SELECTED : LaunchpadColorManager.COLOR_VIEW_PLAY);
        padGrid.light (94, previousViewId == Views.DRUM64 ? LaunchpadColorManager.COLOR_VIEW_SELECTED : LaunchpadColorManager.COLOR_VIEW_PLAY);

        for (int i = 95; i < 100; i++)
            padGrid.light (i, LaunchpadColorManager.COLOR_VIEW_OFF);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (velocity != 0)
            return;

        final Views viewID;

        switch (note)
        {
            // Play
            case 92:
                viewID = Views.PLAY;
                break;
            case 93:
                viewID = Views.PIANO;
                break;
            case 94:
                viewID = Views.DRUM64;
                break;

            // Drum Sequencer
            case 76:
                viewID = Views.DRUM;
                break;
            case 77:
                viewID = Views.DRUM4;
                break;
            case 78:
                viewID = Views.DRUM8;
                break;

            // Note sequencers
            case 60:
                viewID = Views.SEQUENCER;
                break;
            case 61:
                viewID = Views.POLY_SEQUENCER;
                break;
            case 62:
                viewID = Views.RAINDROPS;
                break;

            // Clip edit
            case 44:
                viewID = Views.CLIP_LENGTH;
                break;

            default:
                // Not used
                return;
        }

        this.activatePreferredView (viewID);
        this.surface.getDisplay ().notify (this.surface.getViewManager ().get (viewID).getName ());
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        return LaunchpadColorManager.COLOR_VIEW_SELECTED;
    }
}