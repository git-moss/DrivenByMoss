// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.usb;

import java.nio.ByteBuffer;

import com.bitwig.extension.controller.api.UsbEndpoint;

import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.usb.IUSBAsyncCallback;
import de.mossgrabers.framework.usb.IUSBEndpoint;

/**
 * Implementation for an USB endpoint.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class USBEndpointImpl implements IUSBEndpoint
{
   private IHost host;

   private UsbEndpoint endpoint;

   /**
    * Constructor.
    *
    * @param host
    *           The host
    * @param endpoint
    *           The Bitwig endpoint
    */
   public USBEndpointImpl(final IHost host, final UsbEndpoint endpoint)
   {
      this.host = host;
      this.endpoint = endpoint;
   }

   /** {@inheritDoc} */
   @Override
   public void send(final ByteBuffer buffer, final int timeout)
   {
      this.endpoint.bulkTransfer(buffer, timeout);
   }

   /** {@inheritDoc} */
   @Override
   public void sendAsync(final ByteBuffer buffer, final IUSBAsyncCallback callback, final int timeout)
   {
      this.endpoint.asyncBulkTransfer(buffer, amountTransferred -> {
         callback.process(amountTransferred);
      }, timeout);
   }
}
