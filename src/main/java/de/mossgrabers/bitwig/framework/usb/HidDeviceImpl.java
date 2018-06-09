// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.usb;

import de.mossgrabers.framework.usb.IHidCallback;
import de.mossgrabers.framework.usb.IHidDevice;
import purejavahidapi.HidDevice;
import purejavahidapi.HidDeviceInfo;
import purejavahidapi.PureJavaHidApi;

import java.io.IOException;


/**
 * Implementation for a HID device.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class HidDeviceImpl implements IHidDevice
{
    private HidDevice hidDevice;
    private boolean   isOpen;


    /**
     * Constructor.
     *
     * @param vendorID The vendor ID
     * @param productID The product ID
     */
    public HidDeviceImpl (final short vendorID, final short productID)
    {
        final HidDeviceInfo hidDeviceInfo = lookupDevice (vendorID, productID);
        if (hidDeviceInfo == null)
            throw new RuntimeException ("Could not find HID device: Vendor ID: " + vendorID + ", Product ID: " + productID);
        try
        {
            this.hidDevice = PureJavaHidApi.openDevice (hidDeviceInfo);
            this.isOpen = true;
            this.hidDevice.setDeviceRemovalListener (source -> this.isOpen = false);
        }
        catch (final IOException ex)
        {
            throw new RuntimeException ("Could not open HID device: Vendor ID: " + vendorID + ", Product ID: " + productID, ex);
        }
    }


    /**
     * Closes the device.
     */
    public void close ()
    {
        if (this.isOpen)
            this.hidDevice.close ();
    }


    /** {@inheritDoc} */
    @Override
    public void setCallback (final IHidCallback callback)
    {
        if (this.isOpen)
            this.hidDevice.setInputReportListener ( (source, id, data, length) -> callback.process (data, length));
    }


    private static HidDeviceInfo lookupDevice (final short vendorID, final short productID)
    {
        for (HidDeviceInfo info: PureJavaHidApi.enumerateDevices ())
        {
            if (info.getVendorId () == vendorID && info.getProductId () == productID)
                return info;
        }
        return null;
    }
}
