// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.apc.mode;

import de.mossgrabers.controller.apc.controller.APCControlSurface;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.daw.IStepInfo;
import de.mossgrabers.framework.daw.constants.EditCapability;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.utils.StringUtils;
import de.mossgrabers.framework.view.AbstractSequencerView;
import de.mossgrabers.framework.view.View;


/**
 * Note edit knob mode.
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
    public NoteMode (final APCControlSurface surface, final IModel model)
    {
        super ("Note Edit", surface, model, APCControlSurface.LED_MODE_VOLUME, 0, null);

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
    public void setValue (final int index, final int value)
    {
        final IValueChanger valueChanger = this.model.getValueChanger ();
        final double normalizedValue = valueChanger.toNormalizedValue (value);
        final IStepInfo stepInfo = this.clip.getStep (this.channel, this.step, this.note);

        switch (index)
        {
            case 0:
                // Since this is an absolute knob but the duration is unlimited, we limit it to 4
                // bars (128 x 32th). Furthermore, make sure that the length is exactly on a 32th
                final double duration = Math.round (normalizedValue * 16.0 / 0.125) * 0.125;
                this.clip.updateStepDuration (this.channel, this.step, this.note, duration);
                this.surface.getDisplay ().notify ("Duration: " + StringUtils.formatMeasures (this.model.getTransport ().getQuartersPerMeasure (), normalizedValue, 0, true));
                break;

            case 1:
                this.clip.updateStepVelocity (this.channel, this.step, this.note, normalizedValue);
                this.surface.getDisplay ().notify ("Velocity: " + StringUtils.formatPercentage (normalizedValue));
                break;

            case 2:
                if (this.host.canEdit (EditCapability.NOTE_EDIT_RELEASE_VELOCITY))
                {
                    this.clip.updateStepReleaseVelocity (this.channel, this.step, this.note, normalizedValue);
                    this.surface.getDisplay ().notify ("Release Velocity: " + StringUtils.formatPercentage (normalizedValue));
                }
                break;

            case 3:
                if (this.host.canEdit (EditCapability.NOTE_EDIT_GAIN))
                {
                    this.clip.updateStepGain (this.channel, this.step, this.note, normalizedValue);
                    this.surface.getDisplay ().notify ("Gain: " + StringUtils.formatPercentage (normalizedValue));
                }
                break;

            case 4:
                if (this.host.canEdit (EditCapability.NOTE_EDIT_PANORAMA))
                {
                    final double pan = normalizedValue * 2.0 - 1.0;
                    this.clip.updateStepPan (this.channel, this.step, this.note, pan);
                    this.surface.getDisplay ().notify ("Panorama: " + StringUtils.formatPercentage (pan));
                }
                break;

            case 5:
                if (this.host.canEdit (EditCapability.NOTE_EDIT_TRANSPOSE))
                {
                    final double pitch = normalizedValue * 48.0 - 24.0;
                    this.clip.updateStepTranspose (this.channel, this.step, this.note, pitch);
                    this.surface.getDisplay ().notify ("Pitch: " + String.format ("%.1f", Double.valueOf (pitch)));
                }
                break;

            case 6:
                if (this.host.canEdit (EditCapability.NOTE_EDIT_TIMBRE))
                {
                    final double timbre = normalizedValue * 2.0 - 1.0;
                    this.clip.updateStepTimbre (this.channel, this.step, this.note, timbre);
                    this.surface.getDisplay ().notify ("Timbre: " + StringUtils.formatPercentage (timbre));
                }
                break;

            case 7:
                if (this.host.canEdit (EditCapability.NOTE_EDIT_PRESSURE))
                {
                    this.clip.updateStepPressure (this.channel, this.step, this.note, normalizedValue);
                    this.surface.getDisplay ().notify ("Pressure: " + StringUtils.formatPercentage (stepInfo.getPressure ()));
                }
                break;

            default:
                return;
        }

        // Note was modified, prevent deletion of note on button up
        final View activeView = this.surface.getViewManager ().getActiveView ();
        if (activeView instanceof AbstractSequencerView)
            AbstractSequencerView.class.cast (activeView).setNoteEdited ();
    }


    /** {@inheritDoc} */
    @Override
    public int getKnobValue (final int index)
    {
        final IStepInfo stepInfo = this.clip.getStep (this.channel, this.step, this.note);
        final IValueChanger valueChanger = this.model.getValueChanger ();

        switch (index)
        {
            case 0:
                final double duration = Math.min (Resolution.RES_1_32.getValue () * 128.0, stepInfo.getDuration ());
                return Math.min (127, (int) Math.round (duration * 127.0 / 16.0));

            case 1:
                return valueChanger.fromNormalizedValue (stepInfo.getVelocity ());

            case 2:
                if (this.host.canEdit (EditCapability.NOTE_EDIT_RELEASE_VELOCITY))
                    return valueChanger.fromNormalizedValue (stepInfo.getReleaseVelocity ());
                return -1;

            case 3:
                if (this.host.canEdit (EditCapability.NOTE_EDIT_GAIN))
                    return valueChanger.fromNormalizedValue (stepInfo.getGain ());
                return -1;

            case 4:
                if (this.host.canEdit (EditCapability.NOTE_EDIT_PANORAMA))
                    return valueChanger.fromNormalizedValue ((stepInfo.getPan () + 1.0) / 2.0);
                return -1;

            case 5:
                if (this.host.canEdit (EditCapability.NOTE_EDIT_TRANSPOSE))
                    return valueChanger.fromNormalizedValue ((stepInfo.getTranspose () + 24.0) / 48.0);
                return -1;

            case 6:
                if (this.host.canEdit (EditCapability.NOTE_EDIT_TIMBRE))
                    return valueChanger.fromNormalizedValue ((stepInfo.getTimbre () + 1.0) / 2.0);
                return -1;

            case 7:
                if (this.host.canEdit (EditCapability.NOTE_EDIT_PRESSURE))
                    return valueChanger.fromNormalizedValue (stepInfo.getPressure ());
                return -1;

            default:
                return -1;
        }
    }
}