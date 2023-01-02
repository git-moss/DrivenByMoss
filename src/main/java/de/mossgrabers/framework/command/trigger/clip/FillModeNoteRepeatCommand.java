// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger.clip;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to toggle note repeat. Adds Select+Repeat to toggle Fill Mode.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class FillModeNoteRepeatCommand<S extends IControlSurface<C>, C extends Configuration> extends NoteRepeatCommand<S, C>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     * @param isMode If true there is an edit mode for adjusting the setting (Modes.NOTE_REPEAT)
     *            otherwise there must be a view (Views.NOTE_REPEAT).
     */
    public FillModeNoteRepeatCommand (final IModel model, final S surface, final boolean isMode)
    {
        super (model, surface, isMode);
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
