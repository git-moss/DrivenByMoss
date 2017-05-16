// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw;

/**
 * Callback interface for observing track selection changes.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
@FunctionalInterface
public interface TrackSelectionObserver
{
    /**
     * The callback function.
     *
     * @param index The index of the track
     * @param isSelected Has the track been selected?
     */
    void call (int index, boolean isSelected);
}
