// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data;

/**
 * Interface to a browser column.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IBrowserColumn extends IItem
{
    /**
     * Get the name of the wildcard.
     *
     * @return The wildcard
     */
    String getWildcard ();


    /**
     * Does the cursor exist?
     *
     * @return True if it cursor exists
     */
    boolean doesCursorExist ();


    /**
     * Get the cursor name of the column.
     *
     * @return The cursor name of the column
     */
    String getCursorName ();


    /**
     * Get the cursor name of the column.
     *
     * @param limit The maximum number of characters to get
     * @return The cursor name of the column
     */
    String getCursorName (int limit);


    /**
     * Get the item data.
     *
     * @return The item data
     */
    IBrowserColumnItem [] getItems ();


    /**
     * Scroll up the items by one page.
     */
    void scrollItemPageUp ();


    /**
     * Scroll down the items by one page.
     */
    void scrollItemPageDown ();


    /**
     * Reset the column filter.
     */
    void resetFilter ();


    /**
     * Select the previous item.
     */
    void selectPreviousItem ();


    /**
     * Select the next item.
     */
    void selectNextItem ();


    /**
     * Get the index of the cursor item.
     *
     * @return The index
     */
    int getCursorIndex ();


    /**
     * Set the cursor index.
     *
     * @param index The new index
     */
    void setCursorIndex (int index);
}