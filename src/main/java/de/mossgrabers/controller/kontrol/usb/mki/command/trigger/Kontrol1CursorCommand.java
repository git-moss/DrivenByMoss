// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.usb.mki.command.trigger;

import de.mossgrabers.controller.kontrol.usb.mki.Kontrol1Configuration;
import de.mossgrabers.controller.kontrol.usb.mki.controller.Kontrol1ControlSurface;
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
        this.surface.getModeManager ().getActiveOrTempMode ().selectPreviousItem ();
    }


    /** {@inheritDoc} */
    @Override
    protected void scrollRight ()
    {
        this.surface.getModeManager ().getActiveOrTempMode ().selectNextItem ();
    }


    /** {@inheritDoc} */
    @Override
    protected void scrollUp ()
    {
        this.surface.getModeManager ().getActiveOrTempMode ().selectNextItemPage ();
    }


    /** {@inheritDoc} */
    @Override
    protected void scrollDown ()
    {
        this.surface.getModeManager ().getActiveOrTempMode ().selectPreviousItemPage ();
    }
}
