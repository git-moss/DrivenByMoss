// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.yaeltex.turn.controller;

import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.featuregroup.AbstractFeatureGroup;
import de.mossgrabers.framework.parameter.NoteAttribute;
import de.mossgrabers.framework.view.ScenePlayView;
import de.mossgrabers.framework.view.sequencer.AbstractDrumView;
import de.mossgrabers.framework.view.sequencer.AbstractSequencerView;

import java.util.EnumMap;
import java.util.Map;


/**
 * Different colors to use for the pads and buttons of the Yaeltex Turn.
 *
 * @author Jürgen Moßgraber
 */
@SuppressWarnings("javadoc")
public class YaeltexTurnColorManager extends ColorManager
{
    private static final ColorEx []           COLOR_TABLE            =
    {
        ColorEx.fromRGB (0x00, 0x00, 0x00),                                                                // 00
        ColorEx.fromRGB (0xf2, 0xc, 0xc),
        ColorEx.fromRGB (0xf2, 0x58, 0x58),
        ColorEx.fromRGB (0xf2, 0xa5, 0xa5),
        ColorEx.fromRGB (0xf2, 0x2c, 0xc),
        ColorEx.fromRGB (0xf2, 0x6e, 0x58),
        ColorEx.fromRGB (0xf2, 0xb0, 0xa5),
        ColorEx.fromRGB (0xf2, 0x4d, 0xc),
        ColorEx.fromRGB (0xf2, 0x84, 0x58),
        ColorEx.fromRGB (0xf2, 0xbb, 0xa5),
        ColorEx.fromRGB (0xf2, 0x6e, 0xc),                                                                 // 10
        ColorEx.fromRGB (0xf2, 0x9a, 0x58),
        ColorEx.fromRGB (0xf2, 0xc6, 0xa5),
        ColorEx.fromRGB (0xf2, 0x8f, 0xc),
        ColorEx.fromRGB (0xf2, 0xb0, 0x58),
        ColorEx.fromRGB (0xf2, 0xd1, 0xa5),
        ColorEx.fromRGB (0xf2, 0xb0, 0xc),
        ColorEx.fromRGB (0xf2, 0xc6, 0x58),
        ColorEx.fromRGB (0xf2, 0xdc, 0xa5),
        ColorEx.fromRGB (0xf2, 0xd1, 0xc),
        ColorEx.fromRGB (0xf2, 0xdc, 0x58),                                                                // 20
        ColorEx.fromRGB (0xf2, 0xe7, 0xa5),
        ColorEx.fromRGB (0xf2, 0xf2, 0xc),
        ColorEx.fromRGB (0xf2, 0xf2, 0x58),
        ColorEx.fromRGB (0xf2, 0xf2, 0xa5),
        ColorEx.fromRGB (0xd1, 0xf2, 0xc),
        ColorEx.fromRGB (0xdc, 0xf2, 0x58),
        ColorEx.fromRGB (0xd1, 0xf2, 0xa5),
        ColorEx.fromRGB (0xb0, 0xf2, 0xc),
        ColorEx.fromRGB (0xc6, 0xf2, 0x58),
        ColorEx.fromRGB (0xb0, 0xf2, 0xa5),                                                                // 30
        ColorEx.fromRGB (0x8f, 0xf2, 0xc),
        ColorEx.fromRGB (0xb0, 0xf2, 0x58),
        ColorEx.fromRGB (0x8f, 0xf2, 0xa5),
        ColorEx.fromRGB (0x6e, 0xf2, 0xc),
        ColorEx.fromRGB (0x9a, 0xf2, 0x58),
        ColorEx.fromRGB (0x6e, 0xf2, 0xa5),
        ColorEx.fromRGB (0x4d, 0xf2, 0xc),
        ColorEx.fromRGB (0x84, 0xf2, 0x58),
        ColorEx.fromRGB (0x4d, 0xf2, 0xa5),
        ColorEx.fromRGB (0x2c, 0xf2, 0xc),                                                                 // 40
        ColorEx.fromRGB (0x6e, 0xf2, 0x58),
        ColorEx.fromRGB (0x2c, 0xf2, 0xa5),
        ColorEx.fromRGB (0xc, 0xf2, 0xc),
        ColorEx.fromRGB (0x58, 0xf2, 0x58),
        ColorEx.fromRGB (0xc, 0xf2, 0xa5),
        ColorEx.fromRGB (0xc, 0xf2, 0x2c),
        ColorEx.fromRGB (0x58, 0xf2, 0x6e),
        ColorEx.fromRGB (0xc, 0xf2, 0xb0),
        ColorEx.fromRGB (0xc, 0xf2, 0x4d),
        ColorEx.fromRGB (0x58, 0xf2, 0x84),                                                                // 50
        ColorEx.fromRGB (0xc, 0xf2, 0xbb),
        ColorEx.fromRGB (0xc, 0xf2, 0x6e),
        ColorEx.fromRGB (0x58, 0xf2, 0x9a),
        ColorEx.fromRGB (0xc, 0xf2, 0xc6),
        ColorEx.fromRGB (0xc, 0xf2, 0x8f),
        ColorEx.fromRGB (0x58, 0xf2, 0xb0),
        ColorEx.fromRGB (0xc, 0xf2, 0xd1),
        ColorEx.fromRGB (0xc, 0xf2, 0xb0),
        ColorEx.fromRGB (0x58, 0xf2, 0xc6),
        ColorEx.fromRGB (0xc, 0xf2, 0xdc),                                                                 // 60
        ColorEx.fromRGB (0xc, 0xf2, 0xd1),
        ColorEx.fromRGB (0x58, 0xf2, 0xdc),
        ColorEx.fromRGB (0xc, 0xf2, 0xe7),
        ColorEx.fromRGB (0xc, 0xf2, 0xf2),
        ColorEx.fromRGB (0x58, 0xf2, 0xf2),
        ColorEx.fromRGB (0xc, 0xf2, 0xf2),
        ColorEx.fromRGB (0xc, 0xd1, 0xf2),
        ColorEx.fromRGB (0x58, 0xdc, 0xf2),
        ColorEx.fromRGB (0xc, 0xe7, 0xf2),
        ColorEx.fromRGB (0xc, 0xb0, 0xf2),                                                                 // 70
        ColorEx.fromRGB (0x58, 0xc6, 0xf2),
        ColorEx.fromRGB (0xc, 0xdc, 0xf2),
        ColorEx.fromRGB (0xc, 0x8f, 0xf2),
        ColorEx.fromRGB (0x58, 0xb0, 0xf2),
        ColorEx.fromRGB (0xc, 0xd1, 0xf2),
        ColorEx.fromRGB (0xc, 0x6e, 0xf2),
        ColorEx.fromRGB (0x58, 0x9a, 0xf2),
        ColorEx.fromRGB (0xc, 0xc6, 0xf2),
        ColorEx.fromRGB (0xc, 0x4d, 0xf2),
        ColorEx.fromRGB (0x58, 0x84, 0xf2),                                                                // 80
        ColorEx.fromRGB (0xc, 0xbb, 0xf2),
        ColorEx.fromRGB (0xc, 0x2c, 0xf2),
        ColorEx.fromRGB (0x58, 0x6e, 0xf2),
        ColorEx.fromRGB (0xc, 0xb0, 0xf2),
        ColorEx.fromRGB (0xc, 0xc, 0xf2),
        ColorEx.fromRGB (0x58, 0x58, 0xf2),
        ColorEx.fromRGB (0xc, 0xa5, 0xf2),
        ColorEx.fromRGB (0x2c, 0xc, 0xf2),
        ColorEx.fromRGB (0x6e, 0x58, 0xf2),
        ColorEx.fromRGB (0x2c, 0xa5, 0xf2),                                                                // 90
        ColorEx.fromRGB (0x4d, 0xc, 0xf2),
        ColorEx.fromRGB (0x84, 0x58, 0xf2),
        ColorEx.fromRGB (0x4d, 0xa5, 0xf2),
        ColorEx.fromRGB (0x6e, 0xc, 0xf2),
        ColorEx.fromRGB (0x9a, 0x58, 0xf2),
        ColorEx.fromRGB (0x6e, 0xa5, 0xf2),
        ColorEx.fromRGB (0x8f, 0xc, 0xf2),
        ColorEx.fromRGB (0xb0, 0x58, 0xf2),
        ColorEx.fromRGB (0x8f, 0xa5, 0xf2),
        ColorEx.fromRGB (0xb0, 0xc, 0xf2),                                                                 // 100
        ColorEx.fromRGB (0xc6, 0x58, 0xf2),
        ColorEx.fromRGB (0xb0, 0xa5, 0xf2),
        ColorEx.fromRGB (0xd1, 0xc, 0xf2),
        ColorEx.fromRGB (0xdc, 0x58, 0xf2),
        ColorEx.fromRGB (0xd1, 0xa5, 0xf2),
        ColorEx.fromRGB (0xf2, 0xc, 0xf2),
        ColorEx.fromRGB (0xf2, 0x58, 0xf2),
        ColorEx.fromRGB (0xf2, 0xa5, 0xf2),
        ColorEx.fromRGB (0xf2, 0xc, 0xd1),
        ColorEx.fromRGB (0xf2, 0x58, 0xdc),                                                                // 110
        ColorEx.fromRGB (0xf2, 0xa5, 0xe7),
        ColorEx.fromRGB (0xf2, 0xc, 0xb0),
        ColorEx.fromRGB (0xf2, 0x58, 0xc6),
        ColorEx.fromRGB (0xf2, 0xa5, 0xdc),
        ColorEx.fromRGB (0xf2, 0xc, 0x8f),
        ColorEx.fromRGB (0xf2, 0x58, 0xb0),
        ColorEx.fromRGB (0xf2, 0xa5, 0xd1),
        ColorEx.fromRGB (0xf2, 0xc, 0x6e),
        ColorEx.fromRGB (0xf2, 0x58, 0x9a),
        ColorEx.fromRGB (0xf2, 0xa5, 0xc6),                                                                // 120
        ColorEx.fromRGB (0xf2, 0xc, 0x4d),
        ColorEx.fromRGB (0xf2, 0x58, 0x84),
        ColorEx.fromRGB (0xf2, 0xa5, 0xbb),
        ColorEx.fromRGB (0xf2, 0xc, 0x2c),
        ColorEx.fromRGB (0xf2, 0x58, 0x6e),
        ColorEx.fromRGB (0xf2, 0xa5, 0xb0),
        ColorEx.fromRGB (0xf0, 0xf0, 0xf0)
    };

    public static final int                   BLACK                  = 0;
    public static final int                   WHITE                  = 127;
    public static final int                   LIGHT_GRAY             = getIndexFor (ColorEx.LIGHT_GRAY);
    public static final int                   GRAY                   = getIndexFor (ColorEx.GRAY);
    public static final int                   DARK_GRAY              = 12;
    public static final int                   RED                    = 1;
    public static final int                   DARK_RED               = getIndexFor (ColorEx.DARK_RED);
    public static final int                   GREEN                  = getIndexFor (ColorEx.GREEN);
    public static final int                   DARK_GREEN             = 51;
    public static final int                   BLUE                   = getIndexFor (ColorEx.BLUE);
    public static final int                   DARK_BLUE              = getIndexFor (ColorEx.DARK_BLUE);
    public static final int                   YELLOW                 = getIndexFor (ColorEx.YELLOW);
    public static final int                   DARK_YELLOW            = getIndexFor (ColorEx.DARK_YELLOW);
    public static final int                   ORANGE                 = 7;
    public static final int                   DARK_ORANGE            = getIndexFor (ColorEx.DARK_ORANGE);
    public static final int                   PINK                   = getIndexFor (ColorEx.PINK);
    public static final int                   ROSE                   = 3;
    public static final int                   BROWN                  = 11;
    public static final int                   DARK_BROWN             = getIndexFor (ColorEx.DARK_BROWN);
    public static final int                   MINT                   = getIndexFor (ColorEx.MINT);
    public static final int                   OLIVE                  = getIndexFor (ColorEx.OLIVE);
    public static final int                   SKY_BLUE               = getIndexFor (ColorEx.SKY_BLUE);
    public static final int                   PURPLE                 = getIndexFor (ColorEx.PURPLE);
    public static final int                   DARK_PURPLE            = getIndexFor (ColorEx.DARK_PURPLE);
    public static final int                   RED_WINE               = 124;
    public static final int                   CYAN                   = getIndexFor (ColorEx.CYAN);

    public static final String                BUTTON_STATE_SHIFT     = "BUTTON_STATE_SHIFT";
    public static final String                BUTTON_STATE_SELECT    = "BUTTON_STATE_SELECT";
    public static final String                BUTTON_STATE_SESSION   = "BUTTON_STATE_SESSION";
    public static final String                BUTTON_STATE_TRACK     = "BUTTON_STATE_TRACK";
    public static final String                BUTTON_STATE_LAYER     = "BUTTON_STATE_LAYER";
    public static final String                BUTTON_STATE_PLAY      = "BUTTON_STATE_PLAY";
    public static final String                BUTTON_STATE_STOP      = "BUTTON_STATE_STOP";
    public static final String                BUTTON_STATE_REC       = "BUTTON_STATE_REC";
    public static final String                BUTTON_STATE_OVERDUB   = "BUTTON_STATE_OVERDUB";
    public static final String                BUTTON_STATE_LOOP      = "BUTTON_STATE_LOOP";
    public static final String                BUTTON_STATE_TAP_TEMPO = "BUTTON_STATE_TAP_TEMPO";
    public static final String                BUTTON_STATE_ARROW     = "BUTTON_STATE_ARROW";

    private final Map<NoteAttribute, Integer> noteParamColors        = new EnumMap<> (NoteAttribute.class);


    /**
     * Constructor.
     */
    public YaeltexTurnColorManager (final IHost host)
    {
        this.registerColorIndex (ColorManager.BUTTON_STATE_OFF, 0);
        this.registerColorIndex (ColorManager.BUTTON_STATE_ON, 0);
        this.registerColorIndex (ColorManager.BUTTON_STATE_HI, 127);

        this.registerColorIndex (AbstractFeatureGroup.BUTTON_COLOR_OFF, BLACK);
        this.registerColorIndex (ScenePlayView.COLOR_SELECTED_PLAY_SCENE, WHITE);

        this.registerColorIndex (BUTTON_STATE_SHIFT, WHITE);
        this.registerColorIndex (BUTTON_STATE_SELECT, WHITE);
        this.registerColorIndex (BUTTON_STATE_SESSION, WHITE);
        this.registerColorIndex (BUTTON_STATE_TRACK, PURPLE);
        this.registerColorIndex (BUTTON_STATE_LAYER, DARK_YELLOW);
        this.registerColorIndex (BUTTON_STATE_PLAY, GREEN);
        this.registerColorIndex (BUTTON_STATE_STOP, GRAY);
        this.registerColorIndex (BUTTON_STATE_REC, RED);
        this.registerColorIndex (BUTTON_STATE_OVERDUB, ORANGE);
        this.registerColorIndex (BUTTON_STATE_LOOP, CYAN);
        this.registerColorIndex (BUTTON_STATE_TAP_TEMPO, WHITE);
        this.registerColorIndex (BUTTON_STATE_ARROW, BLUE);

        this.registerColorIndex (AbstractSequencerView.COLOR_STEP_HILITE_NO_CONTENT, GREEN);
        this.registerColorIndex (AbstractSequencerView.COLOR_STEP_HILITE_CONTENT, GREEN);
        this.registerColorIndex (AbstractSequencerView.COLOR_STEP_MUTED, DARK_GRAY);
        this.registerColorIndex (AbstractSequencerView.COLOR_STEP_MUTED_CONT, DARK_GRAY);
        this.registerColorIndex (AbstractSequencerView.COLOR_STEP_SELECTED, WHITE);
        this.registerColorIndex (AbstractSequencerView.COLOR_NO_CONTENT, BLACK);
        this.registerColorIndex (AbstractSequencerView.COLOR_NO_CONTENT_4, BLACK);
        this.registerColorIndex (AbstractSequencerView.COLOR_CONTENT, BLUE);
        this.registerColorIndex (AbstractSequencerView.COLOR_CONTENT_CONT, CYAN);
        this.registerColorIndex (AbstractSequencerView.COLOR_PAGE, WHITE);
        this.registerColorIndex (AbstractSequencerView.COLOR_ACTIVE_PAGE, GREEN);
        this.registerColorIndex (AbstractSequencerView.COLOR_SELECTED_PAGE, BLUE);

        this.registerColorIndex (AbstractDrumView.COLOR_PAD_OFF, BLACK);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_RECORD, RED);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_PLAY, GREEN);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_SELECTED, WHITE);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_MUTED, DARK_GRAY);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_HAS_CONTENT, YELLOW);
        this.registerColorIndex (AbstractDrumView.COLOR_PAD_NO_CONTENT, BLACK);

        this.registerColorIndex (IPadGrid.GRID_OFF, BLACK);

        for (int i = 0; i < 128; i++)
            this.registerColor (i, COLOR_TABLE[i]);

        for (final DAWColor dawColor: DAWColor.values ())
        {
            this.registerColorIndex (dawColor, getIndexFor (dawColor.getColor ()));
            final String name = dawColor.name ();
            this.registerColor (this.getColorIndex (name), DAWColor.getColorEntry (name));
        }

        this.noteParamColors.put (NoteAttribute.PITCH, Integer.valueOf (WHITE));
        this.noteParamColors.put (NoteAttribute.MUTE, Integer.valueOf (MINT));
        this.noteParamColors.put (NoteAttribute.DURATION, Integer.valueOf (BROWN));
        this.noteParamColors.put (NoteAttribute.VELOCITY, Integer.valueOf (RED));
        this.noteParamColors.put (NoteAttribute.RELEASE_VELOCITY, Integer.valueOf (CYAN));
        this.noteParamColors.put (NoteAttribute.VELOCITY_SPREAD, Integer.valueOf (GREEN));
        this.noteParamColors.put (NoteAttribute.TRANSPOSE, Integer.valueOf (WHITE));
        this.noteParamColors.put (NoteAttribute.GAIN, Integer.valueOf (OLIVE));
        this.noteParamColors.put (NoteAttribute.PANORAMA, Integer.valueOf (GRAY));
        this.noteParamColors.put (NoteAttribute.CHANCE, Integer.valueOf (BLUE));
        this.noteParamColors.put (NoteAttribute.PRESSURE, Integer.valueOf (YELLOW));
        this.noteParamColors.put (NoteAttribute.TIMBRE, Integer.valueOf (RED_WINE));
        this.noteParamColors.put (NoteAttribute.OCCURRENCE, Integer.valueOf (SKY_BLUE));
        this.noteParamColors.put (NoteAttribute.REPEAT, Integer.valueOf (ORANGE));
        this.noteParamColors.put (NoteAttribute.REPEAT_CURVE, Integer.valueOf (ROSE));
        this.noteParamColors.put (NoteAttribute.REPEAT_VELOCITY_CURVE, Integer.valueOf (PINK));
        this.noteParamColors.put (NoteAttribute.REPEAT_VELOCITY_END, Integer.valueOf (PURPLE));

        // Black out non supported note parameters
        for (final NoteAttribute noteAttribute: NoteAttribute.values ())
        {
            if (!host.supports (noteAttribute))
                this.noteParamColors.put (noteAttribute, Integer.valueOf (BLACK));
        }
    }


    /** {@inheritDoc} */
    @Override
    public ColorEx getColor (final int colorIndex, final ButtonID buttonID)
    {
        final ColorEx color;
        switch (buttonID)
        {
            case PLAY:
                color = ColorEx.GREEN;
                break;

            case SHIFT, SELECT, CLIP, SESSION, TAP_TEMPO:
                color = ColorEx.WHITE;
                break;

            case TRACK:
                color = ColorEx.PURPLE;
                break;

            case USER:
                color = ColorEx.DARK_YELLOW;
                break;

            case STOP:
                color = ColorEx.GRAY;
                break;

            case RECORD:
                color = ColorEx.RED;
                break;

            case OVERDUB:
                color = ColorEx.ORANGE;
                break;

            case LOOP:
                color = ColorEx.CYAN;
                break;

            case ARROW_DOWN, ARROW_UP, ARROW_LEFT, ARROW_RIGHT:
                color = ColorEx.BLUE;
                break;

            default:
                return super.getColor (colorIndex, buttonID);
        }

        // Little trick to identify values which need an intensity
        return colorIndex < 128 ? color : ColorEx.evenDarker (color);
    }


    /**
     * Get the index in the color palette which is closest to the given color.
     *
     * @param color The color for which to get the color index.
     * @return The index with the closest color
     */
    public static final int getIndexFor (final ColorEx color)
    {
        return ColorEx.getClosestColorIndex (color, COLOR_TABLE);
    }


    /**
     * Get the color to use for a note parameter.
     *
     * @param noteEditParameter The parameter
     * @return The index of the color
     */
    public int getParamColor (final NoteAttribute noteEditParameter)
    {
        return this.noteParamColors.get (noteEditParameter).intValue ();
    }
}