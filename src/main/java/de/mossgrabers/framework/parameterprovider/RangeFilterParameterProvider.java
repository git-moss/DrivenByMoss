// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.parameterprovider;

import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.observer.IParametersAdjustObserver;


/**
 * Uses sub-range of the parameters from the given parameter provider.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class RangeFilterParameterProvider implements IParameterProvider
{
    private final IParameterProvider provider;
    private final int                startIndex;
    private final int                length;


    /**
     * Constructor.
     *
     * @param provider The parameter provider
     * @param startIndex The index of the first parameters
     * @param length The number of parameters starting at the index
     */
    public RangeFilterParameterProvider (final IParameterProvider provider, final int startIndex, final int length)
    {
        this.provider = provider;
        this.startIndex = startIndex;
        this.length = length;
    }


    /** {@inheritDoc} */
    @Override
    public int size ()
    {
        return this.length;
    }


    /**
     * Throws a FrameworkException if the bank does not contain parameters.
     *
     * {@inheritDoc}
     */
    @Override
    public IParameter get (final int index)
    {
        return this.provider.get (this.startIndex + index);
    }


    /** {@inheritDoc} */
    @Override
    public void addParametersObserver (final IParametersAdjustObserver observer)
    {
        this.provider.addParametersObserver (observer);
    }


    /** {@inheritDoc} */
    @Override
    public void removeParametersObserver (final IParametersAdjustObserver observer)
    {
        this.provider.removeParametersObserver (observer);
    }


    /** {@inheritDoc} */
    @Override
    public void notifyParametersObservers ()
    {
        this.provider.notifyParametersObservers ();
    }
}
