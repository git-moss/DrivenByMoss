// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.mode;

import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.daw.clip.NotePosition;

import java.util.ArrayList;
import java.util.List;


/**
 * Helper class for managing notes which are edited.
 *
 * @author Jürgen Moßgraber
 */
public class NoteEditor implements INoteMode
{
    private INoteClip                clip  = null;
    private final List<NotePosition> notes = new ArrayList<> ();


    /**
     * Constructor.
     */
    public NoteEditor ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public INoteClip getClip ()
    {
        return this.clip;
    }


    /** {@inheritDoc} */
    @Override
    public void clearNotes ()
    {
        this.notes.clear ();
    }


    /** {@inheritDoc} */
    @Override
    public void setNote (final INoteClip clip, final NotePosition notePosition)
    {
        this.notes.clear ();
        this.addNote (clip, notePosition);
    }


    /** {@inheritDoc} */
    @Override
    public void addNote (final INoteClip clip, final NotePosition notePosition)
    {
        if (this.clip != clip)
        {
            this.notes.clear ();
            this.clip = clip;
        }

        // Is the note already edited? Remove it.
        for (final NotePosition gridStep: this.notes)
        {
            if (gridStep.equals (notePosition))
            {
                this.notes.remove (gridStep);
                return;
            }
        }

        this.notes.add (new NotePosition (notePosition.getChannel (), notePosition.getStep (), notePosition.getNote ()));
    }


    /** {@inheritDoc} */
    @Override
    public List<NotePosition> getNotes ()
    {
        return new ArrayList<> (this.notes);
    }


    /** {@inheritDoc} */
    @Override
    public List<NotePosition> getNotePosition (final int parameterIndex)
    {
        // Implementation for simple note edit modes, for complex modes getNotePosition needs to be
        // implemented in the mode itself

        return this.notes;
    }
}
