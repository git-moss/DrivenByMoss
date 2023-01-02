// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.acvs.controller;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.DAWColor;


/**
 * Different colors to use for the pads and buttons of Akai MPC.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ACVSColorManager extends ColorManager
{
    /** ID for color when Undo button is turned on. */
    public static final String BUTTON_UNDO_STATE_ON  = "BUTTON_UNDO_STATE_ON";
    /** ID for color when Undo button is highlighted. */
    public static final String BUTTON_UNDO_STATE_HI  = "BUTTON_UNDO_STATE_HI";

    /** ID for color when Undo button is turned on. */
    public static final String BUTTON_ARROW_STATE_ON = "BUTTON_ARROW_STATE_ON";
    /** ID for color when Undo button is highlighted. */
    public static final String BUTTON_ARROW_STATE_HI = "BUTTON_ARROW_STATE_HI";

    /** ID for the color black. */
    public static final int    COLOR_BLACK           = 0;
    /** ID for the color dark gray. */
    public static final int    COLOR_DARK_GRAY       = 77;
    /** ID for the color gray. */
    public static final int    COLOR_GRAY            = 35;
    /** ID for the color half gray. */
    public static final int    COLOR_GRAY_HALF       = 47;
    /** ID for the color light gray. */
    public static final int    COLOR_LIGHT_GRAY      = 21;
    /** ID for the color silver. */
    public static final int    COLOR_SILVER          = 33;
    /** ID for the color dark brown. */
    public static final int    COLOR_DARK_BROWN      = 1;
    /** ID for the color brown. */
    public static final int    COLOR_BROWN           = 24;
    /** ID for the color dark blue. */
    public static final int    COLOR_DARK_BLUE       = 71;
    /** ID for the color purple blue. */
    public static final int    COLOR_PURPLE_BLUE     = 32;
    /** ID for the color purple. */
    public static final int    COLOR_PURPLE          = 75;
    /** ID for the color pink. */
    public static final int    COLOR_PINK            = 76;
    /** ID for the color red. */
    public static final int    COLOR_RED             = 3;
    /** ID for the color orange. */
    public static final int    COLOR_ORANGE          = 23;
    /** ID for the color light orange. */
    public static final int    COLOR_LIGHT_ORANGE    = 5;
    /** ID for the color moss green. */
    public static final int    COLOR_MOSS_GREEN      = 27;
    /** ID for the color green. */
    public static final int    COLOR_GREEN           = 69;
    /** ID for the color cold green. */
    public static final int    COLOR_COLD_GREEN      = 28;
    /** ID for the color blue. */
    public static final int    COLOR_BLUE            = 17;
    /** ID for the color light purple. */
    public static final int    COLOR_LIGHT_PURPLE    = 19;
    /** ID for the color light pink. */
    public static final int    COLOR_LIGHT_PINK      = 20;
    /** ID for the color rose. */
    public static final int    COLOR_ROSE            = 22;
    /** ID for the color reddish brown. */
    public static final int    COLOR_REDDISH_BROWN   = 36;
    /** ID for the color light brown. */
    public static final int    COLOR_LIGHT_BROWN     = 9;
    /** ID for the color light green. */
    public static final int    COLOR_LIGHT_GREEN     = 4;
    /** ID for the color bluish green. */
    public static final int    COLOR_BLUISH_GREEN    = 6;
    /** ID for the color green blue. */
    public static final int    COLOR_GREEN_BLUE      = 14;
    /** ID for the color light blue. */
    public static final int    COLOR_LIGHT_BLUE      = 15;


    /**
     * Private due to utility class.
     */
    public ACVSColorManager ()
    {
        this.registerColorIndex (IPadGrid.GRID_OFF, 0);

        this.registerColorIndex (ColorManager.BUTTON_STATE_OFF, 0);
        this.registerColorIndex (ColorManager.BUTTON_STATE_ON, 0);
        this.registerColorIndex (ColorManager.BUTTON_STATE_HI, 127);

        this.registerColorIndex (BUTTON_UNDO_STATE_ON, 2);
        this.registerColorIndex (BUTTON_UNDO_STATE_HI, 1);

        this.registerColorIndex (BUTTON_ARROW_STATE_ON, 3);
        this.registerColorIndex (BUTTON_ARROW_STATE_HI, 4);

        this.registerColor (COLOR_BLACK, ColorEx.BLACK);
        this.registerColor (COLOR_GRAY_HALF, ColorEx.LIGHT_GRAY);
        this.registerColor (COLOR_DARK_GRAY, ColorEx.DARK_GRAY);
        this.registerColor (COLOR_GRAY, ColorEx.GRAY);
        this.registerColor (COLOR_LIGHT_GRAY, ColorEx.LIGHT_GRAY);
        this.registerColor (COLOR_SILVER, ColorEx.LIGHT_GRAY);
        this.registerColor (COLOR_DARK_BROWN, ColorEx.DARK_BROWN);
        this.registerColor (COLOR_BROWN, ColorEx.BROWN);
        this.registerColor (COLOR_DARK_BLUE, ColorEx.DARK_BLUE);
        this.registerColor (COLOR_PURPLE_BLUE, ColorEx.BLUE);
        this.registerColor (COLOR_PURPLE, ColorEx.PURPLE);
        this.registerColor (COLOR_PINK, ColorEx.PINK);
        this.registerColor (COLOR_RED, ColorEx.RED);
        this.registerColor (COLOR_ORANGE, ColorEx.ORANGE);
        this.registerColor (COLOR_LIGHT_ORANGE, ColorEx.ORANGE);
        this.registerColor (COLOR_MOSS_GREEN, ColorEx.GREEN);
        this.registerColor (COLOR_GREEN, ColorEx.GREEN);
        this.registerColor (COLOR_COLD_GREEN, ColorEx.GREEN);
        this.registerColor (COLOR_BLUE, ColorEx.BLUE);
        this.registerColor (COLOR_LIGHT_PURPLE, ColorEx.PURPLE);
        this.registerColor (COLOR_LIGHT_PINK, ColorEx.PINK);
        this.registerColor (COLOR_ROSE, ColorEx.PINK);
        this.registerColor (COLOR_REDDISH_BROWN, ColorEx.BROWN);
        this.registerColor (COLOR_LIGHT_BROWN, ColorEx.BROWN);
        this.registerColor (COLOR_LIGHT_GREEN, ColorEx.GREEN);
        this.registerColor (COLOR_BLUISH_GREEN, ColorEx.GREEN);
        this.registerColor (COLOR_GREEN_BLUE, ColorEx.BLUE);
        this.registerColor (COLOR_LIGHT_BLUE, ColorEx.BLUE);

        this.registerColor (2, ColorEx.RED);
        this.registerColor (127, ColorEx.BLACK);

        this.registerColorIndex (DAWColor.COLOR_OFF, COLOR_BLACK);
        this.registerColorIndex (DAWColor.DAW_COLOR_GRAY_HALF, COLOR_GRAY_HALF);
        this.registerColorIndex (DAWColor.DAW_COLOR_DARK_GRAY, COLOR_DARK_GRAY);
        this.registerColorIndex (DAWColor.DAW_COLOR_GRAY, COLOR_GRAY);
        this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_GRAY, COLOR_LIGHT_GRAY);
        this.registerColorIndex (DAWColor.DAW_COLOR_SILVER, COLOR_SILVER);
        this.registerColorIndex (DAWColor.DAW_COLOR_DARK_BROWN, COLOR_DARK_BROWN);
        this.registerColorIndex (DAWColor.DAW_COLOR_BROWN, COLOR_BROWN);
        this.registerColorIndex (DAWColor.DAW_COLOR_DARK_BLUE, COLOR_DARK_BLUE);
        this.registerColorIndex (DAWColor.DAW_COLOR_PURPLE_BLUE, COLOR_PURPLE_BLUE);
        this.registerColorIndex (DAWColor.DAW_COLOR_PURPLE, COLOR_PURPLE);
        this.registerColorIndex (DAWColor.DAW_COLOR_PINK, COLOR_PINK);
        this.registerColorIndex (DAWColor.DAW_COLOR_RED, COLOR_RED);
        this.registerColorIndex (DAWColor.DAW_COLOR_ORANGE, COLOR_ORANGE);
        this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_ORANGE, COLOR_LIGHT_ORANGE);
        this.registerColorIndex (DAWColor.DAW_COLOR_MOSS_GREEN, COLOR_MOSS_GREEN);
        this.registerColorIndex (DAWColor.DAW_COLOR_GREEN, COLOR_GREEN);
        this.registerColorIndex (DAWColor.DAW_COLOR_COLD_GREEN, COLOR_COLD_GREEN);
        this.registerColorIndex (DAWColor.DAW_COLOR_BLUE, COLOR_BLUE);
        this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_PURPLE, COLOR_LIGHT_PURPLE);
        this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_PINK, COLOR_LIGHT_PINK);
        this.registerColorIndex (DAWColor.DAW_COLOR_ROSE, COLOR_ROSE);
        this.registerColorIndex (DAWColor.DAW_COLOR_REDDISH_BROWN, COLOR_REDDISH_BROWN);
        this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_BROWN, COLOR_LIGHT_BROWN);
        this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_GREEN, COLOR_LIGHT_GREEN);
        this.registerColorIndex (DAWColor.DAW_COLOR_BLUISH_GREEN, COLOR_BLUISH_GREEN);
        this.registerColorIndex (DAWColor.DAW_COLOR_GREEN_BLUE, COLOR_GREEN_BLUE);
        this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_BLUE, COLOR_LIGHT_BLUE);
    }
}