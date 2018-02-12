// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.bitwig.data;

import de.mossgrabers.framework.daw.data.IScene;

import com.bitwig.extension.controller.api.Scene;


/**
 * Encapsulates the data of a scene.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SceneImpl implements IScene
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
    public SceneImpl (final Scene scene, final int index)
    {
        this.index = index;
        this.scene = scene;

        scene.exists ().markInterested ();
        scene.name ().markInterested ();
        scene.sceneIndex ().markInterested ();
        scene.addIsSelectedInEditorObserver (value -> this.isSelected = value);
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        this.scene.exists ().setIsSubscribed (enable);
        this.scene.name ().setIsSubscribed (enable);
        this.scene.sceneIndex ().setIsSubscribed (enable);
    }


    /** {@inheritDoc} */
    @Override
    public int getIndex ()
    {
        return this.index;
    }


    /** {@inheritDoc} */
    @Override
    public boolean doesExist ()
    {
        return this.scene.exists ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public String getName ()
    {
        return this.scene.name ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public String getName (final int limit)
    {
        return this.scene.name ().getLimited (limit);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isSelected ()
    {
        return this.isSelected;
    }
}
