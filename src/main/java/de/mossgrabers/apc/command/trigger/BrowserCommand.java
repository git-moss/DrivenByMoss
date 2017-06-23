// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.apc.command.trigger;

import de.mossgrabers.apc.APCConfiguration;
import de.mossgrabers.apc.controller.APCControlSurface;
import de.mossgrabers.apc.mode.Modes;
import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.BrowserProxy;


/**
 * The browser command.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class BrowserCommand extends AbstractTriggerCommand<APCControlSurface, APCConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public BrowserCommand (final Model model, final APCControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event == ButtonEvent.DOWN)
            this.startBrowser (false, false);
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        if (event == ButtonEvent.DOWN)
            this.startBrowser (true, true);
    }


    /**
     * Start a browser.
     *
     * @param insertDevice Insert a device if true otherwise select preset
     * @param beforeCurrent Insert the device before the current device if any
     */
    public void startBrowser (final boolean insertDevice, final boolean beforeCurrent)
    {
        final BrowserProxy browser = this.model.getBrowser ();

        // Patch Browser already active?
        if (browser.isActive ())
        {
            // Confirm or discard new selected patch
            final boolean stopMode = this.surface.isMkII () ? !this.surface.isShiftPressed () : true;
            this.model.getBrowser ().stopBrowsing (stopMode);
            this.surface.getModeManager ().restoreMode ();
            return;
        }

        if (insertDevice)
        {
            if (beforeCurrent)
                browser.browseToInsertBeforeDevice ();
            else
                browser.browseToInsertAfterDevice ();
            return;
        }

        // Browse for presets
        if (this.model.getCursorDevice ().hasSelectedDevice ())
            browser.browseForPresets ();
        else
            browser.browseToInsertAfterDevice ();

        this.surface.scheduleTask ( () -> {
            if (this.model.getBrowser ().isActive ())
                this.surface.getModeManager ().setActiveMode (Modes.MODE_BROWSER);
        }, 200);
    }
}
