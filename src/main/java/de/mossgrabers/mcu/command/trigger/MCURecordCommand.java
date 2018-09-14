package de.mossgrabers.mcu.command.trigger;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.command.trigger.clip.NewCommand;
import de.mossgrabers.framework.command.trigger.transport.RecordCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.mcu.MCUConfiguration;
import de.mossgrabers.mcu.controller.MCUControlSurface;


/**
 * Like the normal record command but creates a new clip when Option is pressed.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MCURecordCommand extends RecordCommand<MCUControlSurface, MCUConfiguration>
{
    final NewCommand<MCUControlSurface, MCUConfiguration> newCmd;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public MCURecordCommand (final IModel model, final MCUControlSurface surface)
    {
        super (model, surface);

        this.newCmd = new NewCommand<> (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (this.surface.isSelectPressed ())
            this.newCmd.executeNormal (event);
        else
            super.executeNormal (event);
    }
}
