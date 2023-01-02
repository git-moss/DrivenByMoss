// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw;

import de.mossgrabers.framework.observer.IObserverManagement;


/**
 * Interface to the Arranger.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IArranger extends IObserverManagement
{
    /**
     * Are the cue markers visible?
     *
     * @return True if cue markers are visible
     */
    boolean areCueMarkersVisible ();


    /**
     * Show/hide the cue markers in the arranger panel.
     */
    void toggleCueMarkerVisibility ();


    /**
     * Is playback follow enabled?.
     *
     * @return True if playback follows
     */
    boolean isPlaybackFollowEnabled ();


    /**
     * Enable/disable arranger playback follow.
     */
    void togglePlaybackFollow ();


    /**
     * Are double track heights enabled?.
     *
     * @return True if double row track height is enabled
     */
    boolean hasDoubleRowTrackHeight ();


    /**
     * Toggles the double/single row height of the Arranger tracks.
     */
    void toggleTrackRowHeight ();


    /**
     * Is the clip launcher visible?
     *
     * @return True if the clip launcher is visible
     */
    boolean isClipLauncherVisible ();


    /**
     * Toggle the clip launcher visibility.
     */
    void toggleClipLauncher ();


    /**
     * Is the timeline visible?
     *
     * @return True if the timeline is visible?
     */
    boolean isTimelineVisible ();


    /**
     * Toggles the timeline visibility.
     */
    void toggleTimeLine ();


    /**
     * Is the IO section visible?
     *
     * @return True if the IO section is visible
     */
    boolean isIoSectionVisible ();


    /**
     * Toggles the visibility of the IO section
     */
    void toggleIoSection ();


    /**
     * Are effect tracks visible?
     *
     * @return True if effect tracks are visible
     */
    boolean areEffectTracksVisible ();


    /**
     * Toggles the effect tracks visibility.
     */
    void toggleEffectTracks ();
}