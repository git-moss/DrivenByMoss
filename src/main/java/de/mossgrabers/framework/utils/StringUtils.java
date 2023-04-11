// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.utils;

import de.mossgrabers.framework.controller.color.ColorEx;

import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;


/**
 * Helper class for string methods.
 *
 * @author Jürgen Moßgraber
 */
public class StringUtils
{
    private static final char [] REMOVABLE_CHARS =
    {
        ' ',
        'e',
        'a',
        'u',
        'i',
        'o'
    };


    /**
     * Constructor, private due to help class.
     */
    private StringUtils ()
    {
        // Intentionally empty
    }


    /**
     * Pad the given text with the space character until it reaches the given length.
     *
     * @param str The text to pad
     * @param length The maximum length
     * @return The padded text
     */
    public static String pad (final String str, final int length)
    {
        return pad (str, length, ' ');
    }


    /**
     * Pad the given text with the given character until it reaches the given length.
     *
     * @param str The text to pad
     * @param length The maximum length
     * @param character The character to use for padding
     * @return The padded text
     */
    public static String pad (final String str, final int length, final char character)
    {
        final String text = str == null ? "" : str;
        final int diff = length - text.length ();
        if (diff == 0)
            return text;
        if (diff < 0)
            return text.substring (0, length);
        final StringBuilder sb = new StringBuilder (text.length () + diff).append (text);
        for (int i = 0; i < diff; i++)
            sb.append (character);
        return sb.toString ();
    }


    /**
     * First replaces umlauts with alternative writing, then shortens a text to the given length.
     *
     * @param text The text to shorten
     * @param length The length to shorten to
     * @return The shortened text
     */
    public static String shortenAndFixASCII (final String text, final int length)
    {
        return optimizeName (fixASCII (text), length);
    }


    /**
     * Replace umlauts and other non-ASCII characters with alternative writing.
     *
     * @param text The string to check
     * @return The string with replaced characters, might be longer than the original!
     */
    public static String fixASCII (final String text)
    {
        if (text == null)
            return "";
        final StringBuilder str = new StringBuilder ();
        for (int i = 0; i < text.length (); i++)
        {
            final char c = text.charAt (i);
            if (c > 127)
            {
                switch (c)
                {
                    case 'Ä':
                        str.append ("Ae");
                        break;
                    case 'ä':
                        str.append ("ae");
                        break;
                    case 'Ö', '\u0152':
                        str.append ("Oe");
                        break;
                    case 'ö', '\u0153':
                        str.append ("oe");
                        break;
                    case 'Ü':
                        str.append ("Ue");
                        break;
                    case 'ü':
                        str.append ("ue");
                        break;
                    case 'ß':
                        str.append ("ss");
                        break;
                    case 'é', 'ê':
                        str.append ("e");
                        break;
                    case 'ī', 'ï':
                        str.append ("i");
                        break;
                    case 'ā':
                        str.append ("a");
                        break;
                    case '→':
                        str.append ("->");
                        break;
                    case '♯':
                        str.append ("#");
                        break;
                    case '\u2013':
                        str.append ("-");
                        break;
                    case '¼':
                        str.append ("1/4");
                        break;
                    case '⅕':
                        str.append ("1/5");
                        break;
                    case '⅙':
                        str.append ("1/6");
                        break;
                    case '’':
                        str.append ("'");
                        break;
                    // superscript p
                    case '\u1d3e':
                        str.append ("p");
                        break;
                    default:
                        str.append ("?");
                        break;
                }
            }
            else
                str.append (c);
        }
        return str.toString ();
    }


    /**
     * Replaces some non-ASCII characters which are not in the default font which is used for
     * drawing text on graphics displays.
     *
     * @param text The string to check
     * @return The string with replaced characters
     */
    public static String fixFontCharacters (final String text)
    {
        if (text == null)
            return "";
        final StringBuilder str = new StringBuilder ();
        for (int i = 0; i < text.length (); i++)
        {
            final char c = text.charAt (i);
            if (c == '♯')
                str.append ("#");
            else
                str.append (c);
        }
        return str.toString ();
    }


    /**
     * Convert an string containing only ascii characters to a hex string separated by spaces.
     *
     * @param asciiText The ASCII text to convert
     * @return The formatted hex code
     */
    public static String asciiToHex (final String asciiText)
    {
        final int [] array = new int [asciiText.length ()];
        for (int i = 0; i < asciiText.length (); i++)
            array[i] = asciiText.charAt (i);
        return toHexStr (array);
    }


    /**
     * Shortens a text to the given length.
     *
     * @param text The text to shorten
     * @param length The length to shorten to
     * @return The shortened text
     */
    public static String optimizeName (final String text, final int length)
    {
        if (text == null)
            return "";

        String shortened = text;
        for (final char element: REMOVABLE_CHARS)
        {
            if (shortened.length () <= length)
                return shortened;
            int pos;
            while ((pos = shortened.indexOf (element)) != -1)
            {
                shortened = shortened.substring (0, pos) + shortened.substring (pos + 1, shortened.length ());
                if (shortened.length () <= length)
                    return shortened;
            }
        }
        return shortened.length () <= length ? shortened : shortened.substring (0, length);
    }


    /**
     * Convert the bytes to a hex string.
     *
     * @param data The data to convert
     * @return The hex string
     */
    public static String toHexStr (final int [] data)
    {
        return toHexStr (data, true);
    }


    /**
     * Convert the bytes to a hex string.
     *
     * @param data The data to convert
     * @param addSpace True to add a space character after each hex number
     * @return The hex string
     */
    public static String toHexStr (final int [] data, final boolean addSpace)
    {
        final StringBuilder sysex = new StringBuilder ();
        for (final int d: data)
        {
            sysex.append (toHexStr (d));
            if (addSpace)
                sysex.append (' ');
        }
        return sysex.toString ();
    }


    /**
     * Convert the bytes to a hex string. Rewinds the buffer and adds the bytes from the beginning
     * till the capacity.
     *
     * @param data The data to convert
     * @return The hex string
     */
    public static String toHexStr (final ByteBuffer data)
    {
        final StringBuilder sysex = new StringBuilder ();
        while (data.position () < data.limit ())
            sysex.append (toHexStr (Byte.toUnsignedInt (data.get ()))).append (' ');
        return sysex.toString ();
    }


    /**
     * Convert the bytes to a hex string
     *
     * @param data The data to convert
     * @return The hex string
     */
    public static String toHexStr (final byte [] data)
    {
        final StringBuilder sysex = new StringBuilder ();
        for (final byte d: data)
            sysex.append (toHexStr (Byte.toUnsignedInt (d))).append (' ');
        return sysex.toString ();
    }


    /**
     * Convert the byte to a hex string
     *
     * @param number The value to convert
     * @return The hex string
     */
    public static String toHexStr (final int number)
    {
        return String.format ("%02X", Integer.valueOf (number));
    }


    /**
     * Parse a byte from an hex encoded string. A byte has 2 digits in the string.
     *
     * @param data The data in hex
     * @param index The index of the byte
     * @return The parsed byte as integer
     */
    public static int fromHexStr (final String data, final int index)
    {
        final int pos = index * 2;
        return Integer.parseInt (data.substring (pos, pos + 2), 16);
    }


    /**
     * Convert a string with hex encoded bytes. One byte is 2 characters without any spaces.
     *
     * @param data The data to convert
     * @return The parsed byte array
     */
    public static int [] fromHexStr (final String data)
    {
        final int length = data.length ();
        if (length % 2 != 0)
            throw new IllegalArgumentException ("Length of hex data must be a multiple of 2!");

        final int size = length / 2;
        final int [] result = new int [size];
        for (int i = 0; i < size; i++)
        {
            final int pos = i * 2;
            result[i] = Integer.parseInt (data.substring (pos, pos + 2), 16);
        }
        return result;
    }


    /**
     * Interpret the content of an integer array as an ASCII text.
     *
     * @param start At which index to start to convert the ASCII text in the array
     * @param length The number of integers to convert
     * @param data The integer array
     * @return The converted ASCII string
     */
    public static String integerArrayToString (final int start, final int length, final int [] data)
    {
        final StringBuilder sb = new StringBuilder (length);
        for (int i = 0; i < length; i++)
            sb.append ((char) data[start + i]);
        return sb.toString ();
    }


    /**
     * Format a velocity percentage.
     *
     * @param noteVelocity The velocity in the range of 0..1.
     * @return The formatted velocity
     */
    public static String formatPercentage (final double noteVelocity)
    {
        final DecimalFormat df = new DecimalFormat ("0", DecimalFormatSymbols.getInstance (Locale.ENGLISH));
        df.setMaximumFractionDigits (1);
        return df.format (Double.valueOf (noteVelocity * 100.0)) + "%";
    }


    /**
     * Format the given time as measure.quarters.eights / measure.quarters.eights.ticks.
     *
     * @param quartersPerMeasure The number of quarters of a measure
     * @param beats The beats to format
     * @param startOffset An offset that is added to the measure, quarter and eights values
     * @param includeFrames Add the frames (ticks) if true
     * @return The formatted text
     */
    public static String formatMeasures (final int quartersPerMeasure, final double beats, final int startOffset, final boolean includeFrames)
    {
        return formatMeasures (quartersPerMeasure, beats, startOffset, includeFrames, "%d.%d.%d", "%d.%d.%d:%03d");
    }


    /**
     * Format the given time as measure.quarters.eights / measure.quarters.eights.ticks. Padded to 3
     * / 2 digits.
     *
     * @param quartersPerMeasure The number of quarters of a measure
     * @param beats The beats to format
     * @param startOffset An offset that is added to the measure, quarter and eights values
     * @param includeFrames Add the frames (ticks) if true
     * @return The formatted text
     */
    public static String formatMeasuresLong (final int quartersPerMeasure, final double beats, final int startOffset, final boolean includeFrames)
    {
        return formatMeasures (quartersPerMeasure, beats, startOffset, includeFrames, "%03d.%d.%d", "%d.%d.%d:%02d");
    }


    /**
     * Format the given time as hours.minutes.seconds / hours.minutes.seconds.millis.
     *
     * @param tempo The tempo
     * @param beats The beats to format as time
     * @param includeFrames Add the frames (ticks) if true
     * @return The formatted text
     */
    public static String formatTime (final double tempo, final double beats, final boolean includeFrames)
    {
        return formatTime (tempo, beats, includeFrames, "%d.%d.%d", "%d.%d.%d:%03d");
    }


    /**
     * Format the given time as minutes.seconds / minutes.seconds.millis. Padded to 3 / 2 digits.
     *
     * @param tempo The tempo
     * @param beats The beats to format as time
     * @param includeFrames Add the frames (ticks) if true
     * @return The formatted text
     */
    public static String formatTimeLong (final double tempo, final double beats, final boolean includeFrames)
    {
        return formatTime (tempo, beats, includeFrames, "%02d.%02d.%02d", "%d.%02d.%02d:%03d");
    }


    private static String formatMeasures (final int quartersPerMeasure, final double beats, final int startOffset, final boolean includeFrames, final String shortFormat, final String longFormat)
    {
        final int measure = (int) Math.floor (beats / quartersPerMeasure);
        double t = beats - measure * quartersPerMeasure;
        final int quarters = (int) Math.floor (t); // :1
        t = t - quarters; // *1
        final int eights = (int) Math.floor (t / 0.25);

        if (!includeFrames)
            return String.format (shortFormat, Integer.valueOf (measure + startOffset), Integer.valueOf (quarters + startOffset), Integer.valueOf (eights + startOffset));

        t = t - eights * 0.25;
        final int frames = (int) Math.floor (t / 0.25 * 100.0);
        return String.format (longFormat, Integer.valueOf (measure + startOffset), Integer.valueOf (quarters + startOffset), Integer.valueOf (eights + startOffset), Integer.valueOf (frames));
    }


    private static String formatTime (final double tempo, final double beats, final boolean includeFrames, final String shortFormat, final String longFormat)
    {
        final double time = beats * 60.0 / tempo;

        final int seconds = (int) Math.floor (time % 60);
        double t = (time - seconds) / 60.0;
        final int minutes = (int) Math.floor (t % 60);
        t = (t - minutes) / 60.0;
        final int hours = (int) Math.floor (t);

        if (!includeFrames)
            return String.format (shortFormat, Integer.valueOf (minutes), Integer.valueOf (seconds));

        final int millis = (int) ((time - ((hours * 60 + minutes) * 60 + seconds)) * 1000);
        return String.format (longFormat, Integer.valueOf (hours), Integer.valueOf (minutes), Integer.valueOf (seconds), Integer.valueOf (millis));
    }


    /**
     * Format the color as a 3 byte hex number, e.g. FFFFFF.
     *
     * @param color THe color to format
     * @return The formatted color
     */
    public static String formatColor (final ColorEx color)
    {
        return toHexStr (color.toIntRGB255 (), false);
    }
}
