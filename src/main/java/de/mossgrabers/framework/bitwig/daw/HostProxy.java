// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.bitwig.daw;

import de.mossgrabers.framework.daw.IHost;

import com.bitwig.extension.api.graphics.Bitmap;
import com.bitwig.extension.api.graphics.BitmapFormat;
import com.bitwig.extension.api.graphics.Image;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.UsbDevice;

import java.nio.ByteBuffer;


/**
 * Encapsulates the ControllerHost instance.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class HostProxy implements IHost
{
    private ControllerHost host;


    /**
     * Constructor.
     *
     * @param host The host
     */
    public HostProxy (final ControllerHost host)
    {
        this.host = host;
    }


    /** {@inheritDoc} */
    @Override
    public String getName ()
    {
        return "Bitwig";
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasClips ()
    {
        return true;
    }


    /** {@inheritDoc} */
    @Override
    public void scheduleTask (final Runnable task, final long delay)
    {
        this.host.scheduleTask (task, delay);
    }


    /** {@inheritDoc} */
    @Override
    public void error (final String text)
    {
        this.host.errorln (text);
    }


    /** {@inheritDoc} */
    @Override
    public void error (final String text, final Exception ex)
    {
        this.host.errorln (text);
        this.host.errorln (ex.getClass () + ":" + ex.getMessage ());
    }


    /** {@inheritDoc} */
    @Override
    public void println (final String text)
    {
        this.host.println (text);
    }


    /** {@inheritDoc} */
    @Override
    public void showNotification (final String message)
    {
        this.host.showPopupNotification (message);
    }


    /** {@inheritDoc} */
    @Override
    public void sendDatagramPacket (final String string, final int port, final byte [] data)
    {
        this.host.sendDatagramPacket (string, port, data);
    }


    /** {@inheritDoc} */
    @Override
    public Image loadSVG (final String path, final int scale)
    {
        return this.host.loadSVG (path, scale);
    }


    /** {@inheritDoc} */
    @Override
    public Bitmap createBitmap (final int width, final int height)
    {
        return this.host.createBitmap (width, height, BitmapFormat.ARGB32);
    }


    /** {@inheritDoc} */
    @Override
    public ByteBuffer createByteBuffer (int size)
    {
        return this.host.createByteBuffer (size);
    }


    /** {@inheritDoc} */
    @Override
    public UsbDevice getUsbDevice (int index)
    {
        return this.host.getUsbDevice (index);
    }
}
