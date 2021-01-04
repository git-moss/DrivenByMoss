// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.parameterprovider;

import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.daw.data.ResetParameter;
import de.mossgrabers.framework.observer.IParametersAdjustObserver;


/**
 * Get a number of parameters. This implementation encapsulates the parameters of a another
 * parameter provider to call reset.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ResetParameterProvider implements IParameterProvider
{
    private final IParameterProvider parameterProvider;


    /**
     * Constructor.
     *
     * @param parameterProvider The parameter provider
     */
    public ResetParameterProvider (final IParameterProvider parameterProvider)
    {
        this.parameterProvider = parameterProvider;
    }


    /** {@inheritDoc} */
    @Override
    public int size ()
    {
        return this.parameterProvider.size ();
    }


    /**
     * Throws a FrameworkException if the bank does not contain parameters.
     *
     * {@inheritDoc}
     */
    @Override
    public IParameter get (final int index)
    {
        return new ResetParameter (this.parameterProvider.get (index));
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
    public void notifyParametersObservers ()
    {
        this.parameterProvider.notifyParametersObservers ();
    }
}
