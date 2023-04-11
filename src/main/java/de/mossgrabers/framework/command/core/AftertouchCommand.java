// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.core;

/**
 * An aftertouch command.
 *
 * @author Jürgen Moßgraber
 */
public interface AftertouchCommand
{
    /**
     * Poly aftertouch on the grid pad was received.
     *
     * @param note The note of the pad
     * @param value The aftertouch of the note
     */
    void onPolyAftertouch (int note, int value);


    /**
     * Channel aftertouch on the grid pad was received.
     *
     * @param value The aftertouch value
     */
    void onChannelAftertouch (int value);
}
