// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.usb.mki.command.trigger;

import de.mossgrabers.controller.kontrol.usb.mki.Kontrol1Configuration;
import de.mossgrabers.controller.kontrol.usb.mki.controller.Kontrol1ControlSurface;
import de.mossgrabers.controller.kontrol.usb.mki.mode.IKontrol1Mode;
import de.mossgrabers.framework.daw.IModel;


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
    protected void scrollLeft ()
    {
        ((IKontrol1Mode) this.surface.getModeManager ().getActiveOrTempMode ()).scrollLeft ();
    }


    /** {@inheritDoc} */
    @Override
    protected void scrollRight ()
    {
        ((IKontrol1Mode) this.surface.getModeManager ().getActiveOrTempMode ()).scrollRight ();
    }


    /** {@inheritDoc} */
    @Override
    protected void scrollUp ()
    {
        ((IKontrol1Mode) this.surface.getModeManager ().getActiveOrTempMode ()).scrollUp ();
    }


    /** {@inheritDoc} */
    @Override
    protected void scrollDown ()
    {
        ((IKontrol1Mode) this.surface.getModeManager ().getActiveOrTempMode ()).scrollDown ();
    }
}
