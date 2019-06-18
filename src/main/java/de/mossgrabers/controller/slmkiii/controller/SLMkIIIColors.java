// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.slmkiii.controller;

import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.PadGrid;
import de.mossgrabers.framework.daw.DAWColors;
import de.mossgrabers.framework.mode.AbstractMode;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.view.AbstractDrumView;
import de.mossgrabers.framework.view.AbstractSessionView;


/**
 * Different colors to use for the pads and buttons of Novation SL MkIII.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
@SuppressWarnings("javadoc")
public class SLMkIIIColors
{
    public static final int SLMKIII_BLACK            = 0;
    public static final int SLMKIII_DARK_GREY        = 1;
    public static final int SLMKIII_GREY             = 2;
    public static final int SLMKIII_WHITE            = 3;
    public static final int SLMKIII_WHITE_HALF       = 1;
    public static final int SLMKIII_RED              = 5;
    public static final int SLMKIII_DARK_RED         = 6;
    public static final int SLMKIII_RED_HALF         = 7;
    public static final int SLMKIII_SKIN             = 8;
    public static final int SLMKIII_ORANGE           = 9;
    public static final int SLMKIII_BROWN_DARK       = 10;
    public static final int SLMKIII_ORANGE_HALF      = 11;
    public static final int SLMKIII_AMBER            = 96;
    public static final int SLMKIII_AMBER_HALF       = 14;
    public static final int SLMKIII_YELLOW_LIGHT     = 12;
    public static final int SLMKIII_YELLOW           = 13;
    public static final int SLMKIII_YELLOW_HALF      = 15;
    public static final int SLMKIII_GREEN_LIGHT      = 16;
    public static final int SLMKIII_DARK_YELLOW      = 17;
    public static final int SLMKIII_DARK_GREEN       = 18;
    public static final int SLMKIII_DARK_YELLOW_HALF = 19;
    public static final int SLMKIII_GREEN_GRASS      = 20;
    public static final int SLMKIII_GREEN            = 21;
    public static final int SLMKIII_DARK_GREEN_HALF  = 22;
    public static final int SLMKIII_GREEN_HALF       = 27;
    public static final int SLMKIII_MINT             = 29;
    public static final int SLMKIII_MINT_HALF        = 31;
    public static final int SLMKIII_OLIVE            = 34;
    public static final int SLMKIII_SKY_BLUE         = 36;
    public static final int SLMKIII_LIGHT_BLUE       = 37;
    public static final int SLMKIII_BLUE_METAL       = 38;
    public static final int SLMKIII_LIGHT_BLUE_HALF  = 39;
    public static final int SLMKIII_BLUE             = 45;
    public static final int SLMKIII_BLUE_HALF        = 47;
    public static final int SLMKIII_DARK_BLUE        = 49;
    public static final int SLMKIII_DARK_BLUE_HALF   = 51;
    public static final int SLMKIII_PURPLE           = 53;
    public static final int SLMKIII_PURPLE_HALF      = 54;
    public static final int SLMKIII_PINK_LIGHT       = 56;
    public static final int SLMKIII_PINK             = 57;
    public static final int SLMKIII_RED_WINE         = 58;
    public static final int SLMKIII_BROWN            = 61;
    public static final int SLMKIII_BLUE_PURPLISH    = 81;
    public static final int SLMKIII_PINK_DARK        = 82;
    public static final int SLMKIII_DARK_ORANGE      = 84;


    /**
     * Private due to utility class.
     */
    private SLMkIIIColors ()
    {
        // Intentionally empty
    }


    /**
     * Configures all colors for Push controllers.
     *
     * @param colorManager The color manager
     */
    public static void addColors (final ColorManager colorManager)
    {
        colorManager.registerColor (Scales.SCALE_COLOR_OFF, SLMKIII_BLACK);
        colorManager.registerColor (Scales.SCALE_COLOR_OCTAVE, SLMKIII_BLUE);
        colorManager.registerColor (Scales.SCALE_COLOR_NOTE, SLMKIII_WHITE);
        colorManager.registerColor (Scales.SCALE_COLOR_OUT_OF_SCALE, SLMKIII_BLACK);

        colorManager.registerColor (AbstractMode.BUTTON_COLOR_OFF, SLMKIII_BLACK);
        colorManager.registerColor (AbstractMode.BUTTON_COLOR_ON, SLMKIII_WHITE);
        colorManager.registerColor (AbstractMode.BUTTON_COLOR_HI, SLMKIII_WHITE_HALF);

        // TODO

        // colorManager.registerColor (AbstractSequencerView.COLOR_STEP_HILITE_NO_CONTENT,
        // PUSH2_COLOR2_GREEN_LO : PUSH1_COLOR2_GREEN_LO);
        // colorManager.registerColor (AbstractSequencerView.COLOR_STEP_HILITE_CONTENT,
        // PUSH2_COLOR2_GREEN_HI : PUSH1_COLOR2_GREEN_HI);
        // colorManager.registerColor (AbstractSequencerView.COLOR_NO_CONTENT, SLMKIII_BLACK :
        // PUSH1_COLOR2_BLACK);
        // colorManager.registerColor (AbstractSequencerView.COLOR_CONTENT, PUSH2_COLOR2_BLUE_HI :
        // PUSH1_COLOR2_BLUE_HI);
        // colorManager.registerColor (AbstractSequencerView.COLOR_CONTENT_CONT,
        // PUSH2_COLOR2_BLUE_LO : PUSH1_COLOR2_BLUE_LO);
        // colorManager.registerColor (AbstractSequencerView.COLOR_PAGE, PUSH2_COLOR2_WHITE :
        // PUSH1_COLOR2_WHITE);
        // colorManager.registerColor (AbstractSequencerView.COLOR_ACTIVE_PAGE, PUSH2_COLOR2_GREEN :
        // PUSH1_COLOR2_GREEN);
        // colorManager.registerColor (AbstractSequencerView.COLOR_SELECTED_PAGE,
        // PUSH2_COLOR2_OCEAN_HI : PUSH1_COLOR2_OCEAN_HI);
        // colorManager.registerColor (AbstractSequencerView.COLOR_RESOLUTION,
        // SLMkIIIColors.PUSH2_COLOR_SCENE_ORANGE : SLMkIIIColors.PUSH1_COLOR_SCENE_ORANGE);
        // colorManager.registerColor (AbstractSequencerView.COLOR_RESOLUTION_SELECTED,
        // SLMkIIIColors.PUSH2_COLOR_SCENE_ORANGE_HI : SLMkIIIColors.PUSH1_COLOR_SCENE_ORANGE_HI);
        // colorManager.registerColor (AbstractSequencerView.COLOR_RESOLUTION_OFF, SLMKIII_BLACK :
        // PUSH1_COLOR2_BLACK);
        // colorManager.registerColor (AbstractSequencerView.COLOR_TRANSPOSE,
        // SLMkIIIColors.PUSH2_COLOR_SCENE_WHITE : SLMkIIIColors.PUSH1_COLOR_SCENE_YELLOW);
        // colorManager.registerColor (AbstractSequencerView.COLOR_TRANSPOSE_SELECTED,
        // SLMkIIIColors.PUSH2_COLOR_SCENE_YELLOW_HI : SLMkIIIColors.PUSH1_COLOR_SCENE_YELLOW_HI);

        colorManager.registerColor (AbstractDrumView.COLOR_PAD_OFF, SLMKIII_BLACK);
        colorManager.registerColor (AbstractDrumView.COLOR_PAD_RECORD, SLMKIII_RED);
        colorManager.registerColor (AbstractDrumView.COLOR_PAD_PLAY, SLMKIII_GREEN);
        colorManager.registerColor (AbstractDrumView.COLOR_PAD_SELECTED, SLMKIII_BLUE);
        colorManager.registerColor (AbstractDrumView.COLOR_PAD_MUTED, SLMKIII_AMBER_HALF);
        colorManager.registerColor (AbstractDrumView.COLOR_PAD_HAS_CONTENT, SLMKIII_YELLOW);
        colorManager.registerColor (AbstractDrumView.COLOR_PAD_NO_CONTENT, SLMKIII_YELLOW_HALF);

        colorManager.registerColor (AbstractSessionView.COLOR_SCENE, SLMKIII_GREEN_LIGHT);
        colorManager.registerColor (AbstractSessionView.COLOR_SELECTED_SCENE, SLMKIII_GREEN);
        colorManager.registerColor (AbstractSessionView.COLOR_SCENE_OFF, SLMKIII_BLACK);

        colorManager.registerColor (PadGrid.GRID_OFF, SLMKIII_BLACK);

        colorManager.registerColor (DAWColors.COLOR_OFF, SLMKIII_BLACK);
        colorManager.registerColor (DAWColors.DAW_COLOR_GRAY_HALF, SLMKIII_BLACK);

        colorManager.registerColor (DAWColors.DAW_COLOR_DARK_GRAY, SLMKIII_DARK_GREY);
        colorManager.registerColor (DAWColors.DAW_COLOR_GRAY, SLMKIII_WHITE_HALF);
        colorManager.registerColor (DAWColors.DAW_COLOR_LIGHT_GRAY, SLMKIII_WHITE);
        colorManager.registerColor (DAWColors.DAW_COLOR_SILVER, SLMKIII_GREY);

        colorManager.registerColor (DAWColors.DAW_COLOR_DARK_BROWN, SLMKIII_BROWN_DARK);
        colorManager.registerColor (DAWColors.DAW_COLOR_BROWN, SLMKIII_BROWN);
        colorManager.registerColor (DAWColors.DAW_COLOR_DARK_BLUE, SLMKIII_DARK_BLUE);
        colorManager.registerColor (DAWColors.DAW_COLOR_PURPLE_BLUE, SLMKIII_BLUE_PURPLISH);
        colorManager.registerColor (DAWColors.DAW_COLOR_PURPLE, SLMKIII_PURPLE_HALF);

        colorManager.registerColor (DAWColors.DAW_COLOR_PINK, SLMKIII_PINK);
        colorManager.registerColor (DAWColors.DAW_COLOR_RED, SLMKIII_RED);
        colorManager.registerColor (DAWColors.DAW_COLOR_ORANGE, SLMKIII_ORANGE);
        colorManager.registerColor (DAWColors.DAW_COLOR_LIGHT_ORANGE, SLMKIII_AMBER);
        colorManager.registerColor (DAWColors.DAW_COLOR_MOSS_GREEN, SLMKIII_GREEN_GRASS);
        colorManager.registerColor (DAWColors.DAW_COLOR_GREEN, SLMKIII_GREEN);
        colorManager.registerColor (DAWColors.DAW_COLOR_COLD_GREEN, SLMKIII_GREEN_LIGHT);
        colorManager.registerColor (DAWColors.DAW_COLOR_BLUE, SLMKIII_BLUE);
        colorManager.registerColor (DAWColors.DAW_COLOR_LIGHT_PURPLE, SLMKIII_PURPLE);

        colorManager.registerColor (DAWColors.DAW_COLOR_LIGHT_PINK, SLMKIII_PINK_LIGHT);
        colorManager.registerColor (DAWColors.DAW_COLOR_SKIN, SLMKIII_SKIN);
        colorManager.registerColor (DAWColors.DAW_COLOR_REDDISH_BROWN, SLMKIII_DARK_RED);
        colorManager.registerColor (DAWColors.DAW_COLOR_LIGHT_BROWN, SLMKIII_AMBER_HALF);
        colorManager.registerColor (DAWColors.DAW_COLOR_LIGHT_GREEN, SLMKIII_GREEN_LIGHT);
        colorManager.registerColor (DAWColors.DAW_COLOR_BLUISH_GREEN, SLMKIII_MINT);
        colorManager.registerColor (DAWColors.DAW_COLOR_GREEN_BLUE, SLMKIII_LIGHT_BLUE);
        colorManager.registerColor (DAWColors.DAW_COLOR_LIGHT_BLUE, SLMKIII_SKY_BLUE);

        colorManager.registerColor (ColorManager.BUTTON_STATE_OFF, SLMKIII_BLACK);
        colorManager.registerColor (ColorManager.BUTTON_STATE_ON, SLMKIII_WHITE);
        colorManager.registerColor (ColorManager.BUTTON_STATE_HI, SLMKIII_WHITE_HALF);
    }
}