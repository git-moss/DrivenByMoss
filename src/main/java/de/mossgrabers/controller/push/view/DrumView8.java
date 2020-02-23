// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.view;

import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.controller.push.mode.NoteMode;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.hardware.IHwButton;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.daw.IStepInfo;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.view.AbstractSequencerView;
import de.mossgrabers.framework.view.Views;


/**
 * The Drum 8 view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DrumView8 extends DrumViewBase
{
    private static final int NUM_DISPLAY_COLS = 8;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public DrumView8 (final PushControlSurface surface, final IModel model)
    {
        super (Views.VIEW_NAME_DRUM8, surface, model, 1, 0);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (!this.model.canSelectedTrackHoldNotes ())
            return;

        // Toggle the note on up, so we can intercept the long presses
        if (velocity != 0)
            return;

        final int index = note - DRUM_START_KEY;
        final int x = index % 8;
        final int y = index / 8;

        final int sound = y + this.soundOffset;
        final int col = x;
        final int row = this.scales.getDrumOffset () + this.selectedPad + sound;

        final int channel = this.configuration.getMidiEditChannel ();
        final int vel = this.configuration.isAccentActive () ? this.configuration.getFixedAccentValue () : this.surface.getButton (ButtonID.get (ButtonID.PAD1, index)).getPressedVelocity ();
        final INoteClip clip = this.getClip ();

        if (this.handleNoteAreaButtonCombinations (clip, channel, col, y, row, vel))
            return;

        clip.toggleStep (channel, col, row, vel);
    }


    /**
     * Handle button combinations on the note area of the sequencer.
     *
     * @param clip The sequenced midi clip
     * @param channel The MIDI channel of the note
     * @param row The row in the current page in the clip
     * @param note The note in the current page of the pad in the clip
     * @param step The step in the current page in the clip
     * @param velocity The velocity
     * @return True if handled
     */
    private boolean handleNoteAreaButtonCombinations (final INoteClip clip, final int channel, final int step, final int row, final int note, final int velocity)
    {
        // Handle note duplicate function
        final IHwButton duplicateButton = this.surface.getButton (ButtonID.DUPLICATE);
        if (duplicateButton != null && duplicateButton.isPressed ())
        {
            duplicateButton.setConsumed ();
            final IStepInfo noteStep = clip.getStep (channel, step, note);
            if (noteStep.getState () == IStepInfo.NOTE_START)
                this.copyNote = noteStep;
            else if (this.copyNote != null)
                clip.setStep (channel, step, note, this.copyNote);
            return true;
        }

        // Change length of a note or create a new one with a length
        final int offset = row * clip.getNumSteps ();
        for (int s = 0; s < step; s++)
        {
            final IHwButton button = this.surface.getButton (ButtonID.get (ButtonID.PAD1, offset + s));
            if (button.isLongPressed ())
            {
                button.setConsumed ();
                final int length = step - s + 1;
                final double duration = length * Resolution.getValueAt (this.getResolutionIndex ());
                final int state = note < 0 ? 0 : clip.getStep (channel, s, note).getState ();
                if (state == IStepInfo.NOTE_START)
                    clip.updateStepDuration (channel, s, note, duration);
                else
                    clip.setStep (channel, s, note, velocity, duration);
                return true;
            }
        }

        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNoteLongPress (final int note)
    {
        if (!this.isActive ())
            return;

        final int index = note - DRUM_START_KEY;
        this.surface.getButton (ButtonID.get (ButtonID.PAD1, index)).setConsumed ();

        final int x = index % 8;
        final int y = index / 8;

        final int sound = y + this.soundOffset;
        final int stepX = x;
        final int stepY = this.scales.getDrumOffset () + this.selectedPad + sound;

        final int editMidiChannel = this.configuration.getMidiEditChannel ();
        final INoteClip clip = this.getClip ();
        final int state = clip.getStep (editMidiChannel, stepX, stepY).getState ();
        if (state != IStepInfo.NOTE_START)
            return;

        final ModeManager modeManager = this.surface.getModeManager ();
        final NoteMode noteMode = (NoteMode) modeManager.getMode (Modes.NOTE);
        noteMode.setValues (clip, editMidiChannel, stepX, stepY);
        modeManager.setActiveMode (Modes.NOTE);
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        if (!this.model.canSelectedTrackHoldNotes () || !this.isActive ())
        {
            this.surface.getPadGrid ().turnOff ();
            return;
        }

        // Clip length/loop area
        final int step = this.getClip ().getCurrentStep ();

        // Paint the sequencer steps
        final int hiStep = this.isInXRange (step) ? step % DrumView8.NUM_DISPLAY_COLS : -1;
        final int offsetY = this.scales.getDrumOffset ();
        final int editMidiChannel = this.configuration.getMidiEditChannel ();
        for (int sound = 0; sound < 8; sound++)
        {
            for (int col = 0; col < DrumView8.NUM_DISPLAY_COLS; col++)
            {
                final int isSet = this.getClip ().getStep (editMidiChannel, col, offsetY + this.selectedPad + sound + this.soundOffset).getState ();
                final boolean hilite = col == hiStep;
                final int x = col % 8;
                int y = col / 8;
                y += sound;
                this.surface.getPadGrid ().lightEx (x, 7 - y, this.getStepColor (isSet, hilite));
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateNoteMapping ()
    {
        this.surface.setKeyTranslationTable (this.scales.translateMatrixToGrid (EMPTY_TABLE));
    }


    /** {@inheritDoc} */
    @Override
    protected String updateLowerSceneButtons (final int scene)
    {
        if (scene > 1)
            return AbstractSequencerView.COLOR_RESOLUTION_OFF;

        final int [] offsets =
        {
            0,
            8
        };

        return this.soundOffset == offsets[scene] ? AbstractSequencerView.COLOR_TRANSPOSE_SELECTED : AbstractSequencerView.COLOR_TRANSPOSE;
    }


    /** {@inheritDoc} */
    @Override
    protected void onLowerScene (final int index)
    {
        // 0, 8
        if (index > 1)
            return;
        this.soundOffset = index == 0 ? 0 : 8;
        this.surface.getDisplay ().notify ("Offset: " + this.soundOffset);
    }
}