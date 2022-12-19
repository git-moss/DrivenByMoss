// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.parameter;

import de.mossgrabers.framework.controller.display.IDisplay;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.daw.clip.IStepInfo;
import de.mossgrabers.framework.daw.clip.NoteOccurrenceType;
import de.mossgrabers.framework.daw.clip.NotePosition;
import de.mossgrabers.framework.daw.clip.StepState;
import de.mossgrabers.framework.mode.INoteMode;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.StringUtils;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;


/**
 * A parameter implementation for editing a parameter of a note.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class NoteParameter extends AbstractParameterImpl
{
    private final IHost                             host;
    private final ITransport                        transport;
    private final INoteMode                         callback;
    private final IDisplay                          display;
    private final Scales                            scales;
    private final NoteAttribute                     noteAttribute;
    private final int                               parameterIndex;

    private static final Map<NoteAttribute, String> ATTRIBUTE_NAMES = new EnumMap<> (NoteAttribute.class);
    static
    {
        ATTRIBUTE_NAMES.put (NoteAttribute.PITCH, "Pitch");
        ATTRIBUTE_NAMES.put (NoteAttribute.GAIN, "Gain");
        ATTRIBUTE_NAMES.put (NoteAttribute.PANORAMA, "Panorama");
        ATTRIBUTE_NAMES.put (NoteAttribute.DURATION, "Duration");
        ATTRIBUTE_NAMES.put (NoteAttribute.VELOCITY, "Velocity");
        ATTRIBUTE_NAMES.put (NoteAttribute.RELEASE_VELOCITY, "Release Velocity");
        ATTRIBUTE_NAMES.put (NoteAttribute.VELOCITY_SPREAD, "Velocity Spread");
        ATTRIBUTE_NAMES.put (NoteAttribute.MUTE, "Mute");
        ATTRIBUTE_NAMES.put (NoteAttribute.PRESSURE, "Pressure");
        ATTRIBUTE_NAMES.put (NoteAttribute.TIMBRE, "Timbre");
        ATTRIBUTE_NAMES.put (NoteAttribute.TRANSPOSE, "Transpose");
        ATTRIBUTE_NAMES.put (NoteAttribute.CHANCE, "Chance");
        ATTRIBUTE_NAMES.put (NoteAttribute.REPEAT, "Repeat");
        ATTRIBUTE_NAMES.put (NoteAttribute.REPEAT_CURVE, "Repeat Curve");
        ATTRIBUTE_NAMES.put (NoteAttribute.REPEAT_VELOCITY_CURVE, "Repeat Velocity Curve");
        ATTRIBUTE_NAMES.put (NoteAttribute.REPEAT_VELOCITY_END, "Repeat Velocity End");
        ATTRIBUTE_NAMES.put (NoteAttribute.OCCURRENCE, "Occurrence");
        ATTRIBUTE_NAMES.put (NoteAttribute.RECURRENCE_LENGTH, "Recurrence");
    }


    /**
     * Constructor. The parameter index is ignored.
     *
     * @param noteAttribute The initial note attribute to edit with the parameter
     * @param display The display for notifications, might be null
     * @param model The transport
     * @param callback Callback to get further information about the note to edit
     * @param valueChanger The value changer
     */
    public NoteParameter (final NoteAttribute noteAttribute, final IDisplay display, final IModel model, final INoteMode callback, final IValueChanger valueChanger)
    {
        this (-1, noteAttribute, display, model, callback, valueChanger);
    }


    /**
     * Constructor.
     *
     * @param parameterIndex The index for identifying the parameter
     * @param noteAttribute The initial note attribute to edit with the parameter
     * @param display The display for notifications, might be null
     * @param model The transport
     * @param callback Callback to get further information about the note to edit
     * @param valueChanger The value changer
     */
    public NoteParameter (final int parameterIndex, final NoteAttribute noteAttribute, final IDisplay display, final IModel model, final INoteMode callback, final IValueChanger valueChanger)
    {
        super (valueChanger, 0);

        this.parameterIndex = parameterIndex;
        this.noteAttribute = noteAttribute;
        this.display = display;
        this.host = model.getHost ();
        this.transport = model.getTransport ();
        this.scales = model.getScales ();
        this.callback = callback;
    }


    /** {@inheritDoc} */
    @Override
    public int getValue ()
    {
        final INoteClip clip = this.callback.getClip ();
        final List<NotePosition> notePositions = this.callback.getNotePosition (this.parameterIndex);
        if (notePositions.isEmpty ())
            return 0;

        final NotePosition notePosition = notePositions.get (0);
        if (notePosition.getNote () == -1)
            return 0;
        final IStepInfo stepInfo = clip.getStep (notePosition);
        if ((stepInfo.getState () == StepState.OFF) || !this.host.supports (this.noteAttribute))
            return 0;

        double normalizedValue = 0;
        switch (this.noteAttribute)
        {
            case PITCH:
                normalizedValue = notePosition.getNote () / 127.0;
                break;

            case GAIN:
                normalizedValue = stepInfo.getGain ();
                break;

            case PANORAMA:
                normalizedValue = (stepInfo.getPan () + 1.0) / 2.0;
                break;

            case DURATION:
                normalizedValue = stepInfo.getDuration ();
                break;

            case VELOCITY:
                normalizedValue = stepInfo.getVelocity ();
                break;

            case RELEASE_VELOCITY:
                normalizedValue = stepInfo.getReleaseVelocity ();
                break;

            case VELOCITY_SPREAD:
                normalizedValue = stepInfo.getVelocitySpread ();
                break;

            case MUTE:
                normalizedValue = stepInfo.isMuted () ? 1 : 0;
                break;

            case PRESSURE:
                normalizedValue = stepInfo.getPressure ();
                break;

            case TIMBRE:
                normalizedValue = stepInfo.getTimbre ();
                break;

            case TRANSPOSE:
                normalizedValue = (stepInfo.getTranspose () + 24.0) / 48.0;
                break;

            case CHANCE:
                normalizedValue = stepInfo.getChance ();
                break;

            case REPEAT:
                normalizedValue = (stepInfo.getRepeatCount () + 127.0) / 254.0;
                break;

            case REPEAT_CURVE:
                normalizedValue = stepInfo.getRepeatCurve ();
                break;

            case REPEAT_VELOCITY_CURVE:
                normalizedValue = stepInfo.getRepeatVelocityCurve ();
                break;

            case REPEAT_VELOCITY_END:
                normalizedValue = (stepInfo.getRepeatVelocityEnd () + 1) / 2.0;
                break;

            case OCCURRENCE:
                final int occurrence = stepInfo.getOccurrence ().ordinal ();
                normalizedValue = occurrence / (double) NoteOccurrenceType.values ().length;
                break;

            case RECURRENCE_LENGTH:
                final int recurrence = stepInfo.getRecurrenceLength () - 1;
                normalizedValue = recurrence / (double) 7;
                break;

            default:
                break;
        }

        return this.valueChanger.fromNormalizedValue (normalizedValue);
    }


    /** {@inheritDoc} */
    @Override
    public void setValue (final IValueChanger valueChanger, final int value)
    {
        this.setNormalizedValue (valueChanger.toNormalizedValue (value));
    }


    /** {@inheritDoc} */
    @Override
    public void setNormalizedValue (final double normalizedValue)
    {
        if (!this.host.supports (this.noteAttribute))
            return;

        final INoteClip clip = this.callback.getClip ();
        for (final NotePosition notePosition: this.callback.getNotePosition (this.parameterIndex))
        {
            final IStepInfo stepInfo = clip.getStep (notePosition);
            if (stepInfo.getState () == StepState.OFF)
                return;

            switch (this.noteAttribute)
            {
                case PITCH:
                    // Move (transpose) the note up and down
                    int newNote = (int) Math.round (normalizedValue * 126);
                    if (!this.scales.isChromatic ())
                        newNote = this.scales.getNearestNoteInScale (newNote);
                    clip.moveStepY (notePosition, newNote);
                    this.notify ("Note: %s", Scales.formatNoteAndOctave (newNote, -3));
                    break;

                case GAIN:
                    clip.updateStepGain (notePosition, normalizedValue);
                    this.notify ("Gain: %s", StringUtils.formatPercentage (normalizedValue));
                    break;

                case PANORAMA:
                    final double pan = normalizedValue * 2.0 - 1.0;
                    clip.updateStepPan (notePosition, pan);
                    this.notify ("Panorama: %s", StringUtils.formatPercentage (pan));
                    break;

                case DURATION:
                    // Since this is an absolute knob but the duration is unlimited, we limit it to
                    // 4 bars (128 x 32th). Furthermore, make sure that the length is exactly on a
                    // 32th
                    final double duration = Math.round (normalizedValue * 16.0 / 0.125) * 0.125;
                    clip.updateStepDuration (notePosition, duration);
                    this.notify ("Duration: %s", this.formatLength (duration));
                    break;

                case VELOCITY:
                    clip.updateStepVelocity (notePosition, normalizedValue);
                    this.notify ("Velocity: %s", StringUtils.formatPercentage (normalizedValue));
                    break;

                case RELEASE_VELOCITY:
                    clip.updateStepReleaseVelocity (notePosition, normalizedValue);
                    this.notify ("Release Velocity: %s", StringUtils.formatPercentage (normalizedValue));
                    break;

                case VELOCITY_SPREAD:
                    clip.updateStepVelocitySpread (notePosition, normalizedValue);
                    this.notify ("Velocity Spread: %d%%", Integer.valueOf ((int) Math.round (normalizedValue * 100)));
                    break;

                case MUTE:
                    final boolean isMuted = normalizedValue > 0.5;
                    clip.updateStepMuteState (notePosition, isMuted);
                    this.notify ("Muted: %s", isMuted ? "Yes" : "No");
                    break;

                case PRESSURE:
                    clip.updateStepPressure (notePosition, normalizedValue);
                    this.notify ("Pressure: %s", StringUtils.formatPercentage (normalizedValue));
                    break;

                case TIMBRE:
                    clip.updateStepTimbre (notePosition, normalizedValue);
                    this.notify ("Timbre: %s", StringUtils.formatPercentage (normalizedValue));
                    break;

                case TRANSPOSE:
                    final double v = normalizedValue * 48.0 - 24.0;
                    clip.updateStepTranspose (notePosition, v);
                    this.delayedNotify ("Pitch: %s", () -> String.format ("%.1f", Double.valueOf (v)));
                    break;

                case CHANCE:
                    clip.updateStepChance (notePosition, normalizedValue);
                    this.notify ("Chance: %s", StringUtils.formatPercentage (stepInfo.getChance ()));
                    break;

                case REPEAT:
                    // The range is limited to 1/16 to 16 to make it controllable with an
                    // absolute knob!
                    clip.updateStepRepeatCount (notePosition, (int) Math.round ((normalizedValue - 0.5) * 30));
                    this.notify ("Repeat: %s", stepInfo.getFormattedRepeatCount ());
                    break;

                case REPEAT_CURVE:
                    throw new UnsupportedOperationException ();

                case REPEAT_VELOCITY_CURVE:
                    throw new UnsupportedOperationException ();

                case REPEAT_VELOCITY_END:
                    throw new UnsupportedOperationException ();

                case OCCURRENCE:
                    throw new UnsupportedOperationException ();

                case RECURRENCE_LENGTH:
                    throw new UnsupportedOperationException ();
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void changeValue (final IValueChanger valueChanger, final int value)
    {
        if (!this.host.supports (this.noteAttribute))
            return;

        final INoteClip clip = this.callback.getClip ();
        for (final NotePosition notePosition: this.callback.getNotePosition (this.parameterIndex))
        {
            final IStepInfo stepInfo = clip.getStep (notePosition);
            if (stepInfo.getState () == StepState.OFF)
                return;

            switch (this.noteAttribute)
            {
                case PITCH:
                    // Move (transpose) the note up and down
                    final int offset = this.valueChanger.isIncrease (value) ? 1 : -1;
                    int newNote = notePosition.getNote ();
                    final boolean isChromatic = this.scales.isChromatic ();
                    do
                    {
                        newNote += offset;
                        // No more scale notes found?
                        if (newNote < 0 || newNote > 127)
                            return;
                    } while (!isChromatic && !this.scales.isInScale (this.scales.toNoteInOctave (newNote)));

                    clip.moveStepY (notePosition, newNote);
                    this.notify ("Note: %s", Scales.formatNoteAndOctave (newNote, -3));
                    break;

                case GAIN:
                    clip.changeStepGain (notePosition, value);
                    this.delayedNotify ("Gain: %s", () -> StringUtils.formatPercentage (stepInfo.getGain ()));
                    break;

                case PANORAMA:
                    clip.changeStepPan (notePosition, value);
                    this.delayedNotify ("Pan: %s", () -> StringUtils.formatPercentage (stepInfo.getPan ()));
                    break;

                case DURATION:
                    clip.changeStepDuration (notePosition, value);
                    this.delayedNotify ("Duration: %s", () -> this.formatLength (stepInfo.getDuration ()));
                    break;

                case VELOCITY:
                    clip.changeStepVelocity (notePosition, value);
                    this.delayedNotify ("Velocity: %s", () -> StringUtils.formatPercentage (stepInfo.getVelocity ()));
                    break;

                case RELEASE_VELOCITY:
                    clip.changeStepReleaseVelocity (notePosition, value);
                    this.delayedNotify ("Release Velocity: %s", () -> StringUtils.formatPercentage (stepInfo.getReleaseVelocity ()));
                    break;

                case VELOCITY_SPREAD:
                    clip.changeStepVelocitySpread (notePosition, value);
                    this.delayedNotify ("Vel. Spread: %s", () -> StringUtils.formatPercentage (stepInfo.getVelocitySpread ()));
                    break;

                case MUTE:
                    clip.changeStepMuteState (notePosition, value);
                    this.delayedNotify ("Mute: %s", () -> stepInfo.isMuted () ? "Yes" : "No");
                    break;

                case PRESSURE:
                    clip.changeStepPressure (notePosition, value);
                    this.delayedNotify ("Pressure: %s", () -> StringUtils.formatPercentage (stepInfo.getPressure ()));
                    break;

                case TIMBRE:
                    clip.changeStepTimbre (notePosition, value);
                    this.delayedNotify ("Timbre: %s", () -> StringUtils.formatPercentage (stepInfo.getTimbre ()));
                    break;

                case TRANSPOSE:
                    clip.changeStepTranspose (notePosition, value);
                    this.delayedNotify ("Pitch: %s", () -> String.format ("%.1f", Double.valueOf (stepInfo.getTranspose ())));
                    break;

                case CHANCE:
                    clip.changeStepChance (notePosition, value);
                    this.delayedNotify ("Chance: %s", () -> StringUtils.formatPercentage (stepInfo.getChance ()));
                    break;

                case REPEAT:
                    clip.changeStepRepeatCount (notePosition, value);
                    this.delayedNotify ("Repeat: %s", stepInfo::getFormattedRepeatCount);
                    break;

                case REPEAT_CURVE:
                    clip.changeStepRepeatCurve (notePosition, value);
                    this.delayedNotify ("Curve: %s", () -> StringUtils.formatPercentage (stepInfo.getRepeatCurve ()));
                    break;

                case REPEAT_VELOCITY_CURVE:
                    clip.changeStepRepeatVelocityCurve (notePosition, value);
                    this.delayedNotify ("Vel-Crve: %s", () -> StringUtils.formatPercentage (stepInfo.getRepeatVelocityCurve ()));
                    break;

                case REPEAT_VELOCITY_END:
                    clip.changeStepRepeatVelocityEnd (notePosition, value);
                    this.delayedNotify ("Vel. End: %s", () -> StringUtils.formatPercentage (stepInfo.getRepeatVelocityEnd ()));
                    break;

                case OCCURRENCE:
                    final boolean increase = this.valueChanger.isIncrease (value);
                    clip.setStepPrevNextOccurrence (notePosition, increase);
                    this.delayedNotify ("Occurrence: %s", () -> stepInfo.getOccurrence ().getName ());
                    break;

                case RECURRENCE_LENGTH:
                    clip.changeStepRecurrenceLength (notePosition, value);
                    final int recurrence = stepInfo.getRecurrenceLength ();
                    this.delayedNotify ("Recurrence: %s", () -> recurrence < 2 ? "Off" : Integer.toString (recurrence));
                    break;
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void setValueImmediatly (final int value)
    {
        this.setValue (value);
    }


    /** {@inheritDoc} */
    @Override
    public void inc (final double increment)
    {
        throw new UnsupportedOperationException ();
    }


    /** {@inheritDoc} */
    @Override
    public void resetValue ()
    {
        if (!this.host.supports (this.noteAttribute))
            return;

        final INoteClip clip = this.callback.getClip ();
        for (final NotePosition notePosition: this.callback.getNotePosition (this.parameterIndex))
        {
            final IStepInfo stepInfo = clip.getStep (notePosition);
            if (stepInfo.getState () == StepState.OFF)
                return;

            switch (this.noteAttribute)
            {
                case PITCH:
                    final int newNote = 60 + this.scales.getScaleOffset ();
                    clip.moveStepY (notePosition, newNote);
                    this.notify ("Note: %s", Scales.formatNoteAndOctave (newNote, -3));
                    break;

                case GAIN:
                    clip.updateStepGain (notePosition, 0.5);
                    break;

                case PANORAMA:
                    clip.updateStepPan (notePosition, 0);
                    break;

                case DURATION:
                    clip.updateStepDuration (notePosition, 1.0);
                    break;

                case VELOCITY:
                    clip.updateStepVelocity (notePosition, 1.0);
                    break;

                case RELEASE_VELOCITY:
                    clip.updateStepReleaseVelocity (notePosition, 1.0);
                    break;

                case VELOCITY_SPREAD:
                    clip.updateStepVelocitySpread (notePosition, 0);
                    break;

                case MUTE:
                    clip.updateStepMuteState (notePosition, false);
                    break;

                case PRESSURE:
                    clip.updateStepPressure (notePosition, 0);
                    break;

                case TIMBRE:
                    clip.updateStepTimbre (notePosition, 0);
                    break;

                case TRANSPOSE:
                    clip.updateStepTranspose (notePosition, 0);
                    break;

                case CHANCE:
                    clip.updateStepChance (notePosition, 1.0);
                    break;

                case REPEAT:
                    clip.updateStepRepeatCount (notePosition, 0);
                    break;

                case REPEAT_CURVE:
                    clip.updateStepRepeatCurve (notePosition, 0);
                    break;

                case REPEAT_VELOCITY_CURVE:
                    clip.updateStepRepeatVelocityCurve (notePosition, 0);
                    break;

                case REPEAT_VELOCITY_END:
                    clip.updateStepRepeatVelocityEnd (notePosition, 0);
                    break;

                case OCCURRENCE:
                    clip.setStepOccurrence (notePosition, NoteOccurrenceType.ALWAYS);
                    break;

                case RECURRENCE_LENGTH:
                    clip.updateStepRecurrenceLength (notePosition, 1);
                    break;
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public String getDisplayedValue ()
    {
        if (!this.host.supports (this.noteAttribute))
            return "";

        final INoteClip clip = this.callback.getClip ();
        final List<NotePosition> notePositions = this.callback.getNotePosition (this.parameterIndex);
        if (notePositions.isEmpty ())
            return "";

        final NotePosition notePosition = notePositions.get (0);

        final IStepInfo stepInfo = clip.getStep (notePosition);
        if (stepInfo.getState () == StepState.OFF)
            return "";

        switch (this.noteAttribute)
        {
            case PITCH:
                return Scales.formatNoteAndOctave (notePosition.getNote (), -3);

            case GAIN:
                return StringUtils.formatPercentage (stepInfo.getGain ());

            case PANORAMA:
                return StringUtils.formatPercentage (stepInfo.getPan ());

            case DURATION:
                return this.formatLength (stepInfo.getDuration ());

            case VELOCITY:
                return StringUtils.formatPercentage (stepInfo.getVelocity ());

            case RELEASE_VELOCITY:
                return StringUtils.formatPercentage (stepInfo.getReleaseVelocity ());

            case VELOCITY_SPREAD:
                return StringUtils.formatPercentage (stepInfo.getVelocitySpread ());

            case MUTE:
                return String.format ("Mute: %s", stepInfo.isMuted () ? "Yes" : "No");

            case PRESSURE:
                return StringUtils.formatPercentage (stepInfo.getPressure ());

            case TIMBRE:
                return StringUtils.formatPercentage (stepInfo.getTimbre ());

            case TRANSPOSE:
                return String.format ("%.1f", Double.valueOf (stepInfo.getTranspose ()));

            case CHANCE:
                return StringUtils.formatPercentage (stepInfo.getChance ());

            case REPEAT:
                return stepInfo.getFormattedRepeatCount ();

            case REPEAT_CURVE:
                return StringUtils.formatPercentage (stepInfo.getRepeatCurve ());

            case REPEAT_VELOCITY_CURVE:
                return StringUtils.formatPercentage (stepInfo.getRepeatVelocityCurve ());

            case REPEAT_VELOCITY_END:
                return StringUtils.formatPercentage (stepInfo.getRepeatVelocityEnd ());

            case OCCURRENCE:
                return stepInfo.getOccurrence ().getName ();

            case RECURRENCE_LENGTH:
                final int recurrence = stepInfo.getRecurrenceLength ();
                return recurrence < 2 ? "Off" : Integer.toString (recurrence);
        }

        return "";
    }


    /** {@inheritDoc} */
    @Override
    public boolean doesExist ()
    {
        return !this.callback.getNotePosition (this.parameterIndex).isEmpty ();
    }


    /** {@inheritDoc} */
    @Override
    public String getName ()
    {
        return ATTRIBUTE_NAMES.get (this.noteAttribute);
    }


    private void notify (final String format, final Object... args)
    {
        if (this.display != null)
            this.display.notify (String.format (format, args));
    }


    private void delayedNotify (final String format, final Supplier<String> supplier)
    {
        if (this.display != null)
            this.host.scheduleTask ( () -> this.display.notify (String.format (format, supplier.get ())), 10);
    }


    private String formatLength (final double duration)
    {
        return StringUtils.formatMeasures (this.transport.getQuartersPerMeasure (), duration, 0, true);
    }
}
