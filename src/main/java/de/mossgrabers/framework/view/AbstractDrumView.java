// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.view;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.controller.hardware.IHwButton;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IDrumPadBank;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.daw.IStepInfo;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.constants.Resolution;
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

    protected static final int DRUM_START_KEY        = 36;
    protected static final int GRID_COLUMNS          = 8;

    protected int              selectedPad;
    protected int              loopPadPressed        = -1;
    protected int              sequencerLines;
    protected int              playLines;
    protected int              allLines;
    protected int              sequencerSteps;
    protected int              halfColumns;
    protected IStepInfo        copyNote;


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

        this.canScrollUp = false;
        this.canScrollDown = false;

        final ITrackBank tb = model.getTrackBank ();
        tb.addSelectionObserver ( (index, isSelected) -> this.keyManager.clearPressedKeys ());
        tb.addNoteObserver (this::updateNote);
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        super.onActivate ();
        this.model.getInstrumentDevice ().getDrumPadBank ().setIndication (true);
    }


    /** {@inheritDoc} */
    @Override
    public void onDeactivate ()
    {
        super.onDeactivate ();
        this.model.getInstrumentDevice ().getDrumPadBank ().setIndication (false);
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
        final int offsetY = this.scales.getDrumOffset ();

        // Sequencer steps
        if (y >= this.playLines)
        {
            if (this.isActive ())
                this.handleNoteArea (index, x, y, velocity, offsetY);
            return;
        }

        // halfColumns x playLines Drum Pad Grid
        if (x < this.halfColumns)
        {
            this.selectedPad = this.halfColumns * y + x;
            final int playedPad = velocity == 0 ? -1 : this.selectedPad;

            // Mark selected note
            this.keyManager.setKeyPressed (offsetY + this.selectedPad, velocity);
            this.playNote (offsetY + this.selectedPad, velocity);

            if (playedPad < 0)
                return;

            this.handleDrumGridButtonCombinations (playedPad);
            return;
        }

        if (!this.isActive ())
            return;

        // Clip length/loop area
        final int pad = (this.playLines - 1 - y) * this.halfColumns + x - this.halfColumns;
        this.handleLoopArea (pad, velocity);
    }


    /**
     * Handle button presses in the note area of the note sequencer.
     *
     * @param index The index of the pad
     * @param x The x position of the pad in the sequencer grid
     * @param y The y position of the pad in the sequencer grid
     * @param velocity The velocity
     * @param offsetY
     */
    private void handleNoteArea (final int index, final int x, final int y, final int velocity, final int offsetY)
    {
        // Toggle the note on up, so we can intercept the long presses
        if (velocity != 0)
            return;

        final INoteClip clip = this.getClip ();
        final int channel = this.configuration.getMidiEditChannel ();
        final int step = GRID_COLUMNS * (this.allLines - 1 - y) + x;
        final int note = offsetY + this.selectedPad;
        final int vel = this.configuration.isAccentActive () ? this.configuration.getFixedAccentValue () : this.surface.getButton (ButtonID.get (ButtonID.PAD1, index)).getPressedVelocity ();

        if (this.handleNoteAreaButtonCombinations (clip, channel, step, note, vel))
            return;

        clip.toggleStep (channel, step, note, vel);
    }


    /**
     * Handle button combinations on the note area of the sequencer.
     *
     * @param clip The sequenced midi clip
     * @param channel The MIDI channel of the note
     * @param step The step in the current page in the clip
     * @param note The note in the current page of the pad in the clip
     * @param velocity The velocity
     * @return True if handled
     */
    private boolean handleNoteAreaButtonCombinations (final INoteClip clip, final int channel, final int step, final int note, int velocity)
    {
        // Handle note duplicate function
        final IHwButton duplicateButton = this.surface.getButton (ButtonID.DUPLICATE);
        if (duplicateButton != null && duplicateButton.isPressed ())
        {
            duplicateButton.setConsumed ();
            final IStepInfo noteStep = clip.getStep (channel, step, note);
            if (noteStep.getState () == IStepInfo.NOTE_START)
                this.copyNote = noteStep;
            else if (this.copyNote != null)
                clip.setStep (channel, step, note, this.copyNote);
            return true;
        }

        // Change length of a note or create a new one with a length
        for (int s = step - 1; s >= 0; s--)
        {
            final int x = s % GRID_COLUMNS;
            final int y = this.allLines - 1 - s / GRID_COLUMNS;
            final int pad = y * GRID_COLUMNS + x;
            final IHwButton button = this.surface.getButton (ButtonID.get (ButtonID.PAD1, pad));
            if (button.isLongPressed ())
            {
                button.setConsumed ();
                final int length = step - s + 1;
                final double duration = length * Resolution.getValueAt (this.getResolutionIndex ());
                final int state = note < 0 ? 0 : clip.getStep (channel, s, note).getState ();
                if (state == IStepInfo.NOTE_START)
                    clip.updateStepDuration (channel, s, note, duration);
                else
                    clip.setStep (channel, s, note, velocity, duration);
                return true;
            }
        }

        return false;
    }


    /**
     * Handle button presses in the loop area of the drum sequencer.
     *
     * @param pad The pressed pad
     * @param velocity The velocity
     */
    private void handleLoopArea (final int pad, final int velocity)
    {
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

        final INoteClip clip = this.getClip ();
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
        final IPadGrid padGrid = this.surface.getPadGrid ();
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

        if (this.sequencerLines > 0)
            this.drawSequencer ();
    }


    protected String getPadColor (final int index, final ICursorDevice primary, final boolean isSoloed, final boolean isRecording)
    {
        final int offsetY = this.scales.getDrumOffset ();

        // Playing note?
        if (this.keyManager.isKeyPressed (offsetY + index))
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


    protected String getStepColor (final int isSet, final boolean hilite)
    {
        switch (isSet)
        {
            // Note continues
            case IStepInfo.NOTE_CONTINUE:
                return hilite ? AbstractSequencerView.COLOR_STEP_HILITE_CONTENT : AbstractSequencerView.COLOR_CONTENT_CONT;
            // Note starts
            case IStepInfo.NOTE_START:
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
        this.changeOctave (event, false, this.surface.isShiftPressed () ? 4 : this.scales.getDrumDefaultOffset ());
    }


    /** {@inheritDoc} */
    @Override
    public void onOctaveUp (final ButtonEvent event)
    {
        this.changeOctave (event, true, this.surface.isShiftPressed () ? 4 : this.scales.getDrumDefaultOffset ());
    }


    /** {@inheritDoc} */
    @Override
    public boolean isOctaveUpButtonOn ()
    {
        return this.scales.canScrollDrumOctaveUp ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isOctaveDownButtonOn ()
    {
        return this.scales.canScrollDrumOctaveDown ();
    }


    /**
     * Switch the drum octave up or down.
     *
     * @param event The button press event
     * @param isUp Move up if true otherwise down
     * @param offset The offset to move up or down
     */
    protected void changeOctave (final ButtonEvent event, final boolean isUp, final int offset)
    {
        if (event != ButtonEvent.DOWN)
            return;
        this.keyManager.clearPressedKeys ();
        if (isUp)
            this.scales.incDrumOffset (offset);
        else
            this.scales.decDrumOffset (offset);
        this.updateNoteMapping ();
        this.surface.getDisplay ().notify (this.scales.getDrumRangeText ());
        this.model.getInstrumentDevice ().getDrumPadBank ().scrollTo (this.scales.getDrumOffset (), false);
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


    protected void handleDrumGridButtonCombinations (final int playedPad)
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
        this.surface.setTriggerConsumed (ButtonID.DELETE);
        this.updateNoteMapping ();
        final int editMidiChannel = this.configuration.getMidiEditChannel ();
        this.getClip ().clearRow (editMidiChannel, this.scales.getDrumOffset () + playedPad);
    }


    protected void handleMuteButton (final int playedPad)
    {
        this.surface.setTriggerConsumed (ButtonID.MUTE);
        this.updateNoteMapping ();
        this.model.getInstrumentDevice ().getDrumPadBank ().getItem (playedPad).toggleMute ();
    }


    protected void handleSoloButton (final int playedPad)
    {
        this.surface.setTriggerConsumed (ButtonID.SOLO);
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

        if (this.surface.isSelectPressed () && !this.surface.isTriggerConsumed (ButtonID.SELECT))
            return false;

        if (this.surface.isDeletePressed () && !this.surface.isTriggerConsumed (ButtonID.DELETE))
            return false;

        if (this.surface.isMutePressed () && !this.surface.isTriggerConsumed (ButtonID.MUTE))
            return false;

        return !this.surface.isSoloPressed () || this.surface.isTriggerConsumed (ButtonID.SOLO);
    }


    /**
     * Draw the clip loop and sequencer steps area.
     */
    private void drawSequencer ()
    {
        final boolean isActive = this.isActive ();

        final INoteClip clip = this.getClip ();

        // Clip length/loop area
        final int step = clip.getCurrentStep ();

        final int lengthOfOnePad = this.getLengthOfOnePage (this.sequencerSteps);
        final double loopStart = clip.getLoopStart ();
        final int loopStartPad = (int) Math.ceil (loopStart / lengthOfOnePad);
        final int loopEndPad = (int) Math.ceil ((loopStart + clip.getLoopLength ()) / lengthOfOnePad);
        final int currentPage = step / this.sequencerSteps;

        final int numOfPages = this.halfColumns * this.playLines;
        final IPadGrid padGrid = this.surface.getPadGrid ();
        for (int pad = 0; pad < numOfPages; pad++)
        {
            final int x = this.halfColumns + pad % this.halfColumns;
            final int y = this.sequencerLines + pad / this.halfColumns;
            padGrid.lightEx (x, y, isActive ? this.getPageColor (loopStartPad, loopEndPad, currentPage, clip.getEditPage (), pad) : AbstractSequencerView.COLOR_NO_CONTENT);
        }

        // Paint the sequencer steps
        final int hiStep = this.isInXRange (step) ? step % this.sequencerSteps : -1;
        final int editMidiChannel = this.configuration.getMidiEditChannel ();
        for (int col = 0; col < this.sequencerSteps; col++)
        {
            final int isSet = clip.getStep (editMidiChannel, col, this.scales.getDrumOffset () + this.selectedPad).getState ();
            final boolean hilite = col == hiStep;
            final int x = col % GRID_COLUMNS;
            final int y = col / GRID_COLUMNS;
            padGrid.lightEx (x, y, isActive ? this.getStepColor (isSet, hilite) : AbstractSequencerView.COLOR_NO_CONTENT);
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
