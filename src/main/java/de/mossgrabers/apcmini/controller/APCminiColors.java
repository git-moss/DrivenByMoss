// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.apcmini.controller;

import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.PadGrid;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.view.AbstractDrumView;
import de.mossgrabers.framework.view.AbstractPlayView;
import de.mossgrabers.framework.view.AbstractSequencerView;


/**
 * Different colors to use for the pads and buttons of APC40 mkI and mkII.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class APCminiColors
{
    /** off. */
    public static final int APC_COLOR_BLACK        = 0;
    /** green, 7-127 also green. */
    public static final int APC_COLOR_GREEN        = 1;
    /** green blink. */
    public static final int APC_COLOR_GREEN_BLINK  = 2;
    /** red. */
    public static final int APC_COLOR_RED          = 3;
    /** red blink. */
    public static final int APC_COLOR_RED_BLINK    = 4;
    /** yellow. */
    public static final int APC_COLOR_YELLOW       = 5;
    /** yellow blink. */
    public static final int APC_COLOR_YELLOW_BLINK = 6;


    /**
     * Private due to utility class.
     */
    private APCminiColors ()
    {
        // Intentionally empty
    }


    /**
     * Configures all colors for APCmini controller.
     *
     * @param colorManager The color manager
     */
    public static void addColors (final ColorManager colorManager)
    {
        colorManager.registerColor (Scales.SCALE_COLOR_OFF, APC_COLOR_BLACK);
        colorManager.registerColor (Scales.SCALE_COLOR_OCTAVE, APC_COLOR_YELLOW);
        colorManager.registerColor (Scales.SCALE_COLOR_NOTE, APC_COLOR_BLACK);
        colorManager.registerColor (Scales.SCALE_COLOR_OUT_OF_SCALE, APC_COLOR_BLACK);

        colorManager.registerColor (AbstractSequencerView.COLOR_STEP_HILITE_NO_CONTENT, APC_COLOR_GREEN);
        colorManager.registerColor (AbstractSequencerView.COLOR_STEP_HILITE_CONTENT, APC_COLOR_GREEN);
        colorManager.registerColor (AbstractSequencerView.COLOR_NO_CONTENT, APC_COLOR_BLACK);
        colorManager.registerColor (AbstractSequencerView.COLOR_CONTENT, APC_COLOR_RED);
        colorManager.registerColor (AbstractSequencerView.COLOR_CONTENT_CONT, APC_COLOR_RED);
        colorManager.registerColor (AbstractSequencerView.COLOR_PAGE, APC_COLOR_YELLOW);
        colorManager.registerColor (AbstractSequencerView.COLOR_ACTIVE_PAGE, APC_COLOR_GREEN);
        colorManager.registerColor (AbstractSequencerView.COLOR_SELECTED_PAGE, APC_COLOR_RED);

        colorManager.registerColor (AbstractDrumView.COLOR_PAD_OFF, APC_COLOR_BLACK);
        colorManager.registerColor (AbstractDrumView.COLOR_PAD_RECORD, APC_COLOR_RED);
        colorManager.registerColor (AbstractDrumView.COLOR_PAD_PLAY, APC_COLOR_GREEN);
        colorManager.registerColor (AbstractDrumView.COLOR_PAD_SELECTED, APC_COLOR_YELLOW_BLINK);
        colorManager.registerColor (AbstractDrumView.COLOR_PAD_MUTED, APC_COLOR_YELLOW);
        colorManager.registerColor (AbstractDrumView.COLOR_PAD_HAS_CONTENT, APC_COLOR_YELLOW);
        colorManager.registerColor (AbstractDrumView.COLOR_PAD_NO_CONTENT, APC_COLOR_BLACK);

        colorManager.registerColor (AbstractPlayView.COLOR_PLAY, APC_COLOR_GREEN);
        colorManager.registerColor (AbstractPlayView.COLOR_RECORD, APC_COLOR_RED);
        colorManager.registerColor (AbstractPlayView.COLOR_OFF, APC_COLOR_BLACK);

        colorManager.registerColor (PadGrid.GRID_OFF, APC_COLOR_BLACK);
    }
}