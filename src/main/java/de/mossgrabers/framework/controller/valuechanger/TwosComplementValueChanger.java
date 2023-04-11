// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller.valuechanger;

/**
 * Default implementation for changing values. The relative knob implementation is Two's
 * Complement.<br/>
 * [0..127] = [0..63,-64..-1] speed<br/>
 *
 * @see <a href=
 *      "https://en.wikipedia.org/wiki/Two%27s_complement">https://en.wikipedia.org/wiki/Two%27s_complement</a>
 *
 * @author Jürgen Moßgraber
 */
public class TwosComplementValueChanger implements IValueChanger
{
    private int    upperBound;
    protected int  stepSize;
    private double sensitivity = 1.0;


    /**
     * Constructor.
     *
     * @param upperBound The range of the parameter values (0 to upperBound - 1)
     * @param stepSize The value for de-/increasing the value by '1' without any scaling
     */
    public TwosComplementValueChanger (final int upperBound, final int stepSize)
    {
        this.upperBound = upperBound;
        this.stepSize = stepSize;
    }


    /** {@inheritDoc} */
    @Override
    public void setStepSize (final int stepSize)
    {
        this.stepSize = stepSize;
    }


    /** {@inheritDoc} */
    @Override
    public void setSensitivity (final double sensitivity)
    {
        this.sensitivity = sensitivity;
    }


    /** {@inheritDoc} */
    @Override
    public int getUpperBound ()
    {
        return this.upperBound;
    }


    /** {@inheritDoc} */
    @Override
    public void setUpperBound (final int upperBound)
    {
        this.upperBound = upperBound;
    }


    /** {@inheritDoc} */
    @Override
    public double calcKnobChange (final int control)
    {
        return this.calcKnobChange (control, this.sensitivity);
    }


    /** {@inheritDoc} */
    @Override
    public double calcKnobChange (final int control, final double sensitivity)
    {
        return this.decode (control) * this.stepSize * rescale (sensitivity);
    }


    /** {@inheritDoc} */
    @Override
    public int calcSteppedKnobChange (final int control)
    {
        final double result = this.calcKnobChange (control, -100);
        if (result >= 0)
            return (int) Math.max (1, Math.round (result));
        return (int) Math.min (-1, Math.round (result));
    }


    /** {@inheritDoc} */
    @Override
    public int decode (final int control)
    {
        return control < 64 ? control : control - 128;
    }


    /** {@inheritDoc} */
    @Override
    public int encode (final int speed)
    {
        return speed < 0 ? speed + 128 : speed;
    }


    /** {@inheritDoc} */
    @Override
    public int changeValue (final int control, final int value)
    {
        return this.changeValue (control, value, this.sensitivity, this.upperBound);
    }


    /** {@inheritDoc} */
    @Override
    public int changeValue (final int control, final int value, final double sensitivity, final int upperBound)
    {
        return this.changeValue (control, value, sensitivity, upperBound, 0);
    }


    private int changeValue (final int control, final int value, final double sensitivity, final int maxParameterValue, final int minParameterValue)
    {
        final double speed = this.calcKnobChange (control, sensitivity);
        return (int) Math.max (Math.min (value + speed, maxParameterValue - 1.0), minParameterValue);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isIncrease (final int control)
    {
        return this.calcKnobChange (control) > 0;
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
    public double toNormalizedValue (final double value)
    {
        return Math.min (value / (this.getUpperBound () - 1), 1.0);
    }


    /** {@inheritDoc} */
    @Override
    public int fromNormalizedValue (final double value)
    {
        return (int) Math.round (value * (this.getUpperBound () - 1));
    }


    /**
     * Set the sensitivity of the relative knob.
     *
     * @param sensitivity The sensitivity in the range [-100..100], 0 is the default, negative
     *            values are slower, positive faster
     * @return The sensitivity scaled to the range of [0.1, 10], the default value is 1
     */
    public static double rescale (final double sensitivity)
    {
        if (sensitivity < 0)
            return Math.max (0.1, (100.0 + sensitivity) / 100.0);
        return 1 + sensitivity / 100.0 * 9.0;
    }
}
