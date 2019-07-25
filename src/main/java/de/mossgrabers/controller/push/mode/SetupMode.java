// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.mode;

import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.graphics.display.DisplayModel;
import de.mossgrabers.framework.mode.AbstractMode;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Configuration settings for Push 2.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SetupMode extends BaseMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public SetupMode (final PushControlSurface surface, final IModel model)
    {
        super ("Setup", surface, model);
        this.isTemporary = false;
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        final PushConfiguration config = this.surface.getConfiguration ();
        switch (index)
        {
            case 2:
                config.changeDisplayBrightness (value);
                break;
            case 3:
                config.changeLEDBrightness (value);
                break;
            case 5:
                config.changePadSensitivity (value);
                break;
            case 6:
                config.changePadGain (value);
                break;
            case 7:
                config.changePadDynamics (value);
                break;
            default:
                // Not used
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        this.isKnobTouched[index] = isTouched;

        if (!isTouched || !this.surface.isDeletePressed ())
            return;

        this.surface.setTriggerConsumed (this.surface.getDeleteTriggerId ());

        final PushConfiguration config = this.surface.getConfiguration ();
        switch (index)
        {
            case 2:
                config.setDisplayBrightness (100);
                break;
            case 3:
                config.setLEDBrightness (100);
                break;
            case 5:
                config.setPadSensitivity (5);
                break;
            case 6:
                config.setPadGain (5);
                break;
            case 7:
                config.setPadDynamics (5);
                break;
            default:
                // Not used
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateSecondRow ()
    {
        this.surface.updateTrigger (102, AbstractMode.BUTTON_COLOR_HI);
        this.surface.updateTrigger (103, AbstractMode.BUTTON_COLOR_ON);
        for (int i = 2; i < 8; i++)
            this.surface.updateTrigger (102 + i, AbstractMode.BUTTON_COLOR_OFF);
    }


    /** {@inheritDoc} */
    @Override
    public void onSecondRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;
        if (index == 1)
            this.surface.getModeManager ().setActiveMode (Modes.MODE_INFO);
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
        final PushConfiguration config = this.surface.getConfiguration ();
        final int displayBrightness = config.getDisplayBrightness ();
        final int ledBrightness = config.getLedBrightness ();
        final int padSensitivity = config.getPadSensitivity ();
        final int padGain = config.getPadGain ();
        final int padDynamics = config.getPadDynamics ();

        final DisplayModel message = this.surface.getDisplay ().getModel ();
        message.addOptionElement ("", "Setup", true, "", "", false, true);
        message.addOptionElement ("Brightness", "Info", false, "", "", false, true);
        message.addParameterElement ("Display", displayBrightness * 1023 / 100, displayBrightness + "%", this.isKnobTouched[2], -1);
        message.addParameterElement ("LEDs", ledBrightness * 1023 / 100, ledBrightness + "%", this.isKnobTouched[3], -1);
        message.addOptionElement ("        Pads", "", false, "", "", false, false);
        message.addParameterElement ("Sensitivity", padSensitivity * 1023 / 10, Integer.toString (padSensitivity), this.isKnobTouched[5], -1);
        message.addParameterElement ("Gain", padGain * 1023 / 10, Integer.toString (padGain), this.isKnobTouched[6], -1);
        message.addParameterElement ("Dynamics", padDynamics * 1023 / 10, Integer.toString (padDynamics), this.isKnobTouched[7], -1);
        message.send ();
    }
}