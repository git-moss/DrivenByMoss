package de.mossgrabers.controller.kontrol.usb.mkii.controller;

import de.mossgrabers.framework.utils.StringUtils;

import java.nio.ByteBuffer;


public class Kontrol2DisplayProtocol
{
    private static final byte COMMAND_TRANSMIT_PIXEL = (byte) 0x00;
    private static final byte COMMAND_REPEAT_PIXEL   = (byte) 0x01;

    private static final byte COMMAND_SKIP_PIXEL     = (byte) 0x02;
    private static final byte COMMAND_BLIT           = (byte) 0x03;
    private static final byte COMMAND_START_OF_DATA  = (byte) 0x84;
    private static final byte COMMAND_END_OF_DATA    = (byte) 0x40;

    private static final int  LENGTH_HEADER          = 16;
    private static final int  LENGTH_FOOTER          = 4;
    private static final int  LENGTH_BLIT            = 4;
    private static final int  LENGTH_SKIP_PIXELS     = 7;


    public static void encodeImage (final ByteBuffer buffer, final ByteBuffer data, final int display, final int x, final int y, final int width, final int height)
    {
        writeHeader (buffer, (byte) display, (short) x, (short) y, (short) width, (short) height);
        writeImage (buffer, data);
        blit (buffer);
        writeFooter (buffer, (byte) display);
        buffer.rewind ();
    }


    public static void fill (final ByteBuffer buffer, final ByteBuffer data, final int display, final int x, final int y, final int width, final int height)
    {
        writeHeader (buffer, (byte) display, (short) x, (short) y, (short) width, (short) height);
        
        buffer.put (COMMAND_REPEAT_PIXEL);
        buffer.put ((byte) 0x80);
        buffer.put ((byte) 0x00);
        buffer.put ((byte) 0x20);
        buffer.put ((byte) 0x20);
        
        
        blit (buffer);
        writeFooter (buffer, (byte) display);
        buffer.rewind ();
    }


    public static void writeHeader (final ByteBuffer buffer, final byte display, final short x, final short y, final short width, final short height)
    {
        buffer.put (COMMAND_START_OF_DATA);
        buffer.put ((byte) 0x00);
        buffer.put (display);
        buffer.put ((byte) 0x60);
        buffer.putShort ((short) 0);
        buffer.putShort ((short) 0);
        // Offset X-Axis (16-bit)
        buffer.putShort ((short) x);
        // Offset Y-Axis (16-bit)
        buffer.putShort ((short) y);
        // Width (16 bit) - max 480 Pixel (= "01 E0")
        buffer.putShort ((short) width);
        // Height (16 bit) - max 270 Pixel
        buffer.putShort ((short) height);
    }


    public static void writeImage (final ByteBuffer buffer, final ByteBuffer data)
    {
        final int length = data.limit () / 3;
        int i = 0;
        int rest = length;
        while (rest > 0)
        {
            skipPixel (buffer);
            i += addPixels (buffer, data, rest);
            rest = length - i;
        }
    }


    public static void writeImage2 (final ByteBuffer buffer, final ByteBuffer data)
    {
        final int length = data.limit () / 3;
        int i = 0;
        int rest = length;
        buffer.put (COMMAND_TRANSMIT_PIXEL);
        while (rest > 0)
        {
            // skipPixel (buffer);
            i += addPixels (buffer, data, rest);
            rest = length - i;
        }
    }


    public static void blit (final ByteBuffer buffer)
    {
        buffer.put (COMMAND_BLIT);
        buffer.put ((byte) 0x00);
        buffer.putShort ((short) 0x00);
    }


    public static void writeFooter (final ByteBuffer buffer, final byte display)
    {
        buffer.put (COMMAND_END_OF_DATA);
        buffer.put ((byte) 0x00);
        buffer.put (display);
        buffer.put ((byte) 0x00);
    }


    public static void skipPixel (final ByteBuffer buffer)
    {
        buffer.put (COMMAND_SKIP_PIXEL);
        buffer.putShort ((short) 0x00);
        // TODO numbers of pixels to skip
        buffer.putShort ((short) 0x00);
        buffer.putShort ((short) 0x00);
    }


    private static void convertPixel (ByteBuffer buffer, ByteBuffer data)
    {
        int red = Byte.toUnsignedInt (data.get ());
        int green = Byte.toUnsignedInt (data.get ());
        int blue = Byte.toUnsignedInt (data.get ());
        int pixel = ((red * 0x1F / 0xFF) << 11) + ((green * 0x3F / 0xFF) << 5) + (blue * 0x1F / 0xFF);

        // Bytes need to be swapped
        buffer.put ((byte) ((pixel & 0xFF00) >> 8));
        buffer.put ((byte) (pixel & 0x00FF));
    }


    private static int addPixels (final ByteBuffer buffer, final ByteBuffer data, final int rest)
    {
        final int bytesToAdd = Math.min (rest, 22);

        buffer.put ((byte) (bytesToAdd / 2));
        for (int j = 0; j < bytesToAdd; j++)
            convertPixel (buffer, data);
        return bytesToAdd;
    }
}
