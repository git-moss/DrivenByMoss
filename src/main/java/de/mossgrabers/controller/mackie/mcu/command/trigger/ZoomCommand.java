// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.mcu.command.trigger;

import de.mossgrabers.controller.mackie.mcu.MCUConfiguration;
import de.mossgrabers.controller.mackie.mcu.controller.MCUControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.IBrowser;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command for the zoom button.
 *
 * @author Jürgen Moßgraber
 */
public class ZoomCommand extends AbstractTriggerCommand<MCUControlSurface, MCUConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public ZoomCommand (final IModel model, final MCUControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final IBrowser browser = this.model.getBrowser ();
        if (browser.isActive ())
        {
            final ICursorDevice cursorDevice = this.model.getCursorDevice ();
            if (cursorDevice.doesExist ())
                browser.replace (cursorDevice);
            return;
        }

        this.surface.getConfiguration ().toggleZoomState ();
    }
}
