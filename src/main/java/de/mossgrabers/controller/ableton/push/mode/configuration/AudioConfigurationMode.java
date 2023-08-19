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
 * Audio configuration settings for Push 3.
 *
 * @author Jürgen Moßgraber
 */
public class AudioConfigurationMode extends AbstractConfigurationMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public AudioConfigurationMode (final PushControlSurface surface, final IModel model)
    {
        super (3, "Audio Configuration", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        final PushConfiguration config = this.surface.getConfiguration ();
        switch (index)
        {
            case 0:
                config.changePedal1 (value);
                break;
            case 1:
                config.changePedal2 (value);
                break;
            case 3:
                config.changePreamp1Type (value);
                break;
            case 4:
                config.changePreamp1Gain (value);
                break;
            case 5:
                config.changePreamp2Type (value);
                break;
            case 6:
                config.changePreamp2Gain (value);
                break;
            case 7:
                config.changeAudioOutputs (value);
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
            case 0:
                config.setPedal1 (0);
                break;
            case 1:
                config.setPedal2 (0);
                break;
            case 3:
                config.setPreamp1Type (0);
                break;
            case 4:
                config.setPreamp1Gain (0);
                break;
            case 5:
                config.setPreamp2Type (0);
                break;
            case 6:
                config.setPreamp2Gain (0);
                break;
            case 7:
                config.setAudioOutputs (0);
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
        final int useCV1 = config.getPedal1 ();
        final int useCV2 = config.getPedal2 ();
        final int preampType1 = config.getPreamp1Type ();
        final int preampType2 = config.getPreamp2Type ();
        final int preampGain1 = config.getPreamp1Gain ();
        final int preampGain2 = config.getPreamp2Gain ();
        final int outputs = config.getAudioOutputs ();

        display.addParameterElement (this.menu[0], false, "", (ChannelType) null, null, false, "Pedal&CV 1", useCV1 > 0 ? 1023 : 0, PushConfiguration.FOOT_CV_OPTIONS[useCV1], this.isKnobTouched (0), -1);
        display.addParameterElement (this.menu[1], false, "", (ChannelType) null, null, false, "Pedal&CV 2", useCV2 > 0 ? 1023 : 0, PushConfiguration.FOOT_CV_OPTIONS[useCV2], this.isKnobTouched (1), -1);
        display.addOptionElement ("   Preamp", this.menu[2], false, "", "", false, true);
        display.addParameterElement (this.menu[3], true, "", (ChannelType) null, null, false, "P1: Type", preampType1 * 1023 / (PushConfiguration.PREAMP_TYPE_OPTIONS.length - 1), PushConfiguration.PREAMP_TYPE_OPTIONS[preampType1], this.isKnobTouched (3), -1);
        display.addParameterElement (" ", false, "", (ChannelType) null, null, false, "P1: Gain", preampGain1 * 1023 / (PushConfiguration.PREAMP_GAIN_OPTIONS.length - 1), PushConfiguration.PREAMP_GAIN_OPTIONS[preampGain1], this.isKnobTouched (4), -1);
        display.addParameterElement (" ", false, "", (ChannelType) null, null, false, "P2: Type", preampType2 * 1023 / (PushConfiguration.PREAMP_TYPE_OPTIONS.length - 1), PushConfiguration.PREAMP_TYPE_OPTIONS[preampType2], this.isKnobTouched (5), -1);
        display.addParameterElement (" ", false, "", (ChannelType) null, null, false, "P2: Gain", preampGain2 * 1023 / (PushConfiguration.PREAMP_GAIN_OPTIONS.length - 1), PushConfiguration.PREAMP_GAIN_OPTIONS[preampGain2], this.isKnobTouched (6), -1);
        display.addParameterElement (" ", false, "", (ChannelType) null, null, false, "Outputs", outputs * 1023 / (PushConfiguration.OUTPUT_CONFIGURATION_SHORT.length - 1), PushConfiguration.OUTPUT_CONFIGURATION_SHORT[outputs], this.isKnobTouched (7), -1);
    }
}