// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.parameter;

import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Encapsulates a parameter and calls its' reset method in all methods which modify the value.
 *
 * @author Jürgen Moßgraber
 */
public class ResetParameter extends AbstractParameterWrapper
{
    /**
     * Constructor.
     *
     * @param parameter The parameter to encapsulate
     */
    public ResetParameter (final IParameter parameter)
    {
        super (parameter);
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
    public String getName (final int limit)
    {
        return StringUtils.optimizeName (this.getName (), limit);
    }


    /** {@inheritDoc} */
    @Override
    public void inc (final double increment)
    {
        this.parameter.resetValue ();
    }
}
