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
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.scale.Scales;

import java.util.Optional;


/**
 * The Sequencer view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SequencerView extends BaseSequencerView
{
    private static final int NUM_DISPLAY_COLS = 16;
    private static final int START_KEY        = 36;

    protected int            offsetY;


    /**
     * Constructor.
     *
     * @param surface The controller
     * @param model The model
     */
    public SequencerView (final BeatstepControlSurface surface, final IModel model)
    {
        super ("Sequencer", surface, model, 128, SequencerView.NUM_DISPLAY_COLS);

        this.offsetY = SequencerView.START_KEY;

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
                    this.scales.incOctave ();
                else
                    this.scales.decOctave ();
                this.updateNoteMapping ();
                this.surface.getDisplay ().notify (this.scales.getRangeText ());
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

        if (this.isPlayMode)
        {
            this.selectedPad = index; // 0-16

            // Mark selected notes
            for (int i = 0; i < 128; i++)
            {
                if (this.keyManager.map (note) == this.keyManager.map (i))
                    this.keyManager.setKeyPressed (i, velocity);
            }
        }
        else
        {
            if (velocity != 0)
            {
                final int step = index < 8 ? index + 8 : index - 8;
                final int y = this.offsetY + this.selectedPad;
                final int map = this.scales.getNoteMatrix ()[y];
                final NotePosition notePosition = new NotePosition (this.configuration.getMidiEditChannel (), step, map);
                this.getClip ().toggleStep (notePosition, this.configuration.isAccentActive () ? this.configuration.getFixedAccentValue () : velocity);
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateNoteMapping ()
    {
        this.delayedUpdateNoteMapping (this.model.canSelectedTrackHoldNotes () && this.isPlayMode ? this.scales.getNoteMatrix () : Scales.getEmptyMatrix ());
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
            for (int i = 36; i < 52; i++)
            {
                padGrid.light (i, this.keyManager.isKeyPressed (i) || this.selectedPad == i - 36 ? BeatstepColorManager.BEATSTEP_BUTTON_STATE_PINK : this.colorManager.getColorIndex (this.keyManager.getColor (i)));
            }
        }
        else
        {
            final INoteClip clip = this.getClip ();
            // Paint the sequencer steps
            final int step = clip.getCurrentStep ();
            final int hiStep = this.isInXRange (step) ? step % SequencerView.NUM_DISPLAY_COLS : -1;
            final NotePosition notePosition = new NotePosition (this.configuration.getMidiEditChannel (), 0, 0);
            for (int col = 0; col < SequencerView.NUM_DISPLAY_COLS; col++)
            {
                notePosition.setStep (col);
                notePosition.setNote (this.scales.getNoteMatrix ()[this.offsetY + this.selectedPad]);
                final StepState stepState = clip.getStep (notePosition).getState ();
                padGrid.lightEx (col % 8, 1 - col / 8, getSequencerColor (stepState, col == hiStep));
            }
        }
    }


    private static int getSequencerColor (final StepState stepState, final boolean hilite)
    {
        if (stepState != StepState.OFF)
            return hilite ? BeatstepColorManager.BEATSTEP_BUTTON_STATE_PINK : BeatstepColorManager.BEATSTEP_BUTTON_STATE_BLUE;
        return hilite ? BeatstepColorManager.BEATSTEP_BUTTON_STATE_PINK : BeatstepColorManager.BEATSTEP_BUTTON_STATE_OFF;
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
        if (sel.isEmpty () || sel.get ().getIndex () != trackIndex)
            return;

        // Light notes sent from the sequencer
        for (int i = 0; i < 128; i++)
        {
            if (this.keyManager.map (i) == note)
                this.keyManager.setKeyPressed (i, velocity);
        }
    }
}