// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.graphics.grid;

import de.mossgrabers.framework.graphics.IGraphicsConfiguration;
import de.mossgrabers.framework.graphics.IGraphicsContext;
import de.mossgrabers.framework.graphics.IGraphicsDimensions;


/**
 * An element in the grid.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IGridElement
{
    /**
     * Draw the element.
     *
     * @param gc The graphic context
     * @param configuration The layout settings to use
     * @param dimensions Pre-calculated dimensions
     * @param left The left bound of the drawing area of the element
     * @param width The width of the drawing area of the element
     * @param height The height of the drawing area of the element
     */
    void draw (final IGraphicsContext gc, final IGraphicsConfiguration configuration, IGraphicsDimensions dimensions, final double left, final double width, final double height);
}
