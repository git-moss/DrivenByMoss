// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.faderfox.ec4.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import de.mossgrabers.framework.controller.display.AbstractTextDisplay;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * The EC4 display.
 *
 * @author Jürgen Moßgraber
 */
public class EC4Display extends AbstractTextDisplay
{
    /** The display page which shows the controller names. */
    public static final int      DISPLAY_CONTROLS = 0;
    /** The display page with the total view. */
    public static final int      DISPLAY_TOTAL    = 3;

    private static final byte [] SYSEX_HEADER     =
    {
        (byte) 0xF0,
        0x00,
        0x00,
        0x00,
        0x4E,
        0x2C,
        0x1B
    };

    private final int            display;


    /**
     * Constructor. 4 rows (0-1) with 4 blocks (0-3). Each block consists of 4 characters.
     *
     * @param host The host
     * @param output The MIDI output which addresses the display
     * @param display The display index 0-3
     */
    public EC4Display (final IHost host, final IMidiOutput output, final int display)
    {
        super (host, output, 4 /* No of rows */, display == 3 ? 5 : 4 /* No of cells */, display == 3 ? 20 : 16);

        this.display = display;
    }


    /** {@inheritDoc} */
    @Override
    public void writeLine (final int row, final String text, final String previousText)
    {
        if (text.equals (previousText))
            return;

        final Map<Integer, String> diff = calculateDiff (text, previousText);
        if (diff.isEmpty ())
            return;

        final int rowOffset = this.noOfCharacters * row;
        try (final ByteArrayOutputStream out = new ByteArrayOutputStream ())
        {
            out.write (SYSEX_HEADER);

            out.write (0x4E);
            out.write (0x22);
            out.write ((byte) (0x10 + this.display));

            for (final Map.Entry<Integer, String> e: diff.entrySet ())
            {
                final int offset = rowOffset + e.getKey ().intValue ();
                final String token = e.getValue ();

                out.write (0x4A);
                out.write (0x20 + offset / 16);
                out.write (0x10 + offset % 16);

                for (int i = 0; i < token.length (); i++)
                {
                    final byte ascii = (byte) token.charAt (i);
                    out.write (0x4D);
                    out.write (0x20 + ascii / 16);
                    out.write (0x10 + ascii % 16);
                }
            }

            out.write ((byte) 0xF7);
            this.output.sendSysex (out.toByteArray ());
        }
        catch (final IOException ex)
        {
            this.host.error ("Could not send sysex command.", ex);
        }
    }


    private static Map<Integer, String> calculateDiff (final String text, final String previousText)
    {
        final Map<Integer, String> result = new TreeMap<> ();
        if (previousText == null || previousText.isBlank ())
        {
            result.put (Integer.valueOf (0), text);
            return result;
        }

        final int length = Math.max (previousText.length (), text.length ());
        final String textOld = StringUtils.pad (previousText, length);
        final String textNew = StringUtils.pad (text, length);

        int position = 0;
        final StringBuilder sb = new StringBuilder ();
        for (int i = 0; i < length; i++)
        {
            final char character = textNew.charAt (i);
            if (character == textOld.charAt (i))
            {
                if (sb.length () > 0)
                {
                    result.put (Integer.valueOf (position), sb.toString ());
                    sb.setLength (0);
                }
                position = i + 1;
            }
            else
                sb.append (character);
        }
        if (sb.length () > 0)
            result.put (Integer.valueOf (position), sb.toString ());
        return result;
    }


    /** {@inheritDoc} */
    @Override
    public void shutdown ()
    {
        // Nothing to do...
    }


    /**
     * Show or hide the total display. This is an overlay display with 4 rows of text.
     *
     * @param isVisible True to display
     */
    public void setTotalDisplayVisible (final boolean isVisible)
    {
        this.sendSysex (new byte []
        {
            0x4E,
            0x22,
            (byte) (isVisible ? 0x14 : 0x15)
        });
    }


    /**
     * Send a byte array to the device.
     *
     * @param content The content bytes
     */
    private void sendSysex (final byte [] content)
    {
        try (final ByteArrayOutputStream out = new ByteArrayOutputStream ())
        {
            out.write (SYSEX_HEADER);
            out.write (content);
            out.write ((byte) 0xF7);
            this.output.sendSysex (out.toByteArray ());
        }
        catch (final IOException ex)
        {
            this.host.error ("Could not send sysex command.", ex);
        }
    }
}