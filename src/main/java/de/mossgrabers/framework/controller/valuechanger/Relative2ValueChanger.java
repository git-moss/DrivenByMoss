// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
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
     * @param fractionValue Amount by which values are incremented / decremented
     * @param slowFractionValue Amount by which values are slowly incremented / decremented
     */
    public Relative2ValueChanger (final int upperBound, final int fractionValue, final double slowFractionValue)
    {
        super (upperBound, fractionValue, slowFractionValue);
    }


    /** {@inheritDoc} */
    @Override
    public double calcKnobSpeed (final int control, final double fractionValue)
    {
        return (control < 0x41 ? control : 0x40 - control) * fractionValue;
    }
}
