// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.usb;

import de.mossgrabers.bitwig.framework.daw.MemoryBlockImpl;
import de.mossgrabers.framework.daw.IMemoryBlock;
import de.mossgrabers.framework.usb.IUSBAsyncCallback;
import de.mossgrabers.framework.usb.IUSBEndpoint;

import com.bitwig.extension.controller.api.UsbOutputPipe;


/**
 * Implementation for an USB endpoint.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class USBEndpointImpl implements IUSBEndpoint
{
    private UsbOutputPipe endpoint;


    /**
     * Constructor.
     *
     * @param pipe The Bitwig pipe (aka endpoint)
     */
    public USBEndpointImpl (final UsbOutputPipe pipe)
    {
        this.endpoint = pipe;
    }


    /** {@inheritDoc} */
    @Override
    public void send (final IMemoryBlock memoryBlock, final int timeout)
    {
        this.endpoint.write (((MemoryBlockImpl) memoryBlock).getMemoryBlock (), timeout);
    }


    /** {@inheritDoc} */
    @Override
    public void sendAsync (final IMemoryBlock memoryBlock, final IUSBAsyncCallback callback, final int timeout)
    {
        this.endpoint.writeAsync (((MemoryBlockImpl) memoryBlock).getMemoryBlock (), callback::process, timeout);
    }
}
