// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.parameterprovider.special;

import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.daw.data.ResetParameter;
import de.mossgrabers.framework.parameterprovider.IParameterProvider;


/**
 * Get a number of parameters. This implementation encapsulates the parameters of a another
 * parameter provider to call reset.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ResetParameterProvider extends AbstractWrapperProvider
{
    /**
     * Constructor.
     *
     * @param parameterProvider The parameter provider
     */
    public ResetParameterProvider (final IParameterProvider parameterProvider)
    {
        super (parameterProvider);
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
}
