package de.mossgrabers.controller.novation.launchcontrol.mode.main;

import de.mossgrabers.controller.novation.launchcontrol.controller.LaunchControlXLColorManager;
import de.mossgrabers.controller.novation.launchcontrol.controller.LaunchControlXLControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.daw.IStepInfo;
import de.mossgrabers.framework.daw.constants.Capability;
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
        int noteRow = this.getNoteRow (channel, index);
        final INoteClip clip = this.getClip ();
        if (noteRow == -1)
        {
            // Use the note of the currently selected scale base
            noteRow = 60 + this.scales.getScaleOffset ();
            clip.toggleStep (channel, index, noteRow, 127);
        }
        else
            clip.clearStep (channel, index, noteRow);
    }


    /** {@inheritDoc} */
    @Override
    protected void handleKnobRow0 (final INoteClip clip, final int channel, final int column, final int noteRow, final double normalizedValue)
    {
        if (this.surface.isPressed (ButtonID.REC_ARM))
        {
            if (this.host.supports (Capability.NOTE_EDIT_CHANCE))
            {
                clip.updateStepChance (channel, column, noteRow, normalizedValue);
                this.surface.getDisplay ().notify (String.format ("Chance: %d%%", Integer.valueOf ((int) Math.round (normalizedValue * 100))));
            }
            return;
        }

        // Move (transpose) the note up and down
        int newNote = (int) Math.round (normalizedValue * 126);
        if (!this.scales.isChromatic ())
            newNote = this.scales.getNearestNoteInScale (newNote);
        this.getClip ().moveStepY (channel, column, noteRow, newNote);
        this.surface.getDisplay ().notify ("Note: " + Scales.formatNoteAndOctave (newNote, -3));
    }


    /** {@inheritDoc} */
    @Override
    protected int getKnobValueRow0 (final int noteRow, final IStepInfo stepInfo)
    {
        if (this.surface.isPressed (ButtonID.REC_ARM))
        {
            if (this.host.supports (Capability.NOTE_EDIT_CHANCE))
                return (int) (stepInfo.getChance () * 127);
            return 0;
        }

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


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItem ()
    {
        this.scales.prevScale ();
        this.configuration.setScale (this.scales.getScale ().getName ());
        this.mvHelper.notifyScale (this.scales);
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItem ()
    {
        this.scales.nextScale ();
        this.configuration.setScale (this.scales.getScale ().getName ());
        this.mvHelper.notifyScale (this.scales);
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasPreviousItem ()
    {
        return this.scales.hasPrevScale ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasNextItem ()
    {
        return this.scales.hasNextScale ();
    }


    /** {@inheritDoc} */
    @Override
    protected Modes getSequencerMode ()
    {
        return Modes.NOTE_SEQUENCER;
    }
}
