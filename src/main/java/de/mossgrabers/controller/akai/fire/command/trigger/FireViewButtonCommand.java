// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.fire.command.trigger;

import de.mossgrabers.controller.akai.fire.FireConfiguration;
import de.mossgrabers.controller.akai.fire.controller.FireControlSurface;
import de.mossgrabers.framework.command.trigger.view.ViewButtonCommand;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IBrowser;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to relay a command to the active view. Special handling if browser mode is active.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class FireViewButtonCommand extends ViewButtonCommand<FireControlSurface, FireConfiguration>
{
    /**
     * Constructor.
     *
     * @param buttonID The button which events to relay
     * @param model The model
     * @param surface The surface
     */
    public FireViewButtonCommand (final ButtonID buttonID, final IModel model, final FireControlSurface surface)
    {
        super (buttonID, model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (this.surface.getModeManager ().isActive (Modes.BROWSER))
        {
            if (velocity == 0)
                return;
            final IBrowser browser = this.model.getBrowser ();
            if (browser == null)
                return;
            if (this.buttonID == ButtonID.ARROW_LEFT)
                browser.previousContentType ();
            else if (this.buttonID == ButtonID.ARROW_RIGHT)
                browser.nextContentType ();
            return;
        }

        super.execute (event, velocity);
    }
}
