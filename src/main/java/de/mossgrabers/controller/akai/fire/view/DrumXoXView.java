// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.fire.view;

import de.mossgrabers.controller.akai.fire.FireConfiguration;
import de.mossgrabers.controller.akai.fire.controller.FireControlSurface;
import de.mossgrabers.controller.akai.fire.controller.FirePadGrid;
import de.mossgrabers.controller.akai.fire.mode.FireLayerMode;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.controller.grid.LightInfo;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.daw.clip.IStepInfo;
import de.mossgrabers.framework.daw.clip.NotePosition;
import de.mossgrabers.framework.daw.clip.StepState;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.daw.data.IDrumDevice;
import de.mossgrabers.framework.daw.data.IDrumPad;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.IDrumPadBank;
import de.mossgrabers.framework.daw.data.bank.ISlotBank;
import de.mossgrabers.framework.featuregroup.IMode;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.Views;
import de.mossgrabers.framework.view.sequencer.AbstractDrumView;
import de.mossgrabers.framework.view.sequencer.AbstractSequencerView;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * The Drum view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DrumXoXView extends AbstractDrumView<FireControlSurface, FireConfiguration> implements IFireView
{
    // @formatter:off
    private static final int [] DRUM_MATRIX =
    {
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
         0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
    };
    // @formatter:on

    private static final Map<Resolution, Resolution> PREV_RESOLUTION = new EnumMap<> (Resolution.class);
    private static final Map<Resolution, Resolution> NEXT_RESOLUTION = new EnumMap<> (Resolution.class);

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

    private final ISlotBank       slotBank;
    private ISlot                 sourceSlot      = null;
    private final List<IStepInfo> sourceNotes     = new ArrayList<> ();
    private boolean               blockSelectKnob = false;
    private boolean               isCopy          = true;
    private boolean               isSolo          = true;
    private boolean               editLoopRange   = false;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public DrumXoXView (final FireControlSurface surface, final IModel model)
    {
        super (Views.NAME_DRUM, surface, model, 2, 1, 16, 128, 2 * 16, true, true);

        this.playColumns = 16;
        this.allRows = 2;

        this.buttonSelect = ButtonID.ALT;

        this.slotBank = this.model.getSlotBank (this.playColumns);
    }


    /** {@inheritDoc} */
    @Override
    protected void drawDrumPads (final IPadGrid padGrid, final IDrumPadBank drumPadBank)
    {
        final IDrumPadBank drumPadBank16 = this.getDrumDevice ().getDrumPadBank ();

        final boolean isRecording = this.model.hasRecordingState ();
        for (int x = 0; x < this.playColumns; x++)
            padGrid.lightEx (x, 1, this.getDrumPadColor (x, drumPadBank16, isRecording));
    }


    /** {@inheritDoc} */
    @Override
    protected void drawPages (final INoteClip clip, final boolean isActive)
    {
        // Clips area
        final SessionView view = (SessionView) this.surface.getViewManager ().get (Views.SESSION);
        final FirePadGrid padGrid = this.surface.getPadGrid ();
        final boolean isRecArmed = this.model.getCursorTrack ().isRecArm ();
        for (int x = 0; x < this.slotBank.getPageSize (); x++)
        {
            final ISlot slot = this.slotBank.getItem (x);
            final LightInfo color = view.getPadColor (slot, isRecArmed);
            padGrid.lightEx (x, 0, color.getColor (), color.getBlinkColor (), color.isFast ());
        }
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
                padGrid.lightEx (col % this.numColumns, 2 + col / this.numColumns, color);
            }
            return;
        }

        super.drawSequencerSteps (clip, isActive, noteRow, rowColor, y -> 2 + y);
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
        if (y < 2)
        {
            if (this.isActive ())
                this.handleSequencerArea (index, x, y, offsetY, velocity);
            return;
        }

        // Drum Pad row
        if (y == 2)
        {
            if (this.isCopy && this.isButtonCombination (ButtonID.SCENE1))
            {
                if (velocity > 0)
                    return;

                this.sourceSlot = null;
                final INoteClip clip = this.getClip ();
                if (!clip.doesExist ())
                    return;

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

            this.handleNoteArea (x, 0, offsetY, velocity);
            return;
        }

        // Clips area
        this.handleClipRow (x, velocity);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNoteLongPress (final int note)
    {
        if (this.surface.isShiftPressed () || this.surface.isSelectPressed ())
            return;

        final int index = note - DRUM_START_KEY;
        final int y = index / this.numColumns;
        if (y < 3)
            return;

        final int clipIndex = index % this.numColumns;
        final ISlot slot = this.slotBank.getItem (clipIndex);

        slot.select ();
        if (slot.hasContent ())
        {
            final String slotName = slot.getName ();
            if (!slotName.isBlank ())
                this.surface.getDisplay ().notify (slotName);
        }

        this.surface.getButton (ButtonID.get (ButtonID.PAD1, index)).setConsumed ();
    }


    private void handleClipRow (final int clipIndex, final int velocity)
    {
        final ISlot slot = this.slotBank.getItem (clipIndex);
        final ITrack track = this.model.getCursorTrack ();

        if (this.surface.isSelectPressed () && velocity != 0)
        {
            this.surface.setTriggerConsumed (ButtonID.SELECT);
            slot.launchImmediately ();
            return;
        }

        // Trigger on pad release to intercept long presses
        if (velocity != 0 || this.handleClipRowButtonCombinations (track, slot))
            return;

        if (this.surface.getConfiguration ().isSelectClipOnLaunch ())
            slot.select ();

        if (!track.isRecArm () || slot.hasContent ())
        {
            // Needs to be called here to always reset the state!
            final boolean wasLaunchedImmediately = slot.testAndClearLaunchedImmediately ();
            if (this.surface.isSelectPressed ())
                track.launchLastClipImmediately ();
            else if (!wasLaunchedImmediately)
                slot.launch ();
            return;
        }

        final FireConfiguration configuration = this.surface.getConfiguration ();
        switch (configuration.getActionForRecArmedPad ())
        {
            case 0:
                this.model.recordNoteClip (track, slot);
                break;

            case 1:
                final int lengthInBeats = configuration.getNewClipLenghthInBeats (this.model.getTransport ().getQuartersPerMeasure ());
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
    public void onSelectKnobValue (final int value)
    {
        final IDrumDevice drumDevice = this.getDrumDevice ();
        if (!drumDevice.hasDrumPads () || this.blockSelectKnob)
            return;

        final boolean isUp = this.model.getValueChanger ().isIncrease (value);

        // Change note repeat if active and a pad is held
        if (this.configuration.isNoteRepeatActive ())
        {
            boolean isDrumPadPressed = false;
            for (int i = 0; i < 16; i++)
            {
                if (this.surface.isPressed (ButtonID.get (ButtonID.PAD33, i)))
                    isDrumPadPressed = true;
            }
            if (isDrumPadPressed)
            {
                final Resolution activePeriod = this.configuration.getNoteRepeatPeriod ();
                final Resolution sel;
                if (isUp)
                    sel = NEXT_RESOLUTION.get (activePeriod);
                else
                    sel = PREV_RESOLUTION.get (activePeriod);
                this.configuration.setNoteRepeatPeriod (sel);
                this.mvHelper.delayDisplay ( () -> "Period: " + sel.getName ());
                return;
            }
        }

        this.adjustPage (isUp, 0);
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        if (event != ButtonEvent.UP || !this.isActive () || this.handleGridNavigation (buttonID) || !ButtonID.isSceneButton (buttonID))
            return;

        final IDrumPadBank drumPadBank16 = this.getDrumDevice ().getDrumPadBank ();
        final int index = buttonID.ordinal () - ButtonID.SCENE1.ordinal ();
        switch (index)
        {
            case 0:
                this.isCopy = !this.isCopy;
                break;

            case 1:
                if (this.isButtonCombination (ButtonID.ALT))
                {
                    for (int i = 0; i < drumPadBank16.getPageSize (); i++)
                    {
                        final IDrumPad item = drumPadBank16.getItem (i);
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


    /** {@inheritDoc} */
    @Override
    public int getSoloButtonColor (final int index)
    {
        if (!this.isActive ())
            return 0;

        switch (index)
        {
            case 0:
                return this.isCopy ? 2 : 1;

            case 1:
                return this.isSolo ? 2 : 1;

            case 2:
                return this.isEditLoopRange () ? 2 : 0;

            default:
            case 3:
                return this.configuration.isNoteRepeatActive () ? 2 : 0;
        }
    }


    protected boolean handleClipRowButtonCombinations (final ITrack track, final ISlot slot)
    {
        if (!track.doesExist ())
            return true;

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
            track.stop ();
            return true;
        }

        // Browse for clips
        if (this.isButtonCombination (ButtonID.BROWSE))
        {
            this.model.getBrowser ().replace (slot);
            return true;
        }

        // Select the clip (without playback)
        if (this.isButtonCombination (ButtonID.ALT))
        {
            slot.select ();
            return true;
        }

        return false;
    }


    /** {@inheritDoc} */
    @Override
    protected boolean isSoloTrigger ()
    {
        return this.isSolo && this.isButtonCombination (ButtonID.SCENE2);
    }


    /** {@inheritDoc} */
    @Override
    protected boolean isMuteTrigger ()
    {
        return !this.isSolo && this.isButtonCombination (ButtonID.SCENE2);
    }


    /** {@inheritDoc} */
    @Override
    protected boolean isDeleteTrigger ()
    {
        return !this.isCopy && this.isButtonCombination (ButtonID.SCENE1);
    }


    /** {@inheritDoc} */
    @Override
    protected IDrumDevice getDrumDevice ()
    {
        return this.model.getDrumDevice (16);
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


    private void adjustPage (final boolean isUp, final int selection)
    {
        this.blockSelectKnob = true;
        this.changeOctave (ButtonEvent.DOWN, isUp, 16, true, true);
        this.surface.scheduleTask ( () -> {
            this.selectDrumPad (selection);
            this.blockSelectKnob = false;
        }, 100);
    }


    private void selectDrumPad (final int index)
    {
        this.getDrumDevice ().getDrumPadBank ().getItem (index).select ();
        final IMode activeMode = this.surface.getModeManager ().getActive ();
        if (activeMode instanceof final FireLayerMode fireLayerMode)
            fireLayerMode.parametersAdjusted ();
    }


    private boolean isEditLoopRange ()
    {
        return this.surface.isPressed (ButtonID.SCENE3) || this.editLoopRange;
    }
}