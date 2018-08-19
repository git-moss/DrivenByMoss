// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.usb.mkii.controller;

import de.mossgrabers.controller.kontrol.usb.mkii.Kontrol2Configuration;
import de.mossgrabers.framework.controller.display.AbstractDisplay;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.controller.display.Format;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.graphics.IGraphicsDimensions;
import de.mossgrabers.framework.graphics.display.DisplayModel;
import de.mossgrabers.framework.graphics.display.VirtualDisplay;
import de.mossgrabers.framework.graphics.grid.DefaultGraphicsDimensions;
import de.mossgrabers.framework.graphics.grid.GridChangeListener;


/**
 * The display of Kontrol 2.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Kontrol2Display extends AbstractDisplay implements GridChangeListener
{
    private final Kontrol2UsbDevice  usbDevice;
    private final DisplayModel       model;
    private final VirtualDisplay     virtualDisplay;
    private final Kontrol2UsbDisplay usbDisplay;


    /**
     * Constructor. 2 rows (0-1) with 9 blocks (0-8). Each block consists of 8 characters.
     *
     * @param host The host
     * @param configuration The configuration
     * @param usbDevice The USB device
     */
    public Kontrol2Display (final IHost host, final Kontrol2Configuration configuration, final Kontrol2UsbDevice usbDevice)
    {
        super (host, null, 2 /* No of rows */, 9 /* No of cells */, 72 /* No of characters */);
        this.usbDevice = usbDevice;

        this.model = new DisplayModel ();
        this.model.addGridElementChangeListener (this);

        final IGraphicsDimensions dimensions = new DefaultGraphicsDimensions (2 * 480, 360);
        this.virtualDisplay = new VirtualDisplay (host, this.model, configuration, dimensions, "Kontrol mkII Display");
        this.usbDisplay = new Kontrol2UsbDisplay (host);
    }


    /**
     * Get the dislay model.
     *
     * @return The display model
     */
    public DisplayModel getModel ()
    {
        return this.model;
    }


    /** {@inheritDoc} */
    @Override
    public void shutdown ()
    {
        this.usbDevice.shutdown ();
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        super.flush ();
        this.usbDevice.sendDisplayData ();
    }


    /** {@inheritDoc} */
    @Override
    public Kontrol2Display clearCell (final int row, final int cell)
    {
        // Not a line based display
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public Kontrol2Display setBlock (final int row, final int block, final String value)
    {
        // Not a line based display
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public Display setCell (final int row, final int column, final int value, final Format format)
    {
        // Not a line based display
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public Kontrol2Display setCell (final int row, final int cell, final String value)
    {
        // Not a line based display
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public void writeLine (final int row, final String text)
    {
        // Not a line based display
    }


    /**
     * Show the display debug window.
     */
    public void showDebugWindow ()
    {
        if (this.virtualDisplay != null)
            this.virtualDisplay.getImage ().showDisplayWindow ();
    }


    /** {@inheritDoc} */
    @Override
    public void gridHasChanged ()
    {
        if (this.usbDisplay != null)
            this.usbDisplay.send (this.virtualDisplay.getImage ());
    }
}