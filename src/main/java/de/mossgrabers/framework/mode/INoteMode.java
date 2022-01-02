// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.mode;

import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.daw.data.GridStep;

import java.util.List;


/**
 * Interface to a note editing mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface INoteMode
{
    /**
     * Set a note for editing.
     *
     * @param clip The clip to edit
     * @param channel The MIDI channel
     * @param step The step to edit
     * @param note The note to edit
     */
    void setNote (INoteClip clip, int channel, int step, int note);


    /**
     * Add a note for editing.
     *
     * @param clip The clip to edit
     * @param channel The MIDI channel
     * @param step The step to edit
     * @param note The note to edit
     */
    void addNote (INoteClip clip, int channel, int step, int note);


    /**
     * Clear all edit notes.
     */
    void clearNotes ();


    /**
     * Get the currently edited notes.
     *
     * @return The notes
     */
    List<GridStep> getNotes ();
}
