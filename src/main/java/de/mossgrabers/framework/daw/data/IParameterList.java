// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data;

import java.util.List;

import de.mossgrabers.framework.parameter.IParameter;


/**
 * Interface to a parameter list.
 *
 * @author Jürgen Moßgraber
 */
public interface IParameterList
{
    /**
     * Get the maximum number of parameters which can be in the list.
     *
     * @return The maximum number of parameters, always a multiple of 8
     */
    int getMaxNumberOfParameters ();


    /**
     * Get the parameter page list.
     *
     * @return The list
     */
    List<IParameter> getParameters ();
}