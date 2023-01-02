// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw;

import de.mossgrabers.framework.observer.IObserverManagement;
import de.mossgrabers.framework.parameter.IParameter;


/**
 * Interface to a DAW project.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IProject extends IObserverManagement
{
    /**
     * Get the name of the active project.
     *
     * @return The name
     */
    String getName ();


    /**
     * Switch to the previous open project.
     */
    void previous ();


    /**
     * Switch to the next open project.
     */
    void next ();


    /**
     * Creates a new empty scene as the last scene in the project.
     *
     * @since API version 13
     */
    void createScene ();


    /**
     * Creates a new scene (using an existing empty scene if possible) from the clips that are
     * currently playing in the clip launcher.
     */
    void createSceneFromPlayingLauncherClips ();


    /**
     * Returns true if the project was modified and needs to be saved.
     *
     * @return True if it needs to be saved
     */
    boolean isDirty ();


    /**
     * Save the current project.
     */
    void save ();


    /**
     * Show the load project dialog.
     */
    void load ();


    /**
     * Get the cue mix parameter.
     *
     * @return The cue mix parameter
     */
    IParameter getCueMixParameter ();


    /**
     * Get the cue volume as a formatted text.
     *
     * @return The cue volume text
     */
    String getCueVolumeStr ();


    /**
     * Get the cue volume as a formatted text.
     *
     * @param limit Limit the text to this length
     * @return The cue volume text
     */
    String getCueVolumeStr (int limit);


    /**
     * Get the cue volume.
     *
     * @return The cue volume
     */
    int getCueVolume ();


    /**
     * Change the cue volume.
     *
     * @param control The control value
     */
    void changeCueVolume (int control);


    /**
     * Set the cue volume.
     *
     * @param value The new value
     */
    void setCueVolume (int value);


    /**
     * Reset the cue volume to its default value.
     */
    void resetCueVolume ();


    /**
     * Signal that the cue volume fader/knob is touched for automation recording.
     *
     * @param isBeingTouched True if touched
     */
    void touchCueVolume (boolean isBeingTouched);


    /**
     * Get the cue volume parameter.
     *
     * @return The cue volume parameter
     */
    IParameter getCueVolumeParameter ();


    /**
     * Get the cue mix as a formatted text
     *
     * @return The cue mix text
     */
    String getCueMixStr ();


    /**
     * Get the cue mix as a formatted text
     *
     * @param limit Limit the text to this l@Override ength
     * @return The cue mix text
     */
    String getCueMixStr (int limit);


    /**
     * Get the cue mix.
     *
     * @return The cue mix
     */
    int getCueMix ();


    /**
     * Change the cue mix.
     *
     * @param control The control value
     */
    void changeCueMix (int control);


    /**
     * Set the cue mix.
     *
     * @param value The new value
     */
    void setCueMix (int value);


    /**
     * Reset the cue mix to its default value.
     */
    void resetCueMix ();


    /**
     * Signal that the cue mix fader/knob is touched for automation recording.
     *
     * @param isBeingTouched True if touched
     */
    void touchCueMix (boolean isBeingTouched);


    /**
     * Check if any of the tracks is soloed.
     *
     * @return True if there is at least one soloed track
     */
    boolean hasSolo ();


    /**
     * Check if any of the tracks is muted.
     *
     * @return True if there is at least one muted track
     */
    boolean hasMute ();


    /**
     * Deactivate all solo states of all tracks.
     */
    void clearSolo ();


    /**
     * Deactivate all mute states of all tracks.
     */
    void clearMute ();
}
