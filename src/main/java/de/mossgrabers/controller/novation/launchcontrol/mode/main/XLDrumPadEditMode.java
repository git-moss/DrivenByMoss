package de.mossgrabers.controller.novation.launchcontrol.mode.main;

import de.mossgrabers.controller.novation.launchcontrol.controller.LaunchControlXLControlSurface;
import de.mossgrabers.controller.novation.launchcontrol.mode.buttons.XLDrumSequencerMode;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.daw.IStepInfo;
import de.mossgrabers.framework.daw.constants.Capability;
import de.mossgrabers.framework.daw.data.IDrumDevice;
import de.mossgrabers.framework.daw.data.ILayer;
import de.mossgrabers.framework.mode.Modes;

import java.util.List;


/**
 * Drum sequencer mode for the LauchControl XL.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class XLDrumPadEditMode extends XLBaseNoteEditMode
{
    private int note;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     * @param clipRows The rows of the monitored clip
     * @param clipCols The columns of the monitored clip
     * @param controls The IDs of the knobs or faders to control this mode
     */
    public XLDrumPadEditMode (final LaunchControlXLControlSurface surface, final IModel model, final int clipRows, final int clipCols, final List<ContinuousID> controls)
    {
        super ("Drum Sequencer", surface, model, clipRows, clipCols, controls);

        this.defaultMode = Modes.DRUM_SEQUENCER;
        this.note = this.model.getScales ().getDrumOffset ();
    }


    /** {@inheritDoc} */
    @Override
    protected int getNoteRow (final int channel, final int step)
    {
        return this.note;
    }


    /** {@inheritDoc} */
    @Override
    protected void executeRow0 (final int index)
    {
        this.note = this.model.getScales ().getDrumOffset () + index;
        ((XLDrumSequencerMode) this.surface.getTrackButtonModeManager ().get (Modes.DRUM_SEQUENCER)).setSelectedPad (index);
        final ILayer item = this.model.getDrumDevice ().getDrumPadBank ().getItem (index);
        if (item.doesExist ())
            this.host.showNotification ("Drum Pad: " + item.getName ());
    }


    /** {@inheritDoc} */
    @Override
    protected void handleKnobRow0 (final INoteClip clip, final int channel, final int column, final int noteRow, final double normalizedValue)
    {
        if (this.host.supports (Capability.NOTE_EDIT_CHANCE))
        {
            clip.updateChance (channel, column, noteRow, normalizedValue);
            this.surface.getDisplay ().notify (String.format ("Chance: %d%%", Integer.valueOf ((int) Math.round (normalizedValue * 100))));
        }
    }


    /** {@inheritDoc} */
    @Override
    protected int getKnobValueRow0 (final int noteRow, final IStepInfo stepInfo)
    {
        if (this.host.supports (Capability.NOTE_EDIT_CHANCE))
            return (int) (stepInfo.getChance () * 127);
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    protected int getFirstRowColor (final int index)
    {
        final boolean isRecording = this.model.hasRecordingState ();
        final IDrumDevice drumDevice = this.model.getDrumDevice ();
        final String drumPadColor = this.getDrumPadColor (index, drumDevice.getDrumPadBank (), isRecording);
        return this.colorManager.getColorIndex (drumPadColor);
    }
}
