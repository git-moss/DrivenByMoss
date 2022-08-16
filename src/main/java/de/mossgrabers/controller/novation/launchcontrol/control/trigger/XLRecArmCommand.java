package de.mossgrabers.controller.novation.launchcontrol.control.trigger;

import de.mossgrabers.controller.novation.launchcontrol.LaunchControlXLConfiguration;
import de.mossgrabers.controller.novation.launchcontrol.controller.LaunchControlXLControlSurface;
import de.mossgrabers.controller.novation.launchcontrol.mode.XLTransportMode;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command for the record arm button.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class XLRecArmCommand extends AbstractTriggerCommand<LaunchControlXLControlSurface, LaunchControlXLConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public XLRecArmCommand (final IModel model, final LaunchControlXLControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        final ModeManager trackButtonModeManager = this.surface.getTrackButtonModeManager ();

        if (event == ButtonEvent.DOWN)
        {
            trackButtonModeManager.setTemporary (Modes.TRANSPORT);
            return;
        }

        if (event == ButtonEvent.UP)
        {
            trackButtonModeManager.restore ();

            if (!((XLTransportMode) trackButtonModeManager.get (Modes.TRANSPORT)).hasTransportBeenSelected ())
                trackButtonModeManager.setActive (Modes.REC_ARM);
        }
    }
}
