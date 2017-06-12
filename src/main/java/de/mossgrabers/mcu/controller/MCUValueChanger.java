package de.mossgrabers.mcu.controller;

import de.mossgrabers.framework.controller.DefaultValueChanger;


/**
 * The MCU value changer.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MCUValueChanger extends DefaultValueChanger
{
    /**
     * Constructor.
     *
     * @param upperBound The range of the parameter values (0 to upperBound - 1)
     * @param fractionValue Amount by which values are incremented / decremented
     * @param slowFractionValue Amount by which values are slowly incremented / decremented
     */
    public MCUValueChanger (final int upperBound, final int fractionValue, final double slowFractionValue)
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
