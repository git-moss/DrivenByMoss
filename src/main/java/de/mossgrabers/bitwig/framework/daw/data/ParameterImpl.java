// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw.data;

import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.observer.IValueObserver;

import com.bitwig.extension.controller.api.Parameter;


/**
 * Encapsulates the data of a parameter.
 *
 * @author Jürgen Moßgraber
 */
public class ParameterImpl extends RangedValueImpl
{
    private final Parameter parameter;
    private final boolean   fixNames;


    /**
     * Constructor.
     *
     * @param valueChanger The value changer
     * @param parameter The parameter
     */
    public ParameterImpl (final IValueChanger valueChanger, final Parameter parameter)
    {
        this (valueChanger, parameter, 0);
    }


    /**
     * Constructor.
     *
     * @param valueChanger The value changer
     * @param parameter The parameter
     * @param index The index of the item in the page
     */
    public ParameterImpl (final IValueChanger valueChanger, final Parameter parameter, final int index)
    {
        this (valueChanger, parameter, index, false);
    }


    /**
     * Constructor.
     *
     * @param valueChanger The value changer
     * @param parameter The parameter
     * @param index The index of the item in the page
     * @param fixNames Don't use targetName if true
     */
    public ParameterImpl (final IValueChanger valueChanger, final Parameter parameter, final int index, final boolean fixNames)
    {
        super (null, valueChanger, parameter, index);

        this.parameter = parameter;

        // TODO Bugfix required: https://github.com/teotigraphix/Framework4Bitwig/issues/268
        this.fixNames = fixNames;

        parameter.exists ().markInterested ();
        parameter.name ().markInterested ();
        parameter.value ().markInterested ();
        parameter.modulatedValue ().markInterested ();
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        super.enableObservers (enable);

        Util.setIsSubscribed (this.parameter.exists (), enable);
        Util.setIsSubscribed (this.parameter.name (), enable);
        Util.setIsSubscribed (this.parameter.value (), enable);
        Util.setIsSubscribed (this.parameter.modulatedValue (), enable);
    }


    /** {@inheritDoc} */
    @Override
    public boolean doesExist ()
    {
        if (this.targetName != null)
            return !this.targetName.get ().isEmpty ();
        return this.parameter.exists ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public String getName ()
    {
        return this.targetName == null || this.fixNames ? this.parameter.name ().get () : this.targetName.get ();
    }


    /** {@inheritDoc} */
    @Override
    public String getName (final int limit)
    {
        return this.targetName == null || this.fixNames ? this.parameter.name ().getLimited (limit) : this.targetName.getLimited (limit);
    }


    /** {@inheritDoc} */
    @Override
    public void addNameObserver (final IValueObserver<String> observer)
    {
        this.parameter.name ().addValueObserver (observer::update);
    }


    /** {@inheritDoc} */
    @Override
    public int getValue ()
    {
        final double value = this.targetValue == null ? this.parameter.value ().get () : this.targetValue.get ();
        return this.valueChanger.fromNormalizedValue (value);
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
    public int getModulatedValue ()
    {
        final double value = this.targetModulatedValue == null ? this.parameter.modulatedValue ().get () : this.targetModulatedValue.get ();
        return this.valueChanger.fromNormalizedValue (value);
    }


    /** {@inheritDoc} */
    @Override
    public void setIndication (final boolean enable)
    {
        this.parameter.setIndication (enable);
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
