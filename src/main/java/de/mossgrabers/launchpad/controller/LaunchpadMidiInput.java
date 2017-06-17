// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.launchpad.controller;

import de.mossgrabers.framework.midi.MidiInput;

import com.bitwig.extension.controller.api.NoteInput;


/**
 * The Launchpad midi input
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class LaunchpadMidiInput extends MidiInput
{
    private final String inputName;


    /**
     * Constructor.
     *
     * @param isPro Is Pro or MkII?
     */
    public LaunchpadMidiInput (final boolean isPro)
    {
        this.inputName = isPro ? "Novation Launchpad Pro" : "Novation Launchpad MkII";
    }


    /** {@inheritDoc} */
    @Override
    public NoteInput createNoteInput ()
    {
        return this.createNoteInputBase (this.inputName, new String []
        {
            "80????", // Note off
            "90????" // Note on
        });
    }
}