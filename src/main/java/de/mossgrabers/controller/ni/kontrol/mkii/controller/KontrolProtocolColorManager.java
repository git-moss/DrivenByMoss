// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.kontrol.mkii.controller;

import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.featuregroup.AbstractFeatureGroup;
import de.mossgrabers.framework.featuregroup.AbstractMode;


/**
 * Different colors to use for the buttons of Komplete Kontrol MkII.
 *
 * @author Jürgen Moßgraber
 */
public class KontrolProtocolColorManager extends ColorManager
{
    /**
     * Constructor.
     */
    public KontrolProtocolColorManager ()
    {
        this.registerColorIndex (AbstractFeatureGroup.BUTTON_COLOR_OFF, 0);
        this.registerColorIndex (AbstractFeatureGroup.BUTTON_COLOR_ON, 0);
        this.registerColorIndex (AbstractMode.BUTTON_COLOR_HI, 1);

        this.registerColorIndex (ColorManager.BUTTON_STATE_OFF, 0);
        this.registerColorIndex (ColorManager.BUTTON_STATE_ON, 0);
        this.registerColorIndex (ColorManager.BUTTON_STATE_HI, 1);
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
            case REC_ARM:
            case ROW3_1:
            case ROW3_2:
            case ROW3_3:
            case ROW3_4:
            case ROW3_5:
            case ROW3_6:
            case ROW3_7:
            case ROW3_8:
                return colorIndex > 0 ? ColorEx.RED : ColorEx.DARK_RED;
            case SOLO:
            case ROW2_1:
            case ROW2_2:
            case ROW2_3:
            case ROW2_4:
            case ROW2_5:
            case ROW2_6:
            case ROW2_7:
            case ROW2_8:
                return colorIndex > 0 ? ColorEx.BLUE : ColorEx.DARK_BLUE;
            case MUTE:
            case ROW1_1:
            case ROW1_2:
            case ROW1_3:
            case ROW1_4:
            case ROW1_5:
            case ROW1_6:
            case ROW1_7:
            case ROW1_8:
                return colorIndex > 0 ? ColorEx.ORANGE : ColorEx.DARK_ORANGE;
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