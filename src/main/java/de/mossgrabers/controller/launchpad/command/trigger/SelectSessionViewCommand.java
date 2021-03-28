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
    private TemporaryMode temporaryMode = TemporaryMode.OFF;


    private enum TemporaryMode
    {
        OFF,
        POSSIBLE,
        ACTIVE
    }


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


    /**
     * Activate temporary display of session view.
     */
    public void setTemporary ()
    {
        this.temporaryMode = TemporaryMode.ACTIVE;
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        final ViewManager viewManager = this.surface.getViewManager ();
        final SessionView sessionView = (SessionView) viewManager.get (Views.SESSION);
        final Configuration configuration = this.surface.getConfiguration ();

        switch (event)
        {
            case DOWN:
                this.temporaryMode = TemporaryMode.OFF;

                // Display Mix view with Shift
                if (this.surface.isShiftPressed ())
                {
                    viewManager.setActive (Views.MIX);
                    this.notifyViewName (Views.MIX, false, false);
                    return;
                }

                if (viewManager.isActive (Views.SESSION))
                {
                    // Disable birds-eye-view if active
                    if (sessionView.isBirdsEyeActive ())
                    {
                        sessionView.setBirdsEyeActive (false);
                        this.notifyViewName (Views.SESSION, false, configuration.isFlipSession ());
                        return;
                    }

                    // Flip clips
                    final boolean flipped = !configuration.isFlipSession ();
                    configuration.setFlipSession (flipped);
                    this.notifyViewName (Views.SESSION, false, flipped);
                    return;
                }

                // Activate session view
                this.temporaryMode = TemporaryMode.POSSIBLE;
                viewManager.setActive (Views.SESSION);
                this.notifyViewName (Views.SESSION, false, configuration.isFlipSession ());
                break;

            case LONG:
                // Only trigger birds-eye-view if session view is already active
                if (this.temporaryMode != TemporaryMode.POSSIBLE)
                {
                    this.surface.setTriggerConsumed (ButtonID.SESSION);
                    sessionView.setBirdsEyeActive (true);
                    this.notifyViewName (Views.SESSION, true, configuration.isFlipSession ());
                }
                break;

            case UP:
                if (this.temporaryMode == TemporaryMode.ACTIVE)
                    this.surface.getViewManager ().restore ();
                break;
        }
    }


    private void notifyViewName (final Views activeView, final boolean isBirdsEye, final boolean isFlipped)
    {
        // Launchpad sometimes do not update correctly, force a flush
        this.surface.getPadGrid ().forceFlush ();

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
