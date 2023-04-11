// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw.data;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.data.AbstractItemImpl;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.observer.IValueObserver;

import com.bitwig.extension.controller.api.Scene;
import com.bitwig.extension.controller.api.SettableColorValue;


/**
 * Encapsulates the data of a scene.
 *
 * @author Jürgen Moßgraber
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
        Util.setIsSubscribed (this.scene.exists (), enable);
        Util.setIsSubscribed (this.scene.name (), enable);
        Util.setIsSubscribed (this.scene.sceneIndex (), enable);
        Util.setIsSubscribed (this.scene.color (), enable);
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
    public void addNameObserver (final IValueObserver<String> observer)
    {
        this.scene.name ().addValueObserver (observer::update);
    }


    /** {@inheritDoc} */
    @Override
    public void select ()
    {
        this.scene.selectInEditor ();
    }


    /** {@inheritDoc} */
    @Override
    public ColorEx getColor ()
    {
        final SettableColorValue color = this.scene.color ();
        return new ColorEx (color.red (), color.green (), color.blue ());
    }


    /** {@inheritDoc} */
    @Override
    public void setColor (final ColorEx color)
    {
        this.scene.color ().set ((float) color.getRed (), (float) color.getGreen (), (float) color.getBlue ());
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
        this.scene.deleteObject ();
    }


    /** {@inheritDoc} */
    @Override
    public void launch (final boolean isPressed, final boolean isAlternative)
    {
        if (isPressed)
        {
            if (isAlternative)
                this.scene.launchAlt ();
            else
                this.scene.launch ();
            return;
        }

        if (isAlternative)
            this.scene.launchReleaseAlt ();
        else
            this.scene.launchRelease ();
    }
}
