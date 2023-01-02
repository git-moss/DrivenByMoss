// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchpad.command.trigger;

import de.mossgrabers.controller.novation.launchpad.LaunchpadConfiguration;
import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.Views;


/**
 * Base command for entering a track mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class AbstractTrackCommand extends AbstractTriggerCommand<LaunchpadControlSurface, LaunchpadConfiguration>
{
    private boolean firstRowUsed;
    private boolean wasSession;
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
        final ViewManager viewManager = this.surface.getViewManager ();
        switch (event)
        {
            case DOWN:
                this.firstRowUsed = false;
                if (modeManager.isActive (controlMode))
                {
                    modeManager.setActive (Modes.DUMMY);
                    return;
                }
                modeManager.setActive (controlMode);
                this.wasSession = viewManager.isActive (Views.SESSION);
                if (!this.wasSession)
                    viewManager.setActive (Views.SESSION);
                this.surface.getDisplay ().notify (notification);
                break;

            case LONG:
                this.firstRowUsed = true;
                break;

            case UP:
                if (this.firstRowUsed)
                {
                    modeManager.setActive (Modes.DUMMY);
                    if (!this.wasSession)
                        viewManager.restore ();
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
                if (viewManager.isActive (view))
                {
                    this.surface.getViewManager ().setActive (Views.SESSION);
                    return;
                }
                this.temporaryView = false;
                this.surface.getModeManager ().setActive (Modes.DUMMY);
                viewManager.setActive (view);
                this.surface.getDisplay ().notify (notification);
                break;

            case LONG:
                this.temporaryView = true;
                break;

            case UP:
                if (this.temporaryView)
                    viewManager.restore ();
                break;
        }
    }
}
