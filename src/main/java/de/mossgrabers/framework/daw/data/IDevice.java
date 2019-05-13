// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data;

/**
 * Interface to a device.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IDevice extends IItem
{
    /**
     * Get the identifier of the device if any.
     *
     * @return The identifier, returns an empty string if none, never null
     */
    String getID ();


    /**
     * Delete the channel.
     */
    void remove ();


    /**
     * Duplicate the channel.
     */
    void duplicate ();
}