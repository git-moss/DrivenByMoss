// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.graphics;

import de.mossgrabers.framework.graphics.IBitmap;
import de.mossgrabers.framework.graphics.IRenderer;

import com.bitwig.extension.api.graphics.Bitmap;

import java.nio.ByteBuffer;


/**
 * Implementation of a bitmap.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class BitmapImpl implements IBitmap
{
    private Bitmap bitmap;


    /**
     * Constructor.
     *
     * @param bitmap The Bitwig bitmap
     */
    public BitmapImpl (final Bitmap bitmap)
    {
        this.bitmap = bitmap;
    }


    /** {@inheritDoc} */
    @Override
    public void setDisplayWindowTitle (final String title)
    {
        this.bitmap.setDisplayWindowTitle (title);
    }


    /** {@inheritDoc} */
    @Override
    public void showDisplayWindow ()
    {
        this.bitmap.showDisplayWindow ();
    }


    /** {@inheritDoc} */
    @Override
    public void render (final IRenderer renderer)
    {
        this.bitmap.render (gc -> {
            renderer.render (new GraphicsContextImpl (gc));
        });
    }


    /** {@inheritDoc} */
    @Override
    public void fillTransferBuffer (final ByteBuffer buffer)
    {
        buffer.clear ();

        final int capacity = buffer.capacity ();
        final int height = this.bitmap.getHeight ();
        final int width = this.bitmap.getWidth ();
        final int padding = (capacity - height * width * 2) / height;

        final ByteBuffer byteBuffer = this.bitmap.getMemoryBlock ().createByteBuffer ();

        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                final int blue = byteBuffer.get ();
                final int green = byteBuffer.get ();
                final int red = byteBuffer.get ();
                byteBuffer.get (); // Drop unused Alpha

                final int pixel = SPixelFromRGB (red, green, blue);
                buffer.put ((byte) (pixel & 0x00FF));
                buffer.put ((byte) ((pixel & 0xFF00) >> 8));
            }

            for (int x = 0; x < padding; x++)
                buffer.put ((byte) 0x00);
        }

        byteBuffer.rewind ();
    }


    private static int SPixelFromRGB (final int red, final int green, final int blue)
    {
        int pixel = (blue & 0xF8) >> 3;
        pixel <<= 6;
        pixel += (green & 0xFC) >> 2;
        pixel <<= 5;
        pixel += (red & 0xF8) >> 3;
        return pixel;
    }
}
