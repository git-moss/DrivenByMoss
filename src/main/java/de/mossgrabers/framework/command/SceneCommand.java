// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command;

import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.command.core.ContinuousCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.SceneView;


/**
 * Command to use a scene button.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SceneCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C> implements ContinuousCommand
{
    protected int scene;


    /**
     * Constructor.
     *
     * @param index The index of the scene button
     * @param model The model
     * @param surface The surface
     */
    public SceneCommand (final int index, final IModel model, final S surface)
    {
        super (model, surface);
        this.scene = index;
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event)
    {
        final SceneView view = (SceneView) this.surface.getViewManager ().getActiveView ();
        if (view != null)
            view.onScene (this.scene, event);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final int value)
    {
        this.execute (value == 0 ? ButtonEvent.UP : ButtonEvent.DOWN);
    }
}
