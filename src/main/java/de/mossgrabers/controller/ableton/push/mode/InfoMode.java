// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.mode;

import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.featuregroup.AbstractFeatureGroup;
import de.mossgrabers.framework.featuregroup.AbstractMode;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Configuration settings for Push 1.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class InfoMode extends BaseMode<IItem>
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public InfoMode (final PushControlSurface surface, final IModel model)
    {
        super ("Info", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onSecondRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
        if (index == 0)
            this.surface.getModeManager ().setTemporary (Modes.SETUP);
    }


    /** {@inheritDoc} */
    @Override
    public String getButtonColorID (final ButtonID buttonID)
    {
        final int index = this.isButtonRow (1, buttonID);
        if (index >= 0)
        {
            if (index == 0)
                return AbstractFeatureGroup.BUTTON_COLOR_ON;
            if (index == 1)
                return AbstractMode.BUTTON_COLOR_HI;
        }
        return AbstractFeatureGroup.BUTTON_COLOR_OFF;
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 (final ITextDisplay display)
    {
        // Intentionally empty - mode is only for Push 2
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 (final IGraphicDisplay display)
    {
        display.addOptionElement ("  Firmware: " + this.surface.getMajorVersion () + "." + this.surface.getMinorVersion () + " Build " + this.surface.getBuildNumber (), "Setup", false, "", "", false, true);
        display.addOptionElement ("", "Info", true, "", "", false, true);
        display.addEmptyElement (true);
        display.addOptionElement ("Board Revision: " + this.surface.getBoardRevision (), " ", false, "", "", false, true);
        display.addEmptyElement (true);
        display.addOptionElement ("        Serial Number: " + this.surface.getSerialNumber (), " ", false, "", "", false, true);
        display.addEmptyElement (true);
        display.addEmptyElement (true);
    }
}
