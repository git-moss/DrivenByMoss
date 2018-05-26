// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.usb;

import de.mossgrabers.framework.usb.IUSBAsyncCallback;
import de.mossgrabers.framework.usb.IUSBEndpoint;

import com.bitwig.extension.controller.api.UsbPipe;

import java.nio.ByteBuffer;


/**
 * Implementation for an USB endpoint.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class USBEndpointImpl implements IUSBEndpoint
{
    private UsbPipe endpoint;


    /**
     * Constructor.
     *
     * @param pipe The Bitwig pipe (aka endpoint)
     */
    public USBEndpointImpl (final UsbPipe pipe)
    {
        this.endpoint = pipe;
    }


    /** {@inheritDoc} */
    @Override
    public void send (final ByteBuffer buffer, final int timeout)
    {
        this.endpoint.transfer (buffer, timeout);
    }


    /** {@inheritDoc} */
    @Override
    public void sendAsync (final ByteBuffer buffer, final IUSBAsyncCallback callback, final int timeout)
    {
        this.endpoint.asyncTransfer (buffer, callback::process, timeout);
    }
}
