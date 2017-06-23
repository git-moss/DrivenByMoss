// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.mcu.command.trigger;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.BrowserProxy;
import de.mossgrabers.mcu.MCUConfiguration;
import de.mossgrabers.mcu.controller.MCUControlSurface;
import de.mossgrabers.mcu.mode.Modes;


/**
 * Command to Browse presets or insert new devices.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class BrowserCommand extends AbstractTriggerCommand<MCUControlSurface, MCUConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public BrowserCommand (final Model model, final MCUControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;

        final BrowserProxy browser = this.model.getBrowser ();

        if (this.surface.isShiftPressed ())
            browser.browseToInsertBeforeDevice ();
        else if (this.surface.isSelectPressed ())
            browser.browseToInsertAfterDevice ();
        else if (this.model.getCursorDevice ().hasSelectedDevice ())
            browser.browseForPresets ();
        else
            browser.browseToInsertAfterDevice ();

        this.surface.scheduleTask ( () -> {
            if (this.model.getBrowser ().isActive ())
                this.surface.getModeManager ().setActiveMode (Modes.MODE_BROWSER);
        }, 200);
    }

}
