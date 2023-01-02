// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.mcu.command.trigger;

import de.mossgrabers.controller.mackie.mcu.MCUConfiguration;
import de.mossgrabers.controller.mackie.mcu.controller.MCUControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.command.trigger.mode.ModeMultiSelectCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Selects the next Send mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SendSelectCommand extends AbstractTriggerCommand<MCUControlSurface, MCUConfiguration>
{
    private final ModeMultiSelectCommand<MCUControlSurface, MCUConfiguration> trackModesCommand;
    private final ModeMultiSelectCommand<MCUControlSurface, MCUConfiguration> layerModesCommand;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public SendSelectCommand (final IModel model, final MCUControlSurface surface)
    {
        super (model, surface);

        this.trackModesCommand = new ModeMultiSelectCommand<> (model, surface, Modes.SEND1, Modes.SEND2, Modes.SEND3, Modes.SEND4, Modes.SEND5, Modes.SEND6, Modes.SEND7, Modes.SEND8);
        this.layerModesCommand = new ModeMultiSelectCommand<> (model, surface, Modes.DEVICE_LAYER_SEND1, Modes.DEVICE_LAYER_SEND2, Modes.DEVICE_LAYER_SEND3, Modes.DEVICE_LAYER_SEND4, Modes.DEVICE_LAYER_SEND5, Modes.DEVICE_LAYER_SEND6, Modes.DEVICE_LAYER_SEND7, Modes.DEVICE_LAYER_SEND8);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (Modes.isLayerMode (this.surface.getModeManager ().getActiveID ()))
            this.layerModesCommand.execute (event, velocity);
        else
            this.trackModesCommand.execute (event, velocity);
    }
}
