// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.controller;

import de.mossgrabers.framework.midi.MidiInput;

import com.bitwig.extension.controller.api.NoteInput;


/**
 * The Push midi input
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PushMidiInput extends MidiInput
{
    private final String inputName;


    /**
     * Constructor.
     *
     * @param isPush2 Is Push 2 or 1
     */
    public PushMidiInput (final boolean isPush2)
    {
        this.inputName = isPush2 ? "Ableton Push 2" : "Ableton Push 1";
    }


    /** {@inheritDoc} */
    @Override
    public NoteInput createNoteInput ()
    {
        return this.createNoteInputBase (this.inputName, "80????", // Note off
                "90????", // Note on
                "B040??" // Sustainpedal
        );
    }
}