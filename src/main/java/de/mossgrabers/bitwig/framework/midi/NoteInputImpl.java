// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.midi;

import de.mossgrabers.framework.daw.midi.INoteInput;
import de.mossgrabers.framework.daw.midi.INoteRepeat;

import com.bitwig.extension.controller.api.NoteInput;


/**
 * Implementation for a note input.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
class NoteInputImpl implements INoteInput
{
    private final NoteInput   noteInput;
    private final INoteRepeat noteRepeat;


    /**
     * Constructor.
     *
     * @param noteInput The Bitwig note input to wrap
     */
    public NoteInputImpl (final NoteInput noteInput)
    {
        this.noteInput = noteInput;
        // Still forward MIDI notes to the registered callback handler even if MIDI notes are fed
        // directly into Bitwig
        noteInput.setShouldConsumeEvents (false);

        this.noteRepeat = new NoteRepeatImpl (this.noteInput.arpeggiator ());
    }


    /** {@inheritDoc} */
    @Override
    public void setKeyTranslationTable (final Integer [] table)
    {
        this.noteInput.setKeyTranslationTable (table);
    }


    /** {@inheritDoc} */
    @Override
    public void setVelocityTranslationTable (final Integer [] table)
    {
        this.noteInput.setVelocityTranslationTable (table);
    }


    /** {@inheritDoc} */
    @Override
    public INoteRepeat getNoteRepeat ()
    {
        return this.noteRepeat;
    }


    public void sendRawMidiEvent (final int status, final int data1, final int data2)
    {
        this.noteInput.sendRawMidiEvent (status, data1, data2);
    }
}
