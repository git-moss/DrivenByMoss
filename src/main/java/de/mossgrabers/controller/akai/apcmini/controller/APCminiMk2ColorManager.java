// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.apcmini.controller;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.view.AbstractPlayView;
import de.mossgrabers.framework.view.AbstractSessionView;
import de.mossgrabers.framework.view.BrowserView;
import de.mossgrabers.framework.view.sequencer.AbstractDrumView;
import de.mossgrabers.framework.view.sequencer.AbstractSequencerView;


/**
 * Different colors to use for the pads and buttons of APC40 mkI and mkII.
 *
 * @author Jürgen Moßgraber
 */
@SuppressWarnings(
{
    "unused",
    "javadoc"
})
public class APCminiMk2ColorManager extends ColorManager
{
    private static final int SIMPLE_OFF     = 0;
    private static final int SIMPLE_ON      = 1;
    private static final int SIMPLE_BLINK   = 2;

    public static final int  BLACK          = 0;
    public static final int  DARKEST_GRAY   = 1;
    public static final int  GRAY           = 2;
    public static final int  WHITE          = 3;
    public static final int  LIGHT_RED      = 4;
    public static final int  RED            = 5;
    public static final int  DARK_RED       = 6;
    public static final int  DEEP_RED       = 7;
    public static final int  PEACH          = 8;
    public static final int  ORANGE         = 9;
    public static final int  BROWN          = 10;
    public static final int  DARK_BROWN     = 11;
    public static final int  YELLOW         = 12;
    public static final int  GOLD           = 13;
    public static final int  OLIVE          = 14;
    public static final int  DARK_GREEN     = 15;
    public static final int  LIGHT_GREEN    = 16;
    public static final int  BRIGHT_GREEN   = 17;
    public static final int  FOREST_GREEN   = 18;
    public static final int  DEEP_GREEN     = 19;
    public static final int  LIGHTER_GREEN  = 20;
    public static final int  GREEN          = 21;
    public static final int  DARK_GREEN_2   = 22;
    public static final int  DEEP_GREEN_2   = 23;
    public static final int  LIGHT_TEAL     = 24;
    public static final int  TURQUOISE      = 25;
    public static final int  DARK_TEAL      = 26;
    public static final int  DEEP_TEAL      = 27;
    public static final int  LIGHT_CYAN     = 28;
    public static final int  CYAN           = 29;
    public static final int  DARK_CYAN      = 30;
    public static final int  DEEP_CYAN      = 31;
    public static final int  LIGHT_BLUE     = 32;
    public static final int  BLUE           = 33;
    public static final int  DARK_BLUE      = 34;
    public static final int  DEEP_BLUE      = 35;
    public static final int  SKY_BLUE       = 36;
    public static final int  LIGHT_SKY_BLUE = 37;
    public static final int  DARK_SKY_BLUE  = 38;
    public static final int  NAVY           = 39;
    public static final int  MEDIUM_BLUE    = 40;
    public static final int  BRIGHT_BLUE    = 41;
    public static final int  DEEP_BLUE_2    = 42;
    public static final int  BLACK_2        = 43;
    public static final int  PURPLE         = 44;
    public static final int  DARK_PURPLE    = 45;
    public static final int  DEEP_PURPLE    = 46;
    public static final int  LIGHT_VIOLET   = 47;
    public static final int  MAGENTA        = 48;
    public static final int  FUCHSIA        = 49;
    public static final int  DEEP_MAGENTA   = 50;
    public static final int  DARK_MAGENTA   = 51;
    public static final int  LIGHT_PINK     = 52;
    public static final int  PINK           = 53;
    public static final int  DEEP_PINK      = 54;
    public static final int  DARK_PINK      = 55;
    public static final int  RED_PINK       = 56;
    public static final int  HOT_PINK       = 57;
    public static final int  BROWN_2        = 58;
    public static final int  DARK_BROWN_2   = 59;
    public static final int  LIGHT_ORANGE   = 60;
    public static final int  BROWN_3        = 61;
    public static final int  DARK_BROWN_3   = 62;
    public static final int  OLIVE_2        = 63;
    public static final int  DARK_GREEN_3   = 64;
    public static final int  LIGHT_GREEN_2  = 65;
    public static final int  TEAL           = 66;
    public static final int  LIGHT_GRAY     = 67;
    public static final int  GRAY_2         = 68;
    public static final int  LIGHT_GRAY_2   = 69;
    public static final int  GRAY_3         = 70;
    public static final int  DARK_GRAY      = 71;
    public static final int  BRIGHT_RED     = 72;
    public static final int  BRIGHT_GREEN_2 = 73;
    public static final int  BRIGHT_YELLOW  = 74;
    public static final int  BRIGHT_LIME    = 75;
    public static final int  BRIGHT_TEAL    = 76;
    public static final int  BRIGHT_CYAN    = 77;
    public static final int  LIGHT_BLUE_2   = 78;
    public static final int  DEEP_BLUE_3    = 79;
    public static final int  MEDIUM_BLUE_2  = 80;
    public static final int  VIOLET         = 81;
    public static final int  MAGENTA_2      = 82;
    public static final int  DARK_ORANGE    = 83;
    public static final int  ORANGE_2       = 84;
    public static final int  LIGHT_ORANGE_2 = 85;
    public static final int  SOFT_GREEN     = 86;
    public static final int  FRESH_GREEN    = 87;
    public static final int  SOFT_YELLOW    = 88;
    public static final int  LIGHT_YELLOW   = 89;
    public static final int  LIGHT_GOLD     = 90;
    public static final int  PASTEL_GREEN   = 91;
    public static final int  PASTEL_BLUE    = 92;
    public static final int  PASTEL_VIOLET  = 93;
    public static final int  PASTEL_PINK    = 94;
    public static final int  SOFT_RED       = 95;
    public static final int  DEEP_ORANGE    = 96;
    public static final int  SOFT_BROWN     = 97;
    public static final int  LIGHT_BROWN    = 98;
    public static final int  SOFT_GRAY      = 99;
    public static final int  LIGHT_GRAY_4   = 100;
    public static final int  DEEP_GRAY      = 101;
    public static final int  DARK_GRAY_2    = 102;
    public static final int  MEDIUM_GRAY    = 103;
    public static final int  DARK_GRAY_3    = 104;
    public static final int  RICH_BROWN     = 105;
    public static final int  BRIGHT_RED_2   = 106;
    public static final int  VIBRANT_RED    = 107;
    public static final int  RICH_ORANGE    = 108;
    public static final int  RICH_YELLOW    = 109;
    public static final int  FRESH_YELLOW   = 110;
    public static final int  LUSH_GREEN     = 111;
    public static final int  LIGHT_TEAL_2   = 112;
    public static final int  SOFT_BLUE      = 113;
    public static final int  FRESH_BLUE     = 114;
    public static final int  LIGHT_PURPLE   = 115;
    public static final int  LIGHT_VIOLET_2 = 116;
    public static final int  BRIGHT_VIOLET  = 117;
    public static final int  MID_GRAY       = 118;
    public static final int  LIGHT_MAGENTA  = 119;
    public static final int  DEEP_MAGENTA_2 = 120;
    public static final int  DARK_MAGENTA_2 = 121;
    public static final int  LIGHT_PINK_2   = 122;
    public static final int  SOFT_PINK      = 123;
    public static final int  LIGHT_RED_2    = 124;
    public static final int  LIGHT_MAROON   = 125;
    public static final int  LIGHT_BROWN_2  = 126;
    public static final int  DARK_BROWN_4   = 127;


    /**
     * Constructor.
     */
    public APCminiMk2ColorManager ()
    {
        this.registerColorIndex (ColorManager.BUTTON_STATE_OFF, SIMPLE_OFF);
        this.registerColorIndex (ColorManager.BUTTON_STATE_ON, SIMPLE_ON);
        this.registerColorIndex (ColorManager.BUTTON_STATE_HI, SIMPLE_BLINK);

        this.registerColorIndex (AbstractSessionView.COLOR_SCENE_OFF, SIMPLE_OFF);
        this.registerColorIndex (AbstractSessionView.COLOR_SCENE, SIMPLE_ON);
        this.registerColorIndex (AbstractSessionView.COLOR_SELECTED_SCENE, SIMPLE_BLINK);

        this.registerColorIndex (Scales.SCALE_COLOR_OFF, BLACK);
        this.registerColorIndex (Scales.SCALE_COLOR_OCTAVE, YELLOW);
        this.registerColorIndex (Scales.SCALE_COLOR_NOTE, BLACK);
        this.registerColorIndex (Scales.SCALE_COLOR_OUT_OF_SCALE, BLACK);

        this.registerColorIndex (AbstractSequencerView.COLOR_NO_CONTENT, BLACK);
        this.registerColorIndex (AbstractSequencerView.COLOR_NO_CONTENT_4, BLACK);
        this.registerColorIndex (AbstractSequencerView.COLOR_CONTENT, BLUE);
        this.registerColorIndex (AbstractSequencerView.COLOR_CONTENT_CONT, LIGHT_BLUE);
        this.registerColorIndex (AbstractSequencerView.COLOR_STEP_HILITE_NO_CONTENT, GREEN);
        this.registerColorIndex (AbstractSequencerView.COLOR_STEP_HILITE_CONTENT, BRIGHT_GREEN);
        this.registerColorIndex (AbstractSequencerView.COLOR_STEP_MUTED, GRAY);
        this.registerColorIndex (AbstractSequencerView.COLOR_STEP_MUTED_CONT, DARK_GRAY);
        this.registerColorIndex (AbstractSequencerView.COLOR_STEP_SELECTED, YELLOW);
        this.registerColorIndex (AbstractSequencerView.COLOR_PAGE, YELLOW);
        this.registerColorIndex (AbstractSequencerView.COLOR_ACTIVE_PAGE, GREEN);
        this.registerColorIndex (AbstractSequencerView.COLOR_SELECTED_PAGE, RED);

        this.registerColorIndex (AbstractDrumView.COLOR_PAD_OFF, BLACK);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_RECORD, RED);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_PLAY, GREEN);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_SELECTED, BLUE);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_MUTED, DARK_ORANGE);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_HAS_CONTENT, LIGHT_YELLOW);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_NO_CONTENT, BLACK);

        this.registerColorIndex (AbstractPlayView.COLOR_PLAY, GREEN);
        this.registerColorIndex (AbstractPlayView.COLOR_RECORD, RED);
        this.registerColorIndex (AbstractPlayView.COLOR_OFF, BLACK);

        this.registerColorIndex (BrowserView.OFF, BLACK);
        this.registerColorIndex (BrowserView.DISCARD, RED);
        this.registerColorIndex (BrowserView.CONFIRM, GREEN);
        this.registerColorIndex (BrowserView.PLAY, ORANGE);
        this.registerColorIndex (BrowserView.COLUMN1, GREEN);
        this.registerColorIndex (BrowserView.COLUMN2, RED);
        this.registerColorIndex (BrowserView.COLUMN3, GREEN);
        this.registerColorIndex (BrowserView.COLUMN4, RED);
        this.registerColorIndex (BrowserView.COLUMN5, GREEN);
        this.registerColorIndex (BrowserView.COLUMN6, RED);
        this.registerColorIndex (BrowserView.COLUMN7, BLACK);
        this.registerColorIndex (BrowserView.COLUMN8, ORANGE);

        this.registerColorIndex (IPadGrid.GRID_OFF, BLACK);

        this.registerColorIndex (DAWColor.DAW_COLOR_GRAY_HALF, DARKEST_GRAY);
        this.registerColorIndex (DAWColor.DAW_COLOR_DARK_GRAY, DARK_GRAY);
        this.registerColorIndex (DAWColor.DAW_COLOR_GRAY, GRAY);
        this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_GRAY, LIGHT_BLUE);
        this.registerColorIndex (DAWColor.DAW_COLOR_SILVER, MID_GRAY);
        this.registerColorIndex (DAWColor.DAW_COLOR_DARK_BROWN, DARK_BROWN);
        this.registerColorIndex (DAWColor.DAW_COLOR_BROWN, BROWN);
        this.registerColorIndex (DAWColor.DAW_COLOR_DARK_BLUE, DARK_BLUE);
        this.registerColorIndex (DAWColor.DAW_COLOR_PURPLE_BLUE, LIGHT_PURPLE);
        this.registerColorIndex (DAWColor.DAW_COLOR_PURPLE, PURPLE);
        this.registerColorIndex (DAWColor.DAW_COLOR_PINK, PINK);
        this.registerColorIndex (DAWColor.DAW_COLOR_RED, RED);
        this.registerColorIndex (DAWColor.DAW_COLOR_ORANGE, LIGHT_ORANGE);
        this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_ORANGE, ORANGE);
        this.registerColorIndex (DAWColor.DAW_COLOR_MOSS_GREEN, LIGHT_GREEN);
        this.registerColorIndex (DAWColor.DAW_COLOR_GREEN, GREEN);
        this.registerColorIndex (DAWColor.DAW_COLOR_COLD_GREEN, TURQUOISE);
        this.registerColorIndex (DAWColor.DAW_COLOR_BLUE, BLUE);
        this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_PURPLE, LIGHT_PURPLE);
        this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_PINK, LIGHT_PINK);
        this.registerColorIndex (DAWColor.DAW_COLOR_ROSE, SOFT_RED);
        this.registerColorIndex (DAWColor.DAW_COLOR_REDDISH_BROWN, RICH_BROWN);
        this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_BROWN, LIGHT_BROWN);
        this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_GREEN, PASTEL_BLUE);
        this.registerColorIndex (DAWColor.DAW_COLOR_BLUISH_GREEN, CYAN);
        this.registerColorIndex (DAWColor.DAW_COLOR_GREEN_BLUE, FOREST_GREEN);
        this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_BLUE, LIGHT_GRAY);

        ////////////////////////////////////////////////////////////////////////
        // Register colors

        for (int i = 0; i < 128; i++)
            this.registerColor (i, ColorEx.BLACK);

        this.registerColorIndex (DAWColor.COLOR_OFF, BLACK);
        for (final DAWColor dc: DAWColor.values ())
        {
            final String name = dc.name ();
            this.registerColor (this.getColorIndex (name), DAWColor.getColorEntry (name));
        }

        this.registerColor (BLACK, ColorEx.BLACK);
        this.registerColor (WHITE, ColorEx.WHITE);
        this.registerColor (RED, ColorEx.RED);
        this.registerColor (YELLOW, ColorEx.YELLOW);
        this.registerColor (LIGHT_YELLOW, ColorEx.brighter (ColorEx.YELLOW));
        this.registerColor (DARK_ORANGE, ColorEx.DARK_ORANGE);
    }
}