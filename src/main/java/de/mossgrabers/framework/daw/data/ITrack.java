// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data;

import de.mossgrabers.framework.daw.constants.RecordQuantization;
import de.mossgrabers.framework.daw.data.bank.ISlotBank;


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
     * Returns true if there is a parent track.
     *
     * @return True if there is a parent track otherwise it is a top level track
     */
    boolean hasParent ();


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
     * Creates a new clip at the given track and slot index (or greater).
     *
     * @param slotIndex The index of the slot
     * @param lengthInBeats The length of the new clip
     */
    void createClip (int slotIndex, int lengthInBeats);


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


    /**
     * Test if record quantization for note lengths is enabled.
     *
     * @return True if enabled
     */
    boolean isRecordQuantizationNoteLength ();


    /**
     * Toggle record quantization note length enablement.
     */
    void toggleRecordQuantizationNoteLength ();


    /**
     * Get the record quantization grid.
     *
     * @return The record quantization grid resolution
     */
    RecordQuantization getRecordQuantizationGrid ();


    /**
     * Set the record quantization grid.
     *
     * @param recordQuantization The record quantization grid resolution
     */
    void setRecordQuantizationGrid (RecordQuantization recordQuantization);


    /**
     * Add an equalizer device to this track.
     */
    void addEqualizerDevice ();
}