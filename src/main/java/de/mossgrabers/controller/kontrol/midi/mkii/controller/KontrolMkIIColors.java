// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.midi.mkii.controller;

import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.mode.AbstractMode;


/**
 * Different colors to use for the buttons of Komplete Kontrol MkII.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class KontrolMkIIColors
{
    /**
     * Private due to utility class.
     */
    private KontrolMkIIColors ()
    {
        // Intentionally empty
    }


    /**
     * Configures all colors for Kontrol controllers.
     *
     * @param colorManager The color manager
     */
    public static void addColors (final ColorManager colorManager)
    {
        // TODO ?
        // colorManager.registerColor (Scales.SCALE_COLOR_OFF, isPush2 ? PUSH2_COLOR2_BLACK :
        // PUSH1_COLOR2_BLACK);
        // colorManager.registerColor (Scales.SCALE_COLOR_OCTAVE, isPush2 ? PUSH2_COLOR2_OCEAN_HI :
        // PUSH1_COLOR2_OCEAN_HI);
        // colorManager.registerColor (Scales.SCALE_COLOR_NOTE, isPush2 ? PUSH2_COLOR2_WHITE :
        // PUSH1_COLOR2_WHITE);
        // colorManager.registerColor (Scales.SCALE_COLOR_OUT_OF_SCALE, isPush2 ? PUSH2_COLOR_BLACK
        // : PUSH1_COLOR_BLACK);

        colorManager.registerColor (AbstractMode.BUTTON_COLOR_OFF, 0);
        colorManager.registerColor (AbstractMode.BUTTON_COLOR_ON, 0);
        colorManager.registerColor (AbstractMode.BUTTON_COLOR_HI, 1);

        colorManager.registerColor (ColorManager.BUTTON_STATE_OFF, 0);
        colorManager.registerColor (ColorManager.BUTTON_STATE_ON, 0);
        colorManager.registerColor (ColorManager.BUTTON_STATE_HI, 1);
    }
}