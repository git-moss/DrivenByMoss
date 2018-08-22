package de.mossgrabers.controller.kontrol.usb.mkii.controller;

import de.mossgrabers.framework.utils.StringUtils;

import java.nio.ByteBuffer;


public class Kontrol2DisplayProtocol
{
    public static void main (final String [] args)
    {
        final ByteBuffer img = ByteBuffer.allocate (10 * 10 * 4);
        for (int i = 0; i < 100; i++)
        {
            img.put ((byte) i);
            img.put ((byte) i);
            img.put ((byte) i);
            img.put ((byte) 0);
        }
        img.rewind ();

        final ByteBuffer data = Kontrol2DisplayProtocol.encodeImage (img, 1, 0, 0, 10, 10);
        System.out.println (StringUtils.toHexStr (data));
    }

    private static final int COMMAND_TRANSMIT_PIXEL = 0x0000;
    private static final int COMMAND_REPEAT_PIXEL   = 0x0100;
    private static final int COMMAND_SKIP_PIXEL     = 0x0200;
    private static final int COMMAND_BLIT           = 0x0300;
    private static final int COMMAND_START_OF_DATA  = 0x8400;
    private static final int COMMAND_END_OF_DATA    = 0x4000;

    ByteBuffer               buffer                 = ByteBuffer.allocate (1000);


    public static ByteBuffer encodeImage (final ByteBuffer image, final int display, final int x, final int y, final int width, final int height)
    {
        final Kontrol2DisplayProtocol protocol = new Kontrol2DisplayProtocol ();
        protocol.startOfData ((byte) display, (byte) x, (byte) y, (short) width, (short) height);
        protocol.writeImage (image);
        protocol.blit ();
        protocol.endOfData ((byte) display);
        final ByteBuffer b = protocol.getBuffer ();
        b.rewind ();
        return b;
    }


    public ByteBuffer getBuffer ()
    {
        return this.buffer;
    }


    void startOfData (final byte display, final byte x, final byte y, final short width, final short height)
    {
        this.buffer.putShort ((short) COMMAND_START_OF_DATA); // TODO
        this.buffer.put (display);
        this.buffer.put ((byte) 0x60);
        this.buffer.putShort ((short) 0);
        this.buffer.putShort ((short) 0);
        this.buffer.putShort ((short) 0);
        this.buffer.put (x);
        this.buffer.put (y);
        this.buffer.putShort (width);
        this.buffer.putShort (height);
    }


    void writeImage (final ByteBuffer image)
    {
        this.buffer.putShort ((short) COMMAND_TRANSMIT_PIXEL); // TODO

        final int length = image.capacity () / 4;

        this.buffer.putShort ((short) (length / 4));

        for (int i = 0; i < length; i++)
        {
            final byte red = image.get ();
            final byte green = image.get ();
            final byte blue = image.get ();
            image.get (); // Drop transparency

            final int color = red / 7 | green / 3 * 64 | blue / 7 * 2048;
            this.buffer.putShort ((short) color);
        }

        // buffer.putInt ((int)(image.byteCount()/4);
        // ushort *swappedData = reinterpret_cast<ushort *>(image.bits());
        // for (int i = 0; i < image.byteCount() / 2; i++)
        // {
        // swappedData[i] = _byteswap_ushort(swappedData[i]);
        // }
        // buffer.write(reinterpret_cast<const char*>(swappedData), image.byteCount());
    }


    void blit ()
    {
        this.buffer.putShort ((short) COMMAND_BLIT);
        this.buffer.putShort ((short) 0);

    }


    void endOfData (final byte display)
    {
        this.buffer.putShort ((short) COMMAND_END_OF_DATA);
        this.buffer.put (display);
        this.buffer.put ((byte) 0);
    }
}
