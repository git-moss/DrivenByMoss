// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.maschine.controller;

import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.featuregroup.AbstractFeatureGroup;
import de.mossgrabers.framework.featuregroup.AbstractMode;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.view.AbstractDrumView;
import de.mossgrabers.framework.view.AbstractPlayView;
import de.mossgrabers.framework.view.AbstractSequencerView;


/**
 * Color states to use for the Maschine buttons.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
@SuppressWarnings("javadoc")
public class MaschineColorManager extends ColorManager
{
    public static final int COLOR_BLACK        = 0;
    public static final int COLOR_GREY         = 76;
    public static final int COLOR_WHITE        = 78;
    public static final int COLOR_ROSE         = 7;
    public static final int COLOR_RED          = 6;
    public static final int COLOR_RED_LO       = 5;
    public static final int COLOR_AMBER        = 14;
    public static final int COLOR_AMBER_LO     = 13;
    public static final int COLOR_LIME         = 34;
    public static final int COLOR_LIME_LO      = 33;
    public static final int COLOR_GREEN        = 30;
    public static final int COLOR_GREEN_LO     = 29;
    public static final int COLOR_SPRING       = 26;
    public static final int COLOR_SPRING_LO    = 25;
    public static final int COLOR_TURQUOISE_LO = 27;
    public static final int COLOR_TURQUOISE    = 31;
    public static final int COLOR_SKY          = 38;
    public static final int COLOR_SKY_LO       = 37;
    public static final int COLOR_BLUE         = 42;
    public static final int COLOR_BLUE_LO      = 41;
    public static final int COLOR_MAGENTA      = 58;
    public static final int COLOR_MAGENTA_LO   = 57;
    public static final int COLOR_PINK         = 62;
    public static final int COLOR_PINK_LO      = 61;
    public static final int COLOR_ORANGE       = 10;
    public static final int COLOR_ORANGE_LO    = 9;
    public static final int COLOR_PURPLE       = 50;
    public static final int COLOR_PURPLE_LO    = 49;
    public static final int COLOR_SKIN         = 11;
    public static final int COLOR_YELLOW       = 22;


    /**
     * Constructor.
     */
    public MaschineColorManager ()
    {
        this.registerColorIndex (ColorManager.BUTTON_STATE_OFF, 0);
        this.registerColorIndex (ColorManager.BUTTON_STATE_ON, 0);
        this.registerColorIndex (ColorManager.BUTTON_STATE_HI, 127);

        this.registerColorIndex (Scales.SCALE_COLOR_OFF, COLOR_BLACK);
        this.registerColorIndex (Scales.SCALE_COLOR_OCTAVE, COLOR_BLUE);
        this.registerColorIndex (Scales.SCALE_COLOR_NOTE, COLOR_WHITE);
        this.registerColorIndex (Scales.SCALE_COLOR_OUT_OF_SCALE, COLOR_BLACK);

        this.registerColorIndex (AbstractFeatureGroup.BUTTON_COLOR_OFF, 0);
        this.registerColorIndex (AbstractFeatureGroup.BUTTON_COLOR_ON, 0);
        this.registerColorIndex (AbstractMode.BUTTON_COLOR_HI, 127);

        this.registerColorIndex (AbstractPlayView.COLOR_PLAY, COLOR_GREEN);
        this.registerColorIndex (AbstractPlayView.COLOR_RECORD, COLOR_RED);
        this.registerColorIndex (AbstractPlayView.COLOR_OFF, COLOR_BLACK);

        this.registerColorIndex (AbstractSequencerView.COLOR_STEP_HILITE_NO_CONTENT, COLOR_GREEN_LO);
        this.registerColorIndex (AbstractSequencerView.COLOR_STEP_HILITE_CONTENT, COLOR_GREEN);
        this.registerColorIndex (AbstractSequencerView.COLOR_NO_CONTENT, COLOR_BLACK);
        this.registerColorIndex (AbstractSequencerView.COLOR_CONTENT, COLOR_BLUE);
        this.registerColorIndex (AbstractSequencerView.COLOR_CONTENT_CONT, COLOR_BLUE_LO);

        this.registerColorIndex (AbstractSequencerView.COLOR_PAGE, COLOR_WHITE);
        this.registerColorIndex (AbstractSequencerView.COLOR_ACTIVE_PAGE, COLOR_GREEN);
        this.registerColorIndex (AbstractSequencerView.COLOR_SELECTED_PAGE, COLOR_BLUE);
        this.registerColorIndex (AbstractSequencerView.COLOR_RESOLUTION, COLOR_ORANGE);
        this.registerColorIndex (AbstractSequencerView.COLOR_RESOLUTION_SELECTED, COLOR_RED);
        this.registerColorIndex (AbstractSequencerView.COLOR_RESOLUTION_OFF, COLOR_BLACK);

        this.registerColorIndex (AbstractDrumView.COLOR_PAD_OFF, COLOR_BLACK);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_RECORD, COLOR_RED);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_PLAY, COLOR_GREEN);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_SELECTED, COLOR_BLUE);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_MUTED, COLOR_AMBER);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_HAS_CONTENT, COLOR_GREY);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_NO_CONTENT, COLOR_BLACK);

        this.registerColorIndex (DAWColor.COLOR_OFF, COLOR_BLACK);
        this.registerColorIndex (DAWColor.DAW_COLOR_GRAY_HALF, COLOR_GREY);
        this.registerColorIndex (DAWColor.DAW_COLOR_DARK_GRAY, COLOR_GREY);
        this.registerColorIndex (DAWColor.DAW_COLOR_GRAY, COLOR_GREY);
        this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_GRAY, COLOR_GREY);
        this.registerColorIndex (DAWColor.DAW_COLOR_SILVER, COLOR_GREY);
        this.registerColorIndex (DAWColor.DAW_COLOR_DARK_BROWN, COLOR_AMBER_LO);
        this.registerColorIndex (DAWColor.DAW_COLOR_BROWN, COLOR_AMBER_LO);
        this.registerColorIndex (DAWColor.DAW_COLOR_DARK_BLUE, COLOR_BLUE_LO);
        this.registerColorIndex (DAWColor.DAW_COLOR_PURPLE_BLUE, COLOR_PURPLE_LO);
        this.registerColorIndex (DAWColor.DAW_COLOR_PURPLE, COLOR_PURPLE);
        this.registerColorIndex (DAWColor.DAW_COLOR_PINK, COLOR_PINK);
        this.registerColorIndex (DAWColor.DAW_COLOR_RED, COLOR_RED);
        this.registerColorIndex (DAWColor.DAW_COLOR_ORANGE, COLOR_ORANGE);
        this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_ORANGE, COLOR_YELLOW);
        this.registerColorIndex (DAWColor.DAW_COLOR_MOSS_GREEN, COLOR_LIME_LO);
        this.registerColorIndex (DAWColor.DAW_COLOR_GREEN, COLOR_SPRING);
        this.registerColorIndex (DAWColor.DAW_COLOR_COLD_GREEN, COLOR_TURQUOISE);
        this.registerColorIndex (DAWColor.DAW_COLOR_BLUE, COLOR_BLUE);
        this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_PURPLE, COLOR_MAGENTA);
        this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_PINK, COLOR_PINK);
        this.registerColorIndex (DAWColor.DAW_COLOR_ROSE, COLOR_SKIN);
        this.registerColorIndex (DAWColor.DAW_COLOR_REDDISH_BROWN, COLOR_AMBER);
        this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_BROWN, COLOR_AMBER_LO);
        this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_GREEN, COLOR_SPRING);
        this.registerColorIndex (DAWColor.DAW_COLOR_BLUISH_GREEN, COLOR_LIME);
        this.registerColorIndex (DAWColor.DAW_COLOR_GREEN_BLUE, COLOR_TURQUOISE_LO);
        this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_BLUE, COLOR_SKY);

        this.registerColor (COLOR_BLACK, ColorEx.BLACK);
        this.registerColor (COLOR_GREY, ColorEx.GRAY);
        this.registerColor (COLOR_WHITE, ColorEx.WHITE);
        this.registerColor (COLOR_ROSE, DAWColor.DAW_COLOR_ROSE.getColor ());
        this.registerColor (COLOR_RED, DAWColor.DAW_COLOR_REDDISH_BROWN.getColor ());
        this.registerColor (COLOR_RED_LO, ColorEx.fromRGB (39, 4, 1));
        this.registerColor (COLOR_AMBER, DAWColor.DAW_COLOR_REDDISH_BROWN.getColor ());
        this.registerColor (COLOR_AMBER_LO, DAWColor.DAW_COLOR_DARK_BROWN.getColor ());
        this.registerColor (COLOR_YELLOW, ColorEx.fromRGB (107, 105, 1));
        this.registerColor (COLOR_LIME, ColorEx.fromRGB (29, 104, 1));
        this.registerColor (COLOR_LIME_LO, DAWColor.DAW_COLOR_MOSS_GREEN.getColor ());
        this.registerColor (COLOR_GREEN, ColorEx.fromRGB (1, 104, 1));
        this.registerColor (COLOR_GREEN_LO, ColorEx.fromRGB (1, 36, 1));
        this.registerColor (COLOR_SPRING, DAWColor.DAW_COLOR_GREEN.getColor ());
        this.registerColor (COLOR_SPRING_LO, ColorEx.fromRGB (1, 36, 1));
        this.registerColor (COLOR_TURQUOISE_LO, ColorEx.fromRGB (1, 248, 75));
        this.registerColor (COLOR_TURQUOISE, DAWColor.DAW_COLOR_COLD_GREEN.getColor ());
        this.registerColor (COLOR_SKY, ColorEx.fromRGB (1, 82, 100));
        this.registerColor (COLOR_SKY_LO, ColorEx.fromRGB (1, 26, 37));
        this.registerColor (COLOR_BLUE, ColorEx.fromRGB (4, 23, 110));
        this.registerColor (COLOR_BLUE_LO, ColorEx.fromRGB (1, 8, 38));
        this.registerColor (COLOR_MAGENTA, ColorEx.fromRGB (110, 28, 109));
        this.registerColor (COLOR_MAGENTA_LO, ColorEx.fromRGB (39, 9, 38));
        this.registerColor (COLOR_PINK, ColorEx.fromRGB (110, 20, 40));
        this.registerColor (COLOR_PINK_LO, ColorEx.fromRGB (48, 9, 26));
        this.registerColor (COLOR_ORANGE, DAWColor.DAW_COLOR_ORANGE.getColor ());
        this.registerColor (COLOR_ORANGE_LO, ColorEx.DARK_ORANGE);
        this.registerColor (COLOR_PURPLE, ColorEx.PURPLE);
        this.registerColor (COLOR_PURPLE_LO, ColorEx.PURPLE);
        this.registerColor (COLOR_SKIN, ColorEx.SKIN);
    }


    /** {@inheritDoc} */
    @Override
    public ColorEx getColor (final int colorIndex, final ButtonID buttonID)
    {
        if (colorIndex < 0)
            return ColorEx.BLACK;

        if (buttonID == null)
            return ColorEx.GRAY;

        switch (buttonID)
        {
            case PLAY:
                return colorIndex > 0 ? ColorEx.GREEN : ColorEx.DARK_GREEN;

            case RECORD:
                return colorIndex > 0 ? ColorEx.RED : ColorEx.DARK_RED;

            case PAD1:
            case PAD2:
            case PAD3:
            case PAD4:
            case PAD5:
            case PAD6:
            case PAD7:
            case PAD8:
            case PAD9:
            case PAD10:
            case PAD11:
            case PAD12:
            case PAD13:
            case PAD14:
            case PAD15:
            case PAD16:
            case ROW3_1:
            case ROW3_2:
            case ROW3_3:
            case ROW3_4:
            case ROW3_5:
            case ROW3_6:
            case ROW3_7:
            case ROW3_8:
                return super.getColor (colorIndex, buttonID);

            default:
                return colorIndex > 0 ? ColorEx.WHITE : ColorEx.GRAY;
        }
    }
}