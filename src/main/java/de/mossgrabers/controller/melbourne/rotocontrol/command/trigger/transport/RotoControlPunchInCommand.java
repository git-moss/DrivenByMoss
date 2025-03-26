// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.melbourne.rotocontrol.command.trigger.transport;

import de.mossgrabers.controller.melbourne.rotocontrol.RotoControlConfiguration;
import de.mossgrabers.controller.melbourne.rotocontrol.controller.RotoControlControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * The Punch In command. Toggles punch in.
 *
 * @author Jürgen Moßgraber
 */
public class RotoControlPunchInCommand extends AbstractTriggerCommand<RotoControlControlSurface, RotoControlConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public RotoControlPunchInCommand (final IModel model, final RotoControlControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (event != ButtonEvent.LONG)
            this.model.getTransport ().togglePunchIn ();
    }
}
