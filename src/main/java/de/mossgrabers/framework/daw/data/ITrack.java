// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data;

import de.mossgrabers.framework.daw.ISlotBank;


/**
 * Interface to a track.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface ITrack extends IChannel
{
    /**
     * Is the track a group?
     *
     * @return True if the track is a group
     */
    boolean isGroup ();


    /**
     * True if armed for recording.
     *
     * @return True if armed for recording
     */
    boolean isRecArm ();


    /**
     * Turn on/off record arm.
     *
     * @param value True to turn arm the track for recording, otherwise off
     */
    void setRecArm (boolean value);


    /**
     * Toggle record arm.
     */
    void toggleRecArm ();


    /**
     * True if monitoring is on.
     *
     * @return True if monitoring is on
     */
    boolean isMonitor ();


    /**
     * Turn on/off track monitoring.
     *
     * @param value True to turn on track monitoring, otherwise off
     */
    void setMonitor (boolean value);


    /**
     * Toggle monitor.
     */
    void toggleMonitor ();


    /**
     * True if auto monitoring is on.
     *
     * @return True if auto monitoring is on
     */
    boolean isAutoMonitor ();


    /**
     * Turn on/off auto track monitoring.
     *
     * @param value True to turn on auto track monitoring, otherwise off
     */
    void setAutoMonitor (boolean value);


    /**
     * Toggle auto monitor.
     */
    void toggleAutoMonitor ();


    /**
     * Toggle the note repeat state of the track (only applies to midi/note tracks).
     */
    void toggleNoteRepeat ();


    /**
     * Set the note repeat length.
     *
     * @param length The length
     */
    void setNoteRepeatLength (double length);


    /**
     * Get the note repeat length.
     *
     * @return The length
     */
    double getNoteRepeatLength ();


    /**
     * True if note repeat is enabled.
     *
     * @return True if note repeat is enabled
     */
    boolean isNoteRepeat ();


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
     * Change the crossfade mode.
     *
     * @param control The control value
     */
    void changeCrossfadeModeAsNumber (final int control);


    /**
     * Set the crossfade mode.
     *
     * @param mode The crossfade mode A, AB or B
     */
    void setCrossfadeMode (final String mode);


    /**
     * Get the crossfade mode as a number.
     *
     * @return The crossfade mode 0, 1 or 2
     */
    int getCrossfadeModeAsNumber ();


    /**
     * Set the crossfade mode as a number.
     *
     * @param modeValue The crossfade mode 0, 1 or 2
     */
    void setCrossfadeModeAsNumber (final int modeValue);


    /**
     * Set the crossfade mode to the next value.
     */
    void toggleCrossfadeMode ();


    /**
     * Get the slot bank.
     *
     * @return The slot bank
     */
    ISlotBank getSlotBank ();


    /**
     * Returns true if a clip is playing on the track.
     *
     * @return True if a clip is playing on the track.
     */
    boolean isPlaying ();


    /**
     * Stop playback on the track.
     */
    void stop ();


    /**
     * Switch playback back to the arrangement.
     */
    void returnToArrangement ();
}