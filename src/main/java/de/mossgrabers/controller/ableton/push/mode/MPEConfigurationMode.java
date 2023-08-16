// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.mode;

import de.mossgrabers.controller.ableton.push.PushConfiguration;
import de.mossgrabers.controller.ableton.push.PushVersion;
import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.constants.Capability;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.daw.resource.ChannelType;
import de.mossgrabers.framework.featuregroup.AbstractFeatureGroup;
import de.mossgrabers.framework.featuregroup.AbstractMode;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * MPE configuration settings for Push 3.
 *
 * @author Jürgen Moßgraber
 */
public class MPEConfigurationMode extends BaseMode<IItem>
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public MPEConfigurationMode (final PushControlSurface surface, final IModel model)
    {
        super ("MPE Configuration", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        final PushConfiguration config = this.surface.getConfiguration ();
        switch (index)
        {
            case 1:
                config.changeMPEEnabled (value);
                break;
            case 2:
                config.changeMPEPitchbendRange (value);
                break;
            case 3:
                config.changePerPadPitchbendEnabled (value);
                break;
            case 4:
                config.changeInTuneLocation (value);
                break;
            case 5:
                config.changeInTuneWidth (value);
                break;
            case 6:
                config.changeSlideHeight (value);
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
            case 1:
                config.setMPEEnabled (true);
                break;
            case 2:
                config.setMPEPitchbendRange (48);
                break;
            case 3:
                config.setPerPadPitchbendEnabled (true);
                break;
            case 4:
                config.setInTuneLocation (1);
                break;
            case 5:
                config.setInTuneWidth (9);
                break;
            case 6:
                config.setSlideHeight (3);
                break;
            default:
                // Not used
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public String getButtonColorID (final ButtonID buttonID)
    {
        final int index = this.isButtonRow (1, buttonID);
        if (index >= 0)
        {
            if (index == 0 || index == 2)
                return AbstractFeatureGroup.BUTTON_COLOR_ON;
            if (index == 1)
                return AbstractMode.BUTTON_COLOR_HI;
        }

        return AbstractFeatureGroup.BUTTON_COLOR_OFF;
    }


    /** {@inheritDoc} */
    @Override
    public void onSecondRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;
        if (index == 0)
            this.surface.getModeManager ().setTemporary (Modes.SETUP);
        else if (index == 2)
            this.surface.getModeManager ().setTemporary (Modes.INFO);
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
        display.addOptionElement ("", "Setup", false, "", "", false, true);

        final PushConfiguration config = this.surface.getConfiguration ();
        if (config.getPushVersion () == PushVersion.VERSION_2)
        {
            display.addOptionElement ("Not available on Push 2.", "MPE", true, "", "", false, true);
            display.addOptionElement ("", "Info", false, "", "", false, true);
            display.addOptionElement ("", " ", false, "", "", false, true);
            display.addOptionElement ("", " ", false, "", "", false, true);
            display.addOptionElement ("", " ", false, "", "", false, true);
            display.addOptionElement ("", " ", false, "", "", false, true);
            display.addOptionElement ("", " ", false, "", "", false, true);
            return;
        }

        final boolean isMPEEnabled = config.isMPEEnabled ();
        final int mpePitchBendRange = config.getMPEPitchBendRange ();
        final boolean isPerPadPitchbendEnabled = config.isPerPadPitchbend ();
        final int inTuneLocation = config.getInTuneLocation ();
        final int inTuneWidth = config.getInTuneWidth ();
        final int slideHeight = config.getInTuneSlideHeight ();

        display.addParameterElement ("MPE", true, "", (ChannelType) null, null, false, "MPE", isMPEEnabled ? 1023 : 0, isMPEEnabled ? "On" : "Off", this.isKnobTouched (1), -1);
        if (this.surface.getHost ().supports (Capability.MPE_PITCH_RANGE))
            display.addParameterElement ("Info", false, "", (ChannelType) null, null, false, "PB Range", mpePitchBendRange * 1023 / 96, Integer.toString (mpePitchBendRange), this.isKnobTouched (2), -1);
        else
            display.addOptionElement ("", "Info", false, "", "", false, true);
        display.addParameterElement (" ", false, "", (ChannelType) null, null, false, "Per-Pad PB", isPerPadPitchbendEnabled ? 1023 : 0, isPerPadPitchbendEnabled ? "On" : "Off", this.isKnobTouched (3), -1);
        display.addParameterElement (" ", false, "", (ChannelType) null, null, false, "Location", inTuneLocation == 0 ? 0 : 1023, PushConfiguration.IN_TUNE_LOCATION_OPTIONS[inTuneLocation], this.isKnobTouched (4), -1);
        display.addParameterElement (" ", false, "", (ChannelType) null, null, false, "Width (mm)", inTuneWidth * 1023 / (PushConfiguration.IN_TUNE_WIDTH_OPTIONS.length - 1), PushConfiguration.IN_TUNE_WIDTH_OPTIONS[inTuneWidth], this.isKnobTouched (5), -1);
        display.addParameterElement (" ", false, "", (ChannelType) null, null, false, "Height (mm)", slideHeight * 1023 / (PushConfiguration.SLIDE_HEIGHT_OPTIONS.length - 1), PushConfiguration.SLIDE_HEIGHT_OPTIONS[slideHeight], this.isKnobTouched (6), -1);
        display.addOptionElement ("", " ", false, "", "", false, true);
    }
}