// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.clip;

/**
 * Interface to additional methods for keeping track of alternative clip/scene interactions.
 *
 * @author Jürgen Moßgraber
 */
public interface ISessionAlternative
{
    /**
     * Check for the alternate interaction flag.
     *
     * @return True if shift button was used in combination with a clip or scene
     */
    boolean wasAlternateInteractionUsed ();


    /**
     * Set or clear the alternate interaction flag.
     *
     * @param wasUsed Set to true if it was used
     */
    void setAlternateInteractionUsed (boolean wasUsed);
}
