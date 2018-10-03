// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.view;

import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.controller.push.mode.Modes;
import de.mossgrabers.controller.push.mode.NoteMode;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractNoteSequencerView;
import de.mossgrabers.framework.view.AbstractSequencerView;


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
    public boolean usesButton (final int buttonID)
    {
        if (buttonID == PushControlSurface.PUSH_BUTTON_REPEAT)
            return this.model.getHost ().hasRepeat ();

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
        final int x = index % 8;
        final INoteClip cursorClip = this.getClip ();
        final int mappedNote = this.keyManager.map (y);
        final int state = cursorClip.getStep (x, mappedNote);
        final ModeManager modeManager = this.surface.getModeManager ();
        final NoteMode noteMode = (NoteMode) modeManager.getMode (Modes.MODE_NOTE);
        noteMode.setValues (cursorClip, x, mappedNote, state == 2 ? 1.0 : 0, 127);
        modeManager.setActiveMode (Modes.MODE_NOTE);
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
                this.getClip ().toggleStep (x, this.keyManager.map (y), this.configuration.isAccentActive () ? this.configuration.getFixedAccentValue () : this.surface.getGridNoteVelocity (note));
            return;
        }

        super.onGridNote (note, velocity);
    }


    /** {@inheritDoc} */
    @Override
    public void updateSceneButtons ()
    {
        final ColorManager colorManager = this.model.getColorManager ();
        final int colorResolution = colorManager.getColor (AbstractSequencerView.COLOR_RESOLUTION);
        final int colorSelectedResolution = colorManager.getColor (AbstractSequencerView.COLOR_RESOLUTION_SELECTED);
        for (int i = PushControlSurface.PUSH_BUTTON_SCENE1; i <= PushControlSurface.PUSH_BUTTON_SCENE8; i++)
            this.surface.updateButton (i, i == PushControlSurface.PUSH_BUTTON_SCENE1 + this.selectedIndex ? colorSelectedResolution : colorResolution);
    }
}