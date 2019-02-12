// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.command.trigger;

import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.ViewManager;
import de.mossgrabers.framework.view.Views;


/**
 * Command for the layout button (only Push 2).
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class LayoutCommand extends AbstractTriggerCommand<PushControlSurface, PushConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public LayoutCommand (final IModel model, final PushControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final ViewManager viewManager = this.surface.getViewManager ();
        if (viewManager.isActiveView (Views.VIEW_PLAY))
            viewManager.setActiveView (Views.VIEW_PIANO);
        else if (viewManager.isActiveView (Views.VIEW_PIANO))
            viewManager.setActiveView (this.model.getHost ().hasClips () ? Views.VIEW_DRUM64 : Views.VIEW_PLAY);
        else if (viewManager.isActiveView (Views.VIEW_DRUM64))
            viewManager.setActiveView (Views.VIEW_PLAY);
        else if (viewManager.isActiveView (Views.VIEW_SEQUENCER))
            viewManager.setActiveView (Views.VIEW_RAINDROPS);
        else if (viewManager.isActiveView (Views.VIEW_RAINDROPS))
            viewManager.setActiveView (Views.VIEW_DRUM);
        else if (viewManager.isActiveView (Views.VIEW_DRUM))
            viewManager.setActiveView (Views.VIEW_DRUM4);
        else if (viewManager.isActiveView (Views.VIEW_DRUM4))
            viewManager.setActiveView (Views.VIEW_DRUM8);
        else if (viewManager.isActiveView (Views.VIEW_DRUM8))
            viewManager.setActiveView (Views.VIEW_SEQUENCER);
        else
        {
            final PushConfiguration configuration = this.surface.getConfiguration ();
            if (viewManager.isActiveView (Views.VIEW_SESSION))
            {
                if (configuration.isFlipSession ())
                    viewManager.setActiveView (Views.VIEW_SCENE_PLAY);
                else
                    configuration.setFlipSession (true);
            }
            else if (viewManager.isActiveView (Views.VIEW_SCENE_PLAY))
            {
                configuration.setFlipSession (false);
                viewManager.setActiveView (Views.VIEW_SESSION);
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final ViewManager viewManager = this.surface.getViewManager ();
        if (Views.isSequencerView (viewManager.getActiveViewId ()))
            viewManager.setActiveView (Views.VIEW_PLAY);
        else
        {
            if (viewManager.getView (Views.VIEW_SEQUENCER) != null)
                viewManager.setActiveView (Views.VIEW_SEQUENCER);
        }
    }
}
