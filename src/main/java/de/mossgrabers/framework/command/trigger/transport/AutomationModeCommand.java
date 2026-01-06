// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger.transport;

import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.constants.AutomationMode;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Command to change the automation parameters.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author Jürgen Moßgraber
 */
public class AutomationModeCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    private final AutomationMode autoMode;
    private final boolean        notify;


    /**
     * Constructor.
     *
     * @param autoMode The automation mode
     * @param model The model
     * @param surface The surface
     * @param notify Notify on the display
     */
    public AutomationModeCommand (final AutomationMode autoMode, final IModel model, final S surface, final boolean notify)
    {
        super (model, surface);

        this.autoMode = autoMode;
        this.notify = notify;
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final ITransport transport = this.model.getTransport ();
        if (this.surface.isSelectPressed ())
            transport.resetAutomationOverrides ();
        else
        {
            transport.setAutomationWriteMode (this.autoMode);
            if (this.notify)
                this.surface.getDisplay ().notify (StringUtils.limit (this.autoMode.name (), 4));
        }
    }
}
