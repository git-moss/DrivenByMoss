// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.view.sequencer;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.controller.hardware.IHwButton;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.clip.DefaultStepInfo;
import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.daw.clip.IStepInfo;
import de.mossgrabers.framework.daw.clip.NotePosition;
import de.mossgrabers.framework.daw.clip.StepState;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractPlayView;
import de.mossgrabers.framework.view.TransposeView;
import de.mossgrabers.framework.view.Views;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * The abstract Poly Sequencer view.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractPolySequencerView<S extends IControlSurface<C>, C extends Configuration> extends AbstractSequencerView<S, C> implements TransposeView
{
    protected static final int            GRID_COLUMNS        = 8;
    protected static final int            GRID_ROWS           = 8;
    protected static final int            NUM_SEQUENCER_LINES = 4;

    protected final int                   sequencerSteps;
    protected final Map<Integer, Integer> noteMemory          = new HashMap<> ();
    protected int                         copyStep            = -1;
    protected int                         numColumns;
    protected int                         numRows;
    protected long                        lastPressedNoteTime = 0;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     * @param useDawColors True to use the color of the current track for coloring the octaves
     */
    protected AbstractPolySequencerView (final S surface, final IModel model, final boolean useDawColors)
    {
        this (surface, model, useDawColors, GRID_COLUMNS, GRID_ROWS, NUM_SEQUENCER_LINES);
    }


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     * @param useDawColors True to use the color of the current track for coloring the octaves
     * @param numColumns The number of columns of the grid
     * @param numRows The number of rows of the grid
     * @param numSequencerRows The number of rows to use for the sequencer (rest is for the play
     *            area)
     */
    protected AbstractPolySequencerView (final S surface, final IModel model, final boolean useDawColors, final int numColumns, final int numRows, final int numSequencerRows)
    {
        super (Views.NAME_POLY_SEQUENCER, surface, model, 128, numSequencerRows * numColumns, useDawColors);

        this.sequencerSteps = numSequencerRows * numColumns;

        this.numColumns = numColumns;
        this.numRows = numRows;
        this.numSequencerRows = numSequencerRows;

        final ITrackBank tb = model.getTrackBank ();
        tb.addSelectionObserver ( (index, isSelected) -> this.keyManager.clearPressedKeys ());
        tb.addNoteObserver (this.keyManager::call);
    }


    /** {@inheritDoc} */
    @Override
    public void onOctaveDown (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        if (this.surface.isShiftPressed ())
        {
            this.getClip ().transpose (-1);
            return;
        }

        if (this.surface.isSelectPressed ())
        {
            this.getClip ().transpose (-12);
            return;
        }

        this.keyManager.clearPressedKeys ();
        this.scales.decOctave ();
        this.updateNoteMapping ();
        this.clearEditNotes ();
        this.surface.getDisplay ().notify (this.scales.getRangeText ());
    }


    /** {@inheritDoc} */
    @Override
    public void onOctaveUp (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        if (this.surface.isShiftPressed ())
        {
            this.getClip ().transpose (1);
            return;
        }

        if (this.surface.isSelectPressed ())
        {
            this.getClip ().transpose (12);
            return;
        }

        this.keyManager.clearPressedKeys ();
        this.scales.incOctave ();
        this.updateNoteMapping ();
        this.clearEditNotes ();
        this.surface.getDisplay ().notify (this.scales.getRangeText ());
    }


    /** {@inheritDoc} */
    @Override
    public boolean isOctaveUpButtonOn ()
    {
        return true;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isOctaveDownButtonOn ()
    {
        return true;
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        final boolean isKeyboardEnabled = this.model.canSelectedTrackHoldNotes ();
        if (!isKeyboardEnabled)
            return;

        final int index = note - 36;
        final int x = index % this.numColumns;
        final int y = index / this.numColumns;

        if (y < this.numRows - this.numSequencerRows)
        {
            this.handleNoteArea (note, velocity);
            return;
        }

        // Only allow playing the notes if there is no clip
        // Toggle the note on up, so we can intercept the long presses (but not yet used)
        if (this.isActive ())
            this.handleSequencerArea (x, y, velocity);
    }


    /**
     * Handle button presses in the note area of the poly sequencer.
     *
     * @param note The played note
     * @param velocity The velocity
     */
    protected void handleNoteArea (final int note, final int velocity)
    {
        // No pressed keys? Clear up the note memory for programming the sequencer...
        final long now = System.currentTimeMillis ();
        if (now - this.lastPressedNoteTime > 500)
        {
            this.lastPressedNoteTime = now;
            this.noteMemory.clear ();
        }

        // Mark selected notes immediately for better performance
        final int mappedNote = this.keyManager.map (note);
        if (mappedNote == -1)
            return;

        this.keyManager.setAllKeysPressed (mappedNote, velocity);
        if (velocity > 0)
            this.noteMemory.put (Integer.valueOf (mappedNote), Integer.valueOf (velocity));
    }


    /**
     * Handle pads pressed in the sequencer area.
     *
     * @param x The x position of the pad
     * @param y The y position of the pad
     * @param velocity The velocity
     */
    protected void handleSequencerArea (final int x, final int y, final int velocity)
    {
        if (velocity != 0)
            return;

        final INoteClip clip = this.getClip ();
        final int step = this.numColumns * (this.numRows - 1 - y) + x;
        final NotePosition notePosition = new NotePosition (this.configuration.getMidiEditChannel (), step, 0);

        if (this.handleSequencerAreaButtonCombinations (clip, notePosition.getChannel (), step))
            return;

        // Clear all notes on the step, if there is at least one
        if (this.getStep (clip, step).getState () != StepState.OFF)
        {
            for (int row = 0; row < 128; row++)
            {
                notePosition.setNote (row);
                if (clip.getStep (notePosition).getState () != StepState.OFF)
                    clip.clearStep (notePosition);
            }
            return;
        }

        // Create a step and fill it with the memorized notes
        final boolean isAccentActive = this.configuration.isAccentActive ();
        boolean notesPresent = false;
        for (int row = 0; row < 128; row++)
        {
            notePosition.setNote (row);
            final Integer k = Integer.valueOf (row);
            if (this.noteMemory.containsKey (k))
            {
                final int v = isAccentActive ? this.configuration.getFixedAccentValue () : this.noteMemory.get (k).intValue ();
                clip.toggleStep (notePosition, v);
                notesPresent = true;
            }
        }
        if (!notesPresent)
            this.surface.getDisplay ().notify ("Play some notes in the lower part first.");
    }


    /**
     * Handle button combinations in the sequencer area.
     *
     * @param clip The sequenced MIDI clip
     * @param channel The MIDI channel of the note
     * @param step The step in the current page in the clip
     * @return True if handled
     */
    protected boolean handleSequencerAreaButtonCombinations (final INoteClip clip, final int channel, final int step)
    {
        final NotePosition notePosition = new NotePosition (channel, step, 0);

        // Handle note duplicate function
        if (this.isButtonCombination (ButtonID.DUPLICATE))
        {
            if (this.getStep (clip, step).getState () == StepState.START)
                this.copyStep = step;
            else if (this.copyStep >= 0)
            {
                final NotePosition copyPosition = new NotePosition (channel, this.copyStep, 0);
                for (int row = 0; row < 128; row++)
                {
                    copyPosition.setNote (row);
                    final IStepInfo stepInfo = clip.getStep (copyPosition);
                    if (stepInfo != null && stepInfo.getVelocity () > 0)
                    {
                        notePosition.setNote (row);
                        clip.setStep (notePosition, stepInfo);
                    }
                }
            }
            return true;
        }

        if (this.isButtonCombination (ButtonID.MUTE))
        {
            for (int note = 0; note < 128; note++)
            {
                notePosition.setNote (note);
                final IStepInfo stepInfo = clip.getStep (notePosition);
                final StepState isSet = stepInfo.getState ();
                if (isSet == StepState.START)
                    clip.updateStepMuteState (notePosition, !stepInfo.isMuted ());
            }
            return true;
        }

        return this.changeLengthOfNotes (clip, notePosition);
    }


    /**
     * Change the length of all notes of the step or create a new one.
     *
     * @param clip The clip
     * @param notePosition The note position
     * @return At least one note was present and its' length has been changed
     */
    protected boolean changeLengthOfNotes (final INoteClip clip, final NotePosition notePosition)
    {
        final int step = notePosition.getStep ();

        // Find the step before the current step which is long pressed
        for (int s = step - 1; s >= 0; s--)
        {
            notePosition.setStep (s);
            final int x = s % this.numColumns;
            final int y = this.numRows - 1 - s / this.numColumns;
            final int pad = y * this.numColumns + x;
            final IHwButton button = this.surface.getButton (ButtonID.get (ButtonID.PAD1, pad));
            if (button.isLongPressed ())
            {
                button.setConsumed ();
                final int length = step - s + 1;
                final double duration = length * Resolution.getValueAt (this.getResolutionIndex ());

                // Create new note(s)
                if (this.getStep (clip, s).getState () != StepState.START)
                {
                    final boolean isAccentActive = this.configuration.isAccentActive ();
                    for (int row = 0; row < 128; row++)
                    {
                        notePosition.setNote (row);
                        final Integer k = Integer.valueOf (row);
                        if (this.noteMemory.containsKey (k))
                        {
                            final int velocity = isAccentActive ? this.configuration.getFixedAccentValue () : this.noteMemory.get (k).intValue ();
                            clip.setStep (notePosition, velocity, duration);
                        }
                    }
                    return true;
                }

                // Change length of existing notes
                for (int row = 0; row < 128; row++)
                {
                    notePosition.setNote (row);
                    final IStepInfo stepInfo = clip.getStep (notePosition);
                    if (stepInfo != null && stepInfo.getState () == StepState.START)
                        clip.updateStepDuration (notePosition, duration);
                }

                return true;
            }
        }

        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final IPadGrid padGrid = this.surface.getPadGrid ();

        final boolean isKeyboardEnabled = this.model.canSelectedTrackHoldNotes ();
        if (!isKeyboardEnabled)
        {
            padGrid.turnOff ();
            return;
        }

        final INoteClip clip = this.getClip ();
        final boolean isActive = this.isActive ();
        final int step = clip.getCurrentStep ();

        // Paint the sequencer steps
        final int hiStep = this.isInXRange (step) ? step % this.sequencerSteps : -1;
        final List<NotePosition> editNotes = this.getEditNotes ();
        for (int col = 0; col < this.sequencerSteps; col++)
        {
            final IStepInfo stepInfo = this.getStep (clip, col);
            final boolean hilite = col == hiStep;
            final int x = col % this.numColumns;
            final int y = col / this.numColumns;
            padGrid.lightEx (x, y, isActive ? this.getStepColor (stepInfo, hilite, col, editNotes) : AbstractSequencerView.COLOR_NO_CONTENT);
        }

        // Paint the play part
        final boolean isRecording = this.model.hasRecordingState ();
        final ITrack cursorTrack = this.model.getCursorTrack ();
        final int startNote = this.scales.getStartNote ();
        for (int i = startNote; i < startNote + this.sequencerSteps; i++)
            padGrid.light (i, this.getGridColor (isKeyboardEnabled, isRecording, cursorTrack, i));

    }


    /** {@inheritDoc} */
    @Override
    public void updateNoteMapping ()
    {
        final int [] matrix = this.scales.getNoteMatrix ();
        for (int i = this.scales.getStartNote () + this.sequencerSteps; i < this.scales.getEndNote (); i++)
            matrix[i] = -1;
        this.delayedUpdateNoteMapping (matrix);
    }


    /**
     * Check if any note is set at the current step.
     *
     * @param clip The clip which contains the notes
     * @param col The column/step to check
     * @return The aggregated about the step aggregated from all notes at that step
     */
    protected IStepInfo getStep (final INoteClip clip, final int col)
    {
        final DefaultStepInfo result = new DefaultStepInfo ();

        boolean isMuted = true;

        final NotePosition notePosition = new NotePosition (this.configuration.getMidiEditChannel (), col, 0);
        for (int row = 0; row < 128; row++)
        {
            notePosition.setNote (row);
            final IStepInfo stepInfo = clip.getStep (notePosition);
            final StepState r = stepInfo.getState ();
            if (r == StepState.START)
                result.setState (StepState.START);
            else if (r == StepState.CONTINUE && result.getState () != StepState.START)
                result.setState (StepState.CONTINUE);

            if ((r == StepState.START || r == StepState.CONTINUE) && !stepInfo.isMuted ())
                isMuted = false;
        }
        result.setMuted (isMuted);
        return result;
    }


    /**
     * Get the step color.
     *
     * @param stepInfo The note info
     * @param hilite True if note should be highlighted
     * @param step The step
     * @param editNotes The currently edited notes
     * @return The color identifier
     */
    protected String getStepColor (final IStepInfo stepInfo, final boolean hilite, final int step, final List<NotePosition> editNotes)
    {
        final int channel = this.configuration.getMidiEditChannel ();

        switch (stepInfo.getState ())
        {
            // Note starts
            case START:
                if (hilite)
                    return COLOR_STEP_HILITE_CONTENT;
                if (isChordEdit (channel, step, editNotes))
                    return COLOR_STEP_SELECTED;
                if (stepInfo.isMuted ())
                    return COLOR_STEP_MUTED;
                return COLOR_CONTENT;

            // Note continues
            case CONTINUE:
                if (hilite)
                    return COLOR_STEP_HILITE_CONTENT;
                if (isChordEdit (channel, step, editNotes))
                    return COLOR_STEP_SELECTED;
                if (stepInfo.isMuted ())
                    return COLOR_STEP_MUTED_CONT;
                return COLOR_CONTENT_CONT;

            // Empty
            case OFF:
            default:
                return hilite ? AbstractSequencerView.COLOR_STEP_HILITE_NO_CONTENT : AbstractSequencerView.COLOR_NO_CONTENT;
        }
    }


    /**
     * Get the color for a pad.
     *
     * @param isKeyboardEnabled Can we play?
     * @param isRecording Is recording enabled?
     * @param track The track to use the color for octaves
     * @param note The note of the pad
     * @return The ID of the color
     */
    protected String getGridColor (final boolean isKeyboardEnabled, final boolean isRecording, final ITrack track, final int note)
    {
        if (isKeyboardEnabled)
        {
            if (this.keyManager.isKeyPressed (note))
                return isRecording ? AbstractPlayView.COLOR_RECORD : AbstractPlayView.COLOR_PLAY;
            return this.getPadColor (note, this.useDawColors ? track : null);
        }
        return AbstractPlayView.COLOR_OFF;
    }


    protected static boolean isChordEdit (final int channel, final int step, final List<NotePosition> editNotes)
    {
        for (final NotePosition editNote: editNotes)
        {
            if (editNote.getChannel () == channel && editNote.getStep () == step)
                return true;
        }
        return false;
    }
}