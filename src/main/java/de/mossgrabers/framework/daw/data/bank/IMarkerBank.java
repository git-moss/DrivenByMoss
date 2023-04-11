// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data.bank;

import de.mossgrabers.framework.daw.data.IMarker;


/**
 * Interface to a marker bank.
 *
 * @author Jürgen Moßgraber
 */
public interface IMarkerBank extends IBank<IMarker>
{
    /**
     * Create a new marker at the current play cursor position.
     */
    void addMarker ();
}