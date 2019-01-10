// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.apc.controller;

import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.PadGrid;
import de.mossgrabers.framework.daw.DAWColors;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.view.AbstractDrumView;
import de.mossgrabers.framework.view.AbstractPlayView;
import de.mossgrabers.framework.view.AbstractSequencerView;


/**
 * Different colors to use for the pads and buttons of APC40 mkI and mkII.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
@SuppressWarnings("javadoc")
public class APCColors
{
    // APC Colors for 5x8 clip matrix

    /** off. */
    public static final int    APC_COLOR_BLACK                 = 0;
    /** green. */
    public static final int    APC_COLOR_GREEN                 = 1;
    /** green blink. */
    public static final int    APC_COLOR_GREEN_BLINK           = 2;
    /** red. */
    public static final int    APC_COLOR_RED                   = 3;
    /** red blink. */
    public static final int    APC_COLOR_RED_BLINK             = 4;
    /** yellow. */
    public static final int    APC_COLOR_YELLOW                = 5;
    /** yellow blink. */
    public static final int    APC_COLOR_YELLOW_BLINK          = 6;

    // APC40mkII Colors for 5x8 clip matrix
    public static final int    APC_MKII_COLOR_BLACK            = 0;
    public static final int    APC_MKII_COLOR_GREY_LO          = 1;
    public static final int    APC_MKII_COLOR_GREY_MD          = 103;
    public static final int    APC_MKII_COLOR_GREY_LT          = 2;
    public static final int    APC_MKII_COLOR_WHITE            = 3;
    public static final int    APC_MKII_COLOR_ROSE             = 4;
    public static final int    APC_MKII_COLOR_RED_HI           = 5;
    public static final int    APC_MKII_COLOR_RED              = 6;
    public static final int    APC_MKII_COLOR_RED_LO           = 7;
    public static final int    APC_MKII_COLOR_RED_AMBER        = 8;
    public static final int    APC_MKII_COLOR_AMBER_HI         = 9;
    public static final int    APC_MKII_COLOR_AMBER            = 10;
    public static final int    APC_MKII_COLOR_AMBER_LO         = 11;
    public static final int    APC_MKII_COLOR_AMBER_YELLOW     = 12;
    public static final int    APC_MKII_COLOR_YELLOW_HI        = 13;
    public static final int    APC_MKII_COLOR_YELLOW           = 14;
    public static final int    APC_MKII_COLOR_YELLOW_LO        = 15;
    public static final int    APC_MKII_COLOR_YELLOW_LIME      = 16;
    public static final int    APC_MKII_COLOR_LIME_HI          = 17;
    public static final int    APC_MKII_COLOR_LIME             = 18;
    public static final int    APC_MKII_COLOR_LIME_LO          = 19;
    public static final int    APC_MKII_COLOR_LIME_GREEN       = 20;
    public static final int    APC_MKII_COLOR_GREEN_HI         = 21;
    public static final int    APC_MKII_COLOR_GREEN            = 22;
    public static final int    APC_MKII_COLOR_GREEN_LO         = 23;
    public static final int    APC_MKII_COLOR_GREEN_SPRING     = 24;
    public static final int    APC_MKII_COLOR_SPRING_HI        = 25;
    public static final int    APC_MKII_COLOR_SPRING           = 26;
    public static final int    APC_MKII_COLOR_SPRING_LO        = 27;
    public static final int    APC_MKII_COLOR_SPRING_TURQUOISE = 28;
    public static final int    APC_MKII_COLOR_TURQUOISE_LO     = 29;
    public static final int    APC_MKII_COLOR_TURQUOISE        = 30;
    public static final int    APC_MKII_COLOR_TURQUOISE_HI     = 31;
    public static final int    APC_MKII_COLOR_TURQUOISE_CYAN   = 32;
    public static final int    APC_MKII_COLOR_CYAN_HI          = 33;
    public static final int    APC_MKII_COLOR_CYAN             = 34;
    public static final int    APC_MKII_COLOR_CYAN_LO          = 35;
    public static final int    APC_MKII_COLOR_CYAN_SKY         = 36;
    public static final int    APC_MKII_COLOR_SKY_HI           = 37;
    public static final int    APC_MKII_COLOR_SKY              = 38;
    public static final int    APC_MKII_COLOR_SKY_LO           = 39;
    public static final int    APC_MKII_COLOR_SKY_OCEAN        = 40;
    public static final int    APC_MKII_COLOR_OCEAN_HI         = 41;
    public static final int    APC_MKII_COLOR_OCEAN            = 42;
    public static final int    APC_MKII_COLOR_OCEAN_LO         = 43;
    public static final int    APC_MKII_COLOR_OCEAN_BLUE       = 44;
    public static final int    APC_MKII_COLOR_BLUE_HI          = 45;
    public static final int    APC_MKII_COLOR_BLUE             = 46;
    public static final int    APC_MKII_COLOR_BLUE_LO          = 47;
    public static final int    APC_MKII_COLOR_BLUE_ORCHID      = 48;
    public static final int    APC_MKII_COLOR_ORCHID_HI        = 49;
    public static final int    APC_MKII_COLOR_ORCHID           = 50;
    public static final int    APC_MKII_COLOR_ORCHID_LO        = 51;
    public static final int    APC_MKII_COLOR_ORCHID_MAGENTA   = 52;
    public static final int    APC_MKII_COLOR_MAGENTA_HI       = 53;
    public static final int    APC_MKII_COLOR_MAGENTA          = 54;
    public static final int    APC_MKII_COLOR_MAGENTA_LO       = 55;
    public static final int    APC_MKII_COLOR_MAGENTA_PINK     = 56;
    public static final int    APC_MKII_COLOR_PINK_HI          = 57;
    public static final int    APC_MKII_COLOR_PINK             = 58;
    public static final int    APC_MKII_COLOR_PINK_LO          = 59;

    public static final String COLOR_VIEW_SELECTED             = "COLOR_VIEW_SELECTED";
    public static final String COLOR_VIEW_UNSELECTED           = "COLOR_VIEW_UNSELECTED";
    public static final String COLOR_VIEW_OFF                  = "COLOR_VIEW_OFF";
    public static final String COLOR_KEY_WHITE                 = "COLOR_KEY_WHITE";
    public static final String COLOR_KEY_BLACK                 = "COLOR_KEY_BLACK";
    public static final String COLOR_KEY_SELECTED              = "COLOR_KEY_SELECTED";
    public static final String BUTTON_STATE_BLINK              = "BUTTON_STATE_BLINK";


    /**
     * Private due to utility class.
     */
    private APCColors ()
    {
        // Intentionally empty
    }


    /**
     * Configures all colors for APC controllers.
     *
     * @param colorManager The color manager
     * @param isMkII True if mkII
     */
    public static void addColors (final ColorManager colorManager, final boolean isMkII)
    {
        colorManager.registerColor (Scales.SCALE_COLOR_OFF, isMkII ? APC_MKII_COLOR_BLACK : APC_COLOR_BLACK);
        colorManager.registerColor (Scales.SCALE_COLOR_OCTAVE, isMkII ? APC_MKII_COLOR_OCEAN_HI : APC_COLOR_YELLOW);
        colorManager.registerColor (Scales.SCALE_COLOR_NOTE, isMkII ? APC_MKII_COLOR_WHITE : APC_COLOR_BLACK);
        colorManager.registerColor (Scales.SCALE_COLOR_OUT_OF_SCALE, isMkII ? APC_MKII_COLOR_BLACK : APC_COLOR_BLACK);

        colorManager.registerColor (AbstractSequencerView.COLOR_STEP_HILITE_NO_CONTENT, isMkII ? APC_MKII_COLOR_GREEN_HI : APC_COLOR_GREEN);
        colorManager.registerColor (AbstractSequencerView.COLOR_STEP_HILITE_CONTENT, isMkII ? APC_MKII_COLOR_GREEN_LO : APC_COLOR_GREEN);
        colorManager.registerColor (AbstractSequencerView.COLOR_NO_CONTENT, isMkII ? APC_MKII_COLOR_BLACK : APC_COLOR_BLACK);
        colorManager.registerColor (AbstractSequencerView.COLOR_CONTENT, isMkII ? APC_MKII_COLOR_BLUE_HI : APC_COLOR_RED);
        colorManager.registerColor (AbstractSequencerView.COLOR_CONTENT_CONT, isMkII ? APC_MKII_COLOR_BLUE_LO : APC_COLOR_RED);
        colorManager.registerColor (AbstractSequencerView.COLOR_PAGE, isMkII ? APC_MKII_COLOR_WHITE : APC_COLOR_GREEN);
        colorManager.registerColor (AbstractSequencerView.COLOR_ACTIVE_PAGE, isMkII ? APC_MKII_COLOR_GREEN_HI : APC_COLOR_YELLOW);
        colorManager.registerColor (AbstractSequencerView.COLOR_SELECTED_PAGE, isMkII ? APC_MKII_COLOR_BLUE_LO : APC_COLOR_RED);

        colorManager.registerColor (AbstractDrumView.COLOR_PAD_OFF, isMkII ? APC_MKII_COLOR_BLACK : APC_COLOR_BLACK);
        colorManager.registerColor (AbstractDrumView.COLOR_PAD_RECORD, isMkII ? APC_MKII_COLOR_RED_HI : APC_COLOR_RED);
        colorManager.registerColor (AbstractDrumView.COLOR_PAD_PLAY, isMkII ? APC_MKII_COLOR_GREEN_HI : APC_COLOR_GREEN);
        colorManager.registerColor (AbstractDrumView.COLOR_PAD_SELECTED, isMkII ? APC_MKII_COLOR_BLUE_HI : APC_COLOR_YELLOW_BLINK);
        colorManager.registerColor (AbstractDrumView.COLOR_PAD_MUTED, isMkII ? APC_MKII_COLOR_AMBER_LO : APC_COLOR_BLACK);
        colorManager.registerColor (AbstractDrumView.COLOR_PAD_HAS_CONTENT, isMkII ? APC_MKII_COLOR_YELLOW_HI : APC_COLOR_YELLOW);
        colorManager.registerColor (AbstractDrumView.COLOR_PAD_NO_CONTENT, isMkII ? APC_MKII_COLOR_YELLOW_LO : APC_COLOR_BLACK);

        colorManager.registerColor (AbstractPlayView.COLOR_PLAY, isMkII ? APC_MKII_COLOR_GREEN : APC_COLOR_GREEN);
        colorManager.registerColor (AbstractPlayView.COLOR_RECORD, isMkII ? APC_MKII_COLOR_RED : APC_COLOR_RED);
        colorManager.registerColor (AbstractPlayView.COLOR_OFF, isMkII ? APC_MKII_COLOR_BLACK : APC_COLOR_BLACK);

        colorManager.registerColor (PadGrid.GRID_OFF, isMkII ? APC_MKII_COLOR_BLACK : APC_COLOR_BLACK);

        colorManager.registerColor (DAWColors.COLOR_OFF, isMkII ? APC_MKII_COLOR_BLACK : APC_COLOR_BLACK);

        if (isMkII)
        {
            colorManager.registerColor (DAWColors.DAW_COLOR_DARK_GRAY, APC_MKII_COLOR_GREY_LO);
            colorManager.registerColor (DAWColors.DAW_COLOR_GRAY, APC_MKII_COLOR_GREY_MD);
            colorManager.registerColor (DAWColors.DAW_COLOR_LIGHT_GRAY, APC_MKII_COLOR_GREY_LO);
            colorManager.registerColor (DAWColors.DAW_COLOR_SILVER, APC_MKII_COLOR_SKY_OCEAN);
            colorManager.registerColor (DAWColors.DAW_COLOR_DARK_BROWN, APC_MKII_COLOR_AMBER_LO);
            colorManager.registerColor (DAWColors.DAW_COLOR_BROWN, APC_MKII_COLOR_AMBER_YELLOW);
            colorManager.registerColor (DAWColors.DAW_COLOR_DARK_BLUE, APC_MKII_COLOR_OCEAN);
            colorManager.registerColor (DAWColors.DAW_COLOR_PURPLE_BLUE, APC_MKII_COLOR_OCEAN_BLUE);
            colorManager.registerColor (DAWColors.DAW_COLOR_PURPLE, APC_MKII_COLOR_PINK);
            colorManager.registerColor (DAWColors.DAW_COLOR_PINK, APC_MKII_COLOR_PINK_HI);
            colorManager.registerColor (DAWColors.DAW_COLOR_RED, APC_MKII_COLOR_RED);
            colorManager.registerColor (DAWColors.DAW_COLOR_ORANGE, APC_MKII_COLOR_AMBER);
            colorManager.registerColor (DAWColors.DAW_COLOR_LIGHT_ORANGE, APC_MKII_COLOR_RED_LO);
            colorManager.registerColor (DAWColors.DAW_COLOR_MOSS_GREEN, APC_MKII_COLOR_LIME_LO);
            colorManager.registerColor (DAWColors.DAW_COLOR_GREEN, APC_MKII_COLOR_SPRING);
            colorManager.registerColor (DAWColors.DAW_COLOR_COLD_GREEN, APC_MKII_COLOR_TURQUOISE);
            colorManager.registerColor (DAWColors.DAW_COLOR_BLUE, APC_MKII_COLOR_SKY_HI);
            colorManager.registerColor (DAWColors.DAW_COLOR_LIGHT_PURPLE, APC_MKII_COLOR_BLUE_ORCHID);
            colorManager.registerColor (DAWColors.DAW_COLOR_LIGHT_PINK, APC_MKII_COLOR_MAGENTA_PINK);
            colorManager.registerColor (DAWColors.DAW_COLOR_SKIN, APC_MKII_COLOR_ROSE);
            colorManager.registerColor (DAWColors.DAW_COLOR_REDDISH_BROWN, APC_MKII_COLOR_AMBER);
            colorManager.registerColor (DAWColors.DAW_COLOR_LIGHT_BROWN, APC_MKII_COLOR_AMBER_HI);
            colorManager.registerColor (DAWColors.DAW_COLOR_LIGHT_GREEN, APC_MKII_COLOR_LIME);
            colorManager.registerColor (DAWColors.DAW_COLOR_BLUISH_GREEN, APC_MKII_COLOR_SPRING_HI);
            colorManager.registerColor (DAWColors.DAW_COLOR_GREEN_BLUE, APC_MKII_COLOR_TURQUOISE_CYAN);
            colorManager.registerColor (DAWColors.DAW_COLOR_LIGHT_BLUE, APC_MKII_COLOR_OCEAN_HI);
        }

        colorManager.registerColor (ColorManager.BUTTON_STATE_OFF, isMkII ? APC_MKII_COLOR_BLACK : APC_COLOR_BLACK);
        colorManager.registerColor (ColorManager.BUTTON_STATE_ON, 1);
        colorManager.registerColor (ColorManager.BUTTON_STATE_HI, 2);
        colorManager.registerColor (BUTTON_STATE_BLINK, 3);

        colorManager.registerColor (COLOR_VIEW_SELECTED, isMkII ? APC_MKII_COLOR_GREEN : APC_COLOR_GREEN);
        colorManager.registerColor (COLOR_VIEW_UNSELECTED, isMkII ? APC_MKII_COLOR_AMBER : APC_COLOR_BLACK);
        colorManager.registerColor (COLOR_VIEW_OFF, isMkII ? APC_MKII_COLOR_BLACK : APC_COLOR_BLACK);
        colorManager.registerColor (COLOR_KEY_WHITE, isMkII ? APC_MKII_COLOR_AMBER_LO : APC_COLOR_GREEN);
        colorManager.registerColor (COLOR_KEY_BLACK, isMkII ? APC_MKII_COLOR_RED_HI : APC_COLOR_RED);
        colorManager.registerColor (COLOR_KEY_SELECTED, isMkII ? APC_MKII_COLOR_GREEN_HI : APC_COLOR_GREEN);
    }
}