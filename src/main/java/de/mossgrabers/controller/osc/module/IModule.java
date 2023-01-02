// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.osc.module;

import de.mossgrabers.controller.osc.exception.IllegalParameterException;
import de.mossgrabers.controller.osc.exception.MissingCommandException;
import de.mossgrabers.controller.osc.exception.UnknownCommandException;

import java.util.LinkedList;


/**
 * Interface for OSC modules who write / parse OSC commands.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IModule
{
    /**
     * Get the commands which are supported by this module.
     *
     * @return The command names
     */
    String [] getSupportedCommands ();


    /**
     * Parse and execute an OSC command.
     *
     * @param command The first part of the command
     * @param path The rest of the path commands
     * @param value A value parameter for the command, may be null
     * @throws IllegalParameterException Wrong or missing value parameter
     * @throws UnknownCommandException Unknown command
     * @throws MissingCommandException Missing sub-command
     */
    void execute (String command, LinkedList<String> path, Object value) throws IllegalParameterException, UnknownCommandException, MissingCommandException;


    /**
     * Send all related data of this module via OSC messages.
     *
     * @param dump Ignore cache if true
     */
    void flush (boolean dump);
}
