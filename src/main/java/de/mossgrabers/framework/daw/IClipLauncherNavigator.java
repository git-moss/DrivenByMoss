// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw;

/**
 * Interface for navigating the clip launcher.
 *
 * @author Jürgen Moßgraber
 */
public interface IClipLauncherNavigator
{
    /**
     * Navigate to the previous or next scene (if any).
     *
     * @param isLeft Select the previous scene if true
     */
    void navigateScenes (boolean isLeft);


    /**
     * Navigate to the previous or next clip of the selected track (if any).
     *
     * @param isLeft Select the previous clip if true
     */
    void navigateClips (boolean isLeft);


    /**
     * Navigate to the previous or next track (if any). Contains complex workaround to make sure
     * that the same slot is selected a newly selected track as well.
     *
     * @param isLeft Select the previous track if true
     */
    void navigateTracks (boolean isLeft);


    /**
     * Select a track in the current track bank page. Contains complex workaround to make sure that
     * the same slot is selected a newly selected track as well.
     *
     * @param index The index of the track
     */
    void selectTrack (int index);
}
