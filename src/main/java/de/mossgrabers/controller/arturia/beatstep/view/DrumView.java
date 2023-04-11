// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.arturia.beatstep.view;

import de.mossgrabers.controller.arturia.beatstep.controller.BeatstepColorManager;
import de.mossgrabers.controller.arturia.beatstep.controller.BeatstepControlSurface;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.daw.clip.NotePosition;
import de.mossgrabers.framework.daw.clip.StepState;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.IDrumDevice;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.IDrumPadBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;

import java.util.Optional;


/**
 * The Drum view.
 *
 * @author Jürgen Moßgraber
 */
public class DrumView extends BaseSequencerView
{
    private static final int NUM_DISPLAY_COLS = 16;


    /**
     * Constructor.
     *
     * @param surface The controller
     * @param model The model
     */
    public DrumView (final BeatstepControlSurface surface, final IModel model)
    {
        super ("Drum", surface, model, 128, DrumView.NUM_DISPLAY_COLS);

        final ITrackBank tb = model.getTrackBank ();
        tb.addSelectionObserver ( (index, isSelected) -> this.keyManager.clearPressedKeys ());
        tb.addNoteObserver (this::updateNote);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnob (final int index, final int value, final boolean isTurnedRight)
    {
        switch (index)
        {
            case 12:
                this.changeScrollPosition (isTurnedRight);
                break;

            case 13:
                this.changeResolution (value);
                this.surface.getDisplay ().notify (Resolution.getNameAt (this.getResolutionIndex ()));
                break;

            // Up/Down
            case 14:
                this.keyManager.clearPressedKeys ();
                if (isTurnedRight)
                {
                    this.scales.incDrumOctave ();
                    this.model.getDrumDevice ().getDrumPadBank ().selectNextPage ();
                }
                else
                {
                    this.scales.decDrumOctave ();
                    this.model.getDrumDevice ().getDrumPadBank ().selectPreviousPage ();
                }
                this.updateNoteMapping ();
                this.surface.getDisplay ().notify (this.scales.getDrumRangeText ());
                break;

            // Toggle play / sequencer
            case 15:
                this.isPlayMode = !isTurnedRight;
                this.surface.getDisplay ().notify (this.isPlayMode ? "Play/Select" : "Sequence");
                this.updateNoteMapping ();
                break;

            // 0-11
            default:
                this.extensions.onTrackKnob (index, value, isTurnedRight);
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (!this.model.canSelectedTrackHoldNotes ())
            return;

        final int index = note - 36;

        final int offsetY = this.scales.getDrumOffset ();
        if (this.isPlayMode)
        {
            this.selectedPad = index; // 0-16

            // Mark selected note
            this.keyManager.setKeyPressed (offsetY + this.selectedPad, velocity);
        }
        else
        {
            if (velocity != 0)
            {
                final NotePosition notePosition = new NotePosition (this.configuration.getMidiEditChannel (), index < 8 ? index + 8 : index - 8, offsetY + this.selectedPad);
                this.getClip ().toggleStep (notePosition, this.configuration.isAccentActive () ? this.configuration.getFixedAccentValue () : velocity);
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateNoteMapping ()
    {
        this.delayedUpdateNoteMapping (this.model.canSelectedTrackHoldNotes () && this.isPlayMode ? this.scales.getDrumMatrix () : EMPTY_TABLE);
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final IPadGrid padGrid = this.surface.getPadGrid ();
        if (!this.model.canSelectedTrackHoldNotes ())
        {
            padGrid.turnOff ();
            return;
        }

        if (this.isPlayMode)
        {
            final IDrumDevice primary = this.model.getDrumDevice ();
            for (int y = 0; y < 2; y++)
            {
                for (int x = 0; x < 8; x++)
                {
                    final int index = 8 * y + x;
                    padGrid.lightEx (x, y, this.getDrumPadColor (index, primary));
                }
            }
            return;
        }

        final INoteClip clip = this.getClip ();
        // Paint the sequencer steps
        final int step = clip.getCurrentStep ();
        final int hiStep = this.isInXRange (step) ? step % DrumView.NUM_DISPLAY_COLS : -1;
        final int offsetY = this.scales.getDrumOffset ();
        final NotePosition notePosition = new NotePosition (this.configuration.getMidiEditChannel (), 0, offsetY + this.selectedPad);
        for (int col = 0; col < DrumView.NUM_DISPLAY_COLS; col++)
        {
            notePosition.setStep (col);
            final StepState stepState = clip.getStep (notePosition).getState ();
            final boolean hilite = col == hiStep;
            final int x = col % 8;
            final int y = col / 8;
            padGrid.lightEx (x, 1 - y, getSequencerPadColor (stepState, hilite));
        }
    }


    private static int getSequencerPadColor (final StepState stepState, final boolean hilite)
    {
        if (stepState != StepState.OFF)
            return hilite ? BeatstepColorManager.BEATSTEP_BUTTON_STATE_PINK : BeatstepColorManager.BEATSTEP_BUTTON_STATE_BLUE;
        return hilite ? BeatstepColorManager.BEATSTEP_BUTTON_STATE_PINK : BeatstepColorManager.BEATSTEP_BUTTON_STATE_OFF;
    }


    private int getDrumPadColor (final int index, final IDrumDevice primary)
    {
        final int offsetY = this.scales.getDrumOffset ();

        // Playing note?
        if (this.keyManager.isKeyPressed (offsetY + index))
            return BeatstepColorManager.BEATSTEP_BUTTON_STATE_PINK;

        // Selected?
        if (this.selectedPad == index)
            return BeatstepColorManager.BEATSTEP_BUTTON_STATE_RED;

        // Exists and active?
        final IDrumPadBank drumPadBank = primary.getDrumPadBank ();
        final IChannel drumPad = drumPadBank.getItem (index);
        if (!drumPad.doesExist () || !drumPad.isActivated ())
            return BeatstepColorManager.BEATSTEP_BUTTON_STATE_OFF;

        // Muted or soloed?
        if (drumPad.isMute () || drumPadBank.hasSoloedPads () && !drumPad.isSolo ())
            return BeatstepColorManager.BEATSTEP_BUTTON_STATE_OFF;
        return BeatstepColorManager.BEATSTEP_BUTTON_STATE_BLUE;
    }


    /**
     * The callback function for playing note changes.
     *
     * @param trackIndex The index of the track on which the note is playing
     * @param note The played note
     * @param velocity The played velocity
     */
    private void updateNote (final int trackIndex, final int note, final int velocity)
    {
        final Optional<ITrack> sel = this.model.getCurrentTrackBank ().getSelectedItem ();
        if (sel.isPresent () && sel.get ().getIndex () == trackIndex)
            this.keyManager.setKeyPressed (note, velocity);
    }
}