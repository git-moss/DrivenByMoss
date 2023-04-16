// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.apcmini.controller;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.view.AbstractPlayView;
import de.mossgrabers.framework.view.AbstractSessionView;
import de.mossgrabers.framework.view.BrowserView;
import de.mossgrabers.framework.view.sequencer.AbstractDrumView;
import de.mossgrabers.framework.view.sequencer.AbstractSequencerView;


/**
 * Different colors to use for the pads and buttons of APC40 mkI and mkII.
 *
 * @author Jürgen Moßgraber
 */
public class APCminiColorManager extends ColorManager
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
     * Constructor.
     */
    public APCminiColorManager ()
    {
        this.registerColorIndex (Scales.SCALE_COLOR_OFF, APC_COLOR_BLACK);
        this.registerColorIndex (Scales.SCALE_COLOR_OCTAVE, APC_COLOR_YELLOW);
        this.registerColorIndex (Scales.SCALE_COLOR_NOTE, APC_COLOR_BLACK);
        this.registerColorIndex (Scales.SCALE_COLOR_OUT_OF_SCALE, APC_COLOR_BLACK);

        this.registerColorIndex (ColorManager.BUTTON_STATE_OFF, APC_COLOR_BLACK);
        this.registerColorIndex (ColorManager.BUTTON_STATE_ON, APC_COLOR_GREEN);
        this.registerColorIndex (ColorManager.BUTTON_STATE_HI, APC_COLOR_GREEN_BLINK);

        this.registerColorIndex (AbstractSessionView.COLOR_SCENE, APC_COLOR_GREEN);
        this.registerColorIndex (AbstractSessionView.COLOR_SELECTED_SCENE, APC_COLOR_GREEN_BLINK);
        this.registerColorIndex (AbstractSessionView.COLOR_SCENE_OFF, APC_COLOR_BLACK);

        this.registerColorIndex (AbstractSequencerView.COLOR_STEP_HILITE_NO_CONTENT, APC_COLOR_GREEN);
        this.registerColorIndex (AbstractSequencerView.COLOR_STEP_HILITE_CONTENT, APC_COLOR_GREEN);
        this.registerColorIndex (AbstractSequencerView.COLOR_STEP_MUTED, APC_COLOR_GREEN);
        this.registerColorIndex (AbstractSequencerView.COLOR_STEP_MUTED_CONT, APC_COLOR_GREEN);
        this.registerColorIndex (AbstractSequencerView.COLOR_STEP_SELECTED, APC_COLOR_GREEN);
        this.registerColorIndex (AbstractSequencerView.COLOR_NO_CONTENT, APC_COLOR_BLACK);
        this.registerColorIndex (AbstractSequencerView.COLOR_NO_CONTENT_4, APC_COLOR_BLACK);
        this.registerColorIndex (AbstractSequencerView.COLOR_CONTENT, APC_COLOR_RED);
        this.registerColorIndex (AbstractSequencerView.COLOR_CONTENT_CONT, APC_COLOR_RED);
        this.registerColorIndex (AbstractSequencerView.COLOR_PAGE, APC_COLOR_YELLOW);
        this.registerColorIndex (AbstractSequencerView.COLOR_ACTIVE_PAGE, APC_COLOR_GREEN);
        this.registerColorIndex (AbstractSequencerView.COLOR_SELECTED_PAGE, APC_COLOR_RED);

        this.registerColorIndex (AbstractDrumView.COLOR_PAD_OFF, APC_COLOR_BLACK);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_RECORD, APC_COLOR_RED);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_PLAY, APC_COLOR_GREEN);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_SELECTED, APC_COLOR_YELLOW_BLINK);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_MUTED, APC_COLOR_YELLOW);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_HAS_CONTENT, APC_COLOR_YELLOW);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_NO_CONTENT, APC_COLOR_BLACK);

        this.registerColorIndex (AbstractPlayView.COLOR_PLAY, APC_COLOR_GREEN);
        this.registerColorIndex (AbstractPlayView.COLOR_RECORD, APC_COLOR_RED);
        this.registerColorIndex (AbstractPlayView.COLOR_OFF, APC_COLOR_BLACK);

        this.registerColorIndex (BrowserView.OFF, APC_COLOR_BLACK);
        this.registerColorIndex (BrowserView.DISCARD, APC_COLOR_RED_BLINK);
        this.registerColorIndex (BrowserView.CONFIRM, APC_COLOR_GREEN_BLINK);
        this.registerColorIndex (BrowserView.PLAY, APC_COLOR_YELLOW);
        this.registerColorIndex (BrowserView.COLUMN1, APC_COLOR_GREEN);
        this.registerColorIndex (BrowserView.COLUMN2, APC_COLOR_RED);
        this.registerColorIndex (BrowserView.COLUMN3, APC_COLOR_GREEN);
        this.registerColorIndex (BrowserView.COLUMN4, APC_COLOR_RED);
        this.registerColorIndex (BrowserView.COLUMN5, APC_COLOR_GREEN);
        this.registerColorIndex (BrowserView.COLUMN6, APC_COLOR_RED);
        this.registerColorIndex (BrowserView.COLUMN7, APC_COLOR_BLACK);
        this.registerColorIndex (BrowserView.COLUMN8, APC_COLOR_YELLOW);

        this.registerColorIndex (IPadGrid.GRID_OFF, APC_COLOR_BLACK);

        this.registerColor (APC_COLOR_BLACK, ColorEx.BLACK);
        this.registerColor (APC_COLOR_GREEN, ColorEx.GREEN);
        this.registerColor (APC_COLOR_GREEN_BLINK, ColorEx.GREEN);
        this.registerColor (APC_COLOR_RED, ColorEx.RED);
        this.registerColor (APC_COLOR_RED_BLINK, ColorEx.RED);
        this.registerColor (APC_COLOR_YELLOW, ColorEx.YELLOW);
        this.registerColor (APC_COLOR_YELLOW_BLINK, ColorEx.YELLOW);
    }
}