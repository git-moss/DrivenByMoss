// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.parameterprovider;

import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.observer.IBankPageObserver;
import de.mossgrabers.framework.observer.IParametersAdjustObserver;

import java.util.HashSet;
import java.util.Set;


/**
 * Get a number of parameters. This implementation provides all parameters from the current page of
 * the bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class BankParameterProvider implements IParameterProvider, IBankPageObserver
{
    private final IParameterBank                 bank;
    private final Set<IParametersAdjustObserver> observers = new HashSet<> ();


    /**
     * Constructor.
     *
     * @param bank The bank from which to get the parameters
     */
    public BankParameterProvider (final IParameterBank bank)
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
    public void addParametersObserver (final IParametersAdjustObserver observer)
    {
        this.observers.add (observer);
        this.bank.addPageObserver (this);
    }


    /** {@inheritDoc} */
    @Override
    public void removeParametersObserver (final IParametersAdjustObserver observer)
    {
        this.observers.remove (observer);
        if (this.observers.isEmpty ())
            this.bank.removePageObserver (this);
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
}
