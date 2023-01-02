// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw.data;

import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.parameter.AbstractParameterImpl;

import com.bitwig.extension.controller.api.DoubleValue;
import com.bitwig.extension.controller.api.SettableRangedValue;
import com.bitwig.extension.controller.api.StringValue;


/**
 * Encapsulates the data of a ranged value.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class RangedValueImpl extends AbstractParameterImpl
{
    protected final SettableRangedValue rangedValue;
    protected final String              name;

    protected StringValue               targetName;
    protected StringValue               targetDisplayedValue;
    protected DoubleValue               targetValue;
    protected DoubleValue               targetModulatedValue;


    /**
     * Constructor.
     *
     * @param name The name of the value
     * @param valueChanger The value changer
     * @param rangedValue The ranged value
     */
    public RangedValueImpl (final String name, final IValueChanger valueChanger, final SettableRangedValue rangedValue)
    {
        this (name, valueChanger, rangedValue, 0);
    }


    /**
     * Constructor.
     *
     * @param name The name of the value
     * @param valueChanger The value changer
     * @param rangedValue The ranged value
     * @param index The index of the item in a page
     */
    public RangedValueImpl (final String name, final IValueChanger valueChanger, final SettableRangedValue rangedValue, final int index)
    {
        super (valueChanger, index);

        this.name = name;

        this.rangedValue = rangedValue;
        this.rangedValue.markInterested ();
        this.rangedValue.displayedValue ().markInterested ();
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        Util.setIsSubscribed (this.rangedValue, enable);
        Util.setIsSubscribed (this.rangedValue.displayedValue (), enable);
    }


    /** {@inheritDoc} */
    @Override
    public boolean doesExist ()
    {
        return true;
    }


    /** {@inheritDoc} */
    @Override
    public void inc (final double increment)
    {
        this.rangedValue.inc (Double.valueOf (increment), Integer.valueOf (this.valueChanger.getUpperBound ()));
    }


    /** {@inheritDoc} */
    @Override
    public String getName ()
    {
        return this.targetName == null ? this.name : this.targetName.get ();
    }


    /** {@inheritDoc} */
    @Override
    public String getDisplayedValue ()
    {
        return this.targetDisplayedValue == null ? this.rangedValue.displayedValue ().get () : this.targetDisplayedValue.get ();
    }


    /** {@inheritDoc} */
    @Override
    public String getDisplayedValue (final int limit)
    {
        return this.targetDisplayedValue == null ? this.rangedValue.displayedValue ().getLimited (limit) : this.targetDisplayedValue.getLimited (limit);
    }


    /** {@inheritDoc} */
    @Override
    public int getValue ()
    {
        final double value = this.targetValue == null ? this.rangedValue.get () : this.targetValue.get ();
        return this.valueChanger.fromNormalizedValue (value);
    }


    /** {@inheritDoc} */
    @Override
    public void setValue (final IValueChanger valueChanger, final int value)
    {
        this.rangedValue.set (Integer.valueOf (value), Integer.valueOf (valueChanger.getUpperBound ()));
    }


    /** {@inheritDoc} */
    @Override
    public void setNormalizedValue (final double value)
    {
        this.setValue (this.valueChanger.fromNormalizedValue (value));
    }


    /** {@inheritDoc} */
    @Override
    public void setValueImmediatly (final int value)
    {
        this.rangedValue.setImmediately (this.valueChanger.toNormalizedValue (value));
    }


    /** {@inheritDoc} */
    @Override
    public void changeValue (final IValueChanger valueChanger, final int value)
    {
        this.inc (valueChanger.calcKnobChange (value));
    }


    /** {@inheritDoc} */
    @Override
    public void resetValue ()
    {
        this.setValue (0);
    }


    /**
     * Workaround for new hardware API to still be able to receive values via the old interface.
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
