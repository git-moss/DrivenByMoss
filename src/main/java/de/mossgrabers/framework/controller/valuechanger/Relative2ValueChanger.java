// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller.valuechanger;

/**
 * The MCU value changer (@see RelativeEncoding.SIGNED_BIT).
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Relative2ValueChanger extends DefaultValueChanger
{
    /**
     * Constructor.
     *
     * @param upperBound The range of the parameter values (0 to upperBound - 1)
     * @param stepSize The value for de-/increasing the value by '1' without any scaling
     */
    public Relative2ValueChanger (final int upperBound, final int stepSize)
    {
        super (upperBound, stepSize);
    }


    /** {@inheritDoc} */
    @Override
    public int decode (final int control)
    {
        return control < 0x41 ? control : 0x40 - control;
    }


    /** {@inheritDoc} */
    @Override
    public int encode (final int speed)
    {
        return speed < 0 ? 0x40 + speed : speed;
    }
}
