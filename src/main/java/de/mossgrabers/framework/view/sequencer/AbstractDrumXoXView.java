// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.view.sequencer;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.controller.grid.LightInfo;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.daw.clip.ISessionAlternative;
import de.mossgrabers.framework.daw.clip.IStepInfo;
import de.mossgrabers.framework.daw.clip.NotePosition;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.IDrumPadBank;
import de.mossgrabers.framework.daw.data.bank.ISlotBank;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractSessionView;
import de.mossgrabers.framework.view.Views;


/**
 * The abstract Drum XoX view.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author Jürgen Moßgraber
 */
public abstract class AbstractDrumXoXView<S extends IControlSurface<C>, C extends Configuration> extends AbstractDrumView<S, C> implements ISessionAlternative
{
    // @formatter:off
    protected static final int [] DRUM_MATRIX =
    {
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
         0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
    };
    // @formatter:on

    protected static final Map<Resolution, Resolution> PREV_RESOLUTION = new EnumMap<> (Resolution.class);
    protected static final Map<Resolution, Resolution> NEXT_RESOLUTION = new EnumMap<> (Resolution.class);

    static
    {
        PREV_RESOLUTION.put (Resolution.RES_1_4, Resolution.RES_1_4);
        PREV_RESOLUTION.put (Resolution.RES_1_4T, Resolution.RES_1_4);
        PREV_RESOLUTION.put (Resolution.RES_1_8, Resolution.RES_1_4);
        PREV_RESOLUTION.put (Resolution.RES_1_8T, Resolution.RES_1_4);
        PREV_RESOLUTION.put (Resolution.RES_1_16, Resolution.RES_1_8);
        PREV_RESOLUTION.put (Resolution.RES_1_16T, Resolution.RES_1_8);
        PREV_RESOLUTION.put (Resolution.RES_1_32, Resolution.RES_1_16);
        PREV_RESOLUTION.put (Resolution.RES_1_32T, Resolution.RES_1_16);

        NEXT_RESOLUTION.put (Resolution.RES_1_4, Resolution.RES_1_8);
        NEXT_RESOLUTION.put (Resolution.RES_1_4T, Resolution.RES_1_8);
        NEXT_RESOLUTION.put (Resolution.RES_1_8, Resolution.RES_1_16);
        NEXT_RESOLUTION.put (Resolution.RES_1_8T, Resolution.RES_1_16);
        NEXT_RESOLUTION.put (Resolution.RES_1_16, Resolution.RES_1_32);
        NEXT_RESOLUTION.put (Resolution.RES_1_16T, Resolution.RES_1_32);
        NEXT_RESOLUTION.put (Resolution.RES_1_32, Resolution.RES_1_32);
        NEXT_RESOLUTION.put (Resolution.RES_1_32T, Resolution.RES_1_32);
    }

    protected static final int      NUM_CLIPS                   = 16;

    protected ButtonID              deleteButton                = ButtonID.DELETE;
    protected ButtonID              stopButton                  = ButtonID.STOP;
    protected ButtonID              browseButton                = ButtonID.BROWSE;

    protected final ISlotBank       slotBank;
    protected ISlot                 sourceSlot                  = null;
    protected final List<IStepInfo> sourceNotes                 = new ArrayList<> ();
    protected boolean               blockSelectKnob             = false;

    protected int                   numStepRows;
    protected int                   numDrumPadRows;
    protected int                   numClipsRows;
    protected int                   sequencerOffset;
    protected int                   clipsOffset;

    protected ButtonID              editLoopTriggerButton       = ButtonID.FIXED_LENGTH;

    private boolean                 wasAlternateInteractionUsed = false;


    /**
     * Constructor.
     *
     * @param name The name of the view
     * @param surface The surface
     * @param model The model
     * @param numColumns The number of available columns
     */
    public AbstractDrumXoXView (final String name, final S surface, final IModel model, final int numColumns)
    {
        super (name, surface, model, 32 / numColumns, 1, numColumns, 128, 32, true, true);

        this.playColumns = 16;
        this.allRows = this.sequencerLines;

        // Pre-calculation for grid drawing
        this.numClipsRows = NUM_CLIPS / this.numColumns;
        this.numDrumPadRows = this.playColumns / this.numColumns;
        this.numStepRows = this.clipCols / this.numColumns;
        this.sequencerOffset = this.numClipsRows + this.numDrumPadRows;
        this.clipsOffset = this.numStepRows + this.numDrumPadRows;

        this.slotBank = this.model.getSlotBank (NUM_CLIPS);
    }


    /** {@inheritDoc} */
    @Override
    protected void drawPages (final INoteClip clip, final boolean isActive)
    {
        // Clips area
        final AbstractSessionView<?, ?> view = AbstractSessionView.class.cast (this.surface.getViewManager ().get (Views.SESSION));
        final IPadGrid padGrid = this.surface.getPadGrid ();
        final boolean isRecArmed = this.model.getCursorTrack ().isRecArm ();
        for (int x = 0; x < this.slotBank.getPageSize (); x++)
        {
            final ISlot slot = this.slotBank.getItem (x);
            final LightInfo color = view.getPadColor (slot, isRecArmed);
            padGrid.lightEx (x % this.numColumns, x / this.numColumns, color.getColor (), color.getBlinkColor (), color.isFast ());
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void drawDrumPads (final IPadGrid padGrid, final IDrumPadBank drumPadBank)
    {
        // Make the drum pad bank over-writable
        final IDrumPadBank drumPadBank2 = this.getDrumDevice ().getDrumPadBank ();
        final boolean isRecording = this.model.hasRecordingState ();
        for (int x = 0; x < this.playColumns; x++)
            padGrid.lightEx (x % this.numColumns, this.numClipsRows + this.numDrumPadRows - 1 - x / this.numColumns, this.getDrumPadColor (x, drumPadBank2, isRecording));
    }


    /** {@inheritDoc} */
    @Override
    protected void drawSequencerSteps (final INoteClip clip, final boolean isActive, final int noteRow, final Optional<ColorEx> rowColor)
    {
        // Draw loop length
        if (this.isEditLoopRange ())
        {
            int numberOfActiveSteps = (int) Math.floor (clip.getLoopLength () / Resolution.getValueAt (this.getResolutionIndex ()));
            numberOfActiveSteps -= clip.getEditPage () * this.sequencerSteps;

            final IPadGrid padGrid = this.surface.getPadGrid ();
            String color;
            for (int col = 0; col < this.sequencerSteps; col++)
            {
                final boolean isFourth = (col + 1) % 4 == 0;
                if (col < numberOfActiveSteps)
                    color = isFourth ? AbstractSequencerView.COLOR_ACTIVE_PAGE : AbstractSequencerView.COLOR_PAGE;
                else
                    color = isFourth ? AbstractSequencerView.COLOR_SELECTED_PAGE : AbstractSequencerView.COLOR_NO_CONTENT;
                padGrid.lightEx (col % this.numColumns, this.sequencerOffset + col / this.numColumns, color);
            }
            return;
        }

        super.drawSequencerSteps (clip, isActive, noteRow, rowColor, y -> this.sequencerOffset + y);
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
        if (y < this.numStepRows)
        {
            if (this.isActive ())
                this.handleSequencerArea (index, x, y, offsetY, velocity);
            return;
        }

        // Drum Pad row(s)
        if (y >= this.numStepRows && y < this.clipsOffset)
        {
            this.handleNoteArea ((y - this.numStepRows) * this.numColumns + x, 0, offsetY, velocity);
            return;
        }

        // Clips area
        final int row = this.surface.getPadGrid ().getRows () - y - 1;
        this.handleClipRow (index, row * this.numColumns + x, velocity != 0);
    }


    /** {@inheritDoc} */
    @Override
    protected void handleNoteAreaButtonCombinations (final int playedPad)
    {
        if (this.isDuplicateTrigger ())
        {
            this.duplicateSteps ();
            return;
        }

        super.handleNoteAreaButtonCombinations (playedPad);
    }


    /**
     * Handle the clip row(s).
     *
     * @param padIndex The index of the pressed pad
     * @param clipIndex The index of the clip
     * @param isPressed If the pad was pressed
     */
    protected void handleClipRow (final int padIndex, final int clipIndex, final boolean isPressed)
    {
        final boolean isAlternateFunction = this.isAlternateFunction ();
        if (isAlternateFunction)
            this.setAlternateInteractionUsed (true);

        final ISlot slot = this.slotBank.getItem (clipIndex);
        final ITrack track = this.model.getCursorTrack ();

        if (isPressed)
        {
            if (this.handleClipRowButtonCombinations (track, slot))
            {
                // Do not trigger on pad up if already handled
                this.surface.setTriggerConsumed (ButtonID.get (ButtonID.PAD1, padIndex));
                return;
            }
            if (this.configuration.isSelectClipOnLaunch ())
                slot.select ();
        }

        if (!track.isRecArm () || slot.hasContent ())
        {
            this.handleClipLaunch (slot, isPressed);
            return;
        }

        switch (this.configuration.getActionForRecArmedPad ())
        {
            case 0:
                this.model.recordNoteClip (track, slot);
                break;

            case 1:
                final int lengthInBeats = this.configuration.getNewClipLenghthInBeats (this.model.getTransport ().getQuartersPerMeasure ());
                this.model.createNoteClip (track, slot, lengthInBeats, true);
                break;

            case 2:
            default:
                // Do nothing
                break;
        }
    }


    /**
     * Launch the clip.
     *
     * @param slot The slot with the clip to start
     * @param isPressed Was the pad pressed or released?
     */
    protected void handleClipLaunch (final ISlot slot, final boolean isPressed)
    {
        if (this.surface.isPressed (this.buttonSelect))
            slot.select ();
        else
            slot.launch (isPressed, this.isAlternateFunction ());
    }


    /** {@inheritDoc} */
    @Override
    protected void handleSequencerArea (final int index, final int x, final int y, final int offsetY, final int velocity)
    {
        if (this.isEditLoopRange ())
        {
            if (velocity > 0)
                return;

            this.surface.setTriggerConsumed (this.editLoopTriggerButton);

            int steps = (this.numStepRows - 1 - y) * this.numColumns + x + 1;
            final INoteClip clip = this.getClip ();
            steps += clip.getEditPage () * this.sequencerSteps;
            clip.setLoopLength (steps * Resolution.getValueAt (this.getResolutionIndex ()));
            return;
        }

        this.handleNoteEditorMode (x, y, offsetY, velocity);

        super.handleSequencerArea (index, x, y, offsetY, velocity);
    }


    /**
     * Plug-ability for handling note editor mode.
     *
     * @param x The x position of the pad in the sequencer grid
     * @param y The y position of the pad in the sequencer grid
     * @param offsetY The drum offset
     * @param velocity The velocity
     * @return If handled
     */
    protected boolean handleNoteEditorMode (final int x, final int y, final int offsetY, final int velocity)
    {
        return false;
    }


    /**
     * Handle the button combinations on the clip row(s).
     *
     * @param track The track of the slot
     * @param slot The pressed slot
     * @return True if handled
     */
    protected boolean handleClipRowButtonCombinations (final ITrack track, final ISlot slot)
    {
        if (!track.doesExist ())
            return true;

        // Delete selected clip
        if (this.isDeleteTrigger ())
        {
            if (slot.doesExist ())
                slot.remove ();
            return true;
        }

        // Duplicate selected clip
        if (this.isDuplicateTrigger ())
        {
            if (this.sourceSlot != null)
            {
                slot.paste (this.sourceSlot);
                this.sourceSlot = null;
            }
            else if (slot.doesExist () && slot.hasContent ())
                this.sourceSlot = slot;
            this.sourceNotes.clear ();
            return true;
        }

        // Stop clip
        if (this.isButtonCombination (this.stopButton))

        {
            track.stop (false);
            return true;
        }

        // Browse for clips
        if (this.isButtonCombination (this.browseButton))
        {
            this.model.getBrowser ().replace (slot);
            return true;
        }

        return false;
    }


    /**
     * Stores the active steps of the selected drum pad, if there are no saved steps. Otherwise,
     * writes the active steps of the (newly) selected drum pad and clears them afterwards.
     */
    protected void duplicateSteps ()
    {
        this.sourceSlot = null;
        final INoteClip clip = this.getClip ();
        if (!clip.doesExist ())
            return;

        final int drumPad = this.scales.getDrumOffset () + this.selectedPad;
        if (drumPad == -1)
            return;

        // Stores the active steps of the selected drum pad, if there are no saved steps
        final NotePosition notePosition = new NotePosition (this.configuration.getMidiEditChannel (), 0, drumPad);
        if (this.sourceNotes.isEmpty ())
        {
            for (int step = 0; step < this.sequencerSteps; step++)
            {
                notePosition.setStep (step);
                this.sourceNotes.add (clip.getStep (notePosition).createCopy ());
            }
            return;
        }

        // Writes the active steps of the (newly) selected drum pad and clears them afterwards
        for (int step = 0; step < this.sourceNotes.size (); step++)
        {
            notePosition.setStep (step);
            final IStepInfo noteStep = this.sourceNotes.get (step);
            if (noteStep.getVelocity () == 0)
                clip.clearStep (notePosition);
            else
                clip.setStep (notePosition, noteStep);
        }
        this.sourceNotes.clear ();
    }


    /**
     * Test for the duplicate trigger.
     *
     * @return True if duplicate trigger is active
     */
    protected boolean isDuplicateTrigger ()
    {
        return this.isButtonCombination (ButtonID.DUPLICATE);
    }


    /** {@inheritDoc} */
    @Override
    protected int [] getDrumMatrix ()
    {
        final int [] noteMap = Scales.getEmptyMatrix ();

        final int drumOffset = this.scales.getDrumOffset ();

        for (int note = Scales.DRUM_NOTE_START; note < Scales.DRUM_NOTE_END; note++)
        {
            final int ns = DRUM_MATRIX[note - Scales.DRUM_NOTE_START];
            final int n = ns == -1 ? -1 : ns + drumOffset;
            noteMap[note] = n < 0 || n > 127 ? -1 : n;
        }
        return noteMap;
    }


    protected void adjustPage (final boolean isUp, final int selection)
    {
        this.blockSelectKnob = true;
        this.changeOctave (ButtonEvent.DOWN, isUp, 16, true, true);
        this.surface.scheduleTask ( () -> {
            this.selectDrumPad (selection);
            this.blockSelectKnob = false;
        }, 100);
    }


    protected void selectDrumPad (final int index)
    {
        this.getDrumDevice ().getDrumPadBank ().getItem (index).select ();
    }


    protected boolean isEditLoopRange ()
    {
        return this.surface.isPressed (this.editLoopTriggerButton);
    }


    /** {@inheritDoc} */
    @Override
    public boolean wasAlternateInteractionUsed ()
    {
        return this.wasAlternateInteractionUsed;
    }


    /** {@inheritDoc} */
    @Override
    public void setAlternateInteractionUsed (final boolean wasUsed)
    {
        this.wasAlternateInteractionUsed = wasUsed;
    }


    /**
     * Check if the alternate launch/stop function should be executed, e.g. when a SHIFT button is
     * pressed.
     *
     * @return True if alternate function should be executed
     */
    protected boolean isAlternateFunction ()
    {
        return this.surface.isShiftPressed ();
    }


    /**
     * Rotates the steps of the selected drum pad to the left.
     *
     * @param clip The clip which contains the notes
     */
    protected void rotateStepsLeft (final INoteClip clip)
    {
        final int drumPad = this.scales.getDrumOffset () + this.selectedPad;
        if (drumPad == -1)
            return;

        final NotePosition notePosition = new NotePosition (this.configuration.getMidiEditChannel (), 0, drumPad);
        final IStepInfo firstStep = clip.getStep (notePosition).createCopy ();
        for (int step = 1; step < this.sequencerSteps; step++)
        {
            notePosition.setStep (step);
            final IStepInfo noteStep = clip.getStep (notePosition);
            notePosition.setStep (step - 1);
            if (noteStep.getVelocity () == 0)
                clip.clearStep (notePosition);
            else
                clip.setStep (notePosition, noteStep);
        }
        notePosition.setStep (this.sequencerSteps - 1);
        if (firstStep.getVelocity () == 0)
            clip.clearStep (notePosition);
        else
            clip.setStep (notePosition, firstStep);
    }


    /**
     * Rotates the steps of the selected drum pad to the right.
     *
     * @param clip The clip which contains the notes
     */
    protected void rotateStepsRight (final INoteClip clip)
    {
        final int drumPad = this.scales.getDrumOffset () + this.selectedPad;
        if (drumPad == -1)
            return;

        final NotePosition notePosition = new NotePosition (this.configuration.getMidiEditChannel (), this.sequencerSteps - 1, drumPad);
        final IStepInfo lastStep = clip.getStep (notePosition).createCopy ();
        for (int step = 0; step < this.sequencerSteps - 1; step++)
        {
            notePosition.setStep (step);
            final IStepInfo noteStep = clip.getStep (notePosition);
            notePosition.setStep (step + 1);
            if (noteStep.getVelocity () == 0)
                clip.clearStep (notePosition);
            else
                clip.setStep (notePosition, noteStep);
        }
        notePosition.setStep (0);
        if (lastStep.getVelocity () == 0)
            clip.clearStep (notePosition);
        else
            clip.setStep (notePosition, lastStep);
    }
}