// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data;

/**
 * Interface to a track.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface ITrack extends IChannel
{
    /**
     * Get the type of the track.
     *
     * @return The type
     */
    String getType ();


    /**
     * Get the position of the track in all tracks.
     *
     * @return The position
     */
    int getPosition ();


    /**
     * Is the track a group?
     *
     * @return True if the track is a group
     */
    boolean isGroup ();


    /**
     * True if armed for recording.
     *
     * @return True if armed for recording.
     */
    boolean isRecArm ();


    /**
     * True if monitoring is on.
     *
     * @return True if monitoring is on.
     */
    boolean isMonitor ();


    /**
     * True if auto monitoring is on.
     *
     * @return True if auto monitoring is on.
     */
    boolean isAutoMonitor ();


    /**
     * Returns true if the track can hold note data.
     *
     * @return True if the track can hold note data.
     */
    boolean canHoldNotes ();


    /**
     * Returns true if the track can hold audio data.
     *
     * @return True if the track can hold audio data.
     */
    boolean canHoldAudioData ();


    /**
     * Get the crossfade mode (A, B, AB).
     *
     * @return The crossfade mode
     */
    String getCrossfadeMode ();


    /**
     * Get the clip slots of the track.
     *
     * @return The clip slots
     */
    ISlot [] getSlots ();


    /**
     * Returns true if a clip is playing on the track.
     *
     * @return True if a clip is playing on the track.
     */
    boolean isPlaying ();
}