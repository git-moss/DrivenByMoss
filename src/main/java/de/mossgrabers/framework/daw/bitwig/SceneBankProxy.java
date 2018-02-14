// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.bitwig;

import de.mossgrabers.framework.daw.ISceneBank;
import de.mossgrabers.framework.daw.bitwig.data.SceneImpl;
import de.mossgrabers.framework.daw.data.IScene;

import com.bitwig.extension.controller.api.SceneBank;


/**
 * Encapsulates the data of a scene bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SceneBankProxy implements ISceneBank
{
    protected IScene [] scenes;

    private int         numScenes;
    private SceneBank   sceneBank;


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


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        for (final IScene scene: this.scenes)
            scene.enableObservers (enable);
        this.sceneBank.scrollPosition ().setIsSubscribed (enable);
        this.sceneBank.canScrollBackwards ().setIsSubscribed (enable);
        this.sceneBank.canScrollForwards ().setIsSubscribed (enable);
        this.sceneBank.itemCount ().setIsSubscribed (enable);
    }


    /** {@inheritDoc} */
    @Override
    public int getSceneCount ()
    {
        return this.sceneBank.itemCount ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public int getScrollPosition ()
    {
        return this.sceneBank.scrollPosition ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public IScene getScene (final int index)
    {
        return this.scenes[index];
    }


    /** {@inheritDoc} */
    @Override
    public void scrollScenesUp ()
    {
        this.sceneBank.scrollBackwards ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollScenesDown ()
    {
        this.sceneBank.scrollForwards ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollScenesPageUp ()
    {
        this.sceneBank.scrollPageBackwards ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollScenesPageDown ()
    {
        this.sceneBank.scrollPageForwards ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollScenesUp ()
    {
        return this.sceneBank.canScrollBackwards ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollScenesDown ()
    {
        return this.sceneBank.canScrollForwards ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollTo (final int position)
    {
        this.sceneBank.scrollPosition ().set (position);
    }


    /** {@inheritDoc} */
    @Override
    public boolean sceneExists (final int index)
    {
        return this.scenes[index].doesExist ();
    }


    /** {@inheritDoc} */
    @Override
    public String getSceneName (final int index)
    {
        return this.scenes[index].getName ();
    }


    /** {@inheritDoc} */
    @Override
    public void launchScene (final int index)
    {
        this.sceneBank.getScene (index).launch ();
    }


    /** {@inheritDoc} */
    @Override
    public void stop ()
    {
        this.sceneBank.stop ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectScene (final int index)
    {
        this.sceneBank.getScene (index).selectInEditor ();
    }


    /** {@inheritDoc} */
    @Override
    public void showScene (final int index)
    {
        this.sceneBank.getScene (index).showInEditor ();
    }


    /** {@inheritDoc} */
    @Override
    public int getNumScenes ()
    {
        return this.numScenes;
    }


    private IScene [] createScenes (final int count)
    {
        final IScene [] sceneData = new IScene [count];
        for (int i = 0; i < count; i++)
            sceneData[i] = new SceneImpl (this.sceneBank.getScene (i), i);
        return sceneData;
    }
}