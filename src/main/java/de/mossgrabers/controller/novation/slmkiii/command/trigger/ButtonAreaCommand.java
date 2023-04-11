// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.slmkiii.command.trigger;

import de.mossgrabers.controller.novation.slmkiii.SLMkIIIConfiguration;
import de.mossgrabers.controller.novation.slmkiii.controller.SLMkIIIControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.command.core.TriggerCommand;
import de.mossgrabers.framework.command.trigger.track.MonitorCommand;
import de.mossgrabers.framework.command.trigger.track.MuteCommand;
import de.mossgrabers.framework.command.trigger.track.RecArmCommand;
import de.mossgrabers.framework.command.trigger.track.SoloCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to trigger the 16 buttons for solo/mute and monitor/rec arm.
 *
 * @author Jürgen Moßgraber
 */
public class ButtonAreaCommand extends AbstractTriggerCommand<SLMkIIIControlSurface, SLMkIIIConfiguration>
{
    private final TriggerCommand command1;
    private final TriggerCommand command2;


    /**
     * Constructor.
     *
     * @param row The row of the button (0 or 1)
     * @param column The column of the button (0-7)
     * @param model The model
     * @param surface The surface
     */
    public ButtonAreaCommand (final int row, final int column, final IModel model, final SLMkIIIControlSurface surface)
    {
        super (model, surface);

        this.command1 = row == 0 ? new MuteCommand<> (column, model, surface) : new SoloCommand<> (column, model, surface);
        this.command2 = row == 0 ? new MonitorCommand<> (column, model, surface) : new RecArmCommand<> (column, model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (this.surface.isMuteSolo ())
            this.command1.execute (event, velocity);
        else
            this.command2.execute (event, velocity);
    }
}
