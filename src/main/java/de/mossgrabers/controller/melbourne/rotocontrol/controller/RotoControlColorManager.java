// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.melbourne.rotocontrol.controller;

import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.featuregroup.AbstractFeatureGroup;
import de.mossgrabers.framework.featuregroup.AbstractMode;


/**
 * Different colors to use for the buttons of the ROTO CONTROL.
 *
 * @author Jürgen Moßgraber
 */
public class RotoControlColorManager extends ColorManager
{
    // @formatter:off
    /** The default color palette from Ableton. */
    public static final ColorEx [] DEFAULT_PALETTE =
    {
        ColorEx.fromRGB ( 0xFD, 0x95, 0xA7 ), ColorEx.fromRGB ( 0xFD, 0xA4, 0x3A ), ColorEx.fromRGB ( 0xCB, 0x98, 0x34 ), ColorEx.fromRGB ( 0xF7, 0xF3, 0x84 ), ColorEx.fromRGB ( 0xC0, 0xF9, 0x32 ), ColorEx.fromRGB ( 0x32, 0xFD, 0x42 ), ColorEx.fromRGB ( 0x39, 0xFD, 0xAA ), ColorEx.fromRGB ( 0x65, 0xFE, 0xE8 ), ColorEx.fromRGB ( 0x8D, 0xC6, 0xFD ), ColorEx.fromRGB ( 0x56, 0x82, 0xE1 ), ColorEx.fromRGB ( 0x93, 0xA9, 0xFC ), ColorEx.fromRGB ( 0xD6, 0x70, 0xE2 ), ColorEx.fromRGB ( 0xE3, 0x56, 0x9F ), ColorEx.fromRGB ( 0xFF, 0xFF, 0xFF ),
        ColorEx.fromRGB ( 0xFC, 0x39, 0x3D ), ColorEx.fromRGB ( 0xF4, 0x6C, 0x20 ), ColorEx.fromRGB ( 0x98, 0x71, 0x4E ), ColorEx.fromRGB ( 0xFE, 0xEE, 0x4A ), ColorEx.fromRGB ( 0x8B, 0xFD, 0x70 ), ColorEx.fromRGB ( 0x44, 0xC1, 0x21 ), ColorEx.fromRGB ( 0x1E, 0xBE, 0xAF ), ColorEx.fromRGB ( 0x31, 0xE9, 0xFD ), ColorEx.fromRGB ( 0x22, 0xA5, 0xEB ), ColorEx.fromRGB ( 0x12, 0x7E, 0xBE ), ColorEx.fromRGB ( 0x88, 0x70, 0xE1 ), ColorEx.fromRGB ( 0xB5, 0x79, 0xC4 ), ColorEx.fromRGB ( 0xFD, 0x42, 0xD2 ), ColorEx.fromRGB ( 0xD0, 0xD0, 0xD0 ),
        ColorEx.fromRGB ( 0xE0, 0x68, 0x5D ), ColorEx.fromRGB ( 0xFD, 0xA3, 0x78 ), ColorEx.fromRGB ( 0xD2, 0xAC, 0x75 ), ColorEx.fromRGB ( 0xED, 0xFE, 0xB2 ), ColorEx.fromRGB ( 0xD2, 0xE3, 0x9C ), ColorEx.fromRGB ( 0xBA, 0xCF, 0x79 ), ColorEx.fromRGB ( 0x9C, 0xC3, 0x8F ), ColorEx.fromRGB ( 0xD5, 0xFD, 0xE2 ), ColorEx.fromRGB ( 0xCE, 0xF1, 0xF8 ), ColorEx.fromRGB ( 0xB9, 0xC2, 0xE2 ), ColorEx.fromRGB ( 0xCD, 0xBC, 0xE3 ), ColorEx.fromRGB ( 0xAE, 0x9A, 0xE3 ), ColorEx.fromRGB ( 0xE5, 0xDC, 0xE1 ), ColorEx.fromRGB ( 0xA9, 0xA9, 0xA9 ),
        ColorEx.fromRGB ( 0xC5, 0x92, 0x8C ), ColorEx.fromRGB ( 0xB6, 0x82, 0x59 ), ColorEx.fromRGB ( 0x98, 0x83, 0x6B ), ColorEx.fromRGB ( 0xBF, 0xB9, 0x6E ), ColorEx.fromRGB ( 0xA6, 0xBC, 0x25 ), ColorEx.fromRGB ( 0x7E, 0xAF, 0x52 ), ColorEx.fromRGB ( 0x8A, 0xC2, 0xBA ), ColorEx.fromRGB ( 0x9C, 0xB3, 0xC3 ), ColorEx.fromRGB ( 0x86, 0xA5, 0xC1 ), ColorEx.fromRGB ( 0x84, 0x94, 0xCA ), ColorEx.fromRGB ( 0xA5, 0x96, 0xB4 ), ColorEx.fromRGB ( 0xBE, 0xA0, 0xBD ), ColorEx.fromRGB ( 0xBB, 0x72, 0x96 ), ColorEx.fromRGB ( 0x7B, 0x7B, 0x7B ),
        ColorEx.fromRGB ( 0xAD, 0x34, 0x36 ), ColorEx.fromRGB ( 0xA7, 0x51, 0x35 ), ColorEx.fromRGB ( 0x71, 0x4F, 0x42 ), ColorEx.fromRGB ( 0xDA, 0xC2, 0x29 ), ColorEx.fromRGB ( 0x85, 0x95, 0x2B ), ColorEx.fromRGB ( 0x55, 0x9E, 0x38 ), ColorEx.fromRGB ( 0x1B, 0x9B, 0x8E ), ColorEx.fromRGB ( 0x26, 0x63, 0x83 ), ColorEx.fromRGB ( 0x1B, 0x33, 0x93 ), ColorEx.fromRGB ( 0x31, 0x54, 0xA0 ), ColorEx.fromRGB ( 0x62, 0x4E, 0xAB ), ColorEx.fromRGB ( 0xA2, 0x4E, 0xAB ), ColorEx.fromRGB ( 0xCA, 0x32, 0x6E ), ColorEx.fromRGB ( 0x3C, 0x3C, 0x3C )
    };
    // @formatter:on


    /**
     * Constructor.
     */
    public RotoControlColorManager ()
    {
        this.registerColorIndex (AbstractFeatureGroup.BUTTON_COLOR_OFF, 0);
        this.registerColorIndex (AbstractFeatureGroup.BUTTON_COLOR_ON, 127);
        this.registerColorIndex (AbstractMode.BUTTON_COLOR_HI, 127);

        this.registerColorIndex (ColorManager.BUTTON_STATE_OFF, 0);
        this.registerColorIndex (ColorManager.BUTTON_STATE_ON, 127);
        this.registerColorIndex (ColorManager.BUTTON_STATE_HI, 127);
    }


    /** {@inheritDoc} */
    @Override
    public ColorEx getColor (final int colorIndex, final ButtonID buttonID)
    {
        if (buttonID == null)
            return ColorEx.BLACK;

        switch (buttonID)
        {
            case PLAY:
                return colorIndex > 0 ? ColorEx.GREEN : ColorEx.DARK_GREEN;
            case RECORD:
                return colorIndex > 0 ? ColorEx.RED : ColorEx.DARK_RED;
            case SOLO:
                return colorIndex > 0 ? ColorEx.BLUE : ColorEx.DARK_BLUE;
            case ROW_SELECT_1:
            case ROW_SELECT_2:
            case ROW_SELECT_3:
            case ROW_SELECT_4:
            case ROW_SELECT_5:
            case ROW_SELECT_6:
            case ROW_SELECT_7:
            case ROW_SELECT_8:
                return colorIndex > 0 ? ColorEx.GRAY : ColorEx.BLACK;
            default:
                return colorIndex > 0 ? ColorEx.WHITE : ColorEx.DARK_GRAY;
        }
    }
}