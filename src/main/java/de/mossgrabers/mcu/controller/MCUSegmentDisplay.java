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
    private int []     transportBuffer  = new int [10];
    private int []     assignmentBuffer = new int [2];


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

            if (c != this.transportBuffer[i])
            {
                this.output.sendCC (0x40 + i, c);
                this.transportBuffer[i] = c;
            }
            i++;
            addDot = false;
        }
    }


    /**
     * Sets the assignment (mode) string. Must only contain 2 upper case letters.
     *
     * @param mode The string
     */
    public void setAssignmentDisplay (final String mode)
    {
        for (int i = 0; i < 2; i++)
        {
            char c = mode.charAt (i);
            if (this.assignmentBuffer[i] != c)
            {
                int value = c >= 0x40 ? c - 0x40 : c;
                this.output.sendCC (0x4B - i, value);
                this.assignmentBuffer[i] = c;
            }
        }
    }


    /**
     * Clear the 7-digit displays.
     */
    public void shutdown ()
    {
        for (int i = 0; i < 12; i++)
            this.output.sendCC (0x40 + i, 0x20);
    }
}