// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.parameterprovider.track;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.observer.IBankPageObserver;
import de.mossgrabers.framework.observer.IParametersAdjustObserver;
import de.mossgrabers.framework.observer.IValueObserver;
import de.mossgrabers.framework.parameterprovider.AbstractParameterProvider;

import java.util.Optional;


/**
 * Get a number of parameters. This abstract implementation provides a parameter of the current or
 * the given track bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractTrackParameterProvider extends AbstractParameterProvider implements IBankPageObserver, IValueObserver<ITrackBank>
{
    protected final IModel model;
    protected ITrackBank   bank;


    /**
     * Constructor. Monitors the given bank.
     *
     * @param bank The bank from which to get the parameters
     */
    protected AbstractTrackParameterProvider (final ITrackBank bank)
    {
        this (null, bank);
    }


    /**
     * Constructor. Monitors the track and effect track banks as well as switching between them.
     *
     * @param model Uses the current track bank from this model to get the parameters
     */
    protected AbstractTrackParameterProvider (final IModel model)
    {
        this (model, model.getCurrentTrackBank ());
    }


    /**
     * Constructor. Monitors the track and effect track banks as well as switching between them.
     *
     * @param model Uses the current track bank from this model to get the parameters
     * @param bank The bank from which to get the parameters
     */
    private AbstractTrackParameterProvider (final IModel model, final ITrackBank bank)
    {
        this.bank = bank;
        this.model = model;

        // Monitor switching between the instrument/audio and effect track banks - must always be
        // active!
        if (this.model != null)
            this.model.addTrackBankObserver (this);
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

        if (this.model == null)
        {
            this.bank.addPageObserver (this);
            return;
        }

        // Monitor the instrument/audio and effect track banks
        this.model.getTrackBank ().addPageObserver (this);
        final ITrackBank effectTrackBank = this.model.getEffectTrackBank ();
        if (effectTrackBank != null)
            effectTrackBank.addPageObserver (this);
    }


    /** {@inheritDoc} */
    @Override
    public void removeParametersObserver (final IParametersAdjustObserver observer)
    {
        super.removeParametersObserver (observer);

        if (this.hasObservers ())
            return;

        if (this.model == null)
        {
            this.bank.removePageObserver (this);
            return;
        }

        this.model.getTrackBank ().removePageObserver (this);
        final ITrackBank effectTrackBank = this.model.getEffectTrackBank ();
        if (effectTrackBank != null)
            effectTrackBank.removePageObserver (this);
    }


    /** {@inheritDoc} */
    @Override
    public void pageAdjusted ()
    {
        this.notifyParametersObservers ();
    }


    /** {@inheritDoc} */
    @Override
    public void update (final ITrackBank bank)
    {
        this.bank = bank;

        this.notifyParametersObservers ();
    }


    /** {@inheritDoc} */
    @Override
    public Optional<ColorEx> getColor (final int index)
    {
        return Optional.of (this.bank.getItem (index).getColor ());
    }
}
