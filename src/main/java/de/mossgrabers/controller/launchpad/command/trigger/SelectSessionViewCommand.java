// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchpad.command.trigger;

import de.mossgrabers.controller.launchpad.LaunchpadConfiguration;
import de.mossgrabers.controller.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.controller.launchpad.view.SessionView;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.display.IDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.utils.ButtonEvent;
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
        final SessionView sessionView = (SessionView) viewManager.get (Views.SESSION);
        final Configuration configuration = this.surface.getConfiguration ();

        if (event == ButtonEvent.UP)
        {
            if (this.surface.isShiftPressed ())
            {
                viewManager.setActive (Views.MIX);
                this.notifyViewName (Views.MIX, false, false);
            }
            else if (viewManager.isActive (Views.SESSION))
            {
                if (sessionView.isBirdsEyeActive ())
                {
                    sessionView.setBirdsEyeActive (false);
                    this.notifyViewName (Views.SESSION, false, configuration.isFlipSession ());
                }
                else
                {
                    final boolean flipped = !configuration.isFlipSession ();
                    configuration.setFlipSession (flipped);
                    this.notifyViewName (Views.SESSION, false, flipped);
                }
            }
            else
            {
                viewManager.setActive (Views.SESSION);
                this.notifyViewName (Views.SESSION, false, configuration.isFlipSession ());
            }
        }
        else if (event == ButtonEvent.LONG)
        {
            this.surface.setTriggerConsumed (ButtonID.SESSION);
            sessionView.setBirdsEyeActive (true);
            this.notifyViewName (Views.SESSION, true, configuration.isFlipSession ());
        }

        this.surface.getPadGrid ().forceFlush ();
    }


    private void notifyViewName (final Views activeView, final boolean isBirdsEye, final boolean isFlipped)
    {
        final IDisplay display = this.surface.getDisplay ();

        if (activeView == Views.MIX)
        {
            display.notify ("Mix");
            return;
        }

        if (isBirdsEye)
            display.notify ("Session - Birds Eye");
        else
            display.notify (isFlipped ? "Session - Flipped" : "Session");
    }
}
