// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.view;

import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.controller.push.mode.NoteMode;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.controller.hardware.IHwButton;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.daw.IStepInfo;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.view.AbstractSequencerView;
import de.mossgrabers.framework.view.Views;


/**
 * The Drum 4 view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DrumView4 extends DrumViewBase
{
    private static final int NUM_DISPLAY_COLS = 16;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public DrumView4 (final PushControlSurface surface, final IModel model)
    {
        super (Views.VIEW_NAME_DRUM4, surface, model, 2, 0);

        this.soundOffset = 0;
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

        final int sound = y % 4 + this.soundOffset;
        final int offsetY = this.scales.getDrumOffset ();
        final int col = 8 * (1 - y / 4) + x;
        final int row = offsetY + this.selectedPad + sound;

        final int channel = this.configuration.getMidiEditChannel ();
        final int vel = this.configuration.isAccentActive () ? this.configuration.getFixedAccentValue () : this.surface.getButton (ButtonID.get (ButtonID.PAD1, index)).getPressedVelocity ();
        final INoteClip clip = this.getClip ();

        // Handle note duplicate function
        final IHwButton duplicateButton = this.surface.getButton (ButtonID.DUPLICATE);
        if (duplicateButton != null && duplicateButton.isPressed ())
        {
            duplicateButton.setConsumed ();
            final IStepInfo noteStep = clip.getStep (channel, col, row);
            if (noteStep.getState () == IStepInfo.NOTE_START)
                this.copyNote = noteStep;
            else if (this.copyNote != null)
                clip.setStep (channel, col, row, this.copyNote);
            return;
        }

        clip.toggleStep (channel, col, row, vel);
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

        final int sound = y % 4 + this.soundOffset;
        final int stepX = 8 * (1 - y / 4) + x;
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
        final IPadGrid padGrid = this.surface.getPadGrid ();
        if (!this.model.canSelectedTrackHoldNotes () || !this.isActive ())
        {
            padGrid.turnOff ();
            return;
        }

        // Clip length/loop area
        final INoteClip clip = this.getClip ();
        final int step = clip.getCurrentStep ();

        // Paint the sequencer steps
        final int hiStep = this.isInXRange (step) ? step % DrumView4.NUM_DISPLAY_COLS : -1;
        final int offsetY = this.scales.getDrumOffset ();
        final int editMidiChannel = this.configuration.getMidiEditChannel ();
        for (int sound = 0; sound < 4; sound++)
        {
            for (int col = 0; col < DrumView4.NUM_DISPLAY_COLS; col++)
            {
                final int isSet = clip.getStep (editMidiChannel, col, offsetY + this.selectedPad + sound + this.soundOffset).getState ();
                final boolean hilite = col == hiStep;
                final int x = col % 8;
                int y = col / 8;
                if (col < 8)
                    y += 5;
                y += sound;
                padGrid.lightEx (x, 8 - y, this.getStepColor (isSet, hilite));
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
    protected void onLowerScene (final int index)
    {
        if (index > 3)
            return;

        // 7, 6, 5, 4
        this.soundOffset = 4 * index;
        this.surface.getDisplay ().notify ("Offset: " + this.soundOffset);
    }


    /** {@inheritDoc} */
    @Override
    protected String updateLowerSceneButtons (final int scene)
    {
        final int [] offsets =
        {
            0,
            4,
            8,
            12
        };

        return this.soundOffset == offsets[scene] ? AbstractSequencerView.COLOR_TRANSPOSE_SELECTED : AbstractSequencerView.COLOR_TRANSPOSE;
    }
}