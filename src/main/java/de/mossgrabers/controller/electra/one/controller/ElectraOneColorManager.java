// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.electra.one.controller;

import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.color.ColorManager;

import java.util.HashMap;
import java.util.Map;


/**
 * Different colors to use for the Electra.ONe.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
@SuppressWarnings("javadoc")
public class ElectraOneColorManager extends ColorManager
{
    /** State for button LED on. */
    public static final int                    COLOR_BUTTON_STATE_ON  = 127;
    /** State for button LED off. */
    public static final int                    COLOR_BUTTON_STATE_OFF = 0;

    public static final ColorEx                WHITE                  = ColorEx.fromRGB (0xFF, 0xFF, 0xFF);
    public static final ColorEx                RED                    = ColorEx.fromRGB (0xF4, 0x5C, 0x51);
    public static final ColorEx                ORANGE                 = ColorEx.fromRGB (0xF4, 0x95, 0x00);
    public static final ColorEx                BLUE                   = ColorEx.fromRGB (0x52, 0x9D, 0xEC);
    public static final ColorEx                GREEN                  = ColorEx.fromRGB (0x03, 0xA5, 0x98);
    public static final ColorEx                PURPLE                 = ColorEx.fromRGB (0xC4, 0x47, 0x95);

    /** The available Electra.One color palette. */
    public static final ColorEx []             PALETTE                =
    {
        WHITE,
        RED,
        ORANGE,
        BLUE,
        GREEN,
        PURPLE
    };

    private static final Map<ColorEx, ColorEx> PALETTE_MAP            = new HashMap<> ();

    // TODO
    // public static final int LAUNCHKEY_COLOR_BLACK = 0;
    // public static final int LAUNCHKEY_COLOR_GREY_LO = 1;
    // public static final int LAUNCHKEY_COLOR_GREY_MD = 2;
    // public static final int LAUNCHKEY_COLOR_WHITE = 3;
    // public static final int LAUNCHKEY_COLOR_ROSE = 4;
    // public static final int LAUNCHKEY_COLOR_RED_HI = 5;
    // public static final int LAUNCHKEY_COLOR_RED = 6;
    // public static final int LAUNCHKEY_COLOR_RED_LO = 7;
    // public static final int LAUNCHKEY_COLOR_RED_AMBER = 8;
    // public static final int LAUNCHKEY_COLOR_AMBER_HI = 9;


    /**
     * Constructor.
     */
    public ElectraOneColorManager ()
    {
        // this.registerColorIndex (Scales.SCALE_COLOR_OFF, LAUNCHKEY_COLOR_BLACK);
        // this.registerColorIndex (Scales.SCALE_COLOR_OCTAVE, LAUNCHKEY_COLOR_OCEAN_HI);
        // this.registerColorIndex (Scales.SCALE_COLOR_NOTE, LAUNCHKEY_COLOR_WHITE);
        // this.registerColorIndex (Scales.SCALE_COLOR_OUT_OF_SCALE, LAUNCHKEY_COLOR_BLACK);
        //
        // this.registerColorIndex (AbstractSequencerView.COLOR_STEP_HILITE_NO_CONTENT,
        // LAUNCHKEY_COLOR_GREEN_LO);
        // this.registerColorIndex (AbstractSequencerView.COLOR_STEP_HILITE_CONTENT,
        // LAUNCHKEY_COLOR_GREEN_HI);
        // this.registerColorIndex (AbstractSequencerView.COLOR_STEP_MUTED,
        // LAUNCHKEY_COLOR_GREY_MD);
        // this.registerColorIndex (AbstractSequencerView.COLOR_STEP_MUTED_CONT,
        // LAUNCHKEY_COLOR_GREY_LO);
        // this.registerColorIndex (AbstractSequencerView.COLOR_STEP_SELECTED,
        // LAUNCHKEY_COLOR_WHITE);
        // this.registerColorIndex (AbstractSequencerView.COLOR_NO_CONTENT, LAUNCHKEY_COLOR_BLACK);
        // this.registerColorIndex (AbstractSequencerView.COLOR_CONTENT, LAUNCHKEY_COLOR_BLUE_HI);
        // this.registerColorIndex (AbstractSequencerView.COLOR_CONTENT_CONT,
        // LAUNCHKEY_COLOR_BLUE_ORCHID);
        // this.registerColorIndex (AbstractSequencerView.COLOR_PAGE, LAUNCHKEY_COLOR_WHITE);
        // this.registerColorIndex (AbstractSequencerView.COLOR_ACTIVE_PAGE, LAUNCHKEY_COLOR_GREEN);
        // this.registerColorIndex (AbstractSequencerView.COLOR_SELECTED_PAGE,
        // LAUNCHKEY_COLOR_BLUE_ORCHID);
        // this.registerColorIndex (AbstractSequencerView.COLOR_RESOLUTION_OFF,
        // LAUNCHKEY_COLOR_BLACK);
        //
        // this.registerColorIndex (AbstractDrumView.COLOR_PAD_OFF, LAUNCHKEY_COLOR_BLACK);
        // this.registerColorIndex (AbstractDrumView.COLOR_PAD_RECORD, LAUNCHKEY_COLOR_RED_HI);
        // this.registerColorIndex (AbstractDrumView.COLOR_PAD_PLAY, LAUNCHKEY_COLOR_GREEN_HI);
        // this.registerColorIndex (AbstractDrumView.COLOR_PAD_SELECTED, LAUNCHKEY_COLOR_BLUE_HI);
        // this.registerColorIndex (AbstractDrumView.COLOR_PAD_MUTED, LAUNCHKEY_COLOR_AMBER_LO);
        // this.registerColorIndex (AbstractDrumView.COLOR_PAD_HAS_CONTENT,
        // LAUNCHKEY_COLOR_YELLOW_HI);
        // this.registerColorIndex (AbstractDrumView.COLOR_PAD_NO_CONTENT,
        // LAUNCHKEY_COLOR_YELLOW_LO);
        //
        // this.registerColorIndex (AbstractPlayView.COLOR_PLAY, LAUNCHKEY_COLOR_GREEN_HI);
        // this.registerColorIndex (AbstractPlayView.COLOR_RECORD, LAUNCHKEY_COLOR_RED_HI);
        // this.registerColorIndex (AbstractPlayView.COLOR_OFF, LAUNCHKEY_COLOR_BLACK);
        //
        // this.registerColorIndex (AbstractSessionView.COLOR_SCENE, LAUNCHKEY_COLOR_GREEN_LO);
        // this.registerColorIndex (AbstractSessionView.COLOR_SELECTED_SCENE,
        // LAUNCHKEY_COLOR_GREEN_HI);
        // this.registerColorIndex (AbstractSessionView.COLOR_SCENE_OFF, LAUNCHKEY_COLOR_BLACK);
        //
        // this.registerColorIndex (IPadGrid.GRID_OFF, LAUNCHKEY_COLOR_BLACK);
        //
        // this.registerColorIndex (DAWColor.COLOR_OFF, LAUNCHKEY_COLOR_BLACK);
        // this.registerColorIndex (DAWColor.DAW_COLOR_GRAY_HALF, LAUNCHKEY_COLOR_GREY_MD);
        // this.registerColorIndex (DAWColor.DAW_COLOR_DARK_GRAY, LAUNCHKEY_COLOR_GREY_LO);
        // this.registerColorIndex (DAWColor.DAW_COLOR_GRAY, LAUNCHKEY_COLOR_GREY_MD);
        // this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_GRAY, LAUNCHKEY_COLOR_GREY_LO);
        // this.registerColorIndex (DAWColor.DAW_COLOR_SILVER, LAUNCHKEY_COLOR_SKY_OCEAN);
        // this.registerColorIndex (DAWColor.DAW_COLOR_DARK_BROWN, LAUNCHKEY_COLOR_AMBER_LO);
        // this.registerColorIndex (DAWColor.DAW_COLOR_BROWN, LAUNCHKEY_COLOR_AMBER_YELLOW);
        // this.registerColorIndex (DAWColor.DAW_COLOR_DARK_BLUE, LAUNCHKEY_COLOR_OCEAN);
        // this.registerColorIndex (DAWColor.DAW_COLOR_PURPLE_BLUE, LAUNCHKEY_COLOR_OCEAN_BLUE);
        // this.registerColorIndex (DAWColor.DAW_COLOR_PURPLE, LAUNCHKEY_COLOR_ORCHID_HI);
        // this.registerColorIndex (DAWColor.DAW_COLOR_PINK, LAUNCHKEY_COLOR_PINK_HI);
        // this.registerColorIndex (DAWColor.DAW_COLOR_RED, LAUNCHKEY_COLOR_RED);
        // this.registerColorIndex (DAWColor.DAW_COLOR_ORANGE, LAUNCHKEY_COLOR_ORANGE);
        // this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_ORANGE, LAUNCHKEY_COLOR_AMBER_HI);
        // this.registerColorIndex (DAWColor.DAW_COLOR_MOSS_GREEN, LAUNCHKEY_COLOR_LIME_LO);
        // this.registerColorIndex (DAWColor.DAW_COLOR_GREEN, LAUNCHKEY_COLOR_SPRING);
        // this.registerColorIndex (DAWColor.DAW_COLOR_COLD_GREEN, LAUNCHKEY_COLOR_TURQUOISE);
        // this.registerColorIndex (DAWColor.DAW_COLOR_BLUE, LAUNCHKEY_COLOR_SKY_HI);
        // this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_PURPLE, LAUNCHKEY_COLOR_BLUE_ORCHID);
        // this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_PINK, LAUNCHKEY_COLOR_MAGENTA_PINK);
        // this.registerColorIndex (DAWColor.DAW_COLOR_ROSE, LAUNCHKEY_COLOR_ROSE);
        // this.registerColorIndex (DAWColor.DAW_COLOR_REDDISH_BROWN, LAUNCHKEY_COLOR_AMBER);
        // this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_BROWN, LAUNCHKEY_COLOR_AMBER_YELLOW);
        // this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_GREEN, LAUNCHKEY_COLOR_LIME);
        // this.registerColorIndex (DAWColor.DAW_COLOR_BLUISH_GREEN, LAUNCHKEY_COLOR_SPRING_HI);
        // this.registerColorIndex (DAWColor.DAW_COLOR_GREEN_BLUE, LAUNCHKEY_COLOR_TURQUOISE_CYAN);
        // this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_BLUE, LAUNCHKEY_COLOR_OCEAN_HI);

        this.registerColorIndex (ColorManager.BUTTON_STATE_OFF, COLOR_BUTTON_STATE_OFF);
        this.registerColorIndex (ColorManager.BUTTON_STATE_ON, COLOR_BUTTON_STATE_ON);
        this.registerColorIndex (ColorManager.BUTTON_STATE_HI, COLOR_BUTTON_STATE_ON);

        for (int i = 0; i < 127; i++)
            this.registerColor (i, ColorEx.BLACK);
        this.registerColor (127, ColorEx.WHITE);

        // this.registerColor (LAUNCHKEY_COLOR_BLACK, ColorEx.BLACK);
        // this.registerColor (LAUNCHKEY_COLOR_GREY_LO, DAWColor.DAW_COLOR_LIGHT_GRAY.getColor ());
        // this.registerColor (LAUNCHKEY_COLOR_GREY_MD, DAWColor.DAW_COLOR_GRAY_HALF.getColor ());
        // this.registerColor (LAUNCHKEY_COLOR_WHITE, ColorEx.WHITE);
        // this.registerColor (LAUNCHKEY_COLOR_ROSE, DAWColor.DAW_COLOR_ROSE.getColor ());
        // this.registerColor (LAUNCHKEY_COLOR_RED_HI, DAWColor.DAW_COLOR_RED.getColor ());
        // this.registerColor (LAUNCHKEY_COLOR_RED, DAWColor.DAW_COLOR_REDDISH_BROWN.getColor ());
        // this.registerColor (LAUNCHKEY_COLOR_RED_LO, ColorEx.fromRGB (39, 4, 1));
        // this.registerColor (LAUNCHKEY_COLOR_RED_AMBER, ColorEx.fromRGB (45, 34, 21));
        // this.registerColor (LAUNCHKEY_COLOR_AMBER_HI, DAWColor.DAW_COLOR_LIGHT_ORANGE.getColor
        // ());
        // this.registerColor (LAUNCHKEY_COLOR_AMBER, DAWColor.DAW_COLOR_REDDISH_BROWN.getColor ());
        // this.registerColor (LAUNCHKEY_COLOR_AMBER_LO, DAWColor.DAW_COLOR_DARK_BROWN.getColor ());
        // this.registerColor (LAUNCHKEY_COLOR_AMBER_YELLOW, DAWColor.DAW_COLOR_LIGHT_BROWN.getColor
        // ());
        // this.registerColor (LAUNCHKEY_COLOR_YELLOW_HI, ColorEx.fromRGB (253, 250, 1));
        // this.registerColor (LAUNCHKEY_COLOR_YELLOW, ColorEx.fromRGB (107, 105, 1));
        // this.registerColor (LAUNCHKEY_COLOR_YELLOW_LO, ColorEx.fromRGB (37, 36, 1));
        // this.registerColor (LAUNCHKEY_COLOR_YELLOW_LIME, ColorEx.fromRGB (141, 248, 57));
        // this.registerColor (LAUNCHKEY_COLOR_LIME_HI, ColorEx.fromRGB (70, 247, 1));
        // this.registerColor (LAUNCHKEY_COLOR_LIME, ColorEx.fromRGB (29, 104, 1));
        // this.registerColor (LAUNCHKEY_COLOR_LIME_LO, DAWColor.DAW_COLOR_MOSS_GREEN.getColor ());
        // this.registerColor (LAUNCHKEY_COLOR_LIME_GREEN, ColorEx.fromRGB (53, 248, 58));
        // this.registerColor (LAUNCHKEY_COLOR_GREEN_HI, ColorEx.fromRGB (1, 247, 1));
        // this.registerColor (LAUNCHKEY_COLOR_GREEN, ColorEx.fromRGB (1, 104, 1));
        // this.registerColor (LAUNCHKEY_COLOR_GREEN_LO, ColorEx.fromRGB (1, 36, 1));
        // this.registerColor (LAUNCHKEY_COLOR_GREEN_SPRING, ColorEx.fromRGB (52, 248, 88));
        // this.registerColor (LAUNCHKEY_COLOR_SPRING_HI, DAWColor.DAW_COLOR_BLUISH_GREEN.getColor
        // ());
        // this.registerColor (LAUNCHKEY_COLOR_SPRING, DAWColor.DAW_COLOR_GREEN.getColor ());
        // this.registerColor (LAUNCHKEY_COLOR_SPRING_LO, ColorEx.fromRGB (1, 36, 1));
        // this.registerColor (LAUNCHKEY_COLOR_SPRING_TURQUOISE, ColorEx.fromRGB (51, 249, 143));
        // this.registerColor (LAUNCHKEY_COLOR_TURQUOISE_LO, ColorEx.fromRGB (1, 248, 75));
        // this.registerColor (LAUNCHKEY_COLOR_TURQUOISE, DAWColor.DAW_COLOR_COLD_GREEN.getColor
        // ());
        // this.registerColor (LAUNCHKEY_COLOR_TURQUOISE_HI, ColorEx.fromRGB (1, 41, 25));
        // this.registerColor (LAUNCHKEY_COLOR_TURQUOISE_CYAN,
        // DAWColor.DAW_COLOR_GREEN_BLUE.getColor ());
        // this.registerColor (LAUNCHKEY_COLOR_CYAN_HI, ColorEx.fromRGB (1, 248, 161));
        // this.registerColor (LAUNCHKEY_COLOR_CYAN, ColorEx.fromRGB (1, 105, 66));
        // this.registerColor (LAUNCHKEY_COLOR_CYAN_LO, ColorEx.fromRGB (1, 36, 25));
        // this.registerColor (LAUNCHKEY_COLOR_CYAN_SKY, ColorEx.fromRGB (68, 202, 255));
        // this.registerColor (LAUNCHKEY_COLOR_SKY_HI, ColorEx.fromRGB (1, 182, 255));
        // this.registerColor (LAUNCHKEY_COLOR_SKY, ColorEx.fromRGB (1, 82, 100));
        // this.registerColor (LAUNCHKEY_COLOR_SKY_LO, ColorEx.fromRGB (1, 26, 37));
        // this.registerColor (LAUNCHKEY_COLOR_SKY_OCEAN, DAWColor.DAW_COLOR_SILVER.getColor ());
        // this.registerColor (LAUNCHKEY_COLOR_OCEAN_HI, DAWColor.DAW_COLOR_LIGHT_BLUE.getColor ());
        // this.registerColor (LAUNCHKEY_COLOR_OCEAN, DAWColor.DAW_COLOR_DARK_BLUE.getColor ());
        // this.registerColor (LAUNCHKEY_COLOR_OCEAN_LO, ColorEx.fromRGB (1, 15, 38));
        // this.registerColor (LAUNCHKEY_COLOR_OCEAN_BLUE, DAWColor.DAW_COLOR_PURPLE_BLUE.getColor
        // ());
        // this.registerColor (LAUNCHKEY_COLOR_BLUE_HI, ColorEx.fromRGB (14, 54, 255));
        // this.registerColor (LAUNCHKEY_COLOR_BLUE, ColorEx.fromRGB (4, 23, 110));
        // this.registerColor (LAUNCHKEY_COLOR_BLUE_LO, ColorEx.fromRGB (1, 8, 38));
        // this.registerColor (LAUNCHKEY_COLOR_BLUE_ORCHID, DAWColor.DAW_COLOR_LIGHT_PURPLE.getColor
        // ());
        // this.registerColor (LAUNCHKEY_COLOR_ORCHID_HI, DAWColor.DAW_COLOR_PURPLE.getColor ());
        // this.registerColor (LAUNCHKEY_COLOR_ORCHID, ColorEx.fromRGB (35, 26, 122));
        // this.registerColor (LAUNCHKEY_COLOR_ORCHID_LO, ColorEx.fromRGB (20, 14, 67));
        // this.registerColor (LAUNCHKEY_COLOR_ORCHID_MAGENTA, ColorEx.fromRGB (255, 108, 255));
        // this.registerColor (LAUNCHKEY_COLOR_MAGENTA_HI, ColorEx.fromRGB (255, 67, 255));
        // this.registerColor (LAUNCHKEY_COLOR_MAGENTA, ColorEx.fromRGB (110, 28, 109));
        // this.registerColor (LAUNCHKEY_COLOR_MAGENTA_LO, ColorEx.fromRGB (39, 9, 38));
        // this.registerColor (LAUNCHKEY_COLOR_MAGENTA_PINK, DAWColor.DAW_COLOR_LIGHT_PINK.getColor
        // ());
        // this.registerColor (LAUNCHKEY_COLOR_PINK_HI, DAWColor.DAW_COLOR_PINK.getColor ());
        // this.registerColor (LAUNCHKEY_COLOR_PINK, ColorEx.fromRGB (110, 20, 40));
        // this.registerColor (LAUNCHKEY_COLOR_PINK_LO, ColorEx.fromRGB (48, 9, 26));
        // this.registerColor (LAUNCHKEY_COLOR_ORANGE, DAWColor.DAW_COLOR_ORANGE.getColor ());
    }


    /** {@inheritDoc} */
    @Override
    public ColorEx getColor (final int colorIndex, final ButtonID buttonID)
    {
        // TODO
        // switch (buttonID)
        // {
        // case CONTROL:
        // return colorIndex == 127 ? ColorEx.GREEN : ColorEx.RED;
        //
        // case ROW1_1:
        // case ROW1_2:
        // case ROW1_3:
        // case ROW1_4:
        // case ROW1_5:
        // case ROW1_6:
        // return colorIndex == 127 ? ColorEx.GREEN : ColorEx.DARK_GREEN;
        //
        // case ROW2_1:
        // case ROW2_2:
        // case ROW2_3:
        // return colorIndex == 127 ? ColorEx.ORANGE : ColorEx.DARK_ORANGE;
        //
        // default:
        return super.getColor (colorIndex, buttonID);
        // }
    }


    /**
     * Calculate the color from the palette which is the closest to the given color. Calculated
     * colors are cached.
     *
     * @param color The color
     * @return The color from the palette
     */
    public static ColorEx getClosestPaletteColor (final ColorEx color)
    {
        return PALETTE_MAP.computeIfAbsent (color, c -> c.isGrayscale () ? ColorEx.WHITE : ColorEx.getClosestColor (c, PALETTE));
    }
}