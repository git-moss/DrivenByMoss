// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.apc.controller;

import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.featuregroup.AbstractFeatureGroup;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.view.AbstractPlayView;
import de.mossgrabers.framework.view.AbstractSessionView;
import de.mossgrabers.framework.view.sequencer.AbstractDrumExView;
import de.mossgrabers.framework.view.sequencer.AbstractDrumView;
import de.mossgrabers.framework.view.sequencer.AbstractSequencerView;


/**
 * Different colors to use for the pads and buttons of APC40 mkI and mkII.
 *
 * @author Jürgen Moßgraber
 */
@SuppressWarnings("javadoc")
public class APCColorManager extends ColorManager
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

    private final boolean      isMkII;


    /**
     * Constructor.
     *
     * @param isMkII True if it is the MkII
     */
    public APCColorManager (final boolean isMkII)
    {
        this.isMkII = isMkII;

        this.registerColorIndex (Scales.SCALE_COLOR_OFF, isMkII ? APC_MKII_COLOR_BLACK : APC_COLOR_BLACK);
        this.registerColorIndex (Scales.SCALE_COLOR_OCTAVE, isMkII ? APC_MKII_COLOR_OCEAN_HI : APC_COLOR_YELLOW);
        this.registerColorIndex (Scales.SCALE_COLOR_NOTE, isMkII ? APC_MKII_COLOR_WHITE : APC_COLOR_BLACK);
        this.registerColorIndex (Scales.SCALE_COLOR_OUT_OF_SCALE, isMkII ? APC_MKII_COLOR_BLACK : APC_COLOR_BLACK);

        this.registerColorIndex (AbstractFeatureGroup.BUTTON_COLOR_OFF, isMkII ? APC_MKII_COLOR_BLACK : APC_COLOR_BLACK);

        this.registerColorIndex (AbstractSessionView.COLOR_SCENE, isMkII ? APC_MKII_COLOR_GREEN : APC_COLOR_GREEN);
        this.registerColorIndex (AbstractSessionView.COLOR_SELECTED_SCENE, isMkII ? APC_MKII_COLOR_GREEN_HI : APC_COLOR_GREEN_BLINK);
        this.registerColorIndex (AbstractSessionView.COLOR_SCENE_OFF, isMkII ? APC_MKII_COLOR_BLACK : APC_COLOR_BLACK);

        this.registerColorIndex (AbstractSequencerView.COLOR_STEP_HILITE_NO_CONTENT, isMkII ? APC_MKII_COLOR_GREEN_HI : APC_COLOR_GREEN);
        this.registerColorIndex (AbstractSequencerView.COLOR_STEP_HILITE_CONTENT, isMkII ? APC_MKII_COLOR_GREEN_LO : APC_COLOR_GREEN);
        this.registerColorIndex (AbstractSequencerView.COLOR_STEP_MUTED, isMkII ? APC_MKII_COLOR_GREY_MD : APC_COLOR_GREEN);
        this.registerColorIndex (AbstractSequencerView.COLOR_STEP_MUTED_CONT, isMkII ? APC_MKII_COLOR_GREY_LO : APC_COLOR_GREEN);
        this.registerColorIndex (AbstractSequencerView.COLOR_STEP_SELECTED, isMkII ? APC_MKII_COLOR_YELLOW : APC_COLOR_GREEN);
        this.registerColorIndex (AbstractSequencerView.COLOR_NO_CONTENT, isMkII ? APC_MKII_COLOR_BLACK : APC_COLOR_BLACK);
        this.registerColorIndex (AbstractSequencerView.COLOR_NO_CONTENT_4, isMkII ? APC_MKII_COLOR_BLACK : APC_COLOR_BLACK);
        this.registerColorIndex (AbstractSequencerView.COLOR_CONTENT, isMkII ? APC_MKII_COLOR_BLUE_HI : APC_COLOR_RED);
        this.registerColorIndex (AbstractSequencerView.COLOR_CONTENT_CONT, isMkII ? APC_MKII_COLOR_BLUE_LO : APC_COLOR_RED);
        this.registerColorIndex (AbstractSequencerView.COLOR_PAGE, isMkII ? APC_MKII_COLOR_WHITE : APC_COLOR_GREEN);
        this.registerColorIndex (AbstractSequencerView.COLOR_ACTIVE_PAGE, isMkII ? APC_MKII_COLOR_GREEN_HI : APC_COLOR_YELLOW);
        this.registerColorIndex (AbstractSequencerView.COLOR_SELECTED_PAGE, isMkII ? APC_MKII_COLOR_BLUE_LO : APC_COLOR_RED);

        this.registerColorIndex (AbstractDrumView.COLOR_PAD_OFF, isMkII ? APC_MKII_COLOR_BLACK : APC_COLOR_BLACK);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_RECORD, isMkII ? APC_MKII_COLOR_RED_HI : APC_COLOR_RED);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_PLAY, isMkII ? APC_MKII_COLOR_GREEN_HI : APC_COLOR_GREEN);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_SELECTED, isMkII ? APC_MKII_COLOR_BLUE_HI : APC_COLOR_YELLOW_BLINK);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_MUTED, isMkII ? APC_MKII_COLOR_AMBER_LO : APC_COLOR_BLACK);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_HAS_CONTENT, isMkII ? APC_MKII_COLOR_YELLOW_HI : APC_COLOR_YELLOW);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_NO_CONTENT, isMkII ? APC_MKII_COLOR_YELLOW_LO : APC_COLOR_BLACK);

        this.registerColorIndex (AbstractDrumExView.COLOR_EX_SELECT_ON, isMkII ? APC_MKII_COLOR_WHITE : APC_COLOR_GREEN_BLINK);
        this.registerColorIndex (AbstractDrumExView.COLOR_EX_SELECT_OFF, isMkII ? APC_MKII_COLOR_GREY_LO : APC_COLOR_GREEN);
        this.registerColorIndex (AbstractDrumExView.COLOR_EX_MUTE_ON, isMkII ? APC_MKII_COLOR_YELLOW_HI : APC_COLOR_RED_BLINK);
        this.registerColorIndex (AbstractDrumExView.COLOR_EX_MUTE_OFF, isMkII ? APC_MKII_COLOR_YELLOW_LO : APC_COLOR_RED);
        this.registerColorIndex (AbstractDrumExView.COLOR_EX_SOLO_ON, isMkII ? APC_MKII_COLOR_BLUE_HI : APC_COLOR_YELLOW_BLINK);
        this.registerColorIndex (AbstractDrumExView.COLOR_EX_SOLO_OFF, isMkII ? APC_MKII_COLOR_BLUE_LO : APC_COLOR_YELLOW);
        this.registerColorIndex (AbstractDrumExView.COLOR_EX_BROWSE_ON, isMkII ? APC_MKII_COLOR_CYAN_HI : APC_COLOR_GREEN_BLINK);
        this.registerColorIndex (AbstractDrumExView.COLOR_EX_BROWSE_OFF, isMkII ? APC_MKII_COLOR_CYAN_LO : APC_COLOR_GREEN);
        this.registerColorIndex (AbstractDrumExView.COLOR_EX_NOTE_REPEAT_ON, isMkII ? APC_MKII_COLOR_ORCHID_HI : APC_COLOR_RED_BLINK);
        this.registerColorIndex (AbstractDrumExView.COLOR_EX_NOTE_REPEAT_OFF, isMkII ? APC_MKII_COLOR_ORCHID_LO : APC_COLOR_RED);
        this.registerColorIndex (AbstractDrumExView.COLOR_EX_NOTE_REPEAT_PERIOD_ON, isMkII ? APC_MKII_COLOR_SKY_HI : APC_COLOR_YELLOW_BLINK);
        this.registerColorIndex (AbstractDrumExView.COLOR_EX_NOTE_REPEAT_PERIOD_OFF, isMkII ? APC_MKII_COLOR_SKY_LO : APC_COLOR_YELLOW);
        this.registerColorIndex (AbstractDrumExView.COLOR_EX_NOTE_REPEAT_LENGTH_ON, isMkII ? APC_MKII_COLOR_PINK_HI : APC_COLOR_GREEN_BLINK);
        this.registerColorIndex (AbstractDrumExView.COLOR_EX_NOTE_REPEAT_LENGTH_OFF, isMkII ? APC_MKII_COLOR_PINK_LO : APC_COLOR_GREEN);
        this.registerColorIndex (AbstractDrumExView.COLOR_EX_TOGGLE_ON, isMkII ? APC_MKII_COLOR_BLACK : APC_COLOR_BLACK);
        this.registerColorIndex (AbstractDrumExView.COLOR_EX_TOGGLE_OFF, isMkII ? APC_MKII_COLOR_BLACK : APC_COLOR_BLACK);

        this.registerColorIndex (AbstractPlayView.COLOR_PLAY, isMkII ? APC_MKII_COLOR_GREEN : APC_COLOR_GREEN);
        this.registerColorIndex (AbstractPlayView.COLOR_RECORD, isMkII ? APC_MKII_COLOR_RED : APC_COLOR_RED);
        this.registerColorIndex (AbstractPlayView.COLOR_OFF, isMkII ? APC_MKII_COLOR_BLACK : APC_COLOR_BLACK);

        this.registerColorIndex (IPadGrid.GRID_OFF, isMkII ? APC_MKII_COLOR_BLACK : APC_COLOR_BLACK);

        this.registerColorIndex (DAWColor.COLOR_OFF, isMkII ? APC_MKII_COLOR_BLACK : APC_COLOR_BLACK);
        if (isMkII)
        {
            this.registerColorIndex (DAWColor.DAW_COLOR_GRAY_HALF, APC_MKII_COLOR_GREY_MD);
            this.registerColorIndex (DAWColor.DAW_COLOR_DARK_GRAY, APC_MKII_COLOR_GREY_LO);
            this.registerColorIndex (DAWColor.DAW_COLOR_GRAY, APC_MKII_COLOR_GREY_MD);
            this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_GRAY, APC_MKII_COLOR_GREY_LT);
            this.registerColorIndex (DAWColor.DAW_COLOR_SILVER, APC_MKII_COLOR_SKY_OCEAN);
            this.registerColorIndex (DAWColor.DAW_COLOR_DARK_BROWN, APC_MKII_COLOR_AMBER_LO);
            this.registerColorIndex (DAWColor.DAW_COLOR_BROWN, APC_MKII_COLOR_AMBER_YELLOW);
            this.registerColorIndex (DAWColor.DAW_COLOR_DARK_BLUE, APC_MKII_COLOR_OCEAN);
            this.registerColorIndex (DAWColor.DAW_COLOR_PURPLE_BLUE, APC_MKII_COLOR_OCEAN_BLUE);
            this.registerColorIndex (DAWColor.DAW_COLOR_PURPLE, APC_MKII_COLOR_PINK);
            this.registerColorIndex (DAWColor.DAW_COLOR_PINK, APC_MKII_COLOR_PINK_HI);
            this.registerColorIndex (DAWColor.DAW_COLOR_RED, APC_MKII_COLOR_RED);
            this.registerColorIndex (DAWColor.DAW_COLOR_ORANGE, APC_MKII_COLOR_AMBER);
            this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_ORANGE, APC_MKII_COLOR_YELLOW);
            this.registerColorIndex (DAWColor.DAW_COLOR_MOSS_GREEN, APC_MKII_COLOR_SPRING);
            this.registerColorIndex (DAWColor.DAW_COLOR_GREEN, APC_MKII_COLOR_LIME_LO);
            this.registerColorIndex (DAWColor.DAW_COLOR_COLD_GREEN, APC_MKII_COLOR_TURQUOISE);
            this.registerColorIndex (DAWColor.DAW_COLOR_BLUE, APC_MKII_COLOR_SKY_HI);
            this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_PURPLE, APC_MKII_COLOR_BLUE_ORCHID);
            this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_PINK, APC_MKII_COLOR_MAGENTA_PINK);
            this.registerColorIndex (DAWColor.DAW_COLOR_ROSE, APC_MKII_COLOR_ROSE);
            this.registerColorIndex (DAWColor.DAW_COLOR_REDDISH_BROWN, APC_MKII_COLOR_AMBER);
            this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_BROWN, APC_MKII_COLOR_AMBER_HI);
            this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_GREEN, APC_MKII_COLOR_LIME);
            this.registerColorIndex (DAWColor.DAW_COLOR_BLUISH_GREEN, APC_MKII_COLOR_TURQUOISE_CYAN);
            this.registerColorIndex (DAWColor.DAW_COLOR_GREEN_BLUE, APC_MKII_COLOR_SPRING_HI);
            this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_BLUE, APC_MKII_COLOR_OCEAN_HI);
        }

        this.registerColorIndex (ColorManager.BUTTON_STATE_OFF, isMkII ? APC_MKII_COLOR_BLACK : APC_COLOR_BLACK);
        this.registerColorIndex (ColorManager.BUTTON_STATE_ON, 1);
        this.registerColorIndex (ColorManager.BUTTON_STATE_HI, 2);
        this.registerColorIndex (BUTTON_STATE_BLINK, 3);

        this.registerColorIndex (COLOR_VIEW_SELECTED, isMkII ? APC_MKII_COLOR_GREEN : APC_COLOR_GREEN);
        this.registerColorIndex (COLOR_VIEW_UNSELECTED, isMkII ? APC_MKII_COLOR_AMBER : APC_COLOR_BLACK);
        this.registerColorIndex (COLOR_VIEW_OFF, isMkII ? APC_MKII_COLOR_BLACK : APC_COLOR_BLACK);
        this.registerColorIndex (COLOR_KEY_WHITE, isMkII ? APC_MKII_COLOR_AMBER_LO : APC_COLOR_GREEN);
        this.registerColorIndex (COLOR_KEY_BLACK, isMkII ? APC_MKII_COLOR_RED_HI : APC_COLOR_RED);
        this.registerColorIndex (COLOR_KEY_SELECTED, isMkII ? APC_MKII_COLOR_GREEN_HI : APC_COLOR_YELLOW);

        if (isMkII)
        {
            for (int i = 0; i < 128; i++)
                this.registerColor (i, ColorEx.BLACK);

            for (final DAWColor dc: DAWColor.values ())
            {
                final String name = dc.name ();
                this.registerColor (this.getColorIndex (name), DAWColor.getColorEntry (name));
            }

            this.registerColor (APC_MKII_COLOR_BLACK, ColorEx.BLACK);
            this.registerColor (APC_MKII_COLOR_WHITE, ColorEx.WHITE);
            this.registerColor (APC_MKII_COLOR_RED_HI, ColorEx.RED);
            this.registerColor (APC_MKII_COLOR_YELLOW_HI, ColorEx.YELLOW);
            this.registerColor (APC_MKII_COLOR_YELLOW_LO, ColorEx.evenDarker (ColorEx.YELLOW));
            this.registerColor (APC_MKII_COLOR_GREEN_HI, ColorEx.GREEN);
            this.registerColor (APC_MKII_COLOR_GREEN, ColorEx.darker (ColorEx.GREEN));
            this.registerColor (APC_MKII_COLOR_GREEN_LO, ColorEx.evenDarker (ColorEx.GREEN));
            this.registerColor (APC_MKII_COLOR_BLUE_HI, ColorEx.brighter (ColorEx.BLUE));
            this.registerColor (APC_MKII_COLOR_BLUE, ColorEx.BLUE);
            this.registerColor (APC_MKII_COLOR_BLUE_LO, ColorEx.evenDarker (ColorEx.BLUE));
        }
        else
        {
            this.registerColor (APC_COLOR_BLACK, ColorEx.BLACK);
            this.registerColor (APC_COLOR_GREEN, ColorEx.GREEN);
            this.registerColor (APC_COLOR_GREEN_BLINK, ColorEx.GREEN);
            this.registerColor (APC_COLOR_RED, ColorEx.RED);
            this.registerColor (APC_COLOR_RED_BLINK, ColorEx.RED);
            this.registerColor (APC_COLOR_YELLOW, ColorEx.YELLOW);
            this.registerColor (APC_COLOR_YELLOW_BLINK, ColorEx.YELLOW);

            for (int i = APC_COLOR_YELLOW_BLINK + 1; i < 128; i++)
                this.registerColor (i, ColorEx.BLACK);
        }
    }


    /** {@inheritDoc} */
    @Override
    public ColorEx getColor (final int colorIndex, final ButtonID buttonID)
    {
        switch (buttonID)
        {
            case ROW1_1:
            case ROW1_2:
            case ROW1_3:
            case ROW1_4:
            case ROW1_5:
            case ROW1_6:
            case ROW1_7:
            case ROW1_8:
            case ROW3_1:
            case ROW3_2:
            case ROW3_3:
            case ROW3_4:
            case ROW3_5:
            case ROW3_6:
            case ROW3_7:
            case ROW3_8:
            case ROW6_1:
            case ROW6_2:
            case ROW6_3:
            case ROW6_4:
            case ROW6_5:
            case ROW6_6:
            case ROW6_7:
            case ROW6_8:
            case MASTERTRACK:
            case METRONOME:
            case BANK_LEFT:
            case BANK_RIGHT:
            case DEVICE_LEFT:
            case DEVICE_RIGHT:
            case LAYOUT:
            case DEVICE_ON_OFF:
            case TOGGLE_DEVICES_PANE:
            case BROWSE:
            case QUANTIZE:
            case PAN_SEND:
            case SEND1:
            case SEND2:
            case SEND3:
            case STOP_ALL_CLIPS:
                if (this.isMkII)
                    return colorIndex > 0 ? ColorEx.ORANGE : ColorEx.BLACK;
                return colorIndex > 0 ? ColorEx.GREEN : ColorEx.BLACK;

            case ROW2_1:
            case ROW2_2:
            case ROW2_3:
            case ROW2_4:
            case ROW2_5:
            case ROW2_6:
            case ROW2_7:
            case ROW2_8:
                return colorIndex > 0 ? ColorEx.BLUE : ColorEx.BLACK;

            case ROW4_1:
            case ROW4_2:
            case ROW4_3:
            case ROW4_4:
            case ROW4_5:
            case ROW4_6:
            case ROW4_7:
            case ROW4_8:
                return colorIndex > 0 ? ColorEx.RED : ColorEx.BLACK;

            case PLAY:
            case STOP:
                return colorIndex > 0 ? ColorEx.GREEN : ColorEx.BLACK;

            case RECORD:
                return colorIndex > 0 ? ColorEx.RED : ColorEx.BLACK;

            case CLIP:
                if (this.isMkII)
                    return colorIndex > 0 ? ColorEx.RED : ColorEx.BLACK;
                return colorIndex > 0 ? ColorEx.GREEN : ColorEx.BLACK;

            default:
                return super.getColor (colorIndex, buttonID);
        }
    }
}