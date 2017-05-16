// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.command.trigger;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.ApplicationProxy;
import de.mossgrabers.push.PushConfiguration;
import de.mossgrabers.push.controller.PushControlSurface;

import com.bitwig.extension.controller.api.Action;


/**
 * Command to trigger Audio conversion.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ConvertCommand extends AbstractTriggerCommand<PushControlSurface, PushConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public ConvertCommand (final Model model, final PushControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
        final ApplicationProxy application = this.model.getApplication ();
        final Action action = application.getAction (this.surface.isShiftPressed () ? "slice_to_multi_sampler_track" : "slice_to_drum_track");
        if (action == null)
        {
            this.surface.errorln ("Slice action not found.");
            return;
        }
        action.invoke ();
    }
}
