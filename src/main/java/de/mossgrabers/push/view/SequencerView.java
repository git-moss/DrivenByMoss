// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.view;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.view.AbstractNoteSequencerView;
import de.mossgrabers.push.PushConfiguration;
import de.mossgrabers.push.controller.PushColors;
import de.mossgrabers.push.controller.PushControlSurface;


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
    public SequencerView (final PushControlSurface surface, final Model model)
    {
        super (Views.VIEW_NAME_SEQUENCER, surface, model, true);
    }


    /** {@inheritDoc} */
    @Override
    public boolean usesButton (final int buttonID)
    {
        if (buttonID == PushControlSurface.PUSH_BUTTON_REPEAT)
            return false;

        if (this.surface.getConfiguration ().isPush2 () && buttonID == PushControlSurface.PUSH_BUTTON_USER_MODE)
            return false;

        return true;
    }


    /** {@inheritDoc} */
    @Override
    public void onOctaveDown (final ButtonEvent event)
    {
        if (this.surface.isShiftPressed ())
        {
            if (event == ButtonEvent.DOWN)
                this.getClip ().transpose (-1);
            return;
        }

        if (this.surface.isSelectPressed ())
        {
            if (event == ButtonEvent.DOWN)
                this.getClip ().transpose (-12);
            return;
        }

        super.onOctaveDown (event);
    }


    /** {@inheritDoc} */
    @Override
    public void onOctaveUp (final ButtonEvent event)
    {
        if (this.surface.isShiftPressed ())
        {
            if (event == ButtonEvent.DOWN)
                this.getClip ().transpose (1);
            return;
        }

        if (this.surface.isSelectPressed ())
        {
            if (event == ButtonEvent.DOWN)
                this.getClip ().transpose (12);
            return;
        }

        super.onOctaveUp (event);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNoteLongPress (final int note)
    {
        if (!this.model.canSelectedTrackHoldNotes ())
            return;

        this.surface.setGridNoteConsumed (note);

        final int index = note - 36;
        final int y = index / 8;
        if (y >= this.numSequencerRows)
            return;

        // TODO Bugfix required - setStep makes Bitwig hang
        // https://github.com/teotigraphix/Framework4Bitwig/issues/124
        // x = index % 8;
        // state = this.clip.getStep (x, this.noteMap[y]);
        // noteMode = this.surface.getMode (MODE_NOTE);
        // noteMode.setValues (this.clip, x, note, state == 2 ? 1.0 : 0, 127);
        // this.surface.setPendingMode (MODE_NOTE);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (!this.model.canSelectedTrackHoldNotes ())
            return;
        final int index = note - 36;
        final int x = index % 8;
        final int y = index / 8;

        if (y < this.numSequencerRows)
        {
            // Toggle the note on up, so we can intercept the long presses
            if (velocity == 0)
                this.getClip ().toggleStep (x, this.noteMap[y], this.configuration.isAccentActive () ? this.configuration.getFixedAccentValue () : this.surface.getGridNoteVelocity (note));
            return;
        }

        super.onGridNote (note, velocity);
    }


    /** {@inheritDoc} */
    @Override
    public void updateSceneButtons ()
    {
        final boolean isPush2 = this.surface.getConfiguration ().isPush2 ();
        final int yellow = isPush2 ? PushColors.PUSH2_COLOR_SCENE_YELLOW : PushColors.PUSH1_COLOR_SCENE_YELLOW;
        final int green = isPush2 ? PushColors.PUSH2_COLOR_SCENE_GREEN : PushColors.PUSH1_COLOR_SCENE_GREEN;
        for (int i = PushControlSurface.PUSH_BUTTON_SCENE1; i <= PushControlSurface.PUSH_BUTTON_SCENE8; i++)
            this.surface.updateButton (i, i == PushControlSurface.PUSH_BUTTON_SCENE1 + this.selectedIndex ? yellow : green);
    }
}