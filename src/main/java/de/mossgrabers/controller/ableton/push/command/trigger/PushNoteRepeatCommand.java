// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.command.trigger;

import de.mossgrabers.controller.ableton.push.PushConfiguration;
import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.framework.command.trigger.clip.NoteRepeatCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to toggle note repeat. Adds Select+Repeat to toggle Fille Mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PushNoteRepeatCommand extends NoteRepeatCommand<PushControlSurface, PushConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public PushNoteRepeatCommand (final IModel model, final PushControlSurface surface)
    {
        super (model, surface, true);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (this.surface.isSelectPressed ())
        {
            if (event == ButtonEvent.DOWN)
            {
                final ITransport transport = this.model.getTransport ();
                transport.toggleFillModeActive ();
                this.mvHelper.delayDisplay ( () -> "Fill Mode: " + (transport.isFillModeActive () ? "On" : "Off"));
            }
            return;
        }

        super.execute (event, velocity);
    }
}
