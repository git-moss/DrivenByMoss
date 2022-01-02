// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.mcu.command.continuous;

import de.mossgrabers.controller.mackie.mcu.MCUConfiguration;
import de.mossgrabers.controller.mackie.mcu.controller.MCUControlSurface;
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
        final boolean increase = this.model.getValueChanger ().isIncrease (value);

        if (this.surface.isPressed (ButtonID.SELECT))
        {
            this.model.getTransport ().changeTempo (increase, this.surface.isKnobSensitivitySlow ());
            return;
        }

        if (this.surface.isPressed (ButtonID.ARROW_LEFT) || this.surface.isPressed (ButtonID.ARROW_RIGHT))
        {
            this.surface.setTriggerConsumed (ButtonID.ARROW_LEFT);
            this.surface.setTriggerConsumed (ButtonID.ARROW_RIGHT);

            if (increase)
                this.model.getApplication ().zoomIn ();
            else
                this.model.getApplication ().zoomOut ();
            return;
        }

        if (this.surface.isPressed (ButtonID.ARROW_UP) || this.surface.isPressed (ButtonID.ARROW_DOWN))
        {
            this.surface.setTriggerConsumed (ButtonID.ARROW_UP);
            this.surface.setTriggerConsumed (ButtonID.ARROW_DOWN);

            if (increase)
                this.model.getApplication ().incTrackHeight ();
            else
                this.model.getApplication ().decTrackHeight ();
            return;
        }

        super.execute (value);
    }
}
