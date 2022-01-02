// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.parameterprovider.special;

import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.parameterprovider.AbstractParameterProvider;
import de.mossgrabers.framework.parameterprovider.IParameterProvider;


/**
 * Combines two parameter providers into one.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class CombinedParameterProvider extends AbstractParameterProvider
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

        this.first.addParametersObserver (this::observerCallback);
        this.second.addParametersObserver (this::observerCallback);
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


    private void observerCallback ()
    {
        this.notifyParametersObservers ();
    }
}
