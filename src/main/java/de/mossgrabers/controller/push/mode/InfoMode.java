// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.mode;

import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.AbstractMode;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Configuration settings for Push 1.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class InfoMode extends BaseMode
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
        this.isTemporary = false;
    }


    /** {@inheritDoc} */
    @Override
    public void onSecondRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
        if (index == 0)
            this.surface.getModeManager ().setActiveMode (Modes.SETUP);
    }


    /** {@inheritDoc} */
    @Override
    public void updateSecondRow ()
    {
        this.surface.updateTrigger (102, AbstractMode.BUTTON_COLOR_ON);
        this.surface.updateTrigger (103, AbstractMode.BUTTON_COLOR_HI);
        for (int i = 2; i < 8; i++)
            this.surface.updateTrigger (102 + i, AbstractMode.BUTTON_COLOR_OFF);
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
        display.addEmptyElement ();
        display.addOptionElement ("Board Revision: " + this.surface.getBoardRevision (), "", false, "", "", false, false);
        display.addEmptyElement ();
        display.addOptionElement ("        Serial Number: " + this.surface.getSerialNumber (), "", false, "", "", false, false);
        display.addEmptyElement ();
        display.addEmptyElement ();
    }
}
