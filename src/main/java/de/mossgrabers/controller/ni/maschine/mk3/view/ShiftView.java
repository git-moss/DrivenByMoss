// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.mk3.view;

import de.mossgrabers.controller.ni.maschine.core.MaschineColorManager;
import de.mossgrabers.controller.ni.maschine.mk3.MaschineConfiguration;
import de.mossgrabers.controller.ni.maschine.mk3.controller.MaschineControlSurface;
import de.mossgrabers.framework.controller.display.IDisplay;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.IApplication;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.clip.IClip;
import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractShiftView;


/**
 * The Shift view.
 *
 * @author Jürgen Moßgraber
 */
public class ShiftView extends AbstractShiftView<MaschineControlSurface, MaschineConfiguration> implements IExecuteFunction
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
    public void onGridNote (final int note, final int velocity)
    {
        if (velocity == 0)
            return;
        this.setWasUsed ();
        this.executeFunction (note - 36, ButtonEvent.DOWN);
    }


    /** {@inheritDoc} */
    @Override
    public void executeFunction (final int padIndex, final ButtonEvent buttonEvent)
    {
        if (buttonEvent != ButtonEvent.DOWN)
            return;

        final IDisplay display = this.surface.getDisplay ();
        final IClip clip = this.model.getCursorClip ();

        this.surface.setStopConsumed ();

        if (padIndex == 0 || padIndex == 1)
        {
            final IApplication application = this.model.getApplication ();
            if (padIndex == 0)
            {
                application.undo ();
                display.notify ("Undo");
            }
            else
            {
                application.redo ();
                display.notify ("Redo");
            }
        }

        if (clip.doesExist () && clip instanceof final INoteClip noteClip)
        {
            switch (padIndex)
            {
                case 4:
                    clip.quantize (1);
                    display.notify ("Quantize: 100%");
                    break;

                case 5:
                    clip.quantize (0.5);
                    display.notify ("Quantize: 50%");
                    break;

                case 8:
                    noteClip.clearAll ();
                    display.notify ("Clear all notes");
                    break;

                case 12:
                    noteClip.transpose (-1);
                    display.notify ("Transpose: -1");
                    break;

                case 13:
                    noteClip.transpose (1);
                    display.notify ("Transpose: 1");
                    break;

                case 14:
                    noteClip.transpose (-12);
                    display.notify ("Transpose: -12");
                    break;

                case 15:
                    noteClip.transpose (12);
                    display.notify ("Transpose: 12");
                    break;

                default:
                    // 2: Step Undo - not used
                    // 3: Step Redo - not used
                    // 6: Nudge left - not used
                    // 7: Nudge right - not used
                    // 9: Clear Automation - not used
                    // 10: Copy - not used
                    // 11: Paste - not used
                    break;
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final IClip clip = this.model.getCursorClip ();
        final boolean exists = clip.doesExist () && clip instanceof INoteClip;

        final IPadGrid padGrid = this.surface.getPadGrid ();
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