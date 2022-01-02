// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.view;

import de.mossgrabers.controller.ableton.push.PushConfiguration;
import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.view.AbstractNoteSequencerView;
import de.mossgrabers.framework.view.Views;


/**
 * The Sequencer view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SequencerView extends AbstractNoteSequencerView<PushControlSurface, PushConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public SequencerView (final PushControlSurface surface, final IModel model)
    {
        super (Views.NAME_SEQUENCER, surface, model, true);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNoteLongPress (final int note)
    {
        if (!this.isActive ())
            return;

        final int index = note - 36;
        this.surface.getButton (ButtonID.get (ButtonID.PAD1, index)).setConsumed ();

        final int y = index / 8;
        if (y >= this.numSequencerRows)
            return;

        final int step = index % 8;
        final INoteClip clip = this.getClip ();
        final int mappedNote = this.keyManager.map (y);
        final int channel = this.configuration.getMidiEditChannel ();

        this.editNote (clip, channel, step, mappedNote, false);
    }


    /** {@inheritDoc} */
    @Override
    protected boolean handleSequencerAreaButtonCombinations (final INoteClip clip, final int channel, final int step, final int row, final int note, final int velocity)
    {
        final boolean isSelectPressed = this.surface.isSelectPressed ();

        if (this.surface.isShiftPressed ())
        {
            if (velocity > 0)
                this.handleSequencerAreaRepeatOperator (clip, channel, step, note, velocity, !isSelectPressed);
            return true;
        }

        if (isSelectPressed)
        {
            this.editNote (clip, channel, step, note, true);
            return true;
        }

        return super.handleSequencerAreaButtonCombinations (clip, channel, step, row, note, velocity);
    }
}