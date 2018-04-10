// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.usb;

import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.usb.IUSBAsyncCallback;
import de.mossgrabers.framework.usb.IUSBEndpoint;

import com.bitwig.extension.controller.api.UsbEndpoint;
import com.bitwig.extension.controller.api.UsbTransferResult;
import com.bitwig.extension.controller.api.UsbTransferStatus;

import java.nio.ByteBuffer;


/**
 * Implementation for an USB endpoint.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class USBEndpointImpl implements IUSBEndpoint
{
    private IHost       host;
    private UsbEndpoint endpoint;


    /**
     * Constructor.
     *
     * @param host The host
     * @param endpoint The Bitwig endpoint
     */
    public USBEndpointImpl (final IHost host, final UsbEndpoint endpoint)
    {
        this.host = host;
        this.endpoint = endpoint;
    }


    /** {@inheritDoc} */
    @Override
    public void send (final ByteBuffer buffer, final int timeout)
    {
        final UsbTransferResult result = this.endpoint.bulkTransfer (buffer, timeout);
        final UsbTransferStatus status = result.status ();
        if (status != UsbTransferStatus.Completed)
            this.host.error ("USB transmission error: " + status);
    }


    /** {@inheritDoc} */
    @Override
    public void sendAsync (final ByteBuffer buffer, final IUSBAsyncCallback callback, final int timeout)
    {
        this.endpoint.asyncBulkTransfer (buffer, result -> {
            final UsbTransferStatus status = result.status ();
            if (status != UsbTransferStatus.Completed)
                this.host.error ("USB receive error: " + status);
            callback.process (status == UsbTransferStatus.Completed ? result.actualLength () : -1);
        }, timeout);
    }
}
