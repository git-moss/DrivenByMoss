// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.view;

import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Interface to start sessions and update session buttons.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface SceneView
{
    /**
     * A scene button was pressed.
     *
     * @param scene The scene
     * @param event The button event
     */
    void onScene (final int scene, final ButtonEvent event);


    /**
     * Update the scene buttons.
     */
    void updateSceneButtons ();
}
