// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.scale;

import java.util.Arrays;


/**
 * Keeps track of playing MPE notes.
 *
 * @author Jürgen Moßgraber
 */
public class MPEStatus
{
    private final int [] notes = new int [16];


    /**
     * Constructor.
     */
    public MPEStatus ()
    {
        Arrays.fill (this.notes, -1);
    }


    /**
     * Store or clear the note.
     *
     * @param channel The MIDI channel of the MPE note
     * @param note The MIDI note
     * @param isOn True if note is played or released
     */
    public void handleNote (final int channel, final int note, final boolean isOn)
    {
        this.notes[channel] = isOn ? note : -1;
    }


    /**
     * Get the note currently playing on the channel.
     *
     * @param channel The MIDI channel
     * @return THe note or -1 if no note is playing on this channel
     */
    public int getNoteStatus (final int channel)
    {
        return this.notes[channel];
    }
}
