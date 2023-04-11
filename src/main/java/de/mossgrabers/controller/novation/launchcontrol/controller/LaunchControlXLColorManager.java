// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchcontrol.controller;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.featuregroup.AbstractFeatureGroup;
import de.mossgrabers.framework.view.sequencer.AbstractDrumView;
import de.mossgrabers.framework.view.sequencer.AbstractSequencerView;


/**
 * Different colors to use for the pads and buttons of LauchControl XL.
 *
 * @author Jürgen Moßgraber
 */
@SuppressWarnings("javadoc")
public class LaunchControlXLColorManager extends ColorManager
{
    public static final int LAUNCHCONTROL_COLOR_BLACK     = 12;
    public static final int LAUNCHCONTROL_COLOR_RED       = 15;
    public static final int LAUNCHCONTROL_COLOR_RED_LO    = 13;
    public static final int LAUNCHCONTROL_COLOR_YELLOW    = 62;
    public static final int LAUNCHCONTROL_COLOR_YELLOW_LO = 29;
    public static final int LAUNCHCONTROL_COLOR_GREEN     = 60;
    public static final int LAUNCHCONTROL_COLOR_GREEN_LO  = 28;
    public static final int LAUNCHCONTROL_COLOR_AMBER     = 63;
    public static final int LAUNCHCONTROL_COLOR_AMBER_LO  = 30;

    public static final int LAUNCHCONTROL_COLOR_ON        = 127;


    /**
     * Constructor.
     */
    public LaunchControlXLColorManager ()
    {
        this.registerColorIndex (AbstractFeatureGroup.BUTTON_COLOR_OFF, LAUNCHCONTROL_COLOR_BLACK);
        this.registerColorIndex (AbstractFeatureGroup.BUTTON_COLOR_ON, LAUNCHCONTROL_COLOR_ON);

        this.registerColorIndex (ColorManager.BUTTON_STATE_OFF, 0);
        this.registerColorIndex (ColorManager.BUTTON_STATE_ON, 1);
        this.registerColorIndex (ColorManager.BUTTON_STATE_HI, 127);

        this.registerColorIndex (AbstractSequencerView.COLOR_STEP_HILITE_NO_CONTENT, LAUNCHCONTROL_COLOR_GREEN_LO);
        this.registerColorIndex (AbstractSequencerView.COLOR_STEP_HILITE_CONTENT, LAUNCHCONTROL_COLOR_GREEN);
        this.registerColorIndex (AbstractSequencerView.COLOR_STEP_MUTED, LAUNCHCONTROL_COLOR_RED);
        this.registerColorIndex (AbstractSequencerView.COLOR_STEP_MUTED_CONT, LAUNCHCONTROL_COLOR_RED_LO);
        this.registerColorIndex (AbstractSequencerView.COLOR_STEP_SELECTED, LAUNCHCONTROL_COLOR_YELLOW);
        this.registerColorIndex (AbstractSequencerView.COLOR_NO_CONTENT, LAUNCHCONTROL_COLOR_BLACK);
        this.registerColorIndex (AbstractSequencerView.COLOR_NO_CONTENT_4, LAUNCHCONTROL_COLOR_BLACK);
        this.registerColorIndex (AbstractSequencerView.COLOR_CONTENT, LAUNCHCONTROL_COLOR_AMBER);
        this.registerColorIndex (AbstractSequencerView.COLOR_CONTENT_CONT, LAUNCHCONTROL_COLOR_AMBER_LO);

        this.registerColorIndex (AbstractDrumView.COLOR_PAD_OFF, LAUNCHCONTROL_COLOR_BLACK);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_RECORD, LAUNCHCONTROL_COLOR_RED);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_PLAY, LAUNCHCONTROL_COLOR_GREEN);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_SELECTED, LAUNCHCONTROL_COLOR_YELLOW);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_MUTED, LAUNCHCONTROL_COLOR_AMBER_LO);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_HAS_CONTENT, LAUNCHCONTROL_COLOR_YELLOW_LO);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_NO_CONTENT, LAUNCHCONTROL_COLOR_BLACK);

        for (int i = 0; i < 128; i++)
            this.registerColor (i, ColorEx.BLACK);

        this.registerColor (LAUNCHCONTROL_COLOR_BLACK, ColorEx.BLACK);
        this.registerColor (LAUNCHCONTROL_COLOR_RED, DAWColor.DAW_COLOR_REDDISH_BROWN.getColor ());
        this.registerColor (LAUNCHCONTROL_COLOR_RED_LO, ColorEx.fromRGB (39, 4, 1));
        this.registerColor (LAUNCHCONTROL_COLOR_AMBER, DAWColor.DAW_COLOR_REDDISH_BROWN.getColor ());
        this.registerColor (LAUNCHCONTROL_COLOR_AMBER_LO, DAWColor.DAW_COLOR_DARK_BROWN.getColor ());
        this.registerColor (LAUNCHCONTROL_COLOR_YELLOW, ColorEx.fromRGB (107, 105, 1));
        this.registerColor (LAUNCHCONTROL_COLOR_YELLOW_LO, ColorEx.fromRGB (37, 36, 1));
        this.registerColor (LAUNCHCONTROL_COLOR_GREEN, ColorEx.fromRGB (1, 104, 1));
        this.registerColor (LAUNCHCONTROL_COLOR_GREEN_LO, ColorEx.fromRGB (1, 36, 1));
        this.registerColor (LAUNCHCONTROL_COLOR_ON, ColorEx.WHITE);
    }
}