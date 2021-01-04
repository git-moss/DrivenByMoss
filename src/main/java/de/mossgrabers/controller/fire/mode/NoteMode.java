// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.fire.mode;

import de.mossgrabers.controller.fire.FireConfiguration;
import de.mossgrabers.controller.fire.controller.FireControlSurface;
import de.mossgrabers.controller.fire.graphics.canvas.component.TitleValueComponent;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.daw.IStepInfo;
import de.mossgrabers.framework.daw.constants.Capability;
import de.mossgrabers.framework.featuregroup.AbstractMode;
import de.mossgrabers.framework.featuregroup.IView;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.StringUtils;
import de.mossgrabers.framework.view.AbstractSequencerView;

import java.util.List;


/**
 * Note edit knob mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class NoteMode extends AbstractMode<FireControlSurface, FireConfiguration>
{
    protected static final List<ContinuousID> KNOB_IDS = ContinuousID.createSequentialList (ContinuousID.KNOB1, 4);
    static
    {
        KNOB_IDS.add (ContinuousID.VIEW_SELECTION);
    }

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
    public NoteMode (final FireControlSurface surface, final IModel model)
    {
        super ("Note Edit", surface, model, false, null, KNOB_IDS);

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

        if (this.isKnobTouched[index] == isTouched)
            return;

        this.isKnobTouched[index] = isTouched;
        if (isTouched)
        {
            this.clip.startEdit (this.channel, this.step, this.note);
            this.preventNoteDeletion ();
        }
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
                if (this.host.supports (Capability.NOTE_EDIT_GAIN))
                    this.clip.changeStepGain (this.channel, this.step, this.note, value);
                break;

            case 1:
                if (this.host.supports (Capability.NOTE_EDIT_PANORAMA))
                    this.clip.changeStepPan (this.channel, this.step, this.note, value);
                break;

            case 2:
                this.clip.changeStepDuration (this.channel, this.step, this.note, value);
                break;

            case 3:
                this.clip.changeStepVelocity (this.channel, this.step, this.note, value);
                break;

            // This is the select knob
            case 4:
                if (this.host.supports (Capability.NOTE_EDIT_TRANSPOSE))
                {
                    this.clip.changeStepTranspose (this.channel, this.step, this.note, value);
                    this.preventNoteDeletion ();
                }
                break;

            default:
                return;
        }
    }


    /**
     * Note was modified, prevent deletion of note on button up.
     */
    private void preventNoteDeletion ()
    {
        final IView activeView = this.surface.getViewManager ().getActive ();
        if (activeView instanceof AbstractSequencerView)
            AbstractSequencerView.class.cast (activeView).setNoteEdited ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        if (this.clip == null)
            return;

        final IGraphicDisplay display = this.surface.getGraphicsDisplay ();

        final String desc = "Step: " + (this.step + 1) + " - " + Scales.formatNoteAndOctave (this.note, -3);

        String paramLine = "";
        int value = -1;

        final IStepInfo stepInfo = this.clip.getStep (this.channel, this.step, this.note);
        final IValueChanger valueChanger = this.model.getValueChanger ();

        switch (this.getTouchedKnob ())
        {
            case 0:
                if (this.host.supports (Capability.NOTE_EDIT_GAIN))
                {
                    final double noteGain = stepInfo.getGain ();
                    value = Math.min (1023, valueChanger.fromNormalizedValue (noteGain));
                    paramLine = "Gain: " + StringUtils.formatPercentage (noteGain);
                }
                break;

            case 1:
                if (this.host.supports (Capability.NOTE_EDIT_PANORAMA))
                {
                    final double notePan = stepInfo.getPan ();
                    value = valueChanger.fromNormalizedValue ((notePan + 1.0) / 2.0);
                    paramLine = "Pan: " + StringUtils.formatPercentage (notePan);
                }
                break;

            case 2:
                paramLine = this.formatLength (stepInfo.getDuration ());
                break;

            case 3:
                final double noteVelocity = stepInfo.getVelocity ();
                value = valueChanger.fromNormalizedValue (noteVelocity);
                paramLine = "Vel.: " + StringUtils.formatPercentage (noteVelocity);
                break;

            // This is the select knob
            case 4:
                if (this.host.supports (Capability.NOTE_EDIT_TRANSPOSE))
                {
                    final double noteTranspose = stepInfo.getTranspose ();
                    value = valueChanger.fromNormalizedValue ((noteTranspose + 24.0) / 48.0);
                    paramLine = "Pitch: " + String.format ("%.1f", Double.valueOf (noteTranspose));
                }
                break;

            default:
                // That's all...
                break;
        }

        display.addElement (new TitleValueComponent (desc, paramLine, value, false));
        display.send ();
    }


    private String formatLength (final double duration)
    {
        return StringUtils.formatMeasures (this.model.getTransport ().getQuartersPerMeasure (), duration, 0, true);
    }


    /**
     * Reset the transpose setting.
     */
    public void resetTranspose ()
    {
        if (this.clip != null && this.host.supports (Capability.NOTE_EDIT_TRANSPOSE))
            this.clip.updateStepTranspose (this.channel, this.step, this.note, 0);
    }
}