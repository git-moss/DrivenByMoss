// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data;

/**
 * Description of a device.
 *
 * @author Jürgen Moßgraber
 */
public interface IDeviceMetadata
{
    /**
     * Get the name of the device.
     *
     * @return The name
     */
    String name ();


    /**
     * Get the long name of the device for identification (e.g. adds type).
     *
     * @return The full name
     */
    String fullName ();
}
