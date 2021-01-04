// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.parameterprovider;

import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.daw.data.empty.EmptyParameter;
import de.mossgrabers.framework.observer.IParametersAdjustObserver;


/**
 * Get a number of empty parameters.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class EmptyParameterProvider implements IParameterProvider
{
    private final int size;


    /**
     * Constructor.
     *
     * @param size The number of parameters
     */
    public EmptyParameterProvider (final int size)
    {
        this.size = size;
    }


    /** {@inheritDoc} */
    @Override
    public int size ()
    {
        return this.size;
    }


    /**
     * Throws a FrameworkException if the bank does not contain parameters.
     *
     * {@inheritDoc}
     */
    @Override
    public IParameter get (final int index)
    {
        return EmptyParameter.INSTANCE;
    }


    /** {@inheritDoc} */
    @Override
    public void addParametersObserver (final IParametersAdjustObserver observer)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void removeParametersObserver (final IParametersAdjustObserver observer)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void notifyParametersObservers ()
    {
        // Intentionally empty
    }
}
