// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.core;

import de.mossgrabers.framework.ButtonEvent;


/**
 * A trigger command is initiated by a button event.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface TriggerCommand
{
    /**
     * Execute the command.
     *
     * @param event The button event that initiated the command
     */
    void execute (ButtonEvent event);


    /**
     * Execute normal (without shift pressed).
     *
     * @param event The button event
     */
    void executeNormal (ButtonEvent event);


    /**
     * Execute when shift is pressed.
     *
     * @param event The button event
     */
    void executeShifted (ButtonEvent event);
}
