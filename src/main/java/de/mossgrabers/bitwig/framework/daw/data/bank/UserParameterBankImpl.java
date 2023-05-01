// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw.data.bank;

import de.mossgrabers.bitwig.framework.daw.data.ParameterImpl;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.data.bank.AbstractBank;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.daw.data.bank.IParameterPageBank;
import de.mossgrabers.framework.observer.IItemSelectionObserver;
import de.mossgrabers.framework.parameter.IParameter;

import com.bitwig.extension.controller.api.UserControlBank;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


/**
 * Encapsulates the data of a user parameter bank.
 *
 * @author Jürgen Moßgraber
 */
public class UserParameterBankImpl extends AbstractBank<IParameter> implements IParameterBank
{
    private final UserControlBank userControlBank;
    private final IValueChanger   valueChanger;

    private int                   page = 0;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param userControlBank The user controls bank
     * @param numPages The number of user parameter pages
     * @param numParamsPerPage The number of parameters per page
     */
    public UserParameterBankImpl (final IHost host, final IValueChanger valueChanger, final UserControlBank userControlBank, final int numPages, final int numParamsPerPage)
    {
        super (host, numParamsPerPage);

        this.valueChanger = valueChanger;
        this.userControlBank = userControlBank;

        final int itemCount = numPages * numParamsPerPage;
        for (int i = 0; i < itemCount; i++)
            this.items.add (new ParameterImpl (this.valueChanger, this.userControlBank.getControl (i), i));
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        this.items.forEach (item -> item.enableObservers (enable));
    }


    /** {@inheritDoc} */
    @Override
    public IParameter getItem (final int index)
    {
        final int position = this.page * this.getPageSize () + index;
        return this.items.get (position);
    }


    /** {@inheritDoc} */
    @Override
    public Optional<IParameter> getSelectedItem ()
    {
        return Optional.empty ();
    }


    /** {@inheritDoc} */
    @Override
    public List<IParameter> getSelectedItems ()
    {
        return Collections.emptyList ();
    }


    /** {@inheritDoc} */
    @Override
    public void addSelectionObserver (final IItemSelectionObserver observer)
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
        return this.page < this.getItemCount () / this.getPageSize () - 1;
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
        this.setPage (this.page - 1);
    }


    /** {@inheritDoc} */
    @Override
    public void scrollForwards ()
    {
        this.setPage (this.page + 1);
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
        this.setPage (position / this.getPageSize ());
    }


    private void setPage (final int page)
    {
        this.page = Math.min (this.getItemCount () / this.getPageSize () - 1, Math.max (0, page));
        this.firePageObserver ();
    }


    /** {@inheritDoc} */
    @Override
    public int getScrollPosition ()
    {
        return this.page * this.getPageSize ();
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


    /** {@inheritDoc} */
    @Override
    public IParameterPageBank getPageBank ()
    {
        // Not use. User parameters will be removed anyway
        return null;
    }
}