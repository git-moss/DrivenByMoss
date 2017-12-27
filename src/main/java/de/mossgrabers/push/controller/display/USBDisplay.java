// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.controller.display;

import com.bitwig.extension.api.Bitmap;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.UsbDevice;
import com.bitwig.extension.controller.api.UsbEndpoint;
import com.bitwig.extension.controller.api.UsbTransferCallback;
import com.bitwig.extension.controller.api.UsbTransferStatus;

import java.nio.ByteBuffer;


/**
 * Connects to the display of the Push 2 via USB.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class USBDisplay
{
    /** The pixel width of the display. */
    private static final int     WIDTH            = 960;
    /** The pixel height of the display. */
    private static final int     HEIGHT           = 160;

    /** The size of the display content. */
    private static final int     DATA_SZ          = 20 * 0x4000;

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

    private UsbDevice            usbDevice;
    private UsbEndpoint          usbEndpoint;
    private final ByteBuffer     headerBuffer;
    private final ByteBuffer     imageBuffer;
    private ControllerHost       host;


    /**
     * Connect to the USB port and claim the display interface.
     *
     * @param host The controller host
     */
    public USBDisplay (final ControllerHost host)
    {
        this.host = host;

        try
        {
            this.usbDevice = host.getUsbDevice (0);
            this.usbEndpoint = this.usbDevice.createEndpoint (INTERFACE_NUMBER, ENDPOINT_ADDRESS);
        }
        catch (final RuntimeException ex)
        {
            this.usbDevice = null;
            this.usbEndpoint = null;
            host.errorln ("Could not open USB output.");
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
    public void send (final Bitmap image)
    {
        if (this.usbDevice == null)
            return;

        synchronized (this.imageBuffer)
        {
            this.imageBuffer.clear ();

            final ByteBuffer byteBuffer = image.getByteBuffer ();
            for (int y = 0; y < HEIGHT; y++)
            {
                for (int x = 0; x < WIDTH; x++)
                {
                    final int blue = byteBuffer.get ();
                    final int green = byteBuffer.get ();
                    final int red = byteBuffer.get ();
                    byteBuffer.get (); // Drop unused Alpha

                    final int pixel = SPixelFromRGB (red, green, blue);
                    this.imageBuffer.put ((byte) (pixel & 0x00FF));
                    this.imageBuffer.put ((byte) ((pixel & 0xFF00) >> 8));
                }

                for (int x = 0; x < 128; x++)
                    this.imageBuffer.put ((byte) 0x00);
            }

            byteBuffer.rewind ();

            UsbTransferCallback callback = new UsbTransferCallback ()
            {
                @Override
                public void transferCompleted (UsbTransferStatus status)
                {
                    host.println (status.toString ());
                }
            };
            this.usbEndpoint.bulkTransfer (this.headerBuffer, callback);
            this.usbEndpoint.bulkTransfer (this.imageBuffer, callback);
        }
    }


    private static int SPixelFromRGB (final int r, final int g, final int b)
    {
        int pixel = (b & 0xF8) >> 3;
        pixel <<= 6;
        pixel += (g & 0xFC) >> 2;
        pixel <<= 5;
        pixel += (r & 0xF8) >> 3;
        return pixel;
    }
}
