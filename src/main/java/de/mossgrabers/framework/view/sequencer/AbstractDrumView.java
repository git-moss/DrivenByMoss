// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.view.sequencer;

import de.mossgrabers.controller.ni.maschine.core.MaschineColorManager;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.controller.hardware.ButtonEventHandler;
import de.mossgrabers.framework.controller.hardware.IHwButton;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.daw.clip.IStepInfo;
import de.mossgrabers.framework.daw.clip.NotePosition;
import de.mossgrabers.framework.daw.clip.StepState;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.IDrumDevice;
import de.mossgrabers.framework.daw.data.IDrumPad;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.IDrumPadBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.TransposeView;

import java.util.List;
import java.util.Optional;
import java.util.function.IntUnaryOperator;


/**
 * Abstract implementation for a drum sequencer. The grid is split into 3 areas: The sequencer area
 * where steps are displayed, the play area where you can play sounds and the measure are which
 * displays the length of the clip.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author Jürgen Moßgraber
 */
public abstract class AbstractDrumView<S extends IControlSurface<C>, C extends Configuration> extends AbstractSequencerView<S, C> implements TransposeView, ButtonEventHandler
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

    protected int              loopPadPressed        = -1;
    protected int              sequencerLines;
    protected int              playRows;
    protected int              numColumns;
    protected int              allRows;
    protected int              sequencerSteps;
    protected int              playColumns;
    protected IStepInfo        copyNote;

    protected int              selectedPad;
    protected int              scrollPosition        = -1;

    protected ButtonID         firstPad              = ButtonID.PAD1;
    protected ButtonID         buttonSelect          = ButtonID.SELECT;
    protected ButtonID         buttonBrowse          = ButtonID.BROWSE;
    protected ButtonID         buttonSolo            = ButtonID.SOLO;
    protected ButtonID         buttonMute            = ButtonID.MUTE;
    protected ButtonID         buttonDelete          = ButtonID.DELETE;


    /**
     * Constructor.
     *
     * @param name The name of the view
     * @param surface The surface
     * @param model The model
     * @param numSequencerLines The number of rows to use for the sequencer
     * @param numPlayLines The number of rows to use for playing
     * @param useDawColors True to use the drum machine pad colors for coloring the octaves
     */
    protected AbstractDrumView (final String name, final S surface, final IModel model, final int numSequencerLines, final int numPlayLines, final boolean useDawColors)
    {
        this (name, surface, model, numSequencerLines, numPlayLines, GRID_COLUMNS, 128, numSequencerLines * GRID_COLUMNS, true, useDawColors);
    }


    /**
     * Constructor.
     *
     * @param name The name of the view
     * @param surface The surface
     * @param model The model
     * @param numSequencerLines The number of rows to use for the sequencer
     * @param numPlayRows The number of rows to use for playing
     * @param numColumns The number of available columns
     * @param clipRows The rows of the clip
     * @param clipCols The columns of the clip
     * @param followSelection Follow the drum pad selection if true
     * @param useDawColors True to use the drum machine pad colors for coloring the octaves
     */
    protected AbstractDrumView (final String name, final S surface, final IModel model, final int numSequencerLines, final int numPlayRows, final int numColumns, final int clipRows, final int clipCols, final boolean followSelection, final boolean useDawColors)
    {
        super (name, surface, model, clipRows, clipCols, useDawColors);

        this.sequencerLines = numSequencerLines;
        this.playRows = numPlayRows;
        this.allRows = this.sequencerLines + this.playRows;
        this.numColumns = numColumns;
        this.sequencerSteps = numSequencerLines * this.numColumns;
        // This layout is currently fixed to a 4 width
        this.playColumns = 4;

        final ITrackBank tb = model.getTrackBank ();
        tb.addSelectionObserver ( (index, isSelected) -> this.keyManager.clearPressedKeys ());
        tb.addNoteObserver (this::updateNote);

        if (followSelection)
        {
            model.getDrumDevice ().getDrumPadBank ().addSelectionObserver ( (index, isSelected) -> {
                if (isSelected)
                    this.selectedPad = index;
            });
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        super.onActivate ();

        this.getDrumDevice ().getDrumPadBank ().setIndication (true);

        this.registerButtonMonitors (this.buttonSelect);
        this.registerButtonMonitors (this.buttonSolo);
        this.registerButtonMonitors (this.buttonMute);
        this.registerButtonMonitors (this.buttonDelete);
        this.registerButtonMonitors (this.buttonBrowse);
    }


    /** {@inheritDoc} */
    @Override
    public void onDeactivate ()
    {
        super.onDeactivate ();

        this.getDrumDevice ().getDrumPadBank ().setIndication (false);

        this.unregisterButtonMonitors (this.buttonSelect);
        this.unregisterButtonMonitors (this.buttonSolo);
        this.unregisterButtonMonitors (this.buttonMute);
        this.unregisterButtonMonitors (this.buttonDelete);
        this.unregisterButtonMonitors (this.buttonBrowse);
    }


    private void registerButtonMonitors (final ButtonID buttonID)
    {
        final IHwButton button = this.surface.getButton (buttonID);
        if (button == null)
            return;
        button.addEventHandler (ButtonEvent.DOWN, this);
        button.addEventHandler (ButtonEvent.UP, this);
    }


    private void unregisterButtonMonitors (final ButtonID buttonID)
    {
        final IHwButton button = this.surface.getButton (buttonID);
        if (button == null)
            return;
        button.removeEventHandler (ButtonEvent.DOWN, this);
        button.removeEventHandler (ButtonEvent.UP, this);
    }


    /**
     * Callback for pressing / releasing buttons to update the note mapping for button combinations.
     */
    @Override
    public void handle (final ButtonEvent event)
    {
        this.updateNoteMapping ();
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (!this.model.canSelectedTrackHoldNotes ())
            return;

        final int index = note - DRUM_START_KEY;
        final int x = index % this.numColumns;
        final int y = index / this.numColumns;
        final int offsetY = this.scales.getDrumOffset ();

        // Sequencer steps
        if (y >= this.playRows)
        {
            if (this.isActive ())
                this.handleSequencerArea (index, x, y, offsetY, velocity);
            return;
        }

        // halfColumns x playLines Drum Pad Grid
        if (x < this.playColumns)
        {
            this.handleNoteArea (x, y, offsetY, velocity);
            return;
        }

        if (!this.isActive ())
            return;

        // Clip length/loop area
        final int pad = (this.playRows - 1 - y) * this.playColumns + x - this.playColumns;
        this.handleLoopArea (pad, velocity);
    }


    /**
     * Handle button presses in the note area of the note sequencer.
     *
     * @param x The x position of the pad in the sequencer grid
     * @param y The y position of the pad in the sequencer grid
     * @param offsetY The drum offset
     * @param velocity The velocity
     */
    protected void handleNoteArea (final int x, final int y, final int offsetY, final int velocity)
    {
        this.setSelectedPad (this.playColumns * y + x, velocity);

        // Mark selected note
        this.keyManager.setKeyPressed (offsetY + this.selectedPad, velocity);
        this.playNote (offsetY + this.selectedPad, velocity);

        final int playedPad = velocity == 0 ? -1 : this.selectedPad;
        if (playedPad < 0)
            return;

        this.handleNoteAreaButtonCombinations (playedPad);
    }


    /**
     * Handle button presses in the sequencer area of the note sequencer.
     *
     * @param index The index of the pad
     * @param x The x position of the pad in the sequencer grid
     * @param y The y position of the pad in the sequencer grid
     * @param offsetY The drum offset
     * @param velocity The velocity
     */
    protected void handleSequencerArea (final int index, final int x, final int y, final int offsetY, final int velocity)
    {
        // Toggle the note on up, so we can intercept the long presses
        if (velocity != 0)
            return;

        final INoteClip clip = this.getClip ();
        final NotePosition notePosition = new NotePosition (this.configuration.getMidiEditChannel (), this.numColumns * (this.allRows - 1 - y) + x, offsetY + this.selectedPad);
        final int vel = this.getVelocity (index);

        if (this.handleSequencerAreaButtonCombinations (clip, notePosition, vel))
            return;

        clip.toggleStep (notePosition, vel);
    }


    /**
     * Get the velocity of the played pad. Either it is fixed in the settings or the stored value
     * from the down event.
     *
     * @param index The index of the pad
     * @return The velocity
     */
    protected int getVelocity (final int index)
    {
        if (this.configuration.isAccentActive ())
            return this.configuration.getFixedAccentValue ();
        final IHwButton button = this.surface.getButton (ButtonID.get (this.firstPad, index));
        return button.getPressedVelocity ();
    }


    /**
     * Handle button combinations in the sequencer area.
     *
     * @param clip The sequenced MIDI clip
     * @param notePosition The note position
     * @param velocity The velocity
     * @return True if handled
     */
    protected boolean handleSequencerAreaButtonCombinations (final INoteClip clip, final NotePosition notePosition, final int velocity)
    {
        // Handle note duplicate function
        if (this.isButtonCombination (ButtonID.DUPLICATE))
        {
            final IStepInfo noteStep = clip.getStep (notePosition);
            if (noteStep.getState () == StepState.START)
                this.copyNote = noteStep;
            else if (this.copyNote != null)
                clip.setStep (notePosition, this.copyNote);
            return true;
        }

        if (this.isButtonCombination (ButtonID.MUTE))
        {
            final IStepInfo stepInfo = clip.getStep (notePosition);
            final StepState isSet = stepInfo.getState ();
            if (isSet == StepState.START)
                this.getClip ().updateStepMuteState (notePosition, !stepInfo.isMuted ());
            return true;
        }

        // Change length of a note or create a new one with a length
        final NotePosition np = new NotePosition (notePosition);
        final int step = np.getStep ();
        final int note = np.getNote ();
        for (int s = 0; s < step; s++)
        {
            final int pad = this.getPadIndex (s);
            final IHwButton button = this.surface.getButton (ButtonID.get (this.firstPad, pad));
            if (button.isLongPressed ())
            {
                button.setConsumed ();
                final int length = step - s + 1;
                final double duration = length * Resolution.getValueAt (this.getResolutionIndex ());
                np.setStep (s);
                final StepState state = note < 0 ? StepState.OFF : clip.getStep (np).getState ();
                if (state == StepState.START)
                    clip.updateStepDuration (np, duration);
                else
                    clip.setStep (np, velocity, duration);
                return true;
            }
        }

        return false;
    }


    /**
     * Calculate the index of the pad from the given sequencer step.
     *
     * @param step The step
     * @return The pad index
     */
    protected int getPadIndex (final int step)
    {
        final int x = step % this.numColumns;
        final int y = this.allRows - 1 - step / this.numColumns;
        return y * this.numColumns + x;
    }


    /**
     * Handle button presses in the loop area of the drum sequencer.
     *
     * @param pad The pressed pad
     * @param velocity The velocity
     */
    protected void handleLoopArea (final int pad, final int velocity)
    {
        final boolean isAccentActive = this.configuration.isAccentActive ();
        if (isAccentActive)
        {
            if (velocity == 0)
                return;
            final int selPad = (3 - pad / 4) * 4 + pad % 4;
            this.configuration.setFixedAccentValue ((selPad + 1) * 8 - 1);
            return;
        }

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

        final IDrumDevice primary = this.getDrumDevice ();

        // halfColumns x playLines Drum Pad Grid
        this.drawDrumPads (padGrid, primary.getDrumPadBank ());

        if (this.sequencerLines > 0)
        {
            final INoteClip clip = this.getClip ();
            final boolean isActive = this.isActive ();
            this.drawPages (clip, isActive);
            this.drawSequencerSteps (clip, isActive, this.scales.getDrumOffset () + this.selectedPad, this.getPadColor (primary, this.selectedPad));
        }
    }


    /**
     * Draw the drum pad block.
     *
     * @param padGrid The pad grid
     * @param drumPadBank The bank with drum pads to draw
     */
    protected void drawDrumPads (final IPadGrid padGrid, final IDrumPadBank drumPadBank)
    {
        final boolean isRecording = this.model.hasRecordingState ();
        for (int y = 0; y < this.playRows; y++)
        {
            for (int x = 0; x < this.playColumns; x++)
            {
                final int index = this.playColumns * y + x;
                padGrid.lightEx (x, this.allRows - 1 - y, this.getDrumPadColor (index, drumPadBank, isRecording));
            }
        }
    }


    /**
     * Get the color of the drum pad (the drum device layer).
     *
     * @param primary The drum instrument (first instrument on the channel)
     * @param drumPadIndex The index of the drum pad in the current drum pad page
     * @return The color or null if not a drum device, drum layer is empty, ...
     */
    protected Optional<ColorEx> getPadColor (final IDrumDevice primary, final int drumPadIndex)
    {
        if (!primary.hasDrumPads ())
            return Optional.empty ();

        final IDrumPad item = primary.getDrumPadBank ().getItem (drumPadIndex);
        return item.doesExist () ? Optional.of (item.getColor ()) : Optional.empty ();
    }


    protected String getDrumPadColor (final int index, final IDrumPadBank drumPadBank, final boolean isRecording)
    {
        final int offsetY = this.scales.getDrumOffset ();

        // Playing note?
        if (this.keyManager.isKeyPressed (offsetY + index))
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
        return this.useDawColors ? DAWColor.getColorID (drumPad.getColor ()) : AbstractDrumView.COLOR_PAD_HAS_CONTENT;
    }


    /** {@inheritDoc} */
    @Override
    public void updateNoteMapping ()
    {
        this.delayedUpdateNoteMapping (this.canPadsBeTurnedOn () ? this.getDrumMatrix () : EMPTY_TABLE);
    }


    /**
     * Get the drum matrix to use.
     *
     * @return The drum matrix
     */
    protected int [] getDrumMatrix ()
    {
        return this.scales.getDrumMatrix ();
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
        this.changeOctave (event, isUp, offset, false, true);
    }


    /**
     * Switch the drum octave up or down.
     *
     * @param event The button press event
     * @param isUp Move up if true otherwise down
     * @param offset The offset to move up or down
     * @param adjustPage True to adjust the drum machine page to display the pads of the octave
     * @param notify True to display an on-screen notification
     */
    public void changeOctave (final ButtonEvent event, final boolean isUp, final int offset, final boolean adjustPage, final boolean notify)
    {
        if (event != ButtonEvent.DOWN)
            return;
        this.keyManager.clearPressedKeys ();
        if (isUp)
            this.scales.incDrumOffset (offset);
        else
            this.scales.decDrumOffset (offset);
        this.updateNoteMapping ();
        if (notify)
            this.surface.getDisplay ().notify (this.scales.getDrumRangeText ());
        this.getDrumDevice ().getDrumPadBank ().scrollTo (this.scales.getDrumOffset (), adjustPage);
        this.clearEditNotes ();
    }


    /**
     * Reset the drum octave to 0.
     */
    public void resetOctave ()
    {
        this.keyManager.clearPressedKeys ();
        this.scales.resetDrumOctave ();
        this.updateNoteMapping ();
        this.getDrumDevice ().getDrumPadBank ().scrollTo (this.scales.getDrumOffset (), true);
    }


    /**
     * Hook for playing notes with grids which do not use MIDI notes.
     *
     * @param drumPad The drum pad to play
     * @param velocity The velocity of the note
     */
    protected void playNote (final int drumPad, final int velocity)
    {
        // Intentionally empty
    }


    protected void handleNoteAreaButtonCombinations (final int playedPad)
    {
        if (this.isDeleteTrigger ())
        {
            // Delete all of the notes on that 'pad'
            this.handleDeleteButton (playedPad);
            return;
        }

        if (this.isMuteTrigger ())
        {
            // Mute that 'pad'
            this.handleMuteButton (playedPad);
            return;
        }

        if (this.isSoloTrigger ())
        {
            // Solo that 'pad'
            this.handleSoloButton (playedPad);
            return;
        }

        if (this.isBrowseTrigger ())
        {
            this.surface.setTriggerConsumed (this.buttonBrowse);

            final IDrumDevice primary = this.getDrumDevice ();
            if (!primary.hasDrumPads ())
                return;

            final IDrumPadBank drumPadBank = primary.getDrumPadBank ();
            this.scrollPosition = drumPadBank.getScrollPosition ();
            this.model.getBrowser ().replace (drumPadBank.getItem (playedPad));
            return;
        }

        if (this.isSelectTrigger () || this.configuration.isAutoSelectDrum ())
        {
            // Also select the matching device layer channel of the pad
            this.handleSelectButton (playedPad);
        }
    }


    /**
     * Test for the selection trigger.
     *
     * @return True if selection trigger is active
     */
    protected boolean isSelectTrigger ()
    {
        final boolean pressed = this.surface.isPressed (this.buttonSelect);
        if (this.configuration.isCombinationButtonToSoundDrumPads ())
            return !pressed;
        return pressed;
    }


    /**
     * Test for the browse trigger.
     *
     * @return True if browse trigger is active
     */
    protected boolean isBrowseTrigger ()
    {
        return this.surface.isPressed (this.buttonBrowse);
    }


    /**
     * Test for the solo trigger.
     *
     * @return True if solo trigger is active
     */
    protected boolean isSoloTrigger ()
    {
        return this.surface.isPressed (this.buttonSolo);
    }


    /**
     * Test for the mute trigger.
     *
     * @return True if mute trigger is active
     */
    protected boolean isMuteTrigger ()
    {
        return this.surface.isPressed (this.buttonMute);
    }


    /**
     * Test for the delete trigger.
     *
     * @return True if delete trigger is active
     */
    protected boolean isDeleteTrigger ()
    {
        return this.surface.isPressed (this.buttonDelete);
    }


    /**
     * Handle a delete combination.
     *
     * @param playedPad The pad which notes to delete
     */
    protected void handleDeleteButton (final int playedPad)
    {
        this.surface.setTriggerConsumed (this.buttonDelete);
        final int editMidiChannel = this.configuration.getMidiEditChannel ();
        this.getClip ().clearRow (editMidiChannel, this.scales.getDrumOffset () + playedPad);
    }


    /**
     * Handle a mute combination.
     *
     * @param playedPad The pad which channel to mute
     */
    protected void handleMuteButton (final int playedPad)
    {
        this.surface.setTriggerConsumed (this.buttonMute);
        this.getDrumDevice ().getDrumPadBank ().getItem (playedPad).toggleMute ();
    }


    /**
     * Handle a solo combination.
     *
     * @param playedPad The pad which channel to solo
     */
    protected void handleSoloButton (final int playedPad)
    {
        this.surface.setTriggerConsumed (this.buttonSolo);
        this.getDrumDevice ().getDrumPadBank ().getItem (playedPad).toggleSolo ();
    }


    /**
     * Handle the select button.
     *
     * @param playedPad The pad which to select
     */
    protected void handleSelectButton (final int playedPad)
    {
        this.surface.setTriggerConsumed (this.buttonSelect);
        this.getDrumDevice ().getDrumPadBank ().getItem (playedPad).select ();
    }


    private boolean canPadsBeTurnedOn ()
    {
        if (!this.model.canSelectedTrackHoldNotes ())
            return false;

        return !(this.isSelectTrigger () || this.isDeleteTrigger () || this.isMuteTrigger () || this.isSoloTrigger () || this.isBrowseTrigger ());
    }


    /**
     * Draw the sequencer steps.
     *
     * @param clip The clip
     * @param isActive Is there an active clip?
     * @param noteRow The note for which to draw the row
     * @param rowColor The color to use the notes of the row
     */
    protected void drawSequencerSteps (final INoteClip clip, final boolean isActive, final int noteRow, final Optional<ColorEx> rowColor)
    {
        this.drawSequencerSteps (clip, isActive, noteRow, rowColor, null);
    }


    /**
     * Draw the sequencer steps.
     *
     * @param clip The clip
     * @param isActive Is there an active clip?
     * @param noteRow The note for which to draw the row
     * @param rowColor The color to use the notes of the row
     * @param yModifier Option to change the order or row number
     */
    protected void drawSequencerSteps (final INoteClip clip, final boolean isActive, final int noteRow, final Optional<ColorEx> rowColor, final IntUnaryOperator yModifier)
    {
        final int step = clip.getCurrentStep ();
        final int hiStep = this.isInXRange (step) ? step % this.sequencerSteps : -1;
        final IPadGrid padGrid = this.surface.getPadGrid ();
        final List<NotePosition> editNotes = this.getEditNotes ();
        final NotePosition notePosition = new NotePosition (this.configuration.getMidiEditChannel (), 0, noteRow);

        for (int col = 0; col < this.sequencerSteps; col++)
        {
            notePosition.setStep (col);
            final IStepInfo stepInfo = clip.getStep (notePosition);
            final boolean hilite = col == hiStep;
            final int x = col % this.numColumns;
            int y = col / this.numColumns;
            if (yModifier != null)
                y = yModifier.applyAsInt (y);
            padGrid.lightEx (x, y, isActive ? this.getStepColor (stepInfo, hilite, rowColor, notePosition.getChannel (), col, noteRow, editNotes) : AbstractSequencerView.COLOR_NO_CONTENT);
        }
    }


    /**
     * Draw the edit pages / loop area of the sequencer.
     *
     * @param clip The clip
     * @param isActive Is there an active clip?
     */
    protected void drawPages (final INoteClip clip, final boolean isActive)
    {
        final boolean isAccentActive = this.configuration.isAccentActive ();
        if (isAccentActive)
        {
            int selectedVelocityPad = 15 - this.configuration.getFixedAccentValue () / 8;
            final int selY = selectedVelocityPad / 4;
            final int selX = selectedVelocityPad % 4;
            selectedVelocityPad = selY * 4 + 3 - selX;

            final IPadGrid padGrid = this.surface.getPadGrid ();
            for (int pad = 0; pad < 16; pad++)
            {
                final int x = this.playColumns + pad % this.playColumns;
                final int y = this.sequencerLines + pad / this.playColumns;
                padGrid.lightEx (x, y, pad == selectedVelocityPad ? MaschineColorManager.COLOR_GREEN : MaschineColorManager.COLOR_LIME_LO);
            }

            return;
        }

        final int step = clip.getCurrentStep ();
        final int lengthOfOnePad = this.getLengthOfOnePage (this.sequencerSteps);
        final double loopStart = clip.getLoopStart ();
        final int loopStartPad = (int) Math.ceil (loopStart / lengthOfOnePad);
        final int loopEndPad = (int) Math.ceil ((loopStart + clip.getLoopLength ()) / lengthOfOnePad);
        final int currentPage = step / this.sequencerSteps;
        final int numOfPages = this.getNumberOfAvailablePages ();
        final IPadGrid padGrid = this.surface.getPadGrid ();
        for (int pad = 0; pad < numOfPages; pad++)
        {
            final int x = this.playColumns + pad % this.playColumns;
            final int y = this.sequencerLines + pad / this.playColumns;
            padGrid.lightEx (x, y, isActive ? this.getPageColor (loopStartPad, loopEndPad, currentPage, clip.getEditPage (), pad) : AbstractSequencerView.COLOR_NO_CONTENT);
        }
    }


    /**
     * Get the number of clip pages which are available to be drawn on.
     *
     * @return The number
     */
    protected int getNumberOfAvailablePages ()
    {
        return this.playColumns * this.playRows;
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


    /**
     * Set the selected pad.
     *
     * @param selectedPad The selected pad
     */
    public void setSelectedPad (final int selectedPad)
    {
        this.setSelectedPad (selectedPad, 127);
    }


    /**
     * Set the selected pad.
     *
     * @param selectedPad The selected pad
     * @param velocity The velocity
     */
    protected void setSelectedPad (final int selectedPad, final int velocity)
    {
        if (this.selectedPad == selectedPad)
            return;

        this.selectedPad = selectedPad;
        this.clearEditNotes ();
        if (velocity > 0)
            this.getDrumDevice ().getDrumPadBank ().getItem (selectedPad).select ();
    }


    /**
     * Get the selected pad.
     *
     * @return The selected pad
     */
    public int getSelectedPad ()
    {
        return this.selectedPad;
    }


    /**
     * Filling a slot from the browser moves the bank view to that slot. This function moves it back
     * to the correct position.
     */
    public void repositionBankPage ()
    {
        if (this.scrollPosition >= 0)
            this.getDrumDevice ().getDrumPadBank ().scrollTo (this.scrollPosition);
    }


    /**
     * Get the drum device to use.
     *
     * @return The drum device
     */
    protected IDrumDevice getDrumDevice ()
    {
        return this.model.getDrumDevice ();
    }
}
