// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.utils;

import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.observer.INoteObserver;
import de.mossgrabers.framework.scale.Scales;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


/**
 * Manages pressed keys and drum pads on a grid. Note: There are 3 types of notes: 1) the MIDI notes
 * (or even CC) sent from the hardware devices grid (or keyboard), 2) the MIDI note which should be
 * used instead on the grid (normally this starts with MIDI note 36, e.g. an 8x8 grid uses [36..99]
 * to make coding independent from the hardware and 3) the note which is active according to the
 * active note map (e.g. different scale layouts). This class uses notes as input from 2) to store
 * pressed states for 3).
 *
 * @author Jürgen Moßgraber
 */
public class KeyManager implements INoteObserver
{
    private final int []   pressedKeys = new int [128];
    private final IModel   model;
    private final Scales   scales;
    private final IPadGrid padGrid;
    private int []         noteMap     = Scales.getEmptyMatrix ();


    /**
     * Constructor.
     *
     * @param model The model
     * @param scales The scales
     * @param padGrid A pad grid, can be null
     */
    public KeyManager (final IModel model, final Scales scales, final IPadGrid padGrid)
    {
        this.model = model;
        this.scales = scales;
        this.padGrid = padGrid;
        Arrays.fill (this.pressedKeys, 0);
    }


    /**
     * Clears all pressed keys.
     */
    public void clearPressedKeys ()
    {
        Arrays.fill (this.pressedKeys, 0);
    }


    /**
     * Returns true if the given 'key' (note) is currently pressed.
     *
     * @param key The key to test
     * @return True if pressed
     */
    public boolean isKeyPressed (final int key)
    {
        return this.pressedKeys[key] > 0;
    }


    /**
     * Set a pressed key.
     *
     * @param key The key to set (this is a MIDI note)
     * @param velocity The velocity
     */
    public void setKeyPressed (final int key, final int velocity)
    {
        this.pressedKeys[key] = velocity;
    }


    /**
     * Loop over all pads since the note can be present multiple time.
     *
     * @param key The key to set (this is a MIDI note)
     * @param velocity The velocity
     */
    public void setAllKeysPressed (final int key, final int velocity)
    {
        for (int i = 0; i < 128; i++)
        {
            if (this.noteMap[i] == key)
                this.setKeyPressed (i, velocity);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void call (final int trackIndex, final int note, final int velocity)
    {
        final Optional<ITrack> sel = this.model.getCurrentTrackBank ().getSelectedItem ();
        if (sel.isPresent () && sel.get ().getIndex () == trackIndex)
            this.setAllKeysPressed (note, velocity);
    }


    /**
     * Get the currently pressed keys.
     *
     * @return The list with the keys
     */
    public List<Integer> getPressedKeys ()
    {
        final List<Integer> keys = new ArrayList<> ();
        for (int i = 0; i < 128; i++)
        {
            if (this.pressedKeys[i] != 0)
                keys.add (Integer.valueOf (i));
        }
        return keys;
    }


    /**
     * Check if there are pressed keys.
     *
     * @return True if there is at least one pressed key
     */
    public boolean hasPressedKeys ()
    {
        for (int i = 0; i < 128; i++)
        {
            if (this.pressedKeys[i] != 0)
                return true;
        }
        return false;
    }


    /**
     * Get the MIDI note from the grid.
     *
     * @param note The note on the grid
     * @return The translated note depending on applied scales, etc.
     */
    public int getMidiNoteFromGrid (final int note)
    {
        return this.padGrid == null ? -1 : this.map (this.padGrid.translateToGrid (note));
    }


    /**
     * Get the mapped note from the current note matrix.
     *
     * @param note The note which is assigned to a pad
     * @return The note which should be used instead according to the active note map
     */
    public int map (final int note)
    {
        return note < 0 ? -1 : this.noteMap[note];
    }


    /**
     * Get the ID of the color to use for a pad with respect to the current scale settings.
     *
     * @param pad The MIDI note of the pad
     * @return The color ID
     */
    public String getColor (final int pad)
    {
        return this.scales.getColor (this.noteMap, pad);
    }


    /**
     * Set a new note matrix.
     *
     * @param matrix The new matrix
     */
    public void setNoteMatrix (final int [] matrix)
    {
        this.noteMap = matrix;
    }
}
