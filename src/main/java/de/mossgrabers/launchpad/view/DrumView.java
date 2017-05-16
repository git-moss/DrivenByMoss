// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.launchpad.view;

import de.mossgrabers.framework.Model;
import de.mossgrabers.launchpad.controller.LaunchpadColors;
import de.mossgrabers.launchpad.controller.LaunchpadControlSurface;


/**
 * The drum sequencer.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DrumView extends DrumViewBase
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public DrumView (final LaunchpadControlSurface surface, final Model model)
    {
        super ("Drum", surface, model, 4, 4);
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        super.onActivate ();
        this.surface.setLaunchpadToPrgMode ();
        this.surface.scheduleTask (this::delayedUpdateArrowButtons, 150);
    }


    private void delayedUpdateArrowButtons ()
    {
        this.surface.setButton (this.surface.getSessionButton (), LaunchpadColors.LAUNCHPAD_COLOR_GREY_LO);
        this.surface.setButton (this.surface.getNoteButton (), LaunchpadColors.LAUNCHPAD_COLOR_YELLOW);
        this.surface.setButton (this.surface.getDeviceButton (), LaunchpadColors.LAUNCHPAD_COLOR_GREY_LO);
        this.surface.setButton (this.surface.getUserButton (), LaunchpadColors.LAUNCHPAD_COLOR_GREY_LO);
    }
}