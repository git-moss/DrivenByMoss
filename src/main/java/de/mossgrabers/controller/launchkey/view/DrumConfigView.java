// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchkey.view;

import de.mossgrabers.controller.launchkey.LaunchkeyMiniMk3Configuration;
import de.mossgrabers.controller.launchkey.controller.LaunchkeyMiniMk3Colors;
import de.mossgrabers.controller.launchkey.controller.LaunchkeyMiniMk3ControlSurface;
import de.mossgrabers.framework.controller.grid.PadGrid;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractView;
import de.mossgrabers.framework.view.SceneView;
import de.mossgrabers.framework.view.Views;


/**
 * The Drum sequencer configuration view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DrumConfigView extends AbstractView<LaunchkeyMiniMk3ControlSurface, LaunchkeyMiniMk3Configuration> implements SceneView
{
    /**
     * Constructor.
     *
     * @param surface The controller
     * @param model The model
     */
    public DrumConfigView (final LaunchkeyMiniMk3ControlSurface surface, final IModel model)
    {
        super ("Drum Configuration", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (velocity == 0)
            return;

        final int index = note - 36;
        final int col = index % 8;

        final DrumView view = (DrumView) this.surface.getViewManager ().getView (Views.DRUM);
        if (index / 8 == 1)
        {
            view.setResolutionIndex (col);
            return;
        }

        switch (col)
        {
            case 0:
                view.getClip ().scrollStepsPageBackwards ();
                break;
            case 1:
                view.getClip ().scrollStepsPageForward ();
                break;

            case 3:
                this.model.getTransport ().toggleMetronome ();
                break;

            case 6:
                view.onOctaveDown (ButtonEvent.DOWN);
                break;
            case 7:
                view.onOctaveUp (ButtonEvent.DOWN);
                break;

            default:
                // Not used
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final DrumView view = (DrumView) this.surface.getViewManager ().getView (Views.DRUM);
        final INoteClip clip = view.getClip ();

        final PadGrid padGrid = this.surface.getPadGrid ();
        for (int x = 0; x < 8; x++)
            padGrid.lightEx (x, 0, view.getResolutionIndex () == x ? LaunchkeyMiniMk3Colors.LAUNCHKEY_COLOR_BLUE_HI : LaunchkeyMiniMk3Colors.LAUNCHKEY_COLOR_BLUE_LO);

        padGrid.lightEx (0, 1, clip.canScrollStepsBackwards () ? LaunchkeyMiniMk3Colors.LAUNCHKEY_COLOR_LIME_HI : LaunchkeyMiniMk3Colors.LAUNCHKEY_COLOR_LIME_LO);
        padGrid.lightEx (1, 1, clip.canScrollStepsForwards () ? LaunchkeyMiniMk3Colors.LAUNCHKEY_COLOR_LIME_HI : LaunchkeyMiniMk3Colors.LAUNCHKEY_COLOR_LIME_LO);

        padGrid.lightEx (2, 1, LaunchkeyMiniMk3Colors.LAUNCHKEY_COLOR_BLACK);
        padGrid.lightEx (3, 1, this.model.getTransport ().isMetronomeOn () ? LaunchkeyMiniMk3Colors.LAUNCHKEY_COLOR_RED_HI : LaunchkeyMiniMk3Colors.LAUNCHKEY_COLOR_RED_LO);
        padGrid.lightEx (4, 1, LaunchkeyMiniMk3Colors.LAUNCHKEY_COLOR_BLACK);
        padGrid.lightEx (5, 1, LaunchkeyMiniMk3Colors.LAUNCHKEY_COLOR_BLACK);

        padGrid.lightEx (6, 1, this.scales.canScrollDrumOctaveUp () ? LaunchkeyMiniMk3Colors.LAUNCHKEY_COLOR_CYAN_HI : LaunchkeyMiniMk3Colors.LAUNCHKEY_COLOR_CYAN_LO);
        padGrid.lightEx (7, 1, this.scales.canScrollDrumOctaveDown () ? LaunchkeyMiniMk3Colors.LAUNCHKEY_COLOR_CYAN_HI : LaunchkeyMiniMk3Colors.LAUNCHKEY_COLOR_CYAN_LO);
    }


    /** {@inheritDoc} */
    @Override
    public void updateSceneButtons ()
    {
        this.surface.updateTrigger (LaunchkeyMiniMk3ControlSurface.LAUNCHKEY_SCENE1, LaunchkeyMiniMk3Colors.LAUNCHKEY_COLOR_BLACK);
        this.surface.updateTrigger (LaunchkeyMiniMk3ControlSurface.LAUNCHKEY_SCENE2, LaunchkeyMiniMk3Colors.LAUNCHKEY_COLOR_WHITE);
    }


    /** {@inheritDoc} */
    @Override
    public void onScene (final int index, final ButtonEvent event)
    {
        if (event == ButtonEvent.UP)
            this.surface.getViewManager ().restoreView ();
    }
}