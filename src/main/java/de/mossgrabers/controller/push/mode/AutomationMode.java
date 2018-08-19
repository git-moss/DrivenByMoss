// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.mode;

import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.controller.push.controller.PushDisplay;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.graphics.display.DisplayModel;
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
        super (surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 ()
    {
        final Display d = this.surface.getDisplay ();
        final String writeMode = this.model.getTransport ().getAutomationWriteMode ();
        d.clear ().setBlock (1, 0, "Automation Mode:");
        for (int i = 0; i < ITransport.AUTOMATION_MODES.length; i++)
            d.setCell (3, i, (ITransport.AUTOMATION_MODES_VALUES[i].equals (writeMode) ? PushDisplay.SELECT_ARROW : "") + ITransport.AUTOMATION_MODES[i]);
        d.allDone ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 ()
    {
        final String writeMode = this.model.getTransport ().getAutomationWriteMode ();
        final DisplayModel message = this.surface.getDisplay ().getModel ();
        for (int i = 0; i < 8; i++)
            message.addOptionElement ("", "", false, i == 0 ? "Automation Mode" : "", i < ITransport.AUTOMATION_MODES.length ? ITransport.AUTOMATION_MODES[i] : "", i < ITransport.AUTOMATION_MODES.length && ITransport.AUTOMATION_MODES_VALUES[i].equals (writeMode), false);
        message.send ();
    }


    /** {@inheritDoc} */
    @Override
    public void onFirstRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;
        if (index < ITransport.AUTOMATION_MODES_VALUES.length)
            this.model.getTransport ().setAutomationWriteMode (ITransport.AUTOMATION_MODES_VALUES[index]);
    }


    /** {@inheritDoc} */
    @Override
    public void updateFirstRow ()
    {
        final String writeMode = this.model.getTransport ().getAutomationWriteMode ();

        final ColorManager colorManager = this.model.getColorManager ();

        for (int i = 0; i < ITransport.AUTOMATION_MODES_VALUES.length; i++)
            this.surface.updateButton (20 + i, colorManager.getColor (ITransport.AUTOMATION_MODES_VALUES[i].equals (writeMode) ? AbstractMode.BUTTON_COLOR_HI : AbstractMode.BUTTON_COLOR_ON));
        for (int i = ITransport.AUTOMATION_MODES_VALUES.length; i < 8; i++)
            this.surface.updateButton (20 + i, colorManager.getColor (AbstractMode.BUTTON_COLOR_OFF));
    }
}