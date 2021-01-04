// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.maschine.mode;

import de.mossgrabers.controller.maschine.controller.MaschineControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.daw.IStepInfo;
import de.mossgrabers.framework.daw.constants.Capability;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * The edit note mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class EditNoteMode extends BaseMode
{
    /** The duration parameter. */
    public static final int DURATION  = 2;
    /** The velocity parameter. */
    public static final int VELOCITY  = 3;
    /** The gain parameter. */
    public static final int GAIN      = 4;
    /** The panorama parameter. */
    public static final int PANORAMA  = 5;
    /** The transpose parameter. */
    public static final int TRANSPOSE = 6;
    /** The pressure parameter. */
    public static final int PRESSURE  = 7;

    private final IHost     host;

    private INoteClip       clip      = null;
    private int             channel   = 0;
    private int             step      = 0;
    private int             note      = 60;


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

        switch (idx)
        {
            case DURATION:
                this.clip.changeStepDuration (this.channel, this.step, this.note, value);
                if (!hasMCUDisplay)
                    this.mvHelper.delayDisplay ( () -> "Duration: " + StringUtils.formatMeasures (this.model.getTransport ().getQuartersPerMeasure (), stepInfo.getDuration (), 0, true));
                break;

            case VELOCITY:
                this.clip.changeStepVelocity (this.channel, this.step, this.note, value);
                if (!hasMCUDisplay)
                    this.mvHelper.delayDisplay ( () -> "Velocity: " + StringUtils.formatPercentage (stepInfo.getVelocity ()));
                break;

            case GAIN:
                if (this.host.supports (Capability.NOTE_EDIT_GAIN))
                {
                    this.clip.changeStepGain (this.channel, this.step, this.note, value);
                    if (!hasMCUDisplay)
                        this.mvHelper.delayDisplay ( () -> "Gain: " + StringUtils.formatPercentage (stepInfo.getGain ()));
                }
                break;

            case PANORAMA:
                if (this.host.supports (Capability.NOTE_EDIT_PANORAMA))
                {
                    this.clip.changeStepPan (this.channel, this.step, this.note, value);
                    if (!hasMCUDisplay)
                        this.mvHelper.delayDisplay ( () -> "Panorama: " + StringUtils.formatPercentage (stepInfo.getPan () * 2.0 - 1.0));
                }
                break;

            case TRANSPOSE:
                if (this.host.supports (Capability.NOTE_EDIT_TRANSPOSE))
                {
                    this.clip.changeStepTranspose (this.channel, this.step, this.note, value);
                    if (!hasMCUDisplay)
                        this.mvHelper.delayDisplay ( () -> "Pitch: " + String.format ("%.1f", Double.valueOf (stepInfo.getTranspose () * 48.0 - 24.0)));
                }
                break;

            case PRESSURE:
                if (this.host.supports (Capability.NOTE_EDIT_PRESSURE))
                {
                    this.clip.changeStepPressure (this.channel, this.step, this.note, value);
                    if (!hasMCUDisplay)
                        this.mvHelper.delayDisplay ( () -> "Pressure: " + StringUtils.formatPercentage (stepInfo.getPressure ()));
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
            switch (idx)
            {
                case DURATION:
                    this.clip.updateStepDuration (this.channel, this.step, this.note, 1.0);
                    break;

                case VELOCITY:
                    this.clip.updateStepVelocity (this.channel, this.step, this.note, 1.0);
                    break;

                case GAIN:
                    if (this.host.supports (Capability.NOTE_EDIT_GAIN))
                        this.clip.updateStepGain (this.channel, this.step, this.note, 0);
                    break;

                case PANORAMA:
                    if (this.host.supports (Capability.NOTE_EDIT_PANORAMA))
                        this.clip.updateStepPan (this.channel, this.step, this.note, 0);
                    break;

                case TRANSPOSE:
                    if (this.host.supports (Capability.NOTE_EDIT_TRANSPOSE))
                        this.clip.updateStepTranspose (this.channel, this.step, this.note, 0);
                    break;

                case PRESSURE:
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
    public void updateDisplay ()
    {
        final ITextDisplay d = this.surface.getTextDisplay ().clear ();

        final IStepInfo stepInfo = this.clip == null ? null : this.clip.getStep (this.channel, this.step, this.note);

        d.setCell (0, 0, "Note").setCell (1, 1, stepInfo == null ? "-" : Scales.formatNoteAndOctave (this.note, -3));
        d.setCell (0, 1, "Step").setCell (1, 0, stepInfo == null ? "-" : Integer.toString (this.step + 1));
        d.setCell (0, 2, this.mark ("Length", 2)).setCell (1, 2, stepInfo == null ? "-" : this.formatLength (stepInfo.getDuration ()));
        d.setCell (0, 3, this.mark ("Velocity", 3)).setCell (1, 3, stepInfo == null ? "-" : StringUtils.formatPercentage (stepInfo.getVelocity ()));

        if (this.host.supports (Capability.NOTE_EDIT_GAIN))
            d.setCell (0, 4, this.mark ("Gain", 4)).setCell (1, 4, stepInfo == null ? "-" : StringUtils.formatPercentage (stepInfo.getGain ()));
        if (this.host.supports (Capability.NOTE_EDIT_PANORAMA))
            d.setCell (0, 5, this.mark ("Pan", 5)).setCell (1, 5, stepInfo == null ? "-" : StringUtils.formatPercentage (stepInfo.getPan ()));
        if (this.host.supports (Capability.NOTE_EDIT_TRANSPOSE))
            d.setCell (0, 6, this.mark ("Pitch", 6)).setCell (1, 6, stepInfo == null ? "-" : String.format ("%.1f", Double.valueOf (stepInfo.getTranspose ())));
        if (this.host.supports (Capability.NOTE_EDIT_PRESSURE))
            d.setCell (0, 7, this.mark ("Pressure", 7)).setCell (1, 7, stepInfo == null ? "-" : StringUtils.formatPercentage (stepInfo.getPressure ()));

        d.allDone ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItem ()
    {
        super.selectPreviousItem ();

        if (this.selectedParam < 2)
            this.selectedParam = 2;
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