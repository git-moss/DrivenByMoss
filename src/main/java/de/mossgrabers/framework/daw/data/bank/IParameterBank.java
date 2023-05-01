// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data.bank;

import de.mossgrabers.framework.parameter.IParameter;


/**
 * Interface to a parameter bank.
 *
 * @author Jürgen Moßgraber
 */
public interface IParameterBank extends IBank<IParameter>
{
    /**
     * Get the parameter page bank.
     *
     * @return The bank
     */
    IParameterPageBank getPageBank ();
}