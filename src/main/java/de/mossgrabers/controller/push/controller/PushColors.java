// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.controller;

import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.PadGrid;
import de.mossgrabers.framework.daw.DAWColors;
import de.mossgrabers.framework.mode.AbstractMode;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.view.AbstractDrumView;
import de.mossgrabers.framework.view.AbstractPlayView;
import de.mossgrabers.framework.view.AbstractSequencerView;
import de.mossgrabers.framework.view.AbstractSessionView;


/**
 * Different colors to use for the pads and buttons of Push 1 and Push 2.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
@SuppressWarnings("javadoc")
public class PushColors
{
    /** ID for color when button signals a recording state. */
    public static final String PUSH_BUTTON_STATE_REC_ON               = "PUSH_BUTTON_STATE_REC_ON";
    /** ID for color when button signals an activated recording state. */
    public static final String PUSH_BUTTON_STATE_REC_HI               = "PUSH_BUTTON_STATE_REC_HI";
    /** ID for color when button signals an overwrite state. */
    public static final String PUSH_BUTTON_STATE_OVR_ON               = "PUSH_BUTTON_STATE_OVR_ON";
    /** ID for color when button signals an activated overwrite state. */
    public static final String PUSH_BUTTON_STATE_OVR_HI               = "PUSH_BUTTON_STATE_OVR_HI";
    /** ID for color when button signals a play state. */
    public static final String PUSH_BUTTON_STATE_PLAY_ON              = "PUSH_BUTTON_STATE_PLAY_ON";
    /** ID for color when button signals an activated play state. */
    public static final String PUSH_BUTTON_STATE_PLAY_HI              = "PUSH_BUTTON_STATE_PLAY_HI";
    /** ID for color when button signals a mute state. */
    public static final String PUSH_BUTTON_STATE_MUTE_ON              = "PUSH_BUTTON_STATE_MUTE_ON";
    /** ID for color when button signals an activated mute state. */
    public static final String PUSH_BUTTON_STATE_MUTE_HI              = "PUSH_BUTTON_STATE_MUTE_HI";
    /** ID for color when button signals a solo state. */
    public static final String PUSH_BUTTON_STATE_SOLO_ON              = "PUSH_BUTTON_STATE_SOLO_ON";
    /** ID for color when button signals an activated solo state. */
    public static final String PUSH_BUTTON_STATE_SOLO_HI              = "PUSH_BUTTON_STATE_SOLO_HI";
    /** ID for color when button signals a stop clip state. */
    public static final String PUSH_BUTTON_STATE_STOP_ON              = "PUSH_BUTTON_STATE_STOP_ON";
    /** ID for color when button signals an activated stop clip state. */
    public static final String PUSH_BUTTON_STATE_STOP_HI              = "PUSH_BUTTON_STATE_STOP_HI";

    // Second row & Pad button colors
    public static final int    PUSH2_COLOR2_BLACK                     = 0;
    public static final int    PUSH2_COLOR2_GREY_LO                   = 1;
    public static final int    PUSH2_COLOR2_GREY_MD                   = 103;
    public static final int    PUSH2_COLOR2_GREY_LT                   = 2;
    public static final int    PUSH2_COLOR2_WHITE                     = 3;
    public static final int    PUSH2_COLOR2_ROSE                      = 4;
    public static final int    PUSH2_COLOR2_RED_HI                    = 5;
    public static final int    PUSH2_COLOR2_RED                       = 6;
    public static final int    PUSH2_COLOR2_RED_LO                    = 7;
    public static final int    PUSH2_COLOR2_RED_AMBER                 = 8;
    public static final int    PUSH2_COLOR2_AMBER_HI                  = 9;
    public static final int    PUSH2_COLOR2_AMBER                     = 10;
    public static final int    PUSH2_COLOR2_AMBER_LO                  = 11;
    public static final int    PUSH2_COLOR2_AMBER_YELLOW              = 12;
    public static final int    PUSH2_COLOR2_YELLOW_HI                 = 13;
    public static final int    PUSH2_COLOR2_YELLOW                    = 14;
    public static final int    PUSH2_COLOR2_YELLOW_LO                 = 15;
    public static final int    PUSH2_COLOR2_YELLOW_LIME               = 16;
    public static final int    PUSH2_COLOR2_LIME_HI                   = 17;
    public static final int    PUSH2_COLOR2_LIME                      = 18;
    public static final int    PUSH2_COLOR2_LIME_LO                   = 19;
    public static final int    PUSH2_COLOR2_LIME_GREEN                = 20;
    public static final int    PUSH2_COLOR2_GREEN_HI                  = 21;
    public static final int    PUSH2_COLOR2_GREEN                     = 22;
    public static final int    PUSH2_COLOR2_GREEN_LO                  = 23;
    public static final int    PUSH2_COLOR2_GREEN_SPRING              = 24;
    public static final int    PUSH2_COLOR2_SPRING_HI                 = 25;
    public static final int    PUSH2_COLOR2_SPRING                    = 26;
    public static final int    PUSH2_COLOR2_SPRING_LO                 = 27;
    public static final int    PUSH2_COLOR2_SPRING_TURQUOISE          = 28;
    public static final int    PUSH2_COLOR2_TURQUOISE_LO              = 29;
    public static final int    PUSH2_COLOR2_TURQUOISE                 = 30;
    public static final int    PUSH2_COLOR2_TURQUOISE_HI              = 31;
    public static final int    PUSH2_COLOR2_TURQUOISE_CYAN            = 32;
    public static final int    PUSH2_COLOR2_CYAN_HI                   = 33;
    public static final int    PUSH2_COLOR2_CYAN                      = 34;
    public static final int    PUSH2_COLOR2_CYAN_LO                   = 35;
    public static final int    PUSH2_COLOR2_CYAN_SKY                  = 36;
    public static final int    PUSH2_COLOR2_SKY_HI                    = 37;
    public static final int    PUSH2_COLOR2_SKY                       = 38;
    public static final int    PUSH2_COLOR2_SKY_LO                    = 39;
    public static final int    PUSH2_COLOR2_SKY_OCEAN                 = 40;
    public static final int    PUSH2_COLOR2_OCEAN_HI                  = 41;
    public static final int    PUSH2_COLOR2_OCEAN                     = 42;
    public static final int    PUSH2_COLOR2_OCEAN_LO                  = 43;
    public static final int    PUSH2_COLOR2_OCEAN_BLUE                = 44;
    public static final int    PUSH2_COLOR2_BLUE_HI                   = 45;
    public static final int    PUSH2_COLOR2_BLUE                      = 46;
    public static final int    PUSH2_COLOR2_BLUE_LO                   = 47;
    public static final int    PUSH2_COLOR2_BLUE_ORCHID               = 48;
    public static final int    PUSH2_COLOR2_ORCHID_HI                 = 49;
    public static final int    PUSH2_COLOR2_ORCHID                    = 50;
    public static final int    PUSH2_COLOR2_ORCHID_LO                 = 51;
    public static final int    PUSH2_COLOR2_ORCHID_MAGENTA            = 52;
    public static final int    PUSH2_COLOR2_MAGENTA_HI                = 53;
    public static final int    PUSH2_COLOR2_MAGENTA                   = 54;
    public static final int    PUSH2_COLOR2_MAGENTA_LO                = 55;
    public static final int    PUSH2_COLOR2_MAGENTA_PINK              = 56;
    public static final int    PUSH2_COLOR2_PINK_HI                   = 57;
    public static final int    PUSH2_COLOR2_PINK                      = 58;
    public static final int    PUSH2_COLOR2_PINK_LO                   = 59;
    public static final int    PUSH2_COLOR2_SILVER                    = 118;
    public static final int    PUSH2_COLOR2_ORANGE                    = 65;
    public static final int    PUSH2_COLOR2_ORANGE_LIGHT              = 3;
    public static final int    PUSH2_COLOR2_LIGHT_BROWN               = 69;

    // First row colors
    public static final int    PUSH2_COLOR_BLACK                      = 0;
    public static final int    PUSH2_COLOR_RED_LO                     = PUSH2_COLOR2_RED_LO;
    public static final int    PUSH2_COLOR_RED_LO_SBLINK              = 2;
    public static final int    PUSH2_COLOR_RED_LO_FBLINK              = 3;
    public static final int    PUSH2_COLOR_RED_HI                     = PUSH2_COLOR2_RED_HI;
    public static final int    PUSH2_COLOR_RED_HI_SBLINK              = 5;
    public static final int    PUSH2_COLOR_RED_HI_FBLINK              = 6;
    public static final int    PUSH2_COLOR_ORANGE_LO                  = PUSH2_COLOR2_AMBER_LO;
    public static final int    PUSH2_COLOR_ORANGE_LO_SBLINK           = 8;
    public static final int    PUSH2_COLOR_ORANGE_LO_FBLINK           = 9;
    public static final int    PUSH2_COLOR_ORANGE_HI                  = PUSH2_COLOR2_AMBER_HI;
    public static final int    PUSH2_COLOR_ORANGE_HI_SBLINK           = 11;
    public static final int    PUSH2_COLOR_ORANGE_HI_FBLINK           = 12;
    public static final int    PUSH2_COLOR_YELLOW_LO                  = PUSH2_COLOR2_YELLOW_LO;
    public static final int    PUSH2_COLOR_YELLOW_LO_SBLINK           = 14;
    public static final int    PUSH2_COLOR_YELLOW_LO_FBLINK           = 15;
    public static final int    PUSH2_COLOR_YELLOW_MD                  = PUSH2_COLOR2_YELLOW_HI;
    public static final int    PUSH2_COLOR_YELLOW_MD_SBLINK           = 17;
    public static final int    PUSH2_COLOR_YELLOW_MD_FBLINK           = 18;
    public static final int    PUSH2_COLOR_GREEN_LO                   = PUSH2_COLOR2_GREEN_LO;
    public static final int    PUSH2_COLOR_GREEN_LO_SBLINK            = 20;
    public static final int    PUSH2_COLOR_GREEN_LO_FBLINK            = 21;
    public static final int    PUSH2_COLOR_GREEN_HI                   = PUSH2_COLOR2_GREEN_HI;
    public static final int    PUSH2_COLOR_GREEN_HI_SBLINK            = 23;
    public static final int    PUSH2_COLOR_GREEN_HI_FBLINK            = 24;

    // Scene button colors
    public static final int    PUSH2_COLOR_SCENE_RED                  = PUSH2_COLOR2_RED;
    public static final int    PUSH2_COLOR_SCENE_RED_BLINK            = 2;
    public static final int    PUSH2_COLOR_SCENE_RED_BLINK_FAST       = 3;
    public static final int    PUSH2_COLOR_SCENE_RED_HI               = PUSH2_COLOR2_RED_HI;
    public static final int    PUSH2_COLOR_SCENE_RED_HI_BLINK         = 5;
    public static final int    PUSH2_COLOR_SCENE_RED_HI_BLINK_FAST    = 6;
    public static final int    PUSH2_COLOR_SCENE_ORANGE               = PUSH2_COLOR2_AMBER;
    public static final int    PUSH2_COLOR_SCENE_ORANGE_BLINK         = 8;
    public static final int    PUSH2_COLOR_SCENE_ORANGE_BLINK_FAST    = 9;
    public static final int    PUSH2_COLOR_SCENE_ORANGE_HI            = PUSH2_COLOR2_AMBER_HI;
    public static final int    PUSH2_COLOR_SCENE_ORANGE_HI_BLINK      = 11;
    public static final int    PUSH2_COLOR_SCENE_ORANGE_HI_BLINK_FAST = 12;
    public static final int    PUSH2_COLOR_SCENE_YELLOW               = PUSH2_COLOR2_YELLOW;
    public static final int    PUSH2_COLOR_SCENE_YELLOW_BLINK         = 14;
    public static final int    PUSH2_COLOR_SCENE_YELLOW_BLINK_FAST    = 15;
    public static final int    PUSH2_COLOR_SCENE_YELLOW_HI            = PUSH2_COLOR2_YELLOW_HI;
    public static final int    PUSH2_COLOR_SCENE_YELLOW_HI_BLINK      = 17;
    public static final int    PUSH2_COLOR_SCENE_YELLOW_HI_BLINK_FAST = 18;
    public static final int    PUSH2_COLOR_SCENE_GREEN                = PUSH2_COLOR2_GREEN;
    public static final int    PUSH2_COLOR_SCENE_GREEN_BLINK          = 20;
    public static final int    PUSH2_COLOR_SCENE_GREEN_BLINK_FAST     = 21;
    public static final int    PUSH2_COLOR_SCENE_GREEN_HI             = PUSH2_COLOR2_GREEN_HI;
    public static final int    PUSH2_COLOR_SCENE_GREEN_HI_BLINK       = 23;
    public static final int    PUSH2_COLOR_SCENE_GREEN_HI_BLINK_FAST  = 24;
    public static final int    PUSH2_COLOR_SCENE_WHITE                = 60;

    // First row colors
    public static final int    PUSH1_COLOR_BLACK                      = 0;
    public static final int    PUSH1_COLOR_RED_LO                     = 1;
    public static final int    PUSH1_COLOR_RED_LO_SBLINK              = 2;
    public static final int    PUSH1_COLOR_RED_LO_FBLINK              = 3;
    public static final int    PUSH1_COLOR_RED_HI                     = 4;
    public static final int    PUSH1_COLOR_RED_HI_SBLINK              = 5;
    public static final int    PUSH1_COLOR_RED_HI_FBLINK              = 6;
    public static final int    PUSH1_COLOR_ORANGE_LO                  = 7;
    public static final int    PUSH1_COLOR_ORANGE_LO_SBLINK           = 8;
    public static final int    PUSH1_COLOR_ORANGE_LO_FBLINK           = 9;
    public static final int    PUSH1_COLOR_ORANGE_HI                  = 10;
    public static final int    PUSH1_COLOR_ORANGE_HI_SBLINK           = 11;
    public static final int    PUSH1_COLOR_ORANGE_HI_FBLINK           = 12;
    public static final int    PUSH1_COLOR_YELLOW_LO                  = 13;
    public static final int    PUSH1_COLOR_YELLOW_LO_SBLINK           = 14;
    public static final int    PUSH1_COLOR_YELLOW_LO_FBLINK           = 15;
    public static final int    PUSH1_COLOR_YELLOW_MD                  = 16;
    public static final int    PUSH1_COLOR_YELLOW_MD_SBLINK           = 17;
    public static final int    PUSH1_COLOR_YELLOW_MD_FBLINK           = 18;
    public static final int    PUSH1_COLOR_GREEN_LO                   = 19;
    public static final int    PUSH1_COLOR_GREEN_LO_SBLINK            = 20;
    public static final int    PUSH1_COLOR_GREEN_LO_FBLINK            = 21;
    public static final int    PUSH1_COLOR_GREEN_HI                   = 22;
    public static final int    PUSH1_COLOR_GREEN_HI_SBLINK            = 23;
    public static final int    PUSH1_COLOR_GREEN_HI_FBLINK            = 24;

    // Second row & Pad button colors
    public static final int    PUSH1_COLOR2_BLACK                     = 0;
    public static final int    PUSH1_COLOR2_GREY_LO                   = 1;
    public static final int    PUSH1_COLOR2_GREY_MD                   = 103;
    public static final int    PUSH1_COLOR2_GREY_LT                   = 2;
    public static final int    PUSH1_COLOR2_WHITE                     = 3;
    public static final int    PUSH1_COLOR2_ROSE                      = 4;
    public static final int    PUSH1_COLOR2_RED_HI                    = 5;
    public static final int    PUSH1_COLOR2_RED                       = 6;
    public static final int    PUSH1_COLOR2_RED_LO                    = 7;
    public static final int    PUSH1_COLOR2_RED_AMBER                 = 8;
    public static final int    PUSH1_COLOR2_AMBER_HI                  = 9;
    public static final int    PUSH1_COLOR2_AMBER                     = 10;
    public static final int    PUSH1_COLOR2_AMBER_LO                  = 11;
    public static final int    PUSH1_COLOR2_AMBER_YELLOW              = 12;
    public static final int    PUSH1_COLOR2_YELLOW_HI                 = 13;
    public static final int    PUSH1_COLOR2_YELLOW                    = 14;
    public static final int    PUSH1_COLOR2_YELLOW_LO                 = 15;
    public static final int    PUSH1_COLOR2_YELLOW_LIME               = 16;
    public static final int    PUSH1_COLOR2_LIME_HI                   = 17;
    public static final int    PUSH1_COLOR2_LIME                      = 18;
    public static final int    PUSH1_COLOR2_LIME_LO                   = 19;
    public static final int    PUSH1_COLOR2_LIME_GREEN                = 20;
    public static final int    PUSH1_COLOR2_GREEN_HI                  = 21;
    public static final int    PUSH1_COLOR2_GREEN                     = 22;
    public static final int    PUSH1_COLOR2_GREEN_LO                  = 23;
    public static final int    PUSH1_COLOR2_GREEN_SPRING              = 24;
    public static final int    PUSH1_COLOR2_SPRING_HI                 = 25;
    public static final int    PUSH1_COLOR2_SPRING                    = 26;
    public static final int    PUSH1_COLOR2_SPRING_LO                 = 27;
    public static final int    PUSH1_COLOR2_SPRING_TURQUOISE          = 28;
    public static final int    PUSH1_COLOR2_TURQUOISE_LO              = 29;
    public static final int    PUSH1_COLOR2_TURQUOISE                 = 30;
    public static final int    PUSH1_COLOR2_TURQUOISE_HI              = 31;
    public static final int    PUSH1_COLOR2_TURQUOISE_CYAN            = 32;
    public static final int    PUSH1_COLOR2_CYAN_HI                   = 33;
    public static final int    PUSH1_COLOR2_CYAN                      = 34;
    public static final int    PUSH1_COLOR2_CYAN_LO                   = 35;
    public static final int    PUSH1_COLOR2_CYAN_SKY                  = 36;
    public static final int    PUSH1_COLOR2_SKY_HI                    = 37;
    public static final int    PUSH1_COLOR2_SKY                       = 38;
    public static final int    PUSH1_COLOR2_SKY_LO                    = 39;
    public static final int    PUSH1_COLOR2_SKY_OCEAN                 = 40;
    public static final int    PUSH1_COLOR2_OCEAN_HI                  = 41;
    public static final int    PUSH1_COLOR2_OCEAN                     = 42;
    public static final int    PUSH1_COLOR2_OCEAN_LO                  = 43;
    public static final int    PUSH1_COLOR2_OCEAN_BLUE                = 44;
    public static final int    PUSH1_COLOR2_BLUE_HI                   = 45;
    public static final int    PUSH1_COLOR2_BLUE                      = 46;
    public static final int    PUSH1_COLOR2_BLUE_LO                   = 47;
    public static final int    PUSH1_COLOR2_BLUE_ORCHID               = 48;
    public static final int    PUSH1_COLOR2_ORCHID_HI                 = 49;
    public static final int    PUSH1_COLOR2_ORCHID                    = 50;
    public static final int    PUSH1_COLOR2_ORCHID_LO                 = 51;
    public static final int    PUSH1_COLOR2_ORCHID_MAGENTA            = 52;
    public static final int    PUSH1_COLOR2_MAGENTA_HI                = 53;
    public static final int    PUSH1_COLOR2_MAGENTA                   = 54;
    public static final int    PUSH1_COLOR2_MAGENTA_LO                = 55;
    public static final int    PUSH1_COLOR2_MAGENTA_PINK              = 56;
    public static final int    PUSH1_COLOR2_PINK_HI                   = 57;
    public static final int    PUSH1_COLOR2_PINK                      = 58;
    public static final int    PUSH1_COLOR2_PINK_LO                   = 59;
    public static final int    PUSH1_COLOR2_SILVER                    = 118;
    public static final int    PUSH1_COLOR2_ORANGE                    = 65;
    public static final int    PUSH1_COLOR2_ORANGE_LIGHT              = 3;
    public static final int    PUSH1_COLOR2_LIGHT_BROWN               = 69;

    // Scene button colors
    public static final int    PUSH1_COLOR_SCENE_RED                  = 1;
    public static final int    PUSH1_COLOR_SCENE_RED_BLINK            = 2;
    public static final int    PUSH1_COLOR_SCENE_RED_BLINK_FAST       = 3;
    public static final int    PUSH1_COLOR_SCENE_RED_HI               = 4;
    public static final int    PUSH1_COLOR_SCENE_RED_HI_BLINK         = 5;
    public static final int    PUSH1_COLOR_SCENE_RED_HI_BLINK_FAST    = 6;
    public static final int    PUSH1_COLOR_SCENE_ORANGE               = 7;
    public static final int    PUSH1_COLOR_SCENE_ORANGE_BLINK         = 8;
    public static final int    PUSH1_COLOR_SCENE_ORANGE_BLINK_FAST    = 9;
    public static final int    PUSH1_COLOR_SCENE_ORANGE_HI            = 10;
    public static final int    PUSH1_COLOR_SCENE_ORANGE_HI_BLINK      = 11;
    public static final int    PUSH1_COLOR_SCENE_ORANGE_HI_BLINK_FAST = 12;
    public static final int    PUSH1_COLOR_SCENE_YELLOW               = 13;
    public static final int    PUSH1_COLOR_SCENE_YELLOW_BLINK         = 14;
    public static final int    PUSH1_COLOR_SCENE_YELLOW_BLINK_FAST    = 15;
    public static final int    PUSH1_COLOR_SCENE_YELLOW_HI            = 16;
    public static final int    PUSH1_COLOR_SCENE_YELLOW_HI_BLINK      = 17;
    public static final int    PUSH1_COLOR_SCENE_YELLOW_HI_BLINK_FAST = 18;
    public static final int    PUSH1_COLOR_SCENE_GREEN                = 19;
    public static final int    PUSH1_COLOR_SCENE_GREEN_BLINK          = 20;
    public static final int    PUSH1_COLOR_SCENE_GREEN_BLINK_FAST     = 21;
    public static final int    PUSH1_COLOR_SCENE_GREEN_HI             = 22;
    public static final int    PUSH1_COLOR_SCENE_GREEN_HI_BLINK       = 23;
    public static final int    PUSH1_COLOR_SCENE_GREEN_HI_BLINK_FAST  = 24;


    /**
     * Private due to utility class.
     */
    private PushColors ()
    {
        // Intentionally empty
    }


    /**
     * Configures all colors for Push controllers.
     *
     * @param colorManager The color manager
     * @param isPush2 True if Push 2
     */
    public static void addColors (final ColorManager colorManager, final boolean isPush2)
    {
        colorManager.registerColor (Scales.SCALE_COLOR_OFF, isPush2 ? PUSH2_COLOR2_BLACK : PUSH1_COLOR2_BLACK);
        colorManager.registerColor (Scales.SCALE_COLOR_OCTAVE, isPush2 ? PUSH2_COLOR2_OCEAN_HI : PUSH1_COLOR2_OCEAN_HI);
        colorManager.registerColor (Scales.SCALE_COLOR_NOTE, isPush2 ? PUSH2_COLOR2_WHITE : PUSH1_COLOR2_WHITE);
        colorManager.registerColor (Scales.SCALE_COLOR_OUT_OF_SCALE, isPush2 ? PUSH2_COLOR_BLACK : PUSH1_COLOR_BLACK);

        colorManager.registerColor (AbstractMode.BUTTON_COLOR_OFF, isPush2 ? PUSH2_COLOR_BLACK : PUSH1_COLOR_BLACK);
        colorManager.registerColor (AbstractMode.BUTTON_COLOR_ON, isPush2 ? PUSH2_COLOR2_GREY_LO : PUSH1_COLOR_GREEN_LO);
        colorManager.registerColor (AbstractMode.BUTTON_COLOR_HI, isPush2 ? PUSH2_COLOR2_WHITE : PUSH1_COLOR_YELLOW_MD);
        colorManager.registerColor (AbstractMode.BUTTON_COLOR2_ON, isPush2 ? PUSH2_COLOR2_GREY_LO : PUSH1_COLOR2_GREEN_LO);
        colorManager.registerColor (AbstractMode.BUTTON_COLOR2_HI, isPush2 ? PUSH2_COLOR2_WHITE : PUSH1_COLOR2_YELLOW_HI);

        colorManager.registerColor (AbstractSequencerView.COLOR_STEP_HILITE_NO_CONTENT, isPush2 ? PUSH2_COLOR2_GREEN_LO : PUSH1_COLOR2_GREEN_LO);
        colorManager.registerColor (AbstractSequencerView.COLOR_STEP_HILITE_CONTENT, isPush2 ? PUSH2_COLOR2_GREEN_HI : PUSH1_COLOR2_GREEN_HI);
        colorManager.registerColor (AbstractSequencerView.COLOR_NO_CONTENT, isPush2 ? PUSH2_COLOR2_BLACK : PUSH1_COLOR2_BLACK);
        colorManager.registerColor (AbstractSequencerView.COLOR_CONTENT, isPush2 ? PUSH2_COLOR2_BLUE_HI : PUSH1_COLOR2_BLUE_HI);
        colorManager.registerColor (AbstractSequencerView.COLOR_CONTENT_CONT, isPush2 ? PUSH2_COLOR2_BLUE_LO : PUSH1_COLOR2_BLUE_LO);
        colorManager.registerColor (AbstractSequencerView.COLOR_PAGE, isPush2 ? PUSH2_COLOR2_WHITE : PUSH1_COLOR2_WHITE);
        colorManager.registerColor (AbstractSequencerView.COLOR_ACTIVE_PAGE, isPush2 ? PUSH2_COLOR2_GREEN : PUSH1_COLOR2_GREEN);
        colorManager.registerColor (AbstractSequencerView.COLOR_SELECTED_PAGE, isPush2 ? PUSH2_COLOR2_OCEAN_HI : PUSH1_COLOR2_OCEAN_HI);
        colorManager.registerColor (AbstractSequencerView.COLOR_RESOLUTION, isPush2 ? PushColors.PUSH2_COLOR_SCENE_ORANGE : PushColors.PUSH1_COLOR_SCENE_ORANGE);
        colorManager.registerColor (AbstractSequencerView.COLOR_RESOLUTION_SELECTED, isPush2 ? PushColors.PUSH2_COLOR_SCENE_ORANGE_HI : PushColors.PUSH1_COLOR_SCENE_ORANGE_HI);
        colorManager.registerColor (AbstractSequencerView.COLOR_RESOLUTION_OFF, isPush2 ? PUSH2_COLOR2_BLACK : PUSH1_COLOR2_BLACK);
        colorManager.registerColor (AbstractSequencerView.COLOR_TRANSPOSE, isPush2 ? PushColors.PUSH2_COLOR_SCENE_WHITE : PushColors.PUSH1_COLOR_SCENE_YELLOW);
        colorManager.registerColor (AbstractSequencerView.COLOR_TRANSPOSE_SELECTED, isPush2 ? PushColors.PUSH2_COLOR_SCENE_YELLOW_HI : PushColors.PUSH1_COLOR_SCENE_YELLOW_HI);

        colorManager.registerColor (AbstractDrumView.COLOR_PAD_OFF, isPush2 ? PUSH2_COLOR_BLACK : PUSH1_COLOR_BLACK);
        colorManager.registerColor (AbstractDrumView.COLOR_PAD_RECORD, isPush2 ? PUSH2_COLOR2_RED_HI : PUSH1_COLOR2_RED_HI);
        colorManager.registerColor (AbstractDrumView.COLOR_PAD_PLAY, isPush2 ? PUSH2_COLOR2_GREEN_HI : PUSH1_COLOR2_GREEN_HI);
        colorManager.registerColor (AbstractDrumView.COLOR_PAD_SELECTED, isPush2 ? PUSH2_COLOR2_BLUE_HI : PUSH1_COLOR2_BLUE_HI);
        colorManager.registerColor (AbstractDrumView.COLOR_PAD_MUTED, isPush2 ? PUSH2_COLOR2_AMBER_LO : PUSH1_COLOR2_AMBER_LO);
        colorManager.registerColor (AbstractDrumView.COLOR_PAD_HAS_CONTENT, isPush2 ? PUSH2_COLOR2_YELLOW_HI : PUSH1_COLOR2_YELLOW_HI);
        colorManager.registerColor (AbstractDrumView.COLOR_PAD_NO_CONTENT, isPush2 ? PUSH2_COLOR2_YELLOW_LO : PUSH1_COLOR2_YELLOW_LO);

        colorManager.registerColor (AbstractPlayView.COLOR_PLAY, isPush2 ? PUSH2_COLOR2_GREEN_HI : PUSH1_COLOR2_GREEN_HI);
        colorManager.registerColor (AbstractPlayView.COLOR_RECORD, isPush2 ? PUSH2_COLOR2_RED_HI : PUSH1_COLOR2_RED_HI);
        colorManager.registerColor (AbstractPlayView.COLOR_OFF, isPush2 ? PUSH2_COLOR2_BLACK : PUSH1_COLOR2_BLACK);

        colorManager.registerColor (AbstractSessionView.COLOR_SCENE, isPush2 ? PushColors.PUSH2_COLOR_SCENE_GREEN : PushColors.PUSH1_COLOR_SCENE_GREEN);
        colorManager.registerColor (AbstractSessionView.COLOR_SELECTED_SCENE, isPush2 ? PushColors.PUSH2_COLOR_SCENE_GREEN_HI : PushColors.PUSH1_COLOR_SCENE_GREEN_HI);
        colorManager.registerColor (AbstractSessionView.COLOR_SCENE_OFF, isPush2 ? PUSH2_COLOR2_BLACK : PUSH1_COLOR2_BLACK);

        colorManager.registerColor (PadGrid.GRID_OFF, isPush2 ? PUSH2_COLOR2_BLACK : PUSH1_COLOR2_BLACK);

        // Push 2 DAW colors are set in the color palette from indices 70 to 96
        colorManager.registerColor (DAWColors.COLOR_OFF, isPush2 ? PUSH2_COLOR2_BLACK : PUSH1_COLOR2_BLACK);
        colorManager.registerColor (DAWColors.DAW_COLOR_GRAY_HALF, isPush2 ? 70 : PUSH1_COLOR2_GREY_MD);
        colorManager.registerColor (DAWColors.DAW_COLOR_DARK_GRAY, isPush2 ? 71 : 1);
        colorManager.registerColor (DAWColors.DAW_COLOR_GRAY, isPush2 ? 72 : PUSH1_COLOR2_GREY_MD);
        colorManager.registerColor (DAWColors.DAW_COLOR_LIGHT_GRAY, isPush2 ? 73 : PUSH1_COLOR2_GREY_LT);
        colorManager.registerColor (DAWColors.DAW_COLOR_SILVER, isPush2 ? 74 : 40);
        colorManager.registerColor (DAWColors.DAW_COLOR_DARK_BROWN, isPush2 ? 75 : 11);
        colorManager.registerColor (DAWColors.DAW_COLOR_BROWN, isPush2 ? 76 : 12);
        colorManager.registerColor (DAWColors.DAW_COLOR_DARK_BLUE, isPush2 ? 77 : 42);
        colorManager.registerColor (DAWColors.DAW_COLOR_PURPLE_BLUE, isPush2 ? 78 : 44);
        colorManager.registerColor (DAWColors.DAW_COLOR_PURPLE, isPush2 ? 79 : 81);
        colorManager.registerColor (DAWColors.DAW_COLOR_PINK, isPush2 ? 80 : 57);
        colorManager.registerColor (DAWColors.DAW_COLOR_RED, isPush2 ? 81 : 6);
        colorManager.registerColor (DAWColors.DAW_COLOR_ORANGE, isPush2 ? 82 : 60);
        colorManager.registerColor (DAWColors.DAW_COLOR_LIGHT_ORANGE, isPush2 ? 83 : 62);
        colorManager.registerColor (DAWColors.DAW_COLOR_MOSS_GREEN, isPush2 ? 84 : 19);
        colorManager.registerColor (DAWColors.DAW_COLOR_GREEN, isPush2 ? 85 : 26);
        colorManager.registerColor (DAWColors.DAW_COLOR_COLD_GREEN, isPush2 ? 86 : 30);
        colorManager.registerColor (DAWColors.DAW_COLOR_BLUE, isPush2 ? 87 : 37);
        colorManager.registerColor (DAWColors.DAW_COLOR_LIGHT_PURPLE, isPush2 ? 88 : 48);
        colorManager.registerColor (DAWColors.DAW_COLOR_LIGHT_PINK, isPush2 ? 89 : 56);
        colorManager.registerColor (DAWColors.DAW_COLOR_SKIN, isPush2 ? 90 : 4);
        colorManager.registerColor (DAWColors.DAW_COLOR_REDDISH_BROWN, isPush2 ? 91 : 10);
        colorManager.registerColor (DAWColors.DAW_COLOR_LIGHT_BROWN, isPush2 ? 92 : 61);
        colorManager.registerColor (DAWColors.DAW_COLOR_LIGHT_GREEN, isPush2 ? 93 : 18);
        colorManager.registerColor (DAWColors.DAW_COLOR_BLUISH_GREEN, isPush2 ? 94 : 25);
        colorManager.registerColor (DAWColors.DAW_COLOR_GREEN_BLUE, isPush2 ? 95 : 32);
        colorManager.registerColor (DAWColors.DAW_COLOR_LIGHT_BLUE, isPush2 ? 96 : 41);

        colorManager.registerColor (ColorManager.BUTTON_STATE_OFF, 0);
        colorManager.registerColor (ColorManager.BUTTON_STATE_ON, isPush2 ? 8 : 1);
        colorManager.registerColor (ColorManager.BUTTON_STATE_HI, isPush2 ? 127 : 4);
        colorManager.registerColor (PUSH_BUTTON_STATE_REC_ON, isPush2 ? PUSH2_COLOR2_GREY_LO : 1);
        colorManager.registerColor (PUSH_BUTTON_STATE_REC_HI, isPush2 ? PUSH2_COLOR2_RED_HI : 4);
        colorManager.registerColor (PUSH_BUTTON_STATE_OVR_ON, isPush2 ? PUSH2_COLOR2_GREY_LO : 1);
        colorManager.registerColor (PUSH_BUTTON_STATE_OVR_HI, isPush2 ? PUSH2_COLOR2_AMBER : 2);
        colorManager.registerColor (PUSH_BUTTON_STATE_PLAY_ON, isPush2 ? PUSH2_COLOR2_GREY_LO : 1);
        colorManager.registerColor (PUSH_BUTTON_STATE_PLAY_HI, isPush2 ? PUSH2_COLOR2_GREEN_HI : 4);
        colorManager.registerColor (PUSH_BUTTON_STATE_MUTE_ON, isPush2 ? PUSH2_COLOR2_GREY_LO : 1);
        colorManager.registerColor (PUSH_BUTTON_STATE_MUTE_HI, isPush2 ? PUSH2_COLOR2_AMBER_LO : 4);
        colorManager.registerColor (PUSH_BUTTON_STATE_SOLO_ON, isPush2 ? PUSH2_COLOR2_GREY_LO : 1);
        colorManager.registerColor (PUSH_BUTTON_STATE_SOLO_HI, isPush2 ? PUSH2_COLOR2_YELLOW : 4);
        colorManager.registerColor (PUSH_BUTTON_STATE_STOP_ON, isPush2 ? PUSH2_COLOR2_RED_LO : 1);
        colorManager.registerColor (PUSH_BUTTON_STATE_STOP_HI, isPush2 ? PUSH2_COLOR2_RED_HI : 4);
    }
}