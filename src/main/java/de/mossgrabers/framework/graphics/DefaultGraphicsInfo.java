// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.graphics;

/**
 * Default implementation for the necessary data for drawing a component in a graphics context.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DefaultGraphicsInfo implements IGraphicsInfo
{
    private final IGraphicsContext       gc;
    private final IGraphicsConfiguration configuration;
    private final IGraphicsDimensions    dimensions;
    private final IBounds                bounds;


    /**
     * Constructor.
     *
     * @param gc The graphics context
     * @param configuration The configuration
     * @param dimensions The pre-calculated dimensions
     */
    public DefaultGraphicsInfo (final IGraphicsContext gc, final IGraphicsConfiguration configuration, final IGraphicsDimensions dimensions)
    {
        this.gc = gc;
        this.configuration = configuration;
        this.dimensions = dimensions;
        this.bounds = null;
    }


    /**
     * Constructor.
     *
     * @param gc The graphics context
     * @param configuration The configuration
     * @param dimensions The pre-calculated dimensions
     * @param bounds The bounds
     */
    public DefaultGraphicsInfo (final IGraphicsContext gc, final IGraphicsConfiguration configuration, final IGraphicsDimensions dimensions, final IBounds bounds)
    {
        this.gc = gc;
        this.configuration = configuration;
        this.dimensions = dimensions;
        this.bounds = bounds;
    }


    /** {@inheritDoc} */
    @Override
    public IGraphicsContext getContext ()
    {
        return this.gc;
    }


    /** {@inheritDoc} */
    @Override
    public IGraphicsConfiguration getConfiguration ()
    {
        return this.configuration;
    }


    /** {@inheritDoc} */
    @Override
    public IGraphicsDimensions getDimensions ()
    {
        return this.dimensions;
    }


    /** {@inheritDoc} */
    @Override
    public IBounds getBounds ()
    {
        return this.bounds;
    }


    /** {@inheritDoc} */
    @Override
    public IGraphicsInfo withBounds (final double top, final double height)
    {
        return this.withBounds (this.bounds.left (), top, this.bounds.width (), height);
    }


    /** {@inheritDoc} */
    @Override
    public IGraphicsInfo withBounds (final double left, final double top, final double width, final double height)
    {
        return new DefaultGraphicsInfo (this.gc, this.configuration, this.dimensions, new DefaultBounds (left, top, width, height));
    }
}
