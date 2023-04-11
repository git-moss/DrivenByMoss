// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.parameterprovider;

import de.mossgrabers.framework.observer.IParametersAdjustObserver;

import java.util.HashSet;
import java.util.Set;


/**
 * Get a number of parameters. This abstract implementation provides the management of observers.
 *
 * @author Jürgen Moßgraber
 */
public abstract class AbstractParameterProvider implements IParameterProvider
{
    private final Set<IParametersAdjustObserver> observers = new HashSet<> ();


    /** {@inheritDoc} */
    @Override
    public void addParametersObserver (final IParametersAdjustObserver observer)
    {
        synchronized (this.observers)
        {
            this.observers.add (observer);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void removeParametersObserver (final IParametersAdjustObserver observer)
    {
        synchronized (this.observers)
        {
            this.observers.remove (observer);
        }
    }


    /** {@inheritDoc} */
    @Override
    public Set<IParametersAdjustObserver> removeParametersObservers ()
    {
        synchronized (this.observers)
        {
            final HashSet<IParametersAdjustObserver> copy = new HashSet<> (this.observers);
            copy.forEach (this::removeParametersObserver);
            return copy;
        }
    }


    /**
     * Notify all registered observers.
     */
    protected void notifyParametersObservers ()
    {
        synchronized (this.observers)
        {
            this.observers.forEach (IParametersAdjustObserver::parametersAdjusted);
        }
    }


    /**
     * Test if there are registered observers.
     *
     * @return True if observers are registered
     */
    protected boolean hasObservers ()
    {
        synchronized (this.observers)
        {
            return !this.observers.isEmpty ();
        }
    }
}
