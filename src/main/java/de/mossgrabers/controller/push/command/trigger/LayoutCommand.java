// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.command.trigger;

import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.utils.ButtonEvent;
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
        if (viewManager.isActive (Views.PLAY))
            this.activateView (Views.CHORDS);
        else if (viewManager.isActive (Views.CHORDS))
            this.activateView (Views.PIANO);
        else if (viewManager.isActive (Views.PIANO))
            this.activateView (Views.DRUM64);
        else if (viewManager.isActive (Views.DRUM64))
            this.activateView (Views.PLAY);
        else if (viewManager.isActive (Views.SEQUENCER))
            this.activateView (Views.RAINDROPS);
        else if (viewManager.isActive (Views.RAINDROPS))
            this.activateView (Views.DRUM);
        else if (viewManager.isActive (Views.DRUM))
            this.activateView (Views.DRUM4);
        else if (viewManager.isActive (Views.DRUM4))
            this.activateView (Views.DRUM8);
        else if (viewManager.isActive (Views.DRUM8))
            this.activateView (Views.SEQUENCER);
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
            this.activateView (Views.PLAY);
        else
        {
            if (viewManager.get (Views.SEQUENCER) != null)
                this.activateView (Views.SEQUENCER);
        }
    }


    private void activateView (final Views view)
    {
        final ViewManager viewManager = this.surface.getViewManager ();
        viewManager.setActive (view);
        final ITrack cursorTrack = this.model.getCursorTrack ();
        if (cursorTrack.doesExist ())
            viewManager.setPreferredView (cursorTrack.getPosition (), view);
    }
}
