// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.beatstep.view;

import de.mossgrabers.beatstep.controller.BeatstepColors;
import de.mossgrabers.beatstep.controller.BeatstepControlSurface;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.controller.grid.PadGrid;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.scale.Scales;


/**
 * The Drum view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
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
    public DrumView (final BeatstepControlSurface surface, final Model model)
    {
        super ("Drum", surface, model, 128, DrumView.NUM_DISPLAY_COLS);

        this.offsetY = Scales.DRUM_NOTE_START;

        final ITrackBank tb = model.getTrackBank ();
        // Light notes send from the sequencer
        tb.addNoteObserver ( (note, velocity) -> this.pressedKeys[note] = velocity);
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
                this.offsetY = Scales.DRUM_NOTE_START + this.scales.getDrumOctave () * 16;
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

            // Mark selected note
            this.pressedKeys[this.offsetY + this.selectedPad] = velocity;
        }
        else
        {
            if (velocity != 0)
                this.clip.toggleStep (index < 8 ? index + 8 : index - 8, this.offsetY + this.selectedPad, this.configuration.isAccentActive () ? this.configuration.getFixedAccentValue () : velocity);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateNoteMapping ()
    {
        this.noteMap = this.model.canSelectedTrackHoldNotes () && this.isPlayMode ? this.scales.getDrumMatrix () : Scales.getEmptyMatrix ();
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
            final ICursorDevice primary = this.model.getPrimaryDevice ();
            final boolean hasDrumPads = primary.hasDrumPads ();
            boolean isSoloed = false;
            if (hasDrumPads)
            {
                for (int i = 0; i < 16; i++)
                {
                    if (primary.getDrumPad (i).isSolo ())
                    {
                        isSoloed = true;
                        break;
                    }
                }
            }
            for (int y = 0; y < 2; y++)
            {
                for (int x = 0; x < 8; x++)
                {
                    final int index = 8 * y + x;
                    padGrid.lightEx (x, y, this.getPadColor (index, primary, isSoloed));
                }
            }
        }
        else
        {
            // Paint the sequencer steps
            final int step = this.clip.getCurrentStep ();
            final int hiStep = this.isInXRange (step) ? step % DrumView.NUM_DISPLAY_COLS : -1;
            for (int col = 0; col < DrumView.NUM_DISPLAY_COLS; col++)
            {
                final int isSet = this.clip.getStep (col, this.offsetY + this.selectedPad);
                final boolean hilite = col == hiStep;
                final int x = col % 8;
                final int y = col / 8;
                padGrid.lightEx (x, 1 - y, isSet > 0 ? hilite ? BeatstepColors.BEATSTEP_BUTTON_STATE_PINK : BeatstepColors.BEATSTEP_BUTTON_STATE_BLUE : hilite ? BeatstepColors.BEATSTEP_BUTTON_STATE_PINK : BeatstepColors.BEATSTEP_BUTTON_STATE_OFF);
            }
        }
    }


    private int getPadColor (final int index, final ICursorDevice primary, final boolean isSoloed)
    {
        // Playing note?
        if (this.pressedKeys[this.offsetY + index] > 0)
            return BeatstepColors.BEATSTEP_BUTTON_STATE_PINK;
        // Selected?
        if (this.selectedPad == index)
            return BeatstepColors.BEATSTEP_BUTTON_STATE_RED;
        // Exists and active?
        final IChannel drumPad = primary.getDrumPad (index);
        if (!drumPad.doesExist () || !drumPad.isActivated ())
            return BeatstepColors.BEATSTEP_BUTTON_STATE_OFF;
        // Muted or soloed?
        if (drumPad.isMute () || isSoloed && !drumPad.isSolo ())
            return BeatstepColors.BEATSTEP_BUTTON_STATE_OFF;
        return BeatstepColors.BEATSTEP_BUTTON_STATE_BLUE;
    }
}