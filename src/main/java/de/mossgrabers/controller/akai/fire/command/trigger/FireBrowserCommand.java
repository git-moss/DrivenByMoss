// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.fire.command.trigger;

import de.mossgrabers.controller.akai.fire.FireConfiguration;
import de.mossgrabers.controller.akai.fire.controller.FireControlSurface;
import de.mossgrabers.framework.command.trigger.BrowserCommand;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;


/**
 * Command to open and close the browser.
 *
 * @author Jürgen Moßgraber
 */
public class FireBrowserCommand extends BrowserCommand<FireControlSurface, FireConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public FireBrowserCommand (final IModel model, final FireControlSurface surface)
    {
        super (model, surface, ButtonID.SHIFT, ButtonID.ALT);
    }


    /** {@inheritDoc} */
    @Override
    protected boolean getCommit ()
    {
        return !this.surface.isPressed (ButtonID.ALT);
    }
}
