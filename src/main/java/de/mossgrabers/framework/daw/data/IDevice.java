// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data;

/**
 * Interface to a device.
 *
 * @author Jürgen Moßgraber
 */
public interface IDevice extends IItem
{
    /**
     * Returns true if the device is enabled.
     *
     * @return True if the device is enabled
     */
    boolean isEnabled ();


    /**
     * Toggle the device on/off.
     */
    void toggleEnabledState ();


    /**
     * Delete the device.
     */
    void remove ();


    /**
     * Duplicate the device.
     */
    void duplicate ();
}