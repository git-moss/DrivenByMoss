// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.usb;

import de.mossgrabers.bitwig.framework.daw.MemoryBlockImpl;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IMemoryBlock;
import de.mossgrabers.framework.usb.IUsbCallback;
import de.mossgrabers.framework.usb.IUsbEndpoint;

import com.bitwig.extension.controller.api.UsbInputPipe;
import com.bitwig.extension.controller.api.UsbOutputPipe;
import com.bitwig.extension.controller.api.UsbPipe;
import com.bitwig.extension.controller.api.UsbTransferDirection;


/**
 * Implementation for an USB end-point.
 *
 * @author Jürgen Moßgraber
 */
public class UsbEndpointImpl implements IUsbEndpoint
{
    private final IHost   host;
    private final UsbPipe endpoint;


    /**
     * Constructor.
     *
     * @param host The host for logging
     * @param pipe The Bitwig pipe (aka endpoint)
     */
    public UsbEndpointImpl (final IHost host, final UsbPipe pipe)
    {
        this.host = host;
        this.endpoint = pipe;
    }


    /** {@inheritDoc} */
    @Override
    public void send (final IMemoryBlock memoryBlock, final int timeout)
    {
        if (this.endpoint.direction () != UsbTransferDirection.OUT)
            return;

        try
        {
            ((UsbOutputPipe) this.endpoint).write (((MemoryBlockImpl) memoryBlock).memoryBlock (), timeout);
        }
        catch (final RuntimeException ex)
        {
            // Can only catch RuntimeException since it is a Bitwig internal Exception that is
            // thrown
            this.host.error ("Could not send USB memory block.", ex);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void sendAsync (final IMemoryBlock memoryBlock, final IUsbCallback callback, final int timeout)
    {
        if (this.endpoint.direction () != UsbTransferDirection.IN)
            return;

        try
        {
            ((UsbInputPipe) this.endpoint).readAsync (((MemoryBlockImpl) memoryBlock).memoryBlock (), callback::process, timeout);
        }
        catch (final RuntimeException ex)
        {
            // Can only catch RuntimeException since it is a Bitwig internal Exception that is
            // thrown
            this.host.error ("Could not read USB memory block.", ex);
        }
    }
}
