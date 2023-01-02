// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.slmkiii.mode;

import de.mossgrabers.controller.novation.slmkiii.controller.SLMkIIIColorManager;
import de.mossgrabers.controller.novation.slmkiii.controller.SLMkIIIControlSurface;
import de.mossgrabers.controller.novation.slmkiii.controller.SLMkIIIDisplay;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.daw.clip.IStepInfo;
import de.mossgrabers.framework.daw.clip.NotePosition;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.daw.data.empty.EmptyParameter;
import de.mossgrabers.framework.mode.INoteMode;
import de.mossgrabers.framework.mode.NoteEditor;
import de.mossgrabers.framework.parameter.NoteAttribute;
import de.mossgrabers.framework.parameter.NoteParameter;
import de.mossgrabers.framework.parameterprovider.IParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.FixedParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.ResetParameterProvider;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.StringUtils;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;


/**
 * Mode for editing notes.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class NoteMode extends BaseMode<IItem> implements INoteMode
{
    private static final String OFF = "  Off";
    private static final String ON  = "   On";


    private enum Page
    {
        NOTE,
        EXPRESSIONS,
        REPEAT,
        RECCURRENCE_PATTERN
    }


    private final IHost                         host;
    private Page                                page               = Page.NOTE;
    private final NoteEditor                    noteEditor;
    private final Map<Page, IParameterProvider> pageParamProviders = new EnumMap<> (Page.class);


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public NoteMode (final SLMkIIIControlSurface surface, final IModel model)
    {
        super ("Note", surface, model);

        this.setControls (DEFAULT_KNOB_IDS);

        this.host = this.model.getHost ();
        this.noteEditor = new NoteEditor ();

        final IValueChanger valueChanger = model.getValueChanger ();
        final NoteParameter durationParameter = new NoteParameter (NoteAttribute.DURATION, null, model, this, valueChanger);
        final NoteParameter muteParameter = new NoteParameter (NoteAttribute.MUTE, null, model, this, valueChanger);

        final NoteParameter recurrenceParameter = new NoteParameter (NoteAttribute.RECURRENCE_LENGTH, null, model, this, valueChanger);
        this.pageParamProviders.put (Page.NOTE, new FixedParameterProvider (
                // Duration
                durationParameter,
                // Mute
                muteParameter,
                // Velocity
                new NoteParameter (NoteAttribute.VELOCITY, null, model, this, valueChanger),
                // Velocity Spread
                new NoteParameter (NoteAttribute.VELOCITY_SPREAD, null, model, this, valueChanger),
                // Release Velocity
                new NoteParameter (NoteAttribute.RELEASE_VELOCITY, null, model, this, valueChanger),
                // Chance
                new NoteParameter (NoteAttribute.CHANCE, null, model, this, valueChanger),
                // Occurrence
                new NoteParameter (NoteAttribute.OCCURRENCE, null, model, this, valueChanger),
                // Recurrence
                recurrenceParameter));

        this.pageParamProviders.put (Page.EXPRESSIONS, new FixedParameterProvider (
                // Duration
                durationParameter,
                // Mute
                muteParameter,
                // -
                EmptyParameter.INSTANCE,
                // Gain
                new NoteParameter (NoteAttribute.GAIN, null, model, this, valueChanger),
                // Panorama
                new NoteParameter (NoteAttribute.PANORAMA, null, model, this, valueChanger),
                // Transpose
                new NoteParameter (NoteAttribute.TRANSPOSE, null, model, this, valueChanger),
                // Timbre
                new NoteParameter (NoteAttribute.TIMBRE, null, model, this, valueChanger),
                // Pressure
                new NoteParameter (NoteAttribute.PRESSURE, null, model, this, valueChanger)));

        this.pageParamProviders.put (Page.REPEAT, new FixedParameterProvider (
                // Duration
                durationParameter,
                // Mute
                muteParameter,
                // -
                EmptyParameter.INSTANCE,
                // -
                EmptyParameter.INSTANCE,
                // Repeat
                new NoteParameter (NoteAttribute.REPEAT, null, model, this, valueChanger),
                // Repeat Curve
                new NoteParameter (NoteAttribute.REPEAT_CURVE, null, model, this, valueChanger),
                // Repeat Velocity Curve
                new NoteParameter (NoteAttribute.REPEAT_VELOCITY_CURVE, null, model, this, valueChanger),
                // Repeat Velocity End
                new NoteParameter (NoteAttribute.REPEAT_VELOCITY_END, null, model, this, valueChanger)));

        this.pageParamProviders.put (Page.RECCURRENCE_PATTERN, new FixedParameterProvider (
                // Recurrence Length
                recurrenceParameter,
                // Recurrence Length
                recurrenceParameter,
                // Recurrence Length
                recurrenceParameter,
                // Recurrence Length
                recurrenceParameter,
                // Recurrence Length
                recurrenceParameter,
                // Recurrence Length
                recurrenceParameter,
                // Recurrence Length
                recurrenceParameter,
                // Recurrence Length
                recurrenceParameter));

        this.rebind ();
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;

        final List<NotePosition> notes = this.noteEditor.getNotes ();
        final INoteClip clip = this.noteEditor.getClip ();

        if (this.surface.isShiftPressed () && this.page == Page.RECCURRENCE_PATTERN)
        {
            for (final NotePosition notePosition: notes)
                clip.updateStepRecurrenceMaskToggleBit (notePosition, index);
            return;
        }

        if (index < 4)
        {
            switch (index)
            {
                case 0:
                    this.page = Page.NOTE;
                    break;

                case 1:
                    if (this.host.supports (NoteAttribute.TIMBRE))
                        this.page = Page.EXPRESSIONS;
                    break;

                case 2:
                    if (this.host.supports (NoteAttribute.REPEAT))
                        this.page = Page.REPEAT;
                    break;

                case 3:
                    if (this.host.supports (NoteAttribute.RECURRENCE_LENGTH))
                        this.page = Page.RECCURRENCE_PATTERN;
                    break;

                default:
                    // Never reached
                    break;
            }
            this.rebind ();
            return;
        }

        for (final NotePosition notePosition: notes)
        {
            final IStepInfo stepInfo = clip.getStep (notePosition);

            if (this.page == Page.NOTE)
            {
                switch (index)
                {
                    case 5:
                        if (this.host.supports (NoteAttribute.CHANCE))
                            clip.updateStepIsChanceEnabled (notePosition, !stepInfo.isChanceEnabled ());
                        break;

                    case 6:
                        if (this.host.supports (NoteAttribute.OCCURRENCE))
                            clip.updateStepIsOccurrenceEnabled (notePosition, !stepInfo.isOccurrenceEnabled ());
                        break;

                    case 7:
                        if (this.host.supports (NoteAttribute.RECURRENCE_LENGTH))
                            clip.updateStepIsRecurrenceEnabled (notePosition, !stepInfo.isRecurrenceEnabled ());
                        break;

                    default:
                        return;
                }
            }
            else if (this.page == Page.REPEAT && index == 4 && this.host.supports (NoteAttribute.REPEAT))
                clip.updateStepIsRepeatEnabled (notePosition, !stepInfo.isRepeatEnabled ());
        }
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        final List<NotePosition> notes = this.noteEditor.getNotes ();
        if (notes.isEmpty ())
            return SLMkIIIColorManager.SLMKIII_BLACK;

        final int index = this.isButtonRow (0, buttonID);
        if (index >= 0)
        {
            final IStepInfo stepInfo = this.noteEditor.getClip ().getStep (notes.get (0));

            if (this.surface.isShiftPressed ())
            {
                final int recurrenceLength = stepInfo.getRecurrenceLength ();
                final int mask = stepInfo.getRecurrenceMask ();
                final boolean isOn = (mask & 1 << index) > 0;
                final boolean active = index < recurrenceLength;
                int color = SLMkIIIColorManager.SLMKIII_BLACK;
                if (active)
                    color = isOn ? SLMkIIIColorManager.SLMKIII_AMBER : SLMkIIIColorManager.SLMKIII_GREY;
                return color;
            }

            if (index < 4)
            {
                if (index == this.page.ordinal ())
                    return SLMkIIIColorManager.SLMKIII_GREEN;
                if (this.host.supports (NoteAttribute.TIMBRE))
                    return SLMkIIIColorManager.SLMKIII_GREY;
                return SLMkIIIColorManager.SLMKIII_BLACK;
            }

            if (this.page == Page.NOTE)
            {
                if (index == 5 && this.host.supports (NoteAttribute.CHANCE))
                    return stepInfo.isChanceEnabled () ? SLMkIIIColorManager.SLMKIII_AMBER : SLMkIIIColorManager.SLMKIII_AMBER_HALF;
                if (index == 6 && this.host.supports (NoteAttribute.OCCURRENCE))
                    return stepInfo.isOccurrenceEnabled () ? SLMkIIIColorManager.SLMKIII_AMBER : SLMkIIIColorManager.SLMKIII_AMBER_HALF;
                if (index == 7 && this.host.supports (NoteAttribute.RECURRENCE_LENGTH))
                    return stepInfo.isRecurrenceEnabled () ? SLMkIIIColorManager.SLMKIII_AMBER : SLMkIIIColorManager.SLMKIII_AMBER_HALF;
            }
            else if (this.page == Page.REPEAT && index == 4)
                return stepInfo.isRepeatEnabled () ? SLMkIIIColorManager.SLMKIII_AMBER : SLMkIIIColorManager.SLMKIII_AMBER_HALF;
        }

        return SLMkIIIColorManager.SLMKIII_BLACK;

    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final SLMkIIIDisplay d = this.surface.getDisplay ();
        d.clear ();
        d.setCell (0, 8, "Note Edit");

        final List<NotePosition> notes = this.noteEditor.getNotes ();
        if (notes.isEmpty ())
        {
            d.setBlock (1, 1, " Please  select a").setBlock (1, 2, "note.");
            d.setCell (1, 8, "");
            d.hideAllElements ();
            for (int i = 0; i < 8; i++)
                d.setPropertyColor (i, 2, SLMkIIIColorManager.SLMKIII_BLACK);
        }
        else
        {
            final NotePosition notePosition = notes.get (0);
            final IStepInfo stepInfo = this.noteEditor.getClip ().getStep (notePosition);

            if (this.page != Page.RECCURRENCE_PATTERN)
            {
                d.setCell (0, 0, "Length").setCell (1, 0, this.formatLength (stepInfo.getDuration ()));

                d.setCell (0, 1, " Mute");
                if (stepInfo.isMuted ())
                    d.setCell (1, 1, " MUTED");
                d.setPropertyColor (0, 0, SLMkIIIColorManager.SLMKIII_ROSE);
                d.setPropertyColor (0, 1, SLMkIIIColorManager.SLMKIII_BLACK);
                d.setPropertyColor (1, 0, SLMkIIIColorManager.SLMKIII_ROSE);
                d.setPropertyColor (1, 1, SLMkIIIColorManager.SLMKIII_BLACK);
            }

            final int size = notes.size ();
            final boolean isOneNote = size == 1;
            d.setCell (2, 0, isOneNote ? "Step: " + (notePosition.getStep () + 1) : "Notes: " + size);
            d.setCell (2, 1, isOneNote ? Scales.formatNoteAndOctave (notePosition.getNote (), -3) : "*");

            switch (this.page)
            {
                case NOTE:
                    d.setCell (1, 8, "Common");
                    d.setPropertyValue (0, 1, 1);

                    setColor (d, 2, true);
                    d.setCell (0, 2, "Velocity");
                    d.setCell (1, 2, StringUtils.formatPercentage (stepInfo.getVelocity ()));
                    d.setPropertyColor (2, 2, SLMkIIIColorManager.SLMKIII_BLACK);

                    final boolean supportsVelocitySpread = this.host.supports (NoteAttribute.VELOCITY_SPREAD);
                    setColor (d, 3, supportsVelocitySpread);
                    d.setPropertyColor (3, 2, SLMkIIIColorManager.SLMKIII_BLACK);
                    if (supportsVelocitySpread)
                    {
                        d.setCell (0, 3, "V-Spread");
                        d.setCell (1, 3, StringUtils.formatPercentage (stepInfo.getVelocitySpread ()));
                    }

                    final boolean supportsReleaseVelocity = this.host.supports (NoteAttribute.RELEASE_VELOCITY);
                    setColor (d, 4, supportsReleaseVelocity);
                    d.setPropertyColor (4, 2, SLMkIIIColorManager.SLMKIII_BLACK);
                    if (supportsReleaseVelocity)
                    {
                        d.setCell (0, 4, "R-Velcty");
                        d.setCell (1, 4, StringUtils.formatPercentage (stepInfo.getReleaseVelocity ()));
                    }

                    final boolean supportsChance = this.host.supports (NoteAttribute.CHANCE);
                    setColor (d, 5, supportsChance);
                    if (supportsChance)
                    {
                        d.setCell (0, 5, "Chance");
                        d.setCell (1, 5, StringUtils.formatPercentage (stepInfo.getChance ()));

                        d.setCell (3, 5, stepInfo.isChanceEnabled () ? ON : OFF);
                        d.setPropertyColor (5, 2, SLMkIIIColorManager.SLMKIII_ROSE);
                        d.setPropertyValue (5, 1, stepInfo.isChanceEnabled () ? 1 : 0);
                    }

                    final boolean supportsOccurrence = this.host.supports (NoteAttribute.OCCURRENCE);
                    d.setPropertyColor (6, 0, supportsOccurrence ? SLMkIIIColorManager.SLMKIII_ROSE : SLMkIIIColorManager.SLMKIII_BLACK);
                    d.setPropertyColor (6, 1, SLMkIIIColorManager.SLMKIII_BLACK);
                    if (supportsOccurrence)
                    {
                        d.setCell (0, 6, "Occurnce");
                        d.setCell (1, 6, StringUtils.optimizeName (stepInfo.getOccurrence ().getName (), 8));

                        d.setCell (3, 6, stepInfo.isOccurrenceEnabled () ? ON : OFF);
                        d.setPropertyColor (6, 2, SLMkIIIColorManager.SLMKIII_ROSE);
                        d.setPropertyValue (6, 1, stepInfo.isOccurrenceEnabled () ? 1 : 0);
                    }

                    final boolean supportsRecurrence = this.host.supports (NoteAttribute.RECURRENCE_LENGTH);
                    setColor (d, 7, supportsRecurrence);
                    if (supportsRecurrence)
                    {
                        final int recurrence = stepInfo.getRecurrenceLength ();
                        final String recurrenceStr = recurrence < 2 ? "Off" : Integer.toString (recurrence);
                        d.setCell (0, 7, "Recurnce");
                        d.setCell (1, 7, recurrenceStr);

                        d.setCell (3, 7, stepInfo.isRecurrenceEnabled () ? ON : OFF);
                        d.setPropertyColor (7, 2, SLMkIIIColorManager.SLMKIII_ROSE);
                        d.setPropertyValue (7, 1, stepInfo.isRecurrenceEnabled () ? 1 : 0);
                    }
                    break;

                case EXPRESSIONS:
                    if (this.host.supports (NoteAttribute.TIMBRE))
                    {
                        for (int i = 3; i < 8; i++)
                            d.setPropertyColor (i, 2, SLMkIIIColorManager.SLMKIII_BLACK);

                        d.setCell (1, 8, "Expressions");
                        setColor (d, 2, false);

                        setColor (d, 3, true);
                        d.setCell (0, 3, "Gain").setCell (1, 3, StringUtils.formatPercentage (stepInfo.getGain ()));

                        setColor (d, 4, true);
                        d.setCell (0, 4, "Pan").setCell (1, 4, StringUtils.formatPercentage (stepInfo.getPan ()));

                        setColor (d, 5, true);
                        d.setCell (0, 5, "Pitch").setCell (1, 5, String.format ("%.1f", Double.valueOf (stepInfo.getTranspose ())));

                        setColor (d, 6, true);
                        d.setCell (0, 6, "Timbre").setCell (1, 6, StringUtils.formatPercentage (stepInfo.getTimbre ()));

                        setColor (d, 7, true);
                        d.setCell (0, 7, "Pressure").setCell (1, 7, StringUtils.formatPercentage (stepInfo.getPressure ()));
                    }
                    break;

                case REPEAT:
                    final int repeatColor = stepInfo.isRepeatEnabled () ? SLMkIIIColorManager.SLMKIII_AMBER : SLMkIIIColorManager.SLMKIII_AMBER_HALF;
                    for (int i = 3; i < 8; i++)
                        d.setPropertyColor (i, 2, i == 4 ? repeatColor : SLMkIIIColorManager.SLMKIII_BLACK);

                    d.setCell (1, 8, "Repeat");
                    setColor (d, 2, false);
                    setColor (d, 3, false);

                    setColor (d, 4, true);
                    d.setCell (0, 4, "Count");
                    d.setCell (1, 4, stepInfo.getFormattedRepeatCount ());
                    d.setCell (3, 4, stepInfo.isRepeatEnabled () ? ON : OFF);

                    final double repeatCurve = stepInfo.getRepeatCurve ();
                    setColor (d, 5, true);
                    d.setCell (0, 5, "Curve");
                    d.setCell (1, 5, StringUtils.formatPercentage (repeatCurve));

                    final double repeatVelocityCurve = stepInfo.getRepeatVelocityCurve ();
                    setColor (d, 6, true);
                    d.setCell (0, 6, "Vel-Crve");
                    d.setCell (1, 6, StringUtils.formatPercentage (repeatVelocityCurve));

                    final double repeatVelocityEnd = stepInfo.getRepeatVelocityEnd ();
                    setColor (d, 7, true);
                    d.setCell (0, 7, "Vel. End");
                    d.setCell (1, 7, StringUtils.formatPercentage (repeatVelocityEnd));
                    break;

                case RECCURRENCE_PATTERN:
                    d.setCell (1, 8, "Pattern");

                    for (int i = 4; i < 8; i++)
                        d.setPropertyColor (i, 2, SLMkIIIColorManager.SLMKIII_BLACK);

                    final int recurrenceLength = stepInfo.getRecurrenceLength ();
                    final int mask = stepInfo.getRecurrenceMask ();
                    for (int i = 0; i < 8; i++)
                    {
                        final boolean isOn = (mask & 1 << i) > 0;
                        String label = "   -";
                        final boolean active = i < recurrenceLength;
                        if (active)
                            label = i + 1 + ": " + (isOn ? "On" : "Off");
                        d.setCell (0, i, label);

                        int color = SLMkIIIColorManager.SLMKIII_BLACK;
                        if (active)
                            color = isOn ? SLMkIIIColorManager.SLMKIII_ROSE : SLMkIIIColorManager.SLMKIII_GREY;
                        d.setPropertyColor (i, 0, color);
                        d.setPropertyColor (i, 1, SLMkIIIColorManager.SLMKIII_BLACK);
                    }
                    break;
            }
        }

        this.setButtonInfo (d);
        d.allDone ();
    }


    /** {@inheritDoc} */
    @Override
    public int getModeColor ()
    {
        return SLMkIIIColorManager.SLMKIII_ROSE;
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


    private static void setColor (final SLMkIIIDisplay display, final int index, final boolean isOn)
    {
        final int color = isOn ? SLMkIIIColorManager.SLMKIII_ROSE : SLMkIIIColorManager.SLMKIII_BLACK;
        display.setPropertyColor (index, 0, color);
        display.setPropertyColor (index, 1, color);
    }


    /** {@inheritDoc} */
    @Override
    public void clearNotes ()
    {
        this.noteEditor.clearNotes ();
    }


    /** {@inheritDoc} */
    @Override
    public void setNote (final INoteClip clip, final NotePosition notePosition)
    {
        this.noteEditor.setNote (clip, notePosition);
    }


    /** {@inheritDoc} */
    @Override
    public void addNote (final INoteClip clip, final NotePosition notePosition)
    {
        this.noteEditor.addNote (clip, notePosition);
    }


    /** {@inheritDoc} */
    @Override
    public INoteClip getClip ()
    {
        return this.noteEditor.getClip ();
    }


    /** {@inheritDoc} */
    @Override
    public List<NotePosition> getNotes ()
    {
        return this.noteEditor.getNotes ();
    }


    /** {@inheritDoc} */
    @Override
    public List<NotePosition> getNotePosition (final int parameterIndex)
    {
        return this.noteEditor.getNotePosition (parameterIndex);
    }


    private void rebind ()
    {
        final IParameterProvider parameterProvider = this.pageParamProviders.get (this.page);
        this.setParameterProvider (parameterProvider);
        this.setParameterProvider (ButtonID.DELETE, new ResetParameterProvider (parameterProvider));
        this.bindControls ();
    }
}