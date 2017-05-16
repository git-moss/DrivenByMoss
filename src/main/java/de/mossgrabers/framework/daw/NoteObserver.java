// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw;

/**
 * Callback interface for observing notes.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
@FunctionalInterface
public interface NoteObserver
{
    /**
     * The callback function.
     *
     * @param note The played note
     * @param velocity The played velocity
     */
    void call (int note, int velocity);
}
