// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw;

import de.mossgrabers.framework.observer.IObserverManagement;
import de.mossgrabers.framework.parameter.IParameter;


/**
 * Interface to the Groove.
 *
 * @author Jürgen Moßgraber
 */
public interface IGroove extends IObserverManagement
{
    /**
     * Get all groove parameters.
     *
     * @param id The ID of the parameter to get
     * @return The groove parameter, might be null if not supported by the implementation
     */
    IParameter getParameter (GrooveParameterID id);


    /**
     * Sets indication for all groove parameters.
     *
     * @param enable True to enable
     */
    void setIndication (boolean enable);
}