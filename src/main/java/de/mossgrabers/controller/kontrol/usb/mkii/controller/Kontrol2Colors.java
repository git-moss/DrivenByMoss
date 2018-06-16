// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.usb.mkii.controller;

import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.DAWColors;
import de.mossgrabers.framework.mode.AbstractMode;


/**
 * Different colors to use for the buttons of Kontrol 2.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
@SuppressWarnings("javadoc")
public class Kontrol2Colors
{
    public static final int KONTROL2_COLOR_BLACK       = 0;

    public static final int KONTROL2_COLOR_RED_LO      = 4;
    public static final int KONTROL2_COLOR_RED_MD      = 5;
    public static final int KONTROL2_COLOR_RED_HI      = 6;
    public static final int KONTROL2_COLOR_RED_ST      = 7;

    public static final int KONTROL2_COLOR_ORANGE_LO   = 8;
    public static final int KONTROL2_COLOR_ORANGE_MD   = 9;
    public static final int KONTROL2_COLOR_ORANGE_HI   = 10;
    public static final int KONTROL2_COLOR_ORANGE_ST   = 11;

    public static final int KONTROL2_COLOR_ORANGE_L_LO = 12;
    public static final int KONTROL2_COLOR_ORANGE_L_MD = 13;
    public static final int KONTROL2_COLOR_ORANGE_L_HI = 14;
    public static final int KONTROL2_COLOR_ORANGE_L_ST = 15;

    public static final int KONTROL2_COLOR_YELLOW_LO   = 16;
    public static final int KONTROL2_COLOR_YELLOW_MD   = 17;
    public static final int KONTROL2_COLOR_YELLOW_HI   = 18;
    public static final int KONTROL2_COLOR_YELLOW_ST   = 19;

    public static final int KONTROL2_COLOR_YELLOW_L_LO = 20;
    public static final int KONTROL2_COLOR_YELLOW_L_MD = 21;
    public static final int KONTROL2_COLOR_YELLOW_L_HI = 22;
    public static final int KONTROL2_COLOR_YELLOW_L_ST = 23;

    public static final int KONTROL2_COLOR_GREEN_L_LO  = 24;
    public static final int KONTROL2_COLOR_GREEN_L_MD  = 25;
    public static final int KONTROL2_COLOR_GREEN_L_HI  = 26;
    public static final int KONTROL2_COLOR_GREEN_L_ST  = 27;

    public static final int KONTROL2_COLOR_GREEN_LO    = 28;
    public static final int KONTROL2_COLOR_GREEN_MD    = 29;
    public static final int KONTROL2_COLOR_GREEN_HI    = 30;
    public static final int KONTROL2_COLOR_GREEN_ST    = 31;


    /**
     * Private due to utility class.
     */
    private Kontrol2Colors ()
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
        colorManager.registerColor (AbstractMode.BUTTON_COLOR_OFF, KONTROL2_COLOR_BLACK);
        // TODO add when display is decivered
        // colorManager.registerColor (AbstractMode.BUTTON_COLOR_ON, isPush2 ? PUSH2_COLOR2_GREY_LO
        // : PUSH1_COLOR_GREEN_LO);
        // colorManager.registerColor (AbstractMode.BUTTON_COLOR_HI, isPush2 ? PUSH2_COLOR2_WHITE :
        // PUSH1_COLOR_YELLOW_MD);

        colorManager.registerColor (DAWColors.COLOR_OFF, KONTROL2_COLOR_BLACK);
        colorManager.registerColor (DAWColors.DAW_COLOR_DARK_GRAY, KONTROL2_COLOR_BLACK);
        colorManager.registerColor (DAWColors.DAW_COLOR_GRAY, KONTROL2_COLOR_BLACK);
        colorManager.registerColor (DAWColors.DAW_COLOR_LIGHT_GRAY, KONTROL2_COLOR_BLACK);
        colorManager.registerColor (DAWColors.DAW_COLOR_SILVER, KONTROL2_COLOR_BLACK);
        colorManager.registerColor (DAWColors.DAW_COLOR_DARK_BROWN, KONTROL2_COLOR_BLACK);
        colorManager.registerColor (DAWColors.DAW_COLOR_BROWN, KONTROL2_COLOR_BLACK);
        colorManager.registerColor (DAWColors.DAW_COLOR_DARK_BLUE, KONTROL2_COLOR_BLACK);
        colorManager.registerColor (DAWColors.DAW_COLOR_PURPLE_BLUE, KONTROL2_COLOR_BLACK);
        colorManager.registerColor (DAWColors.DAW_COLOR_PURPLE, KONTROL2_COLOR_BLACK);
        colorManager.registerColor (DAWColors.DAW_COLOR_PINK, KONTROL2_COLOR_BLACK);
        colorManager.registerColor (DAWColors.DAW_COLOR_RED, KONTROL2_COLOR_RED_MD);
        colorManager.registerColor (DAWColors.DAW_COLOR_ORANGE, KONTROL2_COLOR_ORANGE_MD);
        colorManager.registerColor (DAWColors.DAW_COLOR_LIGHT_ORANGE, KONTROL2_COLOR_ORANGE_L_MD);
        colorManager.registerColor (DAWColors.DAW_COLOR_MOSS_GREEN, KONTROL2_COLOR_BLACK);
        colorManager.registerColor (DAWColors.DAW_COLOR_GREEN, KONTROL2_COLOR_GREEN_MD);
        colorManager.registerColor (DAWColors.DAW_COLOR_COLD_GREEN, KONTROL2_COLOR_BLACK);
        colorManager.registerColor (DAWColors.DAW_COLOR_BLUE, KONTROL2_COLOR_BLACK);
        colorManager.registerColor (DAWColors.DAW_COLOR_LIGHT_PURPLE, KONTROL2_COLOR_BLACK);
        colorManager.registerColor (DAWColors.DAW_COLOR_LIGHT_PINK, KONTROL2_COLOR_BLACK);
        colorManager.registerColor (DAWColors.DAW_COLOR_SKIN, KONTROL2_COLOR_BLACK);
        colorManager.registerColor (DAWColors.DAW_COLOR_REDDISH_BROWN, KONTROL2_COLOR_BLACK);
        colorManager.registerColor (DAWColors.DAW_COLOR_LIGHT_BROWN, KONTROL2_COLOR_BLACK);
        colorManager.registerColor (DAWColors.DAW_COLOR_LIGHT_GREEN, KONTROL2_COLOR_BLACK);
        colorManager.registerColor (DAWColors.DAW_COLOR_BLUISH_GREEN, KONTROL2_COLOR_BLACK);
        colorManager.registerColor (DAWColors.DAW_COLOR_GREEN_BLUE, KONTROL2_COLOR_BLACK);
        colorManager.registerColor (DAWColors.DAW_COLOR_LIGHT_BLUE, KONTROL2_COLOR_BLACK);
    }
}