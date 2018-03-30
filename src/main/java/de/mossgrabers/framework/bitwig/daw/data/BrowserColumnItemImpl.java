// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.bitwig.daw.data;

import de.mossgrabers.framework.daw.data.IBrowserColumnItem;

import com.bitwig.extension.controller.api.BrowserFilterItem;
import com.bitwig.extension.controller.api.BrowserItem;


/**
 * Encapsulates the data of a browser column entry.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class BrowserColumnItemImpl implements IBrowserColumnItem
{
    private final int         index;
    private final BrowserItem item;


    /**
     * Constructor.
     *
     * @param item The item
     * @param index The index of the item
     */
    public BrowserColumnItemImpl (final BrowserItem item, final int index)
    {
        this.index = index;
        this.item = item;

        item.exists ().markInterested ();
        item.name ().markInterested ();
        item.isSelected ().markInterested ();
        if (item instanceof BrowserFilterItem)
            ((BrowserFilterItem) item).hitCount ().markInterested ();
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        this.item.exists ().setIsSubscribed (enable);
        this.item.name ().setIsSubscribed (enable);
        this.item.isSelected ().setIsSubscribed (enable);
        if (this.item instanceof BrowserFilterItem)
            ((BrowserFilterItem) this.item).hitCount ().setIsSubscribed (enable);
    }


    /** {@inheritDoc} */
    @Override
    public int getIndex ()
    {
        return this.index;
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
        return this.item instanceof BrowserFilterItem ? ((BrowserFilterItem) this.item).hitCount ().get () : 0;
    }
}
