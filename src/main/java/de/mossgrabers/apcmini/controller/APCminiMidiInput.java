// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.apcmini.controller;

import de.mossgrabers.framework.midi.MidiInput;

import com.bitwig.extension.controller.api.NoteInput;


/**
 * The APC40 midi input
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class APCminiMidiInput extends MidiInput
{
    private final String inputName;


    /**
     * Constructor.
     */
    public APCminiMidiInput ()
    {
        this.inputName = "Akai APCmini";
    }


    /** {@inheritDoc} */
    @Override
    public NoteInput createNoteInput ()
    {
        return this.createNoteInputBase (this.inputName);
    }
}