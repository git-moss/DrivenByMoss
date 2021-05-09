// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.jam.view;

import de.mossgrabers.controller.ni.maschine.core.MaschineColorManager;
import de.mossgrabers.controller.ni.maschine.jam.MaschineJamConfiguration;
import de.mossgrabers.controller.ni.maschine.jam.controller.MaschineJamControlSurface;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.featuregroup.AbstractView;


/**
 * Simulates the missing buttons (in contrast to MaschineJam Pro) on the grid.
 *
 * @author J&uuml;rgen Mo&szlig;graber
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
}