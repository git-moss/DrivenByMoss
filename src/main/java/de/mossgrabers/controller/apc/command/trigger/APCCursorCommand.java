// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.apc.command.trigger;

import de.mossgrabers.controller.apc.APCConfiguration;
import de.mossgrabers.controller.apc.controller.APCControlSurface;
import de.mossgrabers.framework.command.trigger.mode.CursorCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.Modes;


/**
 * APC command for cursor arrow keys.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class APCCursorCommand extends CursorCommand<APCControlSurface, APCConfiguration>
{
    /**
     * Constructor.
     *
     * @param direction The direction of the pushed cursor arrow
     * @param model The model
     * @param surface The surface
     */
    public APCCursorCommand (final Direction direction, final IModel model, final APCControlSurface surface)
    {
        super (direction, model, surface);
    }


    /**
     * Scroll scenes up.
     */
    @Override
    protected void scrollUp ()
    {
        if (this.surface.getModeManager ().isActive (Modes.BROWSER))
        {
            this.model.getBrowser ().selectPreviousResult ();
            return;
        }
        super.scrollUp ();
    }


    /**
     * Scroll scenes down.
     */
    @Override
    protected void scrollDown ()
    {
        if (this.surface.getModeManager ().isActive (Modes.BROWSER))
        {
            this.model.getBrowser ().selectNextResult ();
            return;
        }
        super.scrollDown ();
    }
}
