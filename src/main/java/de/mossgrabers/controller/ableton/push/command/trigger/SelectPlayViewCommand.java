// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.command.trigger;

import de.mossgrabers.controller.ableton.push.PushConfiguration;
import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.Views;


/**
 * Command to display a selection for the play modes.
 *
 * @author Jürgen Moßgraber
 */
public class SelectPlayViewCommand extends AbstractTriggerCommand<PushControlSurface, PushConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public SelectPlayViewCommand (final IModel model, final PushControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final ModeManager modeManager = this.surface.getModeManager ();
        final ViewManager viewManager = this.surface.getViewManager ();
        if (Views.isSessionView (viewManager.getActiveID ()))
        {
            this.surface.recallPreferredView (this.model.getCursorTrack ());

            if (modeManager.isActive (Modes.SESSION, Modes.MARKERS) || modeManager.isTemporary ())
                modeManager.setActive (this.surface.getConfiguration ().getMixerMode ());

            return;
        }

        if (modeManager.isActive (Modes.VIEW_SELECT))
            modeManager.restore ();
        else
            modeManager.setTemporary (Modes.VIEW_SELECT);
    }
}
