// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.mkii.controller;

import de.mossgrabers.framework.controller.DefaultValueChanger;


/**
 * Default implementation for changing values with slowed down relative values.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SlowValueChanger extends DefaultValueChanger
{
    /**
     * Constructor.
     *
     * @param upperBound The range of the parameter values (0 to upperBound - 1)
     * @param fractionValue Amount by which values are incremented / decremented
     * @param slowFractionValue Amount by which values are slowly incremented / decremented
     */
    public SlowValueChanger (final int upperBound, final double fractionValue, final double slowFractionValue)
    {
        super (upperBound, fractionValue, slowFractionValue);
    }


    /** {@inheritDoc} */
    @Override
    public double calcKnobSpeed (final int control, final double fractionValue)
    {
        double val = (control <= 61 ? control : control - 128) / 3.0;
        if (val > -1 && val < 0)
            val = -1;
        else if (val > 0 && val < 1)
            val = 1;
        return val * fractionValue;
    }
}
