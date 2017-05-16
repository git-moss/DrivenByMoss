// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.mode;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.mode.AbstractMode;
import de.mossgrabers.push.controller.DisplayMessage;
import de.mossgrabers.push.controller.PushControlSurface;
import de.mossgrabers.push.controller.PushDisplay;


/**
 * Editing of the automation mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class AutomationMode extends BaseMode
{
    private static final String [] MODES        =
    {
            "Latch",
            "Touch",
            "Write"
    };
    private static final String [] MODES_VALUES =
    {
            "latch",
            "touch",
            "write"
    };


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public AutomationMode (final PushControlSurface surface, final Model model)
    {
        super (surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 ()
    {
        final Display d = this.surface.getDisplay ();
        final String writeMode = this.model.getTransport ().getAutomationWriteMode ();
        d.clear ().setBlock (1, 0, "Automation Mode:");
        for (int i = 0; i < AutomationMode.MODES.length; i++)
            d.setCell (3, i, (writeMode == AutomationMode.MODES_VALUES[i] ? PushDisplay.RIGHT_ARROW : "") + AutomationMode.MODES[i]);
        d.allDone ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 ()
    {
        final String writeMode = this.model.getTransport ().getAutomationWriteMode ();
        final DisplayMessage message = ((PushDisplay) this.surface.getDisplay ()).createMessage ();
        for (int i = 0; i < 8; i++)
            message.addOptionElement ("", "", false, i == 0 ? "Automation Mode" : "", i < AutomationMode.MODES.length ? AutomationMode.MODES[i] : "", i < AutomationMode.MODES.length && writeMode == AutomationMode.MODES_VALUES[i], false);
        message.send ();
    }


    /** {@inheritDoc} */
    @Override
    public void onFirstRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;
        if (index < AutomationMode.MODES_VALUES.length)
            this.model.getTransport ().setAutomationWriteMode (AutomationMode.MODES_VALUES[index]);
    }


    /** {@inheritDoc} */
    @Override
    public void updateFirstRow ()
    {
        final String writeMode = this.model.getTransport ().getAutomationWriteMode ();

        final ColorManager colorManager = this.model.getColorManager ();

        for (int i = 0; i < AutomationMode.MODES_VALUES.length; i++)
            this.surface.updateButton (20 + i, colorManager.getColor (writeMode == AutomationMode.MODES_VALUES[i] ? AbstractMode.BUTTON_COLOR_HI : AbstractMode.BUTTON_COLOR_ON));
        for (int i = AutomationMode.MODES_VALUES.length; i < 8; i++)
            this.surface.updateButton (20 + i, colorManager.getColor (AbstractMode.BUTTON_COLOR_OFF));
    }
}