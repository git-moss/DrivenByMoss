// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.osc.exception;

/**
 * Exception for an OSC command with a missing or wrong parameter.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class IllegalParameterException extends Exception
{
    private static final long serialVersionUID = 8078356944040089800L;


    /**
     * Constructor.
     *
     * @param message The message text
     */
    public IllegalParameterException (final String message)
    {
        super (message);
    }
}
