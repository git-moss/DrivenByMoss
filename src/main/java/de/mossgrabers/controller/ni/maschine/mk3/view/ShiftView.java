// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.mk3.view;

import de.mossgrabers.controller.ni.maschine.core.MaschineColorManager;
import de.mossgrabers.controller.ni.maschine.mk3.controller.MaschineControlSurface;
import de.mossgrabers.framework.controller.display.IDisplay;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.clip.IClip;
import de.mossgrabers.framework.daw.clip.INoteClip;


/**
 * The Shift view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ShiftView extends BaseView
{
    /**
     * Constructor.
     *
     * @param surface The controller
     * @param model The model
     */
    public ShiftView (final MaschineControlSurface surface, final IModel model)
    {
        super ("Shift", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void executeFunction (final int padIndex)
    {
        final IDisplay display = this.surface.getDisplay ();
        final IClip clip = this.model.getCursorClip ();

        this.surface.setStopConsumed ();

        switch (padIndex)
        {
            case 0:
                this.model.getApplication ().undo ();
                display.notify ("Undo");
                break;

            case 1:
                this.model.getApplication ().redo ();
                display.notify ("Redo");
                break;

            case 2:
                // Step Undo - not used
                break;

            case 3:
                // Step Redo - not used
                break;

            case 4:
                if (clip.doesExist ())
                {
                    clip.quantize (1);
                    display.notify ("Quantize: 100%");
                }
                break;

            case 5:
                if (clip.doesExist ())
                {
                    clip.quantize (0.5);
                    display.notify ("Quantize: 50%");
                }
                break;

            case 6:
                // Nudge left - not used
                break;

            case 7:
                // Nudge right - not used
                break;

            case 8:
                if (clip.doesExist () && clip instanceof final INoteClip noteClip)
                {
                    noteClip.clearAll ();
                    display.notify ("Clear all notes");
                }
                break;

            case 9:
                // Clear Automation - not used
                break;

            case 10:
                // Copy - not used
                break;

            case 11:
                // Paste - not used
                break;

            case 12:
                if (clip.doesExist () && clip instanceof final INoteClip noteClip)
                {
                    noteClip.transpose (-1);
                    display.notify ("Transpose: -1");
                }
                break;

            case 13:
                if (clip.doesExist () && clip instanceof final INoteClip noteClip)
                {
                    noteClip.transpose (1);
                    display.notify ("Transpose: 1");
                }
                break;

            case 14:
                if (clip.doesExist () && clip instanceof final INoteClip noteClip)
                {
                    noteClip.transpose (-12);
                    display.notify ("Transpose: -12");
                }
                break;

            case 15:
                if (clip.doesExist () && clip instanceof final INoteClip noteClip)
                {
                    noteClip.transpose (12);
                    display.notify ("Transpose: 12");
                }
                break;

            default:
                // Do nothing
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final IPadGrid padGrid = this.surface.getPadGrid ();

        final IClip clip = this.model.getCursorClip ();
        final boolean exists = clip.doesExist () && clip instanceof INoteClip;

        padGrid.lightEx (0, 0, exists ? MaschineColorManager.COLOR_TURQUOISE : MaschineColorManager.COLOR_BLACK);
        padGrid.lightEx (1, 0, exists ? MaschineColorManager.COLOR_TURQUOISE : MaschineColorManager.COLOR_BLACK);
        padGrid.lightEx (2, 0, exists ? MaschineColorManager.COLOR_TURQUOISE : MaschineColorManager.COLOR_BLACK);
        padGrid.lightEx (3, 0, exists ? MaschineColorManager.COLOR_TURQUOISE : MaschineColorManager.COLOR_BLACK);

        padGrid.lightEx (0, 1, exists ? MaschineColorManager.COLOR_AMBER : MaschineColorManager.COLOR_BLACK);
        padGrid.lightEx (1, 1, MaschineColorManager.COLOR_BLACK);
        padGrid.lightEx (2, 1, MaschineColorManager.COLOR_BLACK);
        padGrid.lightEx (3, 1, MaschineColorManager.COLOR_BLACK);

        padGrid.lightEx (0, 2, exists ? MaschineColorManager.COLOR_YELLOW : MaschineColorManager.COLOR_BLACK);
        padGrid.lightEx (1, 2, exists ? MaschineColorManager.COLOR_YELLOW : MaschineColorManager.COLOR_BLACK);
        padGrid.lightEx (2, 2, MaschineColorManager.COLOR_BLACK);
        padGrid.lightEx (3, 2, MaschineColorManager.COLOR_BLACK);

        padGrid.lightEx (0, 3, MaschineColorManager.COLOR_RED);
        padGrid.lightEx (1, 3, MaschineColorManager.COLOR_YELLOW);
        padGrid.lightEx (2, 3, MaschineColorManager.COLOR_BLACK);
        padGrid.lightEx (3, 3, MaschineColorManager.COLOR_BLACK);
    }
}