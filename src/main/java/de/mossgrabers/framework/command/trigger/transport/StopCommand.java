// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger.transport;

import de.mossgrabers.framework.command.trigger.AbstractDoubleTriggerCommand;
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
 * @author Jürgen Moßgraber
 */
public class StopCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractDoubleTriggerCommand<S, C>
{
    private final ITransport transport;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public StopCommand (final IModel model, final S surface)
    {
        super (model, surface);

        this.transport = this.model.getTransport ();
    }


    /** {@inheritDoc} */
    @Override
    protected void executeSingleClick ()
    {
        if (this.transport.isPlaying ())
            this.handleStopOptions ();
        else
        {
            this.transport.stopAndRewind ();
            this.doubleClickTest ();
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void executeDoubleClick ()
    {
        this.transport.setPositionToEnd ();
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        if (event == ButtonEvent.UP)
            this.model.getTrackBank ().stop (false);
    }


    /**
     * Handle the different options when the playback is stopped.
     */
    protected void handleStopOptions ()
    {
        switch (this.surface.getConfiguration ().getBehaviourOnStop ())
        {
            case RETURN_TO_ZERO:
                this.transport.stopAndRewind ();
                break;

            case STOP:
                this.transport.stop ();
                break;

            case PAUSE:
                if (this.transport.isPlaying ())
                    this.transport.play ();
                break;
        }
    }
}
