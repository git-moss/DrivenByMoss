// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data.bank;

import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.observer.IBankPageObserver;
import de.mossgrabers.framework.observer.IItemSelectionObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;


/**
 * An abstract bank.
 *
 * @param <T> The specific item type of the bank content
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractBank<T> implements IBank<T>
{
    protected final IHost                     host;
    protected final List<T>                   items;
    protected final int                       pageSize;

    private final Set<IItemSelectionObserver> selectionObservers = new CopyOnWriteArraySet<> ();
    private final Set<IBankPageObserver>      pageObservers      = new CopyOnWriteArraySet<> ();


    /**
     * Constructor. The size of the elements to store/cache is identical to the page size.
     *
     * @param host The DAW host
     * @param pageSize The number of elements in a page of the bank
     */
    protected AbstractBank (final IHost host, final int pageSize)
    {
        this (host, pageSize, new ArrayList<> (pageSize));
    }


    /**
     * Constructor. The size of the elements to store/cache is identical to the page size. Use for
     * pre-configured lists.
     *
     * @param host The DAW host
     * @param pageSize The number of elements in a page of the bank
     * @param items The bank items
     */
    protected AbstractBank (final IHost host, final int pageSize, final List<T> items)
    {
        this.host = host;
        this.pageSize = pageSize;
        this.items = items;
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasExistingItems ()
    {
        return true;
    }


    /** {@inheritDoc} */
    @Override
    public T getItem (final int index)
    {
        return this.items.get (index);
    }


    /** {@inheritDoc} */
    @Override
    public int getItemCount ()
    {
        return this.items.size ();
    }


    /** {@inheritDoc} */
    @Override
    public int getPageSize ()
    {
        return this.pageSize;
    }


    /** {@inheritDoc} */
    @Override
    public void addSelectionObserver (final IItemSelectionObserver observer)
    {
        this.selectionObservers.add (observer);
    }


    /** {@inheritDoc} */
    @Override
    public void removeSelectionObserver (final IItemSelectionObserver observer)
    {
        this.selectionObservers.remove (observer);
    }


    /** {@inheritDoc} */
    @Override
    public void addPageObserver (final IBankPageObserver observer)
    {
        this.pageObservers.add (observer);
    }


    /** {@inheritDoc} */
    @Override
    public void removePageObserver (final IBankPageObserver observer)
    {
        this.pageObservers.remove (observer);
    }


    /**
     * Notify all registered selection observers.
     *
     * @param itemIndex The index of the item which selection state has changed
     * @param isSelected True if selected otherwise false
     */
    protected void notifySelectionObservers (final int itemIndex, final boolean isSelected)
    {
        // Delay notifications to allow cursor objects to update
        this.host.scheduleTask ( () -> {
            for (final IItemSelectionObserver observer: this.selectionObservers)
                observer.call (itemIndex, isSelected);
        }, 100);
    }


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
    public void setSkipDisabledItems (final boolean shouldSkip)
    {
        // Not used, overwrite to support
    }


    /**
     * Notify all parameters adjusted observers.
     */
    public void firePageObserver ()
    {
        this.pageObservers.forEach (IBankPageObserver::pageAdjusted);
    }
}
