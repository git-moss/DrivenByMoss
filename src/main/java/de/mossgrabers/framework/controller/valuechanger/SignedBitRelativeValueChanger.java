// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller.valuechanger;

/**
 * E.g. used by the MCU jug wheel (@see RelativeEncoding.SIGNED_BIT).<br/>
 * [0..127] = [0..63,0,-1..-63] speed.
 *
 * @author J&uuml;rgen Mo&szlig;graber
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
        return control < 64 ? control : 64 - control;
    }


    /** {@inheritDoc} */
    @Override
    public int encode (final int speed)
    {
        return speed <= 0 ? 64 + Math.abs (speed) : speed;
    }
}
