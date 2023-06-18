// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data.bank;

import de.mossgrabers.framework.observer.IBankPageObserver;
import de.mossgrabers.framework.observer.IItemSelectionObserver;
import de.mossgrabers.framework.observer.IObserverManagement;

import java.util.List;
import java.util.Optional;


/**
 * Interface to a bank. A bank provides a view to a number of items split into pages. A page
 * contains a given number of items.
 *
 * @param <T> The type of the items present in the bank
 *
 * @author Jürgen Moßgraber
 */
public interface IBank<T> extends IObserverManagement
{
    /**
     * Get the (maximum) number of elements in a page of the bank.
     *
     * @return The number of elements
     */
    int getPageSize ();


    /**
     * Get the number of all items in the bank.
     *
     * @return The number of all items
     */
    int getItemCount ();


    /**
     * Check if there is at least 1 existing item in the current page.
     *
     * @return True if there is at least 1 existing item
     */
    boolean hasExistingItems ();


    /**
     * Get the item at the given index.
     *
     * @param index The index of the item to get
     * @return The item
     */
    T getItem (int index);


    /**
     * Get the first selected item on the current page, if any.
     *
     * @return The selected item or null if no item is selected on the current page
     */
    Optional<T> getSelectedItem ();


    /**
     * Returns a list with the selected items in the current page.
     *
     * @return The list is empty if none is selected.
     */
    List<T> getSelectedItems ();


    /**
     * Registers an item selection observer.
     *
     * @param observer The observer to register
     */
    void addSelectionObserver (IItemSelectionObserver observer);


    /**
     * Unregisters an item selection observer.
     *
     * @param observer The observer to unregister
     */
    void removeSelectionObserver (IItemSelectionObserver observer);


    /**
     * Registers a page adjustment observer.
     *
     * @param observer The observer to register
     */
    void addPageObserver (IBankPageObserver observer);


    /**
     * Unregisters a page adjustment observer.
     *
     * @param observer The observer to unregister
     */
    void removePageObserver (IBankPageObserver observer);


    /**
     * Is there a previous page to select?
     *
     * @return True if there is a previous item of the current
     */
    boolean canScrollBackwards ();


    /**
     * Is there a previous item?
     *
     * @return True if there is a next item after the current
     */
    boolean canScrollForwards ();


    /**
     * Is there a next item to select?
     *
     * @return True if there is a page previous of the current
     */
    boolean canScrollPageBackwards ();


    /**
     * Is there a next page?
     *
     * @return True if there is a page after the current
     */
    boolean canScrollPageForwards ();


    /**
     * Scroll items backwards by 1.
     */
    void scrollBackwards ();


    /**
     * Scroll items forwards by 1.
     */
    void scrollForwards ();


    /**
     * Scrolls the bank page so that the item at the given position becomes visible as part of the
     * page. The position is the absolute index of the item in all the items the bank contains.
     *
     * @param position The position of the item to scroll to. The position is automatically adjusted
     *            to the beginning of a page
     */
    void scrollTo (int position);


    /**
     * Scrolls the bank page so that the item at the given position becomes visible as part of the
     * page. The position is the absolute index of the item in all the items the bank contains.
     *
     * @param position The position of the item to scroll to
     * @param adjustPage If true, the position is adjusted to the beginning of a page
     */
    void scrollTo (int position, boolean adjustPage);


    /**
     * Get the position of the first item of the current bank page.
     *
     * @return The position
     */
    int getScrollPosition ();


    /**
     * Get the position of the last item of the current bank page. E.g. if the current bank page
     * contains only 5 items, the position of the 5th item is returned. If there are no items -1 is
     * returned.
     *
     * @return The position or -1
     */
    int getPositionOfLastItem ();


    /**
     * Select the item at the given position. If the position is negative or larger than the bank
     * size nothing happens.
     *
     * @param position The position of the item to select
     */
    void selectItemAtPosition (int position);


    /**
     * Select the next item after the currently selected, if any. Scrolls the page, if necessary.
     */
    void selectNextItem ();


    /**
     * Select the previous item after the currently selected, if any. Scrolls the page, if
     * necessary.
     */
    void selectPreviousItem ();


    /**
     * Select the next page after the currently selected, if any. Selects the first item on the new
     * page.
     */
    void selectNextPage ();


    /**
     * Select the previous page before the currently selected, if any. Selects the last item on the
     * new page.
     */
    void selectPreviousPage ();


    /**
     * Disabled items will not be accessible via the bank if set to true.
     *
     * @param shouldSkip True to exclude disabled items
     */
    void setSkipDisabledItems (boolean shouldSkip);
}
