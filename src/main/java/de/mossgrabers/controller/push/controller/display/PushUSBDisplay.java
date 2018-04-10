// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.controller.display;

import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.graphics.IBitmap;
import de.mossgrabers.framework.usb.IUSBDevice;
import de.mossgrabers.framework.usb.IUSBEndpoint;

import java.nio.ByteBuffer;


/**
 * Connects to the display of the Push 2 via USB.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PushUSBDisplay
{
    /** The size of the display content. */
    private static final int     DATA_SZ          = 20 * 0x4000;

    private static final int     TIMEOUT          = 1000;

    /** Push 2 USB Interface for the display. */
    private static final byte    INTERFACE_NUMBER = 0;
    /** Push 2 USB display endpoint. */
    private static final byte    ENDPOINT_ADDRESS = (byte) 0x01;

    private static final byte [] DISPLAY_HEADER   =
    {
        (byte) 0xef,
        (byte) 0xcd,
        (byte) 0xab,
        (byte) 0x89,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0
    };

    private IUSBDevice           usbDevice;
    private IUSBEndpoint         usbEndpoint;
    private final ByteBuffer     headerBuffer;
    private final ByteBuffer     imageBuffer;

    private boolean              isSending        = false;


    /**
     * Connect to the USB port and claim the display interface.
     *
     * @param host The controller host
     */
    public PushUSBDisplay (final IHost host)
    {
        try
        {
            this.usbDevice = host.getUsbDevice (0);
            this.usbEndpoint = this.usbDevice.createEndpoint (INTERFACE_NUMBER, ENDPOINT_ADDRESS);
        }
        catch (final RuntimeException ex)
        {
            this.usbDevice = null;
            this.usbEndpoint = null;
            host.error ("Could not open USB output.");
        }

        this.headerBuffer = host.createByteBuffer (DISPLAY_HEADER.length);
        this.headerBuffer.put (DISPLAY_HEADER);
        this.imageBuffer = host.createByteBuffer (DATA_SZ);
    }


    /**
     * Send the buffered image to the screen.
     *
     * @param image An image of size 960 x 160 pixel
     */
    public void send (final IBitmap image)
    {
        if (this.usbDevice == null || this.usbEndpoint == null || this.isSending)
            return;

        this.isSending = true;

        image.fillTransferBuffer (this.imageBuffer);
        this.usbEndpoint.send (this.headerBuffer, TIMEOUT);
        this.usbEndpoint.send (this.imageBuffer, TIMEOUT);

        this.isSending = false;
    }


    /**
     * Stops all transfers to the device. Nulls the device.
     */
    public void shutdown ()
    {
        this.usbDevice = null;
        this.usbEndpoint = null;
    }
}
