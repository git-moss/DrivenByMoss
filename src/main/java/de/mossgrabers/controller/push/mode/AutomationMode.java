// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.mode;

import de.mossgrabers.controller.push.controller.Push1Display;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.constants.TransportConstants;
import de.mossgrabers.framework.mode.AbstractMode;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Editing of the automation mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class AutomationMode extends BaseMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public AutomationMode (final PushControlSurface surface, final IModel model)
    {
        super ("Automation", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 (final ITextDisplay display)
    {
        final String writeMode = this.model.getTransport ().getAutomationWriteMode ();
        display.setBlock (1, 0, "Automation Mode:");
        for (int i = 0; i < TransportConstants.AUTOMATION_MODES.size (); i++)
            display.setCell (3, i, (TransportConstants.AUTOMATION_MODES_VALUES[i].equals (writeMode) ? Push1Display.SELECT_ARROW : "") + TransportConstants.AUTOMATION_MODES.get (i));
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 (final IGraphicDisplay display)
    {
        final String writeMode = this.model.getTransport ().getAutomationWriteMode ();
        for (int i = 0; i < 8; i++)
            display.addOptionElement ("", "", false, i == 0 ? "Automation Mode" : "", i < TransportConstants.AUTOMATION_MODES.size () ? TransportConstants.AUTOMATION_MODES.get (i) : "", i < TransportConstants.AUTOMATION_MODES.size () && TransportConstants.AUTOMATION_MODES_VALUES[i].equals (writeMode), false);
    }


    /** {@inheritDoc} */
    @Override
    public void onFirstRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;
        if (index < TransportConstants.AUTOMATION_MODES_VALUES.length)
            this.model.getTransport ().setAutomationWriteMode (TransportConstants.AUTOMATION_MODES_VALUES[index]);
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        final int index = this.isButtonRow (0, buttonID);
        if (index >= 0)
        {
            final String writeMode = this.model.getTransport ().getAutomationWriteMode ();
            if (index < TransportConstants.AUTOMATION_MODES_VALUES.length)
                return this.colorManager.getColorIndex (TransportConstants.AUTOMATION_MODES_VALUES[index].equals (writeMode) ? AbstractMode.BUTTON_COLOR_HI : AbstractMode.BUTTON_COLOR_ON);
        }

        return super.getButtonColor (buttonID);
    }
}