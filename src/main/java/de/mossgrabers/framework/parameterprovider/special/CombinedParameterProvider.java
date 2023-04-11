// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.parameterprovider.special;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.data.empty.EmptyParameter;
import de.mossgrabers.framework.observer.IParametersAdjustObserver;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.parameterprovider.IParameterProvider;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;


/**
 * Combines several parameter providers into one.
 *
 * @author Jürgen Moßgraber
 */
public class CombinedParameterProvider implements IParameterProvider
{
    private final List<IParameterProvider> providers;
    private final int                      overallSize;


    /**
     * Constructor.
     *
     * @param providers The parameter providers to combine
     */
    public CombinedParameterProvider (final IParameterProvider... providers)
    {
        this (Arrays.asList (providers));
    }


    /**
     * Constructor.
     *
     * @param providers The parameter providers to combine
     */
    public CombinedParameterProvider (final List<IParameterProvider> providers)
    {
        this.providers = providers;

        int size = 0;
        for (final IParameterProvider provider: this.providers)
            size += provider.size ();
        this.overallSize = size;
    }


    /** {@inheritDoc} */
    @Override
    public int size ()
    {
        return this.overallSize;
    }


    /**
     * Throws a FrameworkException if the bank does not contain parameters.
     *
     * {@inheritDoc}
     */
    @Override
    public IParameter get (final int index)
    {
        int pos = index;
        for (final IParameterProvider provider: this.providers)
        {
            final int size = provider.size ();
            if (pos < size)
                return provider.get (pos);
            pos -= size;
        }
        return EmptyParameter.INSTANCE;
    }


    /** {@inheritDoc} */
    @Override
    public Optional<ColorEx> getColor (final int index)
    {
        int pos = index;
        for (final IParameterProvider provider: this.providers)
        {
            final int size = provider.size ();
            if (pos < size)
                return provider.getColor (pos);
            pos -= size;
        }
        return Optional.empty ();
    }


    /** {@inheritDoc} */
    @Override
    public void addParametersObserver (final IParametersAdjustObserver observer)
    {
        for (final IParameterProvider provider: this.providers)
            provider.addParametersObserver (observer);
    }


    /** {@inheritDoc} */
    @Override
    public void removeParametersObserver (final IParametersAdjustObserver observer)
    {
        for (final IParameterProvider provider: this.providers)
            provider.removeParametersObserver (observer);
    }


    /** {@inheritDoc} */
    @Override
    public Set<IParametersAdjustObserver> removeParametersObservers ()
    {
        final Set<IParametersAdjustObserver> observers = new HashSet<> ();
        for (final IParameterProvider provider: this.providers)
            observers.addAll (provider.removeParametersObservers ());
        return observers;
    }
}
