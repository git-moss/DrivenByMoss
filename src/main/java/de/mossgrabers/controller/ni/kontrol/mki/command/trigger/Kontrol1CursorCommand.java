// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.kontrol.mki.command.trigger;

import de.mossgrabers.controller.ni.kontrol.mki.Kontrol1Configuration;
import de.mossgrabers.controller.ni.kontrol.mki.controller.Kontrol1ControlSurface;
import de.mossgrabers.framework.command.trigger.Direction;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.IMode;


/**
 * Command for cursor arrow keys.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Kontrol1CursorCommand extends de.mossgrabers.framework.command.trigger.mode.CursorCommand<Kontrol1ControlSurface, Kontrol1Configuration>
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
        super (direction, model, surface, false);
    }


    /** {@inheritDoc} */
    @Override
    protected void scrollUp ()
    {
        final IMode mode = this.surface.getModeManager ().getActive ();
        if (mode != null)
            mode.selectNextItemPage ();
    }


    /** {@inheritDoc} */
    @Override
    protected void scrollDown ()
    {
        final IMode mode = this.surface.getModeManager ().getActive ();
        if (mode != null)
            mode.selectPreviousItemPage ();
    }
}
