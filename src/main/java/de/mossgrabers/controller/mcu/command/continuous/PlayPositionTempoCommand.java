// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mcu.command.continuous;

import de.mossgrabers.controller.mcu.MCUConfiguration;
import de.mossgrabers.controller.mcu.controller.MCUControlSurface;
import de.mossgrabers.framework.command.continuous.PlayPositionCommand;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;


/**
 * Command to change the time (play position).
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PlayPositionTempoCommand extends PlayPositionCommand<MCUControlSurface, MCUConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public PlayPositionTempoCommand (final IModel model, final MCUControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final int value)
    {
        if (this.surface.isPressed (ButtonID.SELECT))
            this.model.getTransport ().changeTempo (this.model.getValueChanger ().isIncrease (value), this.surface.isKnobSensitivitySlow ());
        else
            super.execute (value);
    }
}
