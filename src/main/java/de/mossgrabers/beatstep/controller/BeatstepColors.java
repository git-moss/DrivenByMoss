// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.beatstep.controller;

import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.PadGrid;
import de.mossgrabers.framework.scale.Scales;


/**
 * Different colors to use for the pads and buttons of Beatstep.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
@SuppressWarnings("javadoc")
public class BeatstepColors
{
    public static final int BEATSTEP_BUTTON_STATE_INVALID = -1;
    public static final int BEATSTEP_BUTTON_STATE_OFF     = 0;
    public static final int BEATSTEP_BUTTON_STATE_RED     = 1;
    public static final int BEATSTEP_BUTTON_STATE_BLUE    = 16;
    public static final int BEATSTEP_BUTTON_STATE_PINK    = 17;


    /**
     * Private due to utility class.
     */
    private BeatstepColors ()
    {
        // Intentionally empty
    }


    /**
     * Configures all colors for Beatstep controllers.
     *
     * @param colorManager The color manager
     */
    public static void addColors (final ColorManager colorManager)
    {
        colorManager.registerColor (Scales.SCALE_COLOR_OFF, BEATSTEP_BUTTON_STATE_OFF);
        colorManager.registerColor (Scales.SCALE_COLOR_OCTAVE, BEATSTEP_BUTTON_STATE_RED);
        colorManager.registerColor (Scales.SCALE_COLOR_NOTE, BEATSTEP_BUTTON_STATE_BLUE);
        colorManager.registerColor (Scales.SCALE_COLOR_OUT_OF_SCALE, BEATSTEP_BUTTON_STATE_OFF);

        colorManager.registerColor (PadGrid.GRID_OFF, BEATSTEP_BUTTON_STATE_OFF);
    }
}