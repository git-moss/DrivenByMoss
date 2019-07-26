// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.mki.controller;

import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.PadGrid;
import de.mossgrabers.framework.daw.DAWColors;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.view.AbstractDrumView;
import de.mossgrabers.framework.view.AbstractPlayView;


/**
 * Different colors to use for the key LEDs of the Kontrol 1.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Kontrol1Colors
{
    /** Color intensity for an active button. */
    public static final int     BUTTON_STATE_HI          = 255;
    /** Color intensity for an enabled button. */
    public static final int     BUTTON_STATE_ON          = 6;
    /** Color intensity for an disabled button. */
    public static final int     BUTTON_STATE_OFF         = 0;

    private static final int    DAW_COLOR_START          = 100;

    private static final int    SCALE_COLOR_OCTAVE       = 50;
    private static final int    SCALE_COLOR_NOTE         = 51;
    private static final int    SCALE_COLOR_OUT_OF_SCALE = 52;
    private static final int    COLOR_PLAY               = 53;
    private static final int    COLOR_RECORD             = 54;

    private static final int [] BLACK                    =
    {
        0,
        0,
        0
    };
    private static final int [] RED                      =
    {
        127,
        0,
        0
    };
    private static final int [] GREEN                    =
    {
        0,
        127,
        0
    };
    private static final int [] BLUE                     =
    {
        0,
        0,
        127
    };
    private static final int [] WHITE                    =
    {
        127,
        127,
        127
    };


    /**
     * Private due to utility class.
     */
    private Kontrol1Colors ()
    {
        // Intentionally empty
    }


    /**
     * Configures all colors for Kontrol 1 controller.
     *
     * @param colorManager The color manager
     */
    public static void addColors (final ColorManager colorManager)
    {
        colorManager.registerColor (ColorManager.BUTTON_STATE_OFF, BUTTON_STATE_OFF);
        colorManager.registerColor (ColorManager.BUTTON_STATE_ON, BUTTON_STATE_ON);
        colorManager.registerColor (ColorManager.BUTTON_STATE_HI, BUTTON_STATE_HI);

        colorManager.registerColor (Scales.SCALE_COLOR_OFF, BUTTON_STATE_OFF);
        colorManager.registerColor (Scales.SCALE_COLOR_OCTAVE, SCALE_COLOR_OCTAVE);
        colorManager.registerColor (Scales.SCALE_COLOR_NOTE, SCALE_COLOR_NOTE);
        colorManager.registerColor (Scales.SCALE_COLOR_OUT_OF_SCALE, SCALE_COLOR_OUT_OF_SCALE);

        colorManager.registerColor (AbstractPlayView.COLOR_OFF, BUTTON_STATE_OFF);
        colorManager.registerColor (AbstractPlayView.COLOR_PLAY, COLOR_PLAY);
        colorManager.registerColor (AbstractPlayView.COLOR_RECORD, COLOR_RECORD);

        colorManager.registerColor (AbstractDrumView.COLOR_PAD_OFF, BUTTON_STATE_OFF);
        colorManager.registerColor (AbstractDrumView.COLOR_PAD_RECORD, COLOR_RECORD);
        colorManager.registerColor (AbstractDrumView.COLOR_PAD_PLAY, COLOR_PLAY);
        colorManager.registerColor (AbstractDrumView.COLOR_PAD_MUTED,
                DAW_COLOR_START + 2 /* DAWColors.DAW_COLOR_GRAY */);
        colorManager.registerColor (AbstractDrumView.COLOR_PAD_HAS_CONTENT,
                DAW_COLOR_START + 17 /* DAWColors.DAW_COLOR_BLUE */);
        colorManager.registerColor (AbstractDrumView.COLOR_PAD_NO_CONTENT, BUTTON_STATE_OFF);

        colorManager.registerColor (PadGrid.GRID_OFF, BUTTON_STATE_OFF);

        colorManager.registerColor (DAWColors.COLOR_OFF, BUTTON_STATE_OFF);

        for (int i = 0; i < DAWColors.DAW_COLORS.length; i++)
            colorManager.registerColor (DAWColors.DAW_COLORS[i], DAW_COLOR_START + i);
    }


    /**
     * Get a real RGB value for a registered color index.
     *
     * @param index The index
     * @return The color or black if index is not registered (3 x 0-127)
     */
    public static int [] getColorFromIndex (final int index)
    {
        if (index >= DAW_COLOR_START)
        {
            final double [] colorEntry = DAWColors.getColorEntry (index - DAW_COLOR_START);
            return new int []
            {
                (int) (colorEntry[0] * 127),
                (int) (colorEntry[1] * 127),
                (int) (colorEntry[2] * 127)
            };
        }

        switch (index)
        {
            case SCALE_COLOR_OCTAVE:
                return BLUE;
            case SCALE_COLOR_NOTE:
                return WHITE;
            case SCALE_COLOR_OUT_OF_SCALE:
                return BLACK;
            case COLOR_PLAY:
                return GREEN;
            case COLOR_RECORD:
                return RED;
            default:
                return BLACK;
        }
    }
}