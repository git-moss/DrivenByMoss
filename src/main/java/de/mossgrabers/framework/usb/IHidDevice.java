// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.usb;

/**
 * Interface to a HID device.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IHidDevice
{
    /**
     * Set the callback function to reveive input.
     *
     * @param callback The callback
     */
    void setCallback (final IHidCallback callback);


    /**
     * Sends an output report to the device. If numbered reports are used (see USB HID specification
     * for explanation about numbered reports) the reportID needs to be specified otherwise pass
     * zero there. This method may or may not block. The method returning is no guarantee that the
     * data has been physically transmitted from the host to the device. The method returns the
     * actual number of bytes successfully scheduled to be sent to the device.
     * 
     * @param reportID the report number if numbered reports are used else pass 0
     * @param data a byte array containing the data to be sent
     * @param length the number of bytes to send from the data array
     * @return number bytes actually sent or -1 if the send failed
     */
    int sendOutputReport (byte reportID, byte [] data, int length);


    /**
     * This method sends a feature report to the device. See USB HID specification to learn more
     * about feature reports. This method may or may not block. The method returning is no guarantee
     * that the data has been physically transmitted from the host to the device. The method returns
     * the actual number of bytes successfully scheduled to be sent to the device.
     * 
     * @param data a byte array containing the data to be sent
     * @param reportID a byte specifying the report ID to send
     * @param length the number of bytes to send from the data array
     * @return number bytes actually sent or -1 if the call failed
     * 
     */
    int sendFeatureReport (byte reportID, byte [] data, int length);
}
