// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller;

/**
 * Default implementation for changing values.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DefaultValueChanger implements IValueChanger
{
    private final int    upperBound;
    private final double fractionValue;
    private final double slowFractionValue;
    private boolean      isSlow;


    /**
     * Constructor.
     *
     * @param upperBound The range of the parameter values (0 to upperBound - 1)
     * @param fractionValue Amount by which values are incremented / decremented
     * @param slowFractionValue Amount by which values are slowly incremented / decremented
     */
    public DefaultValueChanger (final int upperBound, final double fractionValue, final double slowFractionValue)
    {
        this.upperBound = upperBound;
        this.fractionValue = fractionValue;
        this.slowFractionValue = slowFractionValue;
    }


    /** {@inheritDoc} */
    @Override
    public int getUpperBound ()
    {
        return this.upperBound;
    }


    /** {@inheritDoc} */
    @Override
    public double getFractionValue ()
    {
        return this.fractionValue;
    }


    /** {@inheritDoc} */
    @Override
    public double getSlowFractionValue ()
    {
        return this.slowFractionValue;
    }


    /** {@inheritDoc} */
    @Override
    public void setSpeed (final boolean isSlow)
    {
        this.isSlow = isSlow;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isSlow ()
    {
        return this.isSlow;
    }


    /** {@inheritDoc} */
    @Override
    public double calcKnobSpeed (final int control)
    {
        return this.calcKnobSpeed (control, this.isSlow ? this.slowFractionValue : this.fractionValue);
    }


    /** {@inheritDoc} */
    @Override
    public double calcKnobSpeed (final int control, final double fractionValue)
    {
        return (control <= 61 ? control : control - 128) * fractionValue;
    }


    /** {@inheritDoc} */
    @Override
    public int changeValue (final int control, final int value)
    {
        return this.changeValue (control, value, this.isSlow ? this.slowFractionValue : this.fractionValue, this.upperBound);
    }


    /** {@inheritDoc} */
    @Override
    public int changeValue (final int control, final int value, final double fractionValue, final int upperBound)
    {
        return this.changeValue (control, value, fractionValue, upperBound, 0);
    }


    /** {@inheritDoc} */
    @Override
    public int changeValue (final int control, final int value, final double fractionValue, final int maxParameterValue, final int minParameterValue)
    {
        final double speed = this.calcKnobSpeed (control, fractionValue);
        return (int) Math.max (Math.min (value + speed, maxParameterValue - 1.0), minParameterValue);
    }


    /** {@inheritDoc} */
    @Override
    public int toMidiValue (final int value)
    {
        return Math.min (value * 127 / (this.getUpperBound () - 1), 127);
    }


    /** {@inheritDoc} */
    @Override
    public int toDAWValue (final int value)
    {
        return value * (this.getUpperBound () - 1) / 127;
    }


    /** {@inheritDoc} */
    @Override
    public int toDisplayValue (final int value)
    {
        // No conversion since the default value range of the script is the same as of the display
        // application (1024)
        return value;
    }


    /** {@inheritDoc} */
    @Override
    public double toNormalizedValue (final int value)
    {
        return Math.min ((double) value / (this.getUpperBound () - 1), 1.0);
    }


    /** {@inheritDoc} */
    @Override
    public int fromNormalizedValue (final double value)
    {
        return (int) Math.round (value * (this.getUpperBound () - 1));
    }
}
