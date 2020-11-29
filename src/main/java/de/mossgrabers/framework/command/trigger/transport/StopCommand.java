// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger.transport;

import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command handle the stop button.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class StopCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public StopCommand (final IModel model, final S surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;
        final ITransport transport = this.model.getTransport ();
        if (transport.isPlaying ())
            this.handleStopOptions ();
        else
            transport.stopAndRewind ();
    }


    /**
     * Handle the different options when the playback is stopped.
     */
    protected void handleStopOptions ()
    {
        final ITransport transport = this.model.getTransport ();
        switch (this.surface.getConfiguration ().getBehaviourOnStop ())
        {
            case RETURN_TO_ZERO:
                transport.stopAndRewind ();
                break;

            case MOVE_PLAY_CURSOR:
                transport.play ();
                break;

            case PAUSE:
                transport.stop ();
                break;
        }
    }
}
