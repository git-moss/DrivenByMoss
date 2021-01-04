// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.mode;

import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.display.Format;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.daw.IStepInfo;
import de.mossgrabers.framework.daw.constants.Capability;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Editing of note parameters.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class NoteMode extends BaseMode
{
    private final IHost host;

    private INoteClip   clip    = null;
    private int         channel = 0;
    private int         step    = 0;
    private int         note    = 60;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public NoteMode (final PushControlSurface surface, final IModel model)
    {
        super ("Note", surface, model);

        this.host = this.model.getHost ();
    }


    /**
     * Set the values.
     *
     * @param clip The clip to edit
     * @param channel The MIDI channel
     * @param step The step to edit
     * @param note The note to edit
     */
    public void setValues (final INoteClip clip, final int channel, final int step, final int note)
    {
        this.clip = clip;
        this.channel = channel;
        this.step = step;
        this.note = note;
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        if (this.clip == null)
            return;

        if (isTouched && this.surface.isDeletePressed ())
        {
            this.surface.setTriggerConsumed (ButtonID.DELETE);
            switch (index)
            {
                case 0:
                    this.clip.updateStepDuration (this.channel, this.step, this.note, 1.0);
                    break;

                case 1:
                    this.clip.updateStepVelocity (this.channel, this.step, this.note, 1.0);
                    break;

                case 2:
                    if (this.host.supports (Capability.NOTE_EDIT_RELEASE_VELOCITY))
                        this.clip.updateStepReleaseVelocity (this.channel, this.step, this.note, 1.0);
                    break;

                case 3:
                    if (this.host.supports (Capability.NOTE_EDIT_GAIN))
                        this.clip.updateStepGain (this.channel, this.step, this.note, 0);
                    break;

                case 4:
                    if (this.host.supports (Capability.NOTE_EDIT_PANORAMA))
                        this.clip.updateStepPan (this.channel, this.step, this.note, 0);
                    break;

                case 5:
                    if (this.host.supports (Capability.NOTE_EDIT_TRANSPOSE))
                        this.clip.updateStepTranspose (this.channel, this.step, this.note, 0);
                    break;

                case 6:
                    if (this.host.supports (Capability.NOTE_EDIT_TIMBRE))
                        this.clip.updateStepTimbre (this.channel, this.step, this.note, 0);
                    break;

                case 7:
                    if (this.host.supports (Capability.NOTE_EDIT_PRESSURE))
                        this.clip.updateStepPressure (this.channel, this.step, this.note, 0);
                    break;

                default:
                    return;
            }
            return;
        }

        if (isTouched)
            this.clip.startEdit (this.channel, this.step, this.note);
        else
            this.clip.stopEdit ();
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        if (this.clip == null)
            return;

        switch (index)
        {
            case 0:
                this.clip.changeStepDuration (this.channel, this.step, this.note, value);
                break;

            case 1:
                this.clip.changeStepVelocity (this.channel, this.step, this.note, value);
                break;

            case 2:
                if (this.host.supports (Capability.NOTE_EDIT_RELEASE_VELOCITY))
                    this.clip.changeStepReleaseVelocity (this.channel, this.step, this.note, value);
                break;

            case 3:
                if (this.host.supports (Capability.NOTE_EDIT_GAIN))
                    this.clip.changeStepGain (this.channel, this.step, this.note, value);
                break;

            case 4:
                if (this.host.supports (Capability.NOTE_EDIT_PANORAMA))
                    this.clip.changeStepPan (this.channel, this.step, this.note, value);
                break;

            case 5:
                if (this.host.supports (Capability.NOTE_EDIT_TRANSPOSE))
                    this.clip.changeStepTranspose (this.channel, this.step, this.note, value);
                break;

            case 6:
                if (this.host.supports (Capability.NOTE_EDIT_TIMBRE))
                    this.clip.changeStepTimbre (this.channel, this.step, this.note, value);
                break;

            case 7:
                if (this.host.supports (Capability.NOTE_EDIT_PRESSURE))
                    this.clip.changeStepPressure (this.channel, this.step, this.note, value);
                break;

            default:
                return;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 (final ITextDisplay display)
    {
        if (this.clip == null)
            return;

        final IStepInfo stepInfo = this.clip.getStep (this.channel, this.step, this.note);
        final IValueChanger valueChanger = this.model.getValueChanger ();

        display.setCell (0, 0, "Length").setCell (1, 0, this.formatLength (stepInfo.getDuration ()));

        final double noteVelocity = stepInfo.getVelocity ();
        final int parameterValue = valueChanger.fromNormalizedValue (noteVelocity);
        display.setCell (0, 1, "Velocity").setCell (1, 1, StringUtils.formatPercentage (noteVelocity)).setCell (2, 1, parameterValue, Format.FORMAT_VALUE);

        if (this.host.supports (Capability.NOTE_EDIT_RELEASE_VELOCITY))
        {
            final double noteReleaseVelocity = stepInfo.getReleaseVelocity ();
            final int parameterReleaseValue = valueChanger.fromNormalizedValue (noteReleaseVelocity);
            display.setCell (0, 2, "R-Velocity").setCell (1, 2, StringUtils.formatPercentage (noteReleaseVelocity)).setCell (2, 2, parameterReleaseValue, Format.FORMAT_VALUE);
        }
        if (this.host.supports (Capability.NOTE_EDIT_GAIN))
        {
            final double noteGain = stepInfo.getGain ();
            final int parameterGainValue = Math.min (1023, valueChanger.fromNormalizedValue (noteGain));
            display.setCell (0, 3, "Gain").setCell (1, 3, StringUtils.formatPercentage (noteGain)).setCell (2, 3, parameterGainValue, Format.FORMAT_VALUE);
        }
        if (this.host.supports (Capability.NOTE_EDIT_PANORAMA))
        {
            final double notePan = stepInfo.getPan ();
            final int parameterPanValue = valueChanger.fromNormalizedValue ((notePan + 1.0) / 2.0);
            display.setCell (0, 4, "Pan").setCell (1, 4, StringUtils.formatPercentage (notePan)).setCell (2, 4, parameterPanValue, Format.FORMAT_PAN);
        }
        if (this.host.supports (Capability.NOTE_EDIT_TRANSPOSE))
        {
            final double noteTranspose = stepInfo.getTranspose ();
            final int parameterTransposeValue = valueChanger.fromNormalizedValue ((noteTranspose + 24.0) / 48.0);
            display.setCell (0, 5, "Pitch").setCell (1, 5, String.format ("%.1f", Double.valueOf (noteTranspose))).setCell (2, 5, parameterTransposeValue, Format.FORMAT_PAN);
        }
        if (this.host.supports (Capability.NOTE_EDIT_TIMBRE))
        {
            final double noteTimbre = stepInfo.getTimbre ();
            final int parameterTimbreValue = valueChanger.fromNormalizedValue ((noteTimbre + 1.0) / 2.0);
            display.setCell (0, 6, "Timbre").setCell (1, 6, StringUtils.formatPercentage (noteTimbre)).setCell (2, 6, parameterTimbreValue, Format.FORMAT_VALUE);
        }
        if (this.host.supports (Capability.NOTE_EDIT_PRESSURE))
        {
            final double notePressure = stepInfo.getPressure ();
            final int parameterPressureValue = valueChanger.fromNormalizedValue (notePressure);
            display.setCell (0, 7, "Pressure").setCell (1, 7, StringUtils.formatPercentage (notePressure)).setCell (2, 7, parameterPressureValue, Format.FORMAT_VALUE);
        }

        display.setCell (3, 0, "Step: " + (this.step + 1));
        display.setCell (3, 1, "Note: " + Scales.formatNoteAndOctave (this.note, -3));
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 (final IGraphicDisplay display)
    {
        if (this.clip == null)
            return;

        final IStepInfo stepInfo = this.clip.getStep (this.channel, this.step, this.note);
        final double noteVelocity = stepInfo.getVelocity ();

        final IValueChanger valueChanger = this.model.getValueChanger ();

        display.addParameterElementWithPlainMenu ("", false, "Step: " + (this.step + 1), null, false, "Length", -1, this.formatLength (stepInfo.getDuration ()), this.isKnobTouched[0], -1);

        final int parameterValue = valueChanger.fromNormalizedValue (noteVelocity);
        display.addParameterElementWithPlainMenu ("", false, Scales.formatNoteAndOctave (this.note, -3), null, false, "Velocity", parameterValue, StringUtils.formatPercentage (noteVelocity), this.isKnobTouched[1], parameterValue);

        if (this.host.supports (Capability.NOTE_EDIT_RELEASE_VELOCITY))
        {
            final double noteReleaseVelocity = stepInfo.getReleaseVelocity ();
            final int parameterReleaseValue = valueChanger.fromNormalizedValue (noteReleaseVelocity);
            display.addParameterElement ("R-Velocity", parameterReleaseValue, StringUtils.formatPercentage (noteReleaseVelocity), this.isKnobTouched[2], parameterReleaseValue);
        }
        else
            display.addEmptyElement ();

        if (this.host.supports (Capability.NOTE_EDIT_GAIN))
        {
            final double noteGain = stepInfo.getGain ();
            final int parameterGainValue = Math.min (1023, valueChanger.fromNormalizedValue (noteGain));
            display.addParameterElement ("Gain", parameterGainValue, StringUtils.formatPercentage (noteGain), this.isKnobTouched[3], parameterGainValue);
        }
        else
            display.addEmptyElement ();

        if (this.host.supports (Capability.NOTE_EDIT_PANORAMA))
        {
            final double notePan = stepInfo.getPan ();
            final int parameterPanValue = valueChanger.fromNormalizedValue ((notePan + 1.0) / 2.0);
            display.addParameterElement ("Pan", parameterPanValue, StringUtils.formatPercentage (notePan), this.isKnobTouched[4], parameterPanValue);
        }
        else
            display.addEmptyElement ();

        if (this.host.supports (Capability.NOTE_EDIT_TRANSPOSE))
        {
            final double noteTranspose = stepInfo.getTranspose ();
            final int parameterTransposeValue = valueChanger.fromNormalizedValue ((noteTranspose + 24.0) / 48.0);
            display.addParameterElement ("Pitch", parameterTransposeValue, String.format ("%.1f", Double.valueOf (noteTranspose)), this.isKnobTouched[5], parameterTransposeValue);
        }
        else
            display.addEmptyElement ();

        if (this.host.supports (Capability.NOTE_EDIT_TIMBRE))
        {
            final double noteTimbre = stepInfo.getTimbre ();
            final int parameterTimbreValue = valueChanger.fromNormalizedValue ((noteTimbre + 1.0) / 2.0);
            display.addParameterElement ("Timbre", parameterTimbreValue, StringUtils.formatPercentage (noteTimbre), this.isKnobTouched[6], parameterTimbreValue);
        }
        else
            display.addEmptyElement ();

        if (this.host.supports (Capability.NOTE_EDIT_PRESSURE))
        {
            final double notePressure = stepInfo.getPressure ();
            final int parameterPressureValue = valueChanger.fromNormalizedValue (notePressure);
            display.addParameterElement ("Pressure", parameterPressureValue, StringUtils.formatPercentage (notePressure), this.isKnobTouched[7], parameterPressureValue);
        }
        else
            display.addEmptyElement ();
    }


    /**
     * Format the duration of the current note.
     *
     * @param duration The note duration
     * @return The formatted value
     */
    private String formatLength (final double duration)
    {
        return StringUtils.formatMeasures (this.model.getTransport ().getQuartersPerMeasure (), duration, 0, true);
    }
}