// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.usb;

/**
 * An exception caused by using USB.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class UsbException extends Exception
{
    private static final long serialVersionUID = 5498805506241156804L;


    /**
     * Constructor.
     *
     * @param message the detail message (which is saved for later retrieval by the
     *            {@link #getMessage()} method).
     */
    public UsbException (final String message)
    {
        super (message);
    }


    /**
     * Constructor.
     *
     * @param message the detail message (which is saved for later retrieval by the
     *            {@link #getMessage()} method).
     * @param cause the cause (which is saved for later retrieval by the {@link #getCause()}
     *            method). (A <tt>null</tt> value is permitted, and indicates that the cause is
     *            nonexistent or unknown.)
     */
    public UsbException (final String message, final Throwable cause)
    {
        super (message, cause);
    }
}
