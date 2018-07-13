// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.mode;

import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.controller.push.controller.display.DisplayModel;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.AbstractMode;
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
        super (surface, model);
        this.isTemporary = false;
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnob (final int index, final int value)
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
    public void onValueKnobTouch (final int index, final boolean isTouched)
    {
        this.isKnobTouched[index] = isTouched;
    }


    /** {@inheritDoc} */
    @Override
    public void updateSecondRow ()
    {
        this.surface.updateButton (102, AbstractMode.BUTTON_COLOR_HI);
        this.surface.updateButton (103, AbstractMode.BUTTON_COLOR_ON);
        for (int i = 2; i < 8; i++)
            this.surface.updateButton (102 + i, AbstractMode.BUTTON_COLOR_OFF);
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
        final DisplayModel message = this.surface.getDisplay ().getModel ();
        message.addOptionElement ("", "Setup", true, "", "", false, true);
        message.addOptionElement ("Brightness", "Info", false, "", "", false, true);
        message.addParameterElement ("Display", config.getDisplayBrightness () * 1023 / 100, config.getDisplayBrightness () + "%", this.isKnobTouched[2], -1);
        message.addParameterElement ("LEDs", config.getLedBrightness () * 1023 / 100, config.getLedBrightness () + "%", this.isKnobTouched[3], -1);
        message.addOptionElement ("        Pads", "", false, "", "", false, false);
        message.addParameterElement ("Sensitivity", config.getPadSensitivity () * 1023 / 10, Integer.toString (config.getPadSensitivity ()), this.isKnobTouched[5], -1);
        message.addParameterElement ("Gain", config.getPadGain () * 1023 / 10, Integer.toString (config.getPadGain ()), this.isKnobTouched[6], -1);
        message.addParameterElement ("Dynamics", config.getPadDynamics () * 1023 / 10, Integer.toString (config.getPadDynamics ()), this.isKnobTouched[7], -1);
        message.send ();
    }
}