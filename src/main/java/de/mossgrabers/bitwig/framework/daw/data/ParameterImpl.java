// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw.data;

import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.data.AbstractItemImpl;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.observer.IValueObserver;

import com.bitwig.extension.controller.api.Parameter;


/**
 * Encapsulates the data of a parameter.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ParameterImpl extends AbstractItemImpl implements IParameter
{
    private final IValueChanger valueChanger;
    private final Parameter     parameter;


    /**
     * Constructor.
     *
     * @param valueChanger The value changer
     * @param parameter The parameter
     * @param index The index of the item in the page
     */
    public ParameterImpl (final IValueChanger valueChanger, final Parameter parameter, final int index)
    {
        super (index);

        this.valueChanger = valueChanger;
        this.parameter = parameter;

        parameter.exists ().markInterested ();
        parameter.name ().markInterested ();
        parameter.displayedValue ().markInterested ();
        parameter.value ().markInterested ();
        parameter.modulatedValue ().markInterested ();
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        Util.setIsSubscribed (this.parameter.exists (), enable);
        Util.setIsSubscribed (this.parameter.name (), enable);
        Util.setIsSubscribed (this.parameter.displayedValue (), enable);
        Util.setIsSubscribed (this.parameter.value (), enable);
        Util.setIsSubscribed (this.parameter.modulatedValue (), enable);
    }


    /** {@inheritDoc} */
    @Override
    public void inc (final double increment)
    {
        this.parameter.inc (Double.valueOf (increment), Integer.valueOf (this.valueChanger.getUpperBound ()));
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
    public void addNameObserver (final IValueObserver<String> observer)
    {
        this.parameter.name ().addValueObserver (observer::update);
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
        return this.valueChanger.fromNormalizedValue (this.parameter.value ().get ());
    }


    /** {@inheritDoc} */
    @Override
    public void setValue (final int value)
    {
        this.parameter.set (Integer.valueOf (value), Integer.valueOf (this.valueChanger.getUpperBound ()));
    }


    /** {@inheritDoc} */
    @Override
    public void setValueImmediatly (final int value)
    {
        this.parameter.setImmediately (this.valueChanger.toNormalizedValue (value));
    }


    /** {@inheritDoc} */
    @Override
    public void changeValue (final int value)
    {
        this.inc (this.valueChanger.calcKnobSpeed (value));
    }


    /** {@inheritDoc} */
    @Override
    public int getModulatedValue ()
    {
        return this.valueChanger.fromNormalizedValue (this.parameter.modulatedValue ().get ());
    }


    /** {@inheritDoc} */
    @Override
    public void setIndication (final boolean enable)
    {
        this.parameter.setIndication (enable);
    }


    /** {@inheritDoc} */
    @Override
    public void resetValue ()
    {
        this.parameter.reset ();
    }


    /** {@inheritDoc} */
    @Override
    public void touchValue (final boolean isBeingTouched)
    {
        this.parameter.touch (isBeingTouched);
    }


    /** {@inheritDoc} */
    @Override
    public void select ()
    {
        // Parameters cannot be selected but should also not crash
    }


    /**
     * Get the Bitwig parameter.
     *
     * @return The parameter
     */
    public Parameter getParameter ()
    {
        return this.parameter;
    }
}
