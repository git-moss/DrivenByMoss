// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller.valuechanger;

/**
 * The HUI value changer.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Relative4ValueChanger extends DefaultValueChanger
{
    /**
     * Constructor.
     *
     * @param upperBound The range of the parameter values (0 to upperBound - 1)
     * @param stepSize The value for de-/increasing the value by '1' without any scaling
     */
    public Relative4ValueChanger (final int upperBound, final int stepSize)
    {
        super (upperBound, stepSize);
    }


    /** {@inheritDoc} */
    @Override
    public int decode (final int control)
    {
        return control > 0x40 ? control - 0x40 : -control;
    }


    /** {@inheritDoc} */
    @Override
    public int encode (final int speed)
    {
        return speed < 0 ? -speed : speed + 0x40;
    }
}
