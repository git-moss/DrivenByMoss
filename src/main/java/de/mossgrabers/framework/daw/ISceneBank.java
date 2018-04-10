// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw;

import de.mossgrabers.framework.daw.data.IScene;


/**
 * Interface to a scene bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface ISceneBank extends ObserverManagement
{
    /**
     * Returns the underlying total scene count (not the number of scenes available in the bank
     * window).
     *
     * @return The total number of scenes.
     */
    int getSceneCount ();


    /**
     * Returns the current scene scroll position.
     *
     * @return The position
     */
    int getScrollPosition ();


    /**
     * Returns the scene object at the given index within the bank.
     *
     * @param index the scene index within scene bank.
     * @return The scene
     */
    IScene getScene (int index);


    /**
     * Scroll up scenes by 1.
     */
    void scrollScenesUp ();


    /**
     * Scroll down scenes by 1.
     */
    void scrollScenesDown ();


    /**
     * Scrolls the scenes one page up.
     */
    void scrollScenesPageUp ();


    /**
     * Scrolls the scenes one page down.
     */
    void scrollScenesPageDown ();


    /**
     * Is there a scene page left of the current?
     *
     * @return True if there is a scene page left of the current
     */
    boolean canScrollScenesUp ();


    /**
     * Is there a scene page right of the current?
     *
     * @return True if there is a scene page right of the current
     */
    boolean canScrollScenesDown ();


    /**
     * Makes the scene with the given position visible in the track bank.
     *
     * @param position the position of the scene within the underlying full list of scenes
     */
    void scrollTo (int position);


    /**
     * Returns whether the scene exists within the bank.
     *
     * @param index scene bank index.
     * @return True if the scene exists
     */
    boolean sceneExists (int index);


    /**
     * Returns the scene name, null if the scene doesn't exist.
     *
     * @param index scene bank index.
     * @return The scene name
     */
    String getSceneName (int index);


    /**
     * Launches the scene.
     *
     * @param index scene bank index.
     */
    void launchScene (int index);


    /**
     * Stop all playing clips.
     */
    void stop ();


    /**
     * Selects the scene in the DAW.
     *
     * @param index scene bank index.
     */
    void selectScene (int index);


    /**
     * Makes the scene visible in the the DAW user interface.
     *
     * @param index scene bank index.
     */
    void showScene (int index);


    /**
     * Get the number of scenes in a page of the scene bank.
     *
     * @return The number of scenes in a page
     */
    int getNumScenes ();
}