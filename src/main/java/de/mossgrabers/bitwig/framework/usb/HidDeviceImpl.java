// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.usb;

import de.mossgrabers.framework.daw.IMemoryBlock;
import de.mossgrabers.framework.usb.IHidCallback;
import de.mossgrabers.framework.usb.IHidDevice;
import de.mossgrabers.framework.usb.UsbException;
import de.mossgrabers.framework.utils.OperatingSystem;
import purejavahidapi.HidDevice;
import purejavahidapi.HidDeviceInfo;
import purejavahidapi.PureJavaHidApi;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Optional;


/**
 * Implementation for a HID device.
 *
 * @author Jürgen Moßgraber
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
        final Optional<HidDeviceInfo> hidDeviceInfo = lookupDevice (vendorID, productID);
        if (hidDeviceInfo.isEmpty ())
            throw new UsbException ("Could not find HID device: Vendor ID: " + vendorID + ", Product ID: " + productID);
        try
        {
            this.hidDevice = PureJavaHidApi.openDevice (hidDeviceInfo.get ());
            if (this.hidDevice == null)
                throw new IOException ("openDevice returned null.");
            this.isOpen = true;
            this.hidDevice.setDeviceRemovalListener (source -> this.isOpen = false);
        }
        catch (final IOException ex)
        {
            throw new UsbException ("Could not open HID device: Vendor ID: " + vendorID + ", Product ID: " + productID, ex);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void close ()
    {
        if (this.isOpen)
        {
            this.isOpen = false;
            this.hidDevice.close ();
        }
    }


    /** {@inheritDoc} */
    @Override
    public int sendOutputReport (final byte reportID, final IMemoryBlock memoryBlock)
    {
        if (!this.isOpen)
            return -1;
        final byte [] data = toBuffer (memoryBlock);

        // purehid documentation says otherwise but MAC also needs the report ID in
        // data[0], therefore add it
        byte [] d = data;
        int l = data.length;
        if (OperatingSystem.isMacOS ())
        {
            l++;
            d = new byte [l];
            d[0] = reportID;
            System.arraycopy (data, 0, d, 1, data.length);
        }

        return this.hidDevice.setOutputReport (reportID, d, d.length);
    }


    /** {@inheritDoc} */
    @Override
    public int sendFeatureReport (final byte reportID, final IMemoryBlock memoryBlock)
    {
        if (!this.isOpen)
            return -1;
        final byte [] data = toBuffer (memoryBlock);
        return this.hidDevice.setFeatureReport (reportID, data, data.length);
    }


    /** {@inheritDoc} */
    @Override
    public void setCallback (final IHidCallback callback)
    {
        if (!this.isOpen)
            return;
        this.hidDevice.setInputReportListener ( (source, id, data, length) -> {

            // purehid documentation says otherwise but MAC also contains the report ID in
            // data[0], therefore remove it
            byte [] d = data;
            int l = length;
            if (OperatingSystem.isMacOS ())
            {
                l--;
                d = new byte [l];
                System.arraycopy (data, 1, d, 0, l);
            }

            callback.process (id, d, l);
        });
    }


    private static Optional<HidDeviceInfo> lookupDevice (final short vendorID, final short productID)
    {
        for (final HidDeviceInfo info: PureJavaHidApi.enumerateDevices ())
        {
            if (info.getVendorId () == vendorID && info.getProductId () == productID)
                return Optional.of (info);
        }
        return Optional.empty ();
    }


    private static byte [] toBuffer (final IMemoryBlock memoryBlock)
    {
        final ByteBuffer buffer = memoryBlock.createByteBuffer ();
        final int size = buffer.capacity ();
        final byte [] data = new byte [size];
        buffer.rewind ();
        buffer.get (data);
        return data;
    }
}
