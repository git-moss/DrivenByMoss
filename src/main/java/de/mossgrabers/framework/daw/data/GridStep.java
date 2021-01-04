// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data;

import de.mossgrabers.framework.daw.INoteClip;


/**
 * The position of a step in a grid.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class GridStep
{
    private INoteClip clip    = null;
    private int       channel = 0;
    private int       step    = 0;
    private int       note    = 60;


    /**
     * Set the values.
     *
     * @param clip The clip to edit
     * @param channel The MIDI channel
     * @param step The step to edit
     * @param note The note to edit
     */
    public void set (final INoteClip clip, final int channel, final int step, final int note)
    {
        this.clip = clip;
        this.channel = channel;
        this.step = step;
        this.note = note;
    }


    /**
     * Clears the clip. isSet will return false after calling this method.
     */
    public void reset ()
    {
        this.clip = null;
    }


    /**
     * Returns true if the data is set (clip is not null)
     *
     * @return True if set.
     */
    public boolean isSet ()
    {
        return this.clip != null;
    }


    /**
     * Get the clip which cootains the step position.
     *
     * @return The clip
     */
    public INoteClip getClip ()
    {
        return this.clip;
    }


    /**
     * Get the MIDI channel of the step.
     *
     * @return The MIDI channel 0-15
     */
    public int getChannel ()
    {
        return this.channel;
    }


    /**
     * The step in the grid. Depending on the grid size, e.g. 8 or 16.
     *
     * @return The step 0..size - 1
     */
    public int getStep ()
    {
        return this.step;
    }


    /**
     * Get the note of the step.
     *
     * @return The note
     */
    public int getNote ()
    {
        return this.note;
    }
}
