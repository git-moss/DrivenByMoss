// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.controller.display.grid;

import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.framework.graphics.IGraphicsContext;


/**
 * An element in the grid.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface GridElement
{
    /** The full width of the drawing area. */
    static final int    DISPLAY_WIDTH  = 960;
    /** The full height of the drawing area. */
    static final int    DISPLAY_HEIGHT = 160;

    /** The size to use for separator spacing. */
    static final double SEPARATOR_SIZE = 2.0;
    /** A drawing 'unit'. */
    static final double UNIT           = DISPLAY_HEIGHT / 12.0;
    /** 2 units. */
    static final double DOUBLE_UNIT    = 2.0 * UNIT;
    /** Half a unit. */
    static final double HALF_UNIT      = UNIT / 2.0;
    /** The height of the menu on top. */
    static final double MENU_HEIGHT    = UNIT + 2.0 * SEPARATOR_SIZE;
    /** Insets on the top and bottom of the element. */
    static final double INSET          = SEPARATOR_SIZE / 2.0 + HALF_UNIT;
    /** Where the controls drawing area starts. */
    static final double CONTROLS_TOP   = MENU_HEIGHT + INSET;


    /**
     * Draw the element.
     *
     * @param gc The graphic context
     * @param left The left bound of the drawing area of the element
     * @param width The width of the drawing area of the element
     * @param height The height of the drawing area of the element
     * @param configuration The layout settings to use
     */
    void draw (final IGraphicsContext gc, final double left, final double width, final double height, final PushConfiguration configuration);
}
