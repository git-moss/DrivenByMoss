// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.clip;

/**
 * A position of a note in a clip. The position is described by its' MIDI channel, the index of the
 * sequencer step and the MIDI note (= the row in the clip).
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public final class NotePosition
{
    private int channel;
    private int step;
    private int note;


    /**
     * Default constructor.
     */
    public NotePosition ()
    {
        // Intentionally empty
    }


    /**
     * Default constructor.
     *
     * @param notePosition The note position to use to initialize the values
     */
    public NotePosition (final NotePosition notePosition)
    {
        this.set (notePosition);
    }


    /**
     * Constructor.
     *
     * @param channel The MIDI channel
     * @param step The step
     * @param note The note
     */
    public NotePosition (final int channel, final int step, final int note)
    {
        this.channel = channel;
        this.step = step;
        this.note = note;
    }


    /**
     * Get the MIDI channel.
     *
     * @return The MIDI channel
     */
    public int getChannel ()
    {
        return this.channel;
    }


    /**
     * Set the MIDI channel.
     *
     * @param channel The MIDI channel, 0-15
     */
    public void setChannel (final int channel)
    {
        this.channel = channel;
    }


    /**
     * Get the step.
     *
     * @return The step
     */
    public int getStep ()
    {
        return this.step;
    }


    /**
     * Set the step.
     *
     * @param step The step
     */
    public void setStep (final int step)
    {
        this.step = step;
    }


    /**
     * Get the MIDI note.
     *
     * @return The note
     */
    public int getNote ()
    {
        return this.note;
    }


    /**
     * Set the MIDI note.
     *
     * @param note The note, 0-127
     */
    public void setNote (final int note)
    {
        this.note = note;
    }


    /**
     * Copies the values of the given note position into this note position.
     *
     * @param notePosition The source note position
     */
    public void set (final NotePosition notePosition)
    {
        this.channel = notePosition.getChannel ();
        this.step = notePosition.getStep ();
        this.note = notePosition.getNote ();
    }


    /** {@inheritDoc} */
    @Override
    public int hashCode ()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.channel;
        result = prime * result + this.note;
        result = prime * result + this.step;
        return result;
    }


    /** {@inheritDoc} */
    @Override
    public boolean equals (final Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null || this.getClass () != obj.getClass ())
            return false;
        final NotePosition other = (NotePosition) obj;
        if (this.channel != other.channel || this.note != other.note)
            return false;
        return this.step == other.step;
    }
}
