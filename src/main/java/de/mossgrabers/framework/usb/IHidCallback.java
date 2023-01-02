// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.usb;

/**
 * Interface for a callback when data from a HID device is received.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IHidCallback
{
    /**
     * Called when ready to process the results.
     *
     * @param reportID The report (= function/method) number
     * @param data The received data
     * @param length The length of the received data, -1 if an error occurred
     */
    void process (byte reportID, byte [] data, int length);
}
