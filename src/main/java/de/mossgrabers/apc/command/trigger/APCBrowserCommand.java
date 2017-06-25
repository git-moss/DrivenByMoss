// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.apc.command.trigger;

import de.mossgrabers.apc.APCConfiguration;
import de.mossgrabers.apc.controller.APCControlSurface;
import de.mossgrabers.apc.mode.Modes;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.trigger.BrowserCommand;


/**
 * The browser command.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class APCBrowserCommand extends BrowserCommand<APCControlSurface, APCConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public APCBrowserCommand (final Model model, final APCControlSurface surface)
    {
        super (Modes.MODE_BROWSER, model, surface);
    }


    /** {@inheritDoc} */
    @Override
    protected boolean getCommit ()
    {
        return this.surface.isMkII () ? !this.surface.isShiftPressed () : true;
    }
}
