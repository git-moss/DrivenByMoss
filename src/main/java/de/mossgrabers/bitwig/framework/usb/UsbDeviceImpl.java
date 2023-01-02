// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.usb;

import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.usb.IHidDevice;
import de.mossgrabers.framework.usb.IUsbDevice;
import de.mossgrabers.framework.usb.IUsbEndpoint;
import de.mossgrabers.framework.usb.UsbException;

import com.bitwig.extension.controller.api.UsbDevice;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Implementation for an USB device.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class UsbDeviceImpl implements IUsbDevice
{
    private static final Pattern PATTERN = Pattern.compile ("idVendor == 0x(\\p{XDigit}+) && idProduct == 0x(\\p{XDigit}+)");

    private final UsbDevice      usbDevice;
    private final IHost          host;


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
    public IUsbEndpoint getEndpoint (final int interfaceIndex, final int endpointIndex) throws UsbException
    {
        try
        {
            return new UsbEndpointImpl (this.host, this.usbDevice.iface (interfaceIndex).pipe (endpointIndex));
        }
        catch (final RuntimeException ex)
        {
            throw new UsbException ("Could not lookup or open the endpoint.", ex);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void release ()
    {
        // This is automatically handled by the Bitwig framework
    }


    /** {@inheritDoc} */
    @Override
    public Optional<IHidDevice> getHidDevice () throws UsbException
    {
        final String expression = this.usbDevice.deviceMatcher ().getExpression ();
        // Parse like "idVendor == 0x17CC && idProduct == 0x1610"
        final Matcher matcher = PATTERN.matcher (expression);
        if (!matcher.matches ())
            return Optional.empty ();

        final short vendorID = Short.parseShort (matcher.group (1), 16);
        final short productID = Short.parseShort (matcher.group (2), 16);
        return Optional.of (new HidDeviceImpl (vendorID, productID));
    }
}
