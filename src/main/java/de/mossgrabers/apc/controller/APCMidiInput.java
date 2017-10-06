// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.apc.controller;

import de.mossgrabers.framework.midi.MidiInput;

import com.bitwig.extension.controller.api.NoteInput;


/**
 * The APC40 midi input
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class APCMidiInput extends MidiInput
{
    private final String inputName;


    /**
     * Constructor.
     *
     * @param isMkII Is APC40 2 or 1?
     */
    public APCMidiInput (final boolean isMkII)
    {
        this.inputName = isMkII ? "Akai APC40 mkII" : "Akai APC40";
    }


    /** {@inheritDoc} */
    @Override
    public NoteInput createNoteInput ()
    {
        return this.createNoteInputBase (this.inputName, new String []
        {
            "B040??" // Sustain pedal
        });
    }
}