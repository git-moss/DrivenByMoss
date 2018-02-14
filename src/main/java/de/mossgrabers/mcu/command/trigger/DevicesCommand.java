// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.mcu.command.trigger;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.command.trigger.ModeSelectCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.mcu.MCUConfiguration;
import de.mossgrabers.mcu.controller.MCUControlSurface;
import de.mossgrabers.mcu.mode.Modes;


/**
 * Command to show/hide the devices pane.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DevicesCommand extends ModeSelectCommand<MCUControlSurface, MCUConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public DevicesCommand (final IModel model, final MCUControlSurface surface)
    {
        super (Modes.MODE_DEVICE_PARAMS, model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event)
    {
        if (this.surface.isPressed (MCUControlSurface.MCU_OPTION))
        {
            if (event == ButtonEvent.DOWN)
                this.model.getCursorDevice ().togglePinned ();
            return;
        }

        super.execute (event);
    }
}
