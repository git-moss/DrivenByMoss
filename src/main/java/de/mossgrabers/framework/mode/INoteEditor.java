// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.mode;

import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.daw.clip.NotePosition;

import java.util.List;


/**
 * Interface to a note editor which manages several notes for editing.
 *
 * @author Jürgen Moßgraber
 */
public interface INoteEditor
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
     * Remove a note from editing.
     *
     * @param clip The clip to edit
     * @param notePosition The position of the note
     */
    void removeNote (INoteClip clip, NotePosition notePosition);


    /**
     * Check if the note is currently edited.
     *
     * @param clip The clip to edit
     * @param notePosition The position of the note
     * @return True if edited
     */
    boolean isNoteEdited (INoteClip clip, NotePosition notePosition);


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
