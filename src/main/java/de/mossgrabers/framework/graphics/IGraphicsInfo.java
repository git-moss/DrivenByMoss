// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.graphics;

/**
 * All necessary data for drawing a component in a graphics context.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IGraphicsInfo
{
    /**
     * Get the graphics context to draw to.
     *
     * @return The graphics context
     */
    IGraphicsContext getContext ();


    /**
     * Get the configuration settings.
     *
     * @return The configuration
     */
    IGraphicsConfiguration getConfiguration ();


    /**
     * Get the pre-calculated dimensions.
     *
     * @return The dimensions
     */
    IGraphicsDimensions getDimensions ();


    /**
     * Get the bounds into which to draw the component.
     *
     * @return The bounds
     */
    IBounds getBounds ();


    /**
     * Clones this object and replaces the bounds object with the given bounds parameters.
     *
     * @param top The top bound of the drawing area
     * @param height The height of the drawing area
     * @return The new instance
     */
    IGraphicsInfo withBounds (final double top, final double height);


    /**
     * Clones this object and replaces the bounds object with the given bounds parameters.
     *
     * @param left The left bound of the drawing area
     * @param top The top bound of the drawing area
     * @param width The width of the drawing area
     * @param height The height of the drawing area
     * @return The new instance
     */
    IGraphicsInfo withBounds (final double left, final double top, final double width, final double height);
}
