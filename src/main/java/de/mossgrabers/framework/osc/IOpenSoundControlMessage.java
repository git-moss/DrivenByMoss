// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.osc;

/**
 * Interface to an OSC message.
 *
 * @author Jürgen Moßgraber
 */
public interface IOpenSoundControlMessage
{
    /**
     * Get the OSC address.
     *
     * @return The OSC address
     */
    String getAddress ();


    /**
     * Get the values array.
     *
     * @return The values array
     */
    Object [] getValues ();
}
