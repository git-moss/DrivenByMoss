// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchpad.view;

import de.mossgrabers.controller.novation.launchpad.LaunchpadConfiguration;
import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadColorManager;
import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.view.AbstractDrumView;


/**
 * The drum sequencer.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DrumView extends AbstractDrumView<LaunchpadControlSurface, LaunchpadConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public DrumView (final LaunchpadControlSurface surface, final IModel model)
    {
        super ("Drum", surface, model, 4, 4, true);

        this.buttonSelect = ButtonID.PAD5;
        this.buttonMute = ButtonID.PAD6;
        this.buttonSolo = ButtonID.PAD7;
        this.buttonBrowse = ButtonID.PAD8;
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        final int ordinal = buttonID.ordinal ();
        if (ordinal < ButtonID.SCENE1.ordinal () || ordinal > ButtonID.SCENE8.ordinal ())
            return 0;

        final int scene = ordinal - ButtonID.SCENE1.ordinal ();

        if (!this.isActive ())
            return LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK;
        return scene == 7 - this.selectedResolutionIndex ? LaunchpadColorManager.LAUNCHPAD_COLOR_YELLOW : LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN;
    }


    /** {@inheritDoc} */
    @Override
    protected void handleLoopArea (final int pad, final int velocity)
    {
        if (pad < 12)
            super.handleLoopArea (pad, velocity);
    }


    /** {@inheritDoc} */
    @Override
    protected void drawPages (final INoteClip clip, final boolean isActive)
    {
        super.drawPages (clip, isActive);

        // Draw the last 4 buttons
        final IPadGrid padGrid = this.surface.getPadGrid ();

        padGrid.lightEx (4, 7, this.isSelectTrigger () ? LaunchpadColorManager.LAUNCHPAD_COLOR_WHITE : LaunchpadColorManager.LAUNCHPAD_COLOR_GREY_LO);
        padGrid.lightEx (5, 7, this.isMuteTrigger () ? LaunchpadColorManager.LAUNCHPAD_COLOR_YELLOW_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_YELLOW_LO);
        padGrid.lightEx (6, 7, this.isSoloTrigger () ? LaunchpadColorManager.LAUNCHPAD_COLOR_BLUE_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_BLUE_LO);
        padGrid.lightEx (7, 7, this.isBrowseTrigger () ? LaunchpadColorManager.LAUNCHPAD_COLOR_CYAN_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_CYAN_LO);
    }


    /** {@inheritDoc} */
    @Override
    protected int getNumberOfAvailablePages ()
    {
        // Remove the last 4 buttons so we can use it for something else
        return super.getNumberOfAvailablePages () - 4;
    }
}