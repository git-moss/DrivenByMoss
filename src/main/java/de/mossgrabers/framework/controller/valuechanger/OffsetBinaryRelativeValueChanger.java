// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller.valuechanger;

/**
 * Implementation for relative values encoded in Offset Binary (e.g. used by Beatstep).<br/>
 * [0..127] = [-64..63] speed.
 *
 * @author Jürgen Moßgraber
 */
public class OffsetBinaryRelativeValueChanger extends TwosComplementValueChanger
{
    /**
     * Constructor.
     *
     * @param upperBound The range of the parameter values (0 to upperBound - 1)
     * @param stepSize The value for de-/increasing the value by '1' without any scaling
     */
    public OffsetBinaryRelativeValueChanger (final int upperBound, final int stepSize)
    {
        super (upperBound, stepSize);
    }


    /** {@inheritDoc} */
    @Override
    public int decode (final int control)
    {
        return control - 64;
    }


    /** {@inheritDoc} */
    @Override
    public int encode (final int speed)
    {
        return speed + 64;
    }
}
