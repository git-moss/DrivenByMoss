// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data;

/**
 * The position of a step in a grid.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 *
 * @param channel The MIDI channel
 * @param step The step to edit
 * @param note The note to edit
 */
public record GridStep (int channel, int step, int note)
{
    // Intentionally empty
}
