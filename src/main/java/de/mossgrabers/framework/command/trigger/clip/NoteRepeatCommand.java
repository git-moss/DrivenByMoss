// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger.clip;

import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.Views;


/**
 * Command to toggle note repeat.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author Jürgen Moßgraber
 */
public class NoteRepeatCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    private enum EditMode
    {
        NONE,
        MODE,
        VIEW
    }


    private final EditMode editMode;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public NoteRepeatCommand (final IModel model, final S surface)
    {
        super (model, surface);

        this.editMode = EditMode.NONE;
    }


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     * @param isMode If true there is an edit mode for adjusting the setting (Modes.NOTE_REPEAT)
     *            otherwise there must be a view (Views.NOTE_REPEAT).
     */
    public NoteRepeatCommand (final IModel model, final S surface, final boolean isMode)
    {
        super (model, surface);

        this.editMode = isMode ? EditMode.MODE : EditMode.VIEW;
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (this.editMode == EditMode.NONE)
        {
            if (event == ButtonEvent.UP)
                this.surface.getConfiguration ().toggleNoteRepeatActive ();
            return;
        }

        if (!this.handleEditModeActivation (event))
            this.surface.getConfiguration ().toggleNoteRepeatActive ();
    }


    /**
     * Handle the de-/activation of the edit mode.
     *
     * @param event The event
     * @return True to cancel further processing
     */
    protected boolean handleEditModeActivation (final ButtonEvent event)
    {
        final boolean isMode = this.editMode == EditMode.MODE;
        if (event == ButtonEvent.LONG || event == ButtonEvent.DOWN && this.surface.isShiftPressed ())
        {
            if (isMode)
                this.surface.getModeManager ().setTemporary (Modes.REPEAT_NOTE);
            else
                this.surface.getViewManager ().setActive (Views.REPEAT_NOTE);
            this.surface.setTriggerConsumed (ButtonID.REPEAT);
            return true;
        }

        if (event != ButtonEvent.UP)
            return true;

        if (isMode)
        {
            final ModeManager modeManager = this.surface.getModeManager ();
            if (modeManager.isActive (Modes.REPEAT_NOTE))
            {
                modeManager.restore ();
                return true;
            }
        }
        else
        {
            final ViewManager viewManager = this.surface.getViewManager ();
            if (viewManager.isActive (Views.REPEAT_NOTE))
            {
                viewManager.restore ();
                return true;
            }
        }

        return false;
    }
}
