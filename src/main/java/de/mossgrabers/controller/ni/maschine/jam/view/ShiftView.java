// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.jam.view;

import de.mossgrabers.controller.ni.maschine.core.MaschineColorManager;
import de.mossgrabers.controller.ni.maschine.jam.MaschineJamConfiguration;
import de.mossgrabers.controller.ni.maschine.jam.controller.MaschineJamControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.daw.data.bank.ISceneBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.featuregroup.AbstractView;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Simulates the missing buttons (in contrast to MaschineJam Pro) on the grid.
 *
 * @author Jürgen Moßgraber
 */
public class ShiftView extends AbstractView<MaschineJamControlSurface, MaschineJamConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public ShiftView (final MaschineJamControlSurface surface, final IModel model)
    {
        super ("Shift", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final IPadGrid padGrid = this.surface.getPadGrid ();

        for (int i = 36; i < 92; i++)
            padGrid.light (i, MaschineColorManager.COLOR_BLACK);

        padGrid.light (92, MaschineColorManager.COLOR_RED_LO);
        padGrid.light (93, MaschineColorManager.COLOR_GREEN_LO);
        padGrid.light (94, MaschineColorManager.COLOR_LIME_LO);
        padGrid.light (95, MaschineColorManager.COLOR_LIME_LO);

        padGrid.light (96, MaschineColorManager.COLOR_MAGENTA_LO);
        padGrid.light (97, MaschineColorManager.COLOR_MAGENTA_LO);
        padGrid.light (98, MaschineColorManager.COLOR_ROSE);
        padGrid.light (99, MaschineColorManager.COLOR_ROSE);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (velocity == 0)
            return;

        final INoteClip cursorClip = this.model.getCursorClip ();
        switch (note)
        {
            case 92:
                this.model.getApplication ().undo ();
                return;
            case 93:
                this.model.getApplication ().redo ();
                return;

            case 94:
                if (cursorClip.doesExist ())
                    cursorClip.quantize (1);
                return;
            case 95:
                if (cursorClip.doesExist ())
                    cursorClip.quantize (0.5);
                return;

            case 96:
                if (cursorClip.doesExist ())
                    cursorClip.transpose (-1);
                return;
            case 97:
                if (cursorClip.doesExist ())
                    cursorClip.transpose (1);
                return;
            case 98:
                if (cursorClip.doesExist ())
                    cursorClip.transpose (-12);
                return;
            case 99:
                if (cursorClip.doesExist ())
                    cursorClip.transpose (12);
                return;

            default:
                // Fall through to be handled below
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        final boolean flipSession = this.surface.getConfiguration ().isFlipSession ();
        final boolean isDown = event == ButtonEvent.DOWN;

        switch (buttonID)
        {
            case ARROW_LEFT:
            case ARROW_RIGHT:
                if (isDown)
                {
                    if (flipSession)
                        this.scrollSceneBank (buttonID == ButtonID.ARROW_RIGHT);
                    else
                        this.scrollTrackBank (buttonID == ButtonID.ARROW_RIGHT);
                }
                break;

            case ARROW_UP:
            case ARROW_DOWN:
                if (isDown)
                {
                    if (flipSession)
                        this.scrollTrackBank (buttonID == ButtonID.ARROW_DOWN);
                    else
                        this.scrollSceneBank (buttonID == ButtonID.ARROW_DOWN);
                }
                break;

            default:
                super.onButton (buttonID, event, velocity);
                break;
        }
    }


    private void scrollTrackBank (final boolean isForwards)
    {
        final ITrackBank trackBank = this.model.getCurrentTrackBank ();
        if (isForwards)
            trackBank.selectNextPage ();
        else
            trackBank.selectPreviousPage ();
    }


    private void scrollSceneBank (final boolean isForwards)
    {
        final ISceneBank sceneBank = this.model.getCurrentTrackBank ().getSceneBank ();
        if (isForwards)
            sceneBank.selectNextPage ();
        else
            sceneBank.selectPreviousPage ();
    }
}