// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.command.trigger;

import de.mossgrabers.controller.ableton.push.PushConfiguration;
import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.framework.command.trigger.application.DeleteCommand;
import de.mossgrabers.framework.command.trigger.track.EnableCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Push 1 delete button. SHIFT + DELETE toggles the selected track's active state in Track and
 * Volume modes.
 *
 * @author Jürgen Moßgraber
 */
public class Push1DeleteCommand extends DeleteCommand<PushControlSurface, PushConfiguration>
{
    private final EnableCommand<PushControlSurface, PushConfiguration> enableCommand;
    private boolean shiftDeletePressed;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public Push1DeleteCommand (final IModel model, final PushControlSurface surface)
    {
        super (model, surface);
        this.enableCommand = new EnableCommand<> (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        // Latch the modifier state on button-down. This prevents releasing SHIFT before DELETE
        // from accidentally invoking the normal delete action on button-up.
        if (event == ButtonEvent.DOWN)
            this.shiftDeletePressed = this.surface.isShiftPressed ();

        final boolean isShiftDelete = this.shiftDeletePressed;
        if (event == ButtonEvent.UP)
            this.shiftDeletePressed = false;

        if (isShiftDelete)
            this.executeShifted (event);
        else
            this.executeNormal (event);
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;

        final Modes activeMode = this.surface.getModeManager ().getActiveID ();
        if (Modes.TRACK.equals (activeMode) || Modes.VOLUME.equals (activeMode))
            this.enableCommand.executeNormal (event);
    }
}
