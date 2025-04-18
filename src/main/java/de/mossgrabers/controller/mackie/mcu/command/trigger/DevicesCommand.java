// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.mcu.command.trigger;

import de.mossgrabers.controller.mackie.mcu.controller.MCUControlSurface;
import de.mossgrabers.controller.mackie.mcu.mode.MCUMultiModeSwitcherCommand;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to show/hide the devices pane.
 *
 * @author Jürgen Moßgraber
 */
public class DevicesCommand extends MCUMultiModeSwitcherCommand
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public DevicesCommand (final IModel model, final MCUControlSurface surface)
    {
        super (model, surface, Modes.DEVICE_PARAMS, Modes.USER);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (this.surface.isPressed (ButtonID.SELECT))
        {
            if (event == ButtonEvent.DOWN)
                this.model.getCursorDevice ().togglePinned ();
            return;
        }

        super.execute (event, velocity);
    }
}
