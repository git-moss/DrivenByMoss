// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.sl.mode;

import de.mossgrabers.controller.novation.sl.SLConfiguration;
import de.mossgrabers.controller.novation.sl.controller.SLControlSurface;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.featuregroup.AbstractParameterMode;


/**
 * Mode for executing several functions.
 *
 * @author Jürgen Moßgraber
 */
public class FunctionMode extends AbstractParameterMode<SLControlSurface, SLConfiguration, IItem>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public FunctionMode (final SLControlSurface surface, final IModel model)
    {
        super ("Functions", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final ITextDisplay d = this.surface.getTextDisplay ().clearRow (0).clearRow (1);
        d.setBlock (0, 0, "Functions:");
        d.setCell (1, 0, "  Undo").setCell (1, 1, "  Redo").setCell (1, 2, " Delete").setCell (1, 3, " Double");
        d.setCell (1, 4, "  New").setCell (1, 5, " Window ").setCell (1, 6, "Metronom").setCell (1, 7, "TapTempo");
        d.done (0).done (1);
    }
}