// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.mcu.command.trigger;

import de.mossgrabers.controller.mackie.mcu.MCUConfiguration;
import de.mossgrabers.controller.mackie.mcu.controller.MCUControlSurface;
import de.mossgrabers.controller.mackie.mcu.mode.MCUMultiModeSwitcherCommand;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to switch to the panorama modes.
 *
 * @author Jürgen Moßgraber
 */
public class PanCommand extends AbstractTriggerCommand<MCUControlSurface, MCUConfiguration>
{
    private final MCUMultiModeSwitcherCommand trackModesCommand;
    private final MCUMultiModeSwitcherCommand layerModesCommand;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public PanCommand (final IModel model, final MCUControlSurface surface)
    {
        super (model, surface);

        this.trackModesCommand = new MCUMultiModeSwitcherCommand (model, surface, Modes.PAN, Modes.TRACK);
        this.layerModesCommand = new MCUMultiModeSwitcherCommand (model, surface, Modes.DEVICE_LAYER_PAN, Modes.DEVICE_LAYER);
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
