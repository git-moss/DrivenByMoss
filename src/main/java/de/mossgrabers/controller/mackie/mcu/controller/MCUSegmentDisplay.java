// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.mcu.controller;

import de.mossgrabers.framework.controller.display.AbstractTextDisplay;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiOutput;


/**
 * The MCU segment display.
 *
 * @author Jürgen Moßgraber
 */
public class MCUSegmentDisplay extends AbstractTextDisplay
{
    private final int [] transportBuffer = new int [10];


    /**
     * Constructor.
     *
     * @param host The host
     * @param output The MIDI output which addresses the display
     */
    public MCUSegmentDisplay (final IHost host, final IMidiOutput output)
    {
        super (host, output, 1, 1, 20);
    }


    /** {@inheritDoc} */
    @Override
    public void writeLine (final int row, final String text)
    {
        // Sets the position string. Must only contain numbers and ':'.
        boolean addDot = false;
        int pos = text.length () - 1;
        int i = 0;
        while (i < 10)
        {
            int c = 0x20;
            if (pos >= 0)
            {
                final char singleDigit = text.charAt (pos);
                pos--;
                final boolean isDot = singleDigit == ':';
                if (isDot)
                {
                    addDot = isDot;
                    continue;
                }

                c = singleDigit;
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


    /** {@inheritDoc} */
    @Override
    public void shutdown ()
    {
        // Clear the 7-digit displays
        for (int i = 0; i < 12; i++)
            this.output.sendCC (0x40 + i, 0x20);
    }
}