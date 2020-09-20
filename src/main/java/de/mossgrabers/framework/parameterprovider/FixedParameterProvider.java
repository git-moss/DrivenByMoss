// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.parameterprovider;

import de.mossgrabers.framework.daw.data.IParameter;


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
}
