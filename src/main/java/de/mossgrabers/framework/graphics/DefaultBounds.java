// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.graphics;

/**
 * Default implementation for boundaries.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DefaultBounds implements IBounds
{
    private final double left;
    private final double top;
    private final double width;
    private final double height;


    /**
     * Constructor.
     *
     * @param left The left bound of the drawing area
     * @param top The top bound of the drawing area
     * @param width The width of the drawing area
     * @param height The height of the drawing area
     */
    public DefaultBounds (final double left, final double top, final double width, final double height)
    {
        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;
    }


    /** {@inheritDoc} */
    @Override
    public double getLeft ()
    {
        return this.left;
    }


    /** {@inheritDoc} */
    @Override
    public double getTop ()
    {
        return this.top;
    }


    /** {@inheritDoc} */
    @Override
    public double getWidth ()
    {
        return this.width;
    }


    /** {@inheritDoc} */
    @Override
    public double getHeight ()
    {
        return this.height;
    }
}
