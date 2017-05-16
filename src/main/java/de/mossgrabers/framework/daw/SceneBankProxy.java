// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw;

import de.mossgrabers.framework.daw.data.SceneData;

import com.bitwig.extension.controller.api.SceneBank;


/**
 * Encapsulates the data of a scene bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SceneBankProxy
{
    protected SceneData [] scenes;

    private int            numScenes;
    private SceneBank      sceneBank;


    /**
     * Constructor.
     *
     * @param sceneBank The scene bank
     * @param numScenes The number of scenes in the page of the bank
     */
    public SceneBankProxy (final SceneBank sceneBank, final int numScenes)
    {
        this.numScenes = numScenes;

        this.sceneBank = sceneBank;
        this.scenes = this.createScenes (this.numScenes);

        this.sceneBank.scrollPosition ().markInterested ();
        this.sceneBank.canScrollBackwards ().markInterested ();
        this.sceneBank.canScrollForwards ().markInterested ();
        this.sceneBank.itemCount ().markInterested ();

    }


    /**
     * Dis-/Enable all attributes. They are enabled by default. Use this function if values are
     * currently not needed to improve performance.
     *
     * @param enable True to enable
     */
    public void enableObservers (final boolean enable)
    {
        for (final SceneData scene: this.scenes)
            scene.enableObservers (enable);
        this.sceneBank.scrollPosition ().setIsSubscribed (enable);
        this.sceneBank.canScrollBackwards ().setIsSubscribed (enable);
        this.sceneBank.canScrollForwards ().setIsSubscribed (enable);
        this.sceneBank.itemCount ().setIsSubscribed (enable);
    }


    /**
     * Returns the underlying total scene count (not the number of scenes available in the bank
     * window).
     *
     * @return The total number of scenes.
     */
    public int getSceneCount ()
    {
        return this.sceneBank.itemCount ().get ();
    }


    /**
     * Returns the current scene scroll position.
     *
     * @return The position
     */
    public int getScrollPosition ()
    {
        return this.sceneBank.scrollPosition ().get ();
    }


    /**
     * Returns the scene object at the given index within the bank.
     *
     * @param index the scene index within scene bank.
     * @return The scene
     */
    public SceneData getScene (final int index)
    {
        return this.scenes[index];
    }


    /**
     * Scroll up scenes by 1.
     */
    public void scrollScenesUp ()
    {
        this.sceneBank.scrollBackwards ();
    }


    /**
     * Scroll down scenes by 1.
     */
    public void scrollScenesDown ()
    {
        this.sceneBank.scrollForwards ();
    }


    /**
     * Scrolls the scenes one page up.
     */
    public void scrollScenesPageUp ()
    {
        this.sceneBank.scrollPageBackwards ();
    }


    /**
     * Scrolls the scenes one page down.
     */
    public void scrollScenesPageDown ()
    {
        this.sceneBank.scrollPageForwards ();
    }


    /**
     * Is there a scene page left of the current?
     *
     * @return True if there is a scene page left of the current
     */
    public boolean canScrollScenesUp ()
    {
        return this.sceneBank.canScrollBackwards ().get ();
    }


    /**
     * Is there a scene page right of the current?
     *
     * @return True if there is a scene page right of the current
     */
    public boolean canScrollScenesDown ()
    {
        return this.sceneBank.canScrollForwards ().get ();
    }


    /**
     * Makes the scene with the given position visible in the track bank.
     *
     * @param position the position of the scene within the underlying full list of scenes
     */
    public void scrollTo (final int position)
    {
        this.sceneBank.scrollPosition ().set (position);
    }


    /**
     * Returns whether the scene exists within the bank.
     *
     * @param index scene bank index.
     * @return True if the scene exists
     */
    public boolean sceneExists (final int index)
    {
        return this.scenes[index].doesExist ();
    }


    /**
     * Returns the scene name, null if the scene doesn't exist.
     *
     * @param index scene bank index.
     * @return The scene name
     */
    public String getSceneName (final int index)
    {
        return this.scenes[index].getName ();
    }


    /**
     * Launches the scene.
     *
     * @param index scene bank index.
     */
    public void launchScene (final int index)
    {
        this.sceneBank.getScene (index).launch ();
    }


    /**
     * Stop all playing clips.
     */
    public void stop ()
    {
        this.sceneBank.stop ();
    }


    /**
     * Selects the scene in Bitwig Studio.
     *
     * @param index scene bank index.
     */
    public void selectScene (final int index)
    {
        this.sceneBank.getScene (index).selectInEditor ();
    }


    /**
     * Makes the scene visible in the Bitwig Studio user interface.
     *
     * @param index scene bank index.
     */
    public void showScene (final int index)
    {
        this.sceneBank.getScene (index).showInEditor ();
    }


    private SceneData [] createScenes (final int count)
    {
        final SceneData [] sceneData = new SceneData [count];
        for (int i = 0; i < count; i++)
            sceneData[i] = new SceneData (this.sceneBank.getScene (i), i);
        return sceneData;
    }


    /**
     * Get the number of scenes in a page of the scene bank.
     *
     * @return The number of scenes in a page
     */
    public int getNumScenes ()
    {
        return this.numScenes;
    }
}