// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.faderfox.ec4.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.mossgrabers.framework.controller.display.AbstractTextDisplay;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiOutput;


/**
 * The EC4 display.
 *
 * @author Jürgen Moßgraber
 */
public class EC4Display extends AbstractTextDisplay
{
    private static final byte [] SYSEX_HEADER =
    {
        (byte) 0xF0,
        0x00,
        0x00,
        0x00,
        0x4E,
        0x2C,
        0x1B
    };


    /**
     * Constructor. 4 rows (0-1) with 4 blocks (0-3). Each block consists of 4 characters.
     *
     * @param host The host
     * @param output The MIDI output which addresses the display
     */
    public EC4Display (final IHost host, final IMidiOutput output)
    {
        super (host, output, 4 /* No of rows */, 4 /* No of cells */, 16);
    }


    /** {@inheritDoc} */
    @Override
    public void writeLine (final int row, final String text)
    {
        // TODO split in 4 parts, cache again

        // final int length = text.length ();
        // final int [] array = new int [length];
        // for (int i = 0; i < length; i++)
        // array[i] = text.charAt (i);

        // TODO
        int display = 0;
        int rowOffset = 16 * row;

        try (final ByteArrayOutputStream out = new ByteArrayOutputStream ())
        {
            out.write (SYSEX_HEADER);

            // Display 0-3
            out.write (0x4E);
            out.write (0x22);
            out.write ((byte) (0x10 + display));

            out.write (0x4A);
            out.write (0x20 + rowOffset / 16);
            out.write (0x10 + rowOffset % 16);

            // TODO do we need to convert to ASCII or is that already done?

            for (int i = 0; i < text.length (); i++)
            {
                final byte ascii = (byte) text.charAt (i);
                out.write (0x4D);
                out.write (0x20 + ascii / 16);
                out.write (0x10 + ascii % 16);
            }

            // 4A 2a 1a 4D 2d 1d F7
            //
            // 4A 23 1C 4D 25 12 4D 26 15 4D 27 13 4D 26 1F F7
            // ^^^^^^^^ ^^^^^^^^ ^^^^^^^^ ^^^^^^^^ ^^^^^^^^
            // address data data data data
            // a=60 d='R' d='e' d='s' d='o'

            out.write ((byte) 0xF7);
            this.output.sendSysex (out.toByteArray ());
        }
        catch (final IOException ex)
        {
            this.host.error ("Could not send sysex command.", ex);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void shutdown ()
    {
        // TODO show on total display
        this.notify ("Please start " + this.host.getName () + " to play...");
    }
}