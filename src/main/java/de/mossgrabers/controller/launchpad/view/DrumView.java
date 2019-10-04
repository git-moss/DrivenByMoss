// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchpad.view;

import de.mossgrabers.controller.launchpad.controller.LaunchpadColors;
import de.mossgrabers.controller.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.controller.launchpad.definition.LaunchpadProControllerDefinition;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;


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
    public DrumView (final LaunchpadControlSurface surface, final IModel model)
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
        this.surface.setTrigger (this.surface.getTriggerId (ButtonID.SESSION), LaunchpadColors.LAUNCHPAD_COLOR_GREY_LO);
        this.surface.setTrigger (this.surface.getTriggerId (ButtonID.NOTE), LaunchpadColors.LAUNCHPAD_COLOR_YELLOW);
        this.surface.setTrigger (this.surface.getTriggerId (ButtonID.DEVICE), LaunchpadColors.LAUNCHPAD_COLOR_GREY_LO);
        if (this.surface.isPro ())
            this.surface.setTrigger (LaunchpadProControllerDefinition.LAUNCHPAD_PRO_BUTTON_USER, this.model.getHost ().hasUserParameters () ? LaunchpadColors.LAUNCHPAD_COLOR_GREY_LO : LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
    }
}