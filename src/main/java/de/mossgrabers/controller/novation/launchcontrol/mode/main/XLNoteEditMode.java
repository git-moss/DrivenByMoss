package de.mossgrabers.controller.novation.launchcontrol.mode.main;

import de.mossgrabers.controller.novation.launchcontrol.controller.LaunchControlXLColorManager;
import de.mossgrabers.controller.novation.launchcontrol.controller.LaunchControlXLControlSurface;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.daw.IStepInfo;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.scale.Scales;

import java.util.List;


/**
 * Note sequencer mode for the LauchControl XL.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class XLNoteEditMode extends XLBaseNoteEditMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     * @param clipRows The rows of the monitored clip
     * @param clipCols The columns of the monitored clip
     * @param controls The IDs of the knobs or faders to control this mode
     */
    public XLNoteEditMode (final LaunchControlXLControlSurface surface, final IModel model, final int clipRows, final int clipCols, final List<ContinuousID> controls)
    {
        super ("Note Sequencer", surface, model, clipRows, clipCols, controls);

        this.defaultMode = Modes.NOTE_SEQUENCER;
    }


    /** {@inheritDoc} */
    @Override
    protected int getNoteRow (final int channel, final int step)
    {
        return this.getClip ().getHighestRow (channel, step);
    }


    /** {@inheritDoc} */
    @Override
    protected void executeRow0 (final int index)
    {
        final int channel = this.configuration.getMidiEditChannel ();
        final int noteRow = this.getNoteRow (channel, index);
        final INoteClip clip = this.getClip ();
        if (noteRow == -1)
            clip.toggleStep (channel, index, 64, 127);
        else
            clip.clearStep (channel, index, noteRow);
    }


    /** {@inheritDoc} */
    @Override
    protected void handleKnobRow0 (final INoteClip clip, final int channel, final int column, final int noteRow, final double normalizedValue)
    {
        // Move (transpose) the note up and down
        final int newNote = (int) Math.round (normalizedValue * 126);
        this.getClip ().moveStepY (channel, column, noteRow, newNote);
        this.surface.getDisplay ().notify ("Note: " + Scales.formatNoteAndOctave (newNote, -3));
    }


    /** {@inheritDoc} */
    @Override
    protected int getKnobValueRow0 (final int noteRow, final IStepInfo stepInfo)
    {
        return noteRow;
    }


    /** {@inheritDoc} */
    @Override
    protected int getFirstRowColor (final int index)
    {
        final int channel = this.configuration.getMidiEditChannel ();
        final int noteRow = this.getNoteRow (channel, index);
        return noteRow < 0 ? LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_BLACK : LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_AMBER;
    }
}
