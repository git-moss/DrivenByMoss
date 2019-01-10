// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw;

import de.mossgrabers.bitwig.framework.daw.data.ParameterImpl;
import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.AbstractBank;
import de.mossgrabers.framework.daw.IParameterBank;
import de.mossgrabers.framework.daw.IParameterPageBank;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.daw.data.IParameter;

import com.bitwig.extension.controller.api.CursorRemoteControlsPage;
import com.bitwig.extension.controller.api.SettableIntegerValue;


/**
 * Encapsulates the data of a parameter bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ParameterBankImpl extends AbstractBank<IParameter> implements IParameterBank
{
    private CursorRemoteControlsPage remoteControls;
    private final IValueChanger      valueChanger;
    private IParameterPageBank       pageBank;


    /**
     * Constructor.
     *
     * @param valueChanger The value changer
     * @param pageBank The page bank
     * @param remoteControlsPage The remote controls bank
     * @param numParams The number of parameters in the page of the bank
     */
    public ParameterBankImpl (final IValueChanger valueChanger, final IParameterPageBank pageBank, final CursorRemoteControlsPage remoteControlsPage, final int numParams)
    {
        super (numParams);
        this.pageBank = pageBank;

        this.valueChanger = valueChanger;
        this.remoteControls = remoteControlsPage;

        this.initItems ();

        this.remoteControls.hasPrevious ().markInterested ();
        this.remoteControls.hasNext ().markInterested ();
    }


    /** {@inheritDoc} */
    @Override
    protected void initItems ()
    {
        for (int i = 0; i < this.pageSize; i++)
            this.items.add (new ParameterImpl (this.valueChanger, this.remoteControls.getParameter (i), i));
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        for (final IItem item: this.items)
            item.enableObservers (enable);

        this.remoteControls.hasPrevious ().setIsSubscribed (enable);
        this.remoteControls.hasNext ().setIsSubscribed (enable);
    }


    /** {@inheritDoc} */
    @Override
    public int getItemCount ()
    {
        return this.pageBank.getItemCount () * this.getPageSize ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollBackwards ()
    {
        return this.remoteControls.hasPrevious ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollForwards ()
    {
        return this.remoteControls.hasNext ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollBackwards ()
    {
        this.remoteControls.selectPreviousPage (true);
    }


    /** {@inheritDoc} */
    @Override
    public void scrollForwards ()
    {
        this.remoteControls.selectNextPage (true);
    }


    /** {@inheritDoc} */
    @Override
    public void scrollPageBackwards ()
    {
        final SettableIntegerValue index = this.remoteControls.selectedPageIndex ();
        index.set (Math.max (index.get () - this.getPageSize (), 0));
    }


    /** {@inheritDoc} */
    @Override
    public void scrollPageForwards ()
    {
        final SettableIntegerValue index = this.remoteControls.selectedPageIndex ();
        index.set (Math.min (index.get () + this.getPageSize (), this.pageBank.getItemCount () - 1));
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
}