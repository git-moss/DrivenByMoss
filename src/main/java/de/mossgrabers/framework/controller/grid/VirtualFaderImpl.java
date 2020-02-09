// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller.grid;

/**
 * Default implementation of a virtual fader.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class VirtualFaderImpl implements IVirtualFader
{
    protected final IPadGrid padGrid;
    protected final int      index;

    protected int            color;
    protected boolean        isPanorama;
    protected final int []   colorStates = new int [8];


    /**
     * Constructor.
     *
     * @param padGrid The pad grid on which the virtual fader is drawn
     * @param index the index of the fader
     */
    public VirtualFaderImpl (final IPadGrid padGrid, final int index)
    {
        this.padGrid = padGrid;
        this.index = index;
    }


    /** {@inheritDoc} */
    @Override
    public void setup (final int color, final boolean isPan)
    {
        this.color = color;
        this.isPanorama = isPan;
    }


    /** {@inheritDoc} */
    @Override
    public void setValue (final int value)
    {
        for (int i = 0; i < this.colorStates.length; i++)
            this.colorStates[i] = 0;

        if (this.isPanorama)
            this.drawPanorama (value);
        else
            this.drawFader (value);

        for (int i = 0; i < this.colorStates.length; i++)
            this.lightPad (this.index, i, this.colorStates[7 - i]);
    }


    /**
     * Simulate a fader.
     *
     * @param value The value of the fader (0-127)
     */
    private void drawFader (final int value)
    {
        final double pos = 8.0 * value / 127.0;
        final int numPads = (int) pos;
        final int fine = (int) Math.round (3.0 * (pos - numPads));
        for (int i = 0; i < 8; i++)
        {
            // Four velocity colors
            if (i < numPads)
                this.colorStates[i] = this.color;
            else if (i == numPads)
            {
                // First 3 colors are supposed to be shades of grey
                this.colorStates[i] = fine == 3 ? this.color : fine;
            }
        }
    }


    /**
     * Simulate pan fader.
     *
     * @param value The value of the fader (0-127, 64 is centered)
     */
    private void drawPanorama (final int value)
    {
        // Centered
        if (value == 64)
        {
            this.colorStates[3] = this.color;
            this.colorStates[4] = this.color;
            return;
        }

        // Pan to the left / bottom
        if (value < 64)
        {
            final double pos = 4.0 * value / 64.0 - 1;
            final int numPads = (int) pos;
            final int fine = 3 - (int) Math.abs (Math.round (3.0 * (pos - numPads)));
            for (int i = 0; i < 4; i++)
            {
                if (i - 1 > numPads)
                    this.colorStates[i] = this.color;
                else if (i - 1 == numPads)
                {
                    // First 3 colors are supposed to be shades of grey
                    this.colorStates[i] = fine == 3 ? this.color : fine;
                }
            }
            return;
        }

        // Pan to the right / top
        final double pos = 4.0 * (value - 64) / 64.0;
        final int numPads = (int) pos;
        final int fine = (int) Math.round (3.0 * (pos - numPads));
        for (int i = 4; i < 8; i++)
        {
            if (i - 4 < numPads)
                this.colorStates[i] = this.color;
            else if (i - 4 == numPads)
            {
                // First 3 colors are supposed to be shades of grey
                this.colorStates[i] = fine == 3 ? this.color : fine;
            }
        }
    }


    /**
     * Set the lighting state of a pad.
     *
     * @param x The x position of the pad in the grid
     * @param y The y position of the pad in the grid
     * @param color A registered color ID of the color / brightness
     */
    protected void lightPad (final int x, final int y, final int color)
    {
        this.padGrid.lightEx (x, y, color);
    }
}
