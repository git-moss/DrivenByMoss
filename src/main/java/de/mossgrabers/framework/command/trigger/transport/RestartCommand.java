// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger.transport;

import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to handle the restart button.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class RestartCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    protected final ITransport transport;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public RestartCommand (final IModel model, final S surface)
    {
        super (model, surface);

        this.transport = this.model.getTransport ();
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (event != ButtonEvent.DOWN)
            return;

        if (this.transport.isPlaying ())
            this.transport.stopAndRewind ();
        else
            this.transport.play ();
    }
}
