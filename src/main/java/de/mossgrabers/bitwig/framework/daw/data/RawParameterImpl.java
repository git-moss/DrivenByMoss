// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw.data;

import de.mossgrabers.framework.controller.valuechanger.IValueChanger;

import com.bitwig.extension.controller.api.Parameter;


/**
 * Encapsulates the data of a parameter but uses the raw setters/getters.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class RawParameterImpl extends ParameterImpl
{
    private final int min;
    private final int max;

    private double    rawValue;


    /**
     * Constructor.
     *
     * @param valueChanger The value changer
     * @param parameter The parameter
     * @param min The minimum allowed value
     * @param max The maximum allowed value
     */
    public RawParameterImpl (final IValueChanger valueChanger, final Parameter parameter, final int min, final int max)
    {
        super (valueChanger, parameter);

        this.min = min;
        this.max = max;

        parameter.value ().addRawValueObserver (this::handleRawValue);
    }


    /**
     * Get the raw value.
     *
     * @return The raw value
     */
    public double getRawValue ()
    {
        return this.rawValue;
    }


    /**
     * Set the raw value.
     *
     * @param rawValue The raw value
     */
    public void setRawValue (final double rawValue)
    {
        this.getParameter ().setRaw (rawValue);
    }


    /**
     * Increase the raw value.
     *
     * @param offset The offset, positive or negative
     */
    public void incRawValue (final double offset)
    {
        this.getParameter ().incRaw (offset);
    }


    private void handleRawValue (final double value)
    {
        this.rawValue = Math.min (this.max, Math.max (this.min, value));
    }
}
