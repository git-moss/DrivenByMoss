// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework;

/**
 * Helper class for string methods.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class StringUtils
{
    /**
     * Construcotr, private due to help class.
     */
    private StringUtils ()
    {
        // Intentionally empty
    }


    /**
     * Replace umlauts with alternative writing.
     *
     * @param name The string to check
     * @return The string with replaced umlauts
     */
    public static String fixASCII (final String name)
    {
        final StringBuilder str = new StringBuilder ();
        for (int i = 0; i < name.length (); i++)
        {
            final char c = name.charAt (i);
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
                    case 'Ö':
                    case '\u0152':
                        str.append ("Oe");
                        break;
                    case 'ö':
                    case '\u0153':
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
                    case 'é':
                        str.append ("e");
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
     * Convert the bytes to a hex string
     *
     * @param data The data to convert
     * @return The hex string
     */
    public static String toHexStr (final int [] data)
    {
        final StringBuilder sysex = new StringBuilder ();
        for (final int d: data)
            sysex.append (toHexStr (d)).append (' ');
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
        final String v = Integer.toHexString (number).toUpperCase ();
        return v.length () < 2 ? '0' + v : v;
    }


    /**
     * Format the given time as measure.quarters.eights.
     *
     * @param quartersPerMeasure The number of quarters of a measure
     * @param time The time to format
     * @param startOffset An offset that is added to the measure, quarter and eights values
     * @return The formatted text
     */
    public static String formatMeasures (final int quartersPerMeasure, final double time, final int startOffset)
    {
        final int measure = (int) Math.floor (time / quartersPerMeasure);
        double t = time - measure * quartersPerMeasure;
        final int quarters = (int) Math.floor (t); // :1
        t = t - quarters; // *1
        final int eights = (int) Math.floor (t / 0.25);
        return measure + startOffset + "." + (quarters + startOffset) + "." + (eights + startOffset);
    }
}
