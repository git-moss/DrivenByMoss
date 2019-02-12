// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.usb.mki.command.trigger;

import de.mossgrabers.controller.kontrol.usb.mki.Kontrol1Configuration;
import de.mossgrabers.controller.kontrol.usb.mki.controller.Kontrol1ControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.Mode;


/**
 * Command for cursor arrow keys.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Kontrol1CursorCommand extends de.mossgrabers.framework.command.trigger.CursorCommand<Kontrol1ControlSurface, Kontrol1Configuration>
{
    /**
     * Constructor.
     *
     * @param direction The direction of the kontrol1ed cursor arrow
     * @param model The model
     * @param surface The surface
     */
    public Kontrol1CursorCommand (final Direction direction, final IModel model, final Kontrol1ControlSurface surface)
    {
        super (direction, model, surface);
    }


    /** {@inheritDoc} */
    @Override
    protected void scrollUp ()
    {
        final Mode mode = this.surface.getModeManager ().getActiveOrTempMode ();
        if (mode != null)
            mode.selectNextItemPage ();
    }


    /** {@inheritDoc} */
    @Override
    protected void scrollDown ()
    {
        final Mode mode = this.surface.getModeManager ().getActiveOrTempMode ();
        if (mode != null)
            mode.selectPreviousItemPage ();
    }
}
