// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.mcu.controller;

import de.mossgrabers.framework.controller.display.AbstractTextDisplay;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiOutput;


/**
 * The MCU assignment display.
 *
 * @author Jürgen Moßgraber
 */
public class MCUAssignmentDisplay extends AbstractTextDisplay
{
    private final int [] assignmentBuffer = new int [2];


    /**
     * Constructor.
     *
     * @param host The host
     * @param output The MIDI output which addresses the display
     */
    public MCUAssignmentDisplay (final IHost host, final IMidiOutput output)
    {
        super (host, output, 1, 1, 2);
    }


    /** {@inheritDoc} */
    @Override
    public void writeLine (final int row, final String text)
    {
        // Sets the assignment (mode) string. Must only contain 2 upper case letters
        for (int i = 0; i < 2; i++)
        {
            final char c = text.charAt (i);
            if (this.assignmentBuffer[i] != c)
            {
                final int value = c >= 0x40 ? c - 0x40 : c;
                this.output.sendCC (0x4B - i, value);
                this.assignmentBuffer[i] = c;
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void shutdown ()
    {
        this.writeLine (0, "  ");
    }
}