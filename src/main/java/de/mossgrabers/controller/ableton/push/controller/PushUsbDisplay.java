// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.controller;

import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IMemoryBlock;
import de.mossgrabers.framework.graphics.IBitmap;
import de.mossgrabers.framework.usb.IUsbDevice;
import de.mossgrabers.framework.usb.IUsbEndpoint;
import de.mossgrabers.framework.usb.UsbException;

import java.nio.ByteBuffer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * Connects to the display of the Push 2 via USB.
 *
 * @author Jürgen Moßgraber
 */
public class PushUsbDisplay
{
    /** The size of the display content. */
    private static final int               DATA_SZ          = 20 * 0x4000;

    private static final int               TIMEOUT          = 1000;

    private static final byte []           DISPLAY_HEADER   =
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

    private IUsbDevice                     usbDevice;
    private IUsbEndpoint                   usbEndpoint;
    private final IHost                    host;
    private final IMemoryBlock             headerBlock;
    private final IMemoryBlock             imageBlock;
    private final byte []                  byteStore        = new byte [DATA_SZ];

    private final Object                   sendLock         = new Object ();
    private final Object                   bufferUpdateLock = new Object ();
    private final ScheduledExecutorService sendExecutor     = Executors.newSingleThreadScheduledExecutor ();


    /**
     * Connect to the USB port and claim the display interface.
     *
     * @param host The controller host
     */
    public PushUsbDisplay (final IHost host)
    {
        this.host = host;

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
        // Copy to the buffer
        synchronized (this.bufferUpdateLock)
        {
            image.encode ( (imageBuffer, width, height) -> {

                int counter = 0;
                final int padding = (DATA_SZ - height * width * 2) / height;

                for (int y = 0; y < height; y++)
                {
                    for (int x = 0; x < width; x++)
                    {
                        final int blue = imageBuffer.get ();
                        final int green = imageBuffer.get ();
                        final int red = imageBuffer.get ();
                        imageBuffer.get (); // Drop unused Alpha

                        final int pixel = sPixelFromRGB (red, green, blue);

                        this.byteStore[counter] = (byte) (pixel & 0x00FF);
                        this.byteStore[counter + 1] = (byte) ((pixel & 0xFF00) >> 8);

                        counter += 2;
                    }

                    for (int x = 0; x < padding; x++)
                    {
                        this.byteStore[counter] = (byte) 0x00;
                        counter++;
                    }
                }

                imageBuffer.rewind ();
            });
        }

        synchronized (this.sendLock)
        {
            if (!this.sendExecutor.isShutdown ())
                this.sendExecutor.submit (this::sendData);
        }
    }


    private void sendData ()
    {
        // Copy the data from the buffer to the USB block
        synchronized (this.bufferUpdateLock)
        {
            final ByteBuffer buffer = this.imageBlock.createByteBuffer ();
            buffer.clear ();
            for (int i = 0; i < DATA_SZ; i++)
                buffer.put (this.byteStore[i]);
        }

        // Send the data
        synchronized (this.sendLock)
        {
            if (this.usbDevice == null || this.usbEndpoint == null)
                return;

            this.usbEndpoint.send (this.headerBlock, TIMEOUT);
            this.usbEndpoint.send (this.imageBlock, TIMEOUT);
        }
    }


    /**
     * Stops all transfers to the device. Nulls the device.
     */
    public void shutdown ()
    {
        synchronized (this.sendLock)
        {
            this.usbDevice = null;
            this.usbEndpoint = null;

            this.sendExecutor.shutdown ();
            try
            {
                if (!this.sendExecutor.awaitTermination (5, TimeUnit.SECONDS))
                    this.host.error ("USB Send executor did not end in 5 seconds.");
            }
            catch (final InterruptedException ex)
            {
                this.host.error ("USB Send executor interrupted.", ex);
                Thread.currentThread ().interrupt ();
            }
        }
    }


    /**
     * Check if the send executor is shutdown.
     *
     * @return True if shutdown
     */
    public boolean isShutdown ()
    {
        return this.sendExecutor.isShutdown ();
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
