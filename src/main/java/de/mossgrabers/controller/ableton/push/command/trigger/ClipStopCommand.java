// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.command.trigger;

import de.mossgrabers.controller.ableton.push.PushConfiguration;
import de.mossgrabers.controller.ableton.push.PushConfiguration.LockState;
import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to handle the Clip Stop button.
 *
 * @author Jürgen Moßgraber
 */
public class ClipStopCommand extends AbstractTriggerCommand<PushControlSurface, PushConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public ClipStopCommand (final IModel model, final PushControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        // Update for key combinations
        this.surface.getViewManager ().getActive ().updateNoteMapping ();

        if (this.surface.isSelectPressed ())
        {
            if (event == ButtonEvent.UP)
                this.model.getCurrentTrackBank ().stop (this.surface.isShiftPressed ());
            return;
        }

        final PushConfiguration config = this.surface.getConfiguration ();
        if (!config.isPushModern ())
        {
            if (event == ButtonEvent.DOWN)
                config.setLockState (LockState.CLIP_STOP);
            return;
        }

        if (event != ButtonEvent.UP)
            return;

        // Toggle clip stop lock mode
        if (this.surface.isShiftPressed () || this.surface.isPressed (ButtonID.LOCK_MODE))
        {
            config.setLockState (config.getLockState () == LockState.CLIP_STOP ? LockState.OFF : LockState.CLIP_STOP);
            return;
        }

        // Behavior like Push 1
        if (config.getLockState () == LockState.CLIP_STOP)
            return;

        this.model.getCursorTrack ().stop (this.surface.isShiftPressed ());
    }
}
