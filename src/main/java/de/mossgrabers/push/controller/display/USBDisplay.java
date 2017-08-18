package de.mossgrabers.push.controller.display;

import com.bitwig.extension.api.Bitmap;
import com.bitwig.extension.controller.api.ControllerHost;

import java.nio.ByteBuffer;


/**
 * Connects to the display of the Push 2 via USB.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class USBDisplay
{
    /** The pixel width of the display. */
    private static final int     WIDTH          = 960;
    /** The pixel height of the display. */
    private static final int     HEIGHT         = 160;

    /** The size of the display content. */
    private static final int     DATA_SZ        = 20 * 0x4000;

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

    // private final UsbOut usbOut;
    private final ByteBuffer     headerBuffer;
    private final ByteBuffer     imageBuffer;


    /**
     * Connect to the USB port and claim the display interface.
     *
     * @param host The controller host
     */
    public USBDisplay (final ControllerHost host)
    {
        // this.usbOut = host.getUsbOut (0);

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
        // if (this.usbOut == null)
        // return;

        final ByteBuffer byteBuffer = image.getByteBuffer ();

        synchronized (this.imageBuffer)
        {
            this.imageBuffer.clear ();

            for (int y = 0; y < HEIGHT; y++)
            {
                for (int x = 0; x < WIDTH; x++)
                {
                    int index = 4 * (x + y * WIDTH);

                    final int blue = scaleTo256 (byteBuffer.get (index)) * 31 / 255;
                    final int green = scaleTo256 (byteBuffer.get (index + 1)) * 63 / 255;
                    final int red = scaleTo256 (byteBuffer.get (index + 2)) * 31 / 255;
                    // Note: Next byte is alpha value, which is not used

                    // 3b(low) green - 5b red / 5b blue - 3b (high) green, e.g. gggRRRRR BBBBBGGG
                    this.imageBuffer.put ((byte) ((green & 0x07) << 5 | red & 0x1F));
                    this.imageBuffer.put ((byte) ((blue & 0x1F) << 3 | (green & 0x38) >> 3));
                }

                for (int x = 0; x < 128; x++)
                    this.imageBuffer.put ((byte) 0x00);
            }

            // this.usbOut.write (this.headerBuffer);
            // this.usbOut.write (this.imageBuffer);
        }
    }


    /**
     * Scales values in the range of [-128 ... -1, 0 ... 127] to the range of [0 ... 255].
     *
     * @param b The value to scale
     * @return The scaled value
     */
    private static int scaleTo256 (final byte b)
    {
        return (b < 0 ? b + 128 : b);
    }
}
