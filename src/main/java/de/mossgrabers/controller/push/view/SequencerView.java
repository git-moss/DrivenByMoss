// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.view;

import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.controller.push.mode.NoteMode;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.daw.IStepInfo;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;
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
        super (Views.VIEW_NAME_SEQUENCER, surface, model, true);
    }


    /** {@inheritDoc} */
    @Override
    public void onOctaveDown (final ButtonEvent event)
    {
        if (!this.isActive ())
            return;

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
        if (!this.isActive ())
            return;

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
        if (!this.isActive ())
            return;

        final int index = note - 36;

        this.surface.getButton (ButtonID.get (ButtonID.PAD1, index)).setConsumed ();

        final int y = index / 8;
        if (y >= this.numSequencerRows)
            return;

        final int x = index % 8;
        final INoteClip cursorClip = this.getClip ();
        final int mappedNote = this.keyManager.map (y);
        final int editMidiChannel = this.surface.getConfiguration ().getMidiEditChannel ();
        final int state = cursorClip.getStep (editMidiChannel, x, mappedNote).getState ();
        if (state != IStepInfo.NOTE_START)
            return;

        final ModeManager modeManager = this.surface.getModeManager ();
        final NoteMode noteMode = (NoteMode) modeManager.getMode (Modes.NOTE);
        noteMode.setValues (cursorClip, editMidiChannel, x, mappedNote);
        modeManager.setActiveMode (Modes.NOTE);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (!this.isActive ())
            return;

        final int index = note - 36;
        final int x = index % 8;
        final int y = index / 8;

        if (y < this.numSequencerRows)
        {
            // Toggle the note on up, so we can intercept the long presses
            if (velocity == 0)
            {
                final int vel = this.configuration.isAccentActive () ? this.configuration.getFixedAccentValue () : this.surface.getButton (ButtonID.get (ButtonID.PAD1, index)).getPressedVelocity ();
                this.getClip ().toggleStep (this.surface.getConfiguration ().getMidiEditChannel (), x, this.keyManager.map (y), vel);
            }
            return;
        }

        super.onGridNote (note, velocity);
    }
}