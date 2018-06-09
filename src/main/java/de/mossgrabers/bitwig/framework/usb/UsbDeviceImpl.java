// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.usb;

import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.usb.IHidDevice;
import de.mossgrabers.framework.usb.IUsbDevice;
import de.mossgrabers.framework.usb.IUsbEndpoint;

import com.bitwig.extension.controller.api.UsbDevice;


/**
 * Implementation for an USB device.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class UsbDeviceImpl implements IUsbDevice
{
    private UsbDevice usbDevice;
    private IHost     host;


    /**
     * Constructor.
     *
     * @param host The host for logging
     * @param usbDevice The Bitwig USB device
     */
    public UsbDeviceImpl (final IHost host, final UsbDevice usbDevice)
    {
        this.host = host;
        this.usbDevice = usbDevice;
    }


    /** {@inheritDoc} */
    @Override
    public IUsbEndpoint getEndpoint (final int interfaceIndex, final int endpointIndex)
    {
        return new UsbEndpointImpl (this.host, this.usbDevice.iface (interfaceIndex).pipe (endpointIndex));
    }


    /** {@inheritDoc} */
    @Override
    public void release ()
    {
        // This is automatically handled by the Bitwig framework
    }


    /** {@inheritDoc} */
    @Override
    public IHidDevice getHidDevice ()
    {
        final String expression = this.usbDevice.deviceMatcher ().getExpression ();
        // TODO parse from expression
        final short vendorID = 0x17cc;
        final short productID = 0x1340;
        return new HidDeviceImpl (vendorID, productID);
    }
}
