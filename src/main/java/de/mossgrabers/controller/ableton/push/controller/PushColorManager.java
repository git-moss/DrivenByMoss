// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.controller;

import java.util.HashSet;
import java.util.Set;

import de.mossgrabers.controller.ableton.push.PushVersion;
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
import de.mossgrabers.framework.view.sequencer.ClipLengthView;


/**
 * Different colors to use for the pads and buttons of Push 1 and Push 2.
 *
 * @author Jürgen Moßgraber
 */
@SuppressWarnings("javadoc")
public class PushColorManager extends ColorManager
{
    /** ID for color when button signals a recording state. */
    public static final String         PUSH_BUTTON_STATE_REC_ON               = "PUSH_BUTTON_STATE_REC_ON";
    /** ID for color when button signals an activated recording state. */
    public static final String         PUSH_BUTTON_STATE_REC_HI               = "PUSH_BUTTON_STATE_REC_HI";
    /** ID for color when button signals an overwrite state. */
    public static final String         PUSH_BUTTON_STATE_OVR_ON               = "PUSH_BUTTON_STATE_OVR_ON";
    /** ID for color when button signals an activated overwrite state. */
    public static final String         PUSH_BUTTON_STATE_OVR_HI               = "PUSH_BUTTON_STATE_OVR_HI";
    /** ID for color when button signals a play state. */
    public static final String         PUSH_BUTTON_STATE_PLAY_ON              = "PUSH_BUTTON_STATE_PLAY_ON";
    /** ID for color when button signals an activated play state. */
    public static final String         PUSH_BUTTON_STATE_PLAY_HI              = "PUSH_BUTTON_STATE_PLAY_HI";
    /** ID for color when button signals a mute state. */
    public static final String         PUSH_BUTTON_STATE_MUTE_ON              = "PUSH_BUTTON_STATE_MUTE_ON";
    /** ID for color when button signals an activated mute state. */
    public static final String         PUSH_BUTTON_STATE_MUTE_HI              = "PUSH_BUTTON_STATE_MUTE_HI";
    /** ID for color when button signals a solo state. */
    public static final String         PUSH_BUTTON_STATE_SOLO_ON              = "PUSH_BUTTON_STATE_SOLO_ON";
    /** ID for color when button signals an activated solo state. */
    public static final String         PUSH_BUTTON_STATE_SOLO_HI              = "PUSH_BUTTON_STATE_SOLO_HI";
    /** ID for color when button signals a stop clip state. */
    public static final String         PUSH_BUTTON_STATE_STOP_ON              = "PUSH_BUTTON_STATE_STOP_ON";
    /** ID for color when button signals an activated stop clip state. */
    public static final String         PUSH_BUTTON_STATE_STOP_HI              = "PUSH_BUTTON_STATE_STOP_HI";

    /** ID for the color to use for note repeat resolution. */
    public static final String         NOTE_REPEAT_PERIOD_OFF                 = "NOTE_REPEAT_PERIOD_OFF";
    /** ID for the color to use for note repeat resolution selected. */
    public static final String         NOTE_REPEAT_PERIOD_HI                  = "NOTE_REPEAT_PERIOD_HI";
    /** ID for the color to use for note repeat length. */
    public static final String         NOTE_REPEAT_LENGTH_OFF                 = "NOTE_REPEAT_LENGTH_OFF";
    /** ID for the color to use for note repeat length selected. */
    public static final String         NOTE_REPEAT_LENGTH_HI                  = "NOTE_REPEAT_LENGTH_HI";

    /** ID for color when button signals a lock state. */
    public static final String         PUSH_BUTTON_STATE_MASTER_ON            = "PUSH_BUTTON_STATE_MASTER_ON";
    /** ID for color when button signals an activated lock state. */
    public static final String         PUSH_BUTTON_STATE_MASTER_HI            = "PUSH_BUTTON_STATE_MASTER_HI";

    ///////////////////////////////////////////////////////////////////////////////////////
    // Only Push 3

    /** ID for color when button signals a lock state. */
    public static final String         PUSH_BUTTON_STATE_LOCK_ON              = "PUSH_BUTTON_STATE_LOCK_ON";
    /** ID for color when button signals an activated lock state. */
    public static final String         PUSH_BUTTON_STATE_LOCK_HI              = "PUSH_BUTTON_STATE_LOCK_HI";
    /** ID for color when button signals a insert scene state. */
    public static final String         PUSH_BUTTON_STATE_INSERT_SCENE_ON      = "PUSH_BUTTON_STATE_INSERT_SCENE_ON";
    /** ID for color when button signals an activated insert scene state. */
    public static final String         PUSH_BUTTON_STATE_INSERT_SCENE_HI      = "PUSH_BUTTON_STATE_INSERT_SCENE_HI";

    // @formatter:off
    /** The default color palette (like fixed on Push 1) */
    protected static final int [] [] DEFAULT_PALETTE =
    {
        { 0x00, 0x00, 0x00 }, { 0x1E, 0x1E, 0x1E }, { 0x7F, 0x7F, 0x7F },
        { 0xFF, 0xFF, 0xFF }, { 0xFF, 0x4C, 0x4C }, { 0xFF, 0x00, 0x00 },
        { 0x59, 0x00, 0x00 }, { 0x19, 0x00, 0x00 }, { 0xFF, 0xBD, 0x6C },
        { 0xFF, 0x54, 0x00 }, { 0x59, 0x1D, 0x00 }, { 0x27, 0x1B, 0x00 },
        { 0xFF, 0xFF, 0x4C }, { 0xFF, 0xFF, 0x00 }, { 0x59, 0x59, 0x00 },
        { 0x19, 0x19, 0x00 }, { 0x88, 0xFF, 0x4C }, { 0x54, 0xFF, 0x00 },
        { 0x1D, 0x59, 0x00 }, { 0x14, 0x2B, 0x00 }, { 0x4C, 0xFF, 0x4C },
        { 0x00, 0xFF, 0x00 }, { 0x00, 0x59, 0x00 }, { 0x00, 0x19, 0x00 },
        { 0x4C, 0xFF, 0x5E }, { 0x00, 0xFF, 0x19 }, { 0x00, 0x59, 0x0D },
        { 0x00, 0x19, 0x02 }, { 0x4C, 0xFF, 0x88 }, { 0x00, 0xFF, 0x55 },
        { 0x00, 0x59, 0x1D }, { 0x00, 0x1F, 0x12 }, { 0x4C, 0xFF, 0xB7 },
        { 0x00, 0xFF, 0x99 }, { 0x00, 0x59, 0x35 }, { 0x00, 0x19, 0x12 },
        { 0x4C, 0xC3, 0xFF }, { 0x00, 0xA9, 0xFF }, { 0x00, 0x41, 0x52 },
        { 0x00, 0x10, 0x19 }, { 0x4C, 0x88, 0xFF }, { 0x00, 0x55, 0xFF },
        { 0x00, 0x1D, 0x59 }, { 0x00, 0x08, 0x19 }, { 0x4C, 0x4C, 0xFF },
        { 0x00, 0x00, 0xFF }, { 0x00, 0x00, 0x59 }, { 0x00, 0x00, 0x19 },
        { 0x87, 0x4C, 0xFF }, { 0x54, 0x00, 0xFF }, { 0x19, 0x00, 0x64 },
        { 0x0F, 0x00, 0x30 }, { 0xFF, 0x4C, 0xFF }, { 0xFF, 0x00, 0xFF },
        { 0x59, 0x00, 0x59 }, { 0x19, 0x00, 0x19 }, { 0xFF, 0x4C, 0x87 },
        { 0xFF, 0x00, 0x54 }, { 0x59, 0x00, 0x1D }, { 0x22, 0x00, 0x13 },
        { 0xFF, 0x15, 0x00 }, { 0x99, 0x35, 0x00 }, { 0x79, 0x51, 0x00 },
        { 0x43, 0x64, 0x00 }, { 0x03, 0x39, 0x00 }, { 0x00, 0x57, 0x35 },
        { 0x00, 0x54, 0x7F }, { 0x00, 0x00, 0xFF }, { 0x00, 0x45, 0x4F },
        { 0x25, 0x00, 0xCC }, { 0x7F, 0x7F, 0x7F }, { 0x20, 0x20, 0x20 },
        { 0xFF, 0x00, 0x00 }, { 0xBD, 0xFF, 0x2D }, { 0xAF, 0xED, 0x06 },
        { 0x64, 0xFF, 0x09 }, { 0x10, 0x8B, 0x00 }, { 0x00, 0xFF, 0x87 },
        { 0x00, 0xA9, 0xFF }, { 0x00, 0x2A, 0xFF }, { 0x3F, 0x00, 0xFF },
        { 0x7A, 0x00, 0xFF }, { 0xB2, 0x1A, 0x7D }, { 0x40, 0x21, 0x00 },
        { 0xFF, 0x4A, 0x00 }, { 0x88, 0xE1, 0x06 }, { 0x72, 0xFF, 0x15 },
        { 0x00, 0xFF, 0x00 }, { 0x3B, 0xFF, 0x26 }, { 0x59, 0xFF, 0x71 },
        { 0x38, 0xFF, 0xCC }, { 0x5B, 0x8A, 0xFF }, { 0x31, 0x51, 0xC6 },
        { 0x87, 0x7F, 0xE9 }, { 0xD3, 0x1D, 0xFF }, { 0xFF, 0x00, 0x5D },
        { 0xFF, 0x7F, 0x00 }, { 0xB9, 0xB0, 0x00 }, { 0x90, 0xFF, 0x00 },
        { 0x83, 0x5D, 0x07 }, { 0x39, 0x2B, 0x00 }, { 0x14, 0x4C, 0x10 },
        { 0x0D, 0x50, 0x38 }, { 0x15, 0x15, 0x2A }, { 0x16, 0x20, 0x5A },
        { 0x69, 0x3C, 0x1C }, { 0xA8, 0x00, 0x0A }, { 0xDE, 0x51, 0x3D },
        { 0xD8, 0x6A, 0x1C }, { 0xFF, 0xE1, 0x26 }, { 0x9E, 0xE1, 0x2F },
        { 0x67, 0xB5, 0x0F }, { 0x1E, 0x1E, 0x30 }, { 0xDC, 0xFF, 0x6B },
        { 0x80, 0xFF, 0xBD }, { 0x9A, 0x99, 0xFF }, { 0x8E, 0x66, 0xFF },
        { 0x40, 0x40, 0x40 }, { 0x75, 0x75, 0x75 }, { 0xE0, 0xFF, 0xFF },
        { 0xA0, 0x00, 0x00 }, { 0x35, 0x00, 0x00 }, { 0x1A, 0xD0, 0x00 },
        { 0x07, 0x42, 0x00 }, { 0xB9, 0xB0, 0x00 }, { 0x3F, 0x31, 0x00 },
        { 0xB3, 0x5F, 0x00 }, { 0x4B, 0x15, 0x02 }
    };
    // @formatter:on

    // Second row & Pad button colors
    public static final int            PUSH2_COLOR2_BLACK                     = 0;
    public static final int            PUSH2_COLOR2_GREY_LO                   = 1;
    public static final int            PUSH2_COLOR2_GREY_MD                   = 103;
    public static final int            PUSH2_COLOR2_GREY_LT                   = 2;
    public static final int            PUSH2_COLOR2_WHITE                     = 3;
    public static final int            PUSH2_COLOR2_ROSE                      = 4;
    public static final int            PUSH2_COLOR2_RED_HI                    = 5;
    public static final int            PUSH2_COLOR2_RED                       = 6;
    public static final int            PUSH2_COLOR2_RED_LO                    = 7;
    public static final int            PUSH2_COLOR2_RED_AMBER                 = 8;
    public static final int            PUSH2_COLOR2_AMBER_HI                  = 9;
    public static final int            PUSH2_COLOR2_AMBER                     = 10;
    public static final int            PUSH2_COLOR2_AMBER_LO                  = 11;
    public static final int            PUSH2_COLOR2_AMBER_YELLOW              = 12;
    public static final int            PUSH2_COLOR2_YELLOW_HI                 = 13;
    public static final int            PUSH2_COLOR2_YELLOW                    = 14;
    public static final int            PUSH2_COLOR2_YELLOW_LO                 = 15;
    public static final int            PUSH2_COLOR2_YELLOW_LIME               = 16;
    public static final int            PUSH2_COLOR2_LIME_HI                   = 17;
    public static final int            PUSH2_COLOR2_LIME                      = 18;
    public static final int            PUSH2_COLOR2_LIME_LO                   = 19;
    public static final int            PUSH2_COLOR2_LIME_GREEN                = 20;
    public static final int            PUSH2_COLOR2_GREEN_HI                  = 21;
    public static final int            PUSH2_COLOR2_GREEN                     = 22;
    public static final int            PUSH2_COLOR2_GREEN_LO                  = 23;
    public static final int            PUSH2_COLOR2_GREEN_SPRING              = 24;
    public static final int            PUSH2_COLOR2_SPRING_HI                 = 25;
    public static final int            PUSH2_COLOR2_SPRING                    = 26;
    public static final int            PUSH2_COLOR2_SPRING_LO                 = 27;
    public static final int            PUSH2_COLOR2_SPRING_TURQUOISE          = 28;
    public static final int            PUSH2_COLOR2_TURQUOISE_LO              = 29;
    public static final int            PUSH2_COLOR2_TURQUOISE                 = 30;
    public static final int            PUSH2_COLOR2_TURQUOISE_HI              = 31;
    public static final int            PUSH2_COLOR2_TURQUOISE_CYAN            = 32;
    public static final int            PUSH2_COLOR2_CYAN_HI                   = 33;
    public static final int            PUSH2_COLOR2_CYAN                      = 34;
    public static final int            PUSH2_COLOR2_CYAN_LO                   = 35;
    public static final int            PUSH2_COLOR2_CYAN_SKY                  = 36;
    public static final int            PUSH2_COLOR2_SKY_HI                    = 37;
    public static final int            PUSH2_COLOR2_SKY                       = 38;
    public static final int            PUSH2_COLOR2_SKY_LO                    = 39;
    public static final int            PUSH2_COLOR2_SKY_OCEAN                 = 40;
    public static final int            PUSH2_COLOR2_OCEAN_HI                  = 41;
    public static final int            PUSH2_COLOR2_OCEAN                     = 42;
    public static final int            PUSH2_COLOR2_OCEAN_LO                  = 43;
    public static final int            PUSH2_COLOR2_OCEAN_BLUE                = 44;
    public static final int            PUSH2_COLOR2_BLUE_HI                   = 45;
    public static final int            PUSH2_COLOR2_BLUE                      = 46;
    public static final int            PUSH2_COLOR2_BLUE_LO                   = 47;
    public static final int            PUSH2_COLOR2_BLUE_ORCHID               = 48;
    public static final int            PUSH2_COLOR2_ORCHID_HI                 = 49;
    public static final int            PUSH2_COLOR2_ORCHID                    = 50;
    public static final int            PUSH2_COLOR2_ORCHID_LO                 = 51;
    public static final int            PUSH2_COLOR2_ORCHID_MAGENTA            = 52;
    public static final int            PUSH2_COLOR2_MAGENTA_HI                = 53;
    public static final int            PUSH2_COLOR2_MAGENTA                   = 54;
    public static final int            PUSH2_COLOR2_MAGENTA_LO                = 55;
    public static final int            PUSH2_COLOR2_MAGENTA_PINK              = 56;
    public static final int            PUSH2_COLOR2_PINK_HI                   = 57;
    public static final int            PUSH2_COLOR2_PINK                      = 58;
    public static final int            PUSH2_COLOR2_PINK_LO                   = 59;
    public static final int            PUSH2_COLOR2_SILVER                    = 118;
    public static final int            PUSH2_COLOR2_ORANGE                    = 65;
    public static final int            PUSH2_COLOR2_ORANGE_LIGHT              = 3;
    public static final int            PUSH2_COLOR2_LIGHT_BROWN               = 69;

    // First row colors
    public static final int            PUSH2_COLOR_BLACK                      = 0;
    public static final int            PUSH2_COLOR_RED_LO                     = PUSH2_COLOR2_RED_LO;
    public static final int            PUSH2_COLOR_RED_LO_SBLINK              = 2;
    public static final int            PUSH2_COLOR_RED_LO_FBLINK              = 3;
    public static final int            PUSH2_COLOR_RED_HI                     = PUSH2_COLOR2_RED_HI;
    public static final int            PUSH2_COLOR_RED_HI_SBLINK              = 5;
    public static final int            PUSH2_COLOR_RED_HI_FBLINK              = 6;
    public static final int            PUSH2_COLOR_ORANGE_LO                  = PUSH2_COLOR2_AMBER_LO;
    public static final int            PUSH2_COLOR_ORANGE_LO_SBLINK           = 8;
    public static final int            PUSH2_COLOR_ORANGE_LO_FBLINK           = 9;
    public static final int            PUSH2_COLOR_ORANGE_HI                  = PUSH2_COLOR2_AMBER_HI;
    public static final int            PUSH2_COLOR_ORANGE_HI_SBLINK           = 11;
    public static final int            PUSH2_COLOR_ORANGE_HI_FBLINK           = 12;
    public static final int            PUSH2_COLOR_YELLOW_LO                  = PUSH2_COLOR2_YELLOW_LO;
    public static final int            PUSH2_COLOR_YELLOW_LO_SBLINK           = 14;
    public static final int            PUSH2_COLOR_YELLOW_LO_FBLINK           = 15;
    public static final int            PUSH2_COLOR_YELLOW_MD                  = PUSH2_COLOR2_YELLOW_HI;
    public static final int            PUSH2_COLOR_YELLOW_MD_SBLINK           = 17;
    public static final int            PUSH2_COLOR_YELLOW_MD_FBLINK           = 18;
    public static final int            PUSH2_COLOR_GREEN_LO                   = PUSH2_COLOR2_GREEN_LO;
    public static final int            PUSH2_COLOR_GREEN_LO_SBLINK            = 20;
    public static final int            PUSH2_COLOR_GREEN_LO_FBLINK            = 21;
    public static final int            PUSH2_COLOR_GREEN_HI                   = PUSH2_COLOR2_GREEN_HI;
    public static final int            PUSH2_COLOR_GREEN_HI_SBLINK            = 23;
    public static final int            PUSH2_COLOR_GREEN_HI_FBLINK            = 24;

    // Scene button colors
    public static final int            PUSH2_COLOR_SCENE_RED                  = PUSH2_COLOR2_RED;
    public static final int            PUSH2_COLOR_SCENE_RED_BLINK            = 2;
    public static final int            PUSH2_COLOR_SCENE_RED_BLINK_FAST       = 3;
    public static final int            PUSH2_COLOR_SCENE_RED_HI               = PUSH2_COLOR2_RED_HI;
    public static final int            PUSH2_COLOR_SCENE_RED_HI_BLINK         = 5;
    public static final int            PUSH2_COLOR_SCENE_RED_HI_BLINK_FAST    = 6;
    public static final int            PUSH2_COLOR_SCENE_ORANGE               = PUSH2_COLOR2_AMBER;
    public static final int            PUSH2_COLOR_SCENE_ORANGE_BLINK         = 8;
    public static final int            PUSH2_COLOR_SCENE_ORANGE_BLINK_FAST    = 9;
    public static final int            PUSH2_COLOR_SCENE_ORANGE_HI            = PUSH2_COLOR2_AMBER_HI;
    public static final int            PUSH2_COLOR_SCENE_ORANGE_HI_BLINK      = 11;
    public static final int            PUSH2_COLOR_SCENE_ORANGE_HI_BLINK_FAST = 12;
    public static final int            PUSH2_COLOR_SCENE_YELLOW               = PUSH2_COLOR2_YELLOW;
    public static final int            PUSH2_COLOR_SCENE_YELLOW_BLINK         = 14;
    public static final int            PUSH2_COLOR_SCENE_YELLOW_BLINK_FAST    = 15;
    public static final int            PUSH2_COLOR_SCENE_YELLOW_HI            = PUSH2_COLOR2_YELLOW_HI;
    public static final int            PUSH2_COLOR_SCENE_YELLOW_HI_BLINK      = 17;
    public static final int            PUSH2_COLOR_SCENE_YELLOW_HI_BLINK_FAST = 18;
    public static final int            PUSH2_COLOR_SCENE_GREEN                = PUSH2_COLOR2_GREEN;
    public static final int            PUSH2_COLOR_SCENE_GREEN_BLINK          = 20;
    public static final int            PUSH2_COLOR_SCENE_GREEN_BLINK_FAST     = 21;
    public static final int            PUSH2_COLOR_SCENE_GREEN_HI             = PUSH2_COLOR2_GREEN_HI;
    public static final int            PUSH2_COLOR_SCENE_GREEN_HI_BLINK       = 23;
    public static final int            PUSH2_COLOR_SCENE_GREEN_HI_BLINK_FAST  = 24;
    public static final int            PUSH2_COLOR_SCENE_WHITE                = 60;

    // First row colors
    public static final int            PUSH1_COLOR_BLACK                      = 0;
    public static final int            PUSH1_COLOR_RED_LO                     = 1;
    public static final int            PUSH1_COLOR_RED_LO_SBLINK              = 2;
    public static final int            PUSH1_COLOR_RED_LO_FBLINK              = 3;
    public static final int            PUSH1_COLOR_RED_HI                     = 4;
    public static final int            PUSH1_COLOR_RED_HI_SBLINK              = 5;
    public static final int            PUSH1_COLOR_RED_HI_FBLINK              = 6;
    public static final int            PUSH1_COLOR_ORANGE_LO                  = 7;
    public static final int            PUSH1_COLOR_ORANGE_LO_SBLINK           = 8;
    public static final int            PUSH1_COLOR_ORANGE_LO_FBLINK           = 9;
    public static final int            PUSH1_COLOR_ORANGE_HI                  = 10;
    public static final int            PUSH1_COLOR_ORANGE_HI_SBLINK           = 11;
    public static final int            PUSH1_COLOR_ORANGE_HI_FBLINK           = 12;
    public static final int            PUSH1_COLOR_YELLOW_LO                  = 13;
    public static final int            PUSH1_COLOR_YELLOW_LO_SBLINK           = 14;
    public static final int            PUSH1_COLOR_YELLOW_LO_FBLINK           = 15;
    public static final int            PUSH1_COLOR_YELLOW_MD                  = 16;
    public static final int            PUSH1_COLOR_YELLOW_MD_SBLINK           = 17;
    public static final int            PUSH1_COLOR_YELLOW_MD_FBLINK           = 18;
    public static final int            PUSH1_COLOR_GREEN_LO                   = 19;
    public static final int            PUSH1_COLOR_GREEN_LO_SBLINK            = 20;
    public static final int            PUSH1_COLOR_GREEN_LO_FBLINK            = 21;
    public static final int            PUSH1_COLOR_GREEN_HI                   = 22;
    public static final int            PUSH1_COLOR_GREEN_HI_SBLINK            = 23;
    public static final int            PUSH1_COLOR_GREEN_HI_FBLINK            = 24;

    // Second row & Pad button colors
    public static final int            PUSH1_COLOR2_BLACK                     = 0;
    public static final int            PUSH1_COLOR2_GREY_LO                   = 1;
    public static final int            PUSH1_COLOR2_GREY_MD                   = 103;
    public static final int            PUSH1_COLOR2_GREY_LT                   = 2;
    public static final int            PUSH1_COLOR2_WHITE                     = 3;
    public static final int            PUSH1_COLOR2_ROSE                      = 4;
    public static final int            PUSH1_COLOR2_RED_HI                    = 5;
    public static final int            PUSH1_COLOR2_RED                       = 6;
    public static final int            PUSH1_COLOR2_RED_LO                    = 7;
    public static final int            PUSH1_COLOR2_RED_AMBER                 = 8;
    public static final int            PUSH1_COLOR2_AMBER_HI                  = 9;
    public static final int            PUSH1_COLOR2_AMBER                     = 10;
    public static final int            PUSH1_COLOR2_AMBER_LO                  = 11;
    public static final int            PUSH1_COLOR2_AMBER_YELLOW              = 12;
    public static final int            PUSH1_COLOR2_YELLOW_HI                 = 13;
    public static final int            PUSH1_COLOR2_YELLOW                    = 14;
    public static final int            PUSH1_COLOR2_YELLOW_LO                 = 15;
    public static final int            PUSH1_COLOR2_YELLOW_LIME               = 16;
    public static final int            PUSH1_COLOR2_LIME_HI                   = 17;
    public static final int            PUSH1_COLOR2_LIME                      = 18;
    public static final int            PUSH1_COLOR2_LIME_LO                   = 19;
    public static final int            PUSH1_COLOR2_LIME_GREEN                = 20;
    public static final int            PUSH1_COLOR2_GREEN_HI                  = 21;
    public static final int            PUSH1_COLOR2_GREEN                     = 22;
    public static final int            PUSH1_COLOR2_GREEN_LO                  = 23;
    public static final int            PUSH1_COLOR2_GREEN_SPRING              = 24;
    public static final int            PUSH1_COLOR2_SPRING_HI                 = 25;
    public static final int            PUSH1_COLOR2_SPRING                    = 26;
    public static final int            PUSH1_COLOR2_SPRING_LO                 = 27;
    public static final int            PUSH1_COLOR2_SPRING_TURQUOISE          = 28;
    public static final int            PUSH1_COLOR2_TURQUOISE_LO              = 29;
    public static final int            PUSH1_COLOR2_TURQUOISE                 = 30;
    public static final int            PUSH1_COLOR2_TURQUOISE_HI              = 31;
    public static final int            PUSH1_COLOR2_TURQUOISE_CYAN            = 32;
    public static final int            PUSH1_COLOR2_CYAN_HI                   = 33;
    public static final int            PUSH1_COLOR2_CYAN                      = 34;
    public static final int            PUSH1_COLOR2_CYAN_LO                   = 35;
    public static final int            PUSH1_COLOR2_CYAN_SKY                  = 36;
    public static final int            PUSH1_COLOR2_SKY_HI                    = 37;
    public static final int            PUSH1_COLOR2_SKY                       = 38;
    public static final int            PUSH1_COLOR2_SKY_LO                    = 39;
    public static final int            PUSH1_COLOR2_SKY_OCEAN                 = 40;
    public static final int            PUSH1_COLOR2_OCEAN_HI                  = 41;
    public static final int            PUSH1_COLOR2_OCEAN                     = 42;
    public static final int            PUSH1_COLOR2_OCEAN_LO                  = 43;
    public static final int            PUSH1_COLOR2_OCEAN_BLUE                = 44;
    public static final int            PUSH1_COLOR2_BLUE_HI                   = 45;
    public static final int            PUSH1_COLOR2_BLUE                      = 46;
    public static final int            PUSH1_COLOR2_BLUE_LO                   = 47;
    public static final int            PUSH1_COLOR2_BLUE_ORCHID               = 48;
    public static final int            PUSH1_COLOR2_ORCHID_HI                 = 49;
    public static final int            PUSH1_COLOR2_ORCHID                    = 50;
    public static final int            PUSH1_COLOR2_ORCHID_LO                 = 51;
    public static final int            PUSH1_COLOR2_ORCHID_MAGENTA            = 52;
    public static final int            PUSH1_COLOR2_MAGENTA_HI                = 53;
    public static final int            PUSH1_COLOR2_MAGENTA                   = 54;
    public static final int            PUSH1_COLOR2_MAGENTA_LO                = 55;
    public static final int            PUSH1_COLOR2_MAGENTA_PINK              = 56;
    public static final int            PUSH1_COLOR2_PINK_HI                   = 57;
    public static final int            PUSH1_COLOR2_PINK                      = 58;
    public static final int            PUSH1_COLOR2_PINK_LO                   = 59;
    public static final int            PUSH1_COLOR2_SILVER                    = 118;
    public static final int            PUSH1_COLOR2_ORANGE                    = 65;
    public static final int            PUSH1_COLOR2_ORANGE_LIGHT              = 3;
    public static final int            PUSH1_COLOR2_LIGHT_BROWN               = 69;

    // Scene button colors
    public static final int            PUSH1_COLOR_SCENE_RED                  = 1;
    public static final int            PUSH1_COLOR_SCENE_RED_BLINK            = 2;
    public static final int            PUSH1_COLOR_SCENE_RED_BLINK_FAST       = 3;
    public static final int            PUSH1_COLOR_SCENE_RED_HI               = 4;
    public static final int            PUSH1_COLOR_SCENE_RED_HI_BLINK         = 5;
    public static final int            PUSH1_COLOR_SCENE_RED_HI_BLINK_FAST    = 6;
    public static final int            PUSH1_COLOR_SCENE_ORANGE               = 7;
    public static final int            PUSH1_COLOR_SCENE_ORANGE_BLINK         = 8;
    public static final int            PUSH1_COLOR_SCENE_ORANGE_BLINK_FAST    = 9;
    public static final int            PUSH1_COLOR_SCENE_ORANGE_HI            = 10;
    public static final int            PUSH1_COLOR_SCENE_ORANGE_HI_BLINK      = 11;
    public static final int            PUSH1_COLOR_SCENE_ORANGE_HI_BLINK_FAST = 12;
    public static final int            PUSH1_COLOR_SCENE_YELLOW               = 13;
    public static final int            PUSH1_COLOR_SCENE_YELLOW_BLINK         = 14;
    public static final int            PUSH1_COLOR_SCENE_YELLOW_BLINK_FAST    = 15;
    public static final int            PUSH1_COLOR_SCENE_YELLOW_HI            = 16;
    public static final int            PUSH1_COLOR_SCENE_YELLOW_HI_BLINK      = 17;
    public static final int            PUSH1_COLOR_SCENE_YELLOW_HI_BLINK_FAST = 18;
    public static final int            PUSH1_COLOR_SCENE_GREEN                = 19;
    public static final int            PUSH1_COLOR_SCENE_GREEN_BLINK          = 20;
    public static final int            PUSH1_COLOR_SCENE_GREEN_BLINK_FAST     = 21;
    public static final int            PUSH1_COLOR_SCENE_GREEN_HI             = 22;
    public static final int            PUSH1_COLOR_SCENE_GREEN_HI_BLINK       = 23;
    public static final int            PUSH1_COLOR_SCENE_GREEN_HI_BLINK_FAST  = 24;

    public static final String         PUSH_BLACK                             = "PUSH_BLACK";
    public static final String         PUSH_RED                               = "PUSH_RED";
    public static final String         PUSH_RED_LO                            = "PUSH_RED_LO";
    public static final String         PUSH_RED_HI                            = "PUSH_RED_HI";
    public static final String         PUSH_ORANGE_LO                         = "PUSH_ORANGE_LO";
    public static final String         PUSH_ORANGE_HI                         = "PUSH_ORANGE_HI";
    public static final String         PUSH_YELLOW_LO                         = "PUSH_YELLOW_LO";
    public static final String         PUSH_YELLOW_MD                         = "PUSH_YELLOW_MD";
    public static final String         PUSH_GREEN_LO                          = "PUSH_GREEN_LO";
    public static final String         PUSH_GREEN_HI                          = "PUSH_GREEN_HI";

    public static final String         PUSH_BLACK_2                           = "PUSH_BLACK_2";
    public static final String         PUSH_WHITE_2                           = "PUSH_WHITE_2";
    public static final String         PUSH_GREY_LO_2                         = "PUSH_GREY_LO_2";
    public static final String         PUSH_GREEN_2                           = "PUSH_GREEN_2";

    private static final Set<ButtonID> MONOCHROME_BUTTONS                     = new HashSet<> ();
    static
    {
        MONOCHROME_BUTTONS.add (ButtonID.ACCENT);
        MONOCHROME_BUTTONS.add (ButtonID.ADD_EFFECT);
        MONOCHROME_BUTTONS.add (ButtonID.ADD_TRACK);
        MONOCHROME_BUTTONS.add (ButtonID.ARROW_DOWN);
        MONOCHROME_BUTTONS.add (ButtonID.ARROW_LEFT);
        MONOCHROME_BUTTONS.add (ButtonID.ARROW_RIGHT);
        MONOCHROME_BUTTONS.add (ButtonID.ARROW_UP);
        MONOCHROME_BUTTONS.add (ButtonID.BROWSE);
        MONOCHROME_BUTTONS.add (ButtonID.CLIP);
        MONOCHROME_BUTTONS.add (ButtonID.DELETE);
        MONOCHROME_BUTTONS.add (ButtonID.DEVICE);
        MONOCHROME_BUTTONS.add (ButtonID.DOUBLE);
        MONOCHROME_BUTTONS.add (ButtonID.DUPLICATE);
        MONOCHROME_BUTTONS.add (ButtonID.FIXED_LENGTH);
        MONOCHROME_BUTTONS.add (ButtonID.HELP);
        MONOCHROME_BUTTONS.add (ButtonID.INSERT_SCENE);
        MONOCHROME_BUTTONS.add (ButtonID.LAYOUT);
        MONOCHROME_BUTTONS.add (ButtonID.LOAD);
        MONOCHROME_BUTTONS.add (ButtonID.LOCK_MODE);
        MONOCHROME_BUTTONS.add (ButtonID.MASTERTRACK);
        MONOCHROME_BUTTONS.add (ButtonID.METRONOME);
        MONOCHROME_BUTTONS.add (ButtonID.NEW);
        MONOCHROME_BUTTONS.add (ButtonID.NOTE);
        MONOCHROME_BUTTONS.add (ButtonID.OCTAVE_DOWN);
        MONOCHROME_BUTTONS.add (ButtonID.OCTAVE_UP);
        MONOCHROME_BUTTONS.add (ButtonID.PAGE_LEFT);
        MONOCHROME_BUTTONS.add (ButtonID.PAGE_RIGHT);
        MONOCHROME_BUTTONS.add (ButtonID.PAN_SEND);
        MONOCHROME_BUTTONS.add (ButtonID.QUANTIZE);
        MONOCHROME_BUTTONS.add (ButtonID.REPEAT);
        MONOCHROME_BUTTONS.add (ButtonID.SAVE);
        MONOCHROME_BUTTONS.add (ButtonID.SCALES);
        MONOCHROME_BUTTONS.add (ButtonID.SELECT);
        MONOCHROME_BUTTONS.add (ButtonID.SESSION);
        MONOCHROME_BUTTONS.add (ButtonID.SETUP);
        MONOCHROME_BUTTONS.add (ButtonID.SHIFT);
        MONOCHROME_BUTTONS.add (ButtonID.TAP_TEMPO);
        MONOCHROME_BUTTONS.add (ButtonID.TRACK);
        MONOCHROME_BUTTONS.add (ButtonID.UNDO);
        MONOCHROME_BUTTONS.add (ButtonID.USER);
        MONOCHROME_BUTTONS.add (ButtonID.VOLUME);
    }

    private final PushVersion pushVersion;


    /**
     * Private due to utility class.
     *
     * @param pushVersion The version of Push
     */
    public PushColorManager (final PushVersion pushVersion)
    {
        this.pushVersion = pushVersion;

        final boolean isModern = this.pushVersion != PushVersion.VERSION_1;
        final boolean isPush3 = this.pushVersion == PushVersion.VERSION_3;

        this.registerColorIndex (PUSH_BLACK, isModern ? PUSH2_COLOR_BLACK : PUSH1_COLOR_BLACK);
        this.registerColorIndex (PUSH_RED, isModern ? PUSH2_COLOR_RED_HI : PUSH1_COLOR_RED_HI);
        this.registerColorIndex (PUSH_RED_LO, isModern ? PUSH2_COLOR_RED_LO : PUSH1_COLOR_RED_LO);
        this.registerColorIndex (PUSH_RED_HI, isModern ? PUSH2_COLOR_RED_HI : PUSH1_COLOR_RED_HI);
        this.registerColorIndex (PUSH_ORANGE_LO, isModern ? PUSH2_COLOR_ORANGE_LO : PUSH1_COLOR_ORANGE_LO);
        this.registerColorIndex (PUSH_ORANGE_HI, isModern ? PUSH2_COLOR_ORANGE_HI : PUSH1_COLOR_ORANGE_HI);
        this.registerColorIndex (PUSH_YELLOW_LO, isModern ? PUSH2_COLOR_YELLOW_LO : PUSH1_COLOR_YELLOW_LO);
        this.registerColorIndex (PUSH_YELLOW_MD, isModern ? PUSH2_COLOR_YELLOW_MD : PUSH1_COLOR_YELLOW_MD);
        this.registerColorIndex (PUSH_GREEN_LO, isModern ? PUSH2_COLOR_GREEN_LO : PUSH1_COLOR_GREEN_LO);
        this.registerColorIndex (PUSH_GREEN_HI, isModern ? PUSH2_COLOR_GREEN_HI : PUSH1_COLOR_GREEN_HI);

        this.registerColorIndex (PUSH_BLACK_2, isModern ? PUSH2_COLOR2_BLACK : PUSH1_COLOR2_BLACK);
        this.registerColorIndex (PUSH_WHITE_2, isModern ? PUSH2_COLOR2_WHITE : PUSH1_COLOR2_WHITE);
        this.registerColorIndex (PUSH_GREY_LO_2, isModern ? PUSH2_COLOR2_GREY_LO : PUSH1_COLOR2_GREY_LO);
        this.registerColorIndex (PUSH_GREEN_2, isModern ? PUSH2_COLOR2_GREEN : PUSH1_COLOR2_GREEN);

        this.registerColorIndex (Scales.SCALE_COLOR_OFF, isModern ? PUSH2_COLOR2_BLACK : PUSH1_COLOR2_BLACK);
        this.registerColorIndex (Scales.SCALE_COLOR_OCTAVE, isModern ? PUSH2_COLOR2_OCEAN_HI : PUSH1_COLOR2_OCEAN_HI);
        this.registerColorIndex (Scales.SCALE_COLOR_NOTE, isModern ? PUSH2_COLOR2_WHITE : PUSH1_COLOR2_WHITE);
        this.registerColorIndex (Scales.SCALE_COLOR_OUT_OF_SCALE, isModern ? PUSH2_COLOR_BLACK : PUSH1_COLOR_BLACK);

        this.registerColorIndex (AbstractFeatureGroup.BUTTON_COLOR_OFF, isModern ? PUSH2_COLOR_BLACK : PUSH1_COLOR_BLACK);
        this.registerColorIndex (AbstractFeatureGroup.BUTTON_COLOR_ON, isModern ? PUSH2_COLOR2_GREY_LO : PUSH1_COLOR_GREEN_LO);
        this.registerColorIndex (AbstractMode.BUTTON_COLOR_HI, isModern ? PUSH2_COLOR2_WHITE : PUSH1_COLOR_YELLOW_MD);
        this.registerColorIndex (AbstractMode.BUTTON_COLOR2_ON, isModern ? PUSH2_COLOR2_GREY_LO : PUSH1_COLOR2_GREEN_LO);
        this.registerColorIndex (AbstractMode.BUTTON_COLOR2_HI, isModern ? PUSH2_COLOR2_WHITE : PUSH1_COLOR2_YELLOW_HI);

        this.registerColorIndex (AbstractSequencerView.COLOR_NO_CONTENT, isModern ? PUSH2_COLOR2_BLACK : PUSH1_COLOR2_BLACK);
        this.registerColorIndex (AbstractSequencerView.COLOR_NO_CONTENT_4, isModern ? PUSH2_COLOR2_BLACK : PUSH1_COLOR2_BLACK);
        this.registerColorIndex (AbstractSequencerView.COLOR_CONTENT, isModern ? PUSH2_COLOR2_BLUE_HI : PUSH1_COLOR2_BLUE_HI);
        this.registerColorIndex (AbstractSequencerView.COLOR_CONTENT_CONT, isModern ? PUSH2_COLOR2_BLUE_LO : PUSH1_COLOR2_BLUE_LO);
        this.registerColorIndex (AbstractSequencerView.COLOR_STEP_HILITE_NO_CONTENT, isModern ? PUSH2_COLOR2_GREEN_LO : PUSH1_COLOR2_GREEN_LO);
        this.registerColorIndex (AbstractSequencerView.COLOR_STEP_HILITE_CONTENT, isModern ? PUSH2_COLOR2_GREEN_HI : PUSH1_COLOR2_GREEN_HI);
        this.registerColorIndex (AbstractSequencerView.COLOR_STEP_MUTED, isModern ? PUSH2_COLOR2_GREY_MD : PUSH1_COLOR2_GREY_MD);
        this.registerColorIndex (AbstractSequencerView.COLOR_STEP_MUTED_CONT, isModern ? PUSH2_COLOR2_GREY_LO : PUSH1_COLOR2_GREY_LO);
        this.registerColorIndex (AbstractSequencerView.COLOR_STEP_SELECTED, isModern ? PUSH2_COLOR2_YELLOW_HI : PUSH1_COLOR2_YELLOW_HI);
        this.registerColorIndex (AbstractSequencerView.COLOR_PAGE, isModern ? PUSH2_COLOR2_WHITE : PUSH1_COLOR2_WHITE);
        this.registerColorIndex (AbstractSequencerView.COLOR_ACTIVE_PAGE, isModern ? PUSH2_COLOR2_GREEN : PUSH1_COLOR2_GREEN);
        this.registerColorIndex (AbstractSequencerView.COLOR_SELECTED_PAGE, isModern ? PUSH2_COLOR2_OCEAN_HI : PUSH1_COLOR2_OCEAN_HI);
        this.registerColorIndex (AbstractSequencerView.COLOR_RESOLUTION, isModern ? PUSH2_COLOR_SCENE_ORANGE : PUSH1_COLOR_SCENE_ORANGE);
        this.registerColorIndex (AbstractSequencerView.COLOR_RESOLUTION_SELECTED, isModern ? PUSH2_COLOR_SCENE_ORANGE_HI : PUSH1_COLOR_SCENE_ORANGE_HI);
        this.registerColorIndex (AbstractSequencerView.COLOR_RESOLUTION_OFF, isModern ? PUSH2_COLOR2_BLACK : PUSH1_COLOR2_BLACK);
        this.registerColorIndex (AbstractSequencerView.COLOR_TRANSPOSE, isModern ? PUSH2_COLOR_SCENE_WHITE : PUSH1_COLOR_SCENE_YELLOW);
        this.registerColorIndex (AbstractSequencerView.COLOR_TRANSPOSE_SELECTED, isModern ? PUSH2_COLOR_SCENE_YELLOW_HI : PUSH1_COLOR_SCENE_YELLOW_HI);

        this.registerColorIndex (AbstractDrumView.COLOR_PAD_OFF, isModern ? PUSH2_COLOR_BLACK : PUSH1_COLOR_BLACK);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_RECORD, isModern ? PUSH2_COLOR2_RED_HI : PUSH1_COLOR2_RED_HI);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_PLAY, isModern ? PUSH2_COLOR2_GREEN_HI : PUSH1_COLOR2_GREEN_HI);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_SELECTED, isModern ? PUSH2_COLOR2_BLUE_HI : PUSH1_COLOR2_BLUE_HI);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_MUTED, isModern ? PUSH2_COLOR2_AMBER_LO : PUSH1_COLOR2_AMBER_LO);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_HAS_CONTENT, isModern ? PUSH2_COLOR2_YELLOW_HI : PUSH1_COLOR2_YELLOW_HI);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_NO_CONTENT, isModern ? PUSH2_COLOR2_YELLOW_LO : PUSH1_COLOR2_YELLOW_LO);

        this.registerColorIndex (AbstractPlayView.COLOR_PLAY, isModern ? PUSH2_COLOR2_GREEN_HI : PUSH1_COLOR2_GREEN_HI);
        this.registerColorIndex (AbstractPlayView.COLOR_RECORD, isModern ? PUSH2_COLOR2_RED_HI : PUSH1_COLOR2_RED_HI);
        this.registerColorIndex (AbstractPlayView.COLOR_OFF, isModern ? PUSH2_COLOR2_BLACK : PUSH1_COLOR2_BLACK);

        this.registerColorIndex (ClipLengthView.COLOR_OUTSIDE, isModern ? PUSH2_COLOR_BLACK : PUSH1_COLOR_BLACK);
        this.registerColorIndex (ClipLengthView.COLOR_PART, isModern ? PUSH2_COLOR2_OCEAN_HI : PUSH1_COLOR2_OCEAN_HI);

        this.registerColorIndex (AbstractSessionView.COLOR_SCENE, isModern ? PUSH2_COLOR_SCENE_GREEN : PUSH1_COLOR_SCENE_GREEN);
        this.registerColorIndex (AbstractSessionView.COLOR_SELECTED_SCENE, isModern ? PUSH2_COLOR_SCENE_GREEN_HI : PUSH1_COLOR_SCENE_GREEN_HI);
        this.registerColorIndex (AbstractSessionView.COLOR_SCENE_OFF, isModern ? PUSH2_COLOR2_BLACK : PUSH1_COLOR2_BLACK);

        this.registerColorIndex (ScenePlayView.COLOR_SELECTED_PLAY_SCENE, isModern ? PUSH2_COLOR2_WHITE : PUSH1_COLOR2_WHITE);

        this.registerColorIndex (IPadGrid.GRID_OFF, isModern ? PUSH2_COLOR2_BLACK : PUSH1_COLOR2_BLACK);

        this.registerColorIndex (NOTE_REPEAT_PERIOD_OFF, isModern ? PUSH2_COLOR_SCENE_YELLOW : PUSH1_COLOR_SCENE_YELLOW);
        this.registerColorIndex (NOTE_REPEAT_PERIOD_HI, isModern ? PUSH2_COLOR_SCENE_YELLOW_HI : PUSH1_COLOR_SCENE_YELLOW_HI);
        this.registerColorIndex (NOTE_REPEAT_LENGTH_OFF, isModern ? PUSH2_COLOR_SCENE_RED : PUSH1_COLOR_SCENE_RED);
        this.registerColorIndex (NOTE_REPEAT_LENGTH_HI, isModern ? PUSH2_COLOR_SCENE_RED_HI : PUSH1_COLOR_SCENE_RED_HI);

        // Push 2 DAW colors are set in the color palette from indices 70 to 96
        this.registerColorIndex (DAWColor.COLOR_OFF, isModern ? PUSH2_COLOR2_BLACK : PUSH1_COLOR2_BLACK);
        this.registerColorIndex (DAWColor.DAW_COLOR_GRAY_HALF, isModern ? 70 : PUSH1_COLOR2_GREY_MD);
        this.registerColorIndex (DAWColor.DAW_COLOR_DARK_GRAY, isModern ? 71 : 1);
        this.registerColorIndex (DAWColor.DAW_COLOR_GRAY, isModern ? 72 : PUSH1_COLOR2_GREY_MD);
        this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_GRAY, isModern ? 73 : PUSH1_COLOR2_GREY_LT);
        this.registerColorIndex (DAWColor.DAW_COLOR_SILVER, isModern ? 74 : 40);
        this.registerColorIndex (DAWColor.DAW_COLOR_DARK_BROWN, isModern ? 75 : 11);
        this.registerColorIndex (DAWColor.DAW_COLOR_BROWN, isModern ? 76 : 12);
        this.registerColorIndex (DAWColor.DAW_COLOR_DARK_BLUE, isModern ? 77 : 42);
        this.registerColorIndex (DAWColor.DAW_COLOR_PURPLE_BLUE, isModern ? 78 : 44);
        this.registerColorIndex (DAWColor.DAW_COLOR_PURPLE, isModern ? 79 : 81);
        this.registerColorIndex (DAWColor.DAW_COLOR_PINK, isModern ? 80 : 57);
        this.registerColorIndex (DAWColor.DAW_COLOR_RED, isModern ? 81 : 6);
        this.registerColorIndex (DAWColor.DAW_COLOR_ORANGE, isModern ? 82 : 60);
        this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_ORANGE, isModern ? 83 : 62);
        this.registerColorIndex (DAWColor.DAW_COLOR_MOSS_GREEN, isModern ? 84 : 19);
        this.registerColorIndex (DAWColor.DAW_COLOR_GREEN, isModern ? 85 : 26);
        this.registerColorIndex (DAWColor.DAW_COLOR_COLD_GREEN, isModern ? 86 : 30);
        this.registerColorIndex (DAWColor.DAW_COLOR_BLUE, isModern ? 87 : 37);
        this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_PURPLE, isModern ? 88 : 48);
        this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_PINK, isModern ? 89 : 56);
        this.registerColorIndex (DAWColor.DAW_COLOR_ROSE, isModern ? 90 : 4);
        this.registerColorIndex (DAWColor.DAW_COLOR_REDDISH_BROWN, isModern ? 91 : 10);
        this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_BROWN, isModern ? 92 : 61);
        this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_GREEN, isModern ? 93 : 18);
        this.registerColorIndex (DAWColor.DAW_COLOR_BLUISH_GREEN, isModern ? 94 : 25);
        this.registerColorIndex (DAWColor.DAW_COLOR_GREEN_BLUE, isModern ? 95 : 32);
        this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_BLUE, isModern ? 96 : 41);

        this.registerColorIndex (ColorManager.BUTTON_STATE_OFF, 0);
        this.registerColorIndex (ColorManager.BUTTON_STATE_ON, isModern ? 30 : 1);
        this.registerColorIndex (ColorManager.BUTTON_STATE_HI, isModern ? 127 : 4);
        this.registerColorIndex (PUSH_BUTTON_STATE_REC_ON, isModern ? PUSH2_COLOR2_GREY_LO : 1);
        this.registerColorIndex (PUSH_BUTTON_STATE_REC_HI, isModern ? PUSH2_COLOR2_RED_HI : 4);
        this.registerColorIndex (PUSH_BUTTON_STATE_OVR_ON, isModern ? PUSH2_COLOR2_GREY_LO : 1);
        this.registerColorIndex (PUSH_BUTTON_STATE_OVR_HI, isModern ? PUSH2_COLOR2_AMBER : 2);
        this.registerColorIndex (PUSH_BUTTON_STATE_PLAY_ON, isModern ? PUSH2_COLOR2_GREY_LO : 1);
        this.registerColorIndex (PUSH_BUTTON_STATE_PLAY_HI, isModern ? PUSH2_COLOR2_GREEN_HI : 4);
        this.registerColorIndex (PUSH_BUTTON_STATE_MUTE_ON, isModern ? PUSH2_COLOR2_GREY_LO : 1);
        this.registerColorIndex (PUSH_BUTTON_STATE_MUTE_HI, isModern ? PUSH2_COLOR2_AMBER_LO : 4);
        this.registerColorIndex (PUSH_BUTTON_STATE_SOLO_ON, isModern ? PUSH2_COLOR2_GREY_LO : 1);
        this.registerColorIndex (PUSH_BUTTON_STATE_SOLO_HI, isModern ? PUSH2_COLOR2_YELLOW : 4);
        this.registerColorIndex (PUSH_BUTTON_STATE_STOP_ON, isModern ? PUSH2_COLOR2_RED_LO : 1);
        this.registerColorIndex (PUSH_BUTTON_STATE_STOP_HI, isModern ? PUSH2_COLOR2_RED_HI : 4);

        this.registerColorIndex (PUSH_BUTTON_STATE_MASTER_ON, isPush3 ? PUSH2_COLOR2_GREY_LO : 30);
        this.registerColorIndex (PUSH_BUTTON_STATE_MASTER_HI, isPush3 ? PUSH2_COLOR2_WHITE : 127);

        // Only Push 3
        this.registerColorIndex (PUSH_BUTTON_STATE_LOCK_ON, PUSH2_COLOR2_GREY_LO);
        this.registerColorIndex (PUSH_BUTTON_STATE_LOCK_HI, PUSH2_COLOR2_RED_LO);
        this.registerColorIndex (PUSH_BUTTON_STATE_INSERT_SCENE_ON, PUSH2_COLOR2_GREY_LO);
        this.registerColorIndex (PUSH_BUTTON_STATE_INSERT_SCENE_HI, PUSH2_COLOR2_WHITE);

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

        if (MONOCHROME_BUTTONS.contains (buttonID))
        {
            if (colorIndex == this.getColorIndex (ColorManager.BUTTON_STATE_OFF))
                return ColorEx.BLACK;
            if (colorIndex == this.getColorIndex (ColorManager.BUTTON_STATE_ON))
                return ColorEx.DARK_GRAY;
            // ColorManager.BUTTON_STATE_HI
            return ColorEx.LIGHT_GRAY;
        }

        if (this.pushVersion == PushVersion.VERSION_1)
        {
            switch (buttonID)
            {
                case PLAY:
                    return this.colorByIndex.get (Integer.valueOf (colorIndex == 1 ? PUSH2_COLOR2_GREY_LO : PUSH2_COLOR2_GREEN_HI));
                case AUTOMATION, RECORD:
                    if (colorIndex == 1)
                        return ColorEx.DARK_GRAY;
                    if (colorIndex == 4)
                        return ColorEx.RED;
                    return ColorEx.DARK_RED;
                case MUTE:
                    return colorIndex == 1 ? ColorEx.DARK_ORANGE : ColorEx.ORANGE;
                case SOLO:
                    return colorIndex == 1 ? ColorEx.DARK_BLUE : ColorEx.BLUE;
                case STOP_CLIP:
                    return colorIndex == 1 ? ColorEx.DARK_RED : ColorEx.RED;
                default:
                    // Fall through...
                    break;
            }
        }

        final ColorEx color = this.colorByIndex.get (Integer.valueOf (colorIndex));
        if (color == null)
            throw new ColorIndexException ("Color for index " + colorIndex + " is not registered!");
        return color;
    }
}