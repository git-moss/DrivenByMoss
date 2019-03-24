// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger;

import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.constants.TransportConstants;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to change the automation parameters.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class AutomationCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    private int index;


    /**
     * Constructor.
     *
     * @param index The automation index
     * @param model The model
     * @param surface The surface
     */
    public AutomationCommand (final int index, final IModel model, final S surface)
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
