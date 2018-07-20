// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw;

import java.util.List;


/**
 * Interface to a bank. A bank provides a view to a number of items split into pages. A page
 * contains a given numbner of items.
 *
 * @param <T> The type of the items present in the bank
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IBank<T> extends ObserverManagement
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
     * Get the item at the given index.
     *
     * @param index The index of the item to get
     * @return The item
     */
    T getItem (final int index);


    /**
     * Get the first selected item on the current page, if any.
     *
     * @return The selected item or null if no item is selected on the current page
     */
    T getSelectedItem ();


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
    void addSelectionObserver (final ItemSelectionObserver observer);


    /**
     * Is there a previous page to select?
     *
     * @return True if there is a page previous of the current
     */
    boolean canScrollBackwards ();


    /**
     * Is there a next page?
     *
     * @return True if there is a page after the current
     */
    boolean canScrollForwards ();


    /**
     * Scroll items backwards by 1.
     */
    void scrollBackwards ();


    /**
     * Scroll items forwards by 1.
     */
    void scrollForwards ();


    /**
     * Scroll items backwards by 1 page.
     */
    void scrollPageBackwards ();


    /**
     * Scroll items forwards by 1 page.
     */
    void scrollPageForwards ();


    /**
     * Scrolls the bank page so that the item at the given position becomes visible as part of the
     * page. The position is the absolute index of the item in all the items the bank contains.
     *
     * @param position The postion of the item to scroll to
     */
    void scrollTo (final int position);


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
}
