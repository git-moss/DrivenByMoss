// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.intuitiveinstruments.exquis.controller;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.featuregroup.AbstractFeatureGroup;
import de.mossgrabers.framework.view.AbstractSessionView;


/**
 * Different colors to use for the Intuitive Instruments Exquis controller.
 *
 * @author Jürgen Moßgraber
 */
public class ExquisColorManager extends ColorManager
{
    /** The index of the first DAWColor. */
    public static final int    FIRST_DAW_COLOR_INDEX  = 50;

    /** State for button LED on. */
    public static final int    COLOR_BUTTON_STATE_ON  = 127;
    /** State for button LED off. */
    public static final int    COLOR_BUTTON_STATE_OFF = 0;

    /** ID for color black. */
    public static final int    BLACK                  = 101;
    /** ID for color dark grey. */
    public static final int    DARK_GREY              = 102;
    /** ID for color white. */
    public static final int    WHITE                  = 103;
    /** ID for color dark red. */
    public static final int    DARK_RED               = 104;
    /** ID for color red. */
    public static final int    RED                    = 105;
    /** ID for color darker blue. */
    public static final int    DARKER_BLUE            = 106;
    /** ID for color blue. */
    public static final int    BLUE                   = 107;
    /** ID for color darker yellow. */
    public static final int    DARKER_YELLOW          = 108;
    /** ID for color yellow. */
    public static final int    YELLOW                 = 109;
    /** ID for color darker green. */
    public static final int    DARKER_GREEN           = 110;
    /** ID for color green. */
    public static final int    GREEN                  = 111;
    /** ID for color darker orange. */
    public static final int    DARKER_ORANGE          = 112;
    /** ID for color dark orange. */
    public static final int    DARK_ORANGE            = 113;
    /** ID for color orange. */
    public static final int    ORANGE                 = 114;

    /** Color when recording is stopped. */
    public static final String RECORD_OFF             = "RECORD_OFF";
    /** Color when recording is active. */
    public static final String RECORD_ON              = "RECORD_ON";
    /** Color when loop is disabled. */
    public static final String LOOP_OFF               = "LOOP_OFF";
    /** Color when loop is enabled. */
    public static final String LOOP_ON                = "LOOP_ON";
    /** Color when session mode is off. */
    public static final String SESSION_OFF            = "SESSION_OFF";
    /** Color when session mode is active. */
    public static final String SESSION_ON             = "SESSION_ON";
    /** Color when play-back is stopped. */
    public static final String PLAY_OFF               = "PLAY_OFF";
    /** Color when play-back is active. */
    public static final String PLAY_ON                = "PLAY_ON";
    /** Color when undo/redo is not available. */
    public static final String DO_OFF                 = "DO_OFF";
    /** Color when undo/redo is available. */
    public static final String DO_ON                  = "DO_ON";

    /** Color for tracks. */
    public static final String TRACKS_COLOR           = "TRACKS_COLOR";


    /**
     * Constructor.
     */
    public ExquisColorManager ()
    {
        this.registerColorIndex (AbstractFeatureGroup.BUTTON_COLOR_OFF, 0);

        this.registerColorIndex (ColorManager.BUTTON_STATE_OFF, COLOR_BUTTON_STATE_OFF);
        this.registerColorIndex (ColorManager.BUTTON_STATE_ON, COLOR_BUTTON_STATE_ON);
        this.registerColorIndex (ColorManager.BUTTON_STATE_HI, COLOR_BUTTON_STATE_ON);
        this.registerColorIndex (IPadGrid.GRID_OFF, COLOR_BUTTON_STATE_OFF);

        this.registerColorIndex (AbstractSessionView.COLOR_SCENE, DARKER_GREEN);
        this.registerColorIndex (AbstractSessionView.COLOR_SELECTED_SCENE, GREEN);
        this.registerColorIndex (AbstractSessionView.COLOR_SCENE_OFF, BLACK);

        this.registerColorIndex (RECORD_OFF, DARK_RED);
        this.registerColorIndex (RECORD_ON, RED);
        this.registerColorIndex (LOOP_OFF, DARKER_BLUE);
        this.registerColorIndex (LOOP_ON, BLUE);
        this.registerColorIndex (SESSION_OFF, DARKER_YELLOW);
        this.registerColorIndex (SESSION_ON, YELLOW);
        this.registerColorIndex (PLAY_OFF, DARKER_GREEN);
        this.registerColorIndex (PLAY_ON, GREEN);
        this.registerColorIndex (DO_OFF, BLACK);
        this.registerColorIndex (DO_ON, WHITE);
        this.registerColorIndex (TRACKS_COLOR, DARKER_BLUE);

        this.registerColor (COLOR_BUTTON_STATE_OFF, ColorEx.BLACK);
        this.registerColor (COLOR_BUTTON_STATE_ON, ColorEx.WHITE);

        this.registerColor (BLACK, ColorEx.BLACK);
        this.registerColor (DARK_GREY, ColorEx.DARK_GRAY);
        this.registerColor (WHITE, ColorEx.WHITE);
        this.registerColor (DARK_RED, ColorEx.DARKER_RED);
        this.registerColor (RED, ColorEx.RED);
        this.registerColor (DARKER_BLUE, ColorEx.DARKER_BLUE);
        this.registerColor (BLUE, ColorEx.BLUE);
        this.registerColor (DARKER_YELLOW, ColorEx.DARKER_YELLOW);
        this.registerColor (YELLOW, ColorEx.YELLOW);
        this.registerColor (DARKER_GREEN, ColorEx.DARKER_GREEN);
        this.registerColor (GREEN, ColorEx.GREEN);
        this.registerColor (ORANGE, ColorEx.ORANGE);
        this.registerColor (DARK_ORANGE, ColorEx.DARK_ORANGE);
        this.registerColor (DARKER_ORANGE, ColorEx.DARKER_ORANGE);

        final DAWColor [] dawColors = DAWColor.values ();
        for (int i = 0; i < dawColors.length; i++)
        {
            final DAWColor dawColor = dawColors[i];
            this.registerColorIndex (dawColor, FIRST_DAW_COLOR_INDEX + i);
            this.registerColor (FIRST_DAW_COLOR_INDEX + i, dawColor.getColor ());
        }
    }
}