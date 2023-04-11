// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller.grid;

import de.mossgrabers.framework.daw.IHost;


/**
 * Default implementation of a virtual fader.
 *
 * @author Jürgen Moßgraber
 */
public class VirtualFaderImpl implements IVirtualFader
{
    private static final int            PAD_VALUE_AMOUNT       = 16;

    // @formatter:off
    private static final int [] SPEED_SCALE          =
    {
        1,   1,  1,  1,  1,  1,  1,  1,
        1,   1,  1,  1,  1,  1,  1,  1,
        1,   1,  1,  1,  1,  1,  1,  1,
        2,   2,  2,  2,  2,  2,  2,  2,
        2,   2,  2,  2,  2,  2,  2,  2,
        2,   2,  2,  2,  2,  2,  2,  2,
        3,   3,  3,  3,  3,  3,  3,  3,
        3,   3,  3,  3,  3,  3,  3,  3,

        4,   4,  4,  4,  4,  4,  4,  4,
        5,   5,  5,  5,  5,  5,  5,  5,
        6,   6,  6,  6,  7,  7,  7,  7,
        8,   8,  8,  8,  9,  9,  9,  9,
        10, 10, 10, 10, 11, 11, 11, 11,
        12, 12, 12, 12, 13, 13, 13, 13,
        14, 14, 15, 15, 16, 16, 17, 17,
        18, 19, 20, 21, 22, 23, 24, 25
    };
    // @formatter:on

    private static final int            LOOP_DELAY             = 6;

    private final IHost                 host;
    private final IVirtualFaderCallback callback;
    private final IPadGrid              padGrid;
    private final int                   index;

    private int                         color;
    private boolean                     isPanorama;
    private final int []                colorStates            = new int [8];

    private int                         moveDelay;
    private int                         moveTimerDelay;
    private int                         moveDestination;
    private int                         moveTargetValue;
    private boolean                     moveDirectionIsUpwards = true;
    private boolean                     isKnobType;


    /**
     * Constructor. Does not update a slider on the grid. Use getColorState method to draw the fader
     * yourself.
     *
     * @param host The host
     * @param callback Callback for getting and setting fader values
     */
    public VirtualFaderImpl (final IHost host, final IVirtualFaderCallback callback)
    {
        this (host, callback, null, -1);
    }


    /**
     * Constructor.
     *
     * @param host The host
     * @param callback Callback for getting and setting fader values
     * @param padGrid The pad grid on which the virtual fader is drawn
     * @param index the index of the fader
     */
    public VirtualFaderImpl (final IHost host, final IVirtualFaderCallback callback, final IPadGrid padGrid, final int index)
    {
        this.host = host;
        this.padGrid = padGrid;
        this.index = index;
        this.callback = callback;
    }


    /** {@inheritDoc} */
    @Override
    public int getColorState (final int index)
    {
        return this.colorStates[index];
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
            this.lightPad (i, this.colorStates[7 - i]);
    }


    /** {@inheritDoc} */
    @Override
    public void moveTo (final int row, final int velocity)
    {
        // About 3 seconds on softest velocity
        this.moveDelay = SPEED_SCALE[velocity] * LOOP_DELAY;
        this.moveTimerDelay = SPEED_SCALE[SPEED_SCALE.length - 1 - velocity] * LOOP_DELAY;

        // Compensate for parameter type detection delay
        this.moveTimerDelay -= 1;

        // Reset parameter type detection flag
        this.isKnobType = false;

        // Calculate the bounds of the destination pad
        final int min = row * PAD_VALUE_AMOUNT;
        final int max = Math.min (127, (row + 1) * PAD_VALUE_AMOUNT - 1);
        int newDestination = this.smoothFaderValue (row, max);

        // Support stepping through 4 values on the destination pad
        if (min <= this.moveDestination && this.moveDestination <= max)
        {
            final int step = (this.moveDestination - min) / 4;

            if (this.moveDirectionIsUpwards)
            {
                newDestination = min + 4 * (step + 2) - 1;
                if (newDestination > max)
                {
                    newDestination = min + 4 * step - 1;
                    this.moveDirectionIsUpwards = false;
                }
            }
            else
            {
                newDestination = min + 4 * (step - 1) + 1;
                if (newDestination < min)
                {
                    newDestination = min + 4 * (step + 2) - 1;
                    this.moveDirectionIsUpwards = true;
                }
            }
        }
        else if (row == 0)
        {
            newDestination = 0;
        }

        this.moveDestination = newDestination;

        this.moveFaderToDestination ();
    }


    protected void moveFaderToDestination ()
    {
        final int current = this.callback.getValue ();
        if (current == this.moveDestination)
            return;

        this.moveDirectionIsUpwards = current < this.moveDestination;
        this.moveTargetValue = this.moveDirectionIsUpwards ? Math.min (current + this.moveDelay, this.moveDestination) : Math.max (current - this.moveDelay, this.moveDestination);

        this.callback.setValue (this.moveTargetValue);

        // Delay to allow the parameter value to update properly
        this.host.scheduleTask (this::moveFaderToDestinationCallback, LOOP_DELAY);
    }


    protected void moveFaderToDestinationCallback ()
    {
        final int updatedValue = this.callback.getValue ();

        // Compare updated parameter value to target update value, if it is different the parameter
        // is either a boolean or selection list type and the destination value should be force set
        // It seems that setting USER parameters is slower and LOOP_DELAY needs to be at least 6!
        if (!this.isKnobType && updatedValue != this.moveTargetValue)
        {
            this.host.println ("FORCED!");

            this.callback.setValue (this.moveDestination);
            return;
        }

        this.isKnobType = true;
        this.host.scheduleTask (this::moveFaderToDestination, this.moveTimerDelay);
    }


    /**
     * Special handling of pads for smoothing the fader, e.g. special handling of 1st row.
     *
     * @param row The row of the pressed pad
     * @param value The calculated value
     * @return The smoothed value
     */
    private int smoothFaderValue (final int row, final int value)
    {
        if (this.isPanorama && (row == 3 || row == 4))
            return 64;

        final int oldValue = this.callback.getValue ();
        if (row == 0)
            return oldValue == 0 ? PAD_VALUE_AMOUNT - 1 : 0;
        return value;
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
     * @param y The y position of the pad in the grid
     * @param color A registered color ID of the color / brightness
     */
    private void lightPad (final int y, final int color)
    {
        if (this.padGrid != null)
            this.padGrid.lightEx (this.index, y, color);
    }
}
