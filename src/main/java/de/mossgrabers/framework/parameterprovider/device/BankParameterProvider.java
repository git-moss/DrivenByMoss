// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.parameterprovider.device;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.data.bank.IBank;
import de.mossgrabers.framework.observer.IBankPageObserver;
import de.mossgrabers.framework.observer.IParametersAdjustObserver;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.parameterprovider.AbstractParameterProvider;

import java.util.Optional;


/**
 * Get a number of parameters. This implementation provides all parameters from the current page of
 * the bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class BankParameterProvider extends AbstractParameterProvider implements IBankPageObserver
{
    private final IBank<IParameter> bank;


    /**
     * Constructor.
     *
     * @param bank The bank from which to get the parameters
     */
    public BankParameterProvider (final IBank<IParameter> bank)
    {
        this.bank = bank;
    }


    /** {@inheritDoc} */
    @Override
    public int size ()
    {
        return this.bank.getPageSize ();
    }


    /**
     * Throws a FrameworkException if the bank does not contain parameters.
     *
     * {@inheritDoc}
     */
    @Override
    public IParameter get (final int index)
    {
        return this.bank.getItem (index);
    }


    /** {@inheritDoc} */
    @Override
    public Optional<ColorEx> getColor (final int index)
    {
        return Optional.empty ();
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
}
