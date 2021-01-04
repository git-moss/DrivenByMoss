// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.parameterprovider;

import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.bank.IChannelBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.observer.IBankPageObserver;
import de.mossgrabers.framework.observer.IParametersAdjustObserver;
import de.mossgrabers.framework.observer.IValueObserver;

import java.util.HashSet;
import java.util.Set;


/**
 * Get a number of parameters. This abstract implementation provides a parameter of the channels of
 * the active page of the given channel bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractChannelParameterProvider implements IParameterProvider, IBankPageObserver, IValueObserver<ITrackBank>
{
    protected final IModel                         model;
    protected final Set<IParametersAdjustObserver> observers = new HashSet<> ();

    private final IChannelBank<? extends IChannel> bank;


    /**
     * Constructor.
     *
     * @param bank The bank from which to get the parameters
     */
    public AbstractChannelParameterProvider (final IChannelBank<? extends IChannel> bank)
    {
        this.bank = bank;
        this.model = null;
    }


    /**
     * Constructor.
     *
     * @param model Uses the current track bank from this model to get the parameters
     */
    public AbstractChannelParameterProvider (final IModel model)
    {
        this.bank = null;
        this.model = model;
    }


    /** {@inheritDoc} */
    @Override
    public int size ()
    {
        return this.getBank ().getPageSize ();
    }


    /** {@inheritDoc} */
    @Override
    public void addParametersObserver (final IParametersAdjustObserver observer)
    {
        this.observers.add (observer);

        if (this.bank != null)
            this.bank.addPageObserver (this);
        else
        {
            this.model.getTrackBank ().addPageObserver (this);
            final ITrackBank effectTrackBank = this.model.getEffectTrackBank ();
            if (effectTrackBank != null)
                effectTrackBank.addPageObserver (this);

            this.model.addTrackBankObserver (this);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void removeParametersObserver (final IParametersAdjustObserver observer)
    {
        this.observers.remove (observer);

        if (!this.observers.isEmpty ())
            return;

        if (this.bank != null)
            this.bank.removePageObserver (this);
        else
        {
            this.model.getTrackBank ().removePageObserver (this);
            final ITrackBank effectTrackBank = this.model.getEffectTrackBank ();
            if (effectTrackBank != null)
                effectTrackBank.removePageObserver (this);

            this.model.removeTrackBankObserver (this);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void notifyParametersObservers ()
    {
        this.observers.forEach (IParametersAdjustObserver::parametersAdjusted);
    }


    /** {@inheritDoc} */
    @Override
    public void pageAdjusted ()
    {
        this.notifyParametersObservers ();
    }


    /** {@inheritDoc} */
    @Override
    public void update (final ITrackBank value)
    {
        this.notifyParametersObservers ();
    }


    protected IChannel getChannel (final int index)
    {
        return this.getBank ().getItem (index);
    }


    protected IChannelBank<? extends IChannel> getBank ()
    {
        return this.bank == null ? this.model.getCurrentTrackBank () : this.bank;
    }
}
