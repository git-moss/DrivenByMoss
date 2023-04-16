// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.apc.command.trigger;

import de.mossgrabers.controller.akai.apc.APCConfiguration;
import de.mossgrabers.controller.akai.apc.controller.APCControlSurface;
import de.mossgrabers.framework.command.trigger.device.DeviceOnOffCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Adds triggering the browser when used with SHIFT.
 *
 * @author Jürgen Moßgraber
 */
public class APCDeviceOnOffCommand extends DeviceOnOffCommand<APCControlSurface, APCConfiguration>
{
    private final APCBrowserCommand apcBrowserCommand;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public APCDeviceOnOffCommand (final IModel model, final APCControlSurface surface)
    {
        super (model, surface);

        this.apcBrowserCommand = new APCBrowserCommand (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        if (event == ButtonEvent.DOWN)
            this.apcBrowserCommand.startBrowser (false, false);
    }
}
