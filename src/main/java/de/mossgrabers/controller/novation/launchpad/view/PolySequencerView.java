// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchpad.view;

import de.mossgrabers.controller.novation.launchpad.LaunchpadConfiguration;
import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadControlSurface;
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
public class PolySequencerView extends AbstractPolySequencerView<LaunchpadControlSurface, LaunchpadConfiguration>
{
    private NotePosition noteEditPosition;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     * @param useTrackColor True to use the color of the current track for coloring the octaves
     */
    public PolySequencerView (final LaunchpadControlSurface surface, final IModel model, final boolean useTrackColor)
    {
        super (surface, model, useTrackColor);
    }


    /** {@inheritDoc} */
    @Override
    protected boolean handleSequencerAreaButtonCombinations (final INoteClip clip, final int channel, final int step)
    {
        final boolean isUpPressed = this.surface.isPressed (ButtonID.UP);
        if (isUpPressed || this.surface.isPressed (ButtonID.DOWN))
        {
            this.surface.setTriggerConsumed (isUpPressed ? ButtonID.UP : ButtonID.DOWN);

            final NotePosition notePosition = new NotePosition (channel, step, 0);
            for (int row = 0; row < 128; row++)
            {
                notePosition.setNote (row);
                if (clip.getStep (notePosition).getState () != StepState.OFF)
                    this.handleSequencerAreaRepeatOperator (clip, notePosition, 127, isUpPressed);
            }
            return true;
        }

        return super.handleSequencerAreaButtonCombinations (clip, channel, step);
    }


    /** {@inheritDoc} */
    @Override
    protected void handleSequencerArea (final int x, final int y, final int velocity)
    {
        // Toggle the note on up, so we can intercept the long presses
        if (velocity != 0)
        {
            this.noteEditPosition = null;
            return;
        }

        // Note: If the length of the note was changed this method will not be called since button
        // up was consumed! Therefore, always call edit note
        if (this.noteEditPosition != null)
        {
            final INoteClip clip = this.getClip ();
            this.clearEditNotes ();
            for (int row = 0; row < 128; row++)
            {
                this.noteEditPosition.setNote (row);
                if (clip.getStep (this.noteEditPosition).getState () == StepState.START)
                    this.editNote (clip, this.noteEditPosition, true);
            }
            return;
        }

        super.handleSequencerArea (x, y, velocity);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNoteLongPress (final int note)
    {
        if (!this.isActive ())
            return;

        final int index = note - 36;
        final int x = index % this.numColumns;
        final int y = index / this.numColumns;

        if (y < this.numRows - this.numSequencerRows)
            return;

        final int step = this.numColumns * (this.numRows - 1 - y) + x;
        this.noteEditPosition = new NotePosition (this.configuration.getMidiEditChannel (), step, 0);
    }
}