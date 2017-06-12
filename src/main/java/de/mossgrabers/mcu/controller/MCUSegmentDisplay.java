// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.mcu.controller;

import de.mossgrabers.framework.midi.MidiOutput;

import com.bitwig.extension.controller.api.ControllerHost;


/**
 * The MCU segment display.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MCUSegmentDisplay
{
    private MidiOutput output;
    private int []     buffer = new int [10];


    /**
     * Constructor.
     *
     * @param host The host
     * @param output The midi output which addresses the display
     */
    public MCUSegmentDisplay (final ControllerHost host, final MidiOutput output)
    {
        this.output = output;
    }


    /**
     * Sets the position string. Must only contain numbers and ':'.
     *
     * @param position The string
     */
    public void setTransportPositionDisplay (final String position)
    {
        boolean addDot = false;
        int pos = position.length () - 1;
        int i = 0;
        while (i < 10)
        {
            int c = 0x20;
            if (pos >= 0)
            {
                final char singleDigit = position.charAt (pos);
                pos--;
                final boolean isDot = singleDigit == ':';
                if (isDot)
                {
                    addDot = isDot;
                    continue;
                }

                c = singleDigit; // 0x30 + i;
                if (addDot)
                    c += 0x40;
            }

            if (c != this.buffer[i])
            {
                this.output.sendCC (0x40 + i, c);
                this.buffer[i] = c;
            }
            i++;
            addDot = false;
        }
    }


    /**
     * Clear the display.
     */
    public void shutdown ()
    {
        for (int i = 0; i < 10; i++)
            this.output.sendCC (0x40 + i, 0x20);
    }
}