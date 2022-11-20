// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.apc.view;

import de.mossgrabers.controller.akai.apc.APCConfiguration;
import de.mossgrabers.controller.akai.apc.controller.APCControlSurface;
import de.mossgrabers.controller.akai.apc.mode.NoteMode;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.daw.clip.NotePosition;
import de.mossgrabers.framework.daw.clip.StepState;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.midi.MidiConstants;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.sequencer.AbstractDrumExView;
import de.mossgrabers.framework.view.sequencer.AbstractDrumView;


/**
 * The drum sequencer.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DrumView extends AbstractDrumExView<APCControlSurface, APCConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public DrumView (final APCControlSurface surface, final IModel model)
    {
        super ("Drum", surface, model, 2, 3, surface.isMkII ());

        this.useExtraToggleButton = false;

        this.buttonSelect = ButtonID.PAD13;
        this.buttonMute = ButtonID.PAD14;
        this.buttonSolo = ButtonID.PAD15;
        this.buttonBrowse = ButtonID.PAD16;
    }


    /** {@inheritDoc} */
    @Override
    protected void handleSequencerArea (final int index, final int x, final int y, final int offsetY, final int velocity)
    {
        if (!this.isActive ())
            return;

        final ModeManager modeManager = this.surface.getModeManager ();

        if (velocity > 0)
        {
            // Turn on Note mode if an existing note is pressed
            final INoteClip cursorClip = this.getClip ();
            final int step = this.numColumns * (this.allRows - 1 - y) + x;
            final NotePosition notePosition = new NotePosition (this.configuration.getMidiEditChannel (), step, offsetY + this.selectedPad);
            final StepState state = cursorClip.getStep (notePosition).getState ();
            if (state == StepState.START)
            {
                final NoteMode noteMode = (NoteMode) modeManager.get (Modes.NOTE);
                noteMode.setValues (cursorClip, notePosition);
                modeManager.setActive (Modes.NOTE);
            }
        }
        else
        {
            // Turn off Note mode
            if (modeManager.isActive (Modes.NOTE))
                modeManager.restore ();

            if (this.isNoteEdited)
            {
                this.isNoteEdited = false;
                return;
            }
        }

        super.handleSequencerArea (index, x, y, offsetY, velocity);
    }


    /** {@inheritDoc} */
    @Override
    protected String getPadContentColor (final IChannel drumPad)
    {
        return this.surface.isMkII () ? DAWColor.getColorID (drumPad.getColor ()) : AbstractDrumView.COLOR_PAD_HAS_CONTENT;
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        if (!ButtonID.isSceneButton (buttonID) || event != ButtonEvent.DOWN || !this.isActive ())
            return;

        switch (buttonID)
        {
            case SCENE1:
                this.changeOctave (event, true, 4);
                break;
            case SCENE2:
                this.changeOctave (event, false, 4);
                break;
            case SCENE3:
                this.toggleExtraButtons ();
                break;
            case SCENE4:
                this.onOctaveUp (event);
                break;
            case SCENE5:
                this.onOctaveDown (event);
                break;
            default:
                // Intentionally empty
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public String getButtonColorID (final ButtonID buttonID)
    {
        if (buttonID == ButtonID.SCENE3)
            return this.extraButtonsOn ? ColorManager.BUTTON_STATE_HI : ColorManager.BUTTON_STATE_OFF;
        return this.isActive () ? ColorManager.BUTTON_STATE_ON : ColorManager.BUTTON_STATE_OFF;
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


    /**
     * Handle the stop buttons.
     *
     * @param index The index of the button (0-7)
     */
    public void handleStopButtons (final int index)
    {
        if (this.noteRepeatPeriodOn)
        {
            this.configuration.setNoteRepeatPeriod (Resolution.values ()[index]);
            this.mvHelper.delayDisplay ( () -> "Period: " + Resolution.getNameAt (index));
            return;
        }

        if (this.noteRepeatLengthOn)
        {
            this.configuration.setNoteRepeatLength (Resolution.values ()[index]);
            this.mvHelper.delayDisplay ( () -> "Note Length: " + Resolution.getNameAt (index));
            return;
        }

        this.setResolutionIndex (index);
    }


    /**
     * Get the color for the stop buttons.
     *
     * @param index THe index of the button
     * @return The color index
     */
    public int getStopButtonColor (final int index)
    {
        if (this.noteRepeatPeriodOn)
            return this.configuration.getNoteRepeatPeriod ().ordinal () == index ? 1 : 0;

        if (this.noteRepeatLengthOn)
            return this.configuration.getNoteRepeatLength ().ordinal () == index ? 1 : 0;

        return this.getResolutionIndex () == index ? 1 : 0;
    }


    /** {@inheritDoc} */
    @Override
    protected void playNote (final int drumPad, final int velocity)
    {
        if (!this.surface.isMkII ())
            this.surface.sendMidiEvent (MidiConstants.CMD_NOTE_ON, drumPad, velocity);
    }
}