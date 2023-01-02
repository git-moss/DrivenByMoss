// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchpad.command.trigger;

import de.mossgrabers.controller.novation.launchpad.LaunchpadConfiguration;
import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.IBrowser;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.utils.ButtonEvent;
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
            if (viewManager.isActive (Views.SHIFT))
                viewManager.restore ();
            viewManager.setTemporary (Views.TEMPO);
            this.surface.getDisplay ().notify (viewManager.getActive ().getName ());
            return;
        }

        final IBrowser browser = this.model.getBrowser ();
        if (viewManager.isActive (Views.BROWSER))
        {
            browser.stopBrowsing (false);
            viewManager.setActive (Views.DEVICE);
            this.surface.getDisplay ().notify (viewManager.getActive ().getName ());
            return;
        }

        if (viewManager.isActive (Views.DEVICE))
        {
            final ICursorDevice cursorDevice = this.model.getCursorDevice ();
            if (this.surface.isShiftPressed () || !cursorDevice.doesExist ())
                browser.insertAfterCursorDevice ();
            else
                browser.replace (cursorDevice);
            return;
        }

        viewManager.setActive (Views.DEVICE);
        this.surface.getDisplay ().notify (viewManager.getActive ().getName ());
    }
}
