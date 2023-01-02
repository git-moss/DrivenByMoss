// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller.hardware;

/**
 * Exception for a not supported MIDI binding.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class BindException extends RuntimeException
{
    private static final long serialVersionUID = 3866286220354473693L;


    /**
     * Constructor.
     *
     * @param type The MIDI binding type, which is not supported
     */
    public BindException (final BindType type)
    {
        super ("Binding type " + type + " is not supported.");
    }
}
