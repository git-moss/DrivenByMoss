// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.mode.configuration;

import de.mossgrabers.controller.ableton.push.PushConfiguration;
import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.resource.ChannelType;


/**
 * Configuration settings for Push 2/3.
 *
 * @author Jürgen Moßgraber
 */
public class SetupMode extends AbstractConfigurationMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public SetupMode (final PushControlSurface surface, final IModel model)
    {
        super (1, "Setup", surface, model);
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
        this.setTouchedKnob (index, isTouched);

        if (!isTouched || !this.surface.isDeletePressed ())
            return;

        this.surface.setTriggerConsumed (ButtonID.DELETE);

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
    public void updateDisplay2 (final IGraphicDisplay display)
    {
        final PushConfiguration config = this.surface.getConfiguration ();
        final int displayBrightness = config.getDisplayBrightness ();
        final int ledBrightness = config.getLedBrightness ();
        final int padSensitivity = config.getPadSensitivity ();
        final int padGain = config.getPadGain ();
        final int padDynamics = config.getPadDynamics ();

        display.addOptionElement ("", this.menu[0], false, "", "", false, true);
        display.addOptionElement ("Brightness", this.menu[1], true, "", "", false, true);
        display.addParameterElement (this.menu[2], false, "", (ChannelType) null, null, false, "Display", displayBrightness * 1023 / 100, displayBrightness + "%", this.isKnobTouched (2), -1);
        display.addParameterElement (this.menu[3], false, "", (ChannelType) null, null, false, "LEDs", ledBrightness * 1023 / 100, ledBrightness + "%", this.isKnobTouched (3), -1);
        display.addOptionElement ("        Pads", " ", false, "", "", false, true);
        display.addParameterElement (" ", false, "", (ChannelType) null, null, false, "Sensitivity", padSensitivity * 1023 / 10, Integer.toString (padSensitivity), this.isKnobTouched (5), -1);
        display.addParameterElement (" ", false, "", (ChannelType) null, null, false, "Gain", padGain * 1023 / 10, Integer.toString (padGain), this.isKnobTouched (6), -1);
        display.addParameterElement (" ", false, "", (ChannelType) null, null, false, "Dynamics", padDynamics * 1023 / 10, Integer.toString (padDynamics), this.isKnobTouched (7), -1);
    }
}