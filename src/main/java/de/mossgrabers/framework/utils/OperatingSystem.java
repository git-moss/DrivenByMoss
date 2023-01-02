// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.utils;

import java.util.Locale;


/**
 * Enumerations for different operating systems.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public enum OperatingSystem
{
    /** Windows. */
    WINDOWS,
    /** Linux. */
    LINUX,
    /** Intel Mac. */
    MAC,
    /** ARM Mac. */
    MAC_ARM,
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
            {
                final String osArch = System.getProperty ("os.arch");
                if (osArch != null && osArch.toLowerCase (Locale.ENGLISH).contains ("aarch64"))
                    os = MAC_ARM;
                else
                    os = MAC;
            }
        }
    }


    /**
     * Returns true if the operating system is MacOS no matter if it is Intel or ARM based.
     *
     * @return True if MacOS
     */
    public static boolean isMacOS ()
    {
        return os == MAC || os == MAC_ARM;
    }


    /**
     * Get the OS we are running on.
     *
     * @return The OS enumeration
     */
    public static OperatingSystem get ()
    {
        return os;
    }
}
