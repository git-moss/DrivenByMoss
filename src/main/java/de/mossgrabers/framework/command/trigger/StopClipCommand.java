// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ControlSurface;
import de.mossgrabers.framework.daw.IModel;


/**
 * Command to stop all clips. Also sets a flag to use in combination with pads.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class StopClipCommand<S extends ControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    private boolean stopPressed = false;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public StopClipCommand (final IModel model, final S surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event)
    {
        if (this.surface.isShiftPressed ())
        {
            // Stop all clips
            this.model.getCurrentTrackBank ().stop ();
            return;
        }
        this.stopPressed = event != ButtonEvent.UP;
    }


    /**
     * Is the stop clip button pressed?
     *
     * @return True if the stop clip button is pressed
     */
    public boolean isStopPressed ()
    {
        return this.stopPressed;
    }
}
