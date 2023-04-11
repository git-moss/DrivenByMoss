// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchkey.mini.view;

import de.mossgrabers.controller.novation.launchkey.mini.LaunchkeyMiniMk3Configuration;
import de.mossgrabers.controller.novation.launchkey.mini.controller.LaunchkeyMiniMk3ColorManager;
import de.mossgrabers.controller.novation.launchkey.mini.controller.LaunchkeyMiniMk3ControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.featuregroup.AbstractView;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.Views;


/**
 * The Drum sequencer configuration view.
 *
 * @author Jürgen Moßgraber
 */
public class DrumConfigView extends AbstractView<LaunchkeyMiniMk3ControlSurface, LaunchkeyMiniMk3Configuration>
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

        final DrumView view = (DrumView) this.surface.getViewManager ().get (Views.DRUM);
        if (index / 8 == 1)
        {
            view.setResolutionIndex (col);
            return;
        }

        final INoteClip clip = view.getClip ();
        switch (col)
        {
            case 0:
                clip.scrollStepsPageBackwards ();
                this.mvHelper.notifyEditPage (clip);
                break;
            case 1:
                clip.scrollStepsPageForward ();
                this.mvHelper.notifyEditPage (clip);
                break;

            case 3:
                this.model.getTransport ().toggleMetronome ();
                this.surface.scheduleTask ( () -> this.surface.getDisplay ().notify ("Metronome: " + (this.model.getTransport ().isMetronomeOn () ? "On" : "Off")), 100);
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
        final DrumView view = (DrumView) this.surface.getViewManager ().get (Views.DRUM);
        final INoteClip clip = view.getClip ();

        final IPadGrid padGrid = this.surface.getPadGrid ();
        for (int x = 0; x < 8; x++)
            padGrid.lightEx (x, 0, view.getResolutionIndex () == x ? LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_BLUE_HI : LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_BLUE_LO);

        padGrid.lightEx (0, 1, clip.canScrollStepsBackwards () ? LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_LIME_HI : LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_LIME_LO);
        padGrid.lightEx (1, 1, clip.canScrollStepsForwards () ? LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_LIME_HI : LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_LIME_LO);

        padGrid.lightEx (2, 1, LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_BLACK);
        padGrid.lightEx (3, 1, this.model.getTransport ().isMetronomeOn () ? LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_RED_HI : LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_RED_LO);
        padGrid.lightEx (4, 1, LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_BLACK);
        padGrid.lightEx (5, 1, LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_BLACK);

        padGrid.lightEx (6, 1, this.scales.canScrollDrumOctaveUp () ? LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_CYAN_HI : LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_CYAN_LO);
        padGrid.lightEx (7, 1, this.scales.canScrollDrumOctaveDown () ? LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_CYAN_HI : LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_CYAN_LO);
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        if (buttonID == ButtonID.SCENE1)
            return LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_BLACK;
        return LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_WHITE;
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        if (buttonID == ButtonID.SCENE2 && event == ButtonEvent.UP)
            this.surface.getViewManager ().restore ();
    }
}