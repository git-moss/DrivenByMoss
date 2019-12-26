// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.command.trigger;

import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.ViewManager;
import de.mossgrabers.framework.view.Views;


/**
 * Command to select the session view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SelectSessionViewCommand extends AbstractTriggerCommand<PushControlSurface, PushConfiguration>
{
    private boolean isTemporary;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public SelectSessionViewCommand (final IModel model, final PushControlSurface surface)
    {
        super (model, surface);
    }


    /**
     * Activate temporary display of session view.
     */
    public void setTemporary ()
    {
        this.isTemporary = true;
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (event == ButtonEvent.DOWN)
        {
            this.isTemporary = false;

            final ViewManager viewManager = this.surface.getViewManager ();
            final ModeManager modeManager = this.surface.getModeManager ();
            if (Views.isSessionView (viewManager.getActiveViewId ()))
            {
                if (modeManager.isActiveOrTempMode (Modes.SESSION_VIEW_SELECT))
                    modeManager.restoreMode ();
                else
                    modeManager.setActiveMode (Modes.SESSION_VIEW_SELECT);
                return;
            }

            // Switch to the preferred session view and display scene/clip mode if enabled
            final PushConfiguration configuration = this.surface.getConfiguration ();
            viewManager.setActiveView (configuration.isScenesClipViewSelected () ? Views.SCENE_PLAY : Views.SESSION);
            if (configuration.shouldDisplayScenesOrClips ())
                modeManager.setActiveMode (Modes.SESSION);
            return;
        }

        if (event == ButtonEvent.UP && this.isTemporary)
            this.surface.getViewManager ().restoreView ();
    }
}
