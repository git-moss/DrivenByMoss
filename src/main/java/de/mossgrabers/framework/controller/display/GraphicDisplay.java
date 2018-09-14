// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller.display;

import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.graphics.IBitmap;
import de.mossgrabers.framework.graphics.display.DisplayModel;
import de.mossgrabers.framework.graphics.display.VirtualDisplay;
import de.mossgrabers.framework.graphics.grid.GridChangeListener;


/**
 * A display which uses graphics rather than fixed characters.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class GraphicDisplay extends AbstractDisplay implements GridChangeListener
{
    protected final DisplayModel model;
    protected VirtualDisplay     virtualDisplay;


    /**
     * Constructor.
     *
     * @param host The host
     */
    public GraphicDisplay (final IHost host)
    {
        this (host, null, 0, 0, 0);
    }


    /**
     * Constructor. Only use for display, which have both fixed characters and graphics.
     *
     * @param host The host
     * @param output The midi output which addresses the display
     * @param noOfLines The number of rows that the display supports
     * @param noOfCells The number of cells that the display supports
     * @param noOfCharacters The number of characters of 1 row that the display supports
     */
    public GraphicDisplay (final IHost host, final IMidiOutput output, final int noOfLines, final int noOfCells, final int noOfCharacters)
    {
        super (host, output, noOfLines, noOfCells, noOfCharacters);

        this.model = new DisplayModel ();
        this.model.addGridElementChangeListener (this);
    }


    /**
     * Get the display model.
     *
     * @return The display model
     */
    public DisplayModel getModel ()
    {
        return this.model;
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
        if (this.virtualDisplay != null)
            this.send (this.virtualDisplay.getImage ());
    }


    /**
     * Send the buffered image to the screen.
     *
     * @param image An image of size 2 x 480 x 360 pixel
     */
    protected abstract void send (final IBitmap image);


    /** {@inheritDoc} */
    @Override
    public void shutdown ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public GraphicDisplay clearCell (final int row, final int cell)
    {
        // Not a line based display
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public GraphicDisplay setBlock (final int row, final int block, final String value)
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
    public GraphicDisplay setCell (final int row, final int cell, final String value)
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
}
