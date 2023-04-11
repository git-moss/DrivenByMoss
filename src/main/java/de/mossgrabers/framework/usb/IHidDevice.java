// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.usb;

import de.mossgrabers.framework.daw.IMemoryBlock;


/**
 * Interface to a HID device.
 *
 * @author Jürgen Moßgraber
 */
public interface IHidDevice
{
    /**
     * Set the callback function to receive input.
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
     * @param reportID The report (= function/method) number
     * @param memoryBlock The memory block with the data to send
     * @return The number of bytes scheduled for transmission or -1 if the call failed
     */
    int sendOutputReport (byte reportID, IMemoryBlock memoryBlock);


    /**
     * This method sends a feature report to the device. See the USB HID specification for more
     * information. This method may or may not block. The method returning is no guarantee that the
     * data has been physically transmitted from the host to the device. The method returns the
     * actual number of bytes successfully scheduled to be sent to the device.
     *
     * @param reportID The report ID (= function/method) number
     * @param memoryBlock The memory block with the data to send
     * @return The number of bytes scheduled for transmission or -1 if the call failed
     */
    int sendFeatureReport (byte reportID, IMemoryBlock memoryBlock);


    /**
     * Closes the device.
     */
    void close ();
}
