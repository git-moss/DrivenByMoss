// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.parameter;

import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.observer.IValueObserver;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Encapsulates a parameter and calls its' reset method in changeValue and setValue.
 *
 * @author Jürgen Moßgraber
 */
public class ResetParameter implements IParameter
{
    private final IParameter parameter;


    /**
     * Constructor.
     *
     * @param parameter The parameter to encapsulate
     */
    public ResetParameter (final IParameter parameter)
    {
        this.parameter = parameter;
    }


    /** {@inheritDoc} */
    @Override
    public void setValue (final int value)
    {
        this.parameter.resetValue ();
    }


    /** {@inheritDoc} */
    @Override
    public void setValue (final IValueChanger valueChanger, final int value)
    {
        this.parameter.resetValue ();
    }


    /** {@inheritDoc} */
    @Override
    public int getValue ()
    {
        return this.parameter.getValue ();
    }


    /** {@inheritDoc} */
    @Override
    public void setNormalizedValue (final double value)
    {
        this.parameter.resetValue ();
    }


    /** {@inheritDoc} */
    @Override
    public void setValueImmediatly (final int value)
    {
        this.parameter.resetValue ();
    }


    /** {@inheritDoc} */
    @Override
    public void changeValue (final int control)
    {
        this.parameter.resetValue ();
    }


    /** {@inheritDoc} */
    @Override
    public void changeValue (final IValueChanger valueChanger, final int control)
    {
        this.parameter.resetValue ();
    }


    /** {@inheritDoc} */
    @Override
    public void resetValue ()
    {
        this.parameter.resetValue ();
    }


    /** {@inheritDoc} */
    @Override
    public String getName ()
    {
        return "Reset " + this.parameter.getName ();
    }


    /** {@inheritDoc} */
    @Override
    public void setName (final String name)
    {
        this.parameter.setName (name);
    }


    /** {@inheritDoc} */
    @Override
    public boolean doesExist ()
    {
        return this.parameter.doesExist ();
    }


    /** {@inheritDoc} */
    @Override
    public int getIndex ()
    {
        return this.parameter.getIndex ();
    }


    /** {@inheritDoc} */
    @Override
    public int getPosition ()
    {
        return this.parameter.getPosition ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isSelected ()
    {
        return this.parameter.isSelected ();
    }


    /** {@inheritDoc} */
    @Override
    public void setSelected (final boolean isSelected)
    {
        this.parameter.setSelected (isSelected);
    }


    /** {@inheritDoc} */
    @Override
    public void select ()
    {
        this.parameter.select ();
    }


    /** {@inheritDoc} */
    @Override
    public void toggleMultiSelect ()
    {
        this.parameter.toggleMultiSelect ();
    }


    /** {@inheritDoc} */
    @Override
    public String getName (final int limit)
    {
        return StringUtils.optimizeName (this.getName (), limit);
    }


    /** {@inheritDoc} */
    @Override
    public void addNameObserver (final IValueObserver<String> observer)
    {
        this.parameter.addNameObserver (observer);
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        this.parameter.enableObservers (enable);
    }


    /** {@inheritDoc} */
    @Override
    public void inc (final double increment)
    {
        this.parameter.resetValue ();
    }


    /** {@inheritDoc} */
    @Override
    public String getDisplayedValue ()
    {
        return this.parameter.getDisplayedValue ();
    }


    /** {@inheritDoc} */
    @Override
    public String getDisplayedValue (final int limit)
    {
        return this.parameter.getDisplayedValue (limit);
    }


    /** {@inheritDoc} */
    @Override
    public void touchValue (final boolean isBeingTouched)
    {
        this.parameter.touchValue (isBeingTouched);
    }


    /** {@inheritDoc} */
    @Override
    public int getModulatedValue ()
    {
        return this.parameter.getModulatedValue ();
    }


    /** {@inheritDoc} */
    @Override
    public void setIndication (final boolean enable)
    {
        this.parameter.setIndication (enable);
    }
}
