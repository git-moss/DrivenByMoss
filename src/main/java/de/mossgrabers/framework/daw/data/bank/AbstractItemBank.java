// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data.bank;

import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.data.IItem;

import java.util.ArrayList;
import java.util.List;


/**
 * An abstract bank which contains items.
 *
 * @param <T> The specific item type of the bank item
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractItemBank<T extends IItem> extends AbstractBank<T>
{
    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param pageSize The number of elements in a page of the bank
     */
    public AbstractItemBank (final IHost host, final int pageSize)
    {
        super (host, pageSize);
    }


    /** {@inheritDoc} */
    @Override
    public T getSelectedItem ()
    {
        for (int i = 0; i < this.pageSize; i++)
        {
            final T item = this.getItem (i);
            if (item.isSelected ())
                return item;
        }
        return null;
    }


    /** {@inheritDoc} */
    @Override
    public List<T> getSelectedItems ()
    {
        final List<T> selection = new ArrayList<> ();
        for (int i = 0; i < this.pageSize; i++)
        {
            final T item = this.getItem (i);
            if (item.isSelected ())
                selection.add (item);
        }
        return selection;
    }


    /** {@inheritDoc} */
    @Override
    public void selectItemAtPosition (final int position)
    {
        if (position < 0 || position >= this.getItemCount ())
            return;
        final int ps = this.pageSize;
        this.scrollTo (position / ps * ps);
        this.host.scheduleTask ( () -> {

            this.getItem (position % ps).select ();
            this.firePageObserver ();

        }, 75);
    }


    /** {@inheritDoc} */
    @Override
    public int getScrollPosition ()
    {
        return this.getItem (0).getPosition ();
    }


    /** {@inheritDoc} */
    @Override
    public int getPositionOfLastItem ()
    {
        for (int i = this.pageSize - 1; i >= 0; i--)
        {
            final T item = this.getItem (i);
            if (item.doesExist ())
            {
                final int pos = item.getPosition ();
                if (pos >= 0)
                    return pos;
            }
        }
        return -1;
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollBackwards ()
    {
        final IItem sel = this.getSelectedItem ();
        final int selIndex = sel != null ? sel.getIndex () : -1;
        return selIndex > 0 || this.canScrollPageBackwards ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollForwards ()
    {
        final IItem sel = this.getSelectedItem ();
        final int selIndex = sel != null ? sel.getIndex () : -1;
        return selIndex >= 0 && selIndex < this.pageSize - 1 && this.getItem (selIndex + 1).doesExist () || this.canScrollPageForwards ();
    }
}
