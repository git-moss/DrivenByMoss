// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.parameterprovider;

import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.observer.IBankPageObserver;
import de.mossgrabers.framework.observer.IParametersAdjustObserver;


/**
 * Combines two parameter providers into one.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class CombinedParameterProvider implements IParameterProvider, IBankPageObserver
{
    private final IParameterProvider first;
    private final IParameterProvider second;


    /**
     * Constructor.
     *
     * @param first The first parameter provider
     * @param second The second parameter provider
     */
    public CombinedParameterProvider (final IParameterProvider first, final IParameterProvider second)
    {
        this.first = first;
        this.second = second;
    }


    /** {@inheritDoc} */
    @Override
    public int size ()
    {
        return this.first.size () + this.second.size ();
    }


    /**
     * Throws a FrameworkException if the bank does not contain parameters.
     *
     * {@inheritDoc}
     */
    @Override
    public IParameter get (final int index)
    {
        final int size = this.first.size ();
        return index < size ? this.first.get (index) : this.second.get (index - size);
    }


    /** {@inheritDoc} */
    @Override
    public void addParametersObserver (final IParametersAdjustObserver observer)
    {
        this.first.addParametersObserver (observer);
        this.second.addParametersObserver (observer);
    }


    /** {@inheritDoc} */
    @Override
    public void removeParametersObserver (final IParametersAdjustObserver observer)
    {
        this.first.removeParametersObserver (observer);
        this.second.removeParametersObserver (observer);
    }


    /** {@inheritDoc} */
    @Override
    public void notifyParametersObservers ()
    {
        this.first.notifyParametersObservers ();
        this.second.notifyParametersObservers ();
    }


    /** {@inheritDoc} */
    @Override
    public void pageAdjusted ()
    {
        this.notifyParametersObservers ();
    }
}
