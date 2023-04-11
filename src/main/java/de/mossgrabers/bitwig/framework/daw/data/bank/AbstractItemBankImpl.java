// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw.data.bank;

import de.mossgrabers.bitwig.framework.daw.data.Util;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.daw.data.bank.AbstractItemBank;

import com.bitwig.extension.controller.api.Bank;

import java.util.Optional;


/**
 * An abstract bank which uses internally a Bitwig bank.
 *
 * @param <B> The specific Bitwig bank type
 * @param <T> The specific item type of the bank item
 *
 * @author Jürgen Moßgraber
 */
public abstract class AbstractItemBankImpl<B extends Bank<?>, T extends IItem> extends AbstractItemBank<T>
{
    protected final IValueChanger valueChanger;
    protected final Optional<B>   bank;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param bank The bank to encapsulate
     * @param pageSize The number of elements in a page of the bank
     */
    protected AbstractItemBankImpl (final IHost host, final IValueChanger valueChanger, final B bank, final int pageSize)
    {
        super (host, pageSize);

        this.valueChanger = valueChanger;
        this.bank = Optional.ofNullable (bank);

        if (this.bank.isEmpty ())
            return;

        bank.scrollPosition ().markInterested ();
        bank.canScrollBackwards ().markInterested ();
        bank.canScrollForwards ().markInterested ();
        bank.itemCount ().markInterested ();
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        for (final IItem item: this.items)
            item.enableObservers (enable);

        if (this.bank.isEmpty ())
            return;

        final B b = this.bank.get ();
        Util.setIsSubscribed (b.scrollPosition (), enable);
        Util.setIsSubscribed (b.canScrollBackwards (), enable);
        Util.setIsSubscribed (b.canScrollForwards (), enable);
        Util.setIsSubscribed (b.itemCount (), enable);
    }


    /** {@inheritDoc} */
    @Override
    public int getItemCount ()
    {
        return this.bank.isEmpty () ? 0 : this.bank.get ().itemCount ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public int getScrollPosition ()
    {
        return this.bank.isEmpty () ? -1 : this.bank.get ().scrollPosition ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollBackwards ()
    {
        if (this.bank.isPresent ())
            this.bank.get ().scrollBackwards ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollForwards ()
    {
        if (this.bank.isPresent ())
            this.bank.get ().scrollForwards ();
    }


    /**
     * Scroll items backwards by 1 page.
     */
    protected void scrollPageBackwards ()
    {
        if (this.bank.isPresent ())
            this.bank.get ().scrollPageBackwards ();
    }


    /**
     * Scroll items forwards by 1 page.
     */
    protected void scrollPageForwards ()
    {
        if (this.bank.isPresent ())
            this.bank.get ().scrollPageForwards ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollPageBackwards ()
    {
        return this.bank.isPresent () && this.bank.get ().canScrollBackwards ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollPageForwards ()
    {
        return this.bank.isPresent () && this.bank.get ().canScrollForwards ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollTo (final int position)
    {
        this.scrollTo (position, true);
    }


    /** {@inheritDoc} */
    @Override
    public void scrollTo (final int position, final boolean adjustPage)
    {
        if (this.bank.isPresent () && position >= 0 && position < this.getItemCount ())
            this.bank.get ().scrollPosition ().set (position);
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItem ()
    {
        final Optional<T> sel = this.getSelectedItem ();
        final int index = sel.isEmpty () ? 0 : sel.get ().getIndex () + 1;
        if (index == this.getPageSize ())
            this.selectNextPage ();
        else
            this.getItem (index).select ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItem ()
    {
        final Optional<T> sel = this.getSelectedItem ();
        final int index = sel.isEmpty () ? 0 : sel.get ().getIndex () - 1;
        if (index == -1)
            this.selectPreviousPage ();
        else
            this.getItem (index).select ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousPage ()
    {
        if (!this.canScrollPageBackwards ())
            return;
        this.scrollPageBackwards ();
        this.host.scheduleTask ( () -> this.getItem (this.getPageSize () - 1).select (), 75);
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextPage ()
    {
        if (!this.canScrollPageForwards ())
            return;
        this.scrollPageForwards ();
        this.host.scheduleTask ( () -> this.getItem (0).select (), 75);
    }


    /** {@inheritDoc} */
    @Override
    public void setSkipDisabledItems (final boolean shouldSkip)
    {
        if (this.bank.isPresent ())
            this.bank.get ().setSkipDisabledItems (shouldSkip);
    }
}
