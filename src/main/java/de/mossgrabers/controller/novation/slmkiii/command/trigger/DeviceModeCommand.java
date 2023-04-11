// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.slmkiii.command.trigger;

import de.mossgrabers.controller.novation.slmkiii.SLMkIIIConfiguration;
import de.mossgrabers.controller.novation.slmkiii.controller.SLMkIIIControlSurface;
import de.mossgrabers.controller.novation.slmkiii.mode.device.ParametersMode;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.command.trigger.mode.ModeSelectCommand;
import de.mossgrabers.framework.daw.IBrowser;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to trigger the Track modes.
 *
 * @author Jürgen Moßgraber
 */
public class DeviceModeCommand extends AbstractTriggerCommand<SLMkIIIControlSurface, SLMkIIIConfiguration>
{
    final ModeSelectCommand<SLMkIIIControlSurface, SLMkIIIConfiguration> deviceModeSelectCommand;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public DeviceModeCommand (final IModel model, final SLMkIIIControlSurface surface)
    {
        super (model, surface);

        this.deviceModeSelectCommand = new ModeSelectCommand<> (this.model, surface, Modes.DEVICE_PARAMS);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (event != ButtonEvent.UP)
            return;

        final IBrowser browser = this.model.getBrowser ();
        if (browser != null && browser.isActive ())
            browser.stopBrowsing (!this.surface.isShiftPressed ());

        final ModeManager modeManager = this.surface.getModeManager ();
        if (modeManager.isActive (Modes.DEVICE_PARAMS))
        {
            final ParametersMode parametersMode = (ParametersMode) modeManager.get (Modes.DEVICE_PARAMS);
            parametersMode.setShowDevices (!parametersMode.isShowDevices ());
            return;
        }

        this.deviceModeSelectCommand.execute (ButtonEvent.DOWN, 127);
    }
}
