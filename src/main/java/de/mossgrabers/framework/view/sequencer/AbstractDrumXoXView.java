// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
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
import de.mossgrabers.framework.daw.clip.IStepInfo;
import de.mossgrabers.framework.daw.clip.NotePosition;
import de.mossgrabers.framework.daw.clip.StepState;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.daw.data.IDrumPad;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.IDrumPadBank;
import de.mossgrabers.framework.daw.data.bank.ISlotBank;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
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
public abstract class AbstractDrumXoXView<S extends IControlSurface<C>, C extends Configuration> extends AbstractDrumView<S, C>
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

    protected static final int      NUM_CLIPS       = 16;

    protected final ISlotBank       slotBank;
    protected ISlot                 sourceSlot      = null;
    protected final List<IStepInfo> sourceNotes     = new ArrayList<> ();
    protected boolean               blockSelectKnob = false;
    protected boolean               isCopy          = true;
    protected boolean               isSolo          = true;
    protected boolean               editLoopRange   = false;

    protected int                   numStepRows;
    protected int                   numDrumPadRows;
    protected int                   numClipsRows;
    protected int                   sequencerOffset;
    protected int                   clipsOffset;


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
            padGrid.lightEx (x % this.numColumns, this.numClipsRows + this.numDrumPadRows - 1 - (x / this.numColumns), this.getDrumPadColor (x, drumPadBank2, isRecording));
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
            if (this.isCopy && this.isButtonCombination (ButtonID.SCENE1))
            {
                if (velocity > 0)
                    return;

                this.sourceSlot = null;
                final INoteClip clip = this.getClip ();
                if (!clip.doesExist ())
                    return;

                // TODO Test / fix

                final int drumPad = this.scales.getDrumOffset () + this.selectedPad;
                if (drumPad != -1)
                {
                    final NotePosition notePosition = new NotePosition (this.configuration.getMidiEditChannel (), 0, drumPad);
                    if (this.sourceNotes.isEmpty ())
                    {
                        for (int step = 0; step < this.sequencerSteps; step++)
                        {
                            notePosition.setStep (step);
                            this.sourceNotes.add (clip.getStep (notePosition).createCopy ());
                        }
                    }
                    else
                    {
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
                }
                return;
            }

            this.handleNoteArea ((y - this.numStepRows) * this.numColumns + x, 0, offsetY, velocity);
            return;
        }

        // Clips area
        final int row = this.surface.getPadGrid ().getRows () - y - 1;
        this.handleClipRow (row * this.numColumns + x, velocity);
    }


    protected void handleClipRow (final int clipIndex, final int velocity)
    {
        final ISlot slot = this.slotBank.getItem (clipIndex);
        final ITrack track = this.model.getCursorTrack ();

        final boolean isPressed = velocity != 0;
        if (isPressed)
        {
            if (this.handleClipRowButtonCombinations (track, slot))
                return;
            if (this.configuration.isSelectClipOnLaunch ())
                slot.select ();
        }

        if (!track.isRecArm () || slot.hasContent ())
        {
            if (!this.surface.isShiftPressed ())
                slot.launch (isPressed, false);
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


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        if (event != ButtonEvent.UP || !this.isActive () || this.handleGridNavigation (buttonID) || !ButtonID.isSceneButton (buttonID))
            return;

        // TODO Test / fix

        final IDrumPadBank drumPadBank2 = this.getDrumDevice ().getDrumPadBank ();
        final int index = buttonID.ordinal () - ButtonID.SCENE1.ordinal ();
        switch (index)
        {
            case 0:
                this.isCopy = !this.isCopy;
                break;

            case 1:
                if (this.isButtonCombination (ButtonID.ALT))
                {
                    for (int i = 0; i < drumPadBank2.getPageSize (); i++)
                    {
                        final IDrumPad item = drumPadBank2.getItem (i);
                        if (this.isSolo)
                            item.setSolo (false);
                        else
                            item.setMute (false);
                    }
                    return;
                }
                this.isSolo = !this.isSolo;
                break;

            case 2:
                this.editLoopRange = !this.editLoopRange;
                break;

            case 3:
            default:
                this.configuration.toggleNoteRepeatActive ();
                this.mvHelper.delayDisplay ( () -> "Note Repeat: " + (this.configuration.isNoteRepeatActive () ? "On" : "Off"));
                break;
        }
    }


    /**
     * Handle the grid left/right buttons.
     *
     * @param buttonID The button ID
     * @return True if handled
     */
    protected boolean handleGridNavigation (final ButtonID buttonID)
    {
        // TODO Test / fix

        final INoteClip clip = this.getClip ();
        if (buttonID == ButtonID.ARROW_LEFT)
        {
            if (this.surface.isPressed (ButtonID.SHIFT))
            {
                final int drumPad = this.scales.getDrumOffset () + this.selectedPad;
                if (drumPad != -1)
                {
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
            }
            else if (this.surface.isPressed (ButtonID.ALT))
                this.setResolutionIndex (this.getResolutionIndex () - 1);
            else
            {
                clip.scrollStepsPageBackwards ();
                this.mvHelper.notifyEditPage (clip);
            }
            return true;
        }

        if (buttonID == ButtonID.ARROW_RIGHT)
        {
            if (this.surface.isPressed (ButtonID.SHIFT))
            {
                final int drumPad = this.scales.getDrumOffset () + this.selectedPad;
                if (drumPad != -1)
                {
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
            else if (this.surface.isPressed (ButtonID.ALT))
                this.setResolutionIndex (this.getResolutionIndex () + 1);
            else
            {
                clip.scrollStepsPageForward ();
                this.mvHelper.notifyEditPage (clip);
            }
            return true;
        }

        return false;
    }


    /** {@inheritDoc} */
    @Override
    protected void handleSequencerArea (final int index, final int x, final int y, final int offsetY, final int velocity)
    {
        // TODO Test / fix

        if (this.isEditLoopRange ())
        {
            if (velocity > 0)
                return;

            if (this.surface.isPressed (ButtonID.SCENE3))
                this.surface.setTriggerConsumed (ButtonID.SCENE3);

            int steps = (1 - y) * this.numColumns + x + 1;
            final INoteClip clip = this.getClip ();
            steps += clip.getEditPage () * this.sequencerSteps;
            clip.setLoopLength (steps * Resolution.getValueAt (this.getResolutionIndex ()));
            return;
        }

        // TODO Test / fix

        // Handle note editor mode
        final ModeManager modeManager = this.surface.getModeManager ();
        if (velocity > 0)
        {
            if (modeManager.isActive (Modes.NOTE))
            {
                // Store existing note for editing
                final int sound = offsetY + this.selectedPad;
                final int step = this.numColumns * (this.allRows - 1 - y) + x;
                final NotePosition notePosition = new NotePosition (this.configuration.getMidiEditChannel (), step, sound);
                final INoteClip clip = this.getClip ();
                final StepState state = clip.getStep (notePosition).getState ();
                if (state == StepState.START)
                    this.editNote (clip, notePosition, true);
                return;
            }
        }
        else
        {
            if (this.isNoteEdited)
                this.isNoteEdited = false;
            if (modeManager.isActive (Modes.NOTE))
                return;
        }

        super.handleSequencerArea (index, x, y, offsetY, velocity);
    }


    protected boolean handleClipRowButtonCombinations (final ITrack track, final ISlot slot)
    {
        if (!track.doesExist ())
            return true;

        // TODO Test / fix

        // Delete selected clip
        if (this.isButtonCombination (ButtonID.SCENE1))
        {
            if (this.isCopy)
            {
                if (this.sourceSlot != null)
                {
                    slot.paste (this.sourceSlot);
                    this.sourceSlot = null;
                }
                else if (slot.doesExist () && slot.hasContent ())
                    this.sourceSlot = slot;
                this.sourceNotes.clear ();
            }
            else if (slot.doesExist ())
                slot.remove ();
            return true;
        }

        // Stop clip
        if (this.isButtonCombination (ButtonID.STOP))
        {
            track.stop (false);
            return true;
        }

        // Browse for clips
        if (this.isButtonCombination (ButtonID.BROWSE))
        {
            this.model.getBrowser ().replace (slot);
            return true;
        }

        return false;
    }


    /** {@inheritDoc} */
    @Override
    protected boolean isSoloTrigger ()
    {
        // TODO Test / fix

        return this.isSolo && this.isButtonCombination (ButtonID.SCENE2);
    }


    /** {@inheritDoc} */
    @Override
    protected boolean isMuteTrigger ()
    {
        // TODO Test / fix

        return !this.isSolo && this.isButtonCombination (ButtonID.SCENE2);
    }


    /** {@inheritDoc} */
    @Override
    protected boolean isDeleteTrigger ()
    {
        // TODO Test / fix

        return !this.isCopy && this.isButtonCombination (ButtonID.SCENE1);
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


    /** {@inheritDoc} */
    @Override
    protected boolean handleSequencerAreaButtonCombinations (final INoteClip clip, final NotePosition notePosition, final int velocity)
    {
        // TODO Test / fix

        final boolean isUpPressed = this.surface.isPressed (ButtonID.ARROW_UP);
        if (isUpPressed || this.surface.isPressed (ButtonID.ARROW_DOWN))
        {
            this.surface.setTriggerConsumed (isUpPressed ? ButtonID.ARROW_UP : ButtonID.ARROW_DOWN);
            if (velocity > 0)
                this.handleSequencerAreaRepeatOperator (clip, notePosition, velocity, isUpPressed);
            return true;
        }

        return super.handleSequencerAreaButtonCombinations (clip, notePosition, velocity);
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
        // TODO Test / fix

        return this.surface.isPressed (ButtonID.SCENE3) || this.editLoopRange;
    }
}