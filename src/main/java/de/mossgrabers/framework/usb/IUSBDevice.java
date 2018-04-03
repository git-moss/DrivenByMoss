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
     * @param interfaceNumber The interface number
     * @param endpointAddress The address of the endpoint
     * @return The endpoint
     */
    IUSBEndpoint createEndpoint (byte interfaceNumber, byte endpointAddress);


    /**
     * Release the USB device.
     */
    void release ();
}
