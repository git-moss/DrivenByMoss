// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller.color;

/**
 * Exception when a color is already registered under an index or does not exist at that index.
 *
 * @author Jürgen Moßgraber
 */
public class ColorIndexException extends RuntimeException
{
    private static final long serialVersionUID = -9195189051324855572L;


    /**
     * Constructor.
     *
     * @param message The message of the exception
     */
    public ColorIndexException (final String message)
    {
        super (message);
    }
}
