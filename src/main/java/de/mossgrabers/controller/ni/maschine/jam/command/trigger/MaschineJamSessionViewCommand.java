// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.jam.command.trigger;

import de.mossgrabers.controller.ni.maschine.jam.MaschineJamConfiguration;
import de.mossgrabers.controller.ni.maschine.jam.controller.MaschineJamControlSurface;
import de.mossgrabers.controller.ni.maschine.jam.view.SessionView;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.display.IDisplay;
import de.mossgrabers.framework.daw.IApplication;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.Views;


/**
 * Command to select the session view.
 *
 * @author Jürgen Moßgraber
 */
public class MaschineJamSessionViewCommand extends AbstractTriggerCommand<MaschineJamControlSurface, MaschineJamConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public MaschineJamSessionViewCommand (final IModel model, final MaschineJamControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        final ViewManager viewManager = this.surface.getViewManager ();
        final SessionView sessionView = (SessionView) viewManager.get (Views.SESSION);
        final Configuration configuration = this.surface.getConfiguration ();

        if (event == ButtonEvent.UP)
        {
            // Toggle Arrange and Mix panel layout
            if (this.surface.isShiftPressed ())
            {
                final IApplication application = this.model.getApplication ();
                final boolean isArrange = IApplication.PANEL_LAYOUT_ARRANGE.equals (application.getPanelLayout ());
                application.setPanelLayout (isArrange ? IApplication.PANEL_LAYOUT_MIX : IApplication.PANEL_LAYOUT_ARRANGE);
                return;
            }

            if (viewManager.isActive (Views.SESSION))
            {
                // Disable birds-eye-view if active
                if (sessionView.isBirdsEyeActive ())
                {
                    sessionView.setBirdsEyeActive (false);
                    this.notifyViewName (false, configuration.isFlipSession ());
                    return;
                }

                // Flip clips
                final boolean flipped = !configuration.isFlipSession ();
                configuration.setFlipSession (flipped);
                this.notifyViewName (false, flipped);
                return;
            }

            // Activate session view
            viewManager.setActive (Views.SESSION);
            this.notifyViewName (false, configuration.isFlipSession ());
        }
        else if (event == ButtonEvent.LONG)
        {
            // Only trigger birds-eye-view if session view is already active
            this.surface.setTriggerConsumed (ButtonID.SESSION);
            sessionView.setBirdsEyeActive (true);
            this.notifyViewName (true, configuration.isFlipSession ());
        }
    }


    private void notifyViewName (final boolean isBirdsEye, final boolean isFlipped)
    {
        final IDisplay display = this.surface.getDisplay ();
        if (isBirdsEye)
            display.notify ("Session - Birds Eye");
        else
            display.notify (isFlipped ? "Session - Flipped" : "Session");
    }
}
