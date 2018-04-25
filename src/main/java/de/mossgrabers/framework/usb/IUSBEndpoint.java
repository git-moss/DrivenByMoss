// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.usb;

import com.bitwig.extension.controller.api.UsbTransferException;

import java.nio.ByteBuffer;


/**
 * Interface to an USB endpoint.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IUSBEndpoint
{
    /**
     * Send data to the endpoint.
     *
     * @param buffer The buffer with the data to send
     * @param timeout Timeout for the sending task
     */
    void send (ByteBuffer buffer, int timeout) throws UsbTransferException;


    /**
     * Send data asynchroneously to the endpoint.
     *
     * @param buffer The buffer with the data to send
     * @param callback Callback when the sending has finished
     * @param timeout Timeout for the sending task
     */
    void sendAsync (ByteBuffer buffer, IUSBAsyncCallback callback, int timeout);
}
