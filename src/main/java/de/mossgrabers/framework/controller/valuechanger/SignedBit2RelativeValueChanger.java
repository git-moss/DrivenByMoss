// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller.valuechanger;

/**
 * E.g. used by HUI.<br/>
 * [1..63] = [-1..-63]<br/>
 * [64] = [0]<br/>
 * [65..127] = [1..63]
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
