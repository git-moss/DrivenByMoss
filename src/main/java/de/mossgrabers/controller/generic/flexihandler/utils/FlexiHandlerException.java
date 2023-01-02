// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.generic.flexihandler.utils;

import de.mossgrabers.controller.generic.controller.FlexiCommand;


/**
 * Exception for unsupported flexi commands.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class FlexiHandlerException extends RuntimeException
{
    private static final long serialVersionUID = 3770417875862714290L;


    /**
     * Constructor.
     *
     * @param command The unsupported command
     */
    public FlexiHandlerException (final FlexiCommand command)
    {
        super ("Unsupported flexi command: " + command.getName ());
    }
}
