// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data.empty;

import de.mossgrabers.framework.daw.data.IParameter;


/**
 * Default data for an empty send.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class EmptyParameter extends EmptyItem implements IParameter
{
    /** The singleton. */
    public static final IParameter INSTANCE = new EmptyParameter ();


    /** {@inheritDoc} */
    @Override
    public void inc (double increment)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public String getDisplayedValue ()
    {
        return "";
    }


    /** {@inheritDoc} */
    @Override
    public String getDisplayedValue (int limit)
    {
        return "";
    }


    /** {@inheritDoc} */
    @Override
    public int getValue ()
    {
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public void setValue (int value)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void changeValue (int value)
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
    public void touchValue (boolean isBeingTouched)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public int getModulatedValue ()
    {
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public void setIndication (boolean enable)
    {
        // Intentionally empty
    }
}
