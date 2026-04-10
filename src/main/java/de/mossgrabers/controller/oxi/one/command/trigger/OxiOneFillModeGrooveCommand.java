// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.oxi.one.command.trigger;

import de.mossgrabers.controller.oxi.one.OxiOneConfiguration;
import de.mossgrabers.controller.oxi.one.controller.OxiOneControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to toggle fill-mode (FLOW). Shift+Fill will open the Groove menu.
 *
 * @author Jürgen Moßgraber
 */
public class OxiOneFillModeGrooveCommand extends AbstractTriggerCommand<OxiOneControlSurface, OxiOneConfiguration>
{
    private final ITransport transport;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public OxiOneFillModeGrooveCommand (final IModel model, final OxiOneControlSurface surface)
    {
        super (model, surface);

        this.transport = this.model.getTransport ();
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event == ButtonEvent.UP)
        {
            this.transport.toggleFillModeActive ();
            this.mvHelper.delayDisplay ( () -> "Fill Mode: " + (this.transport.isFillModeActive () ? "On" : "Off"));
        }
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        if (event == ButtonEvent.DOWN)
            this.surface.getModeManager ().setTemporary (Modes.GROOVE);
    }
}
