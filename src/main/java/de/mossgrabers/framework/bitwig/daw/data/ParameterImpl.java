// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.bitwig.daw.data;

import de.mossgrabers.framework.daw.data.IParameter;

import com.bitwig.extension.controller.api.Parameter;


/**
 * Encapsulates the data of a parameter.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ParameterImpl implements IParameter
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
    public ParameterImpl (final Parameter parameter, final int maxParameterValue)
    {
        this.parameter = parameter;
        this.maxParameterValue = maxParameterValue;

        parameter.exists ().markInterested ();
        parameter.name ().markInterested ();
        parameter.displayedValue ().markInterested ();
        parameter.value ().addValueObserver (maxParameterValue, this::handleValue);
        parameter.modulatedValue ().addValueObserver (maxParameterValue, this::handleModulatedValue);
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        this.parameter.exists ().setIsSubscribed (enable);
        this.parameter.name ().setIsSubscribed (enable);
        this.parameter.displayedValue ().setIsSubscribed (enable);
        this.parameter.value ().setIsSubscribed (enable);
        this.parameter.modulatedValue ().setIsSubscribed (enable);
    }


    /** {@inheritDoc} */
    @Override
    public void inc (final double increment)
    {
        this.parameter.inc (Double.valueOf (increment), Integer.valueOf (this.maxParameterValue));
    }


    /** {@inheritDoc} */
    @Override
    public boolean doesExist ()
    {
        return this.parameter.exists ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public String getName ()
    {
        return this.parameter.name ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public String getName (final int limit)
    {
        return this.parameter.name ().getLimited (limit);
    }


    /** {@inheritDoc} */
    @Override
    public String getDisplayedValue ()
    {
        return this.parameter.displayedValue ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public String getDisplayedValue (final int limit)
    {
        return this.parameter.displayedValue ().getLimited (limit);
    }


    /** {@inheritDoc} */
    @Override
    public int getValue ()
    {
        return this.value;
    }


    /** {@inheritDoc} */
    @Override
    public void setValue (final double value)
    {
        this.parameter.set (Double.valueOf (value), Integer.valueOf (this.maxParameterValue));
    }


    /** {@inheritDoc} */
    @Override
    public int getModulatedValue ()
    {
        return this.modulatedValue;
    }


    /** {@inheritDoc} */
    @Override
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
