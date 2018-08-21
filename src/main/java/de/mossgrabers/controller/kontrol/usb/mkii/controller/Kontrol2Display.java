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
    private static final int            DISPLAY_WIDTH      = 480;
    private static final int            DISPLAY_HEIGHT     = 360;
    private static final int            DISPLAY_WIDTH_LINE = DISPLAY_WIDTH * 4;

    private static final byte []        BLOCK_HEADER       =
    {
        (byte) 0x02,
        (byte) 0x00,
        (byte) 0x00,
        (byte) 0x00,
        (byte) 0x00,
        (byte) 0x00,
        (byte) 0x00
    };

    private static final byte []        FOOTER             =
    {
        (byte) 0x02,
        (byte) 0x00,
        (byte) 0x00,
        (byte) 0x01,
        (byte) 0x03,
        (byte) 0x00,
        (byte) 0x00,
        (byte) 0x00,
        (byte) 0x40,
        (byte) 0x00,
        (byte) 0x00,
        (byte) 0x00
    };

    // One pixel is 16 bit
    private static final int            NUM_OF_PIXELS      = 2 * DISPLAY_WIDTH * DISPLAY_HEIGHT;
    private static final int            DATA_SZ            = FOOTER.length + NUM_OF_PIXELS + (NUM_OF_PIXELS / 6) * (BLOCK_HEADER.length + 1);

    private final Kontrol2UsbDevice     usbDevice;
    private final Kontrol2DisplayHeader header0;
    private final Kontrol2DisplayHeader header1;
    private final IMemoryBlock          imageBlock0;
    private final IMemoryBlock          imageBlock1;
    private final AtomicBoolean         isSending          = new AtomicBoolean (false);


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

        this.header0 = new Kontrol2DisplayHeader (host, true);
        this.header1 = new Kontrol2DisplayHeader (host, false);

        final IGraphicsDimensions dimensions = new DefaultGraphicsDimensions (2 * DISPLAY_WIDTH, DISPLAY_HEIGHT);
        this.virtualDisplay = new VirtualDisplay (host, this.model, configuration, dimensions, "Kontrol mkII Display");

        this.imageBlock0 = host.createMemoryBlock (DATA_SZ);
        this.imageBlock1 = host.createMemoryBlock (DATA_SZ);
    }


    /** {@inheritDoc} */
    @Override
    protected void send (final IBitmap image)
    {
        if (this.usbDevice == null || this.isSending.get ())
            return;

        this.isSending.set (true);

        final ByteBuffer buffer0 = this.imageBlock0.createByteBuffer ();
        final ByteBuffer buffer1 = this.imageBlock1.createByteBuffer ();

        image.encode ( (imageBuffer, width, height) -> {
            buffer0.clear ();
            buffer1.clear ();

            for (int i = 0; i < imageBuffer.capacity (); i += 4)
            {
                if (i / DISPLAY_WIDTH_LINE == 0)
                {
                    if (i % 24 == 0)
                    {
                        buffer0.put (BLOCK_HEADER);
                        buffer0.put ((byte) 0x06);
                    }
                    buffer0.put (imageBuffer.get ());
                    buffer0.put (imageBuffer.get ());
                    buffer0.put (imageBuffer.get ());
                    imageBuffer.get (); // Drop transparency
                }
                else
                {
                    if (i % 24 == 0)
                    {
                        buffer1.put (BLOCK_HEADER);
                        buffer1.put ((byte) 0x06);
                    }
                    buffer1.put (imageBuffer.get ());
                    buffer1.put (imageBuffer.get ());
                    buffer1.put (imageBuffer.get ());
                    imageBuffer.get (); // Drop transparency
                }
            }
            buffer0.put (FOOTER);
            buffer1.put (FOOTER);

            imageBuffer.rewind ();
        });

        this.usbDevice.sendToDisplay (this.header0.getMemoryBlock ());
        this.usbDevice.sendToDisplay (this.imageBlock0);
        this.usbDevice.sendToDisplay (this.header1.getMemoryBlock ());
        this.usbDevice.sendToDisplay (this.imageBlock1);

        this.isSending.set (false);
    }
}