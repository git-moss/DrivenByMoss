// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.fire.command.trigger;

import de.mossgrabers.controller.akai.fire.FireConfiguration;
import de.mossgrabers.controller.akai.fire.controller.FireControlSurface;
import de.mossgrabers.framework.command.trigger.transport.StopCommand;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to handle the stop button.
 *
 * @author Jürgen Moßgraber
 */
public class FireStopCommand extends StopCommand<FireControlSurface, FireConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public FireStopCommand (final IModel model, final FireControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (this.surface.isPressed (ButtonID.ALT))
            this.executeAlt (event);
        else
            super.execute (event, velocity);
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        if (event == ButtonEvent.UP)
            this.model.getTransport ().toggleWriteClipLauncherAutomation ();
    }


    /**
     * Execute button combination with ALT.
     *
     * @param event The button event
     */
    public void executeAlt (final ButtonEvent event)
    {
        if (event == ButtonEvent.UP)
            this.model.getTransport ().toggleWriteArrangerAutomation ();
    }
}
