// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.mcu.command.trigger;

import de.mossgrabers.controller.mackie.mcu.MCUConfiguration;
import de.mossgrabers.controller.mackie.mcu.controller.MCUControlSurface;
import de.mossgrabers.framework.command.trigger.track.ToggleTrackBanksCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command for flipping the faders from controlling volume to controlling parameters.
 *
 * @author Jürgen Moßgraber
 */
public class MCUFlipCommand extends ToggleTrackBanksCommand<MCUControlSurface, MCUConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public MCUFlipCommand (final IModel model, final MCUControlSurface surface)
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
        configuration.toggleUseFadersAsKnobs ();
        this.mvHelper.delayDisplay ( () -> "Use faders as knobs: " + (configuration.useFadersAsKnobs () ? "On" : "Off"));
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        super.executeNormal (event);
    }
}
