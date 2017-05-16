// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.mode;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.mode.AbstractMode;
import de.mossgrabers.push.controller.DisplayMessage;
import de.mossgrabers.push.controller.PushControlSurface;
import de.mossgrabers.push.controller.PushDisplay;


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
    public InfoMode (final PushControlSurface surface, final Model model)
    {
        super (surface, model);
        this.isTemporary = false;
    }


    /** {@inheritDoc} */
    @Override
    public void onSecondRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
        if (index == 0)
            this.surface.getModeManager ().setActiveMode (Modes.MODE_SETUP);
    }


    /** {@inheritDoc} */
    @Override
    public void updateSecondRow ()
    {
        this.surface.updateButton (102, AbstractMode.BUTTON_COLOR_ON);
        this.surface.updateButton (103, AbstractMode.BUTTON_COLOR_HI);
        for (int i = 2; i < 8; i++)
            this.surface.updateButton (102 + i, AbstractMode.BUTTON_COLOR_OFF);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 ()
    {
        // Intentionally empty - mode is only for Push 2
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 ()
    {
        final DisplayMessage message = ((PushDisplay) this.surface.getDisplay ()).createMessage ();
        message.addOptionElement ("  Firmware: " + this.surface.getMajorVersion () + "." + this.surface.getMinorVersion () + " Build " + this.surface.getBuildNumber (), "Setup", false, "", "", false, true);
        message.addOptionElement ("", "Info", true, "", "", false, true);
        message.addEmptyElement ();
        message.addOptionElement ("Board Revision: " + this.surface.getBoardRevision (), "", false, "", "", false, false);
        message.addEmptyElement ();
        message.addOptionElement ("        Serial Number: " + this.surface.getSerialNumber (), "", false, "", "", false, false);
        message.addEmptyElement ();
        message.addEmptyElement ();
        message.send ();
    }
}
