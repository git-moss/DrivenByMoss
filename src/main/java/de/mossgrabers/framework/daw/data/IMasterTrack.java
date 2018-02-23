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