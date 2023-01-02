// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.graphics;

/**
 * Default implementation of pre-calculated grid dimensions.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DefaultGraphicsDimensions implements IGraphicsDimensions
{
    /** The size to use for separator spacing. */
    private static final double SEPARATOR_SIZE = 2.0;

    private final int           width;
    private final int           height;

    /** A drawing 'unit'. */
    private final double        unit;

    /** 2 units. */
    private final double        doubleUnit;

    /** Half a unit. */
    private final double        halfUnit;

    /** The height of the menu on top. */
    private final double        menuHeight;

    /** Insets on the top and bottom of the element. */
    private final double        inset;

    /** Where the controls drawing area starts. */
    private final double        controlsTop;

    private final int           maxParameterValue;


    /**
     * Constructor.
     *
     * @param width The full width of the drawing area
     * @param height The full height of the drawing area
     * @param maxParameterValue
     */
    public DefaultGraphicsDimensions (final int width, final int height, final int maxParameterValue)
    {
        this.width = width;
        this.height = height;

        this.unit = height / 12.0;
        this.doubleUnit = 2.0 * this.unit;
        this.halfUnit = this.unit / 2.0;
        this.menuHeight = this.unit + 2.0 * SEPARATOR_SIZE;
        this.inset = SEPARATOR_SIZE / 2.0 + this.halfUnit;
        this.controlsTop = this.menuHeight + this.inset;

        this.maxParameterValue = maxParameterValue;
    }


    /** {@inheritDoc} */
    @Override
    public int getWidth ()
    {
        return this.width;
    }


    /** {@inheritDoc} */
    @Override
    public int getHeight ()
    {
        return this.height;
    }


    /** {@inheritDoc} */
    @Override
    public double getSeparatorSize ()
    {
        return SEPARATOR_SIZE;
    }


    /** {@inheritDoc} */
    @Override
    public double getMenuHeight ()
    {
        return this.menuHeight;
    }


    /** {@inheritDoc} */
    @Override
    public double getUnit ()
    {
        return this.unit;
    }


    /** {@inheritDoc} */
    @Override
    public double getHalfUnit ()
    {
        return this.halfUnit;
    }


    /** {@inheritDoc} */
    @Override
    public double getDoubleUnit ()
    {
        return this.doubleUnit;
    }


    /** {@inheritDoc} */
    @Override
    public double getControlsTop ()
    {
        return this.controlsTop;
    }


    /** {@inheritDoc} */
    @Override
    public double getInset ()
    {
        return this.inset;
    }


    /** {@inheritDoc} */
    @Override
    public int getParameterUpperBound ()
    {
        return this.maxParameterValue;
    }
}
