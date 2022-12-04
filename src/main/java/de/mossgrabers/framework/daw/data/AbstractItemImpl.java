// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data;

import de.mossgrabers.framework.observer.IValueObserver;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * An item.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractItemImpl implements IItem
{
    protected final int index;
    private boolean     selected = false;


    /**
     * Constructor.
     */
    protected AbstractItemImpl ()
    {
        this (-1);
    }


    /**
     * Constructor.
     *
     * @param index The index of the item in a page
     */
    protected AbstractItemImpl (final int index)
    {
        this.index = index;
    }


    /** {@inheritDoc} */
    @Override
    public boolean doesExist ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public int getIndex ()
    {
        return this.index;
    }


    /** {@inheritDoc} */
    @Override
    public int getPosition ()
    {
        throw new UnsupportedOperationException ();
    }


    /** {@inheritDoc} */
    @Override
    public String getName ()
    {
        return "";
    }


    /** {@inheritDoc} */
    @Override
    public String getName (final int limit)
    {
        return StringUtils.optimizeName (this.getName (), limit);
    }


    /** {@inheritDoc} */
    @Override
    public void setName (final String name)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public boolean isSelected ()
    {
        return this.doesExist () && this.selected;
    }


    /** {@inheritDoc} */
    @Override
    public void setSelected (final boolean isSelected)
    {
        this.selected = isSelected;
    }


    /** {@inheritDoc} */
    @Override
    public void select ()
    {
        // Cannot be selected but should also not crash
    }


    /** {@inheritDoc} */
    @Override
    public void toggleMultiSelect ()
    {
        // Cannot be selected but should also not crash
    }


    /** {@inheritDoc} */
    @Override
    public void addNameObserver (final IValueObserver<String> observer)
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
