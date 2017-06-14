// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.command.trigger;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.view.ViewManager;
import de.mossgrabers.push.PushConfiguration;
import de.mossgrabers.push.controller.PushControlSurface;
import de.mossgrabers.push.view.Views;


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
    public LayoutCommand (final Model model, final PushControlSurface surface)
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
            viewManager.setActiveView (Views.VIEW_DRUM64);
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
            viewManager.setActiveView (Views.VIEW_SEQUENCER);
    }
}
