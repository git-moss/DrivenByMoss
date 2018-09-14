// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.usb.mkii.controller;

import java.nio.ByteBuffer;


/**
 * Implements the display protocol for the Kontrol 2.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Kontrol2DisplayProtocol
{
    /** The size of the header in bytes. */
    public static final int   LENGTH_HEADER          = 16;
    /** The size of the footer in bytes. */
    public static final int   LENGTH_FOOTER          = 4;
    /** The size of the blit command in bytes. */
    public static final int   LENGTH_BLIT            = 4;
    /** The size of the skip pixels command in bytes. */
    public static final int   LENGTH_SKIP_PIXELS     = 7;

    private static final byte COMMAND_TRANSMIT_PIXEL = (byte) 0x00;
    private static final byte COMMAND_REPEAT_PIXEL   = (byte) 0x01;
    private static final byte COMMAND_SKIP_PIXEL     = (byte) 0x02;
    private static final byte COMMAND_BLIT           = (byte) 0x03;
    private static final byte COMMAND_START_OF_DATA  = (byte) 0x84;
    private static final byte COMMAND_END_OF_DATA    = (byte) 0x40;


    /**
     * Fill a rectangle with pixels.
     *
     * @param buffer Where to add the data bytes of the command and data codes
     * @param data The pixel data. Each pixel is 3 bytes: the red component of the color (0-255),
     *            the green component of the color (0-255), the blue component of the color (0-255)
     * @param display The display (0 or 1)
     * @param x The X offset of the rectangle (max. 479)
     * @param y The Y offset of the rectangle (max. 271)
     * @param width The width of the rectangle (max. 480)
     * @param height The height of the rectangle (max. 272)
     */
    public static void pixelRectangle (final ByteBuffer buffer, final ByteBuffer data, final int display, final int x, final int y, final int width, final int height)
    {
        writeHeader (buffer, (byte) display, (short) x, (short) y, (short) width, (short) height);
        transmitPixel (buffer, data);
        blit (buffer);
        writeFooter (buffer, (byte) display);
    }


    /**
     * Fill a rectangle on one of the displays with a specific color.
     *
     * @param buffer Where to add the data bytes of the command and data codes
     * @param display The display (0 or 1)
     * @param x The X offset of the rectangle (max. 479)
     * @param y The Y offset of the rectangle (max. 271)
     * @param width The width of the rectangle (max. 480)
     * @param height The height of the rectangle (max. 272)
     * @param red The red component of the fill color (0-255)
     * @param green The green component of the fill color (0-255)
     * @param blue The blue component of the fill color (0-255)
     */
    public static void fillRectangle (final ByteBuffer buffer, final int display, final int x, final int y, final int width, final int height, final int red, final int green, final int blue)
    {
        writeHeader (buffer, (byte) display, (short) x, (short) y, (short) width, (short) height);
        repeatPixel (buffer, (short) (width * height / 2), red, green, blue, red, green, blue);
        blit (buffer);
        writeFooter (buffer, (byte) display);
        buffer.rewind ();
    }


    /**
     * Clears a rectangle on one of the displays (fills it with black pixels).
     *
     * @param buffer Where to add the data bytes of the command and data codes
     * @param display The display (0 or 1)
     * @param x The X offset of the rectangle (max. 479)
     * @param y The Y offset of the rectangle (max. 271)
     * @param width The width of the rectangle (max. 480)
     * @param height The height of the rectangle (max. 272)
     */
    public static void clearRectangle (final ByteBuffer buffer, final int display, final int x, final int y, final int width, final int height)
    {
        writeHeader (buffer, (byte) display, (short) x, (short) y, (short) width, (short) height);
        repeatPixel (buffer, (short) (width * height / 2), 0, 0, 0, 0, 0, 0);
        blit (buffer);
        writeFooter (buffer, (byte) display);
        buffer.rewind ();
    }


    /**
     * Write a display protocol header to the given buffer.
     *
     * @param buffer Where to add the header
     * @param display The display (0 or 1)
     * @param x The X offset of the rectangle (max. 479)
     * @param y The Y offset of the rectangle (max. 271)
     * @param width The width of the rectangle (max. 480)
     * @param height The height of the rectangle (max. 272)
     */
    public static void writeHeader (final ByteBuffer buffer, final byte display, final short x, final short y, final short width, final short height)
    {
        if (x > 479 || y > 271 || width > 480 || height > 272)
            throw new IllegalArgumentException ("Rectangle is out of bounds.");

        buffer.put (COMMAND_START_OF_DATA);
        buffer.put ((byte) 0x00);
        buffer.put (display);
        buffer.put ((byte) 0x60);
        buffer.putShort ((short) 0);
        buffer.putShort ((short) 0);
        // Offset X-Axis (16-bit)
        buffer.putShort (x);
        // Offset Y-Axis (16-bit)
        buffer.putShort (y);
        // Width (16 bit) - max 480 Pixel (= "01 E0")
        buffer.putShort (width);
        // Height (16 bit) - max 270 Pixel
        buffer.putShort (height);
    }


    /**
     * Set several pixels at the current pixel output position.
     *
     * @param buffer The buffer where to add the encoded command
     * @param data Reads 3 byte from the data for each pixel: the red component of the color
     *            (0-255), the green component of the color (0-255), the blue component of the color
     *            (0-255)
     */
    public static void transmitPixel (final ByteBuffer buffer, final ByteBuffer data)
    {
        buffer.put (COMMAND_TRANSMIT_PIXEL);

        final int length = data.limit () / 3;
        final int l = length / 2;
        buffer.put ((byte) (l >> 16));
        buffer.putShort ((short) (l & 0x0000FFFF));
        for (int i = 0; i < length; i++)
            encodeColor565 (buffer, data);
    }


    /**
     * Flush all drawing commands.
     *
     * @param buffer The buffer where to add the encoded command
     */
    public static void blit (final ByteBuffer buffer)
    {
        buffer.put (COMMAND_BLIT);
        buffer.put ((byte) 0x00);
        buffer.putShort ((short) 0x00);
    }


    /**
     * Write the footer (end of data).
     *
     * @param buffer The buffer where to add the encoded data
     * @param display The display (0/1)
     */
    public static void writeFooter (final ByteBuffer buffer, final byte display)
    {
        buffer.put (COMMAND_END_OF_DATA);
        buffer.put ((byte) 0x00);
        buffer.putShort ((short) 0x00);
    }


    /**
     * Moves the pixel draw position in x- and y-direction relative to the current one.
     *
     * @param buffer The buffer where to add the encoded data
     * @param skipX The number of pixels to skip in x-direction
     * @param skipY The number of pixels to skip in y-direction
     */
    public static void skipPixel (final ByteBuffer buffer, final int skipX, final int skipY)
    {
        buffer.put (COMMAND_SKIP_PIXEL);

        buffer.put ((byte) (skipX >> 16));
        buffer.putShort ((short) (skipX & 0x0000FFFF));
        buffer.put ((byte) (skipY >> 16));
        buffer.putShort ((short) (skipY & 0x0000FFFF));
    }


    /**
     * Draws a pixel tuple n-th times in the current rectangle.
     *
     * @param buffer The buffer where to add the encoded data
     * @param noOfRepetitions The number of times to repeat the pixel
     * @param red1 The red component of the first pixel color
     * @param green1 The green component of the first pixel color
     * @param blue1 The blue component of the first pixel color
     * @param red2 The red component of the second pixel color
     * @param green2 The green component of the second pixel color
     * @param blue2 The blue component of the second pixel color
     */
    public static void repeatPixel (final ByteBuffer buffer, final short noOfRepetitions, final int red1, final int green1, final int blue1, final int red2, final int green2, final int blue2)
    {
        buffer.put (COMMAND_REPEAT_PIXEL);

        // This is the MSB of the length but the display is not that large (maximum is 0xFD20)
        buffer.put ((byte) 0x00);
        buffer.putShort (noOfRepetitions);

        encodeColor565 (buffer, red1, green1, blue1);
        encodeColor565 (buffer, red2, green2, blue2);
    }


    /**
     * Encodes the pixel data (3 bytes: red, green, blue) into 16 bit rgb565. The first byte
     * contains the number of added pixels. Encodes up to 22 pixels.
     *
     * @param buffer The buffer where to add the encoded data
     * @param data Reads 3 byte from the data for each pixel: the red component of the color
     *            (0-255), the green component of the color (0-255), the blue component of the color
     *            (0-255)
     * @param rest The number of pixels left to encode
     * @return The number of pixels left to encode
     */
    public static int addPixels (final ByteBuffer buffer, final ByteBuffer data, final int rest)
    {
        final int bytesToAdd = Math.min (rest, 22);

        buffer.put ((byte) (bytesToAdd / 2));
        for (int j = 0; j < bytesToAdd; j++)
            encodeColor565 (buffer, data);
        return bytesToAdd;
    }


    /**
     * Converts a color to RGB 5-6-5 format. 5 bit red, 6 bit green, 5 bit blue (16 bit).
     *
     * @param buffer The buffer where to add the encoded color
     * @param data Reads 3 byte from the data: the red component of the color (0-255), the green
     *            component of the color (0-255), the blue component of the color (0-255)
     */
    private static void encodeColor565 (final ByteBuffer buffer, final ByteBuffer data)
    {
        final int red = Byte.toUnsignedInt (data.get ());
        final int green = Byte.toUnsignedInt (data.get ());
        final int blue = Byte.toUnsignedInt (data.get ());
        encodeColor565 (buffer, red, green, blue);
    }


    /**
     * Converts a color to RGB 5-6-5 format. 5 bit red, 6 bit green, 5 bit blue (16 bit).
     *
     * @param buffer The buffer where to add the encoded color
     * @param red The red component of the color (0-255)
     * @param green The green component of the color (0-255)
     * @param blue The blue component of the color (0-255)
     */
    private static void encodeColor565 (final ByteBuffer buffer, final int red, final int green, final int blue)
    {
        final int pixel = (red * 0x1F / 0xFF << 11) + (green * 0x3F / 0xFF << 5) + blue * 0x1F / 0xFF;

        // Bytes need to be swapped
        buffer.put ((byte) ((pixel & 0xFF00) >> 8));
        buffer.put ((byte) (pixel & 0x00FF));
    }
}
