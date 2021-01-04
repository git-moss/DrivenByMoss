// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.maschine.view;

import de.mossgrabers.controller.maschine.MaschineConfiguration;
import de.mossgrabers.controller.maschine.controller.MaschineColorManager;
import de.mossgrabers.controller.maschine.controller.MaschineControlSurface;
import de.mossgrabers.controller.maschine.mode.EditNoteMode;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.display.IDisplay;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.controller.hardware.IHwButton;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.daw.IStepInfo;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractPlayView;
import de.mossgrabers.framework.view.AbstractSequencerView;

import java.util.function.IntUnaryOperator;


/**
 * The Play view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PlayView extends AbstractPlayView<MaschineControlSurface, MaschineConfiguration>
{
    private final DrumView              drumView;
    private final MaschineConfiguration configuration;
    private final int                   numColumns;
    private boolean                     isShifted      = false;
    private int                         sequencerSteps = 16;
    private int                         selectedNote   = -1;
    protected IStepInfo                 copyNote;
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
        final int hiStep = this.isInXRange (step) ? step % this.sequencerSteps : -1;
        final int editMidiChannel = this.configuration.getMidiEditChannel ();
        final IPadGrid padGrid = this.surface.getPadGrid ();
        for (int col = 0; col < this.sequencerSteps; col++)
        {
            final int x = col % this.numColumns;
            int y = col / this.numColumns;
            if (yModifier != null)
                y = yModifier.applyAsInt (y);

            if (noteRow == -1)
            {
                padGrid.lightEx (x, y, AbstractSequencerView.COLOR_NO_CONTENT);
                continue;
            }

            final int isSet = clip.getStep (editMidiChannel, col, noteRow).getState ();
            final boolean hilite = col == hiStep;
            padGrid.lightEx (x, y, isActive ? this.getStepColor (isSet, hilite, noteRow) : AbstractSequencerView.COLOR_NO_CONTENT);
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
     * @param isSet The step has content
     * @param hilite The step should be highlighted
     * @param note The note of the step
     * @return The color
     */
    protected String getStepColor (final int isSet, final boolean hilite, final int note)
    {
        switch (isSet)
        {
            case IStepInfo.NOTE_CONTINUE:
                return hilite ? AbstractSequencerView.COLOR_STEP_HILITE_CONTENT : AbstractSequencerView.COLOR_CONTENT_CONT;

            case IStepInfo.NOTE_START:
                return hilite ? AbstractSequencerView.COLOR_STEP_HILITE_CONTENT : AbstractSequencerView.COLOR_CONTENT;

            case IStepInfo.NOTE_OFF:
            default:
                if (hilite)
                    return AbstractSequencerView.COLOR_STEP_HILITE_NO_CONTENT;
                return this.getPadColor (note, this.model.getCursorTrack ());
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
                    input.sendRawMidiEvent (0x90 + channel, thirdChord[0], velocity);
                    input.sendRawMidiEvent (0x90 + channel, thirdChord[1], velocity);
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
                this.surface.setStopConsumed ();
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
                display.notify (Scales.BASES[this.scales.getScaleOffset ()]);
                break;

            case 9:
                this.scales.nextScaleOffset ();
                display.notify (Scales.BASES[this.scales.getScaleOffset ()]);
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
        if (this.selectedNote == -1)
            return;

        // Toggle the note on up, so we can intercept the long presses
        if (velocity != 0)
            return;

        final INoteClip clip = this.drumView.getClip ();
        final int channel = this.configuration.getMidiEditChannel ();
        final int step = this.numColumns * (3 - y) + x;
        final int vel = this.configuration.isAccentActive () ? this.configuration.getFixedAccentValue () : this.surface.getButton (ButtonID.get (ButtonID.PAD1, index)).getPressedVelocity ();

        if (this.handleSequencerAreaButtonCombinations (clip, channel, step, this.selectedNote, vel))
            return;

        clip.toggleStep (channel, step, this.selectedNote, vel);
    }


    /**
     * Handle button combinations in the sequencer area.
     *
     * @param clip The sequenced midi clip
     * @param channel The MIDI channel of the note
     * @param step The step in the current page in the clip
     * @param note The note in the current page of the pad in the clip
     * @param velocity The velocity
     * @return True if handled
     */
    protected boolean handleSequencerAreaButtonCombinations (final INoteClip clip, final int channel, final int step, final int note, final int velocity)
    {
        final ModeManager modeManager = this.surface.getModeManager ();
        if (modeManager.isActive (Modes.NOTE))
        {
            final int isSet = clip.getStep (channel, step, note).getState ();
            this.model.getHost ().showNotification ("Note " + Scales.formatNoteAndOctave (note, -3) + " - Step " + Integer.toString (step + 1));
            ((EditNoteMode) modeManager.get (Modes.NOTE)).setValues (isSet == IStepInfo.NOTE_START ? clip : null, channel, step, note);
            return true;
        }

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
            final int x = s % this.numColumns;
            final int y = 3 - s / this.numColumns;
            final int pad = y * this.numColumns + x;
            final IHwButton button = this.surface.getButton (ButtonID.get (ButtonID.PAD1, pad));
            if (button.isLongPressed ())
            {
                button.setConsumed ();
                final int length = step - s + 1;
                final double duration = length * Resolution.getValueAt (this.drumView.getResolutionIndex ());
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


    private void update ()
    {
        this.updateNoteMapping ();
        this.configuration.setScale (this.scales.getScale ().getName ());
        this.configuration.setScaleBase (Scales.BASES[this.scales.getScaleOffset ()]);
        this.configuration.setScaleLayout (this.scales.getScaleLayout ().getName ());
    }
}