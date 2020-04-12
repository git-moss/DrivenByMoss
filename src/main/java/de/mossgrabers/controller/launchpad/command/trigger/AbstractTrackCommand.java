// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchpad.command.trigger;

import de.mossgrabers.controller.launchpad.LaunchpadConfiguration;
import de.mossgrabers.controller.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.ViewManager;
import de.mossgrabers.framework.view.Views;


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
    public AbstractTrackCommand (final IModel model, final LaunchpadControlSurface surface)
    {
        super (model, surface);
    }


    protected void onModeButton (final ButtonEvent event, final Modes controlMode, final String notification)
    {
        final ModeManager modeManager = this.surface.getModeManager ();
        switch (event)
        {
            case DOWN:
                this.firstRowUsed = false;
                if (modeManager.isActiveOrTempMode (controlMode))
                {
                    modeManager.setActiveMode (Modes.DUMMY);
                    return;
                }
                modeManager.setActiveMode (controlMode);
                this.surface.getViewManager ().setActiveView (Views.SESSION);
                this.surface.getDisplay ().notify (notification);
                break;

            case LONG:
                this.firstRowUsed = true;
                break;

            case UP:
                if (this.firstRowUsed)
                {
                    modeManager.setActiveMode (Modes.DUMMY);
                    this.surface.getViewManager ().restoreView ();
                }
                break;
        }
    }


    protected void onFaderModeButton (final ButtonEvent event, final Views view, final String notification)
    {
        final ViewManager viewManager = this.surface.getViewManager ();
        switch (event)
        {
            case DOWN:
                if (viewManager.isActiveView (view))
                {
                    this.surface.getViewManager ().setActiveView (Views.SESSION);
                    return;
                }
                this.temporaryView = false;
                this.surface.getModeManager ().setActiveMode (Modes.DUMMY);
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
