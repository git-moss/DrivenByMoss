// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.usb.mkii.controller;

import de.mossgrabers.controller.kontrol.usb.mkii.Kontrol2Configuration;
import de.mossgrabers.framework.controller.display.GraphicDisplay;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IMemoryBlock;
import de.mossgrabers.framework.graphics.IBitmap;
import de.mossgrabers.framework.graphics.IGraphicsDimensions;
import de.mossgrabers.framework.graphics.display.VirtualDisplay;
import de.mossgrabers.framework.graphics.grid.DefaultGraphicsDimensions;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * The display of Kontrol 2.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Kontrol2Display extends GraphicDisplay
{
    private static final int        DISPLAY_WIDTH      = 480;
    private static final int        DISPLAY_HEIGHT     = 272;
    private static final int        DISPLAY_WIDTH_LINE = DISPLAY_WIDTH * 2;

    // One pixel is 16 bit
    private static final int        NUM_OF_PIXELS      = DISPLAY_WIDTH * DISPLAY_HEIGHT;
    private static final int        DATA_SZ            = 28 + 2 * NUM_OF_PIXELS;

    private final Kontrol2UsbDevice usbDevice;
    private final IMemoryBlock      imageBlock0;
    private final IMemoryBlock      imageBlock1;
    private final ByteBuffer        data0              = ByteBuffer.allocateDirect (3 * NUM_OF_PIXELS);
    private final ByteBuffer        data1              = ByteBuffer.allocateDirect (3 * NUM_OF_PIXELS);
    private final AtomicBoolean     isSending          = new AtomicBoolean (false);


    /**
     * Constructor.
     *
     * @param host The host
     * @param configuration The configuration
     * @param usbDevice The USB device
     */
    public Kontrol2Display (final IHost host, final Kontrol2Configuration configuration, final Kontrol2UsbDevice usbDevice)
    {
        super (host);
        this.usbDevice = usbDevice;

        final IGraphicsDimensions dimensions = new DefaultGraphicsDimensions (DISPLAY_WIDTH_LINE, DISPLAY_HEIGHT);
        this.virtualDisplay = new VirtualDisplay (host, this.model, configuration, dimensions, "Kontrol mkII Display");

        this.imageBlock0 = host.createMemoryBlock (32); // DATA_SZ);
        this.imageBlock1 = host.createMemoryBlock (32); // DATA_SZ);
    }


    /** {@inheritDoc} */
    @Override
    protected void send (final IBitmap image)
    {
        if (this.usbDevice == null || this.isSending.get ())
            return;

        this.isSending.set (true);

        // host.println ("Lock set");
        //
        // image.encode ( (imageBuffer, width, height) -> {
        //
        // host.println ("Draw start");
        //
        // this.data0.clear ();
        // this.data1.clear ();
        //
        // final int pixels = imageBuffer.capacity () / 4;
        // for (int i = 0; i < pixels; i++)
        // {
        // final ByteBuffer b = i % DISPLAY_WIDTH_LINE < DISPLAY_WIDTH ? this.data0 : this.data1;
        //
        // final byte red = imageBuffer.get ();
        // final byte green = imageBuffer.get ();
        // final byte blue = imageBuffer.get ();
        // imageBuffer.get (); // Drop transparency
        //
        // b.put (red);
        // b.put (green);
        // b.put (blue);
        //
        // // TODO
        // // this.host.error (this.data0.position () + ":" + this.data1.position ());
        // }
        //
        // host.println ("Draw stop");
        //
        // imageBuffer.rewind ();
        //
        // host.println ("Image reset");
        //
        // });
        //
        // host.println ("start send");
        //
        // this.data0.rewind ();
        // this.data1.rewind ();

        // ByteBuffer data = ByteBuffer.allocateDirect (DISPLAY_WIDTH * DISPLAY_HEIGHT * 3 * 2);
        // for (int i = 0; i < 2 * DISPLAY_WIDTH * DISPLAY_HEIGHT; i++)
        // {
        // data.put ((byte) 255);
        // data.put ((byte) 0);
        // data.put ((byte) 0);
        // }
        // data.rewind ();
        //

        for (int i = 0; i < 10; i++)
        {
            final ByteBuffer buffer0 = this.imageBlock0.createByteBuffer ();
            buffer0.clear ();
            // Kontrol2DisplayProtocol.pixelRectangle (buffer0, data, 0, 0, 0, DISPLAY_WIDTH,
            // DISPLAY_HEIGHT);

            Kontrol2DisplayProtocol.fillRectangle (buffer0, 0, 0, i, DISPLAY_WIDTH, 1, 255, 0, 255);
            this.usbDevice.sendToDisplay (this.imageBlock0);
        }

        final ByteBuffer buffer1 = this.imageBlock1.createByteBuffer ();
        buffer1.clear ();
        Kontrol2DisplayProtocol.fillRectangle (buffer1, 1, 0, 0, DISPLAY_WIDTH, DISPLAY_HEIGHT, 255, 255, 0);
        this.usbDevice.sendToDisplay (this.imageBlock1);

        // Kontrol2DisplayProtocol.pixelRectangle (buffer1, this.data1, 1, 0, 0, DISPLAY_WIDTH,
        // DISPLAY_HEIGHT);

        host.println ("Send done");

        this.isSending.set (false);
        host.println ("Lock released");
    }
}