// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.osc.exception;

/**
 * Exception for an unknown OSC command.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class UnknownCommandException extends Exception
{
    private static final long serialVersionUID = -2747212585991228126L;


    /**
     * Constructor.
     *
     * @param command The unknown command
     */
    public UnknownCommandException (final String command)
    {
        super (command);
    }
}
