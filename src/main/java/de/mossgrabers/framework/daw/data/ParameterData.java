// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data;

import com.bitwig.extension.controller.api.Parameter;


/**
 * Encapsulates the data of a parameter.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ParameterData
{
    private final Parameter parameter;
    private final int       maxParameterValue;

    private int             value;
    private int             modulatedValue;


    /**
     * Constructor.
     *
     * @param parameter The parameter
     * @param maxParameterValue The maximum number for values (range is 0 till maxParameterValue-1)
     */
    public ParameterData (final Parameter parameter, final int maxParameterValue)
    {
        this.parameter = parameter;
        this.maxParameterValue = maxParameterValue;

        parameter.exists ().markInterested ();
        parameter.name ().markInterested ();
        parameter.displayedValue ().markInterested ();
        parameter.value ().addValueObserver (maxParameterValue, value -> this.handleValue (value));
        parameter.modulatedValue ().addValueObserver (maxParameterValue, value -> this.handleModulatedValue (value));
    }


    /**
     * Dis-/Enable all attributes. They are enabled by default. Use this function if values are
     * currently not needed to improve performance.
     *
     * @param enable True to enable
     */
    public void enableObservers (final boolean enable)
    {
        this.parameter.exists ().setIsSubscribed (enable);
        this.parameter.name ().setIsSubscribed (enable);
        this.parameter.displayedValue ().setIsSubscribed (enable);
        this.parameter.value ().setIsSubscribed (enable);
        this.parameter.modulatedValue ().setIsSubscribed (enable);
    }


    /**
     * De-/Increase the value by the given amount. Scaled by the range given in the constructor.
     *
     * @param increment The amount of the increment (negative to decrease)
     */
    public void inc (final double increment)
    {
        this.parameter.inc (Double.valueOf (increment), Integer.valueOf (this.maxParameterValue));
    }


    /**
     * True if the parameter does exist.
     *
     * @return True if the parameter does exist.
     */
    public boolean doesExist ()
    {
        return this.parameter.exists ().get ();
    }


    /**
     * Get the name of the parameter.
     *
     * @return The name
     */
    public String getName ()
    {
        return this.parameter.name ().get ();
    }


    /**
     * Get the name of the parameter.
     *
     * @param limit Limit the text to this length
     * @return The name
     */
    public String getName (final int limit)
    {
        return this.parameter.name ().getLimited (limit);
    }


    /**
     * Get the value formatted for display.
     *
     * @return The formatted value
     */
    public String getDisplayedValue ()
    {
        return this.parameter.displayedValue ().get ();
    }


    /**
     * Get the value formatted for display.
     *
     * @param limit Limit the text to this length
     * @return The formatted value
     */
    public String getDisplayedValue (final int limit)
    {
        return this.parameter.displayedValue ().getLimited (limit);
    }


    /**
     * Get the value.
     *
     * @return The value
     */
    public int getValue ()
    {
        return this.value;
    }


    /**
     * Sets the value.
     *
     * @param value The new value to set
     */
    public void setValue (final double value)
    {
        this.parameter.set (Double.valueOf (value), Integer.valueOf (this.maxParameterValue));
    }


    /**
     * Get the value which is modulated. If it is currently not modulated it is identical to
     * getValue.
     *
     * @return The modulated value
     */
    public int getModulatedValue ()
    {
        return this.modulatedValue;
    }


    /**
     * Specifies if this value should be indicated as mapped in Bitwig Studio, which is visually
     * shown as colored dots or tinting on the parameter controls.
     *
     * @param enable Ttrue in case visual indications should be shown in Bitwig Studio
     */
    public void setIndication (final boolean enable)
    {
        this.parameter.setIndication (enable);
    }


    private void handleValue (final int value)
    {
        this.value = value;
    }


    private void handleModulatedValue (final int modulatedValue)
    {
        this.modulatedValue = modulatedValue;
    }
}
