// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.mcu.command.trigger;

import de.mossgrabers.controller.mackie.mcu.MCUConfiguration;
import de.mossgrabers.controller.mackie.mcu.controller.MCUControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.GrooveParameterID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command for toggling the Groove enablement.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class GrooveCommand extends AbstractTriggerCommand<MCUControlSurface, MCUConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public GrooveCommand (final IModel model, final MCUControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final IParameter parameter = this.model.getGroove ().getParameter (GrooveParameterID.ENABLED);
        parameter.setValue (parameter.getValue () == 0 ? this.model.getValueChanger ().getUpperBound () - 1 : 0);
    }
}
