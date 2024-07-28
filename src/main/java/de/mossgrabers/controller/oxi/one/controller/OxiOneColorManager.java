// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.oxi.one.controller;

import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.color.ColorIndexException;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.featuregroup.AbstractFeatureGroup;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.view.AbstractPlayView;
import de.mossgrabers.framework.view.AbstractSessionView;
import de.mossgrabers.framework.view.sequencer.AbstractDrumView;
import de.mossgrabers.framework.view.sequencer.AbstractSequencerView;


/**
 * Different colors to use for the pads and buttons of OXI One.
 *
 * @author Jürgen Moßgraber
 */
public class OxiOneColorManager extends ColorManager
{
    private static final int DAW_COLOR_START             = 100;

    private static final int SCALE_COLOR_OCTAVE          = 50;
    private static final int SCALE_COLOR_NOTE            = 51;
    private static final int SCALE_COLOR_OUT_OF_SCALE    = 52;

    /** ID for the color black. */
    public static final int  OXI_ONE_COLOR_BLACK         = 0;
    /** ID for the color white. */
    public static final int  OXI_ONE_COLOR_WHITE         = 1;
    /** ID for the color green. */
    public static final int  OXI_ONE_COLOR_GREEN         = 2;
    /** ID for the color dark green. */
    public static final int  OXI_ONE_COLOR_DARK_GREEN    = 3;
    /** ID for the color red. */
    public static final int  OXI_ONE_COLOR_RED           = 4;
    /** ID for the color dark red. */
    public static final int  OXI_ONE_COLOR_DARK_RED      = 5;
    /** ID for the color darker red. */
    public static final int  OXI_ONE_COLOR_DARKER_RED    = 6;
    /** ID for the color blue. */
    public static final int  OXI_ONE_COLOR_BLUE          = 7;
    /** ID for the color dark blue. */
    public static final int  OXI_ONE_COLOR_DARK_BLUE     = 8;
    /** ID for the color darker blue. */
    public static final int  OXI_ONE_COLOR_DARKER_BLUE   = 9;
    /** ID for the color ocean. */
    public static final int  OXI_ONE_COLOR_DARK_OCEAN    = 10;
    /** ID for the color orange. */
    public static final int  OXI_ONE_COLOR_ORANGE        = 11;
    /** ID for the color dark orange. */
    public static final int  OXI_ONE_COLOR_DARK_ORANGE   = 12;
    /** ID for the color darker orange. */
    public static final int  OXI_ONE_COLOR_DARKER_ORANGE = 13;
    /** ID for the color yellow. */
    public static final int  OXI_ONE_COLOR_YELLOW        = 14;
    /** ID for the color dark yellow. */
    public static final int  OXI_ONE_COLOR_DARK_YELLOW   = 15;
    /** ID for the color darker yellow. */
    public static final int  OXI_ONE_COLOR_DARKER_YELLOW = 16;
    /** ID for the color brown. */
    public static final int  OXI_ONE_COLOR_BROWN         = 17;
    /** ID for the color gray. */
    public static final int  OXI_ONE_COLOR_GRAY          = 18;


    /**
     * Private due to utility class.
     */
    public OxiOneColorManager ()
    {
        this.registerColorIndex (Scales.SCALE_COLOR_OFF, OXI_ONE_COLOR_BLACK);
        this.registerColorIndex (Scales.SCALE_COLOR_OCTAVE, SCALE_COLOR_OCTAVE);
        this.registerColorIndex (Scales.SCALE_COLOR_NOTE, SCALE_COLOR_NOTE);
        this.registerColorIndex (Scales.SCALE_COLOR_OUT_OF_SCALE, SCALE_COLOR_OUT_OF_SCALE);

        this.registerColorIndex (IPadGrid.GRID_OFF, OXI_ONE_COLOR_BLACK);

        this.registerColorIndex (AbstractSequencerView.COLOR_STEP_HILITE_NO_CONTENT, OXI_ONE_COLOR_DARK_GREEN);
        this.registerColorIndex (AbstractSequencerView.COLOR_STEP_HILITE_CONTENT, OXI_ONE_COLOR_GREEN);
        this.registerColorIndex (AbstractSequencerView.COLOR_STEP_MUTED, OXI_ONE_COLOR_GRAY);
        this.registerColorIndex (AbstractSequencerView.COLOR_STEP_MUTED_CONT, OXI_ONE_COLOR_GRAY);
        this.registerColorIndex (AbstractSequencerView.COLOR_STEP_SELECTED, OXI_ONE_COLOR_DARKER_YELLOW);

        this.registerColorIndex (AbstractSequencerView.COLOR_NO_CONTENT, OXI_ONE_COLOR_BLACK);
        this.registerColorIndex (AbstractSequencerView.COLOR_NO_CONTENT_4, OXI_ONE_COLOR_GRAY);
        this.registerColorIndex (AbstractSequencerView.COLOR_CONTENT, OXI_ONE_COLOR_BLUE);
        this.registerColorIndex (AbstractSequencerView.COLOR_CONTENT_CONT, OXI_ONE_COLOR_DARK_BLUE);
        this.registerColorIndex (AbstractSequencerView.COLOR_PAGE, OXI_ONE_COLOR_GRAY);
        this.registerColorIndex (AbstractSequencerView.COLOR_ACTIVE_PAGE, OXI_ONE_COLOR_RED);
        this.registerColorIndex (AbstractSequencerView.COLOR_SELECTED_PAGE, OXI_ONE_COLOR_DARKER_RED);
        this.registerColorIndex (AbstractSequencerView.COLOR_RESOLUTION, OXI_ONE_COLOR_DARK_ORANGE);
        this.registerColorIndex (AbstractSequencerView.COLOR_RESOLUTION_SELECTED, OXI_ONE_COLOR_ORANGE);
        this.registerColorIndex (AbstractSequencerView.COLOR_RESOLUTION_OFF, OXI_ONE_COLOR_BLACK);
        this.registerColorIndex (AbstractSequencerView.COLOR_TRANSPOSE, OXI_ONE_COLOR_WHITE);
        this.registerColorIndex (AbstractSequencerView.COLOR_TRANSPOSE_SELECTED, OXI_ONE_COLOR_YELLOW);

        this.registerColorIndex (AbstractDrumView.COLOR_PAD_OFF, OXI_ONE_COLOR_BLACK);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_RECORD, OXI_ONE_COLOR_RED);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_PLAY, OXI_ONE_COLOR_GREEN);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_SELECTED, OXI_ONE_COLOR_BLUE);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_MUTED, OXI_ONE_COLOR_BROWN);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_HAS_CONTENT, OXI_ONE_COLOR_YELLOW);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_NO_CONTENT, OXI_ONE_COLOR_DARK_YELLOW);

        this.registerColorIndex (AbstractPlayView.COLOR_OFF, OXI_ONE_COLOR_BLACK);
        this.registerColorIndex (AbstractPlayView.COLOR_PLAY, OXI_ONE_COLOR_GREEN);
        this.registerColorIndex (AbstractPlayView.COLOR_RECORD, OXI_ONE_COLOR_RED);

        this.registerColorIndex (AbstractSessionView.COLOR_SCENE, OXI_ONE_COLOR_DARK_GREEN);
        this.registerColorIndex (AbstractSessionView.COLOR_SELECTED_SCENE, OXI_ONE_COLOR_GREEN);
        this.registerColorIndex (AbstractSessionView.COLOR_SCENE_OFF, OXI_ONE_COLOR_BLACK);

        this.registerColorIndex (AbstractFeatureGroup.BUTTON_COLOR_OFF, 0);

        this.registerColorIndex (ColorManager.BUTTON_STATE_OFF, 0);
        this.registerColorIndex (ColorManager.BUTTON_STATE_ON, 0);
        this.registerColorIndex (ColorManager.BUTTON_STATE_HI, 1);

        this.registerColor (OXI_ONE_COLOR_BLACK, ColorEx.BLACK);
        this.registerColor (OXI_ONE_COLOR_WHITE, ColorEx.WHITE);
        this.registerColor (OXI_ONE_COLOR_GREEN, ColorEx.GREEN);
        this.registerColor (OXI_ONE_COLOR_DARK_GREEN, ColorEx.DARK_GREEN);
        this.registerColor (OXI_ONE_COLOR_RED, ColorEx.RED);
        this.registerColor (OXI_ONE_COLOR_DARK_RED, ColorEx.DARK_RED);
        this.registerColor (OXI_ONE_COLOR_DARKER_RED, ColorEx.DARKER_RED);
        this.registerColor (OXI_ONE_COLOR_BLUE, ColorEx.BLUE);
        this.registerColor (OXI_ONE_COLOR_DARK_BLUE, ColorEx.DARK_BLUE);
        this.registerColor (OXI_ONE_COLOR_DARKER_BLUE, ColorEx.DARKER_BLUE);
        this.registerColor (OXI_ONE_COLOR_DARK_OCEAN, ColorEx.SKY_BLUE);
        this.registerColor (OXI_ONE_COLOR_ORANGE, ColorEx.ORANGE);
        this.registerColor (OXI_ONE_COLOR_DARK_ORANGE, ColorEx.DARK_ORANGE);
        this.registerColor (OXI_ONE_COLOR_DARKER_ORANGE, ColorEx.DARKER_ORANGE);
        this.registerColor (OXI_ONE_COLOR_YELLOW, ColorEx.YELLOW);
        this.registerColor (OXI_ONE_COLOR_DARK_YELLOW, ColorEx.DARK_YELLOW);
        this.registerColor (OXI_ONE_COLOR_DARKER_YELLOW, ColorEx.DARKER_YELLOW);
        this.registerColor (OXI_ONE_COLOR_BROWN, ColorEx.BROWN);
        this.registerColor (OXI_ONE_COLOR_GRAY, ColorEx.DARKER_GRAY);

        this.registerColor (SCALE_COLOR_OCTAVE, ColorEx.BLUE);
        this.registerColor (SCALE_COLOR_NOTE, ColorEx.WHITE);
        this.registerColor (SCALE_COLOR_OUT_OF_SCALE, ColorEx.DARK_GRAY);

        final DAWColor [] values = DAWColor.values ();
        for (int i = 0; i < values.length; i++)
            this.registerColorIndex (values[i], DAW_COLOR_START + i);
    }


    /** {@inheritDoc} */
    @Override
    public ColorEx getColor (final int colorIndex, final ButtonID buttonID)
    {
        if (ButtonID.isInRange (buttonID, ButtonID.PAD1, 128))
        {
            if (colorIndex >= DAW_COLOR_START)
                return DAWColor.getColorEntry (colorIndex - DAW_COLOR_START);

            try
            {
                return super.getColor (colorIndex, buttonID);
            }
            catch (final ColorIndexException ex)
            {
                return ColorEx.RED;
            }
        }

        if (colorIndex == this.getColorIndex (ColorManager.BUTTON_STATE_OFF))
            return ColorEx.BLACK;
        if (colorIndex == this.getColorIndex (ColorManager.BUTTON_STATE_ON))
            return ColorEx.DARK_GRAY;
        // ColorManager.BUTTON_STATE_HI
        return ColorEx.LIGHT_GRAY;
    }
}