// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.maschine.command.trigger;

import de.mossgrabers.controller.maschine.MaschineConfiguration;
import de.mossgrabers.controller.maschine.controller.MaschineControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command for toggling the fixed velocity.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ToggleFixedVelCommand extends AbstractTriggerCommand<MaschineControlSurface, MaschineConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public ToggleFixedVelCommand (final IModel model, final MaschineControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (event != ButtonEvent.DOWN)
            return;

        if (this.surface.isPressed (ButtonID.STOP))
        {
            this.surface.setTriggerConsumed (ButtonID.STOP);
            final Scales scales = this.model.getScales ();
            scales.toggleChromatic ();
            this.surface.getDisplay ().notify ("Chromatic: " + (scales.isChromatic () ? "On" : "Off"));
            this.surface.getConfiguration ().setScaleInKey (!scales.isChromatic ());
            return;
        }

        final MaschineConfiguration configuration = this.surface.getConfiguration ();
        final boolean enabled = !configuration.isAccentActive ();
        configuration.setAccentEnabled (enabled);
        this.surface.getDisplay ().notify ("Fixed Velocity: " + (enabled ? "On" : "Off"));
    }
}
