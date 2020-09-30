// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw.data;

import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.data.AbstractItemImpl;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.observer.IValueObserver;

import com.bitwig.extension.controller.api.DoubleValue;
import com.bitwig.extension.controller.api.Parameter;
import com.bitwig.extension.controller.api.StringValue;


/**
 * Encapsulates the data of a parameter.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ParameterImpl extends AbstractItemImpl implements IParameter
{
    private final IValueChanger valueChanger;
    private final Parameter     parameter;
    private StringValue         targetName;
    private StringValue         targetDisplayedValue;
    private DoubleValue         targetValue;
    private DoubleValue         targetModulatedValue;


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
        if (this.targetName != null)
            return !this.targetName.get ().isEmpty ();
        return this.parameter.exists ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public String getName ()
    {
        // TODO Bugfix required: The ISend check is a workaround for
        // https://github.com/teotigraphix/Framework4Bitwig/issues/267
        return this.targetName == null || this instanceof ISend ? this.parameter.name ().get () : this.targetName.get ();
    }


    /** {@inheritDoc} */
    @Override
    public String getName (final int limit)
    {
        // TODO Bugfix required: The ISend check is a workaround for
        // https://github.com/teotigraphix/Framework4Bitwig/issues/267
        return this.targetName == null || this instanceof ISend ? this.parameter.name ().getLimited (limit) : this.targetName.getLimited (limit);
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
        return this.targetDisplayedValue == null ? this.parameter.displayedValue ().get () : this.targetDisplayedValue.get ();
    }


    /** {@inheritDoc} */
    @Override
    public String getDisplayedValue (final int limit)
    {
        return this.targetDisplayedValue == null ? this.parameter.displayedValue ().getLimited (limit) : this.targetDisplayedValue.getLimited (limit);
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
        this.inc (this.valueChanger.calcKnobChange (value));
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


    /**
     * Workaround for new hardware API to still be able to receive user mode values via the old
     * interface.
     *
     * @param targetName The name of the parameter
     * @param targetDisplayedValue The formatted value for displaying it
     * @param targetValue The value of the parameter
     * @param targetModulatedValue The modulated value of the parameter
     */
    public void setTargetInfo (final StringValue targetName, final StringValue targetDisplayedValue, final DoubleValue targetValue, final DoubleValue targetModulatedValue)
    {
        this.targetName = targetName;
        this.targetDisplayedValue = targetDisplayedValue;
        this.targetValue = targetValue;
        this.targetModulatedValue = targetModulatedValue;
    }
}
