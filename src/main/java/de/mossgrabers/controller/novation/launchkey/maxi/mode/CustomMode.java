// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchkey.maxi.mode;

import de.mossgrabers.controller.novation.launchkey.maxi.LaunchkeyMk3Configuration;
import de.mossgrabers.controller.novation.launchkey.maxi.controller.LaunchkeyMk3ControlSurface;
import de.mossgrabers.controller.novation.launchkey.maxi.controller.LaunchkeyMk3Display;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.featuregroup.AbstractParameterMode;


/**
 * Dummy mode for the 4 custom mapping modes. Only displays the custom mode number in the display.
 *
 * @author Jürgen Moßgraber
 */
public class CustomMode extends AbstractParameterMode<LaunchkeyMk3ControlSurface, LaunchkeyMk3Configuration, IItem>
{
    private final int number;


    /**
     * Constructor.
     *
     * @param number The number of the custom mode (1-4)
     * @param surface The control surface
     * @param model The model
     */
    public CustomMode (final int number, final LaunchkeyMk3ControlSurface surface, final IModel model)
    {
        super ("Custom" + number, surface, model);

        this.number = number;
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final ITextDisplay d = this.surface.getTextDisplay ();
        d.setCell (LaunchkeyMk3Display.SCREEN_ROW_BASE, 0, "Custom mode " + this.number);
        d.setCell (LaunchkeyMk3Display.SCREEN_ROW_BASE + 1, 0, "");
        d.allDone ();
    }
}
