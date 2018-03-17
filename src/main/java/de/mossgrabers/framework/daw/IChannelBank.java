// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw;

import de.mossgrabers.framework.daw.data.ITrack;


/**
 * Interface to a channel bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IChannelBank extends ObserverManagement
{
    /**
     * Registers a track selection observer.
     *
     * @param observer The observer to register
     */
    void addTrackSelectionObserver (final TrackSelectionObserver observer);


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


    /**
     * Get the position of the first track of the current bank page.
     *
     * @return The position
     */
    int getTrackPositionFirst ();


    /**
     * Get the position of the last track of the current bank page. E.g. if the current bank page
     * contains only 5 tracks, the position of the 5th track is returned. If there are no tracks -1
     * is returned.
     *
     * @return The position or -1
     */
    int getTrackPositionLast ();


    /**
     * Check if there is a send at the given index, which can be edited.
     *
     * @param sendIndex The index of the send
     * @return True if there is a send to edit
     */
    boolean canEditSend (int sendIndex);


    /**
     * DAWs which can put different sends in a slot can return here a name to be displayed for a
     * slot.
     *
     * @param sendIndex The index of the send
     * @return The name to display
     */
    String getEditSendName (int sendIndex);
}