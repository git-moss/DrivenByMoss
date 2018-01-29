// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data;

import com.bitwig.extension.controller.api.Scene;


/**
 * Encapsulates the data of a scene.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SceneData
{
    private final int   index;
    private final Scene scene;
    private boolean     isSelected;


    /**
     * Constructor.
     *
     * @param scene The scene
     * @param index The index of the scene
     */
    public SceneData (final Scene scene, final int index)
    {
        this.index = index;
        this.scene = scene;

        scene.exists ().markInterested ();
        scene.name ().markInterested ();
        scene.sceneIndex ().markInterested ();
        scene.addIsSelectedInEditorObserver (value -> this.isSelected = value);
    }


    /**
     * Dis-/Enable all attributes. They are enabled by default. Use this function if values are
     * currently not needed to improve performance.
     *
     * @param enable True to enable
     */
    public void enableObservers (final boolean enable)
    {
        this.scene.exists ().setIsSubscribed (enable);
        this.scene.name ().setIsSubscribed (enable);
        this.scene.sceneIndex ().setIsSubscribed (enable);
    }


    /**
     * Get the index.
     *
     * @return The index
     */
    public int getIndex ()
    {
        return this.index;
    }


    /**
     * Does the slot exist?
     *
     * @return True if it exists
     */
    public boolean doesExist ()
    {
        return this.scene.exists ().get ();
    }


    /**
     * Get the name of the scene.
     *
     * @return The name of the scene
     */
    public String getName ()
    {
        return this.scene.name ().get ();
    }


    /**
     * Get the name of the scene.
     *
     * @param limit Limit the text to this length
     * @return The name of the scene
     */
    public String getName (final int limit)
    {
        return this.scene.name ().getLimited (limit);
    }


    /**
     * Is the slot selected?
     *
     * @return True if selected
     */
    public boolean isSelected ()
    {
        return this.isSelected;
    }
}
