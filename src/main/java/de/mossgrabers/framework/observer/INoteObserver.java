// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.observer;

/**
 * Callback interface for observing notes.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
@FunctionalInterface
public interface INoteObserver
{
    /**
     * The callback function.
     *
     * @param trackIndex The index of the track on which the note is playing
     * @param note The played note
     * @param velocity The played velocity
     */
    void call (int trackIndex, int note, int velocity);
}
