// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.mcu.controller;

import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.featuregroup.AbstractFeatureGroup;
import de.mossgrabers.framework.featuregroup.AbstractMode;


/**
 * Color states to use for the MCU buttons.
 *
 * @author Jürgen Moßgraber
 */
public class MCUColorManager extends ColorManager
{
    /**
     * Private due to utility class.
     */
    public MCUColorManager ()
    {
        this.registerColorIndex (AbstractFeatureGroup.BUTTON_COLOR_OFF, 0);
        this.registerColorIndex (AbstractFeatureGroup.BUTTON_COLOR_ON, 0);
        this.registerColorIndex (AbstractMode.BUTTON_COLOR_HI, 127);

        this.registerColorIndex (ColorManager.BUTTON_STATE_OFF, 0);
        this.registerColorIndex (ColorManager.BUTTON_STATE_ON, 0);
        this.registerColorIndex (ColorManager.BUTTON_STATE_HI, 127);
    }


    /** {@inheritDoc} */
    @Override
    public ColorEx getColor (final int colorIndex, final ButtonID buttonID)
    {
        if (colorIndex <= 0)
            return ColorEx.BLACK;

        if (buttonID == null)
            return ColorEx.RED;

        switch (buttonID)
        {
            case PLAY:
                return ColorEx.GREEN;
            case STOP:
                return ColorEx.GRAY;
            case RECORD:
                return ColorEx.RED;
            case LOOP:
                return ColorEx.YELLOW;
            case REWIND:
            case FORWARD:
            case ZOOM:
                return ColorEx.BLUE;

            case ROW3_1:
            case ROW3_2:
            case ROW3_3:
            case ROW3_4:
            case ROW3_5:
            case ROW3_6:
            case ROW3_7:
            case ROW3_8:
                return ColorEx.GREEN;

            case ROW4_1:
            case ROW4_2:
            case ROW4_3:
            case ROW4_4:
            case ROW4_5:
            case ROW4_6:
            case ROW4_7:
            case ROW4_8:
                return ColorEx.BLUE;

            default:
                // Fall through
                break;
        }

        return ColorEx.RED;
    }
}