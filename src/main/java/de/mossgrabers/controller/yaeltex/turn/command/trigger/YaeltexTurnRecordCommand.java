// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.yaeltex.turn.command.trigger;

import de.mossgrabers.controller.yaeltex.turn.YaeltexTurnConfiguration;
import de.mossgrabers.controller.yaeltex.turn.controller.YaeltexTurnControlSurface;
import de.mossgrabers.framework.command.trigger.transport.RecordCommand;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to handle the record button.
 *
 * @author Jürgen Moßgraber
 */
public class YaeltexTurnRecordCommand extends RecordCommand<YaeltexTurnControlSurface, YaeltexTurnConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public YaeltexTurnRecordCommand (final IModel model, final YaeltexTurnControlSurface surface)
    {
        super (model, surface, null, ButtonID.SELECT, ButtonID.SHIFT);
    }


    /** {@inheritDoc} */
    @Override
    protected void executeLauncherOverdub (final ButtonEvent event)
    {
        if (event == ButtonEvent.UP)
            this.transport.toggleWriteArrangerAutomation ();
    }


    /** {@inheritDoc} */
    @Override
    protected boolean getLauncherOverdubState ()
    {
        return this.transport.isWritingArrangerAutomation ();
    }
}
