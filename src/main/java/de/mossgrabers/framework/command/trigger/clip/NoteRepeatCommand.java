// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger.clip;

import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to toggle note repeat.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class NoteRepeatCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public NoteRepeatCommand (final IModel model, final S surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event)
    {
        final ModeManager modeManager = this.surface.getModeManager ();
        if (event == ButtonEvent.LONG || event == ButtonEvent.DOWN && this.surface.isShiftPressed ())
        {
            modeManager.setActiveMode (Modes.MODE_REPEAT_NOTE);
            this.surface.setButtonConsumed (PushControlSurface.PUSH_BUTTON_REPEAT);
            return;
        }

        if (event != ButtonEvent.UP)
            return;

        if (Modes.MODE_REPEAT_NOTE.equals (modeManager.getActiveOrTempModeId ()))
            modeManager.restoreMode ();
        else
        {
            final ITrackBank tb = this.model.getCurrentTrackBank ();
            final ITrack selectedTrack = tb.getSelectedItem ();
            if (selectedTrack != null)
                selectedTrack.toggleNoteRepeat ();
        }
    }
}
