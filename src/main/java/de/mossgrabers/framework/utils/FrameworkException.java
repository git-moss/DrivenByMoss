// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.utils;

/**
 * A runtime exception appeared inside of the framework.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class FrameworkException extends RuntimeException
{
    private static final long serialVersionUID = 4880127235829143840L;


    /**
     * Constructs a new framework exception with the specified detail message. The cause is not
     * initialized, and may subsequently be initialized by a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for later retrieval by the
     *            {@link #getMessage()} method.
     */
    public FrameworkException (final String message)
    {
        super (message);
    }


    /**
     * Constructs a new framework exception with the specified detail message and cause.
     * <p>
     * Note that the detail message associated with {@code cause} is <i>not</i> automatically
     * incorporated in this runtime exception's detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the
     *            {@link #getMessage()} method).
     * @param cause the cause (which is saved for later retrieval by the {@link #getCause()}
     *            method). (A <tt>null</tt> value is permitted, and indicates that the cause is
     *            nonexistent or unknown.)
     */
    public FrameworkException (final String message, final Throwable cause)
    {
        super (message, cause);
    }
}
