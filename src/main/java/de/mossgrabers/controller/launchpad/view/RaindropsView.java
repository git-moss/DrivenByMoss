// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchpad.view;

import de.mossgrabers.controller.launchpad.LaunchpadConfiguration;
import de.mossgrabers.controller.launchpad.controller.LaunchpadColors;
import de.mossgrabers.controller.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.view.AbstractRaindropsView;


/**
 * The rain drops sequencer.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class RaindropsView extends AbstractRaindropsView<LaunchpadControlSurface, LaunchpadConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public RaindropsView (final LaunchpadControlSurface surface, final IModel model)
    {
        super ("Raindrops", surface, model, true);
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
        this.surface.setTrigger (this.surface.getSessionButton (), LaunchpadColors.LAUNCHPAD_COLOR_GREY_LO);
        this.surface.setTrigger (this.surface.getNoteButton (), LaunchpadColors.LAUNCHPAD_COLOR_GREEN);
        this.surface.setTrigger (this.surface.getDeviceButton (), LaunchpadColors.LAUNCHPAD_COLOR_GREY_LO);
        if (this.surface.getConfiguration ().isPro ())
            this.surface.setTrigger (LaunchpadControlSurface.LAUNCHPAD_PRO_BUTTON_USER, this.model.getHost ().hasUserParameters () ? LaunchpadColors.LAUNCHPAD_COLOR_GREY_LO : LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
    }


    /** {@inheritDoc} */
    @Override
    public void updateSceneButtons ()
    {
        final boolean isActive = this.isActive ();
        for (int i = 0; i < 8; i++)
        {
            final int color = i == 7 - this.selectedResolutionIndex ? LaunchpadColors.LAUNCHPAD_COLOR_YELLOW : LaunchpadColors.LAUNCHPAD_COLOR_GREEN;
            this.surface.setTrigger (this.surface.getSceneTrigger (i), isActive ? color : LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
        }
    }
}