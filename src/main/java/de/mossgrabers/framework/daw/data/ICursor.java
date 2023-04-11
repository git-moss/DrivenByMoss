// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data;

/**
 * Interface to a Cursor.
 *
 * @author Jürgen Moßgraber
 */
public interface ICursor extends IPinnable
{
    /**
     * Is there a previous item?
     *
     * @return True if there is a previous item
     */
    boolean canSelectPrevious ();


    /**
     * Is there a next item?
     *
     * @return True if there is a next item
     */
    boolean canSelectNext ();


    /**
     * Select the previous item.
     */
    void selectPrevious ();


    /**
     * Select the next item.
     */
    void selectNext ();


    /**
     * Moves the cursor item to the left in the item chain (swaps it position with the previous
     * item). If there is no item before this, nothing happens.
     */
    void swapWithPrevious ();


    /**
     * Moves the item to the right in the item chain (swaps it position with the following item). If
     * there is no item after this, nothing happens.
     */
    void swapWithNext ();
}