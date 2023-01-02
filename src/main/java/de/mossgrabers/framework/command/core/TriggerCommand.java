// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.core;

import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * A trigger command is initiated by a button event.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
@FunctionalInterface
public interface TriggerCommand
{
    /**
     * Execute the command.
     *
     * @param event The button event that initiated the command
     * @param velocity The pressure or release velocity that was applied
     */
    void execute (ButtonEvent event, int velocity);
}
