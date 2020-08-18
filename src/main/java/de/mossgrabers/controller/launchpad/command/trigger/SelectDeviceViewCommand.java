// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchpad.command.trigger;

import de.mossgrabers.controller.launchpad.LaunchpadConfiguration;
import de.mossgrabers.controller.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.IBrowser;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.ViewManager;
import de.mossgrabers.framework.view.Views;


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
    public SelectDeviceViewCommand (final IModel model, final LaunchpadControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final ViewManager viewManager = this.surface.getViewManager ();

        if (this.surface.isPro () && this.surface.isShiftPressed ())
        {
            if (viewManager.isActiveView (Views.SHIFT))
                viewManager.restoreView ();
            viewManager.setActiveView (Views.TEMPO);
            this.surface.getDisplay ().notify (viewManager.getActiveView ().getName ());
            return;
        }

        final IBrowser browser = this.model.getBrowser ();
        if (viewManager.isActiveView (Views.BROWSER))
        {
            browser.stopBrowsing (false);
            viewManager.setActiveView (Views.DEVICE);
            this.surface.getDisplay ().notify (viewManager.getActiveView ().getName ());
            return;
        }

        if (viewManager.isActiveView (Views.DEVICE))
        {
            final ICursorDevice cursorDevice = this.model.getCursorDevice ();
            if (this.surface.isShiftPressed () || !cursorDevice.doesExist ())
                browser.insertAfterCursorDevice ();
            else
                browser.replace (cursorDevice);
            return;
        }

        viewManager.setActiveView (Views.DEVICE);
        this.surface.getDisplay ().notify (viewManager.getActiveView ().getName ());
    }
}
