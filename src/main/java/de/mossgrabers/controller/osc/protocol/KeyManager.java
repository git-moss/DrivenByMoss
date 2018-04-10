// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.osc.protocol;

import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.NoteObserver;
import de.mossgrabers.framework.scale.Scales;

import java.util.Arrays;


/**
 * Manages pressed keys and drum pads.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class KeyManager implements NoteObserver
{
    private final Scales scales;

    private final int [] pressedKeys      = new int [128];
    private int []       keysTranslation  = null;
    private int []       drumsTranslation = null;


    /**
     * Constructor.
     *
     * @param model The model
     */
    public KeyManager (final IModel model)
    {
        this.scales = model.getScales ();

        Arrays.fill (this.pressedKeys, 0);
        this.updateNoteMapping ();
    }


    /**
     * Update the drum and key note maps.
     */
    public void updateNoteMapping ()
    {
        this.drumsTranslation = this.scales.getDrumMatrix ();
        this.keysTranslation = this.scales.getNoteMatrix ();
    }


    /**
     * Clears all pressed keys.
     */
    public void clearPressedKeys ()
    {
        for (int i = 0; i < 128; i++)
            this.pressedKeys[i] = 0;
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
     * @param key The key to set
     * @param velocity The velocity
     */
    public void setKeyPressed (final int key, final int velocity)
    {
        this.pressedKeys[key] = velocity;
    }


    /**
     * Get the key translation matrix.
     *
     * @return The matrix
     */
    public int [] getKeyTranslationMatrix ()
    {
        return this.keysTranslation;
    }


    /**
     * Get the drum translation matrix.
     *
     * @return The matrix
     */
    public int [] getDrumTranslationMatrix ()
    {
        return this.drumsTranslation;
    }


    /** {@inheritDoc} */
    @Override
    public void call (final int note, final int velocity)
    {
        for (int i = 0; i < 128; i++)
        {
            if (this.keysTranslation[i] == note)
                this.pressedKeys[i] = velocity;
        }
    }
}
