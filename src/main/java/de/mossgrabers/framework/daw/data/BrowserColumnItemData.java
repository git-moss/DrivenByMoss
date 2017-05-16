// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data;

import com.bitwig.extension.controller.api.BrowserFilterItem;
import com.bitwig.extension.controller.api.BrowserItem;


/**
 * Encapsulates the data of a browser column entry.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class BrowserColumnItemData
{
    private final int         index;
    private final BrowserItem item;


    /**
     * Constructor.
     *
     * @param item The item
     * @param index The index of the item
     */
    public BrowserColumnItemData (final BrowserItem item, final int index)
    {
        this.index = index;
        this.item = item;

        item.exists ().markInterested ();
        item.name ().markInterested ();
        item.isSelected ().markInterested ();
        if (item instanceof BrowserFilterItem)
            ((BrowserFilterItem) item).hitCount ().markInterested ();
    }


    /**
     * Dis-/Enable all attributes. They are enabled by default. Use this function if values are
     * currently not needed to improve performance.
     *
     * @param enable True to enable
     */
    public void enableObservers (final boolean enable)
    {
        this.item.exists ().setIsSubscribed (enable);
        this.item.name ().setIsSubscribed (enable);
        this.item.isSelected ().setIsSubscribed (enable);
        if (this.item instanceof BrowserFilterItem)
            ((BrowserFilterItem) this.item).hitCount ().setIsSubscribed (enable);
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
     * Does the item exist?
     *
     * @return True if it exists
     */
    public boolean doesExist ()
    {
        return this.item.exists ().get ();
    }


    /**
     * Get the name of the item.
     *
     * @return The name of the item
     */
    public String getName ()
    {
        return this.item.name ().get ();
    }


    /**
     * Returns true if the item is selected.
     *
     * @return True if the item is selected.
     */
    public boolean isSelected ()
    {
        return this.item.isSelected ().get ();
    }


    /**
     * Get the hit count.
     *
     * @return The hit count
     */
    public int getHitCount ()
    {
        return this.item instanceof BrowserFilterItem ? ((BrowserFilterItem) this.item).hitCount ().get () : 0;
    }
}
