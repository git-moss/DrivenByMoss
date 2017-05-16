// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.osc.protocol;

import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.controller.ValueChanger;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.CursorClipProxy;
import de.mossgrabers.framework.daw.TrackBankProxy;
import de.mossgrabers.framework.scale.Scales;

import com.bitwig.extension.controller.api.ControllerHost;

import java.util.Arrays;


/**
 * The model with some extensions for OSC.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class OSCModel extends Model
{
    private int []          keysTranslation  = null;
    private int []          drumsTranslation = null;

    private int []          pressedKeys      = new int [128];
    private CursorClipProxy clip;


    /**
     * Constructor.
     *
     * @param host The host
     * @param colorManager The color manager
     * @param valueChanger The value changer
     * @param scales The scales
     */
    public OSCModel (final ControllerHost host, final ColorManager colorManager, final ValueChanger valueChanger, final Scales scales)
    {
        super (host, colorManager, valueChanger, scales, 8, 8, 8, 16, 16, false, 8, 8, 8, 16);

        this.updateNoteMapping ();
        Arrays.fill (this.pressedKeys, 0);

        final TrackBankProxy tb = this.getTrackBank ();
        tb.addNoteObserver ( (note, velocity) -> {
            // Light notes send from the sequencer
            for (int i = 0; i < 128; i++)
            {
                if (this.keysTranslation[i] == note)
                    this.pressedKeys[i] = velocity;
            }
        });
        tb.addTrackSelectionObserver ( (index, isSelected) -> this.clearPressedKeys ());

        this.clip = this.createCursorClip (8, 8);
    }


    /**
     * Update the drum and key note maps.
     */
    public void updateNoteMapping ()
    {
        this.drumsTranslation = this.scales.getDrumMatrix ();
        this.keysTranslation = this.scales.getNoteMatrix ();
    }


    private void clearPressedKeys ()
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


    /**
     * Get the clip.
     *
     * @return The clip
     */
    public CursorClipProxy getClip ()
    {
        return this.clip;
    }
}
