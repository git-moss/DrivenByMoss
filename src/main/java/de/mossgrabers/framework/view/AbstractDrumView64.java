// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.view;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IDrumPadBank;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Abstract implementation for a 64 drum grid.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractDrumView64<S extends IControlSurface<C>, C extends Configuration> extends AbstractView<S, C> implements TransposeView
{
    protected static final int    DRUM_START_KEY = 36;
    protected static final int    GRID_COLUMNS   = 8;
    protected static final int    BLOCK_SIZE     = 16;

    // @formatter:off
    protected static final int [] DRUM_MATRIX    =
    {
        0,  1,  2,  3, 32, 33, 34, 35,      // 1st row
        4,  5,  6,  7, 36, 37, 38, 39,
        8,  9, 10, 11, 40, 41, 42, 43,
       12, 13, 14, 15, 44, 45, 46, 47,
       16, 17, 18, 19, 48, 49, 50, 51,
       20, 21, 22, 23, 52, 53, 54, 55,
       24, 25, 26, 27, 56, 57, 58, 59,
       28, 29, 30, 31, 60, 61, 62, 63      // 8th row
    };
    // @formatter:on

    protected int                 offsetY;
    protected int                 selectedPad    = 0;
    protected int []              pressedKeys    = new int [128];
    protected int                 columns;
    protected int                 rows;
    protected int                 drumOctave;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public AbstractDrumView64 (final S surface, final IModel model)
    {
        super ("Drum 64", surface, model);

        this.offsetY = DRUM_START_KEY;

        this.canScrollUp = false;
        this.canScrollDown = false;

        this.columns = 8;
        this.rows = 8;

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

        final ICursorDevice drumDevice64 = this.model.getDrumDevice64 ();
        drumDevice64.getDrumPadBank ().setIndication (true);
    }


    /** {@inheritDoc} */
    @Override
    public void onDeactivate ()
    {
        super.onDeactivate ();

        final ICursorDevice drumDevice64 = this.model.getDrumDevice64 ();
        drumDevice64.getDrumPadBank ().setIndication (false);
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
        this.selectedPad = (x >= 4 ? 32 : 0) + y * 4 + x % 4;

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

        // halfColumns x playLines Drum Pad Grid
        final ICursorDevice drumDevice64 = this.model.getDrumDevice64 ();
        final boolean isSoloed = drumDevice64.hasDrumPads () && drumDevice64.getDrumPadBank ().hasSoloedPads ();
        final int numPads = this.rows * this.columns;
        final boolean isRecording = this.model.hasRecordingState ();
        for (int index = 0; index < numPads; index++)
        {
            final int x = index / 32 * 4 + index % 4;
            final int y = index / 4 % 8;
            padGrid.lightEx (x, 7 - y, this.getPadColor (index, drumDevice64, isSoloed, isRecording));
        }
    }


    private String getPadColor (final int index, final ICursorDevice primary, final boolean isSoloed, final boolean isRecording)
    {
        // Playing note?
        if (this.pressedKeys[this.offsetY + index] > 0)
            return isRecording ? AbstractDrumView.COLOR_PAD_RECORD : AbstractDrumView.COLOR_PAD_PLAY;
        // Selected?
        if (this.selectedPad == index)
            return AbstractDrumView.COLOR_PAD_SELECTED;

        // Exists and active?
        final IChannel drumPad = primary.getDrumPadBank ().getItem (index);
        if (!drumPad.doesExist () || !drumPad.isActivated ())
            return this.surface.getConfiguration ().isTurnOffEmptyDrumPads () ? AbstractDrumView.COLOR_PAD_OFF : AbstractDrumView.COLOR_PAD_NO_CONTENT;
        // Muted or soloed?
        if (drumPad.isMute () || isSoloed && !drumPad.isSolo ())
            return AbstractDrumView.COLOR_PAD_MUTED;

        return this.getPadContentColor (drumPad);
    }


    protected String getPadContentColor (final IChannel drumPad)
    {
        return DAWColor.getColorIndex (drumPad.getColor ());
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
            final ICursorDevice drumDevice64 = this.model.getDrumDevice64 ();
            final IDrumPadBank drumPadBank = drumDevice64.getDrumPadBank ();
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
            final ICursorDevice drumDevice64 = this.model.getDrumDevice64 ();
            final IDrumPadBank drumPadBank = drumDevice64.getDrumPadBank ();
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
        this.model.getDrumDevice64 ().getDrumPadBank ().getItem (playedPad).toggleMute ();
    }


    protected void handleSoloButton (final int playedPad)
    {
        this.surface.setTriggerConsumed (ButtonID.SOLO);
        this.model.getDrumDevice64 ().getDrumPadBank ().getItem (playedPad).toggleSolo ();
    }


    @SuppressWarnings("unused")
    protected void handleSelectButton (final int playedPad)
    {
        // Intentionally empty
    }


    private int [] getDrumMatrix ()
    {
        final int [] matrix = DRUM_MATRIX;
        final int [] noteMap = Scales.getEmptyMatrix ();
        for (int i = 0; i < 64; i++)
        {
            final int n = matrix[i] == -1 ? -1 : matrix[i] + this.offsetY;
            noteMap[DRUM_START_KEY + i] = n < 0 || n > 127 ? -1 : n;
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
        final ITrack sel = this.model.getCurrentTrackBank ().getSelectedItem ();
        if (sel != null && sel.getIndex () == trackIndex)
            this.pressedKeys[note] = velocity;
    }
}
