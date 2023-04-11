// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.generic.flexihandler;

import de.mossgrabers.controller.generic.controller.FlexiCommand;
import de.mossgrabers.controller.generic.flexihandler.utils.KnobMode;
import de.mossgrabers.controller.generic.flexihandler.utils.MidiValue;


/**
 * Interface for handlers of flexi commands.
 *
 * @author Jürgen Moßgraber
 */
public interface IFlexiCommandHandler
{
    /**
     * Get the commands which are supported by this handler.
     *
     * @return The commands
     */
    FlexiCommand [] getSupportedCommands ();


    /**
     * Get the current value of a command.
     *
     * @param command The command
     * @return The value or -1
     */
    int getCommandValue (FlexiCommand command);


    /**
     * Execute the given command (if supported).
     *
     * @param command The command to execute
     * @param knobMode If a knob is mapped this is the type of the knob (relative, absolute, ...)
     * @param value The knob or button value
     */
    void handle (FlexiCommand command, KnobMode knobMode, MidiValue value);
}
