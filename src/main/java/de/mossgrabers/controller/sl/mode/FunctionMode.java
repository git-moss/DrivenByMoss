// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.sl.mode;

import de.mossgrabers.controller.sl.SLConfiguration;
import de.mossgrabers.controller.sl.controller.SLControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.AbstractMode;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Mode for executing several functions.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class FunctionMode extends AbstractMode<SLControlSurface, SLConfiguration>
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
        this.surface.getTextDisplay ().setCell (0, 0, "  Undo").setCell (0, 1, "  Redo").setCell (0, 2, " Delete").setCell (0, 3, " Double").setCell (0, 4, "  New").setCell (0, 5, " Window ").setCell (0, 6, "Metronom").setCell (0, 7, "TapTempo").clearRow (2).done (0).done (2);
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int index, final ButtonEvent event)
    {
        // Intentionally empty
    }
}