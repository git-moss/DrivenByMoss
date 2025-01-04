// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.oxi.one.view;

import de.mossgrabers.controller.oxi.one.OxiOneConfiguration;
import de.mossgrabers.controller.oxi.one.controller.OxiOneControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.daw.clip.NotePosition;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.Views;
import de.mossgrabers.framework.view.sequencer.AbstractNoteSequencerView;


/**
 * The Sequencer view.
 *
 * @author Jürgen Moßgraber
 */
public class OxiOneSequencerView extends AbstractNoteSequencerView<OxiOneControlSurface, OxiOneConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public OxiOneSequencerView (final OxiOneControlSurface surface, final IModel model)
    {
        super (Views.NAME_SEQUENCER, surface, model, 16, 8, true);

        this.numDisplayRows = 8;
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
                final int lengthOfOnePage = this.getLengthOfOnePage (this.numDisplayCols);
                final int offset = clip.getEditPage () * lengthOfOnePage;
                final int index = note - this.surface.getPadGrid ().getStartNote ();
                final int x = index % this.numDisplayCols;
                final double lengthOfOnePad = Resolution.getValueAt (this.getResolutionIndex ());
                final double pos = offset + x * lengthOfOnePad;
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
        this.surface.getButton (ButtonID.get (ButtonID.PAD1, index)).setConsumed ();

        final int y = index / this.numDisplayCols;
        final int x = index % this.numDisplayCols;
        final int mappedY = this.keyManager.map (y);

        final NotePosition notePosition = new NotePosition (this.configuration.getMidiEditChannel (), x, mappedY);
        this.editNote (this.getClip (), notePosition, false);
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        if (ButtonID.isSceneButton (buttonID))
            this.onSceneButton (buttonID, event);
    }


    /** {@inheritDoc} */
    @Override
    protected boolean handleSequencerAreaButtonCombinations (final INoteClip clip, final NotePosition notePosition, final int row, final int velocity)
    {
        if (this.surface.isPressed (ButtonID.REPEAT) || this.surface.isPressed (ButtonID.ACCENT))
        {
            if (velocity > 0)
                this.handleSequencerAreaRepeatOperator (clip, notePosition, velocity, this.surface.isPressed (ButtonID.REPEAT));
            return true;
        }

        return super.handleSequencerAreaButtonCombinations (clip, notePosition, row, velocity);
    }
}