// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mcu.command.trigger;

import de.mossgrabers.controller.mcu.MCUConfiguration;
import de.mossgrabers.controller.mcu.controller.MCUControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.constants.TransportConstants;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to change the automation parameters.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class AutomationCommand extends AbstractTriggerCommand<MCUControlSurface, MCUConfiguration>
{
    private int index;


    /**
     * Constructor.
     *
     * @param index The automation index
     * @param model The model
     * @param surface The surface
     */
    public AutomationCommand (final int index, final IModel model, final MCUControlSurface surface)
    {
        super (model, surface);
        this.index = index;
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final ITransport transport = this.model.getTransport ();
        switch (this.index)
        {
            // Read/Off
            case 0:
                if (this.surface.isSelectPressed ())
                    this.model.getTransport ().resetAutomationOverrides ();
                else if (transport.isWritingArrangerAutomation ())
                    transport.toggleWriteArrangerAutomation ();
                break;
            // Write
            case 1:
                transport.setAutomationWriteMode (TransportConstants.AUTOMATION_MODES_VALUES[2]);
                if (!transport.isWritingArrangerAutomation ())
                    transport.toggleWriteArrangerAutomation ();
                break;
            // Trim
            case 2:
                transport.toggleWriteClipLauncherAutomation ();
                break;
            // Touch
            case 3:
                transport.setAutomationWriteMode (TransportConstants.AUTOMATION_MODES_VALUES[1]);
                if (!transport.isWritingArrangerAutomation ())
                    transport.toggleWriteArrangerAutomation ();
                break;
            // Latch
            case 4:
                transport.setAutomationWriteMode (TransportConstants.AUTOMATION_MODES_VALUES[0]);
                if (!transport.isWritingArrangerAutomation ())
                    transport.toggleWriteArrangerAutomation ();
                break;
            default:
                // Not used
                break;
        }
    }
}
