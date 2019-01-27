// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw;

import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.observer.ObserverManagement;


/**
 * Interace to the Groove.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IGroove extends ObserverManagement
{
    /**
     * Get all groove parameters.
     *
     * @return The groove parameters
     */
    IParameter [] getParameters ();


    /**
     * Sets indication for all groove parameters.
     *
     * @param enable True to enable
     */
    void setIndication (final boolean enable);
}