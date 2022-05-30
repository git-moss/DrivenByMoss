// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.parameterprovider.special;

import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.daw.data.empty.EmptyParameter;
import de.mossgrabers.framework.parameterprovider.AbstractParameterProvider;
import de.mossgrabers.framework.parameterprovider.IParameterProvider;


/**
 * Combines several parameter providers into one.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class CombinedParameterProvider extends AbstractParameterProvider
{
    private final IParameterProvider [] providers;
    private final int                   overallSize;


    /**
     * Constructor.
     *
     * @param providers The parameter providers to combine
     */
    public CombinedParameterProvider (final IParameterProvider... providers)
    {
        this.providers = providers;

        int size = 0;
        for (final IParameterProvider provider: this.providers)
        {
            provider.addParametersObserver (this::observerCallback);
            size += provider.size ();
        }

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


    private void observerCallback ()
    {
        this.notifyParametersObservers ();
    }
}
