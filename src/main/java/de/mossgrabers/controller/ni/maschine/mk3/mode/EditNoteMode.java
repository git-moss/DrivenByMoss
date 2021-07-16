// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.mk3.mode;

import de.mossgrabers.controller.ni.maschine.mk3.controller.MaschineControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.daw.IStepInfo;
import de.mossgrabers.framework.daw.NoteOccurrenceType;
import de.mossgrabers.framework.daw.constants.Capability;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.StringUtils;

import java.util.Arrays;
import java.util.List;


/**
 * The edit note mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class EditNoteMode extends BaseMode
{
    /** The duration parameter. */
    public static final int            DURATION         = 1;
    /** The second (knob) duration parameter. */
    public static final int            DURATION2        = 2;
    /** The velocity parameter. */
    public static final int            VELOCITY         = 3;
    /** The velocity spread parameter. */
    public static final int            VELOCITY_SPREAD  = 4;
    /** The release velocity parameter. */
    public static final int            RELEASE_VELOCITY = 5;
    /** The chance parameter. */
    public static final int            CHANCE           = 6;
    /** The occurrence parameter. */
    public static final int            OCCURRENCE       = 7;

    /** The duration parameter. */
    public static final int            DURATION3        = 11;
    /** The second (knob) duration parameter. */
    public static final int            DURATION4        = 12;
    /** The gain parameter. */
    public static final int            GAIN             = 13;
    /** The panorama parameter. */
    public static final int            PANORAMA         = 14;
    /** The pitch parameter. */
    public static final int            PITCH            = 15;
    /** The timbre parameter. */
    public static final int            TIMBRE           = 16;
    /** The pressure parameter. */
    public static final int            PRESSURE         = 17;

    /** The duration parameter. */
    public static final int            DURATION5        = 21;
    /** The second (knob) duration parameter. */
    public static final int            DURATION6        = 22;
    /** The gain parameter. */
    public static final int            VELOCITY2        = 23;
    /** The panorama parameter. */
    public static final int            VELOCITY_END     = 24;
    /** The pitch parameter. */
    public static final int            VELOCITY_CURVE   = 25;
    /** The timbre parameter. */
    public static final int            COUNT            = 26;
    /** The pressure parameter. */
    public static final int            CURVE            = 27;

    private static final List<Integer> PARAMS           = Arrays.asList (Integer.valueOf (DURATION), Integer.valueOf (VELOCITY), Integer.valueOf (VELOCITY_SPREAD), Integer.valueOf (RELEASE_VELOCITY), Integer.valueOf (CHANCE), Integer.valueOf (OCCURRENCE), Integer.valueOf (DURATION3), Integer.valueOf (GAIN), Integer.valueOf (PANORAMA), Integer.valueOf (PITCH), Integer.valueOf (TIMBRE), Integer.valueOf (PRESSURE), Integer.valueOf (DURATION5), Integer.valueOf (VELOCITY2), Integer.valueOf (VELOCITY_END), Integer.valueOf (VELOCITY_CURVE), Integer.valueOf (COUNT), Integer.valueOf (CURVE));

    private static final int           PAGE_NOTE        = 0;
    private static final int           PAGE_EXPRESSIONS = 1;
    private static final int           PAGE_REPEAT      = 2;

    private final IHost                host;

    private INoteClip                  clip             = null;
    private int                        channel          = 0;
    private int                        step             = 0;
    private int                        note             = 60;

    private int                        selectedPage     = 0;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public EditNoteMode (final MaschineControlSurface surface, final IModel model)
    {
        super ("Edit note", surface, model);

        this.host = this.model.getHost ();
        this.selectedParam = DURATION;
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
    public void onKnobValue (final int index, final int value)
    {
        if (this.clip == null)
            return;

        final int idx = index < 0 ? this.selectedParam : index;

        final boolean hasMCUDisplay = this.surface.getMaschine ().hasMCUDisplay ();
        final IStepInfo stepInfo = this.clip.getStep (this.channel, this.step, this.note);

        switch (idx + 10 * this.selectedPage)
        {
            case DURATION:
            case DURATION2:
            case DURATION3:
            case DURATION4:
            case DURATION5:
            case DURATION6:
                this.clip.changeStepDuration (this.channel, this.step, this.note, value);
                if (!hasMCUDisplay)
                    this.mvHelper.delayDisplay ( () -> "Duration: " + StringUtils.formatMeasures (this.model.getTransport ().getQuartersPerMeasure (), stepInfo.getDuration (), 0, true));
                break;

            case VELOCITY:
            case VELOCITY2:
                this.clip.changeStepVelocity (this.channel, this.step, this.note, value);
                if (!hasMCUDisplay)
                    this.mvHelper.delayDisplay ( () -> "Velocity: " + StringUtils.formatPercentage (stepInfo.getVelocity ()));
                break;

            case GAIN:
                if (this.host.supports (Capability.NOTE_EDIT_EXPRESSIONS))
                {
                    this.clip.changeStepGain (this.channel, this.step, this.note, value);
                    if (!hasMCUDisplay)
                        this.mvHelper.delayDisplay ( () -> "Gain: " + StringUtils.formatPercentage (stepInfo.getGain ()));
                }
                break;

            case PANORAMA:
                if (this.host.supports (Capability.NOTE_EDIT_EXPRESSIONS))
                {
                    this.clip.changeStepPan (this.channel, this.step, this.note, value);
                    if (!hasMCUDisplay)
                        this.mvHelper.delayDisplay ( () -> "Panorama: " + StringUtils.formatPercentage (stepInfo.getPan () * 2.0 - 1.0));
                }
                break;

            case PITCH:
                if (this.host.supports (Capability.NOTE_EDIT_EXPRESSIONS))
                {
                    this.clip.changeStepTranspose (this.channel, this.step, this.note, value);
                    if (!hasMCUDisplay)
                        this.mvHelper.delayDisplay ( () -> "Pitch: " + String.format ("%.1f", Double.valueOf (stepInfo.getTranspose () * 48.0 - 24.0)));
                }
                break;

            case PRESSURE:
                if (this.host.supports (Capability.NOTE_EDIT_EXPRESSIONS))
                {
                    this.clip.changeStepPressure (this.channel, this.step, this.note, value);
                    if (!hasMCUDisplay)
                        this.mvHelper.delayDisplay ( () -> "Pressure: " + StringUtils.formatPercentage (stepInfo.getPressure ()));
                }
                break;

            case TIMBRE:
                if (this.host.supports (Capability.NOTE_EDIT_EXPRESSIONS))
                {
                    this.clip.changeStepTimbre (this.channel, this.step, this.note, value);
                    if (!hasMCUDisplay)
                        this.mvHelper.delayDisplay ( () -> "Timbre: " + StringUtils.formatPercentage (stepInfo.getTimbre ()));
                }
                break;

            case VELOCITY_SPREAD:
                if (this.host.supports (Capability.NOTE_EDIT_VELOCITY_SPREAD))
                {
                    this.clip.changeVelocitySpread (this.channel, this.step, this.note, value);
                    if (!hasMCUDisplay)
                        this.mvHelper.delayDisplay ( () -> "Velocity Spread: " + StringUtils.formatPercentage (stepInfo.getVelocitySpread ()));
                }
                break;

            case RELEASE_VELOCITY:
                if (this.host.supports (Capability.NOTE_EDIT_RELEASE_VELOCITY))
                {
                    this.clip.changeStepReleaseVelocity (this.channel, this.step, this.note, value);
                    if (!hasMCUDisplay)
                        this.mvHelper.delayDisplay ( () -> "Release Velocity: " + StringUtils.formatPercentage (stepInfo.getReleaseVelocity ()));
                }
                break;

            case CHANCE:
                if (this.host.supports (Capability.NOTE_EDIT_CHANCE))
                {
                    this.clip.changeChance (this.channel, this.step, this.note, value);
                    if (!hasMCUDisplay)
                        this.mvHelper.delayDisplay ( () -> "Chance: " + StringUtils.formatPercentage (stepInfo.getChance ()));
                }
                break;

            case OCCURRENCE:
                if (this.host.supports (Capability.NOTE_EDIT_OCCURRENCE))
                {
                    final boolean increase = this.model.getValueChanger ().isIncrease (value);
                    this.clip.setPrevNextOccurrence (this.channel, this.step, this.note, increase);
                    if (!hasMCUDisplay)
                        this.mvHelper.delayDisplay ( () -> "Occurrence: " + StringUtils.optimizeName (stepInfo.getOccurrence ().getName (), 8));
                }
                break;

            case VELOCITY_END:
                if (this.host.supports (Capability.NOTE_EDIT_REPEAT))
                {
                    this.clip.changeRepeatVelocityEnd (this.channel, this.step, this.note, value);
                    if (!hasMCUDisplay)
                        this.mvHelper.delayDisplay ( () -> "Velocity End: " + StringUtils.formatPercentage (stepInfo.getRepeatVelocityEnd ()));
                }
                break;

            case VELOCITY_CURVE:
                if (this.host.supports (Capability.NOTE_EDIT_REPEAT))
                {
                    this.clip.changeRepeatVelocityCurve (this.channel, this.step, this.note, value);
                    if (!hasMCUDisplay)
                        this.mvHelper.delayDisplay ( () -> "Velocity Curve: " + StringUtils.formatPercentage (stepInfo.getRepeatVelocityCurve ()));
                }
                break;

            case COUNT:
                if (this.host.supports (Capability.NOTE_EDIT_REPEAT))
                {
                    this.clip.changeRepeatCount (this.channel, this.step, this.note, value);
                    if (!hasMCUDisplay)
                        this.mvHelper.delayDisplay ( () -> "Repeat Count: " + stepInfo.getFormattedRepeatCount ());
                }
                break;

            case CURVE:
                if (this.host.supports (Capability.NOTE_EDIT_REPEAT))
                {
                    this.clip.changeRepeatCurve (this.channel, this.step, this.note, value);
                    if (!hasMCUDisplay)
                        this.mvHelper.delayDisplay ( () -> "Repeat Curve: " + StringUtils.formatPercentage (stepInfo.getRepeatCurve ()));
                }
                break;

            default:
                return;
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
            final int idx = index < 0 ? this.selectedParam : index;
            switch (idx + 10 * this.selectedPage)
            {
                case DURATION:
                case DURATION2:
                case DURATION3:
                case DURATION4:
                case DURATION5:
                case DURATION6:
                    this.clip.updateStepDuration (this.channel, this.step, this.note, 1.0);
                    break;

                case VELOCITY:
                case VELOCITY2:
                    this.clip.updateStepVelocity (this.channel, this.step, this.note, 1.0);
                    break;

                case GAIN:
                    if (this.host.supports (Capability.NOTE_EDIT_EXPRESSIONS))
                        this.clip.updateStepGain (this.channel, this.step, this.note, 0.5);
                    break;

                case PANORAMA:
                    if (this.host.supports (Capability.NOTE_EDIT_EXPRESSIONS))
                        this.clip.updateStepPan (this.channel, this.step, this.note, 0);
                    break;

                case PITCH:
                    if (this.host.supports (Capability.NOTE_EDIT_EXPRESSIONS))
                        this.clip.updateStepTranspose (this.channel, this.step, this.note, 0);
                    break;

                case PRESSURE:
                    if (this.host.supports (Capability.NOTE_EDIT_EXPRESSIONS))
                        this.clip.updateStepPressure (this.channel, this.step, this.note, 0);
                    break;

                case TIMBRE:
                    if (this.host.supports (Capability.NOTE_EDIT_EXPRESSIONS))
                        this.clip.updateStepTimbre (this.channel, this.step, this.note, 0);
                    break;

                case VELOCITY_SPREAD:
                    if (this.host.supports (Capability.NOTE_EDIT_VELOCITY_SPREAD))
                        this.clip.updateVelocitySpread (this.channel, this.step, this.note, 0);
                    break;

                case RELEASE_VELOCITY:
                    if (this.host.supports (Capability.NOTE_EDIT_RELEASE_VELOCITY))
                        this.clip.updateStepReleaseVelocity (this.channel, this.step, this.note, 1.0);
                    break;

                case CHANCE:
                    if (this.host.supports (Capability.NOTE_EDIT_CHANCE))
                        this.clip.updateChance (this.channel, this.step, this.note, 1.0);
                    break;

                case OCCURRENCE:
                    if (this.host.supports (Capability.NOTE_EDIT_OCCURRENCE))
                        this.clip.setOccurrence (this.channel, this.step, this.note, NoteOccurrenceType.ALWAYS);
                    break;

                case VELOCITY_END:
                    if (this.host.supports (Capability.NOTE_EDIT_REPEAT))
                        this.clip.updateRepeatVelocityEnd (this.channel, this.step, this.note, 0);
                    break;

                case VELOCITY_CURVE:
                    if (this.host.supports (Capability.NOTE_EDIT_REPEAT))
                        this.clip.updateRepeatVelocityCurve (this.channel, this.step, this.note, 0);
                    break;

                case COUNT:
                    if (this.host.supports (Capability.NOTE_EDIT_REPEAT))
                        this.clip.updateRepeatCount (this.channel, this.step, this.note, 0);
                    break;

                case CURVE:
                    if (this.host.supports (Capability.NOTE_EDIT_REPEAT))
                        this.clip.updateRepeatCurve (this.channel, this.step, this.note, 0);
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
    public void updateDisplay ()
    {
        final ITextDisplay d = this.surface.getTextDisplay ().clear ();

        final IStepInfo stepInfo = this.clip == null ? null : this.clip.getStep (this.channel, this.step, this.note);

        d.setCell (0, 0, "Note").setCell (1, 0, stepInfo == null ? "-" : (Integer.toString (this.step + 1) + ":" + Scales.formatNoteAndOctave (this.note, -3)));

        d.setCell (0, 1, this.mark ("Length", DURATION + this.selectedPage * 10));
        if (stepInfo == null)
            d.setCell (1, 2, "-");
        else
        {
            final String [] formatLength = this.formatLength (stepInfo.getDuration ()).split (":");
            d.setCell (1, 1, formatLength[0]);
            d.setCell (1, 2, ":" + formatLength[1]);
        }

        switch (this.selectedPage)
        {
            default:
            case PAGE_NOTE:
                d.setCell (0, 3, this.mark ("Velocity", VELOCITY)).setCell (1, 3, stepInfo == null ? "-" : StringUtils.formatPercentage (stepInfo.getVelocity ()));

                if (this.host.supports (Capability.NOTE_EDIT_VELOCITY_SPREAD))
                    d.setCell (0, 4, this.mark ("Spread", VELOCITY_SPREAD)).setCell (1, 4, stepInfo == null ? "-" : StringUtils.formatPercentage (stepInfo.getVelocitySpread ()));

                if (this.host.supports (Capability.NOTE_EDIT_RELEASE_VELOCITY))
                    d.setCell (0, 5, this.mark ("R-Vel", RELEASE_VELOCITY)).setCell (1, 5, stepInfo == null ? "-" : StringUtils.formatPercentage (stepInfo.getReleaseVelocity ()));

                if (this.host.supports (Capability.NOTE_EDIT_CHANCE))
                    d.setCell (0, 6, this.mark ("Chance", CHANCE)).setCell (1, 6, stepInfo == null ? "-" : StringUtils.formatPercentage (stepInfo.getChance ()));

                if (this.host.supports (Capability.NOTE_EDIT_OCCURRENCE))
                    d.setCell (0, 7, this.mark ("Occurnce", OCCURRENCE)).setCell (1, 7, stepInfo == null ? "-" : StringUtils.optimizeName (stepInfo.getOccurrence ().getName (), 8));

                break;

            case PAGE_EXPRESSIONS:
                d.setCell (0, 3, this.mark ("Gain", GAIN)).setCell (1, 3, stepInfo == null ? "-" : StringUtils.formatPercentage (stepInfo.getGain ()));
                d.setCell (0, 4, this.mark ("Pan", PANORAMA)).setCell (1, 4, stepInfo == null ? "-" : StringUtils.formatPercentage (stepInfo.getPan ()));
                d.setCell (0, 5, this.mark ("Pitch", PITCH)).setCell (1, 5, stepInfo == null ? "-" : String.format ("%.1f", Double.valueOf (stepInfo.getTranspose ())));
                d.setCell (0, 6, this.mark ("Timbre", TIMBRE)).setCell (1, 6, stepInfo == null ? "-" : StringUtils.formatPercentage (stepInfo.getTimbre ()));
                d.setCell (0, 7, this.mark ("Pressure", PRESSURE)).setCell (1, 7, stepInfo == null ? "-" : StringUtils.formatPercentage (stepInfo.getPressure ()));
                break;

            case PAGE_REPEAT:
                d.setCell (0, 3, this.mark ("Velocity", VELOCITY2)).setCell (1, 3, stepInfo == null ? "-" : StringUtils.formatPercentage (stepInfo.getVelocity ()));
                d.setCell (0, 4, this.mark ("V-End", VELOCITY_END)).setCell (1, 4, stepInfo == null ? "-" : StringUtils.formatPercentage (stepInfo.getRepeatVelocityEnd ()));
                d.setCell (0, 5, this.mark ("V-Curve", VELOCITY_CURVE)).setCell (1, 5, stepInfo == null ? "-" : StringUtils.formatPercentage (stepInfo.getRepeatVelocityCurve ()));
                d.setCell (0, 6, this.mark ("Count", COUNT)).setCell (1, 6, stepInfo == null ? "-" : stepInfo.getFormattedRepeatCount ());
                d.setCell (0, 7, this.mark ("Curve", CURVE)).setCell (1, 7, stepInfo == null ? "-" : StringUtils.formatPercentage (stepInfo.getRepeatCurve ()));
                break;
        }

        d.allDone ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItem ()
    {
        this.selectedParam = PARAMS.get (Math.min (PARAMS.size () - 1, PARAMS.indexOf (Integer.valueOf (this.selectedParam)) + 1)).intValue ();
        this.selectedPage = this.selectedParam / 10;
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItem ()
    {
        this.selectedParam = PARAMS.get (Math.max (0, PARAMS.indexOf (Integer.valueOf (this.selectedParam)) - 1)).intValue ();
        this.selectedPage = this.selectedParam / 10;
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasPreviousItem ()
    {
        return this.selectedParam > 2;
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasNextItem ()
    {
        return this.selectedParam < (this.host.supports (Capability.NOTE_EDIT_EXPRESSIONS) ? CURVE : GAIN);
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItemPage ()
    {
        if (this.host.supports (Capability.NOTE_EDIT_EXPRESSIONS))
        {
            this.selectedPage = Math.max (0, this.selectedPage - 1);
            this.selectedParam = this.selectedParam % 10 + this.selectedPage * 10;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItemPage ()
    {
        if (this.host.supports (Capability.NOTE_EDIT_EXPRESSIONS))
        {
            this.selectedPage = Math.min (PAGE_REPEAT, this.selectedPage + 1);
            this.selectedParam = this.selectedParam % 10 + this.selectedPage * 10;
        }
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasPreviousItemPage ()
    {
        return this.host.supports (Capability.NOTE_EDIT_EXPRESSIONS) && this.selectedPage > 0;
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasNextItemPage ()
    {
        return this.host.supports (Capability.NOTE_EDIT_EXPRESSIONS) && this.selectedPage < PAGE_REPEAT;
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