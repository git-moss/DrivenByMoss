// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.usb.mkii.command.continuous;

import de.mossgrabers.controller.kontrol.usb.mkii.Kontrol2Configuration;
import de.mossgrabers.controller.kontrol.usb.mkii.controller.Kontrol2ControlSurface;
import de.mossgrabers.controller.kontrol.usb.mkii.mode.Modes;
import de.mossgrabers.controller.kontrol.usb.mkii.mode.device.BrowseMode;
import de.mossgrabers.framework.command.Commands;
import de.mossgrabers.framework.command.continuous.MasterVolumeCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.View;


/**
 * Command to change the Main encoder.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MainEncoderCommand extends MasterVolumeCommand<Kontrol2ControlSurface, Kontrol2Configuration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public MainEncoderCommand (final IModel model, final Kontrol2ControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final int value)
    {
        if (this.surface.isShiftPressed ())
        {
            super.execute (value);
            return;
        }

        final ModeManager modeManager = this.surface.getModeManager ();
        if (modeManager.isActiveMode (Modes.MODE_BROWSER))
        {
            final BrowseMode mode = (BrowseMode) modeManager.getMode (Modes.MODE_BROWSER);
            if (value > 64)
                mode.selectPrevious (1);
            else
                mode.selectNext (1);
            return;
        }

        final View activeView = this.surface.getViewManager ().getActiveView ();
        if (activeView == null)
            return;
        if (this.model.getValueChanger ().calcKnobSpeed (value) > 0)
            activeView.getTriggerCommand (Commands.COMMAND_ARROW_RIGHT).execute (ButtonEvent.DOWN);
        else
            activeView.getTriggerCommand (Commands.COMMAND_ARROW_LEFT).execute (ButtonEvent.DOWN);
    }
}
