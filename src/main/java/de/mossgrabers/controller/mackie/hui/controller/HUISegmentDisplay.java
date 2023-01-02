// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.hui.controller;

import de.mossgrabers.framework.controller.display.AbstractTextDisplay;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.utils.StringUtils;

import java.util.Arrays;
import java.util.Locale;


/**
 * The HUI segment display.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class HUISegmentDisplay extends AbstractTextDisplay
{
    private static final String SYSEX_HDR          = "F0 00 00 66 05 00 11 ";

    private final int []        transportBuffer    = new int [8];
    private final int []        oldtransportBuffer = new int [8];


    /**
     * Constructor.
     *
     * @param host The host
     * @param output The MIDI output which addresses the display
     */
    public HUISegmentDisplay (final IHost host, final IMidiOutput output)
    {
        super (host, output, 1, 1, 8);

        Arrays.fill (this.oldtransportBuffer, -1);
    }


    /** {@inheritDoc} */
    @Override
    public void writeLine (final int row, final String text)
    {
        // Sets the position string. Must only contain numbers and ':'
        final String lowerText = text.toLowerCase (Locale.US);
        Arrays.fill (this.transportBuffer, 0);

        // Convert string to display character codes
        int index = 0;
        for (int i = lowerText.length () - 1; i >= 0; i--)
        {
            final char c = lowerText.charAt (i);

            // Set a dot
            if (c == ':')
                this.transportBuffer[index] += 0x10;
            else
            {
                int value = c - '0';
                if (value < 0 || value > 9)
                    value = 0;
                this.transportBuffer[index] += value;
                index++;
                if (index >= 8)
                    break;
            }
        }

        // Lookup number of changed digits
        int pos = 7;
        for (int i = 7; i >= 0; i--)
        {
            if (this.transportBuffer[i] == this.oldtransportBuffer[i])
                pos--;
            else
                break;
        }
        // Nothing has changed
        if (pos == -1)
            return;

        // Store the changes
        System.arraycopy (this.transportBuffer, 0, this.oldtransportBuffer, 0, pos + 1);

        // Create and send the message with changed digits
        final int [] data = new int [pos + 1];
        System.arraycopy (this.transportBuffer, 0, data, 0, data.length);
        final String msg = SYSEX_HDR + StringUtils.toHexStr (data) + "F7";
        this.output.sendSysex (msg);
    }


    /** {@inheritDoc} */
    @Override
    public void shutdown ()
    {
        // Intentionally empty
    }
}