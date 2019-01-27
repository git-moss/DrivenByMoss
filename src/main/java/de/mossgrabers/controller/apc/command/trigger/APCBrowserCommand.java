// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.apc.command.trigger;

import de.mossgrabers.controller.apc.APCConfiguration;
import de.mossgrabers.controller.apc.controller.APCControlSurface;
import de.mossgrabers.controller.apc.mode.Modes;
import de.mossgrabers.framework.command.trigger.BrowserCommand;
import de.mossgrabers.framework.daw.IModel;


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
    public APCBrowserCommand (final IModel model, final APCControlSurface surface)
    {
        super (Modes.MODE_BROWSER, model, surface);
    }


    /** {@inheritDoc} */
    @Override
    protected boolean getCommit ()
    {
        return !this.surface.isMkII () || !this.surface.isShiftPressed ();
    }
}
