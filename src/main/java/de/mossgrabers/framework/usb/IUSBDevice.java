// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.usb;

/**
 * Interface to an USB device.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IUSBDevice
{
    /**
     * Create an endpoint for the USB device.
     * 
     * @param interfaceIndex The index of the registered interface
     * @param endpointIndex The index of the registered endpoint
     * @return The endpoint
     */
    IUSBEndpoint getEndpoint (final int interfaceIndex, final int endpointIndex);


    /**
     * Release the USB device.
     */
    void release ();
}
