// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
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
     * Delete the channel.
     */
    void remove ();


    /**
     * Duplicate the channel.
     */
    void duplicate ();
}