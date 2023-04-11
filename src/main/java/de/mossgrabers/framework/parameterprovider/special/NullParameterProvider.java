// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.parameterprovider.special;

import de.mossgrabers.framework.parameter.IParameter;


/**
 * Get a number of null parameters. This can be used to clear a previous parameter provider to
 * activate a command based binding.
 *
 * @author Jürgen Moßgraber
 */
public class NullParameterProvider extends FixedParameterProvider
{
    /**
     * Constructor.
     *
     * @param size The number of null-parameters to provide
     */
    public NullParameterProvider (final int size)
    {
        super (new IParameter [size]);
    }
}
