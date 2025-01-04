// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller.valuechanger;

/**
 * Relative Signed Bit 2. If the most sign bit is not set, Then the offset is positive. The lower 6
 * significant bits are the offset. This is the same as 'Relative Signed Bit' but with the direction
 * of turn reversed. This is the method the Mackie Control Protocol uses. The offset value is formed
 * as 0svvvvvv. Where s is the sign or direction and vvvvvv is the number of ticks turned.
 *
 * @author Jürgen Moßgraber
 */
public class SignedBit2RelativeValueChanger extends TwosComplementValueChanger
{
    /**
     * Constructor.
     *
     * @param upperBound The range of the parameter values (0 to upperBound - 1)
     * @param stepSize The value for de-/increasing the value by '1' without any scaling
     */
    public SignedBit2RelativeValueChanger (final int upperBound, final int stepSize)
    {
        super (upperBound, stepSize);
    }


    /** {@inheritDoc} */
    @Override
    public int decode (final int control)
    {
        return control < 64 ? control : 64 - control;
    }


    /** {@inheritDoc} */
    @Override
    public int encode (final int speed)
    {
        return speed <= 0 ? 64 + Math.abs (speed) : speed;
    }
}
