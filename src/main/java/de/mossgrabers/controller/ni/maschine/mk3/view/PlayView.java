// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.mk3.view;

import de.mossgrabers.controller.ni.maschine.core.MaschineColorManager;
import de.mossgrabers.controller.ni.maschine.mk3.MaschineConfiguration;
import de.mossgrabers.controller.ni.maschine.mk3.controller.MaschineControlSurface;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.display.IDisplay;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.controller.hardware.IHwButton;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.daw.clip.IStepInfo;
import de.mossgrabers.framework.daw.clip.NotePosition;
import de.mossgrabers.framework.daw.clip.StepState;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.MidiConstants;
import de.mossgrabers.framework.featuregroup.IMode;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.INoteMode;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractPlayView;
import de.mossgrabers.framework.view.sequencer.AbstractSequencerView;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.IntUnaryOperator;


/**
 * The Play view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PlayView extends AbstractPlayView<MaschineControlSurface, MaschineConfiguration>
{
    private static final int            SEQUENCER_STEPS = 16;

    private final DrumView              drumView;
    private final MaschineConfiguration configuration;
    private final int                   numColumns;
    private boolean                     isShifted       = false;
    private int                         selectedNote    = -1;
    private IStepInfo                   copyNote;
    private boolean                     isChordActive;


    /**
     * Constructor.
     *
     * @param surface The controller
     * @param model The model
     * @param drumView The drum view for the sequencer
     */
    public PlayView (final MaschineControlSurface surface, final IModel model, final DrumView drumView)
    {
        super (surface, model, true);

        this.drumView = drumView;
        this.numColumns = 4;
        this.configuration = surface.getConfiguration ();
        this.configuration.addSettingObserver (AbstractConfiguration.ACTIVATE_FIXED_ACCENT, this::initMaxVelocity);
        this.configuration.addSettingObserver (AbstractConfiguration.FIXED_ACCENT_VALUE, this::initMaxVelocity);
    }


    /**
     * Toggle the chord mode on/off.
     */
    public void toggleChordMode ()
    {
        this.isChordActive = !this.isChordActive;
    }


    /**
     * Test if the chord mode is on/off.
     *
     * @return True if the chord mode is on
     */
    public boolean isChordMode ()
    {
        return this.isChordActive;
    }


    /**
     * Toggle to 'shifted' view for editing the configuration settings.
     */
    public void toggleShifted ()
    {
        this.isShifted = !this.isShifted;
        this.updateNoteMapping ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateControlSurface ()
    {
        this.drumView.setSequencerActive (this.model.canSelectedTrackHoldNotes () && this.drumView.getClip ().doesExist ());

        super.updateControlSurface ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateNoteMapping ()
    {
        if (this.isShifted)
            this.delayedUpdateNoteMapping (EMPTY_TABLE);
        else
            super.updateNoteMapping ();
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        if (this.drumView.isGridEditor ())
        {
            this.drumView.drawGrid ();
            return;
        }

        if (this.drumView.isSequencerVisible ())
        {
            final INoteClip clip = this.drumView.getClip ();
            final boolean isActive = this.model.canSelectedTrackHoldNotes ();
            this.drawSequencerSteps (clip, isActive, this.selectedNote, y -> 3 - y);
            return;
        }

        if (this.isShifted)
        {
            this.drawShiftedGrid ();
            return;
        }

        super.drawGrid ();
    }


    /**
     * Draw the sequencer steps.
     *
     * @param clip The clip
     * @param isActive Is there an active clip?
     * @param noteRow The note for which to draw the row
     * @param yModifier Flips the Y order if true
     */
    protected void drawSequencerSteps (final INoteClip clip, final boolean isActive, final int noteRow, final IntUnaryOperator yModifier)
    {
        final int step = clip.getCurrentStep ();
        final int hiStep = this.isInXRange (step) ? step % PlayView.SEQUENCER_STEPS : -1;
        final int channel = this.configuration.getMidiEditChannel ();
        final NotePosition notePosition = new NotePosition (channel, 0, noteRow);

        final List<NotePosition> editNotes = this.getEditNotes ();

        final IPadGrid padGrid = this.surface.getPadGrid ();
        for (int col = 0; col < PlayView.SEQUENCER_STEPS; col++)
        {
            notePosition.setStep (col);

            final int x = col % this.numColumns;
            int y = col / this.numColumns;
            if (yModifier != null)
                y = yModifier.applyAsInt (y);

            if (noteRow == -1)
            {
                padGrid.lightEx (x, y, AbstractSequencerView.COLOR_NO_CONTENT);
                continue;
            }

            final IStepInfo stepInfo = clip.getStep (notePosition);
            final boolean hilite = col == hiStep;

            padGrid.lightEx (x, y, this.getStepColor (isActive, stepInfo, hilite, notePosition, editNotes));
        }
    }


    /**
     * Checks if the given number is in the current display.
     *
     * @param x The index to check
     * @return True if it should be displayed
     */
    protected boolean isInXRange (final int x)
    {
        final INoteClip clip = this.drumView.getClip ();
        final int stepSize = clip.getNumSteps ();
        final int start = clip.getEditPage () * stepSize;
        return x >= start && x < start + stepSize;
    }


    /**
     * Get the color for a step.
     *
     * @param isActive If the sequencer is active
     * @param stepInfo The information about the step
     * @param hilite The step should be highlighted
     * @param notePosition The position of the note
     * @param editNotes The currently edited notes
     * @return The color
     */
    protected String getStepColor (final boolean isActive, final IStepInfo stepInfo, final boolean hilite, final NotePosition notePosition, final List<NotePosition> editNotes)
    {
        if (!isActive)
            return AbstractSequencerView.COLOR_NO_CONTENT;

        final Optional<ITrack> track = this.model.getCurrentTrackBank ().getSelectedItem ();

        switch (stepInfo.getState ())
        {
            case START:
                if (hilite)
                    return AbstractSequencerView.COLOR_STEP_HILITE_CONTENT;
                if (isEdit (notePosition, editNotes))
                    return AbstractSequencerView.COLOR_STEP_SELECTED;
                if (stepInfo.isMuted ())
                    return AbstractSequencerView.COLOR_STEP_MUTED;
                return track.isPresent () ? DAWColor.getColorID (ColorEx.darker (track.get ().getColor ())) : AbstractSequencerView.COLOR_CONTENT;

            case CONTINUE:
                if (hilite)
                    return AbstractSequencerView.COLOR_STEP_HILITE_CONTENT;
                if (isEdit (notePosition, editNotes))
                    return AbstractSequencerView.COLOR_STEP_SELECTED;
                if (stepInfo.isMuted ())
                    return AbstractSequencerView.COLOR_STEP_MUTED_CONT;
                return track.isPresent () ? DAWColor.getColorID (ColorEx.darker (track.get ().getColor ())) : AbstractSequencerView.COLOR_CONTENT_CONT;

            case OFF:
            default:
                if (hilite)
                    return AbstractSequencerView.COLOR_STEP_HILITE_NO_CONTENT;
                return AbstractSequencerView.COLOR_NO_CONTENT;
        }
    }


    private void drawShiftedGrid ()
    {
        final boolean isKeyboardEnabled = this.model.canSelectedTrackHoldNotes ();
        final IPadGrid padGrid = this.surface.getPadGrid ();

        final Scales scales = this.model.getScales ();

        padGrid.light (36, scales.isChromatic () ? MaschineColorManager.COLOR_MAGENTA : MaschineColorManager.COLOR_MAGENTA_LO);
        for (int i = 37; i < 40; i++)
            padGrid.light (i, MaschineColorManager.COLOR_BLACK);
        for (int i = 40; i < 42; i++)
            padGrid.light (i, isKeyboardEnabled ? MaschineColorManager.COLOR_BLUE : MaschineColorManager.COLOR_BLACK);
        for (int i = 42; i < 44; i++)
            padGrid.light (i, MaschineColorManager.COLOR_BLACK);
        for (int i = 44; i < 46; i++)
            padGrid.light (i, isKeyboardEnabled ? MaschineColorManager.COLOR_GREEN : MaschineColorManager.COLOR_BLACK);
        for (int i = 46; i < 48; i++)
            padGrid.light (i, MaschineColorManager.COLOR_BLACK);
        for (int i = 48; i < 50; i++)
            padGrid.light (i, isKeyboardEnabled ? MaschineColorManager.COLOR_ROSE : MaschineColorManager.COLOR_BLACK);
        for (int i = 50; i < 52; i++)
            padGrid.light (i, isKeyboardEnabled ? MaschineColorManager.COLOR_SKIN : MaschineColorManager.COLOR_BLACK);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int key, final int velocity)
    {
        if (!this.model.canSelectedTrackHoldNotes ())
            return;

        if (this.drumView.isGridEditor ())
        {
            this.drumView.onGridNote (key, velocity);
            return;
        }

        final int index = key - 36;

        if (this.drumView.isSequencerVisible ())
        {
            final int x = index % this.numColumns;
            final int y = index / this.numColumns;

            if (this.model.canSelectedTrackHoldNotes ())
                this.handleSequencerArea (index, x, 3 - y, velocity);
            return;
        }

        if (!this.isShifted)
        {
            super.onGridNote (key, velocity);

            // Mark selected notes immediately for better performance
            final int note = this.keyManager.map (key);
            if (note != -1)
            {
                this.keyManager.setAllKeysPressed (note, velocity);
                this.selectedNote = note;

                if (this.isChordActive)
                {
                    // Get the index of the note in the scale and calculate two thirds (chord is
                    // then scale index: 0, 2, 4)
                    final int [] thirdChord = this.scales.getThirdChord (note);
                    if (thirdChord == null)
                        return;
                    // Send additional chord notes to the DAW
                    final IMidiInput input = this.surface.getMidiInput ();
                    final int channel = this.configuration.getMidiEditChannel ();
                    input.sendRawMidiEvent (MidiConstants.CMD_NOTE_ON + channel, thirdChord[0], velocity);
                    input.sendRawMidiEvent (MidiConstants.CMD_NOTE_ON + channel, thirdChord[1], velocity);
                }
            }

            return;
        }

        if (velocity == 0)
            return;

        final IDisplay display = this.surface.getDisplay ();
        switch (index)
        {
            case 0:
                this.scales.toggleChromatic ();
                this.surface.getDisplay ().notify ("Chromatic: " + (this.scales.isChromatic () ? "On" : "Off"));
                this.configuration.setScaleInKey (!this.scales.isChromatic ());
                break;

            case 4:
                this.scales.prevScaleLayout ();
                display.notify (this.scales.getScaleLayout ().getName ());
                break;

            case 5:
                this.scales.nextScaleLayout ();
                display.notify (this.scales.getScaleLayout ().getName ());
                break;

            case 8:
                this.scales.prevScaleOffset ();
                display.notify (Scales.BASES.get (this.scales.getScaleOffsetIndex ()));
                break;

            case 9:
                this.scales.nextScaleOffset ();
                display.notify (Scales.BASES.get (this.scales.getScaleOffsetIndex ()));
                break;

            case 12:
                this.scales.prevScale ();
                display.notify (this.scales.getScale ().getName ());
                break;

            case 13:
                this.scales.nextScale ();
                display.notify (this.scales.getScale ().getName ());
                break;

            case 14:
                this.onOctaveDown (ButtonEvent.DOWN);
                break;

            case 15:
                this.onOctaveUp (ButtonEvent.DOWN);
                break;

            default:
                // Not used
                break;
        }

        this.update ();
    }


    /**
     * Handle button presses in the sequencer area of the note sequencer.
     *
     * @param index The index of the pad
     * @param x The x position of the pad in the sequencer grid
     * @param y The y position of the pad in the sequencer grid
     * @param velocity The velocity
     */
    protected void handleSequencerArea (final int index, final int x, final int y, final int velocity)
    {
        // Toggle the note on up, so we can intercept the long presses
        if (this.selectedNote == -1 || velocity != 0)
            return;

        final INoteClip clip = this.drumView.getClip ();
        final int channel = this.configuration.getMidiEditChannel ();
        final int step = this.numColumns * (3 - y) + x;
        final int vel = this.configuration.isAccentActive () ? this.configuration.getFixedAccentValue () : this.surface.getButton (ButtonID.get (ButtonID.PAD1, index)).getPressedVelocity ();

        final NotePosition notePosition = new NotePosition (channel, step, this.selectedNote);
        if (this.handleSequencerAreaButtonCombinations (clip, notePosition, vel))
            return;

        clip.toggleStep (notePosition, vel);
    }


    /**
     * Handle button combinations in the sequencer area.
     *
     * @param clip The sequenced MIDI clip
     * @param notePosition The position of the note
     * @param velocity The velocity
     * @return True if handled
     */
    protected boolean handleSequencerAreaButtonCombinations (final INoteClip clip, final NotePosition notePosition, final int velocity)
    {
        if (this.isButtonCombination (ButtonID.MUTE))
        {
            final IStepInfo noteStep = clip.getStep (notePosition);
            if (noteStep.getState () == StepState.START)
                clip.updateStepMuteState (notePosition, !noteStep.isMuted ());
            return true;
        }

        if (this.surface.getModeManager ().isActive (Modes.NOTE))
        {
            this.model.getHost ().showNotification ("Note " + Scales.formatNoteAndOctave (notePosition.getNote (), -3) + " - Step " + Integer.toString (notePosition.getStep () + 1));
            this.editNote (clip, notePosition, true);
            return true;
        }

        // Handle note duplicate function
        final IHwButton duplicateButton = this.surface.getButton (ButtonID.DUPLICATE);
        if (duplicateButton != null && duplicateButton.isPressed ())
        {
            duplicateButton.setConsumed ();
            final IStepInfo noteStep = clip.getStep (notePosition);
            if (noteStep.getState () == StepState.START)
                this.copyNote = noteStep;
            else if (this.copyNote != null)
                clip.setStep (notePosition, this.copyNote);
            return true;
        }

        // Change length of a note or create a new one with a length
        final NotePosition np = new NotePosition (notePosition);
        final int step = np.getStep ();
        final int note = np.getNote ();
        for (int s = step - 1; s >= 0; s--)
        {
            np.setStep (s);

            final int x = s % this.numColumns;
            final int y = s / this.numColumns;
            final int pad = y * this.numColumns + x;
            final IHwButton button = this.surface.getButton (ButtonID.get (ButtonID.PAD1, pad));
            if (button.isLongPressed ())
            {
                button.setConsumed ();
                final int length = step - s + 1;
                final double duration = length * Resolution.getValueAt (this.drumView.getResolutionIndex ());
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


    private void update ()
    {
        this.updateNoteMapping ();
        this.configuration.setScale (this.scales.getScale ().getName ());
        this.configuration.setScaleBase (Scales.BASES.get (this.scales.getScaleOffsetIndex ()));
        this.configuration.setScaleLayout (this.scales.getScaleLayout ().getName ());
    }


    protected List<NotePosition> getEditNotes ()
    {
        final ModeManager modeManager = this.surface.getModeManager ();
        final IMode mode = modeManager.get (Modes.NOTE);
        if (mode instanceof final INoteMode noteMode)
            return noteMode.getNotes ();
        return Collections.emptyList ();
    }


    protected static boolean isEdit (final NotePosition notePosition, final List<NotePosition> editNotes)
    {
        for (final NotePosition editNote: editNotes)
        {
            if (editNote.equals (notePosition))
                return true;
        }
        return false;
    }


    /**
     * Show edit mode and set or add note.
     *
     * @param clip The MIDI clip
     * @param notePosition The position of the note
     * @param addNote Add the note to the edited notes otherwise clear the already selected and add
     *            only the new one
     */
    protected void editNote (final INoteClip clip, final NotePosition notePosition, final boolean addNote)
    {
        final StepState state = clip.getStep (notePosition).getState ();
        if (state != StepState.START)
            return;

        final ModeManager modeManager = this.surface.getModeManager ();
        final IMode mode = modeManager.get (Modes.NOTE);
        if (mode instanceof final INoteMode noteMode)
        {
            if (addNote)
                noteMode.addNote (clip, notePosition);
            else
                noteMode.setNote (clip, notePosition);
            modeManager.setActive (Modes.NOTE);
        }
    }
}