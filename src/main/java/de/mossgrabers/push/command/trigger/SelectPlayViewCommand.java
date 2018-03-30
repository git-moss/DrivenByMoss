// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.command.trigger;

import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.IChannelBank;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.ViewManager;
import de.mossgrabers.push.PushConfiguration;
import de.mossgrabers.push.controller.PushControlSurface;
import de.mossgrabers.push.mode.Modes;
import de.mossgrabers.push.view.Views;


/**
 * Command to display a selection for the play modes.
 *
 * @author J&uuml;rgen Mo&szlig;graber
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
    public void execute (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final ViewManager viewManager = this.surface.getViewManager ();
        if (Views.isSessionView (viewManager.getActiveViewId ()))
        {
            final IChannelBank tb = this.model.getCurrentTrackBank ();
            final ITrack selectedTrack = tb.getSelectedTrack ();
            if (selectedTrack == null)
            {
                this.surface.getDisplay ().notify ("Please select a track first.");
                return;
            }

            final Integer preferredView = viewManager.getPreferredView (selectedTrack.getPosition ());
            viewManager.setActiveView (preferredView == null ? Views.VIEW_PLAY : preferredView);
            return;
        }

        final ModeManager modeManager = this.surface.getModeManager ();
        if (modeManager.isActiveMode (Modes.MODE_VIEW_SELECT))
            modeManager.restoreMode ();
        else
            modeManager.setActiveMode (Modes.MODE_VIEW_SELECT);
    }
}
