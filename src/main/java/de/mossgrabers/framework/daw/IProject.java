// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw;

import de.mossgrabers.framework.observer.ObserverManagement;


/**
 * Interface to a DAW project.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IProject extends ObserverManagement
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
     * Creates a new scene (using an existing empty scene if possible) from the clips that are
     * currently playing in the clip launcher.
     */
    void createSceneFromPlayingLauncherClips ();


    /**
     * Save the current project.
     */
    void save ();
}
