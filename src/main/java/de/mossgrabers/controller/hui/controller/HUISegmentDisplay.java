// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.hui.controller;

import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.utils.StringUtils;

import java.util.Arrays;


/**
 * The HUI segment display.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class HUISegmentDisplay
{
    private static final String SYSEX_HDR          = "F0 00 00 66 05 00 11 ";

    private IMidiOutput         output;
    private int []              transportBuffer    = new int [8];
    private int []              oldtransportBuffer = new int [8];


    /**
     * Constructor.
     *
     * @param output The midi output which addresses the display
     */
    public HUISegmentDisplay (final IMidiOutput output)
    {
        this.output = output;

        Arrays.fill (this.oldtransportBuffer, -1);
    }


    /**
     * Sets the position string. Must only contain numbers and ':'.
     *
     * @param position The string
     */
    public void setTransportPositionDisplay (final String position)
    {
        final String text = position.toLowerCase ();
        Arrays.fill (this.transportBuffer, 0);

        // Convert string to display character codes
        int index = 0;
        for (int i = text.length () - 1; i >= 0; i--)
        {
            final char c = text.charAt (i);

            // Set a dot
            if (c == ':')
            {
                this.transportBuffer[index] += 0x10;
                continue;
            }

            final int value = c - '0';
            this.transportBuffer[index] += value;
            index++;
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


    /**
     * Clear the 7-digit displays.
     */
    public void shutdown ()
    {
        // Can't be cleared
    }
}