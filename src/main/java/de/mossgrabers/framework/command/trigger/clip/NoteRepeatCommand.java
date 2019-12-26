// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger.clip;

import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.midi.INoteInput;
import de.mossgrabers.framework.daw.midi.INoteRepeat;
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
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (this.handleEditModeActivation (event))
            return;

        final INoteInput defaultNoteInput = this.surface.getMidiInput ().getDefaultNoteInput ();
        if (defaultNoteInput == null)
            return;

        final INoteRepeat noteRepeat = defaultNoteInput.getNoteRepeat ();
        noteRepeat.toggleActive ();
        this.model.getHost ().scheduleTask ( () -> this.surface.getConfiguration ().setNoteRepeatActive (noteRepeat.isActive ()), 300);
    }


    /**
     * Handle the de-/activation of the edit mode.
     *
     * @param event The event
     * @return True to cancel further processing
     */
    protected boolean handleEditModeActivation (final ButtonEvent event)
    {
        final ModeManager modeManager = this.surface.getModeManager ();
        if (event == ButtonEvent.LONG || event == ButtonEvent.DOWN && this.surface.isShiftPressed ())
        {
            modeManager.setActiveMode (Modes.REPEAT_NOTE);
            this.surface.setTriggerConsumed (ButtonID.REPEAT);
            return true;
        }

        if (event != ButtonEvent.UP)
            return true;

        if (Modes.REPEAT_NOTE.equals (modeManager.getActiveOrTempModeId ()))
        {
            modeManager.restoreMode ();
            return true;
        }

        return false;
    }
}
