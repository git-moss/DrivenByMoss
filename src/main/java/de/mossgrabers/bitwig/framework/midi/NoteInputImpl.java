// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.midi;

import de.mossgrabers.framework.daw.midi.INoteInput;

import com.bitwig.extension.controller.api.NoteInput;


/**
 * Implementation for a note input.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
class NoteInputImpl implements INoteInput
{
    private final NoteInput noteInput;


    /**
     * Constructor.
     *
     * @param noteInput The Bitwig note input to wrap
     */
    public NoteInputImpl (final NoteInput noteInput)
    {
        this.noteInput = noteInput;
        noteInput.setShouldConsumeEvents (false);
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
}
