// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.core;

/**
 * A continuous command is initiated by a knob or fader event.
 *
 * @author Jürgen Moßgraber
 */
@FunctionalInterface
public interface ContinuousCommand
{
    /**
     * Execute the command.
     *
     * @param value The updated value
     */
    void execute (final int value);
}
