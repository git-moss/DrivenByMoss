package de.mossgrabers.controller.novation.launchcontrol.control.trigger;

import de.mossgrabers.controller.novation.launchcontrol.controller.LaunchControlXLControlSurface;
import de.mossgrabers.controller.novation.launchcontrol.mode.XLSelectDeviceParamsPageMode;
import de.mossgrabers.controller.novation.launchcontrol.mode.XLMixMode;
import de.mossgrabers.framework.command.core.TriggerCommand;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command for the device button.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class XLDeviceCommand implements TriggerCommand
{
    private final LaunchControlXLControlSurface surface;
    private final XLMixMode                     mixMode;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param mixMode The mix mode to toggle the device state
     */
    public XLDeviceCommand (final LaunchControlXLControlSurface surface, final XLMixMode mixMode)
    {
        this.surface = surface;
        this.mixMode = mixMode;
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        final ModeManager trackButtonModeManager = this.surface.getTrackButtonModeManager ();

        if (event == ButtonEvent.DOWN)
        {
            trackButtonModeManager.setTemporary (Modes.DEVICE_PARAMS);
            return;
        }

        if (event == ButtonEvent.UP)
        {
            if (!((XLSelectDeviceParamsPageMode) trackButtonModeManager.get (Modes.DEVICE_PARAMS)).hasPageBeenSelected ())
                this.mixMode.toggleDeviceActive ();
            trackButtonModeManager.restore ();
        }
    }
}
