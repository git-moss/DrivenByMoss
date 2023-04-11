// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.mode;

import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.daw.clip.NotePosition;

import java.util.List;


/**
 * Interface to a note editing mode.
 *
 * @author Jürgen Moßgraber
 */
public interface INoteMode
{
    /**
     * Set a note for editing.
     *
     * @param clip The clip to edit
     * @param notePosition The position of the note
     */
    void setNote (INoteClip clip, NotePosition notePosition);


    /**
     * Add a note for editing.
     *
     * @param clip The clip to edit
     * @param notePosition The position of the note
     */
    void addNote (INoteClip clip, NotePosition notePosition);


    /**
     * Clear all edit notes.
     */
    void clearNotes ();


    /**
     * Get the currently edited notes.
     *
     * @return The notes
     */
    List<NotePosition> getNotes ();


    /**
     * Get the clip.
     *
     * @return The clip
     */
    INoteClip getClip ();


    /**
     * Get the positions of the notes to edit for a given parameter.
     *
     * @param parameterIndex The index of the parameter for which to get the note position
     * @return The note positions
     */
    List<NotePosition> getNotePosition (int parameterIndex);
}
