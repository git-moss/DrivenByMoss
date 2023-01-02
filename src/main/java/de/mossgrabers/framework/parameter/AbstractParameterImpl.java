// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.parameter;

import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.data.AbstractItemImpl;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Abstract implementation for a parameter.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractParameterImpl extends AbstractItemImpl implements IParameter
{
    protected final IValueChanger valueChanger;


    /**
     * Constructor.
     *
     * @param valueChanger The value changer
     * @param index The index of the item in a page
     */
    protected AbstractParameterImpl (final IValueChanger valueChanger, final int index)
    {
        super (index);

        this.valueChanger = valueChanger;
    }


    /** {@inheritDoc} */
    @Override
    public int getPosition ()
    {
        return -1;
    }


    /** {@inheritDoc} */
    @Override
    public int getValue ()
    {
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public final void setValue (final int value)
    {
        this.setValue (this.valueChanger, value);
    }


    /** {@inheritDoc} */
    @Override
    public void setValue (final IValueChanger valueChanger, final int value)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void setNormalizedValue (final double value)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public final void changeValue (final int control)
    {
        this.changeValue (this.valueChanger, control);
    }


    /** {@inheritDoc} */
    @Override
    public void changeValue (final IValueChanger valueChanger, final int value)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void setValueImmediatly (final int value)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void inc (final double increment)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void resetValue ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void touchValue (final boolean isBeingTouched)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public int getModulatedValue ()
    {
        return this.getValue ();
    }


    /** {@inheritDoc} */
    @Override
    public String getDisplayedValue ()
    {
        return "";
    }


    /** {@inheritDoc} */
    @Override
    public String getDisplayedValue (final int limit)
    {
        return StringUtils.optimizeName (this.getDisplayedValue (), limit);
    }


    /** {@inheritDoc} */
    @Override
    public void setIndication (final boolean enable)
    {
        // Intentionally empty
    }
}
