// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.view;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.controller.grid.PadGrid;
import de.mossgrabers.framework.daw.DAWColors;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IDrumPadBank;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Abstract implementation for a drum sequencer. The grid is split into 3 areas: The sequencer area
 * where steps are displayed, the play area where you can play sounds and the measure are which
 * displays the length of the clip.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractDrumView<S extends IControlSurface<C>, C extends Configuration> extends AbstractSequencerView<S, C> implements TransposeView
{
    /** The color ID for the recording state. */
    public static final String COLOR_PAD_RECORD      = "COLOR_PAD_RECORD";
    /** The color ID for the play state. */
    public static final String COLOR_PAD_PLAY        = "COLOR_PAD_PLAY";
    /** The color ID for the selected state. */
    public static final String COLOR_PAD_SELECTED    = "COLOR_PAD_SELECTED";
    /** The color ID for the mute state. */
    public static final String COLOR_PAD_MUTED       = "COLOR_PAD_MUTED";
    /** The color ID for the has-content state. */
    public static final String COLOR_PAD_HAS_CONTENT = "COLOR_PAD_HAS_CONTENT";
    /** The color ID for the no-content state. */
    public static final String COLOR_PAD_NO_CONTENT  = "COLOR_PAD_NO_CONTENT";
    /** The color ID for the off state. */
    public static final String COLOR_PAD_OFF         = "COLOR_PAD_OFF";

    static final int           DRUM_START_KEY        = 36;
    static final int           GRID_COLUMNS          = 8;

    protected int              selectedPad;
    private int                loopPadPressed        = -1;
    private int                sequencerLines;
    private int                playLines;
    private int                allLines;
    private int                sequencerSteps;
    private int                halfColumns;


    /**
     * Constructor.
     *
     * @param name The name of the view
     * @param surface The surface
     * @param model The model
     * @param numSequencerLines The number of rows to use for the sequencer
     * @param numPlayLines The number of rows to use for playing
     */
    public AbstractDrumView (final String name, final S surface, final IModel model, final int numSequencerLines, final int numPlayLines)
    {
        super (name, surface, model, 128, numSequencerLines * GRID_COLUMNS);

        this.sequencerLines = numSequencerLines;
        this.playLines = numPlayLines;
        this.allLines = this.sequencerLines + this.playLines;
        this.sequencerSteps = numSequencerLines * GRID_COLUMNS;
        this.halfColumns = GRID_COLUMNS / 2;

        this.offsetY = AbstractDrumView.DRUM_START_KEY;

        this.canScrollUp = false;
        this.canScrollDown = false;

        final ITrackBank tb = model.getTrackBank ();
        // Light notes send from the sequencer
        for (int i = 0; i < tb.getPageSize (); i++)
            tb.getItem (i).addNoteObserver (this::updateNote);
        tb.addSelectionObserver ( (index, isSelected) -> this.keyManager.clearPressedKeys ());
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        super.onActivate ();

        final ICursorDevice primary = this.model.getInstrumentDevice ();
        primary.getDrumPadBank ().setIndication (true);
    }


    /** {@inheritDoc} */
    @Override
    public void onDeactivate ()
    {
        super.onDeactivate ();

        final ICursorDevice primary = this.model.getInstrumentDevice ();
        primary.getDrumPadBank ().setIndication (false);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (!this.model.canSelectedTrackHoldNotes ())
            return;

        final int index = note - DRUM_START_KEY;
        final int x = index % GRID_COLUMNS;
        final int y = index / GRID_COLUMNS;

        // Sequencer steps
        final INoteClip clip = this.getClip ();
        if (y >= this.playLines)
        {
            if (velocity != 0)
            {
                final int col = GRID_COLUMNS * (this.allLines - 1 - y) + x;
                clip.toggleStep (col, this.offsetY + this.selectedPad, this.configuration.isAccentActive () ? this.configuration.getFixedAccentValue () : velocity);
            }
            return;
        }

        // halfColumns x playLines Drum Pad Grid
        if (x < this.halfColumns)
        {
            this.selectedPad = this.halfColumns * y + x;
            final int playedPad = velocity == 0 ? -1 : this.selectedPad;

            // Mark selected note
            this.keyManager.setKeyPressed (this.offsetY + this.selectedPad, velocity);
            this.playNote (this.offsetY + this.selectedPad, velocity);

            if (playedPad < 0)
                return;

            this.handleButtonCombinations (playedPad);
            return;
        }

        // Clip length/loop area
        final int pad = (this.playLines - 1 - y) * this.halfColumns + x - this.halfColumns;

        // Button pressed?
        if (velocity > 0)
        {
            // Not yet a button pressed, store it
            if (this.loopPadPressed == -1)
                this.loopPadPressed = pad;
            return;
        }

        if (this.loopPadPressed == -1)
            return;

        if (pad == this.loopPadPressed && pad != clip.getEditPage ())
        {
            // Only single pad pressed -> page selection
            clip.scrollToPage (pad);
        }
        else
        {
            final int start = this.loopPadPressed < pad ? this.loopPadPressed : pad;
            final int end = (this.loopPadPressed < pad ? pad : this.loopPadPressed) + 1;
            final int lengthOfOnePad = this.getLengthOfOnePage (this.sequencerSteps);

            // Set a new loop between the 2 selected pads
            final int newStart = start * lengthOfOnePad;
            clip.setLoopStart (newStart);
            clip.setLoopLength ((end - start) * lengthOfOnePad);
            clip.setPlayRange (newStart, (double) end * lengthOfOnePad);
        }

        this.loopPadPressed = -1;
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

        // halfColumns x playLines Drum Pad Grid
        final ICursorDevice primary = this.model.getInstrumentDevice ();
        final boolean hasDrumPads = primary.hasDrumPads ();
        boolean isSoloed = false;
        if (hasDrumPads)
        {
            final IDrumPadBank drumPadBank = primary.getDrumPadBank ();
            for (int i = 0; i < this.halfColumns * this.playLines; i++)
            {
                if (drumPadBank.getItem (i).isSolo ())
                {
                    isSoloed = true;
                    break;
                }
            }
        }
        final boolean isRecording = this.model.hasRecordingState ();
        for (int y = 0; y < this.playLines; y++)
        {
            for (int x = 0; x < this.halfColumns; x++)
            {
                final int index = this.halfColumns * y + x;
                padGrid.lightEx (x, this.allLines - 1 - y, this.getPadColor (index, primary, isSoloed, isRecording));
            }
        }

        this.drawSequencer ();
    }


    protected String getPadColor (final int index, final ICursorDevice primary, final boolean isSoloed, final boolean isRecording)
    {
        // Playing note?
        if (this.keyManager.isKeyPressed (this.offsetY + index))
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
        return DAWColors.getColorIndex (drumPad.getColor ());
    }


    protected String getStepColor (final int isSet, final boolean hilite)
    {
        switch (isSet)
        {
            // Note continues
            case INoteClip.NOTE_CONTINUE:
                return hilite ? AbstractSequencerView.COLOR_STEP_HILITE_CONTENT : AbstractSequencerView.COLOR_CONTENT_CONT;
            // Note starts
            case INoteClip.NOTE_START:
                return hilite ? AbstractSequencerView.COLOR_STEP_HILITE_CONTENT : AbstractSequencerView.COLOR_CONTENT;
            // Empty
            default:
                return hilite ? AbstractSequencerView.COLOR_STEP_HILITE_NO_CONTENT : AbstractSequencerView.COLOR_NO_CONTENT;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateNoteMapping ()
    {
        this.delayedUpdateNoteMapping (this.canPadsBeTurnedOn () ? this.scales.getDrumMatrix () : EMPTY_TABLE);
    }


    /** {@inheritDoc} */
    @Override
    public void onOctaveDown (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
        this.keyManager.clearPressedKeys ();
        final int oldDrumOctave = this.scales.getDrumOctave ();
        this.scales.decDrumOctave ();
        final int newDrumOctave = this.scales.getDrumOctave ();
        this.offsetY = AbstractDrumView.DRUM_START_KEY + newDrumOctave * 16;
        this.updateNoteMapping ();
        this.surface.getDisplay ().notify (this.scales.getDrumRangeText ());
        if (oldDrumOctave != newDrumOctave)
            this.model.getInstrumentDevice ().getDrumPadBank ().scrollPageBackwards ();
    }


    /** {@inheritDoc} */
    @Override
    public void onOctaveUp (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
        this.keyManager.clearPressedKeys ();
        final int oldDrumOctave = this.scales.getDrumOctave ();
        this.scales.incDrumOctave ();
        final int newDrumOctave = this.scales.getDrumOctave ();
        this.offsetY = AbstractDrumView.DRUM_START_KEY + newDrumOctave * 16;
        this.updateNoteMapping ();
        this.surface.getDisplay ().notify (this.scales.getDrumRangeText ());
        if (oldDrumOctave != newDrumOctave)
            this.model.getInstrumentDevice ().getDrumPadBank ().scrollPageForwards ();
    }


    /**
     * Hook for playing notes with grids which do not use midi notes.
     *
     * @param note The note to play
     * @param velocity The velocity of the note
     */
    protected void playNote (final int note, final int velocity)
    {
        // Intentionally empty
    }


    protected void handleButtonCombinations (final int playedPad)
    {
        if (this.surface.isDeletePressed ())
        {
            // Delete all of the notes on that 'pad'
            this.handleDeleteButton (playedPad);
            return;
        }

        if (this.surface.isMutePressed ())
        {
            // Mute that 'pad'
            this.handleMuteButton (playedPad);
            return;
        }

        if (this.surface.isSoloPressed ())
        {
            // Solo that 'pad'
            this.handleSoloButton (playedPad);
            return;
        }

        if (this.surface.isSelectPressed () || this.configuration.isAutoSelectDrum ())
        {
            // Also select the matching device layer channel of the pad
            this.handleSelectButton (playedPad);
        }
    }


    protected void handleDeleteButton (final int playedPad)
    {
        this.surface.setButtonConsumed (this.surface.getDeleteButtonId ());
        this.updateNoteMapping ();
        this.getClip ().clearRow (this.offsetY + playedPad);
    }


    protected void handleMuteButton (final int playedPad)
    {
        this.surface.setButtonConsumed (this.surface.getMuteButtonId ());
        this.updateNoteMapping ();
        this.model.getInstrumentDevice ().getDrumPadBank ().getItem (playedPad).toggleMute ();
    }


    protected void handleSoloButton (final int playedPad)
    {
        this.surface.setButtonConsumed (this.surface.getSoloButtonId ());
        this.updateNoteMapping ();
        this.model.getInstrumentDevice ().getDrumPadBank ().getItem (playedPad).toggleSolo ();
    }


    /**
     * Handle the select button.
     *
     * @param playedPad The played pad
     */
    protected void handleSelectButton (final int playedPad)
    {
        // Hook for select button combination with pads
    }


    private boolean canPadsBeTurnedOn ()
    {
        if (!this.model.canSelectedTrackHoldNotes ())
            return false;

        if (this.surface.isSelectPressed () && !this.surface.isButtonConsumed (this.surface.getSelectButtonId ()))
            return false;

        if (this.surface.isDeletePressed () && !this.surface.isButtonConsumed (this.surface.getDeleteButtonId ()))
            return false;

        if (this.surface.isMutePressed () && !this.surface.isButtonConsumed (this.surface.getMuteButtonId ()))
            return false;

        return !this.surface.isSoloPressed () || this.surface.isButtonConsumed (this.surface.getSoloButtonId ());
    }


    /**
     * Draw the clip loop and sequencer steps area.
     */
    private void drawSequencer ()
    {
        final INoteClip clip = this.getClip ();

        // Clip length/loop area
        final int step = clip.getCurrentStep ();

        final int lengthOfOnePad = this.getLengthOfOnePage (this.sequencerSteps);
        final double loopStart = clip.getLoopStart ();
        final int loopStartPad = (int) Math.ceil (loopStart / lengthOfOnePad);
        final int loopEndPad = (int) Math.ceil ((loopStart + clip.getLoopLength ()) / lengthOfOnePad);
        final int currentPage = step / this.sequencerSteps;

        final int numOfPages = this.halfColumns * this.playLines;
        final PadGrid padGrid = this.surface.getPadGrid ();
        for (int pad = 0; pad < numOfPages; pad++)
        {
            final int x = this.halfColumns + pad % this.halfColumns;
            final int y = this.sequencerLines + pad / this.halfColumns;
            padGrid.lightEx (x, y, this.getPageColor (loopStartPad, loopEndPad, currentPage, clip.getEditPage (), pad));
        }

        // Paint the sequencer steps
        final int hiStep = this.isInXRange (step) ? step % this.sequencerSteps : -1;
        for (int col = 0; col < this.sequencerSteps; col++)
        {
            final int isSet = clip.getStep (col, this.offsetY + this.selectedPad);
            final boolean hilite = col == hiStep;
            final int x = col % GRID_COLUMNS;
            final int y = col / GRID_COLUMNS;
            padGrid.lightEx (x, y, this.getStepColor (isSet, hilite));
        }
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
            this.keyManager.setKeyPressed (note, velocity);
    }
}
