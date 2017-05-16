// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data;

import com.bitwig.extension.controller.api.BrowserFilterColumn;
import com.bitwig.extension.controller.api.BrowserItemBank;
import com.bitwig.extension.controller.api.CursorBrowserFilterItem;
import com.bitwig.extension.controller.api.CursorBrowserItem;


/**
 * Encapsulates the data of a browser column.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class BrowserColumnData
{
    private final int                 index;
    private final BrowserFilterColumn column;
    private BrowserColumnItemData []  items;
    private BrowserItemBank<?>        itemBank;
    private CursorBrowserFilterItem   cursorResult;


    /**
     * Constructor.
     *
     * @param column The column
     * @param index The index of the column
     * @param numFilterColumnEntries The number of entries in a filter column (bank page)
     */
    public BrowserColumnData (final BrowserFilterColumn column, final int index, final int numFilterColumnEntries)
    {
        this.index = index;
        this.column = column;

        column.exists ().markInterested ();
        column.name ().markInterested ();
        column.getWildcardItem ().name ().markInterested ();

        this.itemBank = ((CursorBrowserItem) column.createCursorItem ()).createSiblingsBank (numFilterColumnEntries);
        this.itemBank.cursorIndex ().markInterested ();

        this.items = new BrowserColumnItemData [numFilterColumnEntries];
        for (int i = 0; i < numFilterColumnEntries; i++)
            this.items[i] = new BrowserColumnItemData (this.itemBank.getItemAt (i), i);

        this.cursorResult = (CursorBrowserFilterItem) column.createCursorItem ();
        this.cursorResult.exists ().markInterested ();
        this.cursorResult.name ().markInterested ();
    }


    /**
     * Dis-/Enable all attributes. They are enabled by default. Use this function if values are
     * currently not needed to improve performance.
     *
     * @param enable True to enable
     */
    public void enableObservers (final boolean enable)
    {
        this.column.exists ().setIsSubscribed (enable);
        this.column.name ().setIsSubscribed (enable);
        this.column.getWildcardItem ().name ().setIsSubscribed (enable);
        this.itemBank.cursorIndex ().setIsSubscribed (enable);

        for (final BrowserColumnItemData item: this.items)
            item.enableObservers (enable);

        this.cursorResult.exists ().setIsSubscribed (enable);
        this.cursorResult.name ().setIsSubscribed (enable);
    }


    /**
     * Get the index.
     *
     * @return The index
     */
    public int getIndex ()
    {
        return this.index;
    }


    /**
     * Does the column exist?
     *
     * @return True if it exists
     */
    public boolean doesExist ()
    {
        return this.column.exists ().get ();
    }


    /**
     * Get the name of the column.
     *
     * @return The name of the column
     */
    public String getName ()
    {
        return this.column.name ().get ();
    }


    /**
     * Get the name of the wildcard.
     *
     * @return The wildcard
     */
    public String getWildcard ()
    {
        return this.column.getWildcardItem ().name ().get ();
    }


    /**
     * Does the cursor exist?
     *
     * @return True if it cursor exists
     */
    public boolean doesCursorExist ()
    {
        return this.cursorResult.exists ().get ();
    }


    /**
     * Get the cursor name of the column.
     *
     * @return The cursor name of the column
     */
    public String getCursorName ()
    {
        return this.cursorResult.name ().get ();
    }


    /**
     * Get the item data.
     *
     * @return The item data
     */
    public BrowserColumnItemData [] getItems ()
    {
        return this.items;
    }


    /**
     * Scroll up the items by one page.
     */
    public void scrollItemPageUp ()
    {
        this.itemBank.scrollPageBackwards ();
    }


    /**
     * Scroll down the items by one page.
     */
    public void scrollItemPageDown ()
    {
        this.itemBank.scrollPageForwards ();
    }


    /**
     * Reset the column filter.
     */
    public void resetFilter ()
    {
        this.itemBank.cursorIndex ().set (0);
    }


    /**
     * Select the previous item.
     */
    public void selectPreviousItem ()
    {
        this.cursorResult.selectPrevious ();
    }


    /**
     * Select the next item.
     */
    public void selectNextItem ()
    {
        this.cursorResult.selectNext ();
    }


    /**
     * Get the index of the cursor item.
     *
     * @return The index
     */
    public int getCursorIndex ()
    {
        return this.itemBank.cursorIndex ().get ();
    }


    /**
     * Set the cursor index.
     *
     * @param index The new index
     */
    public void setCursorIndex (final int index)
    {
        this.itemBank.cursorIndex ().set (index);
    }
}
