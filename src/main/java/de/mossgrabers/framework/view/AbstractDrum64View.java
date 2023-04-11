// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.view;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.IDrumPadBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.featuregroup.AbstractView;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.sequencer.AbstractDrumView;

import java.util.Optional;


/**
 * Abstract implementation for a 64 (or more) drum grid consisting of 4x4 blocks. Blocks start on
 * the left bottom, then go up the column and continue then on the right bottom.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author Jürgen Moßgraber
 */
public abstract class AbstractDrum64View<S extends IControlSurface<C>, C extends Configuration> extends AbstractView<S, C> implements TransposeView
{
    protected static final int DRUM_START_KEY = 36;
    protected static final int GRID_COLUMNS   = 8;
    protected static final int BLOCK_SIZE     = 16;

    protected int              offsetY;
    protected int              selectedPad    = 0;
    protected int []           pressedKeys    = new int [128];
    protected int              columns;
    protected int              rows;
    protected int              drumOctave;

    private final int          xblocks;
    private final int          yblocks;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    protected AbstractDrum64View (final S surface, final IModel model)
    {
        this (surface, model, 8, 8);
    }


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     * @param columns The number of columns of the grid
     * @param rows The number of rows of the grid
     */
    protected AbstractDrum64View (final S surface, final IModel model, final int columns, final int rows)
    {
        super ("Drum 64", surface, model);

        this.columns = columns;
        this.rows = rows;

        // The number of 4x4 blocks in x and y direction
        this.xblocks = this.columns / 4;
        this.yblocks = this.rows / 4;

        this.offsetY = DRUM_START_KEY;

        this.drumOctave = 0;

        final ITrackBank tb = model.getTrackBank ();
        tb.addSelectionObserver ( (final int index, final boolean isSelected) -> this.clearPressedKeys ());
        tb.addNoteObserver (this::updateNote);
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        super.onActivate ();

        this.getDrumPadBank ().setIndication (true);
    }


    /** {@inheritDoc} */
    @Override
    public void onDeactivate ()
    {
        super.onDeactivate ();

        this.getDrumPadBank ().setIndication (false);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (!this.model.canSelectedTrackHoldNotes ())
            return;

        final int index = note - 36;
        final int x = index % this.columns;
        final int y = index / this.columns;
        final int xblockPos = x / 4;
        final int yblockPos = y / 4;
        final int blocks = xblockPos * this.yblocks * 16 + yblockPos * 16;

        this.selectedPad = blocks + y % 4 * 4 + x % 4;

        final int playedPad = velocity == 0 ? -1 : this.selectedPad;

        // Mark selected note
        this.pressedKeys[this.offsetY + this.selectedPad] = velocity;

        if (playedPad < 0)
            return;

        this.handleButtonCombinations (playedPad);
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

        final IDrumPadBank drumPadBank = this.getDrumPadBank ();
        final boolean isRecording = this.model.hasRecordingState ();

        int blockOffset = 0;

        // Draw all blocks
        for (int xblock = 0; xblock < this.xblocks; xblock++)
        {
            for (int yblock = 0; yblock < this.yblocks; yblock++)
            {
                // Draw a 4x4 square
                for (int blockX = 0; blockX < 4; blockX++)
                {
                    for (int blockY = 0; blockY < 4; blockY++)
                    {
                        final int index = blockOffset + blockY * 4 + blockX;

                        final int x = xblock * 4 + blockX;
                        final int y = yblock * 4 + blockY;

                        padGrid.lightEx (x, this.rows - 1 - y, this.getDrumPadColor (index, drumPadBank, isRecording));
                    }
                }

                blockOffset += 16;
            }
        }
    }


    private String getDrumPadColor (final int index, final IDrumPadBank drumPadBank, final boolean isRecording)
    {
        // Playing note?
        if (this.pressedKeys[this.offsetY + index] > 0)
            return isRecording ? AbstractDrumView.COLOR_PAD_RECORD : AbstractDrumView.COLOR_PAD_PLAY;
        // Selected?
        if (this.selectedPad == index)
            return AbstractDrumView.COLOR_PAD_SELECTED;

        // Exists and active?
        final IChannel drumPad = drumPadBank.getItem (index);
        if (!drumPad.doesExist () || !drumPad.isActivated ())
            return this.surface.getConfiguration ().isTurnOffEmptyDrumPads () ? AbstractDrumView.COLOR_PAD_OFF : AbstractDrumView.COLOR_PAD_NO_CONTENT;

        // Muted or soloed?
        if (drumPad.isMute () || drumPadBank.hasSoloedPads () && !drumPad.isSolo ())
            return AbstractDrumView.COLOR_PAD_MUTED;

        return this.getPadContentColor (drumPad);
    }


    protected String getPadContentColor (final IChannel drumPad)
    {
        return DAWColor.getColorID (drumPad.getColor ());
    }


    private void clearPressedKeys ()
    {
        for (int i = 0; i < 128; i++)
            this.pressedKeys[i] = 0;
    }


    /** {@inheritDoc} */
    @Override
    public void updateNoteMapping ()
    {
        final boolean turnOn = this.model.canSelectedTrackHoldNotes () && !this.surface.isSelectPressed () && !this.surface.isDeletePressed () && !this.surface.isMutePressed () && !this.surface.isSoloPressed ();
        this.delayedUpdateNoteMapping (turnOn ? this.getDrumMatrix () : EMPTY_TABLE);
    }


    /** {@inheritDoc} */
    @Override
    public void onOctaveDown (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        this.clearPressedKeys ();
        final int oldDrumOctave = this.drumOctave;
        this.drumOctave = Math.max (-2, this.drumOctave - 1);
        this.offsetY = DRUM_START_KEY + this.drumOctave * BLOCK_SIZE;
        this.updateNoteMapping ();
        this.surface.getDisplay ().notify (this.getDrumRangeText ());

        if (oldDrumOctave != this.drumOctave)
        {
            final IDrumPadBank drumPadBank = this.getDrumPadBank ();
            for (int i = 0; i < BLOCK_SIZE; i++)
                drumPadBank.scrollBackwards ();
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onOctaveUp (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        this.clearPressedKeys ();
        final int oldDrumOctave = this.drumOctave;
        this.drumOctave = Math.min (1, this.drumOctave + 1);
        this.offsetY = DRUM_START_KEY + this.drumOctave * BLOCK_SIZE;
        this.updateNoteMapping ();
        this.surface.getDisplay ().notify (this.getDrumRangeText ());
        if (oldDrumOctave != this.drumOctave)
        {
            final IDrumPadBank drumPadBank = this.getDrumPadBank ();
            for (int i = 0; i < BLOCK_SIZE; i++)
                drumPadBank.scrollForwards ();
        }
    }


    /** {@inheritDoc} */
    @Override
    public boolean isOctaveUpButtonOn ()
    {
        return this.drumOctave < 1;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isOctaveDownButtonOn ()
    {
        return this.drumOctave > Scales.DRUM_OCTAVE_LOWER;
    }


    protected void handleButtonCombinations (final int playedPad)
    {
        if (this.surface.isDeletePressed ())
        {
            // Delete all of the notes on that "pad"
            this.handleDeleteButton (playedPad);
        }
        else if (this.surface.isMutePressed ())
        {
            // Mute that "pad"
            this.handleMuteButton (playedPad);
        }
        else if (this.surface.isSoloPressed ())
        {
            // Solo that "pad"
            this.handleSoloButton (playedPad);
        }
        else if (this.surface.isSelectPressed () || this.surface.getConfiguration ().isAutoSelectDrum ())
        {
            // Also select the matching device layer channel of the pad
            this.handleSelectButton (playedPad);
        }

        this.updateNoteMapping ();
    }


    @SuppressWarnings("unused")
    protected void handleDeleteButton (final int playedPad)
    {
        // Intentionally empty
    }


    protected void handleMuteButton (final int playedPad)
    {
        this.surface.setTriggerConsumed (ButtonID.MUTE);
        this.getDrumPadBank ().getItem (playedPad).toggleMute ();
    }


    protected void handleSoloButton (final int playedPad)
    {
        this.surface.setTriggerConsumed (ButtonID.SOLO);
        this.getDrumPadBank ().getItem (playedPad).toggleSolo ();
    }


    @SuppressWarnings("unused")
    protected void handleSelectButton (final int playedPad)
    {
        // Intentionally empty
    }


    private int [] getDrumMatrix ()
    {
        final int [] noteMap = Scales.getEmptyMatrix ();

        int blockOffset = 0;

        // All blocks
        for (int xblock = 0; xblock < this.xblocks; xblock++)
        {
            for (int yblock = 0; yblock < this.yblocks; yblock++)
            {
                // One 4x4 square
                for (int blockX = 0; blockX < 4; blockX++)
                {
                    for (int blockY = 0; blockY < 4; blockY++)
                    {
                        final int index = blockOffset + blockY * 4 + blockX;

                        final int x = xblock * 4 + blockX;
                        final int y = yblock * 4 + blockY;

                        final int note = 36 + y * this.columns + x;
                        noteMap[note] = index + this.offsetY;
                        if (noteMap[note] < -1 || noteMap[note] > 127)
                            noteMap[note] = -1;
                    }
                }

                blockOffset += 16;
            }
        }

        return noteMap;
    }


    private String getDrumRangeText ()
    {
        final int s = DRUM_START_KEY + this.drumOctave * 64;
        return Scales.formatDrumNote (s) + " to " + Scales.formatDrumNote (s + 63);
    }


    /**
     * Get the drum octave.
     *
     * @return The drum octave
     */
    public int getDrumOctave ()
    {
        return this.drumOctave;
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
            this.pressedKeys[note] = velocity;
    }


    protected IDrumPadBank getDrumPadBank ()
    {
        return this.model.getDrumDevice (64).getDrumPadBank ();
    }
}
