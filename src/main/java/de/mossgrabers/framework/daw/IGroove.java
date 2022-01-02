// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw;

import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.observer.IObserverManagement;


/**
 * Interface to the Groove.
 *
 * @author J&uuml;rgen Mo&szlig;graber
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