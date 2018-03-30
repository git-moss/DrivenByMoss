// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger.transport;

import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to handle the play button.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PlayCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    private boolean restartFlag = false;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public PlayCommand (final IModel model, final S surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        if (this.surface.isSelectPressed ())
        {
            this.model.getTransport ().togglePunchIn ();
            return;
        }

        if (this.restartFlag)
        {
            this.model.getTransport ().stopAndRewind ();
            this.restartFlag = false;
            return;
        }

        this.handleStopOptions ();
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
        if (this.surface.isSelectPressed ())
            this.model.getTransport ().togglePunchOut ();
        else
            this.model.getTransport ().toggleLoop ();
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
                if (transport.isPlaying ())
                    transport.stopAndRewind ();
                else
                    transport.play ();
                break;

            case MOVE_PLAY_CURSOR:
                transport.play ();
                this.doubleClickTest ();
                break;

            case PAUSE:
                if (transport.isPlaying ())
                    transport.stop ();
                else
                    transport.play ();
                this.doubleClickTest ();
                break;
        }
    }


    /**
     * Detecting a double click.
     */
    private void doubleClickTest ()
    {
        this.restartFlag = true;
        this.surface.scheduleTask ( () -> this.restartFlag = false, 250);
    }
}
