// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data;

import de.mossgrabers.framework.daw.data.bank.IParameterBank;


/**
 * Interface to the Cursor Track.
 *
 * @author Jürgen Moßgraber
 */
public interface ICursorTrack extends ITrack, ICursor
{
    /**
     * Get the track wide parameter bank.
     *
     * @return The bank
     */
    IParameterBank getParameterBank ();
}