// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data;

import de.mossgrabers.framework.daw.TrackSelectionObserver;


/**
 * Interface to the master track.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IMasterTrack extends ITrack
{
    /**
     * Register an observer to get notified when the master track gets de-/selected.
     *
     * @param observer The observer to register
     */
    void addTrackSelectionObserver (TrackSelectionObserver observer);


    /**
     * Set the color of the track as a RGB value.
     *
     * @param red The red part of the color
     * @param green The green part of the color
     * @param blue The blue part of the color
     */
    void setColor (double red, double green, double blue);


    /**
     * Change the volume.
     *
     * @param control The control value
     */
    void changeVolume (int control);


    /**
     * Set the volume.
     *
     * @param value The new value
     */
    void setVolume (double value);


    /**
     * Reset the volume to its default value.
     */
    void resetVolume ();


    /**
     * Signal that the volume fader/knob is touched for automation recording.
     *
     * @param isBeingTouched True if touched
     */
    void touchVolume (boolean isBeingTouched);


    /**
     * Signal that the volume is edited.
     *
     * @param indicate True if edited
     */
    void setVolumeIndication (boolean indicate);


    /**
     * Change the panorama.
     *
     * @param control The control value
     */
    void changePan (int control);


    /**
     * Set the panorama.
     *
     * @param value The new value
     */
    void setPan (double value);


    /**
     * Reset the panorama to its default value.
     */
    void resetPan ();


    /**
     * Signal that the panorama fader/knob is touched for automation recording.
     *
     * @param isBeingTouched True if touched
     */
    void touchPan (boolean isBeingTouched);


    /**
     * Signal that the panorama is edited.
     *
     * @param indicate True if edited
     */
    void setPanIndication (boolean indicate);


    /**
     * Sets the activated state of the track.
     *
     * @param value True to activate
     */
    void setIsActivated (boolean value);


    /**
     * Toggle the activated state of the track.
     */
    void toggleIsActivated ();


    /**
     * Turn on/off mute.
     *
     * @param value True to turn on mute, otherwise off
     */
    void setMute (boolean value);


    /**
     * Toggle mute.
     */
    void toggleMute ();


    /**
     * Turn on/off solo.
     *
     * @param value True to turn on solo, otherwise off
     */
    void setSolo (boolean value);


    /**
     * Toggle solo.
     */
    void toggleSolo ();


    /**
     * Turn on/off record arm.
     *
     * @param value True to turn arm the track for recording, otherwise off
     */
    void setArm (boolean value);


    /**
     * Toggle record arm.
     */
    void toggleArm ();


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
     * Select the master track.
     */
    void select ();


    /**
     * Make the master track visible (scrolls to the track in Bitwig).
     */
    void makeVisible ();


    /**
     * Get the left VU value.
     *
     * @return The left VU value
     */
    int getVuLeft ();


    /**
     * Get the right VU value.
     *
     * @return The right VU value
     */
    int getVuRight ();
}