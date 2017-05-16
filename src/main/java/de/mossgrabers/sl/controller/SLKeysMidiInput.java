// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.sl.controller;

import de.mossgrabers.framework.midi.MidiInput;

import com.bitwig.extension.controller.api.NoteInput;


/**
 * The SL midi input
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SLKeysMidiInput extends MidiInput
{
    private final String inputName;


    /**
     * Constructor.
     *
     * @param isMkII Is MkI or MkII?
     */
    public SLKeysMidiInput (final boolean isMkII)
    {
        super (1);
        this.inputName = isMkII ? "Novation SL MkII (Keyboard)" : "Novation SL MkI (Keyboard)";
    }


    /** {@inheritDoc} */
    @Override
    public NoteInput createNoteInput ()
    {
        return this.createNoteInputBase (this.inputName, "80????", "90????", "B0????", "D0????", "E0????");
    }
}