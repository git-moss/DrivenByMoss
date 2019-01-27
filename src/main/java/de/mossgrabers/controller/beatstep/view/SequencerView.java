// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.beatstep.view;

import de.mossgrabers.controller.beatstep.controller.BeatstepColors;
import de.mossgrabers.controller.beatstep.controller.BeatstepControlSurface;
import de.mossgrabers.framework.controller.grid.PadGrid;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.scale.Scales;


/**
 * The Sequencer view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SequencerView extends BaseSequencerView
{
    private static final int NUM_DISPLAY_COLS = 16;
    private static final int START_KEY        = 36;


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

        // Light notes send from the sequencer
        for (int i = 0; i < tb.getPageSize (); i++)
            tb.getItem (i).addNoteObserver (this::updateNote);
        tb.addSelectionObserver ( (index, isSelected) -> this.keyManager.clearPressedKeys ());
    }


    /** {@inheritDoc} */
    @Override
    public void onKnob (final int index, final int value)
    {
        switch (index)
        {
            case 12:
                this.changeScrollPosition (value);
                break;

            case 13:
                this.changeResolution (value);
                this.surface.getDisplay ().notify (RESOLUTION_TEXTS[this.selectedIndex]);
                break;

            // Up/Down
            case 14:
                this.keyManager.clearPressedKeys ();
                final boolean isInc = value >= 65;
                if (isInc)
                {
                    this.scales.incDrumOctave ();
                    this.model.getInstrumentDevice ().getDrumPadBank ().scrollPageForwards ();
                }
                else
                {
                    this.scales.decDrumOctave ();
                    this.model.getInstrumentDevice ().getDrumPadBank ().scrollPageBackwards ();
                }
                this.offsetY = SequencerView.START_KEY + this.scales.getDrumOctave () * 16;
                this.updateNoteMapping ();
                this.surface.getDisplay ().notify (this.scales.getDrumRangeText ());
                break;

            // Toggle play / sequencer
            case 15:
                this.isPlayMode = !this.isPlayMode;
                this.surface.getDisplay ().notify (this.isPlayMode ? "Play/Select" : "Sequence");
                this.updateNoteMapping ();
                break;

            // 0-11
            default:
                this.extensions.onTrackKnob (index, value);
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
                this.getClip ().toggleStep (index < 8 ? index + 8 : index - 8, /* this.noteMap[] */ this.offsetY + this.selectedPad, this.configuration.isAccentActive () ? this.configuration.getFixedAccentValue () : velocity);
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
        final PadGrid padGrid = this.surface.getPadGrid ();
        if (!this.model.canSelectedTrackHoldNotes ())
        {
            padGrid.turnOff ();
            return;
        }

        if (this.isPlayMode)
        {
            for (int i = 36; i < 52; i++)
            {
                padGrid.light (i, this.keyManager.isKeyPressed (i) || this.selectedPad == i - 36 ? BeatstepColors.BEATSTEP_BUTTON_STATE_PINK : this.model.getColorManager ().getColor (this.keyManager.getColor (i)));
            }
        }
        else
        {
            final INoteClip clip = this.getClip ();
            // Paint the sequencer steps
            final int step = clip.getCurrentStep ();
            final int hiStep = this.isInXRange (step) ? step % SequencerView.NUM_DISPLAY_COLS : -1;
            for (int col = 0; col < SequencerView.NUM_DISPLAY_COLS; col++)
            {
                final int isSet = clip.getStep (col, this.offsetY + this.selectedPad);
                padGrid.lightEx (col % 8, 1 - (col / 8), getSequencerColor (isSet, col == hiStep));
            }
        }
    }


    private static int getSequencerColor (final int isSet, final boolean hilite)
    {
        if (isSet > 0)
            return hilite ? BeatstepColors.BEATSTEP_BUTTON_STATE_PINK : BeatstepColors.BEATSTEP_BUTTON_STATE_BLUE;
        return hilite ? BeatstepColors.BEATSTEP_BUTTON_STATE_PINK : BeatstepColors.BEATSTEP_BUTTON_STATE_OFF;
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
        final ITrack sel = this.model.getCurrentTrackBank ().getSelectedItem ();
        if (sel != null && sel.getIndex () == trackIndex)
        {
            // Light notes send from the sequencer
            for (int i = 0; i < 128; i++)
            {
                if (this.keyManager.map (i) == note)
                    this.keyManager.setKeyPressed (i, velocity);
            }
        }
    }
}