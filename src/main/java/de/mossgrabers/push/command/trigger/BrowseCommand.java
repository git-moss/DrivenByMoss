// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.command.trigger;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.BrowserProxy;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.push.PushConfiguration;
import de.mossgrabers.push.controller.PushControlSurface;
import de.mossgrabers.push.mode.Modes;


/**
 * Command to Browse presets.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class BrowseCommand extends AbstractTriggerCommand<PushControlSurface, PushConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public BrowseCommand (final Model model, final PushControlSurface surface)
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

        // Already browsing?
        final ModeManager modeManager = this.surface.getModeManager ();
        if (modeManager.isActiveMode (Modes.MODE_BROWSER))
        {
            browser.stopBrowsing (!this.surface.isShiftPressed ());
            modeManager.restoreMode ();
            return;
        }

        // Browse for presets
        browser.browseForPresets ();
        modeManager.setActiveMode (Modes.MODE_BROWSER);
    }
}
