// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.mode;

import de.mossgrabers.controller.ableton.push.controller.PushColorManager;
import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.display.Format;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.daw.IStepInfo;
import de.mossgrabers.framework.daw.NoteOccurrenceType;
import de.mossgrabers.framework.daw.constants.Capability;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Editing of note parameters.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class NoteMode extends BaseMode<IItem>
{
    private static final String [] MENU =
    {
        "Note",
        "Expressions",
        "Repeat",
        " ",
        " ",
        " ",
        " ",
        "Recurr. Pattern"
    };


    private enum Page
    {
        NOTE,
        RECCURRENCE_PATTERN,
        EXPRESSIONS,
        REPEAT
    }


    private final IHost host;
    private Page        page    = Page.NOTE;

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
    public void onFirstRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP || this.clip == null)
            return;

        final IStepInfo stepInfo = this.clip.getStep (this.channel, this.step, this.note);

        switch (this.page)
        {
            case NOTE:
                switch (index)
                {
                    case 5:
                        if (this.host.supports (Capability.NOTE_EDIT_CHANCE))
                            this.clip.updateIsChanceEnabled (this.channel, this.step, this.note, !stepInfo.isChanceEnabled ());
                        break;

                    case 6:
                        if (this.host.supports (Capability.NOTE_EDIT_OCCURRENCE))
                            this.clip.updateIsOccurrenceEnabled (this.channel, this.step, this.note, !stepInfo.isOccurrenceEnabled ());
                        break;

                    case 7:
                        if (this.host.supports (Capability.NOTE_EDIT_RECCURRENCE))
                            this.clip.updateIsRecurrenceEnabled (this.channel, this.step, this.note, !stepInfo.isRecurrenceEnabled ());
                        break;

                    default:
                        return;
                }
                break;

            case EXPRESSIONS:
                break;

            case REPEAT:
                if (index == 3)
                {
                    if (this.host.supports (Capability.NOTE_EDIT_REPEAT))
                        this.clip.updateIsRepeatEnabled (this.channel, this.step, this.note, !stepInfo.isRepeatEnabled ());
                }
                break;

            case RECCURRENCE_PATTERN:
                if (this.host.supports (Capability.NOTE_EDIT_RECCURRENCE))
                {
                    int mask = stepInfo.getRecurrenceMask ();
                    final int bitVal = 1 << index;
                    if ((mask & bitVal) > 0)
                        mask &= ~bitVal;
                    else
                        mask |= bitVal;
                    this.clip.updateRecurrenceMask (this.channel, this.step, this.note, mask);
                }
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onSecondRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP || this.clip == null)
            return;

        switch (index)
        {
            case 0:
                this.page = Page.NOTE;
                break;

            case 1:
                if (this.host.supports (Capability.NOTE_EDIT_EXPRESSIONS))
                    this.page = Page.EXPRESSIONS;
                break;

            case 2:
                if (this.host.supports (Capability.NOTE_EDIT_REPEAT))
                    this.page = Page.REPEAT;
                break;

            case 7:
                if (this.host.supports (Capability.NOTE_EDIT_RECCURRENCE))
                    this.page = Page.RECCURRENCE_PATTERN;
                break;
        }
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

            switch (this.page)
            {
                case NOTE:
                    switch (index)
                    {
                        case 0:
                            this.clip.updateStepDuration (this.channel, this.step, this.note, 1.0);
                            break;

                        case 2:
                            this.clip.updateStepVelocity (this.channel, this.step, this.note, 1.0);
                            break;

                        case 3:
                            if (this.host.supports (Capability.NOTE_EDIT_VELOCITY_SPREAD))
                                this.clip.updateVelocitySpread (this.channel, this.step, this.note, 0);
                            break;

                        case 4:
                            if (this.host.supports (Capability.NOTE_EDIT_RELEASE_VELOCITY))
                                this.clip.updateStepReleaseVelocity (this.channel, this.step, this.note, 1.0);
                            break;

                        case 5:
                            if (this.host.supports (Capability.NOTE_EDIT_CHANCE))
                                this.clip.updateChance (this.channel, this.step, this.note, 1.0);
                            break;

                        case 6:
                            if (this.host.supports (Capability.NOTE_EDIT_OCCURRENCE))
                                this.clip.setOccurrence (this.channel, this.step, this.note, NoteOccurrenceType.ALWAYS);
                            break;

                        case 7:
                            if (this.host.supports (Capability.NOTE_EDIT_RECCURRENCE))
                                this.clip.updateRecurrenceLength (this.channel, this.step, this.note, 1);
                            break;

                        default:
                            return;
                    }
                    break;

                case EXPRESSIONS:
                    switch (index)
                    {
                        case 0:
                            this.clip.updateStepDuration (this.channel, this.step, this.note, 1.0);
                            break;

                        case 3:
                            if (this.host.supports (Capability.NOTE_EDIT_EXPRESSIONS))
                                this.clip.updateStepGain (this.channel, this.step, this.note, 0);
                            break;

                        case 4:
                            if (this.host.supports (Capability.NOTE_EDIT_EXPRESSIONS))
                                this.clip.updateStepPan (this.channel, this.step, this.note, 0);
                            break;

                        case 5:
                            if (this.host.supports (Capability.NOTE_EDIT_EXPRESSIONS))
                                this.clip.updateStepTranspose (this.channel, this.step, this.note, 0);
                            break;

                        case 6:
                            if (this.host.supports (Capability.NOTE_EDIT_EXPRESSIONS))
                                this.clip.updateStepTimbre (this.channel, this.step, this.note, 0);
                            break;

                        case 7:
                            if (this.host.supports (Capability.NOTE_EDIT_EXPRESSIONS))
                                this.clip.updateStepPressure (this.channel, this.step, this.note, 0);
                            break;
                    }
                    break;

                case REPEAT:
                    switch (index)
                    {
                        case 0:
                            this.clip.updateStepDuration (this.channel, this.step, this.note, 1.0);
                            break;

                        case 3:
                            if (this.host.supports (Capability.NOTE_EDIT_REPEAT))
                                this.clip.updateRepeatCount (this.channel, this.step, this.note, 0);
                            break;

                        case 4:
                            if (this.host.supports (Capability.NOTE_EDIT_REPEAT))
                                this.clip.updateRepeatCurve (this.channel, this.step, this.note, 0);
                            break;

                        case 5:
                            if (this.host.supports (Capability.NOTE_EDIT_REPEAT))
                                this.clip.updateRepeatVelocityCurve (this.channel, this.step, this.note, 0);
                            break;

                        case 6:
                            if (this.host.supports (Capability.NOTE_EDIT_REPEAT))
                                this.clip.updateRepeatVelocityEnd (this.channel, this.step, this.note, 0);
                            break;
                    }
                    break;

                case RECCURRENCE_PATTERN:
                    if (index == 7)
                    {
                        if (this.host.supports (Capability.NOTE_EDIT_RECCURRENCE))
                            this.clip.updateRecurrenceLength (this.channel, this.step, this.note, 1);
                    }
                    break;
            }
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

        switch (this.page)
        {
            case NOTE:
                switch (index)
                {
                    case 0:
                        this.clip.changeStepDuration (this.channel, this.step, this.note, value);
                        break;

                    case 2:
                        this.clip.changeStepVelocity (this.channel, this.step, this.note, value);
                        break;

                    case 3:
                        if (this.host.supports (Capability.NOTE_EDIT_VELOCITY_SPREAD))
                            this.clip.changeVelocitySpread (this.channel, this.step, this.note, value);
                        break;

                    case 4:
                        if (this.host.supports (Capability.NOTE_EDIT_RELEASE_VELOCITY))
                            this.clip.changeStepReleaseVelocity (this.channel, this.step, this.note, value);
                        break;

                    case 5:
                        if (this.host.supports (Capability.NOTE_EDIT_CHANCE))
                            this.clip.changeChance (this.channel, this.step, this.note, value);
                        break;

                    case 6:
                        if (this.host.supports (Capability.NOTE_EDIT_OCCURRENCE))
                        {
                            final boolean increase = this.model.getValueChanger ().isIncrease (value);
                            this.clip.setPrevNextOccurrence (this.channel, this.step, this.note, increase);
                        }
                        break;

                    case 7:
                        if (this.host.supports (Capability.NOTE_EDIT_RECCURRENCE))
                            this.clip.changeRecurrenceLength (this.channel, this.step, this.note, value);
                        break;

                    default:
                        return;
                }
                break;

            case EXPRESSIONS:
                switch (index)
                {
                    case 0:
                        this.clip.changeStepDuration (this.channel, this.step, this.note, value);
                        break;

                    case 3:
                        if (this.host.supports (Capability.NOTE_EDIT_EXPRESSIONS))
                            this.clip.changeStepGain (this.channel, this.step, this.note, value);
                        break;

                    case 4:
                        if (this.host.supports (Capability.NOTE_EDIT_EXPRESSIONS))
                            this.clip.changeStepPan (this.channel, this.step, this.note, value);
                        break;

                    case 5:
                        if (this.host.supports (Capability.NOTE_EDIT_EXPRESSIONS))
                            this.clip.changeStepTranspose (this.channel, this.step, this.note, value);
                        break;

                    case 6:
                        if (this.host.supports (Capability.NOTE_EDIT_EXPRESSIONS))
                            this.clip.changeStepTimbre (this.channel, this.step, this.note, value);
                        break;

                    case 7:
                        if (this.host.supports (Capability.NOTE_EDIT_EXPRESSIONS))
                            this.clip.changeStepPressure (this.channel, this.step, this.note, value);
                        break;
                }
                break;

            case REPEAT:
                switch (index)
                {
                    case 0:
                        this.clip.changeStepDuration (this.channel, this.step, this.note, value);
                        break;

                    case 3:
                        if (this.host.supports (Capability.NOTE_EDIT_REPEAT))
                            this.clip.changeRepeatCount (this.channel, this.step, this.note, value);
                        break;

                    case 4:
                        if (this.host.supports (Capability.NOTE_EDIT_REPEAT))
                            this.clip.changeRepeatCurve (this.channel, this.step, this.note, value);
                        break;

                    case 5:
                        if (this.host.supports (Capability.NOTE_EDIT_REPEAT))
                            this.clip.changeRepeatVelocityCurve (this.channel, this.step, this.note, value);
                        break;

                    case 6:
                        if (this.host.supports (Capability.NOTE_EDIT_REPEAT))
                            this.clip.changeRepeatVelocityEnd (this.channel, this.step, this.note, value);
                        break;
                }
                break;

            case RECCURRENCE_PATTERN:
                if (index == 7)
                {
                    if (this.host.supports (Capability.NOTE_EDIT_RECCURRENCE))
                        this.clip.changeRecurrenceLength (this.channel, this.step, this.note, value);
                }
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 (final ITextDisplay display)
    {
        if (this.clip == null)
            return;

        final IStepInfo stepInfo = this.clip.getStep (this.channel, this.step, this.note);

        if (this.page != Page.RECCURRENCE_PATTERN)
        {
            display.setCell (0, 0, "Length").setCell (1, 0, this.formatLength (stepInfo.getDuration ()));

            display.setCell (3, 0, "Step: " + (this.step + 1));
            display.setCell (3, 1, "Note: " + Scales.formatNoteAndOctave (this.note, -3));
        }

        final IValueChanger valueChanger = this.model.getValueChanger ();

        switch (this.page)
        {
            case NOTE:
                display.setCell (0, 1, "   NOTE:");

                final double noteVelocity = stepInfo.getVelocity ();
                final int parameterValue = valueChanger.fromNormalizedValue (noteVelocity);
                display.setCell (0, 2, "Velocity");
                display.setCell (1, 2, StringUtils.formatPercentage (noteVelocity));
                display.setCell (2, 2, parameterValue, Format.FORMAT_VALUE);

                if (this.host.supports (Capability.NOTE_EDIT_VELOCITY_SPREAD))
                {
                    final double noteVelocitySpread = stepInfo.getVelocitySpread ();
                    final int parameterSpreadValue = valueChanger.fromNormalizedValue (noteVelocitySpread);
                    display.setCell (0, 3, "V-Spread");
                    display.setCell (1, 3, StringUtils.formatPercentage (noteVelocitySpread));
                    display.setCell (2, 3, parameterSpreadValue, Format.FORMAT_VALUE);
                }

                if (this.host.supports (Capability.NOTE_EDIT_RELEASE_VELOCITY))
                {
                    final double noteReleaseVelocity = stepInfo.getReleaseVelocity ();
                    final int parameterReleaseValue = valueChanger.fromNormalizedValue (noteReleaseVelocity);
                    display.setCell (0, 4, "R-Velcty");
                    display.setCell (1, 4, StringUtils.formatPercentage (noteReleaseVelocity));
                    display.setCell (2, 4, parameterReleaseValue, Format.FORMAT_VALUE);
                }

                if (this.host.supports (Capability.NOTE_EDIT_CHANCE))
                {
                    final double chance = stepInfo.getChance ();
                    final int chanceValue = valueChanger.fromNormalizedValue (chance);
                    display.setCell (0, 5, "Chance");
                    display.setCell (1, 5, StringUtils.formatPercentage (chance));
                    display.setCell (2, 5, chanceValue, Format.FORMAT_VALUE);
                    display.setCell (3, 5, stepInfo.isChanceEnabled () ? "   On" : "  Off");
                }

                if (this.host.supports (Capability.NOTE_EDIT_OCCURRENCE))
                {
                    final NoteOccurrenceType occurrence = stepInfo.getOccurrence ();
                    display.setCell (0, 6, "Occurnce");
                    display.setCell (1, 6, StringUtils.optimizeName (occurrence.getName (), 8));
                    display.setCell (3, 6, stepInfo.isOccurrenceEnabled () ? "   On" : "  Off");
                }

                if (this.host.supports (Capability.NOTE_EDIT_RECCURRENCE))
                {
                    final int recurrence = stepInfo.getRecurrenceLength ();
                    final String recurrenceStr = recurrence < 2 ? "Off" : Integer.toString (recurrence);
                    final int recurrenceVal = (recurrence - 1) * (this.model.getValueChanger ().getUpperBound () - 1) / 7;
                    display.setCell (0, 7, "Recurnce");
                    display.setCell (1, 7, recurrenceStr);
                    display.setCell (2, 7, recurrenceVal, Format.FORMAT_VALUE);
                    display.setCell (3, 7, stepInfo.isRecurrenceEnabled () ? "   On" : "  Off");
                }
                break;

            case EXPRESSIONS:
                if (this.host.supports (Capability.NOTE_EDIT_EXPRESSIONS))
                {
                    display.setCell (0, 2, "EXPRESS:");

                    final double noteGain = stepInfo.getGain ();
                    final int parameterGainValue = Math.min (1023, valueChanger.fromNormalizedValue (noteGain));
                    display.setCell (0, 3, "Gain").setCell (1, 3, StringUtils.formatPercentage (noteGain)).setCell (2, 3, parameterGainValue, Format.FORMAT_VALUE);

                    final double notePan = stepInfo.getPan ();
                    final int parameterPanValue = valueChanger.fromNormalizedValue ((notePan + 1.0) / 2.0);
                    display.setCell (0, 4, "Pan").setCell (1, 4, StringUtils.formatPercentage (notePan)).setCell (2, 4, parameterPanValue, Format.FORMAT_PAN);

                    final double noteTranspose = stepInfo.getTranspose ();
                    final int parameterTransposeValue = valueChanger.fromNormalizedValue ((noteTranspose + 24.0) / 48.0);
                    display.setCell (0, 5, "Pitch").setCell (1, 5, String.format ("%.1f", Double.valueOf (noteTranspose))).setCell (2, 5, parameterTransposeValue, Format.FORMAT_PAN);

                    final double noteTimbre = stepInfo.getTimbre ();
                    final int parameterTimbreValue = valueChanger.fromNormalizedValue ((noteTimbre + 1.0) / 2.0);
                    display.setCell (0, 6, "Timbre").setCell (1, 6, StringUtils.formatPercentage (noteTimbre)).setCell (2, 6, parameterTimbreValue, Format.FORMAT_VALUE);

                    final double notePressure = stepInfo.getPressure ();
                    final int parameterPressureValue = valueChanger.fromNormalizedValue (notePressure);
                    display.setCell (0, 7, "Pressure").setCell (1, 7, StringUtils.formatPercentage (notePressure)).setCell (2, 7, parameterPressureValue, Format.FORMAT_VALUE);
                }
                break;

            case REPEAT:
                display.setCell (0, 2, "REPEAT:");

                final int repeatCount = stepInfo.getRepeatCount ();
                final String repeatCountValue;
                if (repeatCount == 0)
                    repeatCountValue = "Off";
                else if (repeatCount < 0)
                    repeatCountValue = "1/" + Math.abs (repeatCount - 1);
                else
                    repeatCountValue = Integer.toString (repeatCount + 1);
                final int rc = (repeatCount + 127) * (this.model.getValueChanger ().getUpperBound () - 1) / 254;
                display.setCell (0, 3, "Count");
                display.setCell (1, 3, repeatCountValue);
                display.setCell (2, 3, rc, Format.FORMAT_VALUE);
                display.setCell (3, 3, stepInfo.isRepeatEnabled () ? "   On" : "  Off");

                final double repeatCurve = stepInfo.getRepeatCurve ();
                final int repeatCurveValue = valueChanger.fromNormalizedValue ((repeatCurve + 1.0) / 2.0);
                display.setCell (0, 4, "Curve");
                display.setCell (1, 4, StringUtils.formatPercentage (repeatCurve));
                display.setCell (2, 4, repeatCurveValue, Format.FORMAT_VALUE);

                final double repeatVelocityCurve = stepInfo.getRepeatVelocityCurve ();
                final int repeatVelocityCurveValue = valueChanger.fromNormalizedValue ((repeatVelocityCurve + 1.0) / 2.0);
                display.setCell (0, 5, "Vel-Crve");
                display.setCell (1, 5, StringUtils.formatPercentage (repeatVelocityCurve));
                display.setCell (2, 5, repeatVelocityCurveValue, Format.FORMAT_VALUE);

                final double repeatVelocityEnd = stepInfo.getRepeatVelocityEnd ();
                final int repeatVelocityEndValue = valueChanger.fromNormalizedValue ((repeatVelocityEnd + 1.0) / 2.0);
                display.setCell (0, 6, "Vel. End");
                display.setCell (1, 6, StringUtils.formatPercentage (repeatVelocityEnd));
                display.setCell (2, 6, repeatVelocityEndValue, Format.FORMAT_VALUE);
                break;

            case RECCURRENCE_PATTERN:
                display.setBlock (0, 1, "       Recurrence");
                display.setBlock (0, 2, "Pattern");

                final int recurrenceLength = stepInfo.getRecurrenceLength ();
                final int mask = stepInfo.getRecurrenceMask ();
                for (int i = 0; i < 8; i++)
                {
                    final boolean isOn = (mask & (1 << i)) > 0;
                    String label = "   -";
                    if (i < recurrenceLength)
                    {
                        label = isOn ? "   On" : "  Off";
                    }
                    if (i == 7)
                    {
                        final int recurrence = stepInfo.getRecurrenceLength ();
                        final String recurrenceStr = recurrence < 2 ? "Off" : Integer.toString (recurrence);
                        final int recurrenceVal = (recurrence - 1) * (this.model.getValueChanger ().getUpperBound () - 1) / 7;
                        display.setCell (0, 7, "Recurnce");
                        display.setCell (1, 7, recurrenceStr);
                        display.setCell (2, 7, recurrenceVal, Format.FORMAT_VALUE);
                    }
                    display.setCell (3, i, label);
                }
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 (final IGraphicDisplay display)
    {
        if (this.clip == null)
            return;

        final IStepInfo stepInfo = this.clip.getStep (this.channel, this.step, this.note);

        if (this.page != Page.RECCURRENCE_PATTERN)
        {
            display.addParameterElementWithPlainMenu (MENU[0], this.page == Page.NOTE, "Step: " + (this.step + 1), null, false, "Length", -1, this.formatLength (stepInfo.getDuration ()), this.isKnobTouched[0], -1);
            final boolean hasExpressions = this.host.supports (Capability.NOTE_EDIT_EXPRESSIONS);
            display.addParameterElementWithPlainMenu (hasExpressions ? MENU[1] : " ", hasExpressions && this.page == Page.EXPRESSIONS, Scales.formatNoteAndOctave (this.note, -3), null, false, null, -1, null, false, -1);
        }

        final IValueChanger valueChanger = this.model.getValueChanger ();

        switch (this.page)
        {
            case NOTE:
                final double noteVelocity = stepInfo.getVelocity ();
                final int parameterValue = valueChanger.fromNormalizedValue (noteVelocity);
                display.addParameterElementWithPlainMenu (this.host.supports (Capability.NOTE_EDIT_REPEAT) ? MENU[2] : " ", false, null, null, false, "Velocity", parameterValue, StringUtils.formatPercentage (noteVelocity), this.isKnobTouched[2], parameterValue);

                if (this.host.supports (Capability.NOTE_EDIT_VELOCITY_SPREAD))
                {
                    final double noteVelocitySpread = stepInfo.getVelocitySpread ();
                    final int parameterSpreadValue = valueChanger.fromNormalizedValue (noteVelocitySpread);
                    display.addParameterElementWithPlainMenu (MENU[3], false, null, null, false, "Vel-Spread", parameterSpreadValue, StringUtils.formatPercentage (noteVelocitySpread), this.isKnobTouched[3], parameterSpreadValue);
                }
                else
                    display.addEmptyElement (true);

                if (this.host.supports (Capability.NOTE_EDIT_RELEASE_VELOCITY))
                {
                    final double noteReleaseVelocity = stepInfo.getReleaseVelocity ();
                    final int parameterReleaseValue = valueChanger.fromNormalizedValue (noteReleaseVelocity);
                    display.addParameterElementWithPlainMenu (MENU[4], false, null, null, false, "R-Velocity", parameterReleaseValue, StringUtils.formatPercentage (noteReleaseVelocity), this.isKnobTouched[4], parameterReleaseValue);
                }
                else
                    display.addEmptyElement (true);

                if (this.host.supports (Capability.NOTE_EDIT_CHANCE))
                {
                    final double chance = stepInfo.getChance ();
                    final int chanceValue = valueChanger.fromNormalizedValue (chance);
                    display.addParameterElementWithPlainMenu (MENU[5], false, stepInfo.isChanceEnabled () ? "On" : "Off", null, false, "Chance", chanceValue, StringUtils.formatPercentage (chance), this.isKnobTouched[5], chanceValue);
                }
                else
                    display.addEmptyElement (true);

                if (this.host.supports (Capability.NOTE_EDIT_OCCURRENCE))
                {
                    final NoteOccurrenceType occurrence = stepInfo.getOccurrence ();
                    display.addParameterElementWithPlainMenu (MENU[6], false, stepInfo.isOccurrenceEnabled () ? "On" : "Off", null, false, "Occurrence", -1, StringUtils.optimizeName (occurrence.getName (), 9), this.isKnobTouched[6], -1);
                }
                else
                    display.addEmptyElement (true);

                if (this.host.supports (Capability.NOTE_EDIT_RECCURRENCE))
                {
                    final int recurrence = stepInfo.getRecurrenceLength ();
                    final String recurrenceStr = recurrence < 2 ? "Off" : Integer.toString (recurrence);
                    final int recurrenceVal = (recurrence - 1) * (this.model.getValueChanger ().getUpperBound () - 1) / 7;
                    display.addParameterElementWithPlainMenu (MENU[7], false, stepInfo.isRecurrenceEnabled () ? "On" : "Off", null, false, "Recurrence", recurrenceVal, recurrenceStr, this.isKnobTouched[7], recurrenceVal);
                }
                else
                    display.addEmptyElement (true);

                break;

            case EXPRESSIONS:
                display.addParameterElementWithPlainMenu (MENU[2], false, null, null, false, null, -1, null, false, -1);

                final double noteGain = stepInfo.getGain ();
                final int parameterGainValue = Math.min (1023, valueChanger.fromNormalizedValue (noteGain));
                display.addParameterElementWithPlainMenu (MENU[3], false, null, null, false, "Gain", parameterGainValue, StringUtils.formatPercentage (noteGain), this.isKnobTouched[3], parameterGainValue);

                final double notePan = stepInfo.getPan ();
                final int parameterPanValue = valueChanger.fromNormalizedValue ((notePan + 1.0) / 2.0);
                display.addParameterElementWithPlainMenu (MENU[4], false, null, null, false, "Pan", parameterPanValue, StringUtils.formatPercentage (notePan), this.isKnobTouched[4], parameterPanValue);

                final double noteTranspose = stepInfo.getTranspose ();
                final int parameterTransposeValue = valueChanger.fromNormalizedValue ((noteTranspose + 24.0) / 48.0);
                display.addParameterElementWithPlainMenu (MENU[5], false, null, null, false, "Pitch", parameterTransposeValue, String.format ("%.1f", Double.valueOf (noteTranspose)), this.isKnobTouched[5], parameterTransposeValue);

                final double noteTimbre = stepInfo.getTimbre ();
                final int parameterTimbreValue = valueChanger.fromNormalizedValue ((noteTimbre + 1.0) / 2.0);
                display.addParameterElementWithPlainMenu (MENU[6], false, null, null, false, "Timbre", parameterTimbreValue, StringUtils.formatPercentage (noteTimbre), this.isKnobTouched[6], parameterTimbreValue);

                final double notePressure = stepInfo.getPressure ();
                final int parameterPressureValue = valueChanger.fromNormalizedValue (notePressure);
                display.addParameterElementWithPlainMenu (MENU[7], this.page == Page.RECCURRENCE_PATTERN, null, null, false, "Pressure", parameterPressureValue, StringUtils.formatPercentage (notePressure), this.isKnobTouched[7], parameterPressureValue);
                break;

            case REPEAT:
                display.addParameterElementWithPlainMenu (MENU[2], true, null, null, false, null, -1, null, false, -1);

                final int repeatCount = stepInfo.getRepeatCount ();
                final String repeatCountValue;
                if (repeatCount == 0)
                    repeatCountValue = "Off";
                else if (repeatCount < 0)
                    repeatCountValue = "1/" + Math.abs (repeatCount - 1);
                else
                    repeatCountValue = Integer.toString (repeatCount + 1);
                final int rc = (repeatCount + 127) * (this.model.getValueChanger ().getUpperBound () - 1) / 254;
                display.addParameterElementWithPlainMenu (MENU[3], false, stepInfo.isRepeatEnabled () ? "On" : "Off", null, false, "Count", rc, repeatCountValue, this.isKnobTouched[3], rc);

                final double repeatCurve = stepInfo.getRepeatCurve ();
                final int repeatCurveValue = valueChanger.fromNormalizedValue ((repeatCurve + 1.0) / 2.0);
                display.addParameterElementWithPlainMenu (MENU[4], false, null, null, false, "Curve", repeatCurveValue, StringUtils.formatPercentage (repeatCurve), this.isKnobTouched[4], repeatCurveValue);

                final double repeatVelocityCurve = stepInfo.getRepeatVelocityCurve ();
                final int repeatVelocityCurveValue = valueChanger.fromNormalizedValue ((repeatVelocityCurve + 1.0) / 2.0);
                display.addParameterElementWithPlainMenu (MENU[5], false, null, null, false, "Vel. Curve", repeatVelocityCurveValue, StringUtils.formatPercentage (repeatVelocityCurve), this.isKnobTouched[5], repeatVelocityCurveValue);

                final double repeatVelocityEnd = stepInfo.getRepeatVelocityEnd ();
                final int repeatVelocityEndValue = valueChanger.fromNormalizedValue ((repeatVelocityEnd + 1.0) / 2.0);
                display.addParameterElementWithPlainMenu (MENU[6], false, null, null, false, "Vel. End", repeatVelocityEndValue, StringUtils.formatPercentage (repeatVelocityEnd), this.isKnobTouched[6], repeatVelocityEndValue);

                display.addParameterElementWithPlainMenu (MENU[7], false, null, null, false, null, -1, null, false, -1);
                break;

            case RECCURRENCE_PATTERN:
                final int recurrenceLength = stepInfo.getRecurrenceLength ();
                final int mask = stepInfo.getRecurrenceMask ();
                for (int i = 0; i < 8; i++)
                {
                    final boolean isOn = (mask & (1 << i)) > 0;
                    ColorEx color = ColorEx.BLACK;
                    String label = "-";
                    if (i < recurrenceLength)
                    {
                        color = isOn ? ColorEx.ORANGE : null;
                        label = isOn ? "On" : "Off";
                    }
                    if (i == 7)
                    {
                        final int recurrence = stepInfo.getRecurrenceLength ();
                        final String recurrenceStr = recurrence < 2 ? "Off" : Integer.toString (recurrence);
                        final int recurrenceVal = (recurrence - 1) * (this.model.getValueChanger ().getUpperBound () - 1) / 7;
                        display.addParameterElementWithPlainMenu (MENU[i], i == 7, label, color, false, "Recurrence", recurrenceVal, recurrenceStr, this.isKnobTouched[7], recurrenceVal);
                    }
                    else
                        display.addParameterElementWithPlainMenu (MENU[i], i == 7, label, color, false, null, -1, null, false, -1);
                }
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        if (this.clip == null)
            return this.colorManager.getColorIndex (PushColorManager.PUSH_BLACK);

        final IStepInfo stepInfo = this.clip.getStep (this.channel, this.step, this.note);

        int index = this.isButtonRow (0, buttonID);
        if (index >= 0)
        {
            switch (this.page)
            {
                case NOTE:
                    if (index == 5 && this.host.supports (Capability.NOTE_EDIT_CHANCE))
                        return this.colorManager.getColorIndex (stepInfo.isChanceEnabled () ? PushColorManager.PUSH_ORANGE_HI : PushColorManager.PUSH_ORANGE_LO);
                    if (index == 6 && this.host.supports (Capability.NOTE_EDIT_OCCURRENCE))
                        return this.colorManager.getColorIndex (stepInfo.isOccurrenceEnabled () ? PushColorManager.PUSH_ORANGE_HI : PushColorManager.PUSH_ORANGE_LO);
                    if (index == 7 && this.host.supports (Capability.NOTE_EDIT_RECCURRENCE))
                        return this.colorManager.getColorIndex (stepInfo.isRecurrenceEnabled () ? PushColorManager.PUSH_ORANGE_HI : PushColorManager.PUSH_ORANGE_LO);
                    break;

                case EXPRESSIONS:
                    break;

                case REPEAT:
                    if (index == 3)
                        return this.colorManager.getColorIndex (stepInfo.isRepeatEnabled () ? PushColorManager.PUSH_ORANGE_HI : PushColorManager.PUSH_ORANGE_LO);
                    break;

                case RECCURRENCE_PATTERN:
                    final int recurrenceLength = stepInfo.getRecurrenceLength ();
                    final int mask = stepInfo.getRecurrenceMask ();
                    final boolean isOn = (mask & (1 << index)) > 0;
                    String color = PushColorManager.PUSH_BLACK;
                    if (index < recurrenceLength)
                        color = isOn ? PushColorManager.PUSH_ORANGE_HI : PushColorManager.PUSH_ORANGE_LO;
                    return this.colorManager.getColorIndex (color);
            }

            return this.colorManager.getColorIndex (PushColorManager.PUSH_BLACK);
        }

        index = this.isButtonRow (1, buttonID);
        if (index >= 0)
        {
            switch (this.page)
            {
                case NOTE:
                    if (index == 0)
                        return this.colorManager.getColorIndex (PushColorManager.PUSH_GREEN_2);
                    break;

                case EXPRESSIONS:
                    if (index == 1)
                        return this.colorManager.getColorIndex (PushColorManager.PUSH_GREEN_2);
                    break;

                case REPEAT:
                    if (index == 2)
                        return this.colorManager.getColorIndex (PushColorManager.PUSH_GREEN_2);
                    break;

                case RECCURRENCE_PATTERN:
                    if (index == 7)
                        return this.colorManager.getColorIndex (PushColorManager.PUSH_GREEN_2);
                    break;
            }

            if (index == 0)
                return this.colorManager.getColorIndex (PushColorManager.PUSH_GREY_LO_2);
            if (index == 1 && this.host.supports (Capability.NOTE_EDIT_EXPRESSIONS))
                return this.colorManager.getColorIndex (PushColorManager.PUSH_GREY_LO_2);
            if (index == 2 && this.host.supports (Capability.NOTE_EDIT_REPEAT))
                return this.colorManager.getColorIndex (PushColorManager.PUSH_GREY_LO_2);
            if (index == 7 && this.host.supports (Capability.NOTE_EDIT_RECCURRENCE))
                return this.colorManager.getColorIndex (PushColorManager.PUSH_GREY_LO_2);

            return this.colorManager.getColorIndex (PushColorManager.PUSH_BLACK_2);
        }

        return this.colorManager.getColorIndex (PushColorManager.PUSH_BLACK);
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