// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw;

import de.mossgrabers.framework.daw.data.IItem;


/**
 * A bank with nothing in it.
 *
 * @param <T> The specific item type of the bank item
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class EmptyBank<T extends IItem> extends AbstractBank<T>
{
    /**
     * Constructor.
     */
    public EmptyBank ()
    {
        super (null, 0);
    }


    /** {@inheritDoc} */
    @Override
    protected void initItems ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public int getItemCount ()
    {
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollPageBackwards ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollPageForwards ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void scrollBackwards ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void scrollForwards ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void scrollTo (final int position)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void scrollTo (final int position, final boolean adjustPage)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        // Intentionally empty
    }
}
