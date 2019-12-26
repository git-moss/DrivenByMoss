// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw;

import de.mossgrabers.bitwig.framework.daw.data.ParameterImpl;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IParameterBank;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.observer.ItemSelectionObserver;

import com.bitwig.extension.controller.api.UserControlBank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Encapsulates the data of a user parameter bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class UserParameterBankImpl implements IParameterBank
{
    private final UserControlBank  userControlBank;
    private final IValueChanger    valueChanger;
    private final int              itemCount;
    private final int              pageSize;
    private final List<IParameter> items;

    private int                    page = 0;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param userControlBank The user controls bank
     * @param numAllParams The number of all user parameters
     * @param numParams The number of parameters in the page of the bank
     */
    public UserParameterBankImpl (final IHost host, final IValueChanger valueChanger, final UserControlBank userControlBank, final int numAllParams, final int numParams)
    {
        this.valueChanger = valueChanger;
        this.userControlBank = userControlBank;

        this.itemCount = numAllParams;
        this.pageSize = numParams;

        this.items = new ArrayList<> (this.itemCount);
        for (int i = 0; i < this.itemCount; i++)
            this.items.add (new ParameterImpl (this.valueChanger, this.userControlBank.getControl (i), i));
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        for (final IItem item: this.items)
            item.enableObservers (enable);
    }


    /** {@inheritDoc} */
    @Override
    public int getPageSize ()
    {
        return this.pageSize;
    }


    /** {@inheritDoc} */
    @Override
    public int getItemCount ()
    {
        return this.itemCount;
    }


    /** {@inheritDoc} */
    @Override
    public IParameter getItem (final int index)
    {
        final int position = this.page * this.pageSize + index;
        return this.items.get (position);
    }


    /** {@inheritDoc} */
    @Override
    public IParameter getSelectedItem ()
    {
        return null;
    }


    /** {@inheritDoc} */
    @Override
    public List<IParameter> getSelectedItems ()
    {
        return Collections.emptyList ();
    }


    /** {@inheritDoc} */
    @Override
    public void addSelectionObserver (final ItemSelectionObserver observer)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollBackwards ()
    {
        return this.page > 0;
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollForwards ()
    {
        return this.page < this.itemCount / this.pageSize - 1;
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollPageBackwards ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollPageForwards ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void scrollBackwards ()
    {
        this.page = Math.max (0, this.page - 1);
    }


    /** {@inheritDoc} */
    @Override
    public void scrollForwards ()
    {
        this.page = Math.min (this.itemCount / this.pageSize - 1, this.page + 1);
    }


    /** {@inheritDoc} */
    @Override
    public void scrollTo (final int position)
    {
        this.scrollTo (position, false);
    }


    /** {@inheritDoc} */
    @Override
    public void scrollTo (final int position, final boolean adjustPage)
    {
        this.page = position / this.pageSize;
    }


    /** {@inheritDoc} */
    @Override
    public int getScrollPosition ()
    {
        return this.page * this.pageSize;
    }


    /** {@inheritDoc} */
    @Override
    public int getPositionOfLastItem ()
    {
        return this.getItemCount () - 1;
    }


    /** {@inheritDoc} */
    @Override
    public void selectItemAtPosition (final int position)
    {
        this.scrollTo (position);
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItem ()
    {
        this.scrollForwards ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItem ()
    {
        this.scrollBackwards ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextPage ()
    {
        this.scrollForwards ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousPage ()
    {
        this.scrollBackwards ();
    }
}