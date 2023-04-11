// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.arturia.beatstep.controller;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.scale.Scales;


/**
 * Different colors to use for the pads and buttons of Beatstep.
 *
 * @author Jürgen Moßgraber
 */
@SuppressWarnings("javadoc")
public class BeatstepColorManager extends ColorManager
{
    public static final int BEATSTEP_BUTTON_STATE_INVALID = -1;
    public static final int BEATSTEP_BUTTON_STATE_OFF     = 0;
    public static final int BEATSTEP_BUTTON_STATE_RED     = 1;
    public static final int BEATSTEP_BUTTON_STATE_BLUE    = 16;
    public static final int BEATSTEP_BUTTON_STATE_PINK    = 17;


    /**
     * Constructor.
     */
    public BeatstepColorManager ()
    {
        this.registerColorIndex (Scales.SCALE_COLOR_OFF, BEATSTEP_BUTTON_STATE_OFF);
        this.registerColorIndex (Scales.SCALE_COLOR_OCTAVE, BEATSTEP_BUTTON_STATE_RED);
        this.registerColorIndex (Scales.SCALE_COLOR_NOTE, BEATSTEP_BUTTON_STATE_BLUE);
        this.registerColorIndex (Scales.SCALE_COLOR_OUT_OF_SCALE, BEATSTEP_BUTTON_STATE_OFF);

        this.registerColorIndex (IPadGrid.GRID_OFF, BEATSTEP_BUTTON_STATE_OFF);
        this.registerColorIndex (ColorManager.BUTTON_STATE_OFF, BEATSTEP_BUTTON_STATE_OFF);
        this.registerColorIndex (ColorManager.BUTTON_STATE_ON, BEATSTEP_BUTTON_STATE_OFF);
        this.registerColorIndex (ColorManager.BUTTON_STATE_HI, BEATSTEP_BUTTON_STATE_BLUE);

        this.registerColor (BEATSTEP_BUTTON_STATE_OFF, ColorEx.BLACK);
        this.registerColor (BEATSTEP_BUTTON_STATE_RED, ColorEx.RED);
        this.registerColor (BEATSTEP_BUTTON_STATE_BLUE, ColorEx.BLUE);
        this.registerColor (BEATSTEP_BUTTON_STATE_PINK, ColorEx.PINK);
    }
}