// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw;

import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.observer.ItemSelectionObserver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * An abstract bank.
 *
 * @param <T> The specific item type of the bank item
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractBank<T extends IItem> implements IBank<T>
{
    protected final IHost                      host;
    protected final List<T>                    items;
    protected final Set<ItemSelectionObserver> observers = new HashSet<> ();
    protected int                              pageSize;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param pageSize The number of elements in a page of the bank
     */
    public AbstractBank (final IHost host, final int pageSize)
    {
        this.host = host;
        this.pageSize = pageSize;
        this.items = new ArrayList<> (this.pageSize);
    }


    /** {@inheritDoc} */
    @Override
    public void addSelectionObserver (final ItemSelectionObserver observer)
    {
        this.observers.add (observer);
    }


    /**
     * Notify all registered selection observers.
     *
     * @param itemIndex The index of the item which selection state has changed
     * @param isSelected True if selected otherwise false
     */
    protected void notifySelectionObservers (final int itemIndex, final boolean isSelected)
    {
        for (final ItemSelectionObserver observer: this.observers)
            observer.call (itemIndex, isSelected);
    }


    /** {@inheritDoc} */
    @Override
    public T getItem (final int index)
    {
        return this.items.get (index);
    }


    /** {@inheritDoc} */
    @Override
    public T getSelectedItem ()
    {
        for (int i = 0; i < this.getPageSize (); i++)
        {
            final T item = this.getItem (i);
            if (item.isSelected ())
                return item;
        }
        return null;
    }


    /** {@inheritDoc} */
    @Override
    public List<T> getSelectedItems ()
    {
        final List<T> selection = new ArrayList<> ();
        for (int i = 0; i < this.getPageSize (); i++)
        {
            final T item = this.getItem (i);
            if (item.isSelected ())
                selection.add (item);
        }
        return selection;
    }


    /** {@inheritDoc} */
    @Override
    public void selectItemAtPosition (final int position)
    {
        if (position < 0 || position >= this.getItemCount ())
            return;
        final int ps = this.getPageSize ();
        this.scrollTo (position / ps * ps);
        this.host.scheduleTask ( () -> this.getItem (position % ps).select (), 75);
    }


    /** {@inheritDoc} */
    @Override
    public int getPageSize ()
    {
        return this.pageSize;
    }


    /** {@inheritDoc} */
    @Override
    public int getScrollPosition ()
    {
        return this.getItem (0).getPosition ();
    }


    /** {@inheritDoc} */
    @Override
    public int getPositionOfLastItem ()
    {
        for (int i = this.getPageSize () - 1; i >= 0; i--)
        {
            final int pos = this.getItem (i).getPosition ();
            if (pos >= 0)
                return pos;
        }
        return -1;
    }


    /**
     * Initialise the internal items.
     */
    protected abstract void initItems ();


    /** {@inheritDoc} */
    @Override
    public void selectNextItem ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItem ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousPage ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextPage ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollBackwards ()
    {
        final IItem sel = this.getSelectedItem ();
        final int selIndex = sel != null ? sel.getIndex () : -1;
        return selIndex > 0 || this.canScrollPageBackwards ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollForwards ()
    {
        final IItem sel = this.getSelectedItem ();
        final int selIndex = sel != null ? sel.getIndex () : -1;
        return selIndex >= 0 && selIndex < this.pageSize - 1 && this.getItem (selIndex + 1).doesExist () || this.canScrollPageForwards ();
    }


    /** {@inheritDoc} */
    @Override
    public void setSkipDisabledItems (final boolean shouldSkip)
    {
        // Not used, overwrite to support
    }
}
