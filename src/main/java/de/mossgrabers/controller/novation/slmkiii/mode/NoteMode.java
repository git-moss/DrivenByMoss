// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.slmkiii.mode;

import de.mossgrabers.controller.novation.slmkiii.controller.SLMkIIIColorManager;
import de.mossgrabers.controller.novation.slmkiii.controller.SLMkIIIControlSurface;
import de.mossgrabers.controller.novation.slmkiii.controller.SLMkIIIDisplay;
import de.mossgrabers.framework.controller.ButtonID;
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
        RECCURRENCE_PATTERN,
        EXPRESSIONS,
        REPEAT
    }


    private final IHost          host;
    private Page                 page      = Page.NOTE;
    private INoteClip            clip      = null;
    private final List<GridStep> notes     = new ArrayList<> ();
    private boolean              started   = false;
    private final Object         startLock = new Object ();


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public NoteMode (final SLMkIIIControlSurface surface, final IModel model)
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
    public void onButton (final int row, final int index, final ButtonEvent event)
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
                        case 1:
                            if (this.host.supports (Capability.NOTE_EDIT_EXPRESSIONS))
                                this.page = Page.EXPRESSIONS;
                            break;

                        case 2:
                            if (this.host.supports (Capability.NOTE_EDIT_REPEAT))
                                this.page = Page.REPEAT;
                            break;

                        case 3:
                            if (this.host.supports (Capability.NOTE_EDIT_RECCURRENCE))
                                this.page = Page.RECCURRENCE_PATTERN;
                            break;

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
                    switch (index)
                    {
                        case 0:
                            this.page = Page.NOTE;
                            break;

                        case 2:
                            this.page = Page.REPEAT;
                            break;

                        case 3:
                            this.page = Page.RECCURRENCE_PATTERN;
                            break;

                        default:
                            return;
                    }
                    break;

                case REPEAT:
                    switch (index)
                    {
                        case 0:
                            this.page = Page.NOTE;
                            break;

                        case 1:
                            this.page = Page.EXPRESSIONS;
                            break;

                        case 3:
                            this.page = Page.RECCURRENCE_PATTERN;
                            break;

                        case 4:
                            if (this.host.supports (Capability.NOTE_EDIT_REPEAT))
                                this.clip.updateIsRepeatEnabled (channel, step, note, !stepInfo.isRepeatEnabled ());
                            break;

                        default:
                            return;
                    }
                    break;

                case RECCURRENCE_PATTERN:
                    switch (index)
                    {
                        case 0:
                            this.page = Page.NOTE;
                            break;

                        case 1:
                            this.page = Page.EXPRESSIONS;
                            break;

                        case 2:
                            this.page = Page.REPEAT;
                            break;

                        case 3:
                            this.page = Page.RECCURRENCE_PATTERN;
                            break;

                        default:
                            return;
                    }
                    break;
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        if (this.notes.isEmpty ())
            return;

        if (this.surface.isDeletePressed ())
        {
            this.handleDelete (index);
            return;
        }

        synchronized (this.startLock)
        {
            if (!this.started)
            {
                this.clip.startEdit (this.notes);
                this.started = true;
            }
        }

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

                        case 4:
                            if (this.host.supports (Capability.NOTE_EDIT_REPEAT))
                                this.clip.changeRepeatCount (channel, step, note, value);
                            break;

                        case 5:
                            if (this.host.supports (Capability.NOTE_EDIT_REPEAT))
                                this.clip.changeRepeatCurve (channel, step, note, value);
                            break;

                        case 6:
                            if (this.host.supports (Capability.NOTE_EDIT_REPEAT))
                                this.clip.changeRepeatVelocityCurve (channel, step, note, value);
                            break;

                        case 7:
                            if (this.host.supports (Capability.NOTE_EDIT_REPEAT))
                                this.clip.changeRepeatVelocityEnd (channel, step, note, value);
                            break;

                        default:
                            return;
                    }
                    break;

                case RECCURRENCE_PATTERN:
                    if (this.host.supports (Capability.NOTE_EDIT_RECCURRENCE))
                    {
                        final IStepInfo stepInfo = this.clip.getStep (channel, step, note);
                        int mask = stepInfo.getRecurrenceMask ();
                        final int bitVal = 1 << index;
                        if (this.model.getValueChanger ().isIncrease (value))
                            mask |= bitVal;
                        else
                            mask &= ~bitVal;
                        this.clip.updateRecurrenceMask (channel, step, note, mask);
                    }
                    break;
            }
        }

        // Ugly workaround for knobs not having touch support...
        final long lastValueChange = System.currentTimeMillis ();

        this.host.scheduleTask ( () -> {

            if (System.currentTimeMillis () - lastValueChange > 400)
            {
                synchronized (this.startLock)
                {
                    if (this.started)
                    {
                        this.started = false;
                        this.clip.stopEdit ();
                    }
                }
            }

        }, 400);
    }


    /** {@inheritDoc} */
    @Override
    public int getKnobValue (final int index)
    {
        if (this.notes.isEmpty ())
            return 0;

        final IValueChanger valueChanger = this.model.getValueChanger ();

        final GridStep noteInfo = this.notes.get (0);
        final int channel = noteInfo.channel ();
        final int step = noteInfo.step ();
        final int note = noteInfo.note ();
        final IStepInfo stepInfo = this.clip.getStep (channel, step, note);

        switch (this.page)
        {
            case NOTE:
                switch (index)
                {
                    case 2:
                        return valueChanger.fromNormalizedValue (stepInfo.getVelocity ());

                    case 3:
                        if (this.host.supports (Capability.NOTE_EDIT_VELOCITY_SPREAD))
                            return valueChanger.fromNormalizedValue (stepInfo.getVelocitySpread ());
                        break;

                    case 4:
                        if (this.host.supports (Capability.NOTE_EDIT_RELEASE_VELOCITY))
                            return valueChanger.fromNormalizedValue (stepInfo.getReleaseVelocity ());
                        break;

                    case 5:
                        if (this.host.supports (Capability.NOTE_EDIT_CHANCE))
                            return valueChanger.fromNormalizedValue (stepInfo.getChance ());
                        break;

                    case 6:
                        if (this.host.supports (Capability.NOTE_EDIT_OCCURRENCE))
                            return 0;
                        break;

                    case 7:
                        if (this.host.supports (Capability.NOTE_EDIT_RECCURRENCE))
                            return (stepInfo.getRecurrenceLength () - 1) * (this.model.getValueChanger ().getUpperBound () - 1) / 7;
                        break;

                    default:
                        return 0;
                }
                break;

            case EXPRESSIONS:
                switch (index)
                {
                    case 3:
                        if (this.host.supports (Capability.NOTE_EDIT_EXPRESSIONS))
                            return Math.min (valueChanger.getUpperBound () - 1, valueChanger.fromNormalizedValue (stepInfo.getGain ()));
                        break;

                    case 4:
                        if (this.host.supports (Capability.NOTE_EDIT_EXPRESSIONS))
                            return valueChanger.fromNormalizedValue ((stepInfo.getPan () + 1.0) / 2.0);
                        break;

                    case 5:
                        if (this.host.supports (Capability.NOTE_EDIT_EXPRESSIONS))
                            return valueChanger.fromNormalizedValue ((stepInfo.getTranspose () + 24.0) / 48.0);
                        break;

                    case 6:
                        if (this.host.supports (Capability.NOTE_EDIT_EXPRESSIONS))
                            return valueChanger.fromNormalizedValue ((stepInfo.getTimbre () + 1.0) / 2.0);
                        break;

                    case 7:
                        if (this.host.supports (Capability.NOTE_EDIT_EXPRESSIONS))
                            return valueChanger.fromNormalizedValue (stepInfo.getPressure ());
                        break;

                    default:
                        break;
                }
                break;

            case REPEAT:
                switch (index)
                {
                    case 4:
                        if (this.host.supports (Capability.NOTE_EDIT_REPEAT))
                            return (stepInfo.getRepeatCount () + 127) * (this.model.getValueChanger ().getUpperBound () - 1) / 254;
                        break;

                    case 5:
                        if (this.host.supports (Capability.NOTE_EDIT_REPEAT))
                            return valueChanger.fromNormalizedValue ((stepInfo.getRepeatCurve () + 1.0) / 2.0);
                        break;

                    case 6:
                        if (this.host.supports (Capability.NOTE_EDIT_REPEAT))
                            return valueChanger.fromNormalizedValue ((stepInfo.getRepeatVelocityCurve () + 1.0) / 2.0);
                        break;

                    case 7:
                        if (this.host.supports (Capability.NOTE_EDIT_REPEAT))
                            return valueChanger.fromNormalizedValue ((stepInfo.getRepeatVelocityEnd () + 1.0) / 2.0);
                        break;

                    default:
                        break;
                }
                break;

            case RECCURRENCE_PATTERN:
                // None
                break;
        }

        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        if (this.notes.isEmpty ())
            return SLMkIIIColorManager.SLMKIII_BLACK;

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
                        if (index == 0)
                            return SLMkIIIColorManager.SLMKIII_GREEN;
                        if (index == 5 && this.host.supports (Capability.NOTE_EDIT_CHANCE))
                            return stepInfo.isChanceEnabled () ? SLMkIIIColorManager.SLMKIII_AMBER : SLMkIIIColorManager.SLMKIII_AMBER_HALF;
                        if (index == 6 && this.host.supports (Capability.NOTE_EDIT_OCCURRENCE))
                            return stepInfo.isOccurrenceEnabled () ? SLMkIIIColorManager.SLMKIII_AMBER : SLMkIIIColorManager.SLMKIII_AMBER_HALF;
                        if (index == 7 && this.host.supports (Capability.NOTE_EDIT_RECCURRENCE))
                            return stepInfo.isRecurrenceEnabled () ? SLMkIIIColorManager.SLMKIII_AMBER : SLMkIIIColorManager.SLMKIII_AMBER_HALF;
                        break;

                    case EXPRESSIONS:
                        if (index == 1)
                            return SLMkIIIColorManager.SLMKIII_GREEN;
                        break;

                    case REPEAT:
                        if (index == 2)
                            return SLMkIIIColorManager.SLMKIII_GREEN;
                        if (index == 4)
                            return stepInfo.isRepeatEnabled () ? SLMkIIIColorManager.SLMKIII_AMBER : SLMkIIIColorManager.SLMKIII_AMBER_HALF;
                        break;

                    case RECCURRENCE_PATTERN:
                        if (index == 3)
                            return SLMkIIIColorManager.SLMKIII_GREEN;
                        break;
                }

                if (index < 4 && this.host.supports (Capability.NOTE_EDIT_EXPRESSIONS))
                    return SLMkIIIColorManager.SLMKIII_GREY;

                return SLMkIIIColorManager.SLMKIII_BLACK;
            }

            index = this.isButtonRow (1, buttonID);
            if (index >= 0)
            {
                switch (this.page)
                {
                    case NOTE:
                        if (index == 0)
                            return SLMkIIIColorManager.SLMKIII_GREEN_GRASS;
                        break;

                    case EXPRESSIONS:
                        if (index == 1)
                            return SLMkIIIColorManager.SLMKIII_GREEN_GRASS;
                        break;

                    case REPEAT:
                        if (index == 2)
                            return SLMkIIIColorManager.SLMKIII_GREEN_GRASS;
                        break;

                    case RECCURRENCE_PATTERN:
                        if (index == 7)
                            return SLMkIIIColorManager.SLMKIII_GREEN_GRASS;
                        break;
                }

                if (index == 0 || index == 1 && this.host.supports (Capability.NOTE_EDIT_EXPRESSIONS))
                    return SLMkIIIColorManager.SLMKIII_DARK_GREY;
                if (index == 2 && this.host.supports (Capability.NOTE_EDIT_REPEAT) || index == 7 && this.host.supports (Capability.NOTE_EDIT_RECCURRENCE))
                    return SLMkIIIColorManager.SLMKIII_DARK_GREY;

                return SLMkIIIColorManager.SLMKIII_BLACK;
            }
        }

        return SLMkIIIColorManager.SLMKIII_BLACK;
    }


    private void handleDelete (final int index)
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

                        case 4:
                            if (this.host.supports (Capability.NOTE_EDIT_REPEAT))
                                this.clip.updateRepeatCount (channel, step, note, 0);
                            break;

                        case 5:
                            if (this.host.supports (Capability.NOTE_EDIT_REPEAT))
                                this.clip.updateRepeatCurve (channel, step, note, 0);
                            break;

                        case 6:
                            if (this.host.supports (Capability.NOTE_EDIT_REPEAT))
                                this.clip.updateRepeatVelocityCurve (channel, step, note, 0);
                            break;

                        case 7:
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


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final SLMkIIIDisplay d = this.surface.getDisplay ();
        d.clear ();
        d.setCell (0, 8, "Note Edit");

        if (this.notes.isEmpty ())
        {
            d.setBlock (1, 1, " Please  select a").setBlock (1, 2, "note.");
            d.setCell (1, 8, "");
            d.hideAllElements ();
            for (int i = 0; i < 8; i++)
                d.setPropertyColor (i, 2, SLMkIIIColorManager.SLMKIII_BLACK);
        }
        else
        {
            final GridStep noteInfo = this.notes.get (0);
            final int channel = noteInfo.channel ();
            final int step = noteInfo.step ();
            final int note = noteInfo.note ();

            final IStepInfo stepInfo = this.clip.getStep (channel, step, note);

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

                final int size = this.notes.size ();
                final boolean isOneNote = size == 1;
                d.setCell (2, 0, isOneNote ? "Step: " + (step + 1) : "Notes: " + size);
                d.setCell (2, 1, isOneNote ? Scales.formatNoteAndOctave (note, -3) : "*");
            }

            switch (this.page)
            {
                case NOTE:
                    d.setCell (1, 8, "Common");
                    d.setPropertyValue (0, 1, 1);

                    setColor (d, 2, true);
                    d.setCell (0, 2, "Velocity");
                    d.setCell (1, 2, StringUtils.formatPercentage (stepInfo.getVelocity ()));
                    d.setPropertyColor (2, 2, SLMkIIIColorManager.SLMKIII_BLACK);

                    final boolean supportsVelocitySpread = this.host.supports (Capability.NOTE_EDIT_VELOCITY_SPREAD);
                    setColor (d, 3, supportsVelocitySpread);
                    d.setPropertyColor (3, 2, SLMkIIIColorManager.SLMKIII_BLACK);
                    if (supportsVelocitySpread)
                    {
                        d.setCell (0, 3, "V-Spread");
                        d.setCell (1, 3, StringUtils.formatPercentage (stepInfo.getVelocitySpread ()));
                    }

                    final boolean supportsReleaseVelocity = this.host.supports (Capability.NOTE_EDIT_RELEASE_VELOCITY);
                    setColor (d, 4, supportsReleaseVelocity);
                    d.setPropertyColor (4, 2, SLMkIIIColorManager.SLMKIII_BLACK);
                    if (supportsReleaseVelocity)
                    {
                        d.setCell (0, 4, "R-Velcty");
                        d.setCell (1, 4, StringUtils.formatPercentage (stepInfo.getReleaseVelocity ()));
                    }

                    final boolean supportsChance = this.host.supports (Capability.NOTE_EDIT_CHANCE);
                    setColor (d, 5, supportsChance);
                    if (supportsChance)
                    {
                        d.setCell (0, 5, "Chance");
                        d.setCell (1, 5, StringUtils.formatPercentage (stepInfo.getChance ()));

                        d.setCell (3, 5, stepInfo.isChanceEnabled () ? ON : OFF);
                        d.setPropertyColor (5, 2, SLMkIIIColorManager.SLMKIII_ROSE);
                        d.setPropertyValue (5, 1, stepInfo.isChanceEnabled () ? 1 : 0);
                    }

                    final boolean supportsOccurrence = this.host.supports (Capability.NOTE_EDIT_OCCURRENCE);
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

                    final boolean supportsRecurrence = this.host.supports (Capability.NOTE_EDIT_RECCURRENCE);
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
                    if (this.host.supports (Capability.NOTE_EDIT_EXPRESSIONS))
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

                    for (int i = 3; i < 8; i++)
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

                        d.setPropertyColor (i, 0, active ? SLMkIIIColorManager.SLMKIII_ROSE : SLMkIIIColorManager.SLMKIII_BLACK);
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
    public List<GridStep> getNotes ()
    {
        return this.notes;
    }
}