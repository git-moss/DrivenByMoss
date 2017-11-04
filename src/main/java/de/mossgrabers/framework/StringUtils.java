// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
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
}
