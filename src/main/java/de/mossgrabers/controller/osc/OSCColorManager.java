// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.osc;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.scale.Scales;


/**
 * Different colors to use with OSC.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class OSCColorManager extends ColorManager
{
    private static final int BLACK         = 0;
    private static final int SKY_BLUE      = 1;
    private static final int WHITE         = 2;
    private static final int DARK_GRAY     = 3;
    private static final int GRAY          = 4;
    private static final int GRAY_HALF     = 5;
    private static final int LIGHT_GRAY    = 6;
    private static final int SILVER        = 7;
    private static final int DARK_BROWN    = 8;
    private static final int BROWN         = 9;
    private static final int DARK_BLUE     = 10;
    private static final int PURPLE_BLUE   = 11;
    private static final int PURPLE        = 12;
    private static final int PINK          = 13;
    private static final int RED           = 14;
    private static final int ORANGE        = 15;
    private static final int LIGHT_ORANGE  = 16;
    private static final int MOSS_GREEN    = 17;
    private static final int GREEN         = 18;
    private static final int COLD_GREEN    = 19;
    private static final int BLUE          = 20;
    private static final int LIGHT_PURPLE  = 21;
    private static final int LIGHT_PINK    = 22;
    private static final int ROSE          = 23;
    private static final int REDDISH_BROWN = 24;
    private static final int LIGHT_BROWN   = 25;
    private static final int LIGHT_GREEN   = 26;
    private static final int BLUISH_GREEN  = 27;
    private static final int GREEN_BLLUE   = 28;
    private static final int LIGHT_BLUE    = 29;


    /**
     * Constructor.
     */
    public OSCColorManager ()
    {
        this.registerColorIndex (Scales.SCALE_COLOR_OFF, BLACK);
        this.registerColorIndex (Scales.SCALE_COLOR_OCTAVE, SKY_BLUE);
        this.registerColorIndex (Scales.SCALE_COLOR_NOTE, WHITE);
        this.registerColorIndex (Scales.SCALE_COLOR_OUT_OF_SCALE, BLACK);

        this.registerColorIndex (DAWColor.COLOR_OFF, BLACK);
        this.registerColorIndex (DAWColor.DAW_COLOR_DARK_GRAY, DARK_GRAY);
        this.registerColorIndex (DAWColor.DAW_COLOR_GRAY, GRAY);
        this.registerColorIndex (DAWColor.DAW_COLOR_GRAY_HALF, GRAY_HALF);
        this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_GRAY, LIGHT_GRAY);
        this.registerColorIndex (DAWColor.DAW_COLOR_SILVER, SILVER);
        this.registerColorIndex (DAWColor.DAW_COLOR_DARK_BROWN, DARK_BROWN);
        this.registerColorIndex (DAWColor.DAW_COLOR_BROWN, BROWN);
        this.registerColorIndex (DAWColor.DAW_COLOR_DARK_BLUE, DARK_BLUE);
        this.registerColorIndex (DAWColor.DAW_COLOR_PURPLE_BLUE, PURPLE_BLUE);
        this.registerColorIndex (DAWColor.DAW_COLOR_PURPLE, PURPLE);
        this.registerColorIndex (DAWColor.DAW_COLOR_PINK, PINK);
        this.registerColorIndex (DAWColor.DAW_COLOR_RED, RED);
        this.registerColorIndex (DAWColor.DAW_COLOR_ORANGE, ORANGE);
        this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_ORANGE, LIGHT_ORANGE);
        this.registerColorIndex (DAWColor.DAW_COLOR_MOSS_GREEN, MOSS_GREEN);
        this.registerColorIndex (DAWColor.DAW_COLOR_GREEN, GREEN);
        this.registerColorIndex (DAWColor.DAW_COLOR_COLD_GREEN, COLD_GREEN);
        this.registerColorIndex (DAWColor.DAW_COLOR_BLUE, BLUE);
        this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_PURPLE, LIGHT_PURPLE);
        this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_PINK, LIGHT_PINK);
        this.registerColorIndex (DAWColor.DAW_COLOR_ROSE, ROSE);
        this.registerColorIndex (DAWColor.DAW_COLOR_REDDISH_BROWN, REDDISH_BROWN);
        this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_BROWN, LIGHT_BROWN);
        this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_GREEN, LIGHT_GREEN);
        this.registerColorIndex (DAWColor.DAW_COLOR_BLUISH_GREEN, BLUISH_GREEN);
        this.registerColorIndex (DAWColor.DAW_COLOR_GREEN_BLUE, GREEN_BLLUE);
        this.registerColorIndex (DAWColor.DAW_COLOR_LIGHT_BLUE, LIGHT_BLUE);

        this.registerColor (BLACK, ColorEx.BLACK);
        this.registerColor (SKY_BLUE, ColorEx.SKY_BLUE);
        this.registerColor (WHITE, ColorEx.WHITE);
        this.registerColor (DARK_GRAY, ColorEx.DARK_GRAY);
        this.registerColor (GRAY, DAWColor.DAW_COLOR_GRAY.getColor ());
        this.registerColor (GRAY_HALF, DAWColor.DAW_COLOR_GRAY_HALF.getColor ());
        this.registerColor (LIGHT_GRAY, DAWColor.DAW_COLOR_LIGHT_GRAY.getColor ());
        this.registerColor (SILVER, DAWColor.DAW_COLOR_SILVER.getColor ());
        this.registerColor (DARK_BROWN, DAWColor.DAW_COLOR_DARK_BROWN.getColor ());
        this.registerColor (BROWN, DAWColor.DAW_COLOR_BROWN.getColor ());
        this.registerColor (DARK_BLUE, DAWColor.DAW_COLOR_DARK_BLUE.getColor ());
        this.registerColor (PURPLE_BLUE, DAWColor.DAW_COLOR_PURPLE_BLUE.getColor ());
        this.registerColor (PURPLE, DAWColor.DAW_COLOR_PURPLE.getColor ());
        this.registerColor (PINK, DAWColor.DAW_COLOR_PINK.getColor ());
        this.registerColor (RED, DAWColor.DAW_COLOR_RED.getColor ());
        this.registerColor (ORANGE, DAWColor.DAW_COLOR_ORANGE.getColor ());
        this.registerColor (LIGHT_ORANGE, DAWColor.DAW_COLOR_LIGHT_ORANGE.getColor ());
        this.registerColor (MOSS_GREEN, DAWColor.DAW_COLOR_MOSS_GREEN.getColor ());
        this.registerColor (GREEN, DAWColor.DAW_COLOR_GREEN.getColor ());
        this.registerColor (COLD_GREEN, DAWColor.DAW_COLOR_COLD_GREEN.getColor ());
        this.registerColor (BLUE, DAWColor.DAW_COLOR_BLUE.getColor ());
        this.registerColor (LIGHT_PURPLE, DAWColor.DAW_COLOR_LIGHT_PURPLE.getColor ());
        this.registerColor (LIGHT_PINK, DAWColor.DAW_COLOR_LIGHT_PINK.getColor ());
        this.registerColor (ROSE, DAWColor.DAW_COLOR_ROSE.getColor ());
        this.registerColor (REDDISH_BROWN, DAWColor.DAW_COLOR_REDDISH_BROWN.getColor ());
        this.registerColor (LIGHT_BROWN, DAWColor.DAW_COLOR_LIGHT_BROWN.getColor ());
        this.registerColor (LIGHT_GREEN, DAWColor.DAW_COLOR_LIGHT_GREEN.getColor ());
        this.registerColor (BLUISH_GREEN, DAWColor.DAW_COLOR_BLUISH_GREEN.getColor ());
        this.registerColor (GREEN_BLLUE, DAWColor.DAW_COLOR_GREEN_BLUE.getColor ());
        this.registerColor (LIGHT_BLUE, DAWColor.DAW_COLOR_LIGHT_BLUE.getColor ());
    }
}