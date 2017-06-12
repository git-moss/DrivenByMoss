// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.mcu.controller;

import de.mossgrabers.framework.midi.MidiInput;

import com.bitwig.extension.controller.api.NoteInput;


/**
 * The MCU midi input.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MCUMidiInput extends MidiInput
{
    /**
     * Constructor.
     */
    public MCUMidiInput ()
    {
    }


    /** {@inheritDoc} */
    @Override
    public NoteInput createNoteInput ()
    {
        return null;
    }
}