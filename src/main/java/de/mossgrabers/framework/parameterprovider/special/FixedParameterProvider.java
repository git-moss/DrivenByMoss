// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.parameterprovider.special;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.observer.IParametersAdjustObserver;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.parameterprovider.IParameterProvider;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;


/**
 * Get a number of parameters. This implementation provides a fixed number of parameters.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class FixedParameterProvider implements IParameterProvider
{
    private final IParameter [] parameters;


    /**
     * Constructor.
     *
     * @param parameters The fixed parameters
     */
    public FixedParameterProvider (final IParameter... parameters)
    {
        this.parameters = parameters;
    }


    /**
     * Constructor.
     *
     * @param parameters The fixed parameters
     */
    public FixedParameterProvider (final List<IParameter> parameters)
    {
        this.parameters = parameters.toArray (new IParameter [parameters.size ()]);
    }


    /** {@inheritDoc} */
    @Override
    public int size ()
    {
        return this.parameters.length;
    }


    /**
     * Throws a FrameworkException if the bank does not contain parameters.
     *
     * {@inheritDoc}
     */
    @Override
    public IParameter get (final int index)
    {
        return this.parameters[index];
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
    public Set<IParametersAdjustObserver> removeParametersObservers ()
    {
        return Collections.emptySet ();
    }


    /** {@inheritDoc} */
    @Override
    public Optional<ColorEx> getColor (final int index)
    {
        return Optional.empty ();
    }
}
