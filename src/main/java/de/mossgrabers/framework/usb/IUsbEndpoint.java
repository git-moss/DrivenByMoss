// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.usb;

import de.mossgrabers.framework.daw.IMemoryBlock;


/**
 * Interface to an USB end-point.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IUsbEndpoint
{
    /**
     * Send data to the end-point.
     *
     * @param memoryBlock The memory block with the data to send
     * @param timeout Timeout for the sending task
     */
    void send (IMemoryBlock memoryBlock, int timeout);


    /**
     * Send data asynchronously to the end-point.
     *
     * @param memoryBlock The memory block with the data to send
     * @param callback Callback when the sending has finished
     * @param timeout Timeout for the sending task
     */
    void sendAsync (IMemoryBlock memoryBlock, IUsbCallback callback, int timeout);
}
