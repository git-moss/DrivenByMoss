// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.osc.exception;

/**
 * Exception for a missing OSC (sub-)command.
 *
 * @author Jürgen Moßgraber
 */
public class MissingCommandException extends Exception
{
    private static final long serialVersionUID = -6729074086275827225L;


    /**
     * Constructor.
     */
    public MissingCommandException ()
    {
        // Intentionally empty
    }
}
