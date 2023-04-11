// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchpad.view;

import de.mossgrabers.controller.novation.launchpad.LaunchpadConfiguration;
import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadColorManager;
import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.view.sequencer.AbstractRaindropsView;


/**
 * The rain drops sequencer.
 *
 * @author Jürgen Moßgraber
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
    public int getButtonColor (final ButtonID buttonID)
    {
        if (!this.isActive ())
            return LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK;

        final int ordinal = buttonID.ordinal ();
        if (ordinal < ButtonID.SCENE1.ordinal () || ordinal > ButtonID.SCENE8.ordinal ())
            return 0;

        final int scene = buttonID.ordinal () - ButtonID.SCENE1.ordinal ();
        return scene == 7 - this.getResolutionIndex () ? LaunchpadColorManager.LAUNCHPAD_COLOR_YELLOW : LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN;
    }
}