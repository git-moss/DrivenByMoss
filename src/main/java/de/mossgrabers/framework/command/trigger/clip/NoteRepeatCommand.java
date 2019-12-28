// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger.clip;

import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.ViewManager;
import de.mossgrabers.framework.view.Views;


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
    private final boolean isMode;


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

        this.isMode = isMode;
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
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
        if (event == ButtonEvent.LONG || event == ButtonEvent.DOWN && this.surface.isShiftPressed ())
        {
            if (this.isMode)
                this.surface.getModeManager ().setActiveMode (Modes.REPEAT_NOTE);
            else
                this.surface.getViewManager ().setActiveView (Views.REPEAT_NOTE);
            this.surface.setTriggerConsumed (ButtonID.REPEAT);
            return true;
        }

        if (event != ButtonEvent.UP)
            return true;

        if (this.isMode)
        {
            final ModeManager modeManager = this.surface.getModeManager ();
            if (modeManager.isActiveOrTempMode (Modes.REPEAT_NOTE))
            {
                modeManager.restoreMode ();
                return true;
            }
        }
        else
        {
            final ViewManager viewManager = this.surface.getViewManager ();
            if (viewManager.isActiveView (Views.REPEAT_NOTE))
            {
                viewManager.restoreView ();
                return true;
            }
        }

        return false;
    }
}
