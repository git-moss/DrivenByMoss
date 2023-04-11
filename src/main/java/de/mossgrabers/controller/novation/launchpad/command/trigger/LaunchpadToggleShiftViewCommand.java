// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchpad.command.trigger;

import de.mossgrabers.controller.novation.launchpad.LaunchpadConfiguration;
import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.command.trigger.view.ToggleShiftViewCommand;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.Views;


/**
 * Command to show/hide the shift view. Additionally, returns to previous view.
 *
 * @author Jürgen Moßgraber
 */
public class LaunchpadToggleShiftViewCommand extends ToggleShiftViewCommand<LaunchpadControlSurface, LaunchpadConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public LaunchpadToggleShiftViewCommand (final IModel model, final LaunchpadControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        final ViewManager viewManager = this.surface.getViewManager ();
        if (event == ButtonEvent.DOWN && (viewManager.isActive (Views.TEMPO) || viewManager.isActive (Views.SHUFFLE)))
        {
            viewManager.restore ();
            this.surface.setTriggerConsumed (ButtonID.SHIFT);
            return;
        }

        super.execute (event, velocity);
    }
}
