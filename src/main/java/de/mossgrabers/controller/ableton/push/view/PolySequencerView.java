// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.view;

import de.mossgrabers.controller.ableton.push.PushConfiguration;
import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.daw.clip.NotePosition;
import de.mossgrabers.framework.daw.clip.StepState;
import de.mossgrabers.framework.view.sequencer.AbstractPolySequencerView;


/**
 * The Poly Sequencer view.
 *
 * @author Jürgen Moßgraber
 */
public class PolySequencerView extends AbstractPolySequencerView<PushControlSurface, PushConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     * @param useTrackColor True to use the color of the current track for coloring the octaves
     */
    public PolySequencerView (final PushControlSurface surface, final IModel model, final boolean useTrackColor)
    {
        super (surface, model, useTrackColor);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNoteLongPress (final int note)
    {
        if (!this.isActive ())
            return;

        final int index = note - 36;
        this.surface.getButton (ButtonID.get (ButtonID.PAD1, index)).setConsumed ();

        final int x = index % this.numColumns;
        final int y = index / this.numColumns;

        if (y < this.numRows - this.numSequencerRows)
            return;

        final INoteClip clip = this.getClip ();
        final int step = this.numColumns * (this.numRows - 1 - y) + x;
        final NotePosition notePosition = new NotePosition (this.configuration.getMidiEditChannel (), step, 0);

        this.clearEditNotes ();
        for (int row = 0; row < 128; row++)
        {
            notePosition.setNote (row);
            if (clip.getStep (notePosition).getState () == StepState.START)
                this.editNote (clip, notePosition, true);
        }
    }


    /** {@inheritDoc} */
    @Override
    protected boolean handleSequencerAreaButtonCombinations (final INoteClip clip, final int channel, final int step)
    {
        final boolean isSelectPressed = this.surface.isSelectPressed ();
        final NotePosition notePosition = new NotePosition (channel, step, 0);

        // Change note repeat setting for step
        if (this.surface.isShiftPressed ())
        {
            for (int row = 0; row < 128; row++)
            {
                notePosition.setNote (row);
                if (clip.getStep (notePosition).getState () == StepState.START)
                    this.handleSequencerAreaRepeatOperator (clip, notePosition, 127, !isSelectPressed);
            }
            return true;
        }

        // Add note to edit notes with SELECT
        if (isSelectPressed)
        {
            for (int row = 0; row < 128; row++)
            {
                notePosition.setNote (row);
                if (clip.getStep (notePosition).getState () == StepState.START)
                    this.editNote (clip, notePosition, true);
            }
            return true;
        }

        return super.handleSequencerAreaButtonCombinations (clip, channel, step);
    }
}