// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.sl.controller;

import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.IPadGrid;


/**
 * Color states to use for the Maschine Mikro Mk3 buttons.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SLColorManager extends ColorManager
{
    /**
     * Constructor.
     */
    public SLColorManager ()
    {
        this.registerColorIndex (IPadGrid.GRID_OFF, 0);

        this.registerColorIndex (ColorManager.BUTTON_STATE_OFF, 0);
        this.registerColorIndex (ColorManager.BUTTON_STATE_ON, 0);
        this.registerColorIndex (ColorManager.BUTTON_STATE_HI, 127);
    }


    /** {@inheritDoc} */
    @Override
    public ColorEx getColor (final int colorIndex, final ButtonID buttonID)
    {
        if (colorIndex < 0)
            return ColorEx.BLACK;

        return colorIndex > 0 ? ColorEx.RED : ColorEx.BLACK;
    }
}