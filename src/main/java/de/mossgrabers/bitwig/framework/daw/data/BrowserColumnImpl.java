// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw.data;

import de.mossgrabers.framework.daw.data.AbstractItemImpl;
import de.mossgrabers.framework.daw.data.IBrowserColumn;
import de.mossgrabers.framework.daw.data.IBrowserColumnItem;
import de.mossgrabers.framework.observer.IValueObserver;

import com.bitwig.extension.controller.api.BrowserFilterColumn;
import com.bitwig.extension.controller.api.BrowserItemBank;
import com.bitwig.extension.controller.api.CursorBrowserFilterItem;
import com.bitwig.extension.controller.api.CursorBrowserItem;


/**
 * Encapsulates the data of a browser column.
 *
 * @author Jürgen Moßgraber
 */
public class BrowserColumnImpl extends AbstractItemImpl implements IBrowserColumn
{
    private final BrowserFilterColumn     column;
    private final IBrowserColumnItem []   items;
    private final BrowserItemBank<?>      itemBank;
    private final CursorBrowserFilterItem cursorResult;


    /**
     * Constructor.
     *
     * @param column The column
     * @param index The index of the column
     * @param numFilterColumnEntries The number of entries in a filter column (bank page)
     */
    public BrowserColumnImpl (final BrowserFilterColumn column, final int index, final int numFilterColumnEntries)
    {
        super (index);

        this.column = column;

        column.exists ().markInterested ();
        column.name ().markInterested ();
        column.getWildcardItem ().name ().markInterested ();

        this.itemBank = ((CursorBrowserItem) column.createCursorItem ()).createSiblingsBank (numFilterColumnEntries);
        this.itemBank.cursorIndex ().markInterested ();

        this.items = new IBrowserColumnItem [numFilterColumnEntries];
        for (int i = 0; i < numFilterColumnEntries; i++)
            this.items[i] = new BrowserColumnItemImpl (this.itemBank.getItemAt (i), i);

        this.cursorResult = (CursorBrowserFilterItem) column.createCursorItem ();
        this.cursorResult.exists ().markInterested ();
        this.cursorResult.name ().markInterested ();
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        Util.setIsSubscribed (this.column.exists (), enable);
        Util.setIsSubscribed (this.column.name (), enable);
        Util.setIsSubscribed (this.column.getWildcardItem ().name (), enable);
        Util.setIsSubscribed (this.itemBank.cursorIndex (), enable);

        for (final IBrowserColumnItem item: this.items)
            item.enableObservers (enable);

        Util.setIsSubscribed (this.cursorResult.exists (), enable);
        Util.setIsSubscribed (this.cursorResult.name (), enable);
    }


    /** {@inheritDoc} */
    @Override
    public boolean doesExist ()
    {
        return this.column.exists ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public String getName ()
    {
        return this.column.name ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void addNameObserver (final IValueObserver<String> observer)
    {
        this.column.name ().addValueObserver (observer::update);
    }


    /** {@inheritDoc} */
    @Override
    public String getName (final int limit)
    {
        return this.column.name ().getLimited (limit);
    }


    /** {@inheritDoc} */
    @Override
    public String getWildcard ()
    {
        return this.column.getWildcardItem ().name ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean doesCursorExist ()
    {
        return this.cursorResult.exists ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public String getCursorName ()
    {
        return this.cursorResult.name ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public String getCursorName (final int limit)
    {
        return this.cursorResult.name ().getLimited (limit);
    }


    /** {@inheritDoc} */
    @Override
    public IBrowserColumnItem [] getItems ()
    {
        return this.items;
    }


    /** {@inheritDoc} */
    @Override
    public void scrollItemPageUp ()
    {
        this.itemBank.scrollPageBackwards ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollItemPageDown ()
    {
        this.itemBank.scrollPageForwards ();
    }


    /** {@inheritDoc} */
    @Override
    public void resetFilter ()
    {
        this.cursorResult.selectFirst ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItem ()
    {
        this.cursorResult.selectPrevious ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItem ()
    {
        this.cursorResult.selectNext ();
    }


    /** {@inheritDoc} */
    @Override
    public int getCursorIndex ()
    {
        return this.itemBank.cursorIndex ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void setCursorIndex (final int index)
    {
        this.itemBank.cursorIndex ().set (index);
    }
}
