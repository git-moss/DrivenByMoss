// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.parameterprovider;

import de.mossgrabers.framework.daw.data.IParameter;


/**
 * Interface to get a number of parameters.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IParameterProvider
{
    /**
     * Get the number of available parameters.
     *
     * @return The number of available parameters
     */
    int size ();


    /**
     * Get a parameter
     *
     * @param index The index of the parameter
     * @return The parameter
     */
    IParameter get (int index);
}
