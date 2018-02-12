// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data;

import de.mossgrabers.framework.daw.ObserverManagement;


/**
 * Interface to a result item.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IResultItem extends ObserverManagement
{
    /**
     * Get the index.
     *
     * @return The index
     */
    int getIndex ();


    /**
     * Does the slot exist?
     *
     * @return True if it exists
     */
    boolean doesExist ();


    /**
     * get the name of the slot.
     *
     * @return The name of the slot
     */
    String getName ();


    /**
     * Is the slot selected?
     *
     * @return True if selected
     */
    boolean isSelected ();


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
     * True if the slot is queued for playback or recording.
     *
     * @return True if the slot is queued for playback or recording.
     */
    boolean isQueued ();


    /**
     * Get the color of the slot.
     *
     * @return The color
     */
    double [] getColor ();
}