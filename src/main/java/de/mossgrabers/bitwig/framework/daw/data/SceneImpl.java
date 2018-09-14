// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw.data;

import de.mossgrabers.framework.daw.data.AbstractItemImpl;
import de.mossgrabers.framework.daw.data.IScene;

import com.bitwig.extension.controller.api.Scene;
import com.bitwig.extension.controller.api.SettableColorValue;


/**
 * Encapsulates the data of a scene.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SceneImpl extends AbstractItemImpl implements IScene
{
    private final Scene scene;


    /**
     * Constructor.
     *
     * @param scene The scene
     * @param index The index of the scene
     */
    public SceneImpl (final Scene scene, final int index)
    {
        super (index);

        this.scene = scene;

        scene.exists ().markInterested ();
        scene.name ().markInterested ();
        scene.sceneIndex ().markInterested ();
        scene.color ().markInterested ();
        scene.addIsSelectedInEditorObserver (this::setSelected);
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        this.scene.exists ().setIsSubscribed (enable);
        this.scene.name ().setIsSubscribed (enable);
        this.scene.sceneIndex ().setIsSubscribed (enable);
        this.scene.color ().setIsSubscribed (enable);
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
    public void select ()
    {
        this.scene.selectInEditor ();
    }


    /** {@inheritDoc} */
    @Override
    public double [] getColor ()
    {
        final SettableColorValue color = this.scene.color ();
        return new double []
        {
            color.red (),
            color.green (),
            color.blue ()
        };
    }


    /** {@inheritDoc} */
    @Override
    public void setColor (final double red, final double green, final double blue)
    {
        this.scene.color ().set ((float) red, (float) green, (float) blue);
    }


    /** {@inheritDoc} */
    @Override
    public int getPosition ()
    {
        return this.scene.sceneIndex ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void duplicate ()
    {
        this.scene.nextSceneInsertionPoint ().copySlotsOrScenes (this.scene);
    }


    /** {@inheritDoc} */
    @Override
    public void remove ()
    {
        // TODO API extension required - https://github.com/teotigraphix/Framework4Bitwig/issues/180
    }


    /** {@inheritDoc} */
    @Override
    public void launch ()
    {
        this.scene.launch ();
    }
}
