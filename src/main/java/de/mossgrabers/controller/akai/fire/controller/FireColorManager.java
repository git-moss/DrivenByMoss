// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.fire.controller;

import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorEx;
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
 * Different colors to use for the pads and buttons of Akai Fire.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class FireColorManager extends ColorManager
{
    /** ID for color when button is turned on v2. */
    public static final String BUTTON_STATE_ON2         = "BUTTON_STATE_ON2";
    /** ID for color when button is highlighted v2. */
    public static final String BUTTON_STATE_HI2         = "BUTTON_STATE_HI2";

    private static final int   DAW_COLOR_START          = 100;

    private static final int   SCALE_COLOR_OCTAVE       = 50;
    private static final int   SCALE_COLOR_NOTE         = 51;
    private static final int   SCALE_COLOR_OUT_OF_SCALE = 52;

    /** ID for the color black. */
    public static final int    FIRE_COLOR_BLACK         = 0;
    /** ID for the color white. */
    public static final int    FIRE_COLOR_WHITE         = 59;
    /** ID for the color green. */
    public static final int    FIRE_COLOR_GREEN         = 60;
    /** ID for the color dark green. */
    public static final int    FIRE_COLOR_DARK_GREEN    = 61;
    /** ID for the color red. */
    public static final int    FIRE_COLOR_RED           = 62;
    /** ID for the color dark red. */
    public static final int    FIRE_COLOR_DARK_RED      = 63;
    /** ID for the color darker red. */
    public static final int    FIRE_COLOR_DARKER_RED    = 64;
    /** ID for the color blue. */
    public static final int    FIRE_COLOR_BLUE          = 65;
    /** ID for the color dark blue. */
    public static final int    FIRE_COLOR_DARK_BLUE     = 66;
    /** ID for the color ocean. */
    public static final int    FIRE_COLOR_DARK_OCEAN    = 67;
    /** ID for the color orange. */
    public static final int    FIRE_COLOR_ORANGE        = 68;
    /** ID for the color dark orange. */
    public static final int    FIRE_COLOR_DARK_ORANGE   = 69;
    /** ID for the color darker orange. */
    public static final int    FIRE_COLOR_DARKER_ORANGE = 70;
    /** ID for the color yellow. */
    public static final int    FIRE_COLOR_YELLOW        = 71;
    /** ID for the color dark yellow. */
    public static final int    FIRE_COLOR_DARK_YELLOW   = 72;
    /** ID for the color darker yellow. */
    public static final int    FIRE_COLOR_DARKER_YELLOW = 73;
    /** ID for the color brown. */
    public static final int    FIRE_COLOR_BROWN         = 74;
    /** ID for the color gray. */
    public static final int    FIRE_COLOR_GRAY          = 75;


    /**
     * Private due to utility class.
     */
    public FireColorManager ()
    {
        this.registerColorIndex (Scales.SCALE_COLOR_OFF, FIRE_COLOR_BLACK);
        this.registerColorIndex (Scales.SCALE_COLOR_OCTAVE, SCALE_COLOR_OCTAVE);
        this.registerColorIndex (Scales.SCALE_COLOR_NOTE, SCALE_COLOR_NOTE);
        this.registerColorIndex (Scales.SCALE_COLOR_OUT_OF_SCALE, SCALE_COLOR_OUT_OF_SCALE);

        this.registerColorIndex (IPadGrid.GRID_OFF, FIRE_COLOR_BLACK);

        this.registerColorIndex (AbstractSequencerView.COLOR_STEP_HILITE_NO_CONTENT, FIRE_COLOR_DARK_GREEN);
        this.registerColorIndex (AbstractSequencerView.COLOR_STEP_HILITE_CONTENT, FIRE_COLOR_GREEN);
        this.registerColorIndex (AbstractSequencerView.COLOR_STEP_MUTED, FIRE_COLOR_GRAY);
        this.registerColorIndex (AbstractSequencerView.COLOR_STEP_MUTED_CONT, FIRE_COLOR_GRAY);
        this.registerColorIndex (AbstractSequencerView.COLOR_STEP_SELECTED, FIRE_COLOR_DARKER_YELLOW);

        this.registerColorIndex (AbstractSequencerView.COLOR_NO_CONTENT, FIRE_COLOR_BLACK);
        this.registerColorIndex (AbstractSequencerView.COLOR_NO_CONTENT_4, FIRE_COLOR_GRAY);
        this.registerColorIndex (AbstractSequencerView.COLOR_CONTENT, FIRE_COLOR_BLUE);
        this.registerColorIndex (AbstractSequencerView.COLOR_CONTENT_CONT, FIRE_COLOR_DARK_BLUE);
        this.registerColorIndex (AbstractSequencerView.COLOR_PAGE, FIRE_COLOR_GRAY);
        this.registerColorIndex (AbstractSequencerView.COLOR_ACTIVE_PAGE, FIRE_COLOR_RED);
        this.registerColorIndex (AbstractSequencerView.COLOR_SELECTED_PAGE, FIRE_COLOR_DARKER_RED);
        this.registerColorIndex (AbstractSequencerView.COLOR_RESOLUTION, FIRE_COLOR_DARK_ORANGE);
        this.registerColorIndex (AbstractSequencerView.COLOR_RESOLUTION_SELECTED, FIRE_COLOR_ORANGE);
        this.registerColorIndex (AbstractSequencerView.COLOR_RESOLUTION_OFF, FIRE_COLOR_BLACK);
        this.registerColorIndex (AbstractSequencerView.COLOR_TRANSPOSE, FIRE_COLOR_WHITE);
        this.registerColorIndex (AbstractSequencerView.COLOR_TRANSPOSE_SELECTED, FIRE_COLOR_YELLOW);

        this.registerColorIndex (AbstractDrumView.COLOR_PAD_OFF, FIRE_COLOR_BLACK);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_RECORD, FIRE_COLOR_RED);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_PLAY, FIRE_COLOR_GREEN);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_SELECTED, FIRE_COLOR_BLUE);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_MUTED, FIRE_COLOR_BROWN);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_HAS_CONTENT, FIRE_COLOR_YELLOW);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_NO_CONTENT, FIRE_COLOR_DARK_YELLOW);

        this.registerColorIndex (AbstractPlayView.COLOR_OFF, FIRE_COLOR_BLACK);
        this.registerColorIndex (AbstractPlayView.COLOR_PLAY, FIRE_COLOR_GREEN);
        this.registerColorIndex (AbstractPlayView.COLOR_RECORD, FIRE_COLOR_RED);

        this.registerColorIndex (AbstractSessionView.COLOR_SCENE, FIRE_COLOR_DARK_GREEN);
        this.registerColorIndex (AbstractSessionView.COLOR_SELECTED_SCENE, FIRE_COLOR_GREEN);
        this.registerColorIndex (AbstractSessionView.COLOR_SCENE_OFF, FIRE_COLOR_BLACK);

        this.registerColorIndex (AbstractFeatureGroup.BUTTON_COLOR_OFF, 0);

        this.registerColorIndex (ColorManager.BUTTON_STATE_OFF, 0);
        this.registerColorIndex (ColorManager.BUTTON_STATE_ON, 1);
        this.registerColorIndex (ColorManager.BUTTON_STATE_HI, 3);
        this.registerColorIndex (FireColorManager.BUTTON_STATE_ON2, 2);
        this.registerColorIndex (FireColorManager.BUTTON_STATE_HI2, 4);

        this.registerColor (FIRE_COLOR_BLACK, ColorEx.BLACK);
        this.registerColor (FIRE_COLOR_WHITE, ColorEx.WHITE);
        this.registerColor (FIRE_COLOR_GREEN, ColorEx.GREEN);
        this.registerColor (FIRE_COLOR_DARK_GREEN, ColorEx.DARK_GREEN);
        this.registerColor (FIRE_COLOR_RED, ColorEx.RED);
        this.registerColor (FIRE_COLOR_DARK_RED, ColorEx.DARK_RED);
        this.registerColor (FIRE_COLOR_DARKER_RED, ColorEx.fromRGB (20, 0, 0));
        this.registerColor (FIRE_COLOR_BLUE, ColorEx.BLUE);
        this.registerColor (FIRE_COLOR_DARK_BLUE, ColorEx.DARK_BLUE);
        this.registerColor (FIRE_COLOR_DARK_OCEAN, ColorEx.SKY_BLUE);
        this.registerColor (FIRE_COLOR_ORANGE, ColorEx.ORANGE);
        this.registerColor (FIRE_COLOR_DARK_ORANGE, ColorEx.DARK_ORANGE);
        this.registerColor (FIRE_COLOR_DARKER_ORANGE, ColorEx.fromRGB (32, 14, 0));
        this.registerColor (FIRE_COLOR_DARK_YELLOW, ColorEx.DARK_YELLOW);
        this.registerColor (FIRE_COLOR_YELLOW, ColorEx.YELLOW);
        this.registerColor (FIRE_COLOR_DARKER_YELLOW, ColorEx.fromRGB (20, 20, 0));
        this.registerColor (FIRE_COLOR_BROWN, ColorEx.BROWN);
        this.registerColor (FIRE_COLOR_GRAY, ColorEx.GRAY);

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
        if (ButtonID.isPad (buttonID))
        {
            if (colorIndex >= DAW_COLOR_START)
                return DAWColor.getColorEntry (colorIndex - DAW_COLOR_START);

            return super.getColor (colorIndex, buttonID);
        }

        if (colorIndex == 0)
            return ColorEx.BLACK;

        switch (buttonID)
        {
            // Pattern up/down, grid left/right
            case ARROW_UP:
            case ARROW_DOWN:
            case ARROW_LEFT:
            case ARROW_RIGHT:
            case BROWSE:
                return colorIndex == 1 ? ColorEx.DARK_RED : ColorEx.RED;

            // Solo 1-4
            case ROW_SELECT_1:
            case ROW_SELECT_2:
            case ROW_SELECT_3:
            case ROW_SELECT_4:
            case SCENE1:
            case SCENE2:
            case SCENE3:
            case SCENE4:
                return colorIndex == 1 ? ColorEx.DARK_GREEN : ColorEx.GREEN;

            case ALT:
            case STOP:
                return colorIndex == 1 ? ColorEx.DARK_ORANGE : ColorEx.ORANGE;

            case SEQUENCER:
            case NOTE:
            case DRUM:
            case SESSION: // Perform
            case SHIFT:
            case RECORD:
                switch (colorIndex)
                {
                    case 1:
                        return ColorEx.DARK_RED;
                    case 2:
                        return ColorEx.DARK_ORANGE;
                    case 3:
                        return ColorEx.RED;
                    case 4:
                        return ColorEx.ORANGE;
                    default:
                        return ColorEx.BLACK;
                }

            case METRONOME:
            case PLAY:
                switch (colorIndex)
                {
                    case 1:
                        return ColorEx.DARK_GREEN;
                    case 2:
                        return ColorEx.DARK_ORANGE;
                    case 3:
                        return ColorEx.GREEN;
                    case 4:
                        return ColorEx.ORANGE;
                    default:
                        return ColorEx.BLACK;
                }

            default:
                return ColorEx.BLACK;
        }
    }
}