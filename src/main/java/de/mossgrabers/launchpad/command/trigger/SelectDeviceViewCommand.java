// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.launchpad.command.trigger;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.view.ViewManager;
import de.mossgrabers.launchpad.LaunchpadConfiguration;
import de.mossgrabers.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.launchpad.view.Views;


/**
 * Command to select the session view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SelectDeviceViewCommand extends AbstractTriggerCommand<LaunchpadControlSurface, LaunchpadConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public SelectDeviceViewCommand (final Model model, final LaunchpadControlSurface surface)
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
        if (viewManager.isActiveView (Views.VIEW_BROWSER))
        {
            this.model.getBrowser ().stopBrowsing (false);
            viewManager.setActiveView (Views.VIEW_DEVICE);
            return;
        }

        if (viewManager.isActiveView (Views.VIEW_DEVICE))
        {
            if (this.surface.isShiftPressed ())
                this.model.getBrowser ().browseToInsertAfterDevice ();
            else
                this.model.getBrowser ().browseForPresets ();
            this.surface.scheduleTask (this::switchToBrowseView, 150);
            return;
        }

        viewManager.setActiveView (Views.VIEW_DEVICE);
    }


    private void switchToBrowseView ()
    {
        if (this.model.getBrowser ().isActive ())
            this.surface.getViewManager ().setActiveView (Views.VIEW_BROWSER);
    }
}
