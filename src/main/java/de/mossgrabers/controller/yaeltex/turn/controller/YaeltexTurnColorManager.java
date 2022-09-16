// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.yaeltex.turn.controller;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.featuregroup.AbstractFeatureGroup;
import de.mossgrabers.framework.view.ScenePlayView;


/**
 * Different colors to use for the pads and buttons of the Yaeltex Turn.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
@SuppressWarnings("javadoc")
public class YaeltexTurnColorManager extends ColorManager
{
    private static final ColorEx [] COLOR_TABLE                     =
    {
        ColorEx.fromRGB (0x00, 0x00, 0x00),                                                          // 00
        ColorEx.fromRGB (0xf2, 0xc, 0xc),
        ColorEx.fromRGB (0xf2, 0x58, 0x58),
        ColorEx.fromRGB (0xf2, 0xa5, 0xa5),
        ColorEx.fromRGB (0xf2, 0x2c, 0xc),
        ColorEx.fromRGB (0xf2, 0x6e, 0x58),
        ColorEx.fromRGB (0xf2, 0xb0, 0xa5),
        ColorEx.fromRGB (0xf2, 0x4d, 0xc),
        ColorEx.fromRGB (0xf2, 0x84, 0x58),
        ColorEx.fromRGB (0xf2, 0xbb, 0xa5),
        ColorEx.fromRGB (0xf2, 0x6e, 0xc),                                                           // 10
        ColorEx.fromRGB (0xf2, 0x9a, 0x58),
        ColorEx.fromRGB (0xf2, 0xc6, 0xa5),
        ColorEx.fromRGB (0xf2, 0x8f, 0xc),
        ColorEx.fromRGB (0xf2, 0xb0, 0x58),
        ColorEx.fromRGB (0xf2, 0xd1, 0xa5),
        ColorEx.fromRGB (0xf2, 0xb0, 0xc),
        ColorEx.fromRGB (0xf2, 0xc6, 0x58),
        ColorEx.fromRGB (0xf2, 0xdc, 0xa5),
        ColorEx.fromRGB (0xf2, 0xd1, 0xc),
        ColorEx.fromRGB (0xf2, 0xdc, 0x58),                                                          // 20
        ColorEx.fromRGB (0xf2, 0xe7, 0xa5),
        ColorEx.fromRGB (0xf2, 0xf2, 0xc),
        ColorEx.fromRGB (0xf2, 0xf2, 0x58),
        ColorEx.fromRGB (0xf2, 0xf2, 0xa5),
        ColorEx.fromRGB (0xd1, 0xf2, 0xc),
        ColorEx.fromRGB (0xdc, 0xf2, 0x58),
        ColorEx.fromRGB (0xd1, 0xf2, 0xa5),
        ColorEx.fromRGB (0xb0, 0xf2, 0xc),
        ColorEx.fromRGB (0xc6, 0xf2, 0x58),
        ColorEx.fromRGB (0xb0, 0xf2, 0xa5),                                                          // 30
        ColorEx.fromRGB (0x8f, 0xf2, 0xc),
        ColorEx.fromRGB (0xb0, 0xf2, 0x58),
        ColorEx.fromRGB (0x8f, 0xf2, 0xa5),
        ColorEx.fromRGB (0x6e, 0xf2, 0xc),
        ColorEx.fromRGB (0x9a, 0xf2, 0x58),
        ColorEx.fromRGB (0x6e, 0xf2, 0xa5),
        ColorEx.fromRGB (0x4d, 0xf2, 0xc),
        ColorEx.fromRGB (0x84, 0xf2, 0x58),
        ColorEx.fromRGB (0x4d, 0xf2, 0xa5),
        ColorEx.fromRGB (0x2c, 0xf2, 0xc),                                                           // 40
        ColorEx.fromRGB (0x6e, 0xf2, 0x58),
        ColorEx.fromRGB (0x2c, 0xf2, 0xa5),
        ColorEx.fromRGB (0xc, 0xf2, 0xc),
        ColorEx.fromRGB (0x58, 0xf2, 0x58),
        ColorEx.fromRGB (0xc, 0xf2, 0xa5),
        ColorEx.fromRGB (0xc, 0xf2, 0x2c),
        ColorEx.fromRGB (0x58, 0xf2, 0x6e),
        ColorEx.fromRGB (0xc, 0xf2, 0xb0),
        ColorEx.fromRGB (0xc, 0xf2, 0x4d),
        ColorEx.fromRGB (0x58, 0xf2, 0x84),                                                          // 50
        ColorEx.fromRGB (0xc, 0xf2, 0xbb),
        ColorEx.fromRGB (0xc, 0xf2, 0x6e),
        ColorEx.fromRGB (0x58, 0xf2, 0x9a),
        ColorEx.fromRGB (0xc, 0xf2, 0xc6),
        ColorEx.fromRGB (0xc, 0xf2, 0x8f),
        ColorEx.fromRGB (0x58, 0xf2, 0xb0),
        ColorEx.fromRGB (0xc, 0xf2, 0xd1),
        ColorEx.fromRGB (0xc, 0xf2, 0xb0),
        ColorEx.fromRGB (0x58, 0xf2, 0xc6),
        ColorEx.fromRGB (0xc, 0xf2, 0xdc),                                                           // 60
        ColorEx.fromRGB (0xc, 0xf2, 0xd1),
        ColorEx.fromRGB (0x58, 0xf2, 0xdc),
        ColorEx.fromRGB (0xc, 0xf2, 0xe7),
        ColorEx.fromRGB (0xc, 0xf2, 0xf2),
        ColorEx.fromRGB (0x58, 0xf2, 0xf2),
        ColorEx.fromRGB (0xc, 0xf2, 0xf2),
        ColorEx.fromRGB (0xc, 0xd1, 0xf2),
        ColorEx.fromRGB (0x58, 0xdc, 0xf2),
        ColorEx.fromRGB (0xc, 0xe7, 0xf2),
        ColorEx.fromRGB (0xc, 0xb0, 0xf2),                                                           // 70
        ColorEx.fromRGB (0x58, 0xc6, 0xf2),
        ColorEx.fromRGB (0xc, 0xdc, 0xf2),
        ColorEx.fromRGB (0xc, 0x8f, 0xf2),
        ColorEx.fromRGB (0x58, 0xb0, 0xf2),
        ColorEx.fromRGB (0xc, 0xd1, 0xf2),
        ColorEx.fromRGB (0xc, 0x6e, 0xf2),
        ColorEx.fromRGB (0x58, 0x9a, 0xf2),
        ColorEx.fromRGB (0xc, 0xc6, 0xf2),
        ColorEx.fromRGB (0xc, 0x4d, 0xf2),
        ColorEx.fromRGB (0x58, 0x84, 0xf2),                                                          // 80
        ColorEx.fromRGB (0xc, 0xbb, 0xf2),
        ColorEx.fromRGB (0xc, 0x2c, 0xf2),
        ColorEx.fromRGB (0x58, 0x6e, 0xf2),
        ColorEx.fromRGB (0xc, 0xb0, 0xf2),
        ColorEx.fromRGB (0xc, 0xc, 0xf2),
        ColorEx.fromRGB (0x58, 0x58, 0xf2),
        ColorEx.fromRGB (0xc, 0xa5, 0xf2),
        ColorEx.fromRGB (0x2c, 0xc, 0xf2),
        ColorEx.fromRGB (0x6e, 0x58, 0xf2),
        ColorEx.fromRGB (0x2c, 0xa5, 0xf2),                                                          // 90
        ColorEx.fromRGB (0x4d, 0xc, 0xf2),
        ColorEx.fromRGB (0x84, 0x58, 0xf2),
        ColorEx.fromRGB (0x4d, 0xa5, 0xf2),
        ColorEx.fromRGB (0x6e, 0xc, 0xf2),
        ColorEx.fromRGB (0x9a, 0x58, 0xf2),
        ColorEx.fromRGB (0x6e, 0xa5, 0xf2),
        ColorEx.fromRGB (0x8f, 0xc, 0xf2),
        ColorEx.fromRGB (0xb0, 0x58, 0xf2),
        ColorEx.fromRGB (0x8f, 0xa5, 0xf2),
        ColorEx.fromRGB (0xb0, 0xc, 0xf2),                                                           // 100
        ColorEx.fromRGB (0xc6, 0x58, 0xf2),
        ColorEx.fromRGB (0xb0, 0xa5, 0xf2),
        ColorEx.fromRGB (0xd1, 0xc, 0xf2),
        ColorEx.fromRGB (0xdc, 0x58, 0xf2),
        ColorEx.fromRGB (0xd1, 0xa5, 0xf2),
        ColorEx.fromRGB (0xf2, 0xc, 0xf2),
        ColorEx.fromRGB (0xf2, 0x58, 0xf2),
        ColorEx.fromRGB (0xf2, 0xa5, 0xf2),
        ColorEx.fromRGB (0xf2, 0xc, 0xd1),
        ColorEx.fromRGB (0xf2, 0x58, 0xdc),                                                          // 110
        ColorEx.fromRGB (0xf2, 0xa5, 0xe7),
        ColorEx.fromRGB (0xf2, 0xc, 0xb0),
        ColorEx.fromRGB (0xf2, 0x58, 0xc6),
        ColorEx.fromRGB (0xf2, 0xa5, 0xdc),
        ColorEx.fromRGB (0xf2, 0xc, 0x8f),
        ColorEx.fromRGB (0xf2, 0x58, 0xb0),
        ColorEx.fromRGB (0xf2, 0xa5, 0xd1),
        ColorEx.fromRGB (0xf2, 0xc, 0x6e),
        ColorEx.fromRGB (0xf2, 0x58, 0x9a),
        ColorEx.fromRGB (0xf2, 0xa5, 0xc6),                                                          // 120
        ColorEx.fromRGB (0xf2, 0xc, 0x4d),
        ColorEx.fromRGB (0xf2, 0x58, 0x84),
        ColorEx.fromRGB (0xf2, 0xa5, 0xbb),
        ColorEx.fromRGB (0xf2, 0xc, 0x2c),
        ColorEx.fromRGB (0xf2, 0x58, 0x6e),
        ColorEx.fromRGB (0xf2, 0xa5, 0xb0),
        ColorEx.fromRGB (0xf0, 0xf0, 0xf0)
    };

    public static final String      BUTTON_STATE_CROSS_A            = "BUTTON_STATE_CROSS_A";
    public static final String      BUTTON_STATE_CROSS_B            = "BUTTON_STATE_CROSS_B";
    public static final String      BUTTON_STATE_STOP_ON            = "BUTTON_STATE_STOP_ON";
    public static final String      BUTTON_STATE_REC_ARM_ON         = "BUTTON_STATE_REC_ARM_ON";
    public static final String      BUTTON_STATE_SOLO_ON            = "BUTTON_STATE_SOLO_ON";
    public static final String      BUTTON_STATE_MUTE_ON            = "BUTTON_STATE_MUTE_ON";
    public static final String      BUTTON_STATE_SELECT_ON          = "BUTTON_STATE_SELECT_ON";
    public static final String      BUTTON_STATE_SELECT_HI          = "BUTTON_STATE_SELECT_HI";
    public static final String      BUTTON_STATE_NEW_CLIP_LENGTH_ON = "BUTTON_STATE_NEW_CLIP_LENGTH";


    /**
     * Constructor.
     */
    public YaeltexTurnColorManager ()
    {
        final int indexBlack = getIndexFor (ColorEx.BLACK);
        final int indexWhite = getIndexFor (ColorEx.WHITE);
        final int indexLightGray = getIndexFor (ColorEx.LIGHT_GRAY);
        final int indexGray = getIndexFor (ColorEx.GRAY);
        final int indexDarkGray = getIndexFor (ColorEx.DARK_GRAY);
        final int indexRed = getIndexFor (ColorEx.RED);
        final int indexDarkRed = getIndexFor (ColorEx.DARK_RED);
        final int indexGreen = getIndexFor (ColorEx.GREEN);
        final int indexDarkGreen = getIndexFor (ColorEx.DARK_GREEN);
        final int indexBlue = getIndexFor (ColorEx.BLUE);
        final int indexDarkBlue = getIndexFor (ColorEx.DARK_BLUE);
        final int indexYellow = getIndexFor (ColorEx.YELLOW);
        final int indexDarkYellow = getIndexFor (ColorEx.DARK_YELLOW);
        final int indexOrange = getIndexFor (ColorEx.ORANGE);
        final int indexDarkOrange = getIndexFor (ColorEx.DARK_ORANGE);
        final int indexPink = getIndexFor (ColorEx.PINK);
        final int indexRose = getIndexFor (ColorEx.ROSE);
        final int indexBrown = getIndexFor (ColorEx.BROWN);
        final int indexDarkBrown = getIndexFor (ColorEx.DARK_BROWN);
        final int indexMint = getIndexFor (ColorEx.MINT);
        final int indexOlive = getIndexFor (ColorEx.OLIVE);
        final int indexSkyBlue = getIndexFor (ColorEx.SKY_BLUE);
        final int indexPurple = getIndexFor (ColorEx.PURPLE);
        final int indexDarkPurple = getIndexFor (ColorEx.DARK_PURPLE);
        final int indexRedWine = getIndexFor (ColorEx.RED_WINE);
        final int indexCyan = getIndexFor (ColorEx.CYAN);

        this.registerColorIndex (ColorManager.BUTTON_STATE_OFF, 0);
        this.registerColorIndex (ColorManager.BUTTON_STATE_ON, 0);
        this.registerColorIndex (ColorManager.BUTTON_STATE_HI, 127);

        this.registerColorIndex (AbstractFeatureGroup.BUTTON_COLOR_OFF, indexBlack);
        this.registerColorIndex (ScenePlayView.COLOR_SELECTED_PLAY_SCENE, indexWhite);

        this.registerColorIndex (BUTTON_STATE_CROSS_A, indexBlue);
        this.registerColorIndex (BUTTON_STATE_CROSS_B, indexCyan);
        this.registerColorIndex (BUTTON_STATE_STOP_ON, indexDarkGray);
        this.registerColorIndex (BUTTON_STATE_REC_ARM_ON, indexRed);
        this.registerColorIndex (BUTTON_STATE_SOLO_ON, indexYellow);
        this.registerColorIndex (BUTTON_STATE_MUTE_ON, indexOrange);
        this.registerColorIndex (BUTTON_STATE_SELECT_ON, indexWhite);
        this.registerColorIndex (BUTTON_STATE_SELECT_HI, indexYellow);
        this.registerColorIndex (BUTTON_STATE_NEW_CLIP_LENGTH_ON, indexBlue);

        // TODO

        // this.registerColorIndex (AbstractSequencerView.COLOR_STEP_HILITE_NO_CONTENT,
        // COLOR_GREEN_HI);
        // this.registerColorIndex (AbstractSequencerView.COLOR_STEP_HILITE_CONTENT,
        // COLOR_GREEN_LO);
        // this.registerColorIndex (AbstractSequencerView.COLOR_STEP_MUTED, indexDarkGray);
        // this.registerColorIndex (AbstractSequencerView.COLOR_STEP_MUTED_CONT, COLOR_GREY_LO);
        // this.registerColorIndex (AbstractSequencerView.COLOR_STEP_SELECTED, COLOR_WHITE);
        // this.registerColorIndex (AbstractSequencerView.COLOR_NO_CONTENT, indexBlack);
        // this.registerColorIndex (AbstractSequencerView.COLOR_NO_CONTENT_4, indexBlack);
        // this.registerColorIndex (AbstractSequencerView.COLOR_CONTENT, getIndexFor
        // (ColorEx.BLUE)_HI);
        // this.registerColorIndex (AbstractSequencerView.COLOR_CONTENT_CONT, getIndexFor
        // (ColorEx.BLUE)_LO);
        // this.registerColorIndex (AbstractSequencerView.COLOR_PAGE, COLOR_WHITE);
        // this.registerColorIndex (AbstractSequencerView.COLOR_ACTIVE_PAGE, COLOR_GREEN_HI);
        // this.registerColorIndex (AbstractSequencerView.COLOR_SELECTED_PAGE, getIndexFor
        // (ColorEx.BLUE)_LO);
        //
        // this.registerColorIndex (AbstractDrumView.COLOR_PAD_OFF, indexBlack);
        // this.registerColorIndex (AbstractDrumView.COLOR_PAD_RECORD, COLOR_RED_HI);
        // this.registerColorIndex (AbstractDrumView.COLOR_PAD_PLAY, COLOR_GREEN_HI);
        // this.registerColorIndex (AbstractDrumView.COLOR_PAD_SELECTED, getIndexFor
        // (ColorEx.BLUE)_HI);
        // this.registerColorIndex (AbstractDrumView.COLOR_PAD_MUTED, COLOR_AMBER_LO);
        // this.registerColorIndex (AbstractDrumView.COLOR_PAD_HAS_CONTENT, COLOR_YELLOW_HI);
        // this.registerColorIndex (AbstractDrumView.COLOR_PAD_NO_CONTENT, COLOR_YELLOW_LO);
        //
        // this.registerColorIndex (AbstractDrumExView.COLOR_EX_SELECT_ON, COLOR_WHITE);
        // this.registerColorIndex (AbstractDrumExView.COLOR_EX_SELECT_OFF, COLOR_GREY_LO);
        // this.registerColorIndex (AbstractDrumExView.COLOR_EX_MUTE_ON, COLOR_YELLOW_HI);
        // this.registerColorIndex (AbstractDrumExView.COLOR_EX_MUTE_OFF, COLOR_YELLOW_LO);
        // this.registerColorIndex (AbstractDrumExView.COLOR_EX_SOLO_ON, getIndexFor
        // (ColorEx.BLUE)_HI);
        // this.registerColorIndex (AbstractDrumExView.COLOR_EX_SOLO_OFF, getIndexFor
        // (ColorEx.BLUE)_LO);
        // this.registerColorIndex (AbstractDrumExView.COLOR_EX_BROWSE_ON, indexCyan_HI);
        // this.registerColorIndex (AbstractDrumExView.COLOR_EX_BROWSE_OFF, indexCyan_LO);
        // this.registerColorIndex (AbstractDrumExView.COLOR_EX_NOTE_REPEAT_ON, COLOR_ORCHID_HI);
        // this.registerColorIndex (AbstractDrumExView.COLOR_EX_NOTE_REPEAT_OFF, COLOR_ORCHID_LO);
        // this.registerColorIndex (AbstractDrumExView.COLOR_EX_NOTE_REPEAT_PERIOD_ON,
        // COLOR_SKY_HI);
        // this.registerColorIndex (AbstractDrumExView.COLOR_EX_NOTE_REPEAT_PERIOD_OFF,
        // COLOR_SKY_LO);
        // this.registerColorIndex (AbstractDrumExView.COLOR_EX_NOTE_REPEAT_LENGTH_ON,
        // COLOR_PINK_HI);
        // this.registerColorIndex (AbstractDrumExView.COLOR_EX_NOTE_REPEAT_LENGTH_OFF,
        // COLOR_PINK_LO);
        // this.registerColorIndex (AbstractDrumExView.COLOR_EX_TOGGLE_ON, indexBlack);
        // this.registerColorIndex (AbstractDrumExView.COLOR_EX_TOGGLE_OFF, indexBlack);

        this.registerColorIndex (IPadGrid.GRID_OFF, indexBlack);

        for (int i = 0; i < 128; i++)
            this.registerColor (i, COLOR_TABLE[i]);

        for (final DAWColor dawColor: DAWColor.values ())
        {
            this.registerColorIndex (dawColor, getIndexFor (dawColor.getColor ()));
            final String name = dawColor.name ();
            this.registerColor (this.getColorIndex (name), DAWColor.getColorEntry (name));
        }
    }


    /**
     * Get the index in the color palette which is closest to the given color.
     *
     * @param color The color for which to get the color index.
     * @return The index with the closest color
     */
    public static final int getIndexFor (ColorEx color)
    {
        return ColorEx.getClosestColorIndex (color, COLOR_TABLE);
    }
}