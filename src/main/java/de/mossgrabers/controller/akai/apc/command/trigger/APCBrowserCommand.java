// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.apc.command.trigger;

import de.mossgrabers.controller.akai.apc.APCConfiguration;
import de.mossgrabers.controller.akai.apc.controller.APCControlSurface;
import de.mossgrabers.framework.command.trigger.BrowserCommand;
import de.mossgrabers.framework.daw.IModel;


/**
 * The browser command.
 *
 * @author Jürgen Moßgraber
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
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    protected boolean getCommit ()
    {
        return !this.surface.isMkII () || !this.surface.isShiftPressed ();
    }
}
