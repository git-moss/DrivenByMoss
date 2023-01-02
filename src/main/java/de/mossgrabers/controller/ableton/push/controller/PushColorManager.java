// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.controller;

import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.color.ColorIndexException;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.featuregroup.AbstractFeatureGroup;
import de.mossgrabers.framework.featuregroup.AbstractMode;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.view.AbstractPlayView;
import de.mossgrabers.framework.view.AbstractSessionView;
import de.mossgrabers.framework.view.ScenePlayView;
import de.mossgrabers.framework.view.sequencer.AbstractDrumView;
import de.mossgrabers.framework.view.sequencer.AbstractSequencerView;


/**
 * Different colors to use for the pads and buttons of Push 1 and Push 2.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
@SuppressWarnings("javadoc")
public class PushColorManager extends ColorManager
{
    /** ID for color when button signals a recording state. */
    public static final String       PUSH_BUTTON_STATE_REC_ON               = "PUSH_BUTTON_STATE_REC_ON";
    /** ID for color when button signals an activated recording state. */
    public static final String       PUSH_BUTTON_STATE_REC_HI               = "PUSH_BUTTON_STATE_REC_HI";
    /** ID for color when button signals an overwrite state. */
    public static final String       PUSH_BUTTON_STATE_OVR_ON               = "PUSH_BUTTON_STATE_OVR_ON";
    /** ID for color when button signals an activated overwrite state. */
    public static final String       PUSH_BUTTON_STATE_OVR_HI               = "PUSH_BUTTON_STATE_OVR_HI";
    /** ID for color when button signals a play state. */
    public static final String       PUSH_BUTTON_STATE_PLAY_ON              = "PUSH_BUTTON_STATE_PLAY_ON";
    /** ID for color when button signals an activated play state. */
    public static final String       PUSH_BUTTON_STATE_PLAY_HI              = "PUSH_BUTTON_STATE_PLAY_HI";
    /** ID for color when button signals a mute state. */
    public static final String       PUSH_BUTTON_STATE_MUTE_ON              = "PUSH_BUTTON_STATE_MUTE_ON";
    /** ID for color when button signals an activated mute state. */
    public static final String       PUSH_BUTTON_STATE_MUTE_HI              = "PUSH_BUTTON_STATE_MUTE_HI";
    /** ID for color when button signals a solo state. */
    public static final String       PUSH_BUTTON_STATE_SOLO_ON              = "PUSH_BUTTON_STATE_SOLO_ON";
    /** ID for color when button signals an activated solo state. */
    public static final String       PUSH_BUTTON_STATE_SOLO_HI              = "PUSH_BUTTON_STATE_SOLO_HI";
    /** ID for color when button signals a stop clip state. */
    public static final String       PUSH_BUTTON_STATE_STOP_ON              = "PUSH_BUTTON_STATE_STOP_ON";
    /** ID for color when button signals an activated stop clip state. */
    public static final String       PUSH_BUTTON_STATE_STOP_HI              = "PUSH_BUTTON_STATE_STOP_HI";

    /** ID for the color to use for note repeat resolution. */
    public static final String       NOTE_REPEAT_PERIOD_OFF                 = "NOTE_REPEAT_PERIOD_OFF";
    /** ID for the color to use for note repeat resolution selected. */
    public static final String       NOTE_REPEAT_PERIOD_HI                  = "NOTE_REPEAT_PERIOD_HI";
    /** ID for the color to use for note repeat length. */
    public static final String       NOTE_REPEAT_LENGTH_OFF                 = "NOTE_REPEAT_LENGTH_OFF";
    /** ID for the color to use for note repeat length selected. */
    public static final String       NOTE_REPEAT_LENGTH_HI                  = "NOTE_REPEAT_LENGTH_HI";

    // @formatter:off
    /** The default color palette (like fixed on Push 1) */
    protected static final int [] [] DEFAULT_PALETTE                        =
    {
        { 0x00, 0x00, 0x00 },
        { 0x1E, 0x1E, 0x1E },
        { 0x7F, 0x7F, 0x7F },
        { 0xFF, 0xFF, 0xFF },
        { 0xFF, 0x4C, 0x4C },
        { 0xFF, 0x00, 0x00 },
        { 0x59, 0x00, 0x00 },
        { 0x19, 0x00, 0x00 },
        { 0xFF, 0xBD, 0x6C },
        { 0xFF, 0x54, 0x00 },
        { 0x59, 0x1D, 0x00 },
        { 0x27, 0x1B, 0x00 },
        { 0xFF, 0xFF, 0x4C },
        { 0xFF, 0xFF, 0x00 },
        { 0x59, 0x59, 0x00 },
        { 0x19, 0x19, 0x00 },
        { 0x88, 0xFF, 0x4C },
        { 0x54, 0xFF, 0x00 },
        { 0x1D, 0x59, 0x00 },
        { 0x14, 0x2B, 0x00 },
        { 0x4C, 0xFF, 0x4C },
        { 0x00, 0xFF, 0x00 },
        { 0x00, 0x59, 0x00 },
        { 0x00, 0x19, 0x00 },
        { 0x4C, 0xFF, 0x5E },
        { 0x00, 0xFF, 0x19 },
        { 0x00, 0x59, 0x0D },
        { 0x00, 0x19, 0x02 },
        { 0x4C, 0xFF, 0x88 },
        { 0x00, 0xFF, 0x55 },
        { 0x00, 0x59, 0x1D },
        { 0x00, 0x1F, 0x12 },
        { 0x4C, 0xFF, 0xB7 },
        { 0x00, 0xFF, 0x99 },
        { 0x00, 0x59, 0x35 },
        { 0x00, 0x19, 0x12 },
        { 0x4C, 0xC3, 0xFF },
        { 0x00, 0xA9, 0xFF },
        { 0x00, 0x41, 0x52 },
        { 0x00, 0x10, 0x19 },
        { 0x4C, 0x88, 0xFF },
        { 0x00, 0x55, 0xFF },
        { 0x00, 0x1D, 0x59 },
        { 0x00, 0x08, 0x19 },
        { 0x4C, 0x4C, 0xFF },
        { 0x00, 0x00, 0xFF },
        { 0x00, 0x00, 0x59 },
        { 0x00, 0x00, 0x19 },
        { 0x87, 0x4C, 0xFF },
        { 0x54, 0x00, 0xFF },
        { 0x19, 0x00, 0x64 },
        { 0x0F, 0x00, 0x30 },
        { 0xFF, 0x4C, 0xFF },
        { 0xFF, 0x00, 0xFF },
        { 0x59, 0x00, 0x59 },
        { 0x19, 0x00, 0x19 },
        { 0xFF, 0x4C, 0x87 },
        { 0xFF, 0x00, 0x54 },
        { 0x59, 0x00, 0x1D },
        { 0x22, 0x00, 0x13 },
        { 0xFF, 0x15, 0x00 },
        { 0x99, 0x35, 0x00 },
        { 0x79, 0x51, 0x00 },
        { 0x43, 0x64, 0x00 },
        { 0x03, 0x39, 0x00 },
        { 0x00, 0x57, 0x35 },
        { 0x00, 0x54, 0x7F },
        { 0x00, 0x00, 0xFF },
        { 0x00, 0x45, 0x4F },
        { 0x25, 0x00, 0xCC },
        { 0x7F, 0x7F, 0x7F },
        { 0x20, 0x20, 0x20 },
        { 0xFF, 0x00, 0x00 },
        { 0xBD, 0xFF, 0x2D },
        { 0xAF, 0xED, 0x06 },
        { 0x64, 0xFF, 0x09 },
        { 0x10, 0x8B, 0x00 },
        { 0x00, 0xFF, 0x87 },
        { 0x00, 0xA9, 0xFF },
        { 0x00, 0x2A, 0xFF },
        { 0x3F, 0x00, 0xFF },
        { 0x7A, 0x00, 0xFF },
        { 0xB2, 0x1A, 0x7D },
        { 0x40, 0x21, 0x00 },
        { 0xFF, 0x4A, 0x00 },
        { 0x88, 0xE1, 0x06 },
        { 0x72, 0xFF, 0x15 },
        { 0x00, 0xFF, 0x00 },
        { 0x3B, 0xFF, 0x26 },
        { 0x59, 0xFF, 0x71 },
        { 0x38, 0xFF, 0xCC },
        { 0x5B, 0x8A, 0xFF },
        { 0x31, 0x51, 0xC6 },
        { 0x87, 0x7F, 0xE9 },
        { 0xD3, 0x1D, 0xFF },
        { 0xFF, 0x00, 0x5D },
        { 0xFF, 0x7F, 0x00 },
        { 0xB9, 0xB0, 0x00 },
        { 0x90, 0xFF, 0x00 },
        { 0x83, 0x5D, 0x07 },
        { 0x39, 0x2B, 0x00 },
        { 0x14, 0x4C, 0x10 },
        { 0x0D, 0x50, 0x38 },
        { 0x15, 0x15, 0x2A },
        { 0x16, 0x20, 0x5A },
        { 0x69, 0x3C, 0x1C },
        { 0xA8, 0x00, 0x0A },
        { 0xDE, 0x51, 0x3D },
        { 0xD8, 0x6A, 0x1C },
        { 0xFF, 0xE1, 0x26 },
        { 0x9E, 0xE1, 0x2F },
        { 0x67, 0xB5, 0x0F },
        { 0x1E, 0x1E, 0x30 },
        { 0xDC, 0xFF, 0x6B },
        { 0x80, 0xFF, 0xBD },
        { 0x9A, 0x99, 0xFF },
        { 0x8E, 0x66, 0xFF },
        { 0x40, 0x40, 0x40 },
        { 0x75, 0x75, 0x75 },
        { 0xE0, 0xFF, 0xFF },
        { 0xA0, 0x00, 0x00 },
        { 0x35, 0x00, 0x00 },
        { 0x1A, 0xD0, 0x00 },
        { 0x07, 0x42, 0x00 },
        { 0xB9, 0xB0, 0x00 },
        { 0x3F, 0x31, 0x00 },
        { 0xB3, 0x5F, 0x00 },
        { 0x4B, 0x15, 0x02 }
    };
    // @formatter:on

    // Second row & Pad button colors
    public static final int          PUSH2_COLOR2_BLACK                     = 0;
    public static final int          PUSH2_COLOR2_GREY_LO                   = 1;
    public static final int          PUSH2_COLOR2_GREY_MD                   = 103;
    public static final int          PUSH2_COLOR2_GREY_LT                   = 2;
    public static final int          PUSH2_COLOR2_WHITE                     = 3;
    public static final int          PUSH2_COLOR2_ROSE                      = 4;
    public static final int          PUSH2_COLOR2_RED_HI                    = 5;
    public static final int          PUSH2_COLOR2_RED                       = 6;
    public static final int          PUSH2_COLOR2_RED_LO                    = 7;
    public static final int          PUSH2_COLOR2_RED_AMBER                 = 8;
    public static final int          PUSH2_COLOR2_AMBER_HI                  = 9;
    public static final int          PUSH2_COLOR2_AMBER                     = 10;
    public static final int          PUSH2_COLOR2_AMBER_LO                  = 11;
    public static final int          PUSH2_COLOR2_AMBER_YELLOW              = 12;
    public static final int          PUSH2_COLOR2_YELLOW_HI                 = 13;
    public static final int          PUSH2_COLOR2_YELLOW                    = 14;
    public static final int          PUSH2_COLOR2_YELLOW_LO                 = 15;
    public static final int          PUSH2_COLOR2_YELLOW_LIME               = 16;
    public static final int          PUSH2_COLOR2_LIME_HI                   = 17;
    public static final int          PUSH2_COLOR2_LIME                      = 18;
    public static final int          PUSH2_COLOR2_LIME_LO                   = 19;
    public static final int          PUSH2_COLOR2_LIME_GREEN                = 20;
    public static final int          PUSH2_COLOR2_GREEN_HI                  = 21;
    public static final int          PUSH2_COLOR2_GREEN                     = 22;
    public static final int          PUSH2_COLOR2_GREEN_LO                  = 23;
    public static final int          PUSH2_COLOR2_GREEN_SPRING              = 24;
    public static final int          PUSH2_COLOR2_SPRING_HI                 = 25;
    public static final int          PUSH2_COLOR2_SPRING                    = 26;
    public static final int          PUSH2_COLOR2_SPRING_LO                 = 27;
    public static final int          PUSH2_COLOR2_SPRING_TURQUOISE          = 28;
    public static final int          PUSH2_COLOR2_TURQUOISE_LO              = 29;
    public static final int          PUSH2_COLOR2_TURQUOISE                 = 30;
    public static final int          PUSH2_COLOR2_TURQUOISE_HI              = 31;
    public static final int          PUSH2_COLOR2_TURQUOISE_CYAN            = 32;
    public static final int          PUSH2_COLOR2_CYAN_HI                   = 33;
    public static final int          PUSH2_COLOR2_CYAN                      = 34;
    public static final int          PUSH2_COLOR2_CYAN_LO                   = 35;
    public static final int          PUSH2_COLOR2_CYAN_SKY                  = 36;
    public static final int          PUSH2_COLOR2_SKY_HI                    = 37;
    public static final int          PUSH2_COLOR2_SKY                       = 38;
    public static final int          PUSH2_COLOR2_SKY_LO                    = 39;
    public static final int          PUSH2_COLOR2_SKY_OCEAN                 = 40;
    public static final int          PUSH2_COLOR2_OCEAN_HI                  = 41;
    public static final int          PUSH2_COLOR2_OCEAN                     = 42;
    public static final int          PUSH2_COLOR2_OCEAN_LO                  = 43;
    public static final int          PUSH2_COLOR2_OCEAN_BLUE                = 44;
    public static final int          PUSH2_COLOR2_BLUE_HI                   = 45;
    public static final int          PUSH2_COLOR2_BLUE                      = 46;
    public static final int          PUSH2_COLOR2_BLUE_LO                   = 47;
    public static final int          PUSH2_COLOR2_BLUE_ORCHID               = 48;
    public static final int          PUSH2_COLOR2_ORCHID_HI                 = 49;
    public static final int          PUSH2_COLOR2_ORCHID                    = 50;
    public static final int          PUSH2_COLOR2_ORCHID_LO                 = 51;
    public static final int          PUSH2_COLOR2_ORCHID_MAGENTA            = 52;
    public static final int          PUSH2_COLOR2_MAGENTA_HI                = 53;
    public static final int          PUSH2_COLOR2_MAGENTA                   = 54;
    public static final int          PUSH2_COLOR2_MAGENTA_LO                = 55;
    public static final int          PUSH2_COLOR2_MAGENTA_PINK              = 56;
    public static final int          PUSH2_COLOR2_PINK_HI                   = 57;
    public static final int          PUSH2_COLOR2_PINK                      = 58;
    public static final int          PUSH2_COLOR2_PINK_LO                   = 59;
    public static final int          PUSH2_COLOR2_SILVER                    = 118;
    public static final int          PUSH2_COLOR2_ORANGE                    = 65;
    public static final int          PUSH2_COLOR2_ORANGE_LIGHT              = 3;
    public static final int          PUSH2_COLOR2_LIGHT_BROWN               = 69;

    // First row colors
    public static final int          PUSH2_COLOR_BLACK                      = 0;
    public static final int          PUSH2_COLOR_RED_LO                     = PUSH2_COLOR2_RED_LO;
    public static final int          PUSH2_COLOR_RED_LO_SBLINK              = 2;
    public static final int          PUSH2_COLOR_RED_LO_FBLINK              = 3;
    public static final int          PUSH2_COLOR_RED_HI                     = PUSH2_COLOR2_RED_HI;
    public static final int          PUSH2_COLOR_RED_HI_SBLINK              = 5;
    public static final int          PUSH2_COLOR_RED_HI_FBLINK              = 6;
    public static final int          PUSH2_COLOR_ORANGE_LO                  = PUSH2_COLOR2_AMBER_LO;
    public static final int          PUSH2_COLOR_ORANGE_LO_SBLINK           = 8;
    public static final int          PUSH2_COLOR_ORANGE_LO_FBLINK           = 9;
    public static final int          PUSH2_COLOR_ORANGE_HI                  = PUSH2_COLOR2_AMBER_HI;
    public static final int          PUSH2_COLOR_ORANGE_HI_SBLINK           = 11;
    public static final int          PUSH2_COLOR_ORANGE_HI_FBLINK           = 12;
    public static final int          PUSH2_COLOR_YELLOW_LO                  = PUSH2_COLOR2_YELLOW_LO;
    public static final int          PUSH2_COLOR_YELLOW_LO_SBLINK           = 14;
    public static final int          PUSH2_COLOR_YELLOW_LO_FBLINK           = 15;
    public static final int          PUSH2_COLOR_YELLOW_MD                  = PUSH2_COLOR2_YELLOW_HI;
    public static final int          PUSH2_COLOR_YELLOW_MD_SBLINK           = 17;
    public static final int          PUSH2_COLOR_YELLOW_MD_FBLINK           = 18;
    public static final int          PUSH2_COLOR_GREEN_LO                   = PUSH2_COLOR2_GREEN_LO;
    public static final int          PUSH2_COLOR_GREEN_LO_SBLINK            = 20;
    public static final int          PUSH2_COLOR_GREEN_LO_FBLINK            = 21;
    public static final int          PUSH2_COLOR_GREEN_HI                   = PUSH2_COLOR2_GREEN_HI;
    public static final int          PUSH2_COLOR_GREEN_HI_SBLINK            = 23;
    public static final int          PUSH2_COLOR_GREEN_HI_FBLINK            = 24;

    // Scene button colors
    public static final int          PUSH2_COLOR_SCENE_RED                  = PUSH2_COLOR2_RED;
    public static final int          PUSH2_COLOR_SCENE_RED_BLINK            = 2;
    public static final int          PUSH2_COLOR_SCENE_RED_BLINK_FAST       = 3;
    public static final int          PUSH2_COLOR_SCENE_RED_HI               = PUSH2_COLOR2_RED_HI;
    public static final int          PUSH2_COLOR_SCENE_RED_HI_BLINK         = 5;
    public static final int          PUSH2_COLOR_SCENE_RED_HI_BLINK_FAST    = 6;
    public static final int          PUSH2_COLOR_SCENE_ORANGE               = PUSH2_COLOR2_AMBER;
    public static final int          PUSH2_COLOR_SCENE_ORANGE_BLINK         = 8;
    public static final int          PUSH2_COLOR_SCENE_ORANGE_BLINK_FAST    = 9;
    public static final int          PUSH2_COLOR_SCENE_ORANGE_HI            = PUSH2_COLOR2_AMBER_HI;
    public static final int          PUSH2_COLOR_SCENE_ORANGE_HI_BLINK      = 11;
    public static final int          PUSH2_COLOR_SCENE_ORANGE_HI_BLINK_FAST = 12;
    public static final int          PUSH2_COLOR_SCENE_YELLOW               = PUSH2_COLOR2_YELLOW;
    public static final int          PUSH2_COLOR_SCENE_YELLOW_BLINK         = 14;
    public static final int          PUSH2_COLOR_SCENE_YELLOW_BLINK_FAST    = 15;
    public static final int          PUSH2_COLOR_SCENE_YELLOW_HI            = PUSH2_COLOR2_YELLOW_HI;
    public static final int          PUSH2_COLOR_SCENE_YELLOW_HI_BLINK      = 17;
    public static final int          PUSH2_COLOR_SCENE_YELLOW_HI_BLINK_FAST = 18;
    public static final int          PUSH2_COLOR_SCENE_GREEN                = PUSH2_COLOR2_GREEN;
    public static final int          PUSH2_COLOR_SCENE_GREEN_BLINK          = 20;
    public static final int          PUSH2_COLOR_SCENE_GREEN_BLINK_FAST     = 21;
    public static final int          PUSH2_COLOR_SCENE_GREEN_HI             = PUSH2_COLOR2_GREEN_HI;
    public static final int          PUSH2_COLOR_SCENE_GREEN_HI_BLINK       = 23;
    public static final int          PUSH2_COLOR_SCENE_GREEN_HI_BLINK_FAST  = 24;
    public static final int          PUSH2_COLOR_SCENE_WHITE                = 60;

    // First row colors
    public static final int          PUSH1_COLOR_BLACK                      = 0;
    public static final int          PUSH1_COLOR_RED_LO                     = 1;
    public static final int          PUSH1_COLOR_RED_LO_SBLINK              = 2;
    public static final int          PUSH1_COLOR_RED_LO_FBLINK              = 3;
    public static final int          PUSH1_COLOR_RED_HI                     = 4;
    public static final int          PUSH1_COLOR_RED_HI_SBLINK              = 5;
    public static final int          PUSH1_COLOR_RED_HI_FBLINK              = 6;
    public static final int          PUSH1_COLOR_ORANGE_LO                  = 7;
    public static final int          PUSH1_COLOR_ORANGE_LO_SBLINK           = 8;
    public static final int          PUSH1_COLOR_ORANGE_LO_FBLINK           = 9;
    public static final int          PUSH1_COLOR_ORANGE_HI                  = 10;
    public static final int          PUSH1_COLOR_ORANGE_HI_SBLINK           = 11;
    public static final int          PUSH1_COLOR_ORANGE_HI_FBLINK           = 12;
    public static final int          PUSH1_COLOR_YELLOW_LO                  = 13;
    public static final int          PUSH1_COLOR_YELLOW_LO_SBLINK           = 14;
    public static final int          PUSH1_COLOR_YELLOW_LO_FBLINK           = 15;
    public static final int          PUSH1_COLOR_YELLOW_MD                  = 16;
    public static final int          PUSH1_COLOR_YELLOW_MD_SBLINK           = 17;
    public static final int          PUSH1_COLOR_YELLOW_MD_FBLINK           = 18;
    public static final int          PUSH1_COLOR_GREEN_LO                   = 19;
    public static final int          PUSH1_COLOR_GREEN_LO_SBLINK            = 20;
    public static final int          PUSH1_COLOR_GREEN_LO_FBLINK            = 21;
    public static final int          PUSH1_COLOR_GREEN_HI                   = 22;
    public static final int          PUSH1_COLOR_GREEN_HI_SBLINK            = 23;
    public static final int          PUSH1_COLOR_GREEN_HI_FBLINK            = 24;

    // Second row & Pad button colors
    public static final int          PUSH1_COLOR2_BLACK                     = 0;
    public static final int          PUSH1_COLOR2_GREY_LO                   = 1;
    public static final int          PUSH1_COLOR2_GREY_MD                   = 103;
    public static final int          PUSH1_COLOR2_GREY_LT                   = 2;
    public static final int          PUSH1_COLOR2_WHITE                     = 3;
    public static final int          PUSH1_COLOR2_ROSE                      = 4;
    public static final int          PUSH1_COLOR2_RED_HI                    = 5;
    public static final int          PUSH1_COLOR2_RED                       = 6;
    public static final int          PUSH1_COLOR2_RED_LO                    = 7;
    public static final int          PUSH1_COLOR2_RED_AMBER                 = 8;
    public static final int          PUSH1_COLOR2_AMBER_HI                  = 9;
    public static final int          PUSH1_COLOR2_AMBER                     = 10;
    public static final int          PUSH1_COLOR2_AMBER_LO                  = 11;
    public static final int          PUSH1_COLOR2_AMBER_YELLOW              = 12;
    public static final int          PUSH1_COLOR2_YELLOW_HI                 = 13;
    public static final int          PUSH1_COLOR2_YELLOW                    = 14;
    public static final int          PUSH1_COLOR2_YELLOW_LO                 = 15;
    public static final int          PUSH1_COLOR2_YELLOW_LIME               = 16;
    public static final int          PUSH1_COLOR2_LIME_HI                   = 17;
    public static final int          PUSH1_COLOR2_LIME                      = 18;
    public static final int          PUSH1_COLOR2_LIME_LO                   = 19;
    public static final int          PUSH1_COLOR2_LIME_GREEN                = 20;
    public static final int          PUSH1_COLOR2_GREEN_HI                  = 21;
    public static final int          PUSH1_COLOR2_GREEN                     = 22;
    public static final int          PUSH1_COLOR2_GREEN_LO                  = 23;
    public static final int          PUSH1_COLOR2_GREEN_SPRING              = 24;
    public static final int          PUSH1_COLOR2_SPRING_HI                 = 25;
    public static final int          PUSH1_COLOR2_SPRING                    = 26;
    public static final int          PUSH1_COLOR2_SPRING_LO                 = 27;
    public static final int          PUSH1_COLOR2_SPRING_TURQUOISE          = 28;
    public static final int          PUSH1_COLOR2_TURQUOISE_LO              = 29;
    public static final int          PUSH1_COLOR2_TURQUOISE                 = 30;
    public static final int          PUSH1_COLOR2_TURQUOISE_HI              = 31;
    public static final int          PUSH1_COLOR2_TURQUOISE_CYAN            = 32;
    public static final int          PUSH1_COLOR2_CYAN_HI                   = 33;
    public static final int          PUSH1_COLOR2_CYAN                      = 34;
    public static final int          PUSH1_COLOR2_CYAN_LO                   = 35;
    public static final int          PUSH1_COLOR2_CYAN_SKY                  = 36;
    public static final int          PUSH1_COLOR2_SKY_HI                    = 37;
    public static final int          PUSH1_COLOR2_SKY                       = 38;
    public static final int          PUSH1_COLOR2_SKY_LO                    = 39;
    public static final int          PUSH1_COLOR2_SKY_OCEAN                 = 40;
    public static final int          PUSH1_COLOR2_OCEAN_HI                  = 41;
    public static final int          PUSH1_COLOR2_OCEAN                     = 42;
    public static final int          PUSH1_COLOR2_OCEAN_LO                  = 43;
    public static final int          PUSH1_COLOR2_OCEAN_BLUE                = 44;
    public static final int          PUSH1_COLOR2_BLUE_HI                   = 45;
    public static final int          PUSH1_COLOR2_BLUE                      = 46;
    public static final int          PUSH1_COLOR2_BLUE_LO                   = 47;
    public static final int          PUSH1_COLOR2_BLUE_ORCHID               = 48;
    public static final int          PUSH1_COLOR2_ORCHID_HI                 = 49;
    public static final int          PUSH1_COLOR2_ORCHID                    = 50;
    public static final int          PUSH1_COLOR2_ORCHID_LO                 = 51;
    public static final int          PUSH1_COLOR2_ORCHID_MAGENTA            = 52;
    public static final int          PUSH1_COLOR2_MAGENTA_HI                = 53;
    public static final int          PUSH1_COLOR2_MAGENTA                   = 54;
    public static final int          PUSH1_COLOR2_MAGENTA_LO                = 55;
    public static final int          PUSH1_COLOR2_MAGENTA_PINK              = 56;
    public static final int          PUSH1_COLOR2_PINK_HI                   = 57;
    public static final int          PUSH1_COLOR2_PINK                      = 58;
    public static final int          PUSH1_COLOR2_PINK_LO                   = 59;
    public static final int          PUSH1_COLOR2_SILVER                    = 118;
    public static final int          PUSH1_COLOR2_ORANGE                    = 65;
    public static final int          PUSH1_COLOR2_ORANGE_LIGHT              = 3;
    public static final int          PUSH1_COLOR2_LIGHT_BROWN               = 69;

    // Scene button colors
    public static final int          PUSH1_COLOR_SCENE_RED                  = 1;
    public static final int          PUSH1_COLOR_SCENE_RED_BLINK            = 2;
    public static final int          PUSH1_COLOR_SCENE_RED_BLINK_FAST       = 3;
    public static final int          PUSH1_COLOR_SCENE_RED_HI               = 4;
    public static final int          PUSH1_COLOR_SCENE_RED_HI_BLINK         = 5;
    public static final int          PUSH1_COLOR_SCENE_RED_HI_BLINK_FAST    = 6;
    public static final int          PUSH1_COLOR_SCENE_ORANGE               = 7;
    public static final int          PUSH1_COLOR_SCENE_ORANGE_BLINK         = 8;
    public static final int          PUSH1_COLOR_SCENE_ORANGE_BLINK_FAST    = 9;
    public static final int          PUSH1_COLOR_SCENE_ORANGE_HI            = 10;
    public static final int          PUSH1_COLOR_SCENE_ORANGE_HI_BLINK      = 11;
    public static final int          PUSH1_COLOR_SCENE_ORANGE_HI_BLINK_FAST = 12;
    public static final int          PUSH1_COLOR_SCENE_YELLOW               = 13;
    public static final int          PUSH1_COLOR_SCENE_YELLOW_BLINK         = 14;
    public static final int          PUSH1_COLOR_SCENE_YELLOW_BLINK_FAST    = 15;
    public static final int          PUSH1_COLOR_SCENE_YELLOW_HI            = 16;
    public static final int          PUSH1_COLOR_SCENE_YELLOW_HI_BLINK      = 17;
    public static final int          PUSH1_COLOR_SCENE_YELLOW_HI_BLINK_FAST = 18;
    public static final int          PUSH1_COLOR_SCENE_GREEN                = 19;
    public static final int          PUSH1_COLOR_SCENE_GREEN_BLINK          = 20;
    public static final int          PUSH1_COLOR_SCENE_GREEN_BLINK_FAST     = 21;
    public static final int          PUSH1_COLOR_SCENE_GREEN_HI             = 22;
    public static final int          PUSH1_COLOR_SCENE_GREEN_HI_BLINK       = 23;
    public static final int          PUSH1_COLOR_SCENE_GREEN_HI_BLINK_FAST  = 24;

    public static final String       PUSH_BLACK                             = "PUSH_BLACK";
    public static final String       PUSH_RED                               = "PUSH_RED";
    public static final String       PUSH_RED_LO                            = "PUSH_RED_LO";
    public static final String       PUSH_RED_HI                            = "PUSH_RED_HI";
    public static final String       PUSH_ORANGE_LO                         = "PUSH_ORANGE_LO";
    public static final String       PUSH_ORANGE_HI                         = "PUSH_ORANGE_HI";
    public static final String       PUSH_YELLOW_LO                         = "PUSH_YELLOW_LO";
    public static final String       PUSH_YELLOW_MD                         = "PUSH_YELLOW_MD";
    public static final String       PUSH_GREEN_LO                          = "PUSH_GREEN_LO";
    public static final String       PUSH_GREEN_HI                          = "PUSH_GREEN_HI";

    public static final String       PUSH_BLACK_2                           = "PUSH_BLACK_2";
    public static final String       PUSH_WHITE_2                           = "PUSH_WHITE_2";
    public static final String       PUSH_GREY_LO_2                         = "PUSH_GREY_LO_2";
    public static final String       PUSH_GREEN_2                           = "PUSH_GREEN_2";

    private final boolean            isPush2;


    /**
     * Private due to utility class.
     *
     * @param isPush2 True if Push 2
     */
    public PushColorManager (final boolean isPush2)
    {
        this.isPush2 = isPush2;

        this.registerColorIndex (PUSH_BLACK, this.isPush2 ? PushColorManager.PUSH2_COLOR_BLACK : PushColorManager.PUSH1_COLOR_BLACK);
        this.registerColorIndex (PUSH_RED, this.isPush2 ? PushColorManager.PUSH2_COLOR_RED_HI : PushColorManager.PUSH1_COLOR_RED_HI);
        this.registerColorIndex (PUSH_RED_LO, this.isPush2 ? PushColorManager.PUSH2_COLOR_RED_LO : PushColorManager.PUSH1_COLOR_RED_LO);
        this.registerColorIndex (PUSH_RED_HI, this.isPush2 ? PushColorManager.PUSH2_COLOR_RED_HI : PushColorManager.PUSH1_COLOR_RED_HI);
        this.registerColorIndex (PUSH_ORANGE_LO, this.isPush2 ? PushColorManager.PUSH2_COLOR_ORANGE_LO : PushColorManager.PUSH1_COLOR_ORANGE_LO);
        this.registerColorIndex (PUSH_ORANGE_HI, this.isPush2 ? PushColorManager.PUSH2_COLOR_ORANGE_HI : PushColorManager.PUSH1_COLOR_ORANGE_HI);
        this.registerColorIndex (PUSH_YELLOW_LO, this.isPush2 ? PushColorManager.PUSH2_COLOR_YELLOW_LO : PushColorManager.PUSH1_COLOR_YELLOW_LO);
        this.registerColorIndex (PUSH_YELLOW_MD, this.isPush2 ? PushColorManager.PUSH2_COLOR_YELLOW_MD : PushColorManager.PUSH1_COLOR_YELLOW_MD);
        this.registerColorIndex (PUSH_GREEN_LO, this.isPush2 ? PushColorManager.PUSH2_COLOR_GREEN_LO : PushColorManager.PUSH1_COLOR_GREEN_LO);
        this.registerColorIndex (PUSH_GREEN_HI, this.isPush2 ? PushColorManager.PUSH2_COLOR_GREEN_HI : PushColorManager.PUSH1_COLOR_GREEN_HI);

        this.registerColorIndex (PUSH_BLACK_2, this.isPush2 ? PushColorManager.PUSH2_COLOR2_BLACK : PushColorManager.PUSH1_COLOR2_BLACK);
        this.registerColorIndex (PUSH_WHITE_2, this.isPush2 ? PushColorManager.PUSH2_COLOR2_WHITE : PushColorManager.PUSH1_COLOR2_WHITE);
        this.registerColorIndex (PUSH_GREY_LO_2, this.isPush2 ? PushColorManager.PUSH2_COLOR2_GREY_LO : PushColorManager.PUSH1_COLOR2_GREY_LO);
        this.registerColorIndex (PUSH_GREEN_2, this.isPush2 ? PushColorManager.PUSH2_COLOR2_GREEN : PushColorManager.PUSH1_COLOR2_GREEN);

        this.registerColorIndex (Scales.SCALE_COLOR_OFF, isPush2 ? PUSH2_COLOR2_BLACK : PUSH1_COLOR2_BLACK);
        this.registerColorIndex (Scales.SCALE_COLOR_OCTAVE, isPush2 ? PUSH2_COLOR2_OCEAN_HI : PUSH1_COLOR2_OCEAN_HI);
        this.registerColorIndex (Scales.SCALE_COLOR_NOTE, isPush2 ? PUSH2_COLOR2_WHITE : PUSH1_COLOR2_WHITE);
        this.registerColorIndex (Scales.SCALE_COLOR_OUT_OF_SCALE, isPush2 ? PUSH2_COLOR_BLACK : PUSH1_COLOR_BLACK);

        this.registerColorIndex (AbstractFeatureGroup.BUTTON_COLOR_OFF, isPush2 ? PUSH2_COLOR_BLACK : PUSH1_COLOR_BLACK);
        this.registerColorIndex (AbstractFeatureGroup.BUTTON_COLOR_ON, isPush2 ? PUSH2_COLOR2_GREY_LO : PUSH1_COLOR_GREEN_LO);
        this.registerColorIndex (AbstractMode.BUTTON_COLOR_HI, isPush2 ? PUSH2_COLOR2_WHITE : PUSH1_COLOR_YELLOW_MD);
        this.registerColorIndex (AbstractMode.BUTTON_COLOR2_ON, isPush2 ? PUSH2_COLOR2_GREY_LO : PUSH1_COLOR2_GREEN_LO);
        this.registerColorIndex (AbstractMode.BUTTON_COLOR2_HI, isPush2 ? PUSH2_COLOR2_WHITE : PUSH1_COLOR2_YELLOW_HI);

        this.registerColorIndex (AbstractSequencerView.COLOR_NO_CONTENT, isPush2 ? PUSH2_COLOR2_BLACK : PUSH1_COLOR2_BLACK);
        this.registerColorIndex (AbstractSequencerView.COLOR_NO_CONTENT_4, isPush2 ? PUSH2_COLOR2_BLACK : PUSH1_COLOR2_BLACK);
        this.registerColorIndex (AbstractSequencerView.COLOR_CONTENT, isPush2 ? PUSH2_COLOR2_BLUE_HI : PUSH1_COLOR2_BLUE_HI);
        this.registerColorIndex (AbstractSequencerView.COLOR_CONTENT_CONT, isPush2 ? PUSH2_COLOR2_BLUE_LO : PUSH1_COLOR2_BLUE_LO);
        this.registerColorIndex (AbstractSequencerView.COLOR_STEP_HILITE_NO_CONTENT, isPush2 ? PUSH2_COLOR2_GREEN_LO : PUSH1_COLOR2_GREEN_LO);
        this.registerColorIndex (AbstractSequencerView.COLOR_STEP_HILITE_CONTENT, isPush2 ? PUSH2_COLOR2_GREEN_HI : PUSH1_COLOR2_GREEN_HI);
        this.registerColorIndex (AbstractSequencerView.COLOR_STEP_MUTED, isPush2 ? PUSH2_COLOR2_GREY_MD : PUSH1_COLOR2_GREY_MD);
        this.registerColorIndex (AbstractSequencerView.COLOR_STEP_MUTED_CONT, isPush2 ? PUSH2_COLOR2_GREY_LO : PUSH1_COLOR2_GREY_LO);
        this.registerColorIndex (AbstractSequencerView.COLOR_STEP_SELECTED, isPush2 ? PUSH2_COLOR2_YELLOW_HI : PUSH1_COLOR2_YELLOW_HI);
        this.registerColorIndex (AbstractSequencerView.COLOR_PAGE, isPush2 ? PUSH2_COLOR2_WHITE : PUSH1_COLOR2_WHITE);
        this.registerColorIndex (AbstractSequencerView.COLOR_ACTIVE_PAGE, isPush2 ? PUSH2_COLOR2_GREEN : PUSH1_COLOR2_GREEN);
        this.registerColorIndex (AbstractSequencerView.COLOR_SELECTED_PAGE, isPush2 ? PUSH2_COLOR2_OCEAN_HI : PUSH1_COLOR2_OCEAN_HI);
        this.registerColorIndex (AbstractSequencerView.COLOR_RESOLUTION, isPush2 ? PushColorManager.PUSH2_COLOR_SCENE_ORANGE : PushColorManager.PUSH1_COLOR_SCENE_ORANGE);
        this.registerColorIndex (AbstractSequencerView.COLOR_RESOLUTION_SELECTED, isPush2 ? PushColorManager.PUSH2_COLOR_SCENE_ORANGE_HI : PushColorManager.PUSH1_COLOR_SCENE_ORANGE_HI);
        this.registerColorIndex (AbstractSequencerView.COLOR_RESOLUTION_OFF, isPush2 ? PUSH2_COLOR2_BLACK : PUSH1_COLOR2_BLACK);
        this.registerColorIndex (AbstractSequencerView.COLOR_TRANSPOSE, isPush2 ? PushColorManager.PUSH2_COLOR_SCENE_WHITE : PushColorManager.PUSH1_COLOR_SCENE_YELLOW);
        this.registerColorIndex (AbstractSequencerView.COLOR_TRANSPOSE_SELECTED, isPush2 ? PushColorManager.PUSH2_COLOR_SCENE_YELLOW_HI : PushColorManager.PUSH1_COLOR_SCENE_YELLOW_HI);

        this.registerColorIndex (AbstractDrumView.COLOR_PAD_OFF, isPush2 ? PUSH2_COLOR_BLACK : PUSH1_COLOR_BLACK);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_RECORD, isPush2 ? PUSH2_COLOR2_RED_HI : PUSH1_COLOR2_RED_HI);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_PLAY, isPush2 ? PUSH2_COLOR2_GREEN_HI : PUSH1_COLOR2_GREEN_HI);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_SELECTED, isPush2 ? PUSH2_COLOR2_BLUE_HI : PUSH1_COLOR2_BLUE_HI);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_MUTED, isPush2 ? PUSH2_COLOR2_AMBER_LO : PUSH1_COLOR2_AMBER_LO);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_HAS_CONTENT, isPush2 ? PUSH2_COLOR2_YELLOW_HI : PUSH1_COLOR2_YELLOW_HI);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_NO_CONTENT, isPush2 ? PUSH2_COLOR2_YELLOW_LO : PUSH1_COLOR2_YELLOW_LO);

        this.registerColorIndex (AbstractPlayView.COLOR_PLAY, isPush2 ? PUSH2_COLOR2_GREEN_HI : PUSH1_COLOR2_GREEN_HI);
        this.registerColorIndex (AbstractPlayView.COLOR_RECORD, isPush2 ? PUSH2_COLOR2_RED_HI : PUSH1_COLOR2_RED_HI);
        this.registerColorIndex (AbstractPlayView.COLOR_OFF, isPush2 ? PUSH2_COLOR2_BLACK : PUSH1_COLOR2_BLACK);

        this.registerColorIndex (AbstractSessionView.COLOR_SCENE, isPush2 ? PushColorManager.PUSH2_COLOR_SCENE_GREEN : PushColorManager.PUSH1_COLOR_SCENE_GREEN);
        this.registerColorIndex (AbstractSessionView.COLOR_SELECTED_SCENE, isPush2 ? PushColorManager.PUSH2_COLOR_SCENE_GREEN_HI : PushColorManager.PUSH1_COLOR_SCENE_GREEN_HI);
        this.registerColorIndex (AbstractSessionView.COLOR_SCENE_OFF, isPush2 ? PUSH2_COLOR2_BLACK : PUSH1_COLOR2_BLACK);

        this.registerColorIndex (ScenePlayView.COLOR_SELECTED_PLAY_SCENE, isPush2 ? PUSH2_COLOR2_WHITE : PUSH1_COLOR2_WHITE);

        this.registerColorIndex (IPadGrid.GRID_OFF, isPush2 ? PUSH2_COLOR2_BLACK : PUSH1_COLOR2_BLACK);

        this.registerColorIndex (NOTE_REPEAT_PERIOD_OFF, isPush2 ? PUSH2_COLOR_SCENE_YELLOW : PUSH1_COLOR_SCENE_YELLOW);
        this.registerColorIndex (NOTE_REPEAT_PERIOD_HI, isPush2 ? PUSH2_COLOR_SCENE_YELLOW_HI : PUSH1_COLOR_SCENE_YELLOW_HI);
        this.registerColorIndex (NOTE_REPEAT_LENGTH_OFF, isPush2 ? PUSH2_COLOR_SCENE_RED : PUSH1_COLOR_SCENE_RED);
        this.registerColorIndex (NOTE_REPEAT_LENGTH_HI, isPush2 ? PUSH2_COLOR_SCENE_RED_HI : PUSH1_COLOR_SCENE_RED_HI);

        // Push 2 DAW colors are set in the color palette from indices 70 to 96
        this.registerColorIndex (DAWColor.COLOR_OFF, isPush2 ? PUSH2_COLOR2_BLACK : PUSH1_COLOR2_BLACK);
        this.registerColorIndex (DAWColor.DAW_COLOR_GRAY_HALF, isPush2 ? 70 : PUSH1_COLOR2_GREY_MD);
        this.registerColorIndex (DAWColor.DAW_COLOR_DARK_GRAY, isPush2 ? 71 : 1);
        this.registerColorIndex (DAWColor.DAW_COLOR_GRAY, isPush2 ? 72 : PUSH1_COLOR2_GREY_MD);
        this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_GRAY, isPush2 ? 73 : PUSH1_COLOR2_GREY_LT);
        this.registerColorIndex (DAWColor.DAW_COLOR_SILVER, isPush2 ? 74 : 40);
        this.registerColorIndex (DAWColor.DAW_COLOR_DARK_BROWN, isPush2 ? 75 : 11);
        this.registerColorIndex (DAWColor.DAW_COLOR_BROWN, isPush2 ? 76 : 12);
        this.registerColorIndex (DAWColor.DAW_COLOR_DARK_BLUE, isPush2 ? 77 : 42);
        this.registerColorIndex (DAWColor.DAW_COLOR_PURPLE_BLUE, isPush2 ? 78 : 44);
        this.registerColorIndex (DAWColor.DAW_COLOR_PURPLE, isPush2 ? 79 : 81);
        this.registerColorIndex (DAWColor.DAW_COLOR_PINK, isPush2 ? 80 : 57);
        this.registerColorIndex (DAWColor.DAW_COLOR_RED, isPush2 ? 81 : 6);
        this.registerColorIndex (DAWColor.DAW_COLOR_ORANGE, isPush2 ? 82 : 60);
        this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_ORANGE, isPush2 ? 83 : 62);
        this.registerColorIndex (DAWColor.DAW_COLOR_MOSS_GREEN, isPush2 ? 84 : 19);
        this.registerColorIndex (DAWColor.DAW_COLOR_GREEN, isPush2 ? 85 : 26);
        this.registerColorIndex (DAWColor.DAW_COLOR_COLD_GREEN, isPush2 ? 86 : 30);
        this.registerColorIndex (DAWColor.DAW_COLOR_BLUE, isPush2 ? 87 : 37);
        this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_PURPLE, isPush2 ? 88 : 48);
        this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_PINK, isPush2 ? 89 : 56);
        this.registerColorIndex (DAWColor.DAW_COLOR_ROSE, isPush2 ? 90 : 4);
        this.registerColorIndex (DAWColor.DAW_COLOR_REDDISH_BROWN, isPush2 ? 91 : 10);
        this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_BROWN, isPush2 ? 92 : 61);
        this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_GREEN, isPush2 ? 93 : 18);
        this.registerColorIndex (DAWColor.DAW_COLOR_BLUISH_GREEN, isPush2 ? 94 : 25);
        this.registerColorIndex (DAWColor.DAW_COLOR_GREEN_BLUE, isPush2 ? 95 : 32);
        this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_BLUE, isPush2 ? 96 : 41);

        this.registerColorIndex (ColorManager.BUTTON_STATE_OFF, 0);
        this.registerColorIndex (ColorManager.BUTTON_STATE_ON, isPush2 ? 8 : 1);
        this.registerColorIndex (ColorManager.BUTTON_STATE_HI, isPush2 ? 127 : 4);
        this.registerColorIndex (PUSH_BUTTON_STATE_REC_ON, isPush2 ? PUSH2_COLOR2_GREY_LO : 1);
        this.registerColorIndex (PUSH_BUTTON_STATE_REC_HI, isPush2 ? PUSH2_COLOR2_RED_HI : 4);
        this.registerColorIndex (PUSH_BUTTON_STATE_OVR_ON, isPush2 ? PUSH2_COLOR2_GREY_LO : 1);
        this.registerColorIndex (PUSH_BUTTON_STATE_OVR_HI, isPush2 ? PUSH2_COLOR2_AMBER : 2);
        this.registerColorIndex (PUSH_BUTTON_STATE_PLAY_ON, isPush2 ? PUSH2_COLOR2_GREY_LO : 1);
        this.registerColorIndex (PUSH_BUTTON_STATE_PLAY_HI, isPush2 ? PUSH2_COLOR2_GREEN_HI : 4);
        this.registerColorIndex (PUSH_BUTTON_STATE_MUTE_ON, isPush2 ? PUSH2_COLOR2_GREY_LO : 1);
        this.registerColorIndex (PUSH_BUTTON_STATE_MUTE_HI, isPush2 ? PUSH2_COLOR2_AMBER_LO : 4);
        this.registerColorIndex (PUSH_BUTTON_STATE_SOLO_ON, isPush2 ? PUSH2_COLOR2_GREY_LO : 1);
        this.registerColorIndex (PUSH_BUTTON_STATE_SOLO_HI, isPush2 ? PUSH2_COLOR2_YELLOW : 4);
        this.registerColorIndex (PUSH_BUTTON_STATE_STOP_ON, isPush2 ? PUSH2_COLOR2_RED_LO : 1);
        this.registerColorIndex (PUSH_BUTTON_STATE_STOP_HI, isPush2 ? PUSH2_COLOR2_RED_HI : 4);

        for (int i = 0; i < 128; i++)
            this.registerColor (i, getPaletteColor (i));
    }


    /**
     * Get a color entry of the default Push color palette.
     *
     * @param index 0-127
     * @return The palette color as RGB (0-255)
     */
    public static int [] getPaletteColorRGB (final int index)
    {
        if (index >= 70 && index <= 96)
            return DAWColor.getColorEntry (index - 69).toIntRGB255 ();
        return DEFAULT_PALETTE[index];
    }


    /**
     * Get a color of the default Push color palette.
     *
     * @param index 0-127
     * @return The palette color
     */
    public static ColorEx getPaletteColor (final int index)
    {
        if (index >= 70 && index <= 96)
            return DAWColor.getColorEntry (index - 69);
        return ColorEx.fromRGB (DEFAULT_PALETTE[index][0], DEFAULT_PALETTE[index][1], DEFAULT_PALETTE[index][2]);
    }


    /** {@inheritDoc} */
    @Override
    public ColorEx getColor (final int colorIndex, final ButtonID buttonID)
    {
        if (colorIndex < 0)
            return ColorEx.BLACK;

        if (this.isPush2)
        {
            switch (buttonID)
            {
                case NEW:
                case DUPLICATE:
                case FIXED_LENGTH:
                case QUANTIZE:
                case DOUBLE:
                case DELETE:
                case UNDO:
                case METRONOME:
                case TAP_TEMPO:
                case VOLUME:
                case PAN_SEND:
                case TRACK:
                case CLIP:
                case DEVICE:
                case BROWSE:
                case PAGE_LEFT:
                case PAGE_RIGHT:
                case SCALES:
                case USER:
                case REPEAT:
                case ACCENT:
                case OCTAVE_DOWN:
                case OCTAVE_UP:
                case ADD_EFFECT:
                case ADD_TRACK:
                case NOTE:
                case SESSION:
                case SELECT:
                case SHIFT:
                case ARROW_LEFT:
                case ARROW_RIGHT:
                case ARROW_DOWN:
                case ARROW_UP:
                case MASTERTRACK:
                case SETUP:
                case LAYOUT:
                    int color = PUSH2_COLOR2_WHITE;
                    if (colorIndex == 0)
                        color = PUSH2_COLOR2_BLACK;
                    else if (colorIndex == 8)
                        color = PUSH2_COLOR2_GREY_LO;
                    return this.colorByIndex.get (Integer.valueOf (color));

                default:
                    // Fall through
                    break;
            }
        }
        else
        {
            switch (buttonID)
            {
                case PLAY:
                    return this.colorByIndex.get (Integer.valueOf (colorIndex == 1 ? PUSH2_COLOR2_GREY_LO : PUSH2_COLOR2_GREEN_HI));
                case AUTOMATION, RECORD:
                    int col = PUSH2_COLOR2_AMBER;
                    if (colorIndex == 1)
                        col = PUSH2_COLOR2_GREY_LO;
                    else if (colorIndex == 4)
                        col = PUSH2_COLOR2_RED_HI;
                    return this.colorByIndex.get (Integer.valueOf (col));
                case MUTE:
                    return this.colorByIndex.get (Integer.valueOf (colorIndex == 1 ? PUSH2_COLOR2_GREY_LO : PUSH2_COLOR2_AMBER_LO));
                case SOLO:
                    return this.colorByIndex.get (Integer.valueOf (colorIndex == 1 ? PUSH2_COLOR2_GREY_LO : PUSH2_COLOR2_YELLOW));
                case STOP_CLIP:
                    return this.colorByIndex.get (Integer.valueOf (colorIndex == 1 ? PUSH2_COLOR2_RED_LO : PUSH2_COLOR2_RED_HI));

                case NEW:
                case DUPLICATE:
                case FIXED_LENGTH:
                case QUANTIZE:
                case DOUBLE:
                case DELETE:
                case UNDO:
                case METRONOME:
                case TAP_TEMPO:
                case VOLUME:
                case PAN_SEND:
                case TRACK:
                case CLIP:
                case DEVICE:
                case BROWSE:
                case PAGE_LEFT:
                case PAGE_RIGHT:
                case SCALES:
                case USER:
                case REPEAT:
                case ACCENT:
                case OCTAVE_DOWN:
                case OCTAVE_UP:
                case ADD_EFFECT:
                case ADD_TRACK:
                case NOTE:
                case SESSION:
                case SELECT:
                case SHIFT:
                case ARROW_LEFT:
                case ARROW_RIGHT:
                case ARROW_DOWN:
                case ARROW_UP:
                case MASTERTRACK:
                    int color = PUSH2_COLOR2_WHITE;
                    if (colorIndex == 0)
                        color = PUSH2_COLOR_BLACK;
                    else if (colorIndex == 1)
                        color = PUSH2_COLOR2_GREY_LO;
                    return this.colorByIndex.get (Integer.valueOf (color));

                default:
                    // Fall through
                    break;
            }
        }

        final ColorEx color = this.colorByIndex.get (Integer.valueOf (colorIndex));
        if (color == null)
            throw new ColorIndexException ("Color for index " + colorIndex + " is not registered!");
        return color;
    }
}