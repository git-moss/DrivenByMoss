// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.fire.command.trigger;

import de.mossgrabers.controller.akai.fire.FireConfiguration;
import de.mossgrabers.controller.akai.fire.controller.FireControlSurface;
import de.mossgrabers.framework.command.trigger.clip.NewCommand;
import de.mossgrabers.framework.command.trigger.transport.RecordCommand;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to handle the record button.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class FireRecordCommand extends RecordCommand<FireControlSurface, FireConfiguration>
{
    private final NewCommand<FireControlSurface, FireConfiguration> newCommand;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public FireRecordCommand (final IModel model, final FireControlSurface surface)
    {
        super (model, surface);

        this.newCommand = new NewCommand<> (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (this.surface.isPressed (ButtonID.ALT))
        {
            this.newCommand.execute (event, velocity);
            return;
        }

        super.execute (event, velocity);
    }
}
