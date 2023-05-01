// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw.data.bank;

import de.mossgrabers.bitwig.framework.daw.data.ParameterImpl;
import de.mossgrabers.bitwig.framework.daw.data.Util;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.daw.data.bank.AbstractItemBank;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.daw.data.bank.IParameterPageBank;
import de.mossgrabers.framework.parameter.IParameter;

import com.bitwig.extension.controller.api.CursorRemoteControlsPage;
import com.bitwig.extension.controller.api.SettableIntegerValue;


/**
 * Encapsulates the data of a parameter bank.
 *
 * @author Jürgen Moßgraber
 */
public class ParameterBankImpl extends AbstractItemBank<IParameter> implements IParameterBank
{
    private final CursorRemoteControlsPage remoteControls;
    private final IValueChanger            valueChanger;
    private final IParameterPageBank       pageBank;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param remoteControlsPage The remote controls bank
     * @param numParamPages The number of parameter pages
     * @param numParams The number of parameters in the page of the bank
     */
    public ParameterBankImpl (final IHost host, final IValueChanger valueChanger, final CursorRemoteControlsPage remoteControlsPage, final int numParamPages, final int numParams)
    {
        super (host, numParams);

        this.pageBank = numParams > 0 ? new ParameterPageBankImpl (remoteControlsPage, numParams) : null;

        this.valueChanger = valueChanger;
        this.remoteControls = remoteControlsPage;

        for (int i = 0; i < this.getPageSize (); i++)
            this.items.add (new ParameterImpl (this.valueChanger, this.remoteControls.getParameter (i), i, true));

        this.remoteControls.hasPrevious ().markInterested ();
        this.remoteControls.hasNext ().markInterested ();
        this.remoteControls.selectedPageIndex ().markInterested ();
        this.remoteControls.pageCount ().markInterested ();
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        for (final IItem item: this.items)
            item.enableObservers (enable);

        if (this.pageBank != null)
            this.pageBank.enableObservers (enable);

        Util.setIsSubscribed (this.remoteControls.hasPrevious (), enable);
        Util.setIsSubscribed (this.remoteControls.hasNext (), enable);
        Util.setIsSubscribed (this.remoteControls.selectedPageIndex (), enable);
        Util.setIsSubscribed (this.remoteControls.pageCount (), enable);
    }


    /** {@inheritDoc} */
    @Override
    public int getItemCount ()
    {
        return this.pageBank.getItemCount () * this.getPageSize ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollPageBackwards ()
    {
        return this.remoteControls.hasPrevious ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollPageForwards ()
    {
        return this.remoteControls.hasNext ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollBackwards ()
    {
        this.remoteControls.selectPreviousPage (false);
    }


    /** {@inheritDoc} */
    @Override
    public void scrollForwards ()
    {
        this.remoteControls.selectNextPage (false);
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousPage ()
    {
        int pos = this.remoteControls.selectedPageIndex ().get ();
        pos = Math.max (0, pos - this.getPageSize ());
        if (pos >= 0)
            this.remoteControls.selectedPageIndex ().set (pos);
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextPage ()
    {
        final int maxPages = this.remoteControls.pageCount ().get ();
        int pos = this.remoteControls.selectedPageIndex ().get ();
        pos = Math.min (maxPages - 1, pos + this.getPageSize ());
        if (pos >= 0)
            this.remoteControls.selectedPageIndex ().set (pos);
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItem ()
    {
        this.remoteControls.selectPrevious ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItem ()
    {
        this.remoteControls.selectNext ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollTo (final int position)
    {
        final SettableIntegerValue index = this.remoteControls.selectedPageIndex ();
        index.set (Math.max (Math.min (position, this.pageBank.getItemCount () - 1), 0));
    }


    /** {@inheritDoc} */
    @Override
    public void scrollTo (final int position, final boolean adjustPage)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public IParameterPageBank getPageBank ()
    {
        return this.pageBank;
    }
}