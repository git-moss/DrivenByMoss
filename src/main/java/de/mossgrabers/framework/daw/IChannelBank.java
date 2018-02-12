// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw;

import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ITrack;


/**
 * Interface to a channel bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IChannelBank extends ObserverManagement
{
    /**
     * Get the number of all tracks.
     *
     * @return The number of all tracks
     */
    int getTrackCount ();


    /**
     * Is there a page left of the current?
     *
     * @return True if there is a page left of the current
     */
    boolean canScrollTracksUp ();


    /**
     * Is there a page right of the current?
     *
     * @return True if there is a page right of the current
     */
    boolean canScrollTracksDown ();


    /**
     * Is there a scene page left of the current?
     *
     * @return True if there is a scene page left of the current
     */
    boolean canScrollScenesUp ();


    /**
     * Is there a scene page right of the current?
     *
     * @return True if there is a scene page right of the current
     */
    boolean canScrollScenesDown ();


    /**
     * Registers a track selection observer.
     *
     * @param observer The observer to register
     */
    void addTrackSelectionObserver (final TrackSelectionObserver observer);


    /**
     * Returns true if one of the clips of the current bank page is recording.
     *
     * @return True if one of the clips of the current bank page is recording
     */
    boolean isClipRecording ();


    /**
     * Get a Track value object.
     *
     * @param index The index of the track in the bank page
     * @return The track
     */
    ITrack getTrack (final int index);


    /**
     * Get the first selected Track value object of the current page.
     *
     * @return The selected track or null if no track is selected on the current page
     */
    ITrack getSelectedTrack ();


    /**
     * Get the color ID of the current track.
     *
     * @return The color ID
     */
    String getSelectedTrackColorEntry ();


    /**
     * Select a track in the current bank page.
     *
     * @param index The index in the page
     */
    void select (final int index);


    /**
     * Duplicate a track in the current bank page.
     *
     * @param index The index in the page
     */
    void duplicate (final int index);


    /**
     * Make a track in the current bank page visible (scrolls to the track in Bitwig).
     *
     * @param index The index in the page
     */
    void makeVisible (final int index);


    /**
     * Change the volume.
     *
     * @param index The index of the track
     * @param control The control value
     */
    void changeVolume (final int index, final int control);


    /**
     * Set the volume.
     *
     * @param index The index of the track
     * @param value The new value
     */
    void setVolume (final int index, final double value);


    /**
     * Reset the volume to its default value.
     *
     * @param index The index of the track
     */
    void resetVolume (final int index);


    /**
     * Signal that the volume fader/knob is touched for automation recording.
     *
     * @param index The index of the track
     * @param isBeingTouched True if touched
     */
    void touchVolume (final int index, final boolean isBeingTouched);


    /**
     * Signal that the volume is edited.
     *
     * @param index The index of the track
     * @param indicate True if edited
     */
    void setVolumeIndication (final int index, final boolean indicate);


    /**
     * Change the panorama.
     *
     * @param index The index of the track
     * @param control The control value
     */
    void changePan (final int index, final int control);


    /**
     * Set the panorama.
     *
     * @param index The index of the track
     * @param value The new value
     */
    void setPan (final int index, final double value);


    /**
     * Reset the panorama to its default value.
     *
     * @param index The index of the track
     */
    void resetPan (final int index);


    /**
     * Signal that the panorama fader/knob is touched for automation recording.
     *
     * @param index The index of the track
     * @param isBeingTouched True if touched
     */
    void touchPan (final int index, final boolean isBeingTouched);


    /**
     * Signal that the panorama is edited.
     *
     * @param index The index of the track
     * @param indicate True if edited
     */
    void setPanIndication (final int index, final boolean indicate);


    /**
     * Sets the activated state of the track.
     *
     * @param index The index of the track
     * @param value True to activate
     */
    void setIsActivated (final int index, final boolean value);


    /**
     * Toggle the activated state of the track.
     *
     * @param index The index of the track
     */
    void toggleIsActivated (final int index);


    /**
     * Turn on/off mute.
     *
     * @param index The index of the track
     * @param value True to turn on mute, otherwise off
     */
    void setMute (final int index, final boolean value);


    /**
     * Toggle mute.
     *
     * @param index The index of the track
     */
    void toggleMute (final int index);


    /**
     * Turn on/off solo.
     *
     * @param index The index of the track
     * @param value True to turn on solo, otherwise off
     */
    void setSolo (final int index, final boolean value);


    /**
     * Toggle solo.
     *
     * @param index The index of the track
     */
    void toggleSolo (final int index);


    /**
     * Turn on/off record arm.
     *
     * @param index The index of the track
     * @param value True to turn arm the track for recording, otherwise off
     */
    void setArm (final int index, final boolean value);


    /**
     * Toggle record arm.
     *
     * @param index The index of the track
     */
    void toggleArm (final int index);


    /**
     * Turn on/off track monitoring.
     *
     * @param index The index of the track
     * @param value True to turn on track monitoring, otherwise off
     */
    void setMonitor (final int index, final boolean value);


    /**
     * Toggle monitor.
     *
     * @param index The index of the track
     */
    void toggleMonitor (final int index);


    /**
     * Turn on/off auto track monitoring.
     *
     * @param index The index of the track
     * @param value True to turn on auto track monitoring, otherwise off
     */
    void setAutoMonitor (final int index, final boolean value);


    /**
     * Toggle auto monitor.
     *
     * @param index The index of the track
     */
    void toggleAutoMonitor (final int index);


    /**
     * Change the crossfade mode.
     *
     * @param index The index of the track
     * @param control The control value
     */
    void changeCrossfadeModeAsNumber (final int index, final int control);


    /**
     * Get the crossfade mode.
     *
     * @param index The index of the track
     * @return The crossfade mode A, AB or B
     */
    String getCrossfadeMode (final int index);


    /**
     * Set the crossfade mode.
     *
     * @param index The index of the track
     * @param mode The crossfade mode A, AB or B
     */
    void setCrossfadeMode (final int index, final String mode);


    /**
     * Get the crossfade mode as a number.
     *
     * @param index The index of the track
     * @return The crossfade mode 0, 1 or 2
     */
    int getCrossfadeModeAsNumber (final int index);


    /**
     * Set the crossfade mode as a number.
     *
     * @param index The index of the track
     * @param modeValue The crossfade mode 0, 1 or 2
     */
    void setCrossfadeModeAsNumber (final int index, final int modeValue);


    /**
     * Set the crossfade mode to the next value.
     *
     * @param index The index of the track
     */
    void toggleCrossfadeMode (final int index);


    /**
     * Set the color of a track as a RGB value.
     *
     * @param index The index of the track
     * @param red The red part of the color
     * @param green The green part of the color
     * @param blue The blue part of the color
     */
    void setTrackColor (final int index, final double red, final double green, final double blue);


    /**
     * Get the color of a track.
     *
     * @param index The index of the track
     * @return The color as RGB, 0 = red, 1 = green, 2 = blue
     */
    double [] getTrackColorEntry (final int index);


    /**
     * Stop playback on the track.
     *
     * @param index The index of the track
     */
    void stop (final int index);


    /**
     * Stop all playing clips.
     */
    void stop ();


    /**
     * Get the scene bank.
     *
     * @return The scene bank.
     */
    ISceneBank getSceneBank ();


    /**
     * Find the color of the first clip of the scene.
     *
     * @param scene The index of the scene
     * @return The color as an identifier string. Returns green if none is found
     */
    String getColorOfFirstClipInScene (final int scene);


    /**
     * Launch a scene.
     *
     * @param scene The index of the scene
     */
    void launchScene (final int scene);


    /**
     * Switch playback back to the arrangement.
     *
     * @param index The index of the track
     */
    void returnToArrangement (final int index);


    /**
     * Scroll up tracks by 1.
     */
    void scrollTracksUp ();


    /**
     * Scroll down tracks by 1.
     */
    void scrollTracksDown ();


    /**
     * Scroll up tracks by 1 page.
     */
    void scrollTracksPageUp ();


    /**
     * Scroll down tracks by 1 page.
     */
    void scrollTracksPageDown ();


    /**
     * Scrolls the channel bank window so that the channel at the given position becomes visible as
     * part of the window.
     *
     * @param channel The index of the channel to scroll to
     */
    void scrollToChannel (final int channel);


    /**
     * Scroll up scenes by 1.
     */
    void scrollScenesUp ();


    /**
     * Scroll down scenes by 1.
     */
    void scrollScenesDown ();


    /**
     * Scroll up scenes by 1 page.
     */
    void scrollScenesPageUp ();


    /**
     * Scroll down scenes by 1 page.
     */
    void scrollScenesPageDown ();


    /**
     * Scroll the scenes to the given position.
     *
     * @param position The new position
     */
    void scrollToScene (final int position);


    /**
     * Set indication for all clips of the size of the number of tracks and scenes.
     *
     * @param enable True to enable
     */
    void setIndication (final boolean enable);


    /**
     * Select a clip.
     *
     * @param trackIndex The index of the track
     * @param slotIndex The index of the slot
     */
    void selectClip (final int trackIndex, final int slotIndex);


    /**
     * Launch a clip.
     *
     * @param trackIndex The index of the track
     * @param slotIndex The index of the slot
     */
    void launchClip (final int trackIndex, final int slotIndex);


    /**
     * Record a clip.
     *
     * @param trackIndex The index of the track
     * @param slotIndex The index of the slot
     */
    void recordClip (final int trackIndex, final int slotIndex);


    /**
     * Create a clip.
     *
     * @param trackIndex The index of the track
     * @param slotIndex The index of the slot
     * @param length The length of the clip
     */
    void createClip (final int trackIndex, final int slotIndex, final int length);


    /**
     * Delete a clip.
     *
     * @param trackIndex The index of the track
     * @param slotIndex The index of the slot
     */
    void deleteClip (final int trackIndex, final int slotIndex);


    /**
     * Duplicate a clip.
     *
     * @param trackIndex The index of the track
     * @param slotIndex The index of the slot
     */
    void duplicateClip (final int trackIndex, final int slotIndex);


    /**
     * Opens the context browser to insert a clip.
     *
     * @param trackIndex The index of the track
     * @param slotIndex The index of the slot
     */
    void browseToInsertClip (final int trackIndex, final int slotIndex);


    /**
     * Scroll to the next clip page.
     *
     * @param trackIndex The index of the track
     */
    void scrollClipPageForwards (final int trackIndex);


    /**
     * Returns an array with the selected slots of a track.
     *
     * @param index The index of the track
     * @return The array is empty if none is selected.
     */
    ISlot [] getSelectedSlots (final int index);


    /**
     * Returns the first selected slot or null if none is selected.
     *
     * @param index The index of the track
     * @return The first selected slot or null if none is selected
     */
    ISlot getSelectedSlot (final int index);


    /**
     * Returns the first empty slot in the current clip window. If none is empty null is returned.
     * If startFrom is set the search starts from the given index (and wraps around after the last
     * one to 0).
     *
     * @param index The index of the track
     * @param startFrom At what index to start the search
     * @return The empty slot or null if none is found
     */
    ISlot getEmptySlot (final int index, final int startFrom);


    /**
     * Get the position of the first scene of the scene banks current page.
     *
     * @return The position
     */
    int getScenePosition ();


    /**
     * Add a note observer.
     *
     * @param observer The note observer
     */
    void addNoteObserver (final NoteObserver observer);


    /**
     * Get the number of tracks of a bank page.
     *
     * @return The number of tracks of a bank page
     */
    int getNumTracks ();


    /**
     * Get the number of scenes of a bank page.
     *
     * @return The number of scenes of a bank page
     */
    int getNumScenes ();


    /**
     * Get the number of sends of a bank page.
     *
     * @return The number of sends of a bank page
     */
    int getNumSends ();
}