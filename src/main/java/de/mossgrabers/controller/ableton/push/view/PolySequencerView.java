// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.view;

import de.mossgrabers.controller.ableton.push.PushConfiguration;
import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.daw.StepState;
import de.mossgrabers.framework.view.AbstractPolySequencerView;


/**
 * The Poly Sequencer view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
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
        final int channel = this.configuration.getMidiEditChannel ();

        this.clearEditNotes ();
        for (int row = 0; row < 128; row++)
        {
            if (clip.getStep (channel, step, row).getState () == StepState.START)
                this.editNote (clip, channel, step, row, true);
        }
    }


    /** {@inheritDoc} */
    @Override
    protected boolean handleSequencerAreaButtonCombinations (final INoteClip clip, final int channel, final int step)
    {
        final boolean isSelectPressed = this.surface.isSelectPressed ();

        if (this.surface.isShiftPressed ())
        {
            for (int row = 0; row < 128; row++)
            {
                if (clip.getStep (channel, step, row).getState () == StepState.START)
                    this.handleSequencerAreaRepeatOperator (clip, channel, step, row, 127, !isSelectPressed);
            }
            return true;
        }

        if (isSelectPressed)
        {
            for (int row = 0; row < 128; row++)
            {
                if (clip.getStep (channel, step, row).getState () == StepState.START)
                    this.editNote (clip, channel, step, row, true);
            }
            return true;
        }

        return super.handleSequencerAreaButtonCombinations (clip, channel, step);
    }
}