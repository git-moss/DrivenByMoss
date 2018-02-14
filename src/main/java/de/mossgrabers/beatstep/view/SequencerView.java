// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.beatstep.view;

import de.mossgrabers.beatstep.controller.BeatstepColors;
import de.mossgrabers.beatstep.controller.BeatstepControlSurface;
import de.mossgrabers.framework.controller.grid.PadGrid;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITrackBank;
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
        tb.addNoteObserver ( (note, velocity) -> {
            // Light notes send from the sequencer
            for (int i = 0; i < 128; i++)
            {
                if (this.noteMap[i] == note)
                    this.pressedKeys[i] = velocity;
            }
        });
        tb.addTrackSelectionObserver ( (index, isSelected) -> this.clearPressedKeys ());
    }


    /** {@inheritDoc} */
    @Override
    public void onKnob (final int index, final int value)
    {
        if (index < 12)
        {
            this.extensions.onTrackKnob (index, value);
            return;
        }

        final boolean isInc = value >= 65;

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
                this.clearPressedKeys ();
                if (isInc)
                {
                    this.scales.incDrumOctave ();
                    this.model.getPrimaryDevice ().scrollDrumPadsPageDown ();
                }
                else
                {
                    this.scales.decDrumOctave ();
                    this.model.getPrimaryDevice ().scrollDrumPadsPageUp ();
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
                if (this.noteMap[note] == this.noteMap[i])
                    this.pressedKeys[i] = velocity;
            }
        }
        else
        {
            if (velocity != 0)
                this.clip.toggleStep (index < 8 ? index + 8 : index - 8, /* this.noteMap[] */ this.offsetY + this.selectedPad, this.configuration.isAccentActive () ? this.configuration.getFixedAccentValue () : velocity);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateNoteMapping ()
    {
        this.noteMap = this.model.canSelectedTrackHoldNotes () && this.isPlayMode ? this.scales.getNoteMatrix () : Scales.getEmptyMatrix ();
        this.surface.setKeyTranslationTable (this.noteMap);
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
                padGrid.light (i, this.pressedKeys[i] > 0 || this.selectedPad == i - 36 ? BeatstepColors.BEATSTEP_BUTTON_STATE_PINK : this.model.getColorManager ().getColor (this.scales.getColor (this.noteMap, i)));
            }
        }
        else
        {
            // Paint the sequencer steps
            final int step = this.clip.getCurrentStep ();
            final int hiStep = this.isInXRange (step) ? step % SequencerView.NUM_DISPLAY_COLS : -1;
            for (int col = 0; col < SequencerView.NUM_DISPLAY_COLS; col++)
            {
                final int isSet = this.clip.getStep (col, this.offsetY + this.selectedPad);
                final boolean hilite = col == hiStep;
                final int x = col % 8;
                final int y = col / 8;
                padGrid.lightEx (x, 1 - y, isSet > 0 ? hilite ? BeatstepColors.BEATSTEP_BUTTON_STATE_PINK : BeatstepColors.BEATSTEP_BUTTON_STATE_BLUE : hilite ? BeatstepColors.BEATSTEP_BUTTON_STATE_PINK : BeatstepColors.BEATSTEP_BUTTON_STATE_OFF);
            }
        }
    }
}