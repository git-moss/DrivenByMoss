// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.mcu.command.trigger;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.mcu.MCUConfiguration;
import de.mossgrabers.mcu.controller.MCUControlSurface;
import de.mossgrabers.mcu.mode.Modes;


/**
 * Command to toggles between Track and Device editing mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ScrubCommand extends AbstractTriggerCommand<MCUControlSurface, MCUConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public ScrubCommand (final Model model, final MCUControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
        final ModeManager modeManager = this.surface.getModeManager ();
        modeManager.setActiveMode (modeManager.isActiveMode (Modes.MODE_TRACK) ? Modes.MODE_DEVICE_PARAMS : Modes.MODE_TRACK);
        this.surface.getDisplay ().notify (modeManager.isActiveMode (Modes.MODE_TRACK) ? "Track" : "Device");
    }
}
