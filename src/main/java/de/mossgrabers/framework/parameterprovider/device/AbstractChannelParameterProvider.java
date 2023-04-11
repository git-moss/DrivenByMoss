// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.parameterprovider.device;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.bank.IChannelBank;
import de.mossgrabers.framework.observer.IBankPageObserver;
import de.mossgrabers.framework.observer.IParametersAdjustObserver;
import de.mossgrabers.framework.parameterprovider.AbstractParameterProvider;

import java.util.Optional;


/**
 * Get a number of parameters. This abstract implementation provides a parameter of the channels of
 * the active page of the given channel bank.
 *
 * @param <B> The type of the bank
 * @param <C> The type of the banks' item
 *
 * @author Jürgen Moßgraber
 */
public abstract class AbstractChannelParameterProvider<B extends IChannelBank<C>, C extends IChannel> extends AbstractParameterProvider implements IBankPageObserver
{
    protected IChannelBank<? extends IChannel> bank;


    /**
     * Constructor. Monitors the given bank.
     *
     * @param bank The bank from which to get the parameters
     */
    protected AbstractChannelParameterProvider (final B bank)
    {
        this.bank = bank;
    }


    /** {@inheritDoc} */
    @Override
    public int size ()
    {
        return this.bank.getPageSize ();
    }


    /** {@inheritDoc} */
    @Override
    public void addParametersObserver (final IParametersAdjustObserver observer)
    {
        super.addParametersObserver (observer);

        this.bank.addPageObserver (this);
    }


    /** {@inheritDoc} */
    @Override
    public void removeParametersObserver (final IParametersAdjustObserver observer)
    {
        super.removeParametersObserver (observer);

        if (!this.hasObservers ())
            this.bank.removePageObserver (this);
    }


    /** {@inheritDoc} */
    @Override
    public void pageAdjusted ()
    {
        this.notifyParametersObservers ();
    }


    /** {@inheritDoc} */
    @Override
    public Optional<ColorEx> getColor (final int index)
    {
        return Optional.of (this.bank.getItem (index).getColor ());
    }
}
