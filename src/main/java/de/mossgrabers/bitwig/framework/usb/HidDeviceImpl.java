// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.usb;

import de.mossgrabers.framework.usb.IHidCallback;
import de.mossgrabers.framework.usb.IHidDevice;
import de.mossgrabers.framework.usb.UsbException;
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
     * @throws UsbException Could not lookup or open the device
     */
    public HidDeviceImpl (final short vendorID, final short productID) throws UsbException
    {
        final HidDeviceInfo hidDeviceInfo = lookupDevice (vendorID, productID);
        if (hidDeviceInfo == null)
            throw new UsbException ("Could not find HID device: Vendor ID: " + vendorID + ", Product ID: " + productID);
        try
        {
            this.hidDevice = PureJavaHidApi.openDevice (hidDeviceInfo);
            this.isOpen = true;
            this.hidDevice.setDeviceRemovalListener (source -> this.isOpen = false);
        }
        catch (final IOException ex)
        {
            throw new UsbException ("Could not open HID device: Vendor ID: " + vendorID + ", Product ID: " + productID, ex);
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
    public int sendOutputReport (byte reportID, byte [] data, int length)
    {
        if (this.isOpen)
            return this.hidDevice.setOutputReport (reportID, data, length);
        return -1;
    }


    /** {@inheritDoc} */
    @Override
    public int sendFeatureReport (byte reportID, byte [] data, int length)
    {
        if (this.isOpen)
            return this.hidDevice.setFeatureReport (reportID, data, length);
        return -1;
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
