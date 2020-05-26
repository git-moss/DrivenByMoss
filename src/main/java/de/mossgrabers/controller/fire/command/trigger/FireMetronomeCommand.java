// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.fire.command.trigger;

import de.mossgrabers.controller.fire.FireConfiguration;
import de.mossgrabers.controller.fire.controller.FireControlSurface;
import de.mossgrabers.framework.command.trigger.transport.MetronomeCommand;
import de.mossgrabers.framework.command.trigger.transport.TapTempoCommand;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to handle the metrononme button.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class FireMetronomeCommand extends MetronomeCommand<FireControlSurface, FireConfiguration>
{
    private final TapTempoCommand<FireControlSurface, FireConfiguration> tapTempoCommand;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public FireMetronomeCommand (final IModel model, final FireControlSurface surface)
    {
        super (model, surface);

        this.tapTempoCommand = new TapTempoCommand<> (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (this.surface.isPressed (ButtonID.ALT))
        {
            this.tapTempoCommand.execute (event, velocity);
            return;
        }

        super.execute (event, velocity);
    }
}
