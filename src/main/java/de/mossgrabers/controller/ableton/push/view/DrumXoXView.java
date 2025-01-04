// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.view;

import de.mossgrabers.controller.ableton.push.PushConfiguration;
import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.daw.clip.NotePosition;
import de.mossgrabers.framework.view.Views;
import de.mossgrabers.framework.view.sequencer.AbstractDrumXoXView;


/**
 * The Drum XoX view.
 *
 * @author Jürgen Moßgraber
 */
public class DrumXoXView extends AbstractDrumXoXView<PushControlSurface, PushConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public DrumXoXView (final PushControlSurface surface, final IModel model)
    {
        super (Views.NAME_DRUM_XOX, surface, model, 8);
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

        // Sequencer steps
        if (y < this.numStepRows)
        {
            this.surface.getButton (ButtonID.get (ButtonID.PAD1, index)).setConsumed ();

            final int offsetY = this.scales.getDrumOffset ();
            final NotePosition notePosition = new NotePosition (this.configuration.getMidiEditChannel (), (this.numStepRows - 1 - y) * this.numColumns + x, offsetY + this.selectedPad);
            this.editNote (this.getClip (), notePosition, false);
        }
    }


    /** {@inheritDoc} */
    @Override
    protected boolean handleSequencerAreaButtonCombinations (final INoteClip clip, final NotePosition notePosition, final int velocity)
    {
        final boolean isSelectPressed = this.surface.isSelectPressed ();

        if (this.surface.isShiftPressed ())
        {
            if (velocity > 0)
                this.handleSequencerAreaRepeatOperator (clip, notePosition, velocity, !isSelectPressed);
            return true;
        }

        if (isSelectPressed)
        {
            this.surface.setTriggerConsumed (ButtonID.SELECT);
            if (velocity > 0)
                this.editNote (clip, notePosition, true);
            return true;
        }

        return super.handleSequencerAreaButtonCombinations (clip, notePosition, velocity);
    }
}