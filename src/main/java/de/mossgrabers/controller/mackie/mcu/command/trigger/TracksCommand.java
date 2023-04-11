// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.mcu.command.trigger;

import de.mossgrabers.controller.mackie.mcu.MCUConfiguration;
import de.mossgrabers.controller.mackie.mcu.controller.MCUControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.command.trigger.mode.ModeMultiSelectCommand;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to switch to the track/layer modes.
 *
 * @author Jürgen Moßgraber
 */
public class TracksCommand extends AbstractTriggerCommand<MCUControlSurface, MCUConfiguration>
{
    private final ModeMultiSelectCommand<MCUControlSurface, MCUConfiguration> trackModesCommand;
    private final ModeMultiSelectCommand<MCUControlSurface, MCUConfiguration> layerModesCommand;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public TracksCommand (final IModel model, final MCUControlSurface surface)
    {
        super (model, surface);

        this.trackModesCommand = new ModeMultiSelectCommand<> (model, surface, Modes.VOLUME, Modes.TRACK);
        this.layerModesCommand = new ModeMultiSelectCommand<> (model, surface, Modes.DEVICE_LAYER_VOLUME, Modes.DEVICE_LAYER);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (this.surface.isPressed (ButtonID.SELECT))
        {
            if (event == ButtonEvent.DOWN)
                this.model.getCursorTrack ().togglePinned ();
            return;
        }

        if (Modes.isLayerMode (this.surface.getModeManager ().getActiveID ()))
            this.layerModesCommand.execute (event, velocity);
        else
            this.trackModesCommand.execute (event, velocity);
    }
}
