// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.mcu.command.trigger;

import de.mossgrabers.controller.mackie.mcu.MCUConfiguration;
import de.mossgrabers.controller.mackie.mcu.controller.MCUControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to toggle the segment display between ticks and tempo.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TempoTicksCommand extends AbstractTriggerCommand<MCUControlSurface, MCUConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public TempoTicksCommand (final IModel model, final MCUControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final MCUConfiguration configuration = this.surface.getConfiguration ();

        if (this.surface.isSelectPressed ())
            configuration.toggleDisplayTime ();
        else
            configuration.toggleDisplayTicks ();

        this.model.getHost ().scheduleTask ( () -> {
            final String message = (configuration.isDisplayTime () ? "TIME - " : "BEATS - ") + (configuration.isDisplayTicks () ? "TICKS" : "TEMPO");
            this.surface.getDisplay ().notify (message);
        }, 200);
    }
}
