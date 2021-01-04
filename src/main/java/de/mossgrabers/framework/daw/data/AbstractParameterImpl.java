// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data;

import de.mossgrabers.framework.utils.StringUtils;


/**
 * Abstract implementation for a parameter.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractParameterImpl extends AbstractItemImpl implements IParameter
{
    /**
     * Constructor.
     *
     * @param index The index of the item in a page
     */
    public AbstractParameterImpl (final int index)
    {
        super (index);
    }


    /** {@inheritDoc} */
    @Override
    public String getDisplayedValue (final int limit)
    {
        return StringUtils.optimizeName (this.getDisplayedValue (), limit);
    }


    /** {@inheritDoc} */
    @Override
    public int getValue ()
    {
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public void setValue (final int value)
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
    public void changeValue (final int value)
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
    public void setIndication (final boolean enable)
    {
        // Intentionally empty
    }
}
