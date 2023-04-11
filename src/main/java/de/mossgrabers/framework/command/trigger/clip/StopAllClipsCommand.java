// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger.clip;

import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to stop all clips. Also sets a flag to use in combination with pads.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author Jürgen Moßgraber
 */
public class StopAllClipsCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    private boolean stopPressed = false;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public StopAllClipsCommand (final IModel model, final S surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
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
