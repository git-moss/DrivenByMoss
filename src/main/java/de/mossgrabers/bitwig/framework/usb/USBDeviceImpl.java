// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.usb;

import de.mossgrabers.framework.usb.IUSBDevice;
import de.mossgrabers.framework.usb.IUSBEndpoint;

import com.bitwig.extension.controller.api.UsbDevice;


/**
 * Implementation for an USB device.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class USBDeviceImpl implements IUSBDevice
{
    private UsbDevice usbDevice;


    /**
     * Constructor.
     *
     * @param usbDevice The Bitwig USB device
     */
    public USBDeviceImpl (final UsbDevice usbDevice)
    {
        this.usbDevice = usbDevice;
    }


    /** {@inheritDoc} */
    @Override
    public IUSBEndpoint getEndpoint (final int interfaceIndex, final int endpointIndex)
    {
        return new USBEndpointImpl (this.usbDevice.iface (interfaceIndex).pipe (endpointIndex));
    }


    /** {@inheritDoc} */
    @Override
    public void release ()
    {
        // This is automatically handled by the Bitwig framework
    }
}
