// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchpad.command.trigger;

import de.mossgrabers.controller.launchpad.LaunchpadConfiguration;
import de.mossgrabers.controller.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.controller.launchpad.view.SessionView;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.ViewManager;
import de.mossgrabers.framework.view.Views;


/**
 * Command to select the session view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SelectSessionViewCommand extends AbstractTriggerCommand<LaunchpadControlSurface, LaunchpadConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public SelectSessionViewCommand (final IModel model, final LaunchpadControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (event == ButtonEvent.DOWN)
            return;

        final ViewManager viewManager = this.surface.getViewManager ();
        final SessionView sessionView = (SessionView) viewManager.getView (Views.SESSION);

        if (event == ButtonEvent.UP)
        {
            if (viewManager.isActiveView (Views.SESSION))
            {
                if (sessionView.isBirdsEyeActive ())
                    sessionView.setBirdsEyeActive (false);
                else
                {
                    final Configuration configuration = this.surface.getConfiguration ();
                    configuration.setFlipSession (!configuration.isFlipSession ());
                }
            }
            else
                viewManager.setActiveView (Views.SESSION);
        }
        else if (event == ButtonEvent.LONG)
        {
            this.surface.setTriggerConsumed (ButtonID.SESSION);
            sessionView.setBirdsEyeActive (true);
        }

        this.surface.getPadGrid ().forceFlush ();
    }
}
