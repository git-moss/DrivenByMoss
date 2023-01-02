// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data;

import de.mossgrabers.framework.controller.color.ColorEx;


/**
 * Interface to a slot.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface ISlot extends IItem
{
    /**
     * Does the slot have content?
     *
     * @return True if it has content
     */
    boolean hasContent ();


    /**
     * Is the slot recording?
     *
     * @return True if it is recording
     */
    boolean isRecording ();


    /**
     * Is the slot playing?
     *
     * @return True if the slot is playing
     */
    boolean isPlaying ();


    /**
     * True if the slot is queued for playback.
     *
     * @return True if the slot is queued for playback.
     */
    boolean isPlayingQueued ();


    /**
     * True if the slot is queued for recording.
     *
     * @return True if the slot is queued for recording.
     */
    boolean isRecordingQueued ();


    /**
     * True if the slot is queued to be stopped.
     *
     * @return True if the slot is queued to be stopped.
     */
    boolean isStopQueued ();


    /**
     * Get the color of the slot.
     *
     * @return The color
     */
    ColorEx getColor ();


    /**
     * Set the color of the slot.
     *
     * @param color The color
     */
    void setColor (ColorEx color);


    /**
     * Launch a clip.
     */
    void launch ();


    /**
     * Launch the clip immediately without any quantization.
     */
    void launchImmediately ();


    /**
     * Check if the slot was launched with immediately option and resets that state.
     *
     * @return True if it was launched with immediately option
     */
    boolean testAndClearLaunchedImmediately ();


    /**
     * Record a clip.
     */
    void startRecording ();


    /**
     * Delete a clip.
     */
    void remove ();


    /**
     * Duplicate a clip.
     */
    void duplicate ();


    /**
     * Past the content of the given slot into this slot.
     *
     * @param slot The slot to paste into this one
     */
    void paste (ISlot slot);
}