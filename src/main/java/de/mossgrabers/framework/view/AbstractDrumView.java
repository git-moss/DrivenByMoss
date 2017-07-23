// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.view;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ControlSurface;
import de.mossgrabers.framework.controller.grid.PadGrid;
import de.mossgrabers.framework.daw.BitwigColors;
import de.mossgrabers.framework.daw.CursorDeviceProxy;
import de.mossgrabers.framework.daw.TrackBankProxy;
import de.mossgrabers.framework.daw.data.ChannelData;
import de.mossgrabers.framework.scale.Scales;

import java.util.Arrays;


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
public abstract class AbstractDrumView<S extends ControlSurface<C>, C extends Configuration> extends AbstractSequencerView<S, C> implements TransposeView
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
    private int []             pressedKeys;


    /**
     * Constructor.
     *
     * @param name The name of the view
     * @param surface The surface
     * @param model The model
     * @param numSequencerLines The number of rows to use for the sequencer
     * @param numPlayLines The number of rows to use for playing
     */
    public AbstractDrumView (final String name, final S surface, final Model model, final int numSequencerLines, final int numPlayLines)
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

        this.pressedKeys = new int [128];
        Arrays.fill (this.pressedKeys, 0);
        this.noteMap = Scales.getEmptyMatrix ();

        final TrackBankProxy tb = model.getTrackBank ();
        // Light notes send from the sequencer
        tb.addNoteObserver ( (note, velocity) -> this.pressedKeys[note] = velocity);
        tb.addTrackSelectionObserver ( (index, isSelected) -> this.clearPressedKeys ());
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        super.onActivate ();

        final CursorDeviceProxy primary = this.model.getPrimaryDevice ();
        primary.enableObservers (true);
        primary.setDrumPadIndication (true);
    }


    /** {@inheritDoc} */
    @Override
    public void onDeactivate ()
    {
        super.onDeactivate ();

        final CursorDeviceProxy primary = this.model.getPrimaryDevice ();
        primary.enableObservers (false);
        primary.setDrumPadIndication (false);
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
        if (y >= this.playLines)
        {
            if (velocity != 0)
            {
                final int col = GRID_COLUMNS * (this.allLines - 1 - y) + x;
                this.clip.toggleStep (col, this.offsetY + this.selectedPad, this.configuration.isAccentActive () ? this.configuration.getFixedAccentValue () : velocity);
            }
            return;
        }

        // halfColumns x playLines Drum Pad Grid
        if (x < this.halfColumns)
        {
            this.selectedPad = this.halfColumns * y + x;
            final int playedPad = velocity == 0 ? -1 : this.selectedPad;

            // Mark selected note
            this.pressedKeys[this.offsetY + this.selectedPad] = velocity;
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

        if (pad == this.loopPadPressed && pad != this.clip.getEditPage ())
        {
            // Only single pad pressed -> page selection
            this.clip.scrollToPage (pad);
        }
        else
        {
            final int start = this.loopPadPressed < pad ? this.loopPadPressed : pad;
            final int end = (this.loopPadPressed < pad ? pad : this.loopPadPressed) + 1;
            final int lengthOfOnePad = this.getLengthOfOnePage (this.sequencerSteps);

            // Set a new loop between the 2 selected pads
            final int newStart = start * lengthOfOnePad;
            this.clip.setLoopStart (newStart);
            this.clip.setLoopLength ((end - start) * lengthOfOnePad);
            this.clip.setPlayRange (newStart, (double) end * lengthOfOnePad);
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
        final CursorDeviceProxy primary = this.model.getPrimaryDevice ();
        final boolean hasDrumPads = primary.hasDrumPads ();
        boolean isSoloed = false;
        if (hasDrumPads)
        {
            for (int i = 0; i < this.halfColumns * this.playLines; i++)
            {
                if (primary.getDrumPad (i).isSolo ())
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


    protected String getPadColor (final int index, final CursorDeviceProxy primary, final boolean isSoloed, final boolean isRecording)
    {
        // Playing note?
        if (this.pressedKeys[this.offsetY + index] > 0)
            return isRecording ? AbstractDrumView.COLOR_PAD_RECORD : AbstractDrumView.COLOR_PAD_PLAY;
        // Selected?
        if (this.selectedPad == index)
            return AbstractDrumView.COLOR_PAD_SELECTED;
        // Exists and active?
        final ChannelData drumPad = primary.getDrumPad (index);
        if (!drumPad.doesExist () || !drumPad.isActivated ())
            return this.surface.getConfiguration ().isTurnOffEmptyDrumPads () ? AbstractDrumView.COLOR_PAD_OFF : AbstractDrumView.COLOR_PAD_NO_CONTENT;
        // Muted or soloed?
        if (drumPad.isMute () || isSoloed && !drumPad.isSolo ())
            return AbstractDrumView.COLOR_PAD_MUTED;
        return this.getPadContentColor (drumPad);
    }


    protected String getPadContentColor (final ChannelData drumPad)
    {
        return BitwigColors.getColorIndex (drumPad.getColor ());
    }


    protected String getStepColor (final int isSet, final boolean hilite)
    {
        switch (isSet)
        {
            // Note continues
            case 1:
                return hilite ? AbstractSequencerView.COLOR_STEP_HILITE_CONTENT : AbstractSequencerView.COLOR_CONTENT_CONT;
            // Note starts
            case 2:
                return hilite ? AbstractSequencerView.COLOR_STEP_HILITE_CONTENT : AbstractSequencerView.COLOR_CONTENT;
            // Empty
            default:
                return hilite ? AbstractSequencerView.COLOR_STEP_HILITE_NO_CONTENT : AbstractSequencerView.COLOR_NO_CONTENT;
        }
    }


    protected void clearPressedKeys ()
    {
        for (int i = 0; i < 128; i++)
            this.pressedKeys[i] = 0;
    }


    /** {@inheritDoc} */
    @Override
    public void updateNoteMapping ()
    {
        this.noteMap = this.canPadsBeTurnedOn () ? this.scales.getDrumMatrix () : Scales.getEmptyMatrix ();
        this.surface.setKeyTranslationTable (this.scales.translateMatrixToGrid (this.noteMap));
    }


    /** {@inheritDoc} */
    @Override
    public void onOctaveDown (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
        this.clearPressedKeys ();
        final int oldDrumOctave = this.scales.getDrumOctave ();
        this.scales.decDrumOctave ();
        final int newDrumOctave = this.scales.getDrumOctave ();
        this.offsetY = AbstractDrumView.DRUM_START_KEY + newDrumOctave * 16;
        this.updateNoteMapping ();
        this.surface.getDisplay ().notify (this.scales.getDrumRangeText (), true, true);
        if (oldDrumOctave != newDrumOctave)
            this.model.getPrimaryDevice ().scrollDrumPadsPageUp ();
    }


    /** {@inheritDoc} */
    @Override
    public void onOctaveUp (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
        this.clearPressedKeys ();
        final int oldDrumOctave = this.scales.getDrumOctave ();
        this.scales.incDrumOctave ();
        final int newDrumOctave = this.scales.getDrumOctave ();
        this.offsetY = AbstractDrumView.DRUM_START_KEY + newDrumOctave * 16;
        this.updateNoteMapping ();
        this.surface.getDisplay ().notify (this.scales.getDrumRangeText (), true, true);
        if (oldDrumOctave != newDrumOctave)
            this.model.getPrimaryDevice ().scrollDrumPadsPageDown ();
    }


    /**
     * Hook for playing notes with grids who do not use midi notes.
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
        this.clip.clearRow (this.offsetY + playedPad);
    }


    protected void handleMuteButton (final int playedPad)
    {
        this.surface.setButtonConsumed (this.surface.getMuteButtonId ());
        this.updateNoteMapping ();
        this.model.getPrimaryDevice ().toggleLayerOrDrumPadMute (playedPad);
    }


    protected void handleSoloButton (final int playedPad)
    {
        this.surface.setButtonConsumed (this.surface.getSoloButtonId ());
        this.updateNoteMapping ();
        this.model.getPrimaryDevice ().toggleLayerOrDrumPadSolo (playedPad);
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


    protected void onSelect (final ButtonEvent event)
    {
        if (event != ButtonEvent.LONG)
            this.updateNoteMapping ();
    }


    protected void onDelete (final ButtonEvent event)
    {
        if (event != ButtonEvent.LONG)
            this.updateNoteMapping ();
    }


    private boolean canPadsBeTurnedOn ()
    {
        if (!this.model.canSelectedTrackHoldNotes ())
            return false;
        final boolean isMute = !this.surface.isMutePressed ();
        return !this.surface.isSelectPressed () && !this.surface.isDeletePressed () && isMute && !this.surface.isSoloPressed ();
    }


    /**
     * Draw the clip loop and sequencer steps area.
     */
    private void drawSequencer ()
    {
        // Clip length/loop area
        final int step = this.clip.getCurrentStep ();

        final int lengthOfOnePad = this.getLengthOfOnePage (this.sequencerSteps);
        final double loopStart = this.clip.getLoopStart ();
        final int loopStartPad = (int) Math.ceil (loopStart / lengthOfOnePad);
        final int loopEndPad = (int) Math.ceil ((loopStart + this.clip.getLoopLength ()) / lengthOfOnePad);
        int currentPage = step / this.sequencerSteps;

        final int numOfPages = this.halfColumns * this.playLines;
        final PadGrid padGrid = this.surface.getPadGrid ();
        for (int pad = 0; pad < numOfPages; pad++)
        {
            final int x = this.halfColumns + pad % this.halfColumns;
            final int y = this.sequencerLines + pad / this.halfColumns;
            padGrid.lightEx (x, y, this.getPageColor (loopStartPad, loopEndPad, currentPage, this.clip.getEditPage (), pad));
        }

        // Paint the sequencer steps
        final int hiStep = this.isInXRange (step) ? step % this.sequencerSteps : -1;
        for (int col = 0; col < this.sequencerSteps; col++)
        {
            final int isSet = this.clip.getStep (col, this.offsetY + this.selectedPad);
            final boolean hilite = col == hiStep;
            final int x = col % GRID_COLUMNS;
            final int y = col / GRID_COLUMNS;
            padGrid.lightEx (x, y, this.getStepColor (isSet, hilite));
        }
    }
}
