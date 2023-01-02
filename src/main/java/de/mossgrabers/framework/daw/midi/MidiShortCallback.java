// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.midi;

/**
 * Callback for receiving MIDI short messages.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface MidiShortCallback
{
    /**
     * Handle received MIDI data.
     *
     * @param status The MIDI status byte
     * @param data1 The MIDI data byte 1
     * @param data2 The MIDI data byte 2
     */
    void handleMidi (final int status, final int data1, final int data2);
}
