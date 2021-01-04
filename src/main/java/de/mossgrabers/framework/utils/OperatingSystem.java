// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.utils;

import java.util.Locale;


/**
 * Enums for different operating systems.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public enum OperatingSystem
{
    /** Windows. */
    WINDOWS,
    /** Linux. */
    LINUX,
    /** Any Mac. */
    MAC,
    /** No idea. */
    OTHER;


    private static OperatingSystem os = OTHER;

    static
    {
        String osName = System.getProperty ("os.name");
        if (osName != null)
        {
            osName = osName.toLowerCase (Locale.ENGLISH);
            if (osName.contains ("windows"))
                os = WINDOWS;
            else if (osName.contains ("linux"))
                os = LINUX;
            else if (osName.contains ("mac os x"))
                os = MAC;
        }
    }


    /**
     * Get the OS we are running on.
     *
     * @return The OS enum
     */
    public static OperatingSystem get ()
    {
        return os;
    }
}
