// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data;

/**
 * The position of a step in a grid.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class GridStep
{
    private final int channel;
    private final int step;
    private final int note;


    /**
     * Constructor.
     *
     * @param channel The MIDI channel
     * @param step The step to edit
     * @param note The note to edit
     */
    public GridStep (final int channel, final int step, final int note)
    {
        this.channel = channel;
        this.step = step;
        this.note = note;
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
