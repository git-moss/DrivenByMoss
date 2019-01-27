// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.controller;

import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IMemoryBlock;
import de.mossgrabers.framework.graphics.IBitmap;
import de.mossgrabers.framework.usb.IUsbDevice;
import de.mossgrabers.framework.usb.IUsbEndpoint;
import de.mossgrabers.framework.usb.UsbException;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Connects to the display of the Push 2 via USB.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PushUsbDisplay
{
    /** The size of the display content. */
    private static final int     DATA_SZ        = 20 * 0x4000;

    private static final int     TIMEOUT        = 1000;

    private static final byte [] DISPLAY_HEADER =
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

    private IUsbDevice           usbDevice;
    private IUsbEndpoint         usbEndpoint;
    private final IMemoryBlock   headerBlock;
    private final IMemoryBlock   imageBlock;
    private AtomicBoolean        isSending      = new AtomicBoolean (false);


    /**
     * Connect to the USB port and claim the display interface.
     *
     * @param host The controller host
     */
    public PushUsbDisplay (final IHost host)
    {
        try
        {
            this.usbDevice = host.getUsbDevice (0);
            this.usbEndpoint = this.usbDevice.getEndpoint (0, 0);
        }
        catch (final UsbException ex)
        {
            this.usbDevice = null;
            this.usbEndpoint = null;
            host.error ("Could not open USB output.");
        }

        this.headerBlock = host.createMemoryBlock (DISPLAY_HEADER.length);
        this.headerBlock.createByteBuffer ().put (DISPLAY_HEADER);
        this.imageBlock = host.createMemoryBlock (DATA_SZ);
    }


    /**
     * Send the buffered image to the screen.
     *
     * @param image An image of size 960 x 160 pixel
     */
    public void send (final IBitmap image)
    {
        if (this.usbDevice == null || this.usbEndpoint == null || this.isSending.get ())
            return;

        this.isSending.set (true);

        final ByteBuffer buffer = this.imageBlock.createByteBuffer ();

        image.encode ( (imageBuffer, width, height) -> {
            buffer.clear ();

            final int padding = (buffer.capacity () - height * width * 2) / height;

            for (int y = 0; y < height; y++)
            {
                for (int x = 0; x < width; x++)
                {
                    final int blue = imageBuffer.get ();
                    final int green = imageBuffer.get ();
                    final int red = imageBuffer.get ();
                    imageBuffer.get (); // Drop unused Alpha

                    final int pixel = sPixelFromRGB (red, green, blue);
                    buffer.put ((byte) (pixel & 0x00FF));
                    buffer.put ((byte) ((pixel & 0xFF00) >> 8));
                }

                for (int x = 0; x < padding; x++)
                    buffer.put ((byte) 0x00);
            }

            imageBuffer.rewind ();
        });

        this.usbEndpoint.send (this.headerBlock, TIMEOUT);
        this.usbEndpoint.send (this.imageBlock, TIMEOUT);
        this.isSending.set (false);
    }


    /**
     * Stops all transfers to the device. Nulls the device.
     */
    public void shutdown ()
    {
        this.usbDevice = null;
        this.usbEndpoint = null;
    }


    private static int sPixelFromRGB (final int red, final int green, final int blue)
    {
        int pixel = (blue & 0xF8) >> 3;
        pixel <<= 6;
        pixel += (green & 0xFC) >> 2;
        pixel <<= 5;
        pixel += (red & 0xF8) >> 3;
        return pixel;
    }
}
