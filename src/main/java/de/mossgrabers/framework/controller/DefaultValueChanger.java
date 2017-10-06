// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller;

/**
 * Default implementation for changing values.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DefaultValueChanger implements ValueChanger
{
    private final int    upperBound;
    private final int    fractionValue;
    private final double slowFractionValue;
    private boolean      isSlow;


    /**
     * Constructor.
     *
     * @param upperBound The range of the parameter values (0 to upperBound - 1)
     * @param fractionValue Amount by which values are incremented / decremented
     * @param slowFractionValue Amount by which values are slowly incremented / decremented
     */
    public DefaultValueChanger (final int upperBound, final int fractionValue, final double slowFractionValue)
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
    public int getFractionValue ()
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
    public int changeValue (final int control, final int value, final double fractionValue, final int upperBound)
    {
        return this.changeIntValue (control, value, fractionValue, upperBound);
    }


    /** {@inheritDoc} */
    @Override
    public int changeValue (final int control, final int value, final double fractionValue, final int upperBound, final int lowerBound)
    {
        return this.changeIntValue (control, value, fractionValue, upperBound, lowerBound);
    }


    /** {@inheritDoc} */
    @Override
    public int changeValue (final int control, final int value)
    {
        return this.changeValue (control, value, this.isSlow ? this.slowFractionValue : this.fractionValue, this.upperBound);
    }


    /** {@inheritDoc} */
    @Override
    public int changeIntValue (final int control, final int value, final double fractionValue, final int maxParameterValue)
    {
        return this.changeIntValue (control, value, fractionValue, maxParameterValue, 0);
    }


    /** {@inheritDoc} */
    @Override
    public int changeIntValue (final int control, final int value, final double fractionValue, final int maxParameterValue, final int minParameterValue)
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
}
