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
import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.featuregroup.AbstractView;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.Views;


/**
 * The Drum sequencer configuration view.
 *
 * @author Jürgen Moßgraber
 */
public class DrumConfigView extends AbstractView<LaunchkeyMk3ControlSurface, LaunchkeyMk3Configuration>
{
    /**
     * Constructor.
     *
     * @param surface The controller
     * @param model The model
     */
    public DrumConfigView (final LaunchkeyMk3ControlSurface surface, final IModel model)
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
            padGrid.lightEx (x, 0, view.getResolutionIndex () == x ? LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_BLUE_HI : LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_BLUE_LO);

        padGrid.lightEx (0, 1, clip.canScrollStepsBackwards () ? LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_LIME_HI : LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_LIME_LO);
        padGrid.lightEx (1, 1, clip.canScrollStepsForwards () ? LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_LIME_HI : LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_LIME_LO);

        padGrid.lightEx (2, 1, LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_BLACK);
        padGrid.lightEx (3, 1, LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_BLACK);
        padGrid.lightEx (4, 1, LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_BLACK);
        padGrid.lightEx (5, 1, LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_BLACK);
        padGrid.lightEx (6, 1, LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_BLACK);
        padGrid.lightEx (7, 1, LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_BLACK);
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        if (buttonID == ButtonID.SCENE1)
            return LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_BLACK;
        return LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_WHITE;
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        if (buttonID == ButtonID.SCENE2 && event == ButtonEvent.UP)
            this.surface.getViewManager ().restore ();
    }
}