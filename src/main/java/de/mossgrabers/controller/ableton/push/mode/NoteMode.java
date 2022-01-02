// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
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
import de.mossgrabers.framework.daw.data.GridStep;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.mode.INoteMode;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Editing of note parameters.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class NoteMode extends BaseMode<IItem> implements INoteMode
{
    private static final String    OFF  = "  Off";
    private static final String    ON   = "   On";

    private static final String [] MENU =
    {
        "Common",
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


    private final IHost          host;
    private Page                 page  = Page.NOTE;
    private INoteClip            clip  = null;
    private final List<GridStep> notes = new ArrayList<> ();


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


    /** {@inheritDoc} */
    @Override
    public void clearNotes ()
    {
        this.notes.clear ();
    }


    /** {@inheritDoc} */
    @Override
    public void setNote (final INoteClip clip, final int channel, final int step, final int note)
    {
        this.notes.clear ();
        this.addNote (clip, channel, step, note);
    }


    /** {@inheritDoc} */
    @Override
    public void addNote (final INoteClip clip, final int channel, final int step, final int note)
    {
        if (this.clip != clip)
        {
            this.notes.clear ();
            this.clip = clip;
        }

        // Is the note already edited? Remove it.
        for (final GridStep gridStep: this.notes)
        {
            if (gridStep.channel () == channel && gridStep.step () == step && gridStep.note () == note)
            {
                this.notes.remove (gridStep);
                return;
            }
        }

        this.notes.add (new GridStep (channel, step, note));
    }


    /** {@inheritDoc} */
    @Override
    public List<GridStep> getNotes ()
    {
        return new ArrayList<> (this.notes);
    }


    /** {@inheritDoc} */
    @Override
    public void onFirstRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;

        for (final GridStep noteInfo: this.notes)
        {
            final int channel = noteInfo.channel ();
            final int step = noteInfo.step ();
            final int note = noteInfo.note ();

            final IStepInfo stepInfo = this.clip.getStep (channel, step, note);

            switch (this.page)
            {
                case NOTE:
                    switch (index)
                    {
                        case 5:
                            if (this.host.supports (Capability.NOTE_EDIT_CHANCE))
                                this.clip.updateIsChanceEnabled (channel, step, note, !stepInfo.isChanceEnabled ());
                            break;

                        case 6:
                            if (this.host.supports (Capability.NOTE_EDIT_OCCURRENCE))
                                this.clip.updateIsOccurrenceEnabled (channel, step, note, !stepInfo.isOccurrenceEnabled ());
                            break;

                        case 7:
                            if (this.host.supports (Capability.NOTE_EDIT_RECCURRENCE))
                                this.clip.updateIsRecurrenceEnabled (channel, step, note, !stepInfo.isRecurrenceEnabled ());
                            break;

                        default:
                            return;
                    }
                    break;

                case EXPRESSIONS:
                    break;

                case REPEAT:
                    if (index == 3 && this.host.supports (Capability.NOTE_EDIT_REPEAT))
                        this.clip.updateIsRepeatEnabled (channel, step, note, !stepInfo.isRepeatEnabled ());
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
                        this.clip.updateRecurrenceMask (channel, step, note, mask);
                    }
                    break;
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onSecondRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
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

            default:
                // Not used:
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        if (this.notes.isEmpty ())
            return;

        if (isTouched && this.surface.isDeletePressed ())
        {
            this.surface.setTriggerConsumed (ButtonID.DELETE);

            for (final GridStep noteInfo: this.notes)
            {
                final int channel = noteInfo.channel ();
                final int step = noteInfo.step ();
                final int note = noteInfo.note ();

                switch (this.page)
                {
                    case NOTE:
                        switch (index)
                        {
                            case 0:
                                this.clip.updateStepDuration (channel, step, note, 1.0);
                                break;

                            case 1:
                                this.clip.updateMuteState (channel, step, note, false);
                                break;

                            case 2:
                                this.clip.updateStepVelocity (channel, step, note, 1.0);
                                break;

                            case 3:
                                if (this.host.supports (Capability.NOTE_EDIT_VELOCITY_SPREAD))
                                    this.clip.updateVelocitySpread (channel, step, note, 0);
                                break;

                            case 4:
                                if (this.host.supports (Capability.NOTE_EDIT_RELEASE_VELOCITY))
                                    this.clip.updateStepReleaseVelocity (channel, step, note, 1.0);
                                break;

                            case 5:
                                if (this.host.supports (Capability.NOTE_EDIT_CHANCE))
                                    this.clip.updateChance (channel, step, note, 1.0);
                                break;

                            case 6:
                                if (this.host.supports (Capability.NOTE_EDIT_OCCURRENCE))
                                    this.clip.setOccurrence (channel, step, note, NoteOccurrenceType.ALWAYS);
                                break;

                            case 7:
                                if (this.host.supports (Capability.NOTE_EDIT_RECCURRENCE))
                                    this.clip.updateRecurrenceLength (channel, step, note, 1);
                                break;

                            default:
                                return;
                        }
                        break;

                    case EXPRESSIONS:
                        switch (index)
                        {
                            case 0:
                                this.clip.updateStepDuration (channel, step, note, 1.0);
                                break;

                            case 1:
                                this.clip.updateMuteState (channel, step, note, false);
                                break;

                            case 3:
                                if (this.host.supports (Capability.NOTE_EDIT_EXPRESSIONS))
                                    this.clip.updateStepGain (channel, step, note, 0.5);
                                break;

                            case 4:
                                if (this.host.supports (Capability.NOTE_EDIT_EXPRESSIONS))
                                    this.clip.updateStepPan (channel, step, note, 0);
                                break;

                            case 5:
                                if (this.host.supports (Capability.NOTE_EDIT_EXPRESSIONS))
                                    this.clip.updateStepTranspose (channel, step, note, 0);
                                break;

                            case 6:
                                if (this.host.supports (Capability.NOTE_EDIT_EXPRESSIONS))
                                    this.clip.updateStepTimbre (channel, step, note, 0);
                                break;

                            case 7:
                                if (this.host.supports (Capability.NOTE_EDIT_EXPRESSIONS))
                                    this.clip.updateStepPressure (channel, step, note, 0);
                                break;

                            default:
                                return;
                        }
                        break;

                    case REPEAT:
                        switch (index)
                        {
                            case 0:
                                this.clip.updateStepDuration (channel, step, note, 1.0);
                                break;

                            case 1:
                                this.clip.updateMuteState (channel, step, note, false);
                                break;

                            case 3:
                                if (this.host.supports (Capability.NOTE_EDIT_REPEAT))
                                    this.clip.updateRepeatCount (channel, step, note, 0);
                                break;

                            case 4:
                                if (this.host.supports (Capability.NOTE_EDIT_REPEAT))
                                    this.clip.updateRepeatCurve (channel, step, note, 0);
                                break;

                            case 5:
                                if (this.host.supports (Capability.NOTE_EDIT_REPEAT))
                                    this.clip.updateRepeatVelocityCurve (channel, step, note, 0);
                                break;

                            case 6:
                                if (this.host.supports (Capability.NOTE_EDIT_REPEAT))
                                    this.clip.updateRepeatVelocityEnd (channel, step, note, 0);
                                break;

                            default:
                                return;
                        }
                        break;

                    case RECCURRENCE_PATTERN:
                        if (index == 7 && this.host.supports (Capability.NOTE_EDIT_RECCURRENCE))
                            this.clip.updateRecurrenceLength (channel, step, note, 1);
                        break;

                    default:
                        return;
                }
            }
        }

        if (isTouched)
            this.clip.startEdit (this.notes);
        else
            this.clip.stopEdit ();
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        for (final GridStep noteInfo: this.notes)
        {
            final int channel = noteInfo.channel ();
            final int step = noteInfo.step ();
            final int note = noteInfo.note ();

            switch (this.page)
            {
                case NOTE:
                    switch (index)
                    {
                        case 0:
                            this.clip.changeStepDuration (channel, step, note, value);
                            break;

                        case 1:
                            if (this.host.supports (Capability.NOTE_EDIT_MUTE))
                                this.clip.changeMuteState (channel, step, note, value);
                            break;

                        case 2:
                            this.clip.changeStepVelocity (channel, step, note, value);
                            break;

                        case 3:
                            if (this.host.supports (Capability.NOTE_EDIT_VELOCITY_SPREAD))
                                this.clip.changeVelocitySpread (channel, step, note, value);
                            break;

                        case 4:
                            if (this.host.supports (Capability.NOTE_EDIT_RELEASE_VELOCITY))
                                this.clip.changeStepReleaseVelocity (channel, step, note, value);
                            break;

                        case 5:
                            if (this.host.supports (Capability.NOTE_EDIT_CHANCE))
                                this.clip.changeChance (channel, step, note, value);
                            break;

                        case 6:
                            if (this.host.supports (Capability.NOTE_EDIT_OCCURRENCE))
                            {
                                final boolean increase = this.model.getValueChanger ().isIncrease (value);
                                this.clip.setPrevNextOccurrence (channel, step, note, increase);
                            }
                            break;

                        case 7:
                            if (this.host.supports (Capability.NOTE_EDIT_RECCURRENCE))
                                this.clip.changeRecurrenceLength (channel, step, note, value);
                            break;

                        default:
                            return;
                    }
                    break;

                case EXPRESSIONS:
                    switch (index)
                    {
                        case 0:
                            this.clip.changeStepDuration (channel, step, note, value);
                            break;

                        case 1:
                            if (this.host.supports (Capability.NOTE_EDIT_MUTE))
                                this.clip.changeMuteState (channel, step, note, value);
                            break;

                        case 3:
                            if (this.host.supports (Capability.NOTE_EDIT_EXPRESSIONS))
                                this.clip.changeStepGain (channel, step, note, value);
                            break;

                        case 4:
                            if (this.host.supports (Capability.NOTE_EDIT_EXPRESSIONS))
                                this.clip.changeStepPan (channel, step, note, value);
                            break;

                        case 5:
                            if (this.host.supports (Capability.NOTE_EDIT_EXPRESSIONS))
                                this.clip.changeStepTranspose (channel, step, note, value);
                            break;

                        case 6:
                            if (this.host.supports (Capability.NOTE_EDIT_EXPRESSIONS))
                                this.clip.changeStepTimbre (channel, step, note, value);
                            break;

                        case 7:
                            if (this.host.supports (Capability.NOTE_EDIT_EXPRESSIONS))
                                this.clip.changeStepPressure (channel, step, note, value);
                            break;

                        default:
                            return;
                    }
                    break;

                case REPEAT:
                    switch (index)
                    {
                        case 0:
                            this.clip.changeStepDuration (channel, step, note, value);
                            break;

                        case 1:
                            if (this.host.supports (Capability.NOTE_EDIT_MUTE))
                                this.clip.changeMuteState (channel, step, note, value);
                            break;

                        case 3:
                            if (this.host.supports (Capability.NOTE_EDIT_REPEAT))
                                this.clip.changeRepeatCount (channel, step, note, value);
                            break;

                        case 4:
                            if (this.host.supports (Capability.NOTE_EDIT_REPEAT))
                                this.clip.changeRepeatCurve (channel, step, note, value);
                            break;

                        case 5:
                            if (this.host.supports (Capability.NOTE_EDIT_REPEAT))
                                this.clip.changeRepeatVelocityCurve (channel, step, note, value);
                            break;

                        case 6:
                            if (this.host.supports (Capability.NOTE_EDIT_REPEAT))
                                this.clip.changeRepeatVelocityEnd (channel, step, note, value);
                            break;

                        default:
                            return;
                    }
                    break;

                case RECCURRENCE_PATTERN:
                    if (index == 7 && this.host.supports (Capability.NOTE_EDIT_RECCURRENCE))
                        this.clip.changeRecurrenceLength (channel, step, note, value);
                    break;
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 (final ITextDisplay display)
    {
        if (this.notes.isEmpty ())
        {
            display.setRow (1, "                     Please selecta note...                         ");
            return;
        }

        final GridStep noteInfo = this.notes.get (0);
        final int channel = noteInfo.channel ();
        final int step = noteInfo.step ();
        final int note = noteInfo.note ();

        final IStepInfo stepInfo = this.clip.getStep (channel, step, note);

        if (this.page != Page.RECCURRENCE_PATTERN)
        {
            display.setCell (0, 0, "Length").setCell (1, 0, this.formatLength (stepInfo.getDuration ()));

            if (stepInfo.isMuted ())
                display.setCell (2, 1, " MUTED");

            final int size = this.notes.size ();
            final boolean isOneNote = size == 1;
            display.setCell (3, 0, isOneNote ? "Step: " + (step + 1) : "Notes: " + size);
            display.setCell (3, 1, isOneNote ? Scales.formatNoteAndOctave (note, -3) : "*");
        }

        final IValueChanger valueChanger = this.model.getValueChanger ();

        switch (this.page)
        {
            case NOTE:
                display.setCell (0, 1, " COMMON:");

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
                    display.setCell (3, 5, stepInfo.isChanceEnabled () ? ON : OFF);
                }

                if (this.host.supports (Capability.NOTE_EDIT_OCCURRENCE))
                {
                    final NoteOccurrenceType occurrence = stepInfo.getOccurrence ();
                    display.setCell (0, 6, "Occurnce");
                    display.setCell (1, 6, StringUtils.optimizeName (occurrence.getName (), 8));
                    display.setCell (3, 6, stepInfo.isOccurrenceEnabled () ? ON : OFF);
                }

                if (this.host.supports (Capability.NOTE_EDIT_RECCURRENCE))
                {
                    final int recurrence = stepInfo.getRecurrenceLength ();
                    final String recurrenceStr = recurrence < 2 ? "Off" : Integer.toString (recurrence);
                    final int recurrenceVal = (recurrence - 1) * (this.model.getValueChanger ().getUpperBound () - 1) / 7;
                    display.setCell (0, 7, "Recurnce");
                    display.setCell (1, 7, recurrenceStr);
                    display.setCell (2, 7, recurrenceVal, Format.FORMAT_VALUE);
                    display.setCell (3, 7, stepInfo.isRecurrenceEnabled () ? ON : OFF);
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
                final String repeatCountValue = stepInfo.getFormattedRepeatCount ();
                final int rc = (repeatCount + 127) * (this.model.getValueChanger ().getUpperBound () - 1) / 254;
                display.setCell (0, 3, "Count");
                display.setCell (1, 3, repeatCountValue);
                display.setCell (2, 3, rc, Format.FORMAT_VALUE);
                display.setCell (3, 3, stepInfo.isRepeatEnabled () ? ON : OFF);

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
                    final boolean isOn = (mask & 1 << i) > 0;
                    String label = "   -";
                    if (i < recurrenceLength)
                    {
                        label = isOn ? ON : OFF;
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
        if (this.notes.isEmpty ())
        {
            for (int i = 0; i < 8; i++)
                display.addOptionElement (i == 2 ? "Please select a note to edit..." : "", "", false, "", "", false, true);
            return;
        }

        final GridStep noteInfo = this.notes.get (0);
        final int channel = noteInfo.channel ();
        final int step = noteInfo.step ();
        final int note = noteInfo.note ();

        final IStepInfo stepInfo = this.clip.getStep (channel, step, note);

        final IValueChanger valueChanger = this.model.getValueChanger ();

        if (this.page != Page.RECCURRENCE_PATTERN)
        {
            final int size = this.notes.size ();
            final boolean isOneNote = size == 1;

            final String stepBottomMenu = isOneNote ? "Step: " + (step + 1) : "Notes: " + size;
            display.addParameterElementWithPlainMenu (MENU[0], this.page == Page.NOTE, stepBottomMenu, null, false, "Length", -1, this.formatLength (stepInfo.getDuration ()), this.isKnobTouched[0], -1);
            final boolean hasExpressions = this.host.supports (Capability.NOTE_EDIT_EXPRESSIONS);

            final String topMenu = hasExpressions ? MENU[1] : " ";
            final boolean isTopMenuOn = hasExpressions && this.page == Page.EXPRESSIONS;
            final String bottomMenu = isOneNote ? Scales.formatNoteAndOctave (note, -3) : "*";
            if (this.host.supports (Capability.NOTE_EDIT_MUTE))
            {
                final int value = stepInfo.isMuted () ? valueChanger.getUpperBound () : 0;
                display.addParameterElementWithPlainMenu (topMenu, isTopMenuOn, bottomMenu, null, false, "Is Muted?", value, stepInfo.isMuted () ? "Yes" : "No", this.isKnobTouched[1], value);
            }
            else
                display.addParameterElementWithPlainMenu (topMenu, isTopMenuOn, bottomMenu, null, false, null, -1, null, false, -1);
        }

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
                final String repeatCountValue = stepInfo.getFormattedRepeatCount ();
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
                    final boolean isOn = (mask & 1 << i) > 0;
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
        if (this.notes.isEmpty ())
            return this.colorManager.getColorIndex (PushColorManager.PUSH_BLACK);

        for (final GridStep noteInfo: this.notes)
        {
            final int channel = noteInfo.channel ();
            final int step = noteInfo.step ();
            final int note = noteInfo.note ();

            final IStepInfo stepInfo = this.clip.getStep (channel, step, note);

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
                        final boolean isOn = (mask & 1 << index) > 0;
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

                if (index == 0 || index == 1 && this.host.supports (Capability.NOTE_EDIT_EXPRESSIONS))
                    return this.colorManager.getColorIndex (PushColorManager.PUSH_GREY_LO_2);
                if (index == 2 && this.host.supports (Capability.NOTE_EDIT_REPEAT) || index == 7 && this.host.supports (Capability.NOTE_EDIT_RECCURRENCE))
                    return this.colorManager.getColorIndex (PushColorManager.PUSH_GREY_LO_2);

                return this.colorManager.getColorIndex (PushColorManager.PUSH_BLACK_2);
            }
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