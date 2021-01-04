// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.graphics;

import de.mossgrabers.framework.graphics.IBitmap;
import de.mossgrabers.framework.graphics.IEncoder;
import de.mossgrabers.framework.graphics.IRenderer;

import com.bitwig.extension.api.graphics.Bitmap;
import com.bitwig.extension.api.graphics.GraphicsOutput.AntialiasMode;

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
    public void render (final boolean enableAntialias, final IRenderer renderer)
    {
        this.bitmap.render (gc -> renderer.render (new GraphicsContextImpl (enableAntialias ? AntialiasMode.BEST : AntialiasMode.OFF, gc)));
    }


    /** {@inheritDoc} */
    @Override
    public void encode (final IEncoder encoder)
    {
        final ByteBuffer imageBuffer = this.bitmap.getMemoryBlock ().createByteBuffer ();
        encoder.encode (imageBuffer, this.bitmap.getWidth (), this.bitmap.getHeight ());
    }


    /**
     * Get the Bitwig bitmap.
     *
     * @return The bitmap
     */
    public Bitmap getBitmap ()
    {
        return this.bitmap;
    }
}
