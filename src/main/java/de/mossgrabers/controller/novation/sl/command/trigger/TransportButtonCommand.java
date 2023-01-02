// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.sl.command.trigger;

import de.mossgrabers.controller.novation.sl.SLConfiguration;
import de.mossgrabers.controller.novation.sl.controller.SLControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to handle the Transport button.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TransportButtonCommand extends AbstractTriggerCommand<SLControlSurface, SLConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public TransportButtonCommand (final IModel model, final SLControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        // Note: The Transport button sends DOWN on first press and UP on the next press
        if (event == ButtonEvent.LONG)
            return;

        // Toggle transport
        final ModeManager modeManager = this.surface.getModeManager ();
        if (modeManager.isActive (Modes.VIEW_SELECT) || event == ButtonEvent.UP)
            modeManager.restore ();
        else
            modeManager.setTemporary (Modes.VIEW_SELECT);
    }
}
