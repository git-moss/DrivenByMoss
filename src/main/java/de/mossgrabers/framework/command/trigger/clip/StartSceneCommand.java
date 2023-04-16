// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger.clip;

import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.daw.data.bank.ISceneBank;
import de.mossgrabers.framework.daw.data.empty.EmptyScene;
import de.mossgrabers.framework.utils.ButtonEvent;

import java.util.Optional;


/**
 * Command to start the currently selected scene.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author Jürgen Moßgraber
 */
public class StartSceneCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    protected final int index;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public StartSceneCommand (final IModel model, final S surface)
    {
        this (model, surface, -1);
    }


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     * @param index The index of the scene in the page
     */
    public StartSceneCommand (final IModel model, final S surface, final int index)
    {
        super (model, surface);

        this.index = index;
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event == ButtonEvent.LONG)
            return;

        final IScene scene = this.getScene ();
        if (!scene.doesExist ())
            return;

        final boolean isDown = event == ButtonEvent.DOWN;
        if (isDown)
        {
            // Delete the scene
            if (this.surface.isDeletePressed ())
            {
                scene.remove ();
                return;
            }

            if (this.surface.getConfiguration ().isSelectClipOnLaunch ())
            {
                scene.select ();
                this.mvHelper.delayDisplay (scene::getName);
            }
        }

        scene.launch (isDown, false);
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        if (event == ButtonEvent.DOWN)
            this.model.getProject ().createSceneFromPlayingLauncherClips ();
    }


    /**
     * Get the related scene.
     *
     * @return The scene
     */
    public IScene getScene ()
    {
        final ISceneBank sceneBank = this.model.getSceneBank ();
        if (this.index >= 0)
            return sceneBank.getItem (this.index);
        final Optional<IScene> sceneOptional = sceneBank.getSelectedItem ();
        return sceneOptional.isEmpty () ? EmptyScene.INSTANCE : sceneOptional.get ();
    }
}
