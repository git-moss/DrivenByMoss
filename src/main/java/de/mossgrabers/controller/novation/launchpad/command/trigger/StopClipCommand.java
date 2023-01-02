// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchpad.command.trigger;

import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.Views;


/**
 * Track stop clip command.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class StopClipCommand extends AbstractTrackCommand
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public StopClipCommand (final IModel model, final LaunchpadControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (this.surface.isPro () && this.surface.isShiftPressed ())
        {
            if (event != ButtonEvent.DOWN)
                return;
            final ViewManager viewManager = this.surface.getViewManager ();
            if (viewManager.isActive (Views.SHIFT))
                viewManager.restore ();
            viewManager.setActive (Views.SHUFFLE);
            return;
        }

        this.onModeButton (event, Modes.STOP_CLIP, "Stop Clip");
    }
}
