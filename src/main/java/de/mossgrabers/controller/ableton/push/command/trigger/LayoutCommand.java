// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.command.trigger;

import de.mossgrabers.controller.ableton.push.PushConfiguration;
import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.Views;


/**
 * Command for the layout button (only Push 2).
 *
 * @author Jürgen Moßgraber
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
        if (viewManager.isActive (Views.PLAY))
            this.activatePreferredView (Views.CHORDS);
        else if (viewManager.isActive (Views.CHORDS))
            this.activatePreferredView (Views.PIANO);
        else if (viewManager.isActive (Views.PIANO))
            this.activatePreferredView (Views.DRUM64);
        else if (viewManager.isActive (Views.DRUM64))
            this.activatePreferredView (Views.PLAY);
        else if (viewManager.isActive (Views.SEQUENCER))
            this.activatePreferredView (Views.RAINDROPS);
        else if (viewManager.isActive (Views.RAINDROPS))
            this.activatePreferredView (Views.DRUM);
        else if (viewManager.isActive (Views.DRUM))
            this.activatePreferredView (Views.DRUM4);
        else if (viewManager.isActive (Views.DRUM4))
            this.activatePreferredView (Views.DRUM8);
        else if (viewManager.isActive (Views.DRUM8))
            this.activatePreferredView (Views.SEQUENCER);
        else
        {
            final PushConfiguration configuration = this.surface.getConfiguration ();
            if (viewManager.isActive (Views.SESSION))
            {
                if (configuration.isFlipSession ())
                    viewManager.setActive (Views.SCENE_PLAY);
                else
                    configuration.setFlipSession (true);
            }
            else if (viewManager.isActive (Views.SCENE_PLAY))
            {
                configuration.setFlipSession (false);
                viewManager.setActive (Views.SESSION);
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
        if (Views.isSequencerView (viewManager.getActiveID ()))
            this.activatePreferredView (Views.PLAY);
        else
        {
            if (viewManager.get (Views.SEQUENCER) != null)
                this.activatePreferredView (Views.SEQUENCER);
        }
    }
}
