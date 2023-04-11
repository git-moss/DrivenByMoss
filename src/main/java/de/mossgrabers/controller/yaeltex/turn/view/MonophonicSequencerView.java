// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.yaeltex.turn.view;

import de.mossgrabers.controller.yaeltex.turn.YaeltexTurnConfiguration;
import de.mossgrabers.controller.yaeltex.turn.controller.YaeltexTurnControlSurface;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.view.sequencer.AbstractMonophonicSequencerView;
import de.mossgrabers.framework.view.sequencer.AbstractSequencerView;


/**
 * The monophonic sequencer view.
 *
 * @author Jürgen Moßgraber
 */
public class MonophonicSequencerView extends AbstractMonophonicSequencerView<YaeltexTurnControlSurface, YaeltexTurnConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public MonophonicSequencerView (final YaeltexTurnControlSurface surface, final IModel model)
    {
        super ("Monophonic Sequencer", surface, model, 4, 8, true);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (!this.isActive () || velocity == 0)
            return;

        // Set the clip length
        if (this.surface.isShiftPressed ())
        {
            final int index = note - 36;
            final int x = index % this.numSequencerColumns;
            final int y = this.numSequencerRows - 1 - index / this.numSequencerColumns;
            int steps = y * this.numSequencerColumns + x + 1;
            final INoteClip clip = this.getClip ();
            steps += clip.getEditPage () * this.sequencerSteps;
            clip.setLoopLength (steps * Resolution.getValueAt (this.getResolutionIndex ()));
            return;
        }

        super.onGridNote (note, velocity);
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        // Show the clip length
        if (this.surface.isShiftPressed ())
        {
            final boolean isActive = this.isActive ();
            final INoteClip clip = this.getClip ();
            int numberOfActiveSteps = (int) Math.floor (clip.getLoopLength () / Resolution.getValueAt (this.getResolutionIndex ()));
            numberOfActiveSteps -= clip.getEditPage () * this.sequencerSteps;
            final IPadGrid padGrid = this.surface.getPadGrid ();
            String color;
            for (int col = 0; col < this.sequencerSteps; col++)
            {
                final int x = col % this.numSequencerColumns;
                final int y = col / this.numSequencerColumns;
                if (!isActive)
                    color = AbstractSequencerView.COLOR_NO_CONTENT;
                else
                {
                    final boolean isFourth = (col + 1) % 4 == 0;
                    if (col < numberOfActiveSteps)
                        color = isFourth ? AbstractSequencerView.COLOR_ACTIVE_PAGE : AbstractSequencerView.COLOR_PAGE;
                    else
                        color = isFourth ? AbstractSequencerView.COLOR_SELECTED_PAGE : AbstractSequencerView.COLOR_NO_CONTENT;
                }
                padGrid.lightEx (x, y, color);
            }

            return;
        }

        super.drawGrid ();
    }
}
