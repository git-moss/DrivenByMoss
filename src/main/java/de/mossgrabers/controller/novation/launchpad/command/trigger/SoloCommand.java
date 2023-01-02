// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchpad.command.trigger;

import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Track solo command.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SoloCommand extends AbstractTrackCommand
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public SoloCommand (final IModel model, final LaunchpadControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (this.surface.isPro () && this.surface.isShiftPressed ())
        {
            if (event == ButtonEvent.DOWN)
                this.model.getTransport ().toggleMetronome ();
            return;
        }

        this.onModeButton (event, Modes.SOLO, "Solo");
    }
}
