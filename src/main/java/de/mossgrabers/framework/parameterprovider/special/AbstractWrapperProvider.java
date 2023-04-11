// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.parameterprovider.special;

import de.mossgrabers.framework.observer.IParametersAdjustObserver;
import de.mossgrabers.framework.parameterprovider.IParameterProvider;

import java.util.Set;


/**
 * Abstract base class for providers which wrap another provider instance.
 *
 * @author Jürgen Moßgraber
 */
public abstract class AbstractWrapperProvider implements IParameterProvider
{
    protected final IParameterProvider parameterProvider;


    /**
     * Constructor.
     *
     * @param parameterProvider The parameter provider
     */
    protected AbstractWrapperProvider (final IParameterProvider parameterProvider)
    {
        this.parameterProvider = parameterProvider;
    }


    /** {@inheritDoc} */
    @Override
    public int size ()
    {
        return this.parameterProvider.size ();
    }


    /** {@inheritDoc} */
    @Override
    public void addParametersObserver (final IParametersAdjustObserver observer)
    {
        this.parameterProvider.addParametersObserver (observer);
    }


    /** {@inheritDoc} */
    @Override
    public void removeParametersObserver (final IParametersAdjustObserver observer)
    {
        this.parameterProvider.removeParametersObserver (observer);
    }


    /** {@inheritDoc} */
    @Override
    public Set<IParametersAdjustObserver> removeParametersObservers ()
    {
        return this.parameterProvider.removeParametersObservers ();
    }
}
