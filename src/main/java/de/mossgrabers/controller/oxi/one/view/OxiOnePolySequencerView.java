// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.oxi.one.view;

import de.mossgrabers.controller.oxi.one.OxiOneConfiguration;
import de.mossgrabers.controller.oxi.one.controller.OxiOneControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.daw.clip.NotePosition;
import de.mossgrabers.framework.daw.clip.StepState;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.sequencer.AbstractPolySequencerView;


/**
 * The Poly Sequencer view.
 *
 * @author Jürgen Moßgraber
 */
public class OxiOnePolySequencerView extends AbstractPolySequencerView<OxiOneControlSurface, OxiOneConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     * @param useTrackColor True to use the color of the current track for coloring the octaves
     */
    public OxiOnePolySequencerView (final OxiOneControlSurface surface, final IModel model, final boolean useTrackColor)
    {
        super (surface, model, useTrackColor, 16, 8, 4);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        // Set loop start and end
        final boolean init = this.surface.isPressed (ButtonID.PUNCH_IN);
        final boolean end = this.surface.isPressed (ButtonID.PUNCH_OUT);
        if (init || end)
        {
            if (velocity == 0)
            {
                final INoteClip clip = this.getClip ();
                final int lengthOfOnePage = this.getLengthOfOnePage (this.numColumns * this.numSequencerRows);
                final int offset = clip.getEditPage () * lengthOfOnePage;
                final int index = note - this.surface.getPadGrid ().getStartNote ();
                final int x = index % this.numColumns;
                final int y = index / this.numColumns;
                final int step = this.numColumns * (this.numRows - 1 - y) + x;

                final double lengthOfOnePad = Resolution.getValueAt (this.getResolutionIndex ());
                final double pos = offset + step * lengthOfOnePad;
                final double newStart = init ? pos : clip.getLoopStart ();
                final double newLength = end ? Math.max (pos - newStart + lengthOfOnePad, lengthOfOnePad) : clip.getLoopLength ();
                clip.setLoopStart (newStart);
                clip.setLoopLength (newLength);
                clip.setPlayRange (newStart, newStart + newLength);
            }
            return;
        }

        super.onGridNote (note, velocity);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNoteLongPress (final int note)
    {
        if (!this.isActive ())
            return;

        final int index = note - this.surface.getPadGrid ().getStartNote ();
        final int x = index % this.numColumns;
        final int y = index / this.numColumns;
        final int step = this.numColumns * (this.numRows - 1 - y) + x;
        final NotePosition notePosition = new NotePosition (this.configuration.getMidiEditChannel (), step, 0);
        final INoteClip clip = this.getClip ();
        for (int row = 0; row < 128; row++)
        {
            notePosition.setNote (row);
            if (clip.getStep (notePosition).getState () == StepState.START)
                this.editNote (clip, notePosition, true);
        }
        // Prevent note deletion on button-up!
        this.surface.consumePads ();
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        if (ButtonID.isSceneButton (buttonID))
            this.onSceneButton (buttonID, event);
    }
}