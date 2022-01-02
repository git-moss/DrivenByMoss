// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.parameterprovider.special;

import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.parameterprovider.IParameterProvider;


/**
 * Uses a sub-range of the parameters from the given parameter provider.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class RangeFilterParameterProvider extends AbstractWrapperProvider
{
    private final int startIndex;
    private final int length;


    /**
     * Constructor.
     *
     * @param parameterProvider The parameter provider
     * @param startIndex The index of the first parameters
     * @param length The number of parameters starting at the index
     */
    public RangeFilterParameterProvider (final IParameterProvider parameterProvider, final int startIndex, final int length)
    {
        super (parameterProvider);

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
        return this.parameterProvider.get (this.startIndex + index);
    }
}
