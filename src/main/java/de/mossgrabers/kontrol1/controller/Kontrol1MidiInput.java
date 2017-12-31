// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.kontrol1.controller;

import de.mossgrabers.framework.midi.MidiInput;

import com.bitwig.extension.controller.api.NoteInput;


/**
 * The Kontrol 1 midi input
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Kontrol1MidiInput extends MidiInput
{
    private final String inputName;


    /**
     * Constructor.
     */
    public Kontrol1MidiInput ()
    {
        this.inputName = "Komplete Kontrol 1";
    }


    /** {@inheritDoc} */
    @Override
    public NoteInput createNoteInput ()
    {
        return this.createNoteInputBase (this.inputName, new String []
        {
            "80????", // Note off
            "90????", // Note on
            "B0????", // Sustainpedal
            "D0????", // Channel Aftertouch
            "E0????" // Pitchbend
        });
    }
}