package de.mossgrabers.framework.view;

import de.mossgrabers.framework.ButtonEvent;


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
