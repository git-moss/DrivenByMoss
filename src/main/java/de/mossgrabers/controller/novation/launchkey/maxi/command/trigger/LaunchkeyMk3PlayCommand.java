// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchkey.maxi.command.trigger;

import de.mossgrabers.controller.novation.launchkey.maxi.LaunchkeyMk3Configuration;
import de.mossgrabers.controller.novation.launchkey.maxi.controller.LaunchkeyMk3ControlSurface;
import de.mossgrabers.framework.command.trigger.transport.PlayCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to handle the play button.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class LaunchkeyMk3PlayCommand extends PlayCommand<LaunchkeyMk3ControlSurface, LaunchkeyMk3Configuration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public LaunchkeyMk3PlayCommand (final IModel model, final LaunchkeyMk3ControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        if (event == ButtonEvent.DOWN)
            this.transport.returnToArrangement ();
    }
}
