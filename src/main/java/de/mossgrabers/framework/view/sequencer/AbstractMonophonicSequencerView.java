// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.view.sequencer;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.daw.clip.IStepInfo;
import de.mossgrabers.framework.daw.clip.NotePosition;
import de.mossgrabers.framework.daw.data.empty.EmptyStepInfo;

import java.util.List;
import java.util.Optional;


/**
 * Abstract implementation for a view which provides a monophonic sequencer.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author Jürgen Moßgraber
 */
public abstract class AbstractMonophonicSequencerView<S extends IControlSurface<C>, C extends Configuration> extends AbstractSequencerView<S, C>
{
    protected final int sequencerSteps;
    protected final int numSequencerColumns;


    /**
     * Constructor.
     *
     * @param name The name of the view
     * @param surface The surface
     * @param model The model
     * @param numSequencerRows The number of rows to use for the sequencer steps
     * @param numSequencerColumns The number of columns to use for the sequencer steps
     * @param useDawColors True to use the DAW color of items for coloring (for full RGB devices)
     */
    protected AbstractMonophonicSequencerView (final String name, final S surface, final IModel model, final int numSequencerRows, final int numSequencerColumns, final boolean useDawColors)
    {
        super (name, surface, model, 128, numSequencerRows * numSequencerColumns, useDawColors);

        this.sequencerSteps = this.clipCols;
        this.numSequencerRows = numSequencerRows;
        this.numSequencerColumns = numSequencerColumns;
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final boolean isActive = this.isActive ();
        final INoteClip clip = this.getClip ();
        final Optional<ColorEx> rowColor = clip.doesExist () ? Optional.of (clip.getColor ()) : Optional.empty ();

        final int step = clip.getCurrentStep ();
        final int hiStep = this.isInXRange (step) ? step % this.sequencerSteps : -1;
        final int channel = this.configuration.getMidiEditChannel ();
        final IPadGrid padGrid = this.surface.getPadGrid ();
        final List<NotePosition> editNotes = this.getEditNotes ();
        final NotePosition notePosition = new NotePosition (channel, 0, 0);
        for (int col = 0; col < this.sequencerSteps; col++)
        {
            notePosition.setStep (col);
            final int x = col % this.numSequencerColumns;
            final int y = col / this.numSequencerColumns;

            if (!isActive)
                padGrid.lightEx (x, y, AbstractSequencerView.COLOR_NO_CONTENT);
            else
            {
                final int noteRow = clip.getHighestRow (channel, col);
                notePosition.setNote (noteRow);
                final IStepInfo stepInfo = noteRow == -1 ? EmptyStepInfo.INSTANCE : clip.getStep (notePosition);
                final boolean hilite = col == hiStep;
                padGrid.lightEx (x, y, this.getStepColor (stepInfo, hilite, rowColor, channel, col, noteRow, editNotes));
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (!this.isActive () || velocity == 0)
            return;

        final int index = note - 36;
        final int x = index % this.numSequencerColumns;
        final int y = this.numSequencerRows - 1 - index / this.numSequencerColumns;
        final int step = y * this.numSequencerColumns + x;

        final int channel = this.configuration.getMidiEditChannel ();
        final INoteClip clip = this.getClip ();
        final int noteRow = clip.getHighestRow (channel, step);
        final NotePosition notePosition = new NotePosition (this.configuration.getMidiEditChannel (), step, noteRow);
        if (noteRow == -1)
        {
            // Use the note of the currently selected scale base
            notePosition.setNote (60 + this.scales.getScaleOffset ());
            clip.toggleStep (notePosition, 127);
        }
        else
            clip.clearStep (notePosition);
    }
}