// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller.valuechanger;

/**
 * Relative Signed Bit. If the most sign bit is set, Then the offset is positive. The lower 6
 * significant bits are the offset. The offset value is formed as 0svvvvvv. Where s is the sign or
 * direction and vvvvvv is the number of ticks turned. E.g. used by Mackie HUI.
 *
 * @author Jürgen Moßgraber
 */
public class SignedBitRelativeValueChanger extends TwosComplementValueChanger
{
    /**
     * Constructor.
     *
     * @param upperBound The range of the parameter values (0 to upperBound - 1)
     * @param stepSize The value for de-/increasing the value by '1' without any scaling
     */
    public SignedBitRelativeValueChanger (final int upperBound, final int stepSize)
    {
        super (upperBound, stepSize);
    }


    /** {@inheritDoc} */
    @Override
    public int decode (final int control)
    {
        if (control == 64)
            return 0;
        return control > 64 ? control - 64 : -control;
    }


    /** {@inheritDoc} */
    @Override
    public int encode (final int speed)
    {
        return speed < 0 ? -speed : speed + 64;
    }
}
