// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data;

/**
 * Interface to a pinnable object.
 *
 * @author Jürgen Moßgraber
 */
public interface IPinnable
{
    /**
     * Get if the object is pinned.
     *
     * @return True if pinned
     */
    boolean isPinned ();


    /**
     * Toggles the pinned state of the object.
     */
    void togglePinned ();


    /**
     * Set the pinned state of the object.
     *
     * @param isPinned True to pin, false to unpin
     */
    void setPinned (boolean isPinned);
}