// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.mode.configuration;

import de.mossgrabers.controller.ableton.push.PushConfiguration;
import de.mossgrabers.controller.ableton.push.PushVersion;
import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorEx;
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
            case 1:
                config.changeDisplayBrightness (value);
                break;
            case 2:
                config.changeLEDBrightness (value);
                break;
        }

        if (this.surface.getConfiguration ().getPushVersion () == PushVersion.VERSION_2)
        {
            switch (index)
            {
                case 4:
                    config.changePadSensitivity (value);
                    break;
                case 5:
                    config.changePadGain (value);
                    break;
                case 6:
                    config.changePadDynamics (value);
                    break;
                default:
                    // Not used
                    break;
            }
        }
        else
        {
            switch (index)
            {
                case 4:
                    config.changePadCurveThresholdPush3 (value);
                    break;
                case 5:
                    config.changePadCurveDrivePush3 (value);
                    break;
                case 6:
                    config.changePadCurveCompandPush3 (value);
                    break;
                case 7:
                    config.changePadCurveRangePush3 (value);
                    break;
                default:
                    // Not used
                    break;
            }
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
            case 1:
                config.setDisplayBrightness (100);
                return;
            case 2:
                config.setLEDBrightness (100);
                return;
        }

        if (this.surface.getConfiguration ().getPushVersion () == PushVersion.VERSION_2)
        {
            switch (index)
            {
                case 4:
                    config.setPadSensitivityPush2 (5);
                    break;
                case 5:
                    config.setPadGainPush2 (5);
                    break;
                case 6:
                    config.setPadDynamicsPush2 (5);
                    break;
                default:
                    // Not used
                    break;
            }
        }
        else
        {
            switch (index)
            {
                case 4:
                    config.setPadCurveThresholdPush3 (7);
                    break;
                case 5:
                    config.setPadCurveDrivePush3 (4);
                    break;
                case 6:
                    config.setPadCurveCompandPush3 (-26);
                    break;
                case 7:
                    config.setPadCurveRangePush3 (35);
                    break;
                default:
                    // Not used
                    break;
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 (final IGraphicDisplay display)
    {
        final PushConfiguration config = this.surface.getConfiguration ();
        final int displayBrightness = config.getDisplayBrightness ();
        final int ledBrightness = config.getLedBrightness ();

        display.addOptionElement ("Brightness", this.menu[0], false, "", "", false, true);
        display.addParameterElement (this.menu[1], true, "", (ChannelType) null, null, false, "Display", displayBrightness * 1023 / 100, displayBrightness + "%", this.isKnobTouched (1), -1);
        display.addParameterElement (this.menu[2], false, "", (ChannelType) null, null, false, "LEDs", ledBrightness * 1023 / 100, ledBrightness + "%", this.isKnobTouched (2), -1);
        display.addOptionElement ("        Pads", this.menu[3], false, "", "", false, true);

        if (this.surface.getConfiguration ().getPushVersion () == PushVersion.VERSION_2)
        {
            final int padSensitivity = config.getPadSensitivityPush2 ();
            final int padGain = config.getPadGainPush2 ();
            final int padDynamics = config.getPadDynamicsPush2 ();
            display.addParameterElement (" ", false, "", (ChannelType) null, null, false, "Sensitivity", padSensitivity * 1023 / 10, Integer.toString (padSensitivity), this.isKnobTouched (4), -1);
            display.addParameterElement (" ", false, "", (ChannelType) null, null, false, "Gain", padGain * 1023 / 10, Integer.toString (padGain), this.isKnobTouched (5), -1);
            display.addParameterElement (" ", false, "", (ChannelType) null, null, false, "Dynamics", padDynamics * 1023 / 10, Integer.toString (padDynamics), this.isKnobTouched (6), -1);
            display.addOptionElement ("", " ", false, "", "", false, true);
        }
        else
        {
            final int padThreshold = config.getPadCurveThresholdPush3 ();
            final int padDrive = config.getPadCurveDrivePush3 ();
            final int padCompand = config.getPadCurveCompandPush3 ();
            final int padRange = config.getPadCurveRangePush3 ();
            display.addParameterElement (" ", false, "", (ChannelType) null, null, false, "Threshold", padThreshold * 1023 / 100, Integer.toString (padThreshold), this.isKnobTouched (4), -1);
            display.addParameterElement (" ", false, "", (ChannelType) null, null, false, "Drive", (padDrive + 50) * 1023 / 100, Integer.toString (padDrive), this.isKnobTouched (5), -1);
            display.addParameterElement (" ", false, "", (ChannelType) null, null, false, "Compand", (padCompand + 50) * 1023 / 100, Integer.toString (padCompand), this.isKnobTouched (6), -1);
            display.addParameterElement (" ", false, "", (ChannelType) null, null, false, "Range", padRange * 1023 / 100, Integer.toString (padRange), this.isKnobTouched (7), -1);
        }

        if (config.getPushVersion () == PushVersion.VERSION_2)
            display.addGraphOverlay (380, 84, 90, 34, ColorEx.WHITE, this.surface.createPadSensitivityCurvePush2 (), 128);
        else
            display.addGraphOverlay (380, 84, 90, 34, ColorEx.WHITE, this.surface.createPadSensitivityCurvePush3 (), 128);
    }
}