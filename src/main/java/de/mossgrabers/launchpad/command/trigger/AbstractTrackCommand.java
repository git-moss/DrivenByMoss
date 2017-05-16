// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.launchpad.command.trigger;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.view.ViewManager;
import de.mossgrabers.launchpad.LaunchpadConfiguration;
import de.mossgrabers.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.launchpad.view.Views;


/**
 * Base command for entering a track mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class AbstractTrackCommand extends AbstractTriggerCommand<LaunchpadControlSurface, LaunchpadConfiguration>
{
    private boolean firstRowUsed;
    private boolean temporaryView;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public AbstractTrackCommand (final Model model, final LaunchpadControlSurface surface)
    {
        super (model, surface);
    }


    protected void onModeButton (final ButtonEvent event, final Integer controlMode, final String notification)
    {
        final ModeManager modeManager = this.surface.getModeManager ();
        switch (event)
        {
            case DOWN:
                this.firstRowUsed = false;
                if (modeManager.isActiveMode (controlMode))
                {
                    modeManager.setActiveMode (null);
                    return;
                }
                modeManager.setActiveMode (controlMode);
                this.surface.getViewManager ().setActiveView (Views.VIEW_SESSION);
                this.surface.getDisplay ().notify (notification);
                break;

            case LONG:
                this.firstRowUsed = true;
                break;

            case UP:
                if (this.firstRowUsed)
                    modeManager.setActiveMode (null);
                break;
        }
    }


    protected void onFaderModeButton (final ButtonEvent event, final Integer view, final String notification)
    {
        final ViewManager viewManager = this.surface.getViewManager ();
        switch (event)
        {
            case DOWN:
                if (viewManager.isActiveView (view))
                {
                    viewManager.restoreView ();
                    return;
                }
                this.temporaryView = false;
                this.surface.getModeManager ().setActiveMode (null);
                viewManager.setActiveView (view);
                this.surface.getDisplay ().notify (notification);
                break;

            case LONG:
                this.temporaryView = true;
                break;

            case UP:
                if (this.temporaryView)
                    viewManager.restoreView ();
                break;
        }
    }
}
