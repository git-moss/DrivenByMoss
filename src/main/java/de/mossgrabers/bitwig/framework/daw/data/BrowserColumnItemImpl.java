// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw.data;

import de.mossgrabers.framework.daw.data.AbstractItemImpl;
import de.mossgrabers.framework.daw.data.IBrowserColumnItem;
import de.mossgrabers.framework.observer.IValueObserver;

import com.bitwig.extension.controller.api.BrowserFilterItem;
import com.bitwig.extension.controller.api.BrowserItem;
import com.bitwig.extension.controller.api.IntegerValue;


/**
 * Encapsulates the data of a browser column entry.
 *
 * @author Jürgen Moßgraber
 */
public class BrowserColumnItemImpl extends AbstractItemImpl implements IBrowserColumnItem
{
    private final BrowserItem item;


    /**
     * Constructor.
     *
     * @param item The item
     * @param index The index of the item
     */
    public BrowserColumnItemImpl (final BrowserItem item, final int index)
    {
        super (index);

        this.item = item;

        item.exists ().markInterested ();
        item.name ().markInterested ();
        item.isSelected ().markInterested ();
        if (item instanceof final BrowserFilterItem filterItem)
            filterItem.hitCount ().markInterested ();
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        Util.setIsSubscribed (this.item.exists (), enable);
        Util.setIsSubscribed (this.item.name (), enable);
        Util.setIsSubscribed (this.item.isSelected (), enable);
        if (this.item instanceof final BrowserFilterItem filterItem)
            Util.setIsSubscribed (filterItem.hitCount (), enable);
    }


    /** {@inheritDoc} */
    @Override
    public boolean doesExist ()
    {
        return this.item.exists ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public String getName ()
    {
        return this.item.name ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void addNameObserver (final IValueObserver<String> observer)
    {
        this.item.name ().addValueObserver (observer::update);
    }


    /** {@inheritDoc} */
    @Override
    public String getName (final int limit)
    {
        return this.item.name ().getLimited (limit);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isSelected ()
    {
        return this.item.isSelected ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public int getHitCount ()
    {
        return this.item instanceof final BrowserFilterItem filterItem ? this.safeGetInteger (filterItem.hitCount ()) : 0;
    }


    protected int safeGetInteger (final IntegerValue value)
    {
        return value.isSubscribed () ? value.get () : 0;
    }
}
