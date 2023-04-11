// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.slmkiii.controller;

import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.featuregroup.AbstractFeatureGroup;
import de.mossgrabers.framework.featuregroup.AbstractMode;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.view.AbstractPlayView;
import de.mossgrabers.framework.view.AbstractSessionView;
import de.mossgrabers.framework.view.sequencer.AbstractDrumView;
import de.mossgrabers.framework.view.sequencer.AbstractSequencerView;


/**
 * Different colors to use for the pads and buttons of Novation SL MkIII.
 *
 * @author Jürgen Moßgraber
 */
@SuppressWarnings("javadoc")
public class SLMkIIIColorManager extends ColorManager
{
    public static final int    SLMKIII_BLACK            = 0;
    public static final int    SLMKIII_DARK_GREY        = 1;
    public static final int    SLMKIII_GREY             = 2;
    public static final int    SLMKIII_WHITE            = 3;
    public static final int    SLMKIII_WHITE_HALF       = 1;
    public static final int    SLMKIII_RED              = 5;
    public static final int    SLMKIII_DARK_RED         = 6;
    public static final int    SLMKIII_RED_HALF         = 7;
    public static final int    SLMKIII_ROSE             = 8;
    public static final int    SLMKIII_ORANGE           = 9;
    public static final int    SLMKIII_DARK_BROWN       = 10;
    public static final int    SLMKIII_ORANGE_HALF      = 11;
    public static final int    SLMKIII_AMBER            = 96;
    public static final int    SLMKIII_AMBER_HALF       = 14;
    public static final int    SLMKIII_YELLOW_LIGHT     = 12;
    public static final int    SLMKIII_YELLOW           = 13;
    public static final int    SLMKIII_YELLOW_HALF      = 15;
    public static final int    SLMKIII_GREEN_LIGHT      = 16;
    public static final int    SLMKIII_DARK_YELLOW      = 17;
    public static final int    SLMKIII_DARK_GREEN       = 18;
    public static final int    SLMKIII_DARK_YELLOW_HALF = 19;
    public static final int    SLMKIII_GREEN_GRASS      = 20;
    public static final int    SLMKIII_GREEN            = 21;
    public static final int    SLMKIII_DARK_GREEN_HALF  = 22;
    public static final int    SLMKIII_GREEN_HALF       = 27;
    public static final int    SLMKIII_MINT             = 29;
    public static final int    SLMKIII_MINT_HALF        = 31;
    public static final int    SLMKIII_OLIVE            = 34;
    public static final int    SLMKIII_SKY_BLUE         = 36;
    public static final int    SLMKIII_LIGHT_BLUE       = 37;
    public static final int    SLMKIII_BLUE_METAL       = 38;
    public static final int    SLMKIII_LIGHT_BLUE_HALF  = 39;
    public static final int    SLMKIII_BLUE             = 45;
    public static final int    SLMKIII_BLUE_HALF        = 47;
    public static final int    SLMKIII_DARK_BLUE        = 49;
    public static final int    SLMKIII_DARK_BLUE_HALF   = 51;
    public static final int    SLMKIII_PURPLE           = 53;
    public static final int    SLMKIII_PURPLE_HALF      = 54;
    public static final int    SLMKIII_PINK_LIGHT       = 56;
    public static final int    SLMKIII_PINK             = 57;
    public static final int    SLMKIII_RED_WINE         = 58;
    public static final int    SLMKIII_BROWN            = 61;
    public static final int    SLMKIII_BLUE_PURPLISH    = 81;
    public static final int    SLMKIII_PINK_DARK        = 82;
    public static final int    SLMKIII_DARK_ORANGE      = 84;

    /** ID for color when button signals a wind state. */
    public static final String BUTTON_STATE_WIND_ON     = "BUTTON_STATE_WIND_ON";
    /** ID for color when button signals an activated wind state. */
    public static final String BUTTON_STATE_WIND_HI     = "BUTTON_STATE_WIND_HI";
    /** ID for color when button signals a stop clip state. */
    public static final String BUTTON_STATE_STOP_ON     = "BUTTON_STATE_STOP_ON";
    /** ID for color when button signals an activated stop clip state. */
    public static final String BUTTON_STATE_STOP_HI     = "BUTTON_STATE_STOP_HI";
    /** ID for color when button signals a play state. */
    public static final String BUTTON_STATE_PLAY_ON     = "BUTTON_STATE_PLAY_ON";
    /** ID for color when button signals an activated play state. */
    public static final String BUTTON_STATE_PLAY_HI     = "BUTTON_STATE_PLAY_HI";
    /** ID for color when button signals the loop state. */
    public static final String BUTTON_STATE_LOOP_ON     = "BUTTON_STATE_LOOP_ON";
    /** ID for color when button signals an activated loop state. */
    public static final String BUTTON_STATE_LOOP_HI     = "BUTTON_STATE_LOOP_HI";
    /** ID for color when button signals a recording state. */
    public static final String BUTTON_STATE_REC_ON      = "BUTTON_STATE_REC_ON";
    /** ID for color when button signals an activated recording state. */
    public static final String BUTTON_STATE_REC_HI      = "BUTTON_STATE_REC_HI";
    /** ID for color when button signals a clip recording state. */
    public static final String BUTTON_STATE_OVR_ON      = "BUTTON_STATE_OVR_ON";
    /** ID for color when button signals an activated clip recording state. */
    public static final String BUTTON_STATE_OVR_HI      = "BUTTON_STATE_OVR_HI";


    /**
     * Constructor.
     */
    public SLMkIIIColorManager ()
    {
        this.registerColorIndex (Scales.SCALE_COLOR_OFF, SLMKIII_BLACK);
        this.registerColorIndex (Scales.SCALE_COLOR_OCTAVE, SLMKIII_BLUE);
        this.registerColorIndex (Scales.SCALE_COLOR_NOTE, SLMKIII_WHITE);
        this.registerColorIndex (Scales.SCALE_COLOR_OUT_OF_SCALE, SLMKIII_DARK_GREY);

        this.registerColorIndex (AbstractFeatureGroup.BUTTON_COLOR_OFF, SLMKIII_BLACK);
        this.registerColorIndex (AbstractFeatureGroup.BUTTON_COLOR_ON, SLMKIII_WHITE);
        this.registerColorIndex (AbstractMode.BUTTON_COLOR_HI, SLMKIII_WHITE_HALF);

        this.registerColorIndex (AbstractSequencerView.COLOR_STEP_HILITE_NO_CONTENT, SLMkIIIColorManager.SLMKIII_DARK_GREEN);
        this.registerColorIndex (AbstractSequencerView.COLOR_STEP_HILITE_CONTENT, SLMkIIIColorManager.SLMKIII_GREEN);
        this.registerColorIndex (AbstractSequencerView.COLOR_STEP_MUTED, SLMkIIIColorManager.SLMKIII_GREY);
        this.registerColorIndex (AbstractSequencerView.COLOR_STEP_MUTED_CONT, SLMkIIIColorManager.SLMKIII_DARK_GREY);
        this.registerColorIndex (AbstractSequencerView.COLOR_STEP_SELECTED, SLMkIIIColorManager.SLMKIII_YELLOW);
        this.registerColorIndex (AbstractSequencerView.COLOR_NO_CONTENT, SLMkIIIColorManager.SLMKIII_BLACK);
        this.registerColorIndex (AbstractSequencerView.COLOR_NO_CONTENT_4, SLMkIIIColorManager.SLMKIII_BLACK);
        this.registerColorIndex (AbstractSequencerView.COLOR_CONTENT, SLMkIIIColorManager.SLMKIII_BLUE);
        this.registerColorIndex (AbstractSequencerView.COLOR_CONTENT_CONT, SLMkIIIColorManager.SLMKIII_DARK_BLUE);
        this.registerColorIndex (AbstractSequencerView.COLOR_PAGE, SLMkIIIColorManager.SLMKIII_WHITE);
        this.registerColorIndex (AbstractSequencerView.COLOR_ACTIVE_PAGE, SLMkIIIColorManager.SLMKIII_GREEN);
        this.registerColorIndex (AbstractSequencerView.COLOR_SELECTED_PAGE, SLMkIIIColorManager.SLMKIII_BLUE_METAL);
        this.registerColorIndex (AbstractSequencerView.COLOR_RESOLUTION, SLMkIIIColorManager.SLMKIII_ORANGE_HALF);
        this.registerColorIndex (AbstractSequencerView.COLOR_RESOLUTION_SELECTED, SLMkIIIColorManager.SLMKIII_ORANGE);
        this.registerColorIndex (AbstractSequencerView.COLOR_RESOLUTION_OFF, SLMKIII_BLACK);

        this.registerColorIndex (AbstractPlayView.COLOR_OFF, SLMKIII_BLACK);
        this.registerColorIndex (AbstractPlayView.COLOR_PLAY, SLMKIII_GREEN);
        this.registerColorIndex (AbstractPlayView.COLOR_RECORD, SLMKIII_RED);

        this.registerColorIndex (AbstractDrumView.COLOR_PAD_OFF, SLMKIII_BLACK);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_RECORD, SLMKIII_RED);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_PLAY, SLMKIII_GREEN);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_SELECTED, SLMKIII_BLUE);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_MUTED, SLMKIII_AMBER_HALF);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_HAS_CONTENT, SLMKIII_YELLOW);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_NO_CONTENT, SLMKIII_YELLOW_HALF);

        this.registerColorIndex (AbstractSessionView.COLOR_SCENE, SLMKIII_GREEN_LIGHT);
        this.registerColorIndex (AbstractSessionView.COLOR_SELECTED_SCENE, SLMKIII_GREEN);
        this.registerColorIndex (AbstractSessionView.COLOR_SCENE_OFF, SLMKIII_BLACK);

        this.registerColorIndex (IPadGrid.GRID_OFF, SLMKIII_BLACK);

        this.registerColorIndex (DAWColor.COLOR_OFF, SLMKIII_BLACK);
        this.registerColorIndex (DAWColor.DAW_COLOR_GRAY_HALF, SLMKIII_BLACK);

        this.registerColorIndex (DAWColor.DAW_COLOR_DARK_GRAY, SLMKIII_DARK_GREY);
        this.registerColorIndex (DAWColor.DAW_COLOR_GRAY, SLMKIII_WHITE_HALF);
        this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_GRAY, SLMKIII_WHITE);
        this.registerColorIndex (DAWColor.DAW_COLOR_SILVER, SLMKIII_GREY);

        this.registerColorIndex (DAWColor.DAW_COLOR_DARK_BROWN, SLMKIII_DARK_BROWN);
        this.registerColorIndex (DAWColor.DAW_COLOR_BROWN, SLMKIII_BROWN);
        this.registerColorIndex (DAWColor.DAW_COLOR_DARK_BLUE, SLMKIII_DARK_BLUE);
        this.registerColorIndex (DAWColor.DAW_COLOR_PURPLE_BLUE, SLMKIII_BLUE_PURPLISH);
        this.registerColorIndex (DAWColor.DAW_COLOR_PURPLE, SLMKIII_PURPLE_HALF);

        this.registerColorIndex (DAWColor.DAW_COLOR_PINK, SLMKIII_PINK);
        this.registerColorIndex (DAWColor.DAW_COLOR_RED, SLMKIII_RED);
        this.registerColorIndex (DAWColor.DAW_COLOR_ORANGE, SLMKIII_ORANGE);
        this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_ORANGE, SLMKIII_AMBER);
        this.registerColorIndex (DAWColor.DAW_COLOR_MOSS_GREEN, SLMKIII_GREEN_GRASS);
        this.registerColorIndex (DAWColor.DAW_COLOR_GREEN, SLMKIII_GREEN);
        this.registerColorIndex (DAWColor.DAW_COLOR_COLD_GREEN, SLMKIII_GREEN_LIGHT);
        this.registerColorIndex (DAWColor.DAW_COLOR_BLUE, SLMKIII_BLUE);
        this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_PURPLE, SLMKIII_PURPLE);

        this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_PINK, SLMKIII_PINK_LIGHT);
        this.registerColorIndex (DAWColor.DAW_COLOR_ROSE, SLMKIII_ROSE);
        this.registerColorIndex (DAWColor.DAW_COLOR_REDDISH_BROWN, SLMKIII_DARK_RED);
        this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_BROWN, SLMKIII_AMBER_HALF);
        this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_GREEN, SLMKIII_GREEN_LIGHT);
        this.registerColorIndex (DAWColor.DAW_COLOR_BLUISH_GREEN, SLMKIII_MINT);
        this.registerColorIndex (DAWColor.DAW_COLOR_GREEN_BLUE, SLMKIII_LIGHT_BLUE);
        this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_BLUE, SLMKIII_SKY_BLUE);

        this.registerColorIndex (ColorManager.BUTTON_STATE_OFF, SLMKIII_BLACK);
        this.registerColorIndex (ColorManager.BUTTON_STATE_ON, SLMKIII_WHITE);
        this.registerColorIndex (ColorManager.BUTTON_STATE_HI, SLMKIII_WHITE_HALF);

        this.registerColorIndex (BUTTON_STATE_WIND_ON, SLMKIII_YELLOW_HALF);
        this.registerColorIndex (BUTTON_STATE_WIND_HI, SLMKIII_YELLOW);
        this.registerColorIndex (BUTTON_STATE_STOP_ON, SLMKIII_GREY);
        this.registerColorIndex (BUTTON_STATE_STOP_HI, SLMKIII_WHITE);
        this.registerColorIndex (BUTTON_STATE_PLAY_ON, SLMKIII_GREEN_HALF);
        this.registerColorIndex (BUTTON_STATE_PLAY_HI, SLMKIII_GREEN);
        this.registerColorIndex (BUTTON_STATE_LOOP_ON, SLMKIII_BLUE_HALF);
        this.registerColorIndex (BUTTON_STATE_LOOP_HI, SLMKIII_BLUE);
        this.registerColorIndex (BUTTON_STATE_REC_ON, SLMKIII_RED_HALF);
        this.registerColorIndex (BUTTON_STATE_REC_HI, SLMKIII_RED);
        this.registerColorIndex (BUTTON_STATE_OVR_ON, SLMKIII_AMBER_HALF);
        this.registerColorIndex (BUTTON_STATE_OVR_HI, SLMKIII_AMBER);

        for (int i = 0; i < 128; i++)
            this.registerColor (i, ColorEx.BLACK);

        this.registerColor (SLMKIII_BLACK, ColorEx.BLACK);
        this.registerColor (SLMKIII_DARK_GREY, ColorEx.DARK_GRAY);
        this.registerColor (SLMKIII_GREY, ColorEx.GRAY);
        this.registerColor (SLMKIII_WHITE, ColorEx.WHITE);
        this.registerColor (SLMKIII_WHITE_HALF, ColorEx.DARK_GRAY);
        this.registerColor (SLMKIII_RED, ColorEx.RED);
        this.registerColor (SLMKIII_DARK_RED, ColorEx.DARK_RED);
        this.registerColor (SLMKIII_RED_HALF, ColorEx.evenDarker (ColorEx.RED));
        this.registerColor (SLMKIII_ROSE, ColorEx.ROSE);
        this.registerColor (SLMKIII_ORANGE, ColorEx.ORANGE);
        this.registerColor (SLMKIII_DARK_BROWN, ColorEx.DARK_BROWN);
        this.registerColor (SLMKIII_ORANGE_HALF, ColorEx.evenDarker (ColorEx.ORANGE));
        this.registerColor (SLMKIII_AMBER, ColorEx.ORANGE);
        this.registerColor (SLMKIII_AMBER_HALF, ColorEx.darker (ColorEx.ORANGE));
        this.registerColor (SLMKIII_YELLOW_LIGHT, ColorEx.brighter (ColorEx.YELLOW));
        this.registerColor (SLMKIII_YELLOW, ColorEx.YELLOW);
        this.registerColor (SLMKIII_YELLOW_HALF, ColorEx.evenDarker (ColorEx.YELLOW));
        this.registerColor (SLMKIII_GREEN_LIGHT, ColorEx.brighter (ColorEx.GREEN));
        this.registerColor (SLMKIII_DARK_YELLOW, ColorEx.darker (ColorEx.YELLOW));
        this.registerColor (SLMKIII_DARK_GREEN, ColorEx.DARK_GREEN);
        this.registerColor (SLMKIII_DARK_YELLOW_HALF, ColorEx.evenDarker (ColorEx.YELLOW));
        this.registerColor (SLMKIII_GREEN_GRASS, ColorEx.brighter (ColorEx.GREEN));
        this.registerColor (SLMKIII_GREEN, ColorEx.GREEN);
        this.registerColor (SLMKIII_DARK_GREEN_HALF, ColorEx.darker (ColorEx.DARK_GREEN));
        this.registerColor (SLMKIII_GREEN_HALF, ColorEx.darker (ColorEx.GREEN));
        this.registerColor (SLMKIII_MINT, ColorEx.MINT);
        this.registerColor (SLMKIII_MINT_HALF, ColorEx.evenDarker (ColorEx.MINT));
        this.registerColor (SLMKIII_OLIVE, ColorEx.OLIVE);
        this.registerColor (SLMKIII_SKY_BLUE, ColorEx.SKY_BLUE);
        this.registerColor (SLMKIII_LIGHT_BLUE, ColorEx.brighter (ColorEx.BLUE));
        this.registerColor (SLMKIII_BLUE_METAL, ColorEx.fromRGB (97, 199, 221));
        this.registerColor (SLMKIII_LIGHT_BLUE_HALF, ColorEx.darker (ColorEx.BLUE));
        this.registerColor (SLMKIII_BLUE, ColorEx.BLUE);
        this.registerColor (SLMKIII_BLUE_HALF, ColorEx.darker (ColorEx.BLUE));
        this.registerColor (SLMKIII_DARK_BLUE, ColorEx.DARK_BLUE);
        this.registerColor (SLMKIII_DARK_BLUE_HALF, ColorEx.darker (ColorEx.DARK_BLUE));
        this.registerColor (SLMKIII_PURPLE, ColorEx.PURPLE);
        this.registerColor (SLMKIII_PURPLE_HALF, ColorEx.darker (ColorEx.PURPLE));
        this.registerColor (SLMKIII_PINK_LIGHT, ColorEx.brighter (ColorEx.PINK));
        this.registerColor (SLMKIII_PINK, ColorEx.PINK);
        this.registerColor (SLMKIII_RED_WINE, ColorEx.RED_WINE);
        this.registerColor (SLMKIII_BROWN, ColorEx.BROWN);
        this.registerColor (SLMKIII_BLUE_PURPLISH, ColorEx.fromRGB (161, 140, 255));
        this.registerColor (SLMKIII_PINK_DARK, ColorEx.darker (ColorEx.PINK));
        this.registerColor (SLMKIII_DARK_ORANGE, ColorEx.evenDarker (ColorEx.ORANGE));
    }


    /** {@inheritDoc} */
    @Override
    public ColorEx getColor (final int colorIndex, final ButtonID buttonID)
    {
        final ColorEx color = super.getColor (colorIndex, buttonID);
        return color == null ? ColorEx.BLACK : color;
    }
}