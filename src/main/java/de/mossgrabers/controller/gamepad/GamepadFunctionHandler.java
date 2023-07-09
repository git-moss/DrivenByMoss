// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.gamepad;

import de.mossgrabers.controller.gamepad.controller.GamepadControlSurface;
import de.mossgrabers.controller.gamepad.controller.IGamepadCallback;
import de.mossgrabers.framework.command.trigger.clip.NewCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.INoteRepeat;
import de.mossgrabers.framework.daw.midi.MidiConstants;
import de.mossgrabers.framework.utils.ButtonEvent;

import com.studiohartman.jamepad.ControllerAxis;
import com.studiohartman.jamepad.ControllerButton;

import java.util.Optional;


/**
 * Interface for a callback when data from a Gamepad controller is received.
 *
 * @author Jürgen Moßgraber
 */
public class GamepadFunctionHandler implements IGamepadCallback
{
    private final GamepadControlSurface                                   surface;
    private final IModel                                                  model;
    private final GamepadConfiguration                                    configuration;
    private final IMidiInput                                              input;
    private final INoteRepeat                                             noteRepeat;

    private final NewCommand<GamepadControlSurface, GamepadConfiguration> newCommand;

    private final boolean []                                              playingNotes = new boolean [128];


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public GamepadFunctionHandler (final GamepadControlSurface surface, final IModel model)
    {
        this.surface = surface;
        this.model = model;
        this.configuration = this.surface.getConfiguration ();
        this.input = this.surface.getMidiInput ();
        this.noteRepeat = this.input.getDefaultNoteInput ().getNoteRepeat ();

        this.newCommand = new NewCommand<> (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void process (final ControllerButton button, final ButtonEvent event)
    {
        final int function = this.configuration.getFunction (button);
        if (function <= GamepadConfiguration.FUNCTION_OFF)
            return;

        if (function <= GamepadConfiguration.FUNCTION_NOTE_127)
        {
            this.handleMidiNote (function, event == ButtonEvent.DOWN ? 1 : 0);
            return;
        }

        if (function <= GamepadConfiguration.FUNCTION_CC_127)
        {
            final double v = event == ButtonEvent.DOWN ? 1 : 0;
            this.handleMidiCC (function, GamepadConfiguration.FUNCTION_RANGE_127, v, v);
            return;
        }

        switch (function)
        {
            case GamepadConfiguration.FUNCTION_PITCHBEND:
                this.handleMidiPitchbend (event == ButtonEvent.DOWN ? 1 : 0);
                break;

            case GamepadConfiguration.FUNCTION_NOTE_REPEAT_ENABLE:
                this.noteRepeat.setActive (event == ButtonEvent.DOWN);
                break;
            case GamepadConfiguration.FUNCTION_NOTE_REPEAT_PERIOD:
                // Not supported
                break;
            case GamepadConfiguration.FUNCTION_NOTE_REPEAT_LENGTH:
                // Not supported
                break;

            case GamepadConfiguration.FUNCTION_TRACK_PREVIOUS:
                if (event == ButtonEvent.DOWN)
                    this.model.getCursorTrack ().selectPrevious ();
                break;
            case GamepadConfiguration.FUNCTION_TRACK_NEXT:
                if (event == ButtonEvent.DOWN)
                    this.model.getCursorTrack ().selectNext ();
                break;
            case GamepadConfiguration.FUNCTION_CLIP_PREVIOUS:
                if (event == ButtonEvent.DOWN)
                    this.model.getCursorTrack ().getSlotBank ().selectPreviousItem ();
                break;
            case GamepadConfiguration.FUNCTION_CLIP_NEXT:
                if (event == ButtonEvent.DOWN)
                    this.model.getCursorTrack ().getSlotBank ().selectNextItem ();
                break;
            case GamepadConfiguration.FUNCTION_PLAY_CLIP:
                final Optional<ISlot> selectedItem = this.model.getCursorTrack ().getSlotBank ().getSelectedItem ();
                if (selectedItem.isPresent ())
                    selectedItem.get ().launch (event == ButtonEvent.DOWN, false);
                break;

            case GamepadConfiguration.FUNCTION_NEW_CLIP:
                if (event == ButtonEvent.DOWN)
                    this.newCommand.execute ();
                break;

            case GamepadConfiguration.FUNCTION_TRANSPORT_PLAY:
                if (event == ButtonEvent.DOWN)
                    this.model.getTransport ().play ();
                break;
            case GamepadConfiguration.FUNCTION_TRANSPORT_METRONOME:
                if (event == ButtonEvent.DOWN)
                    this.model.getTransport ().toggleMetronome ();
                break;

            default:
                // No more
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void process (final ControllerAxis axis, final float value)
    {
        final int function = this.configuration.getFunction (axis);
        if (function <= GamepadConfiguration.FUNCTION_OFF)
            return;

        final double positive = Math.abs (value);

        if (function <= GamepadConfiguration.FUNCTION_NOTE_127)
        {
            this.handleMidiNote (function, positive);
            return;
        }

        if (function <= GamepadConfiguration.FUNCTION_CC_127)
        {
            final int functionRange = this.configuration.getFunctionRange (axis);
            this.handleMidiCC (function, functionRange, value, positive);
            return;
        }

        switch (function)
        {
            case GamepadConfiguration.FUNCTION_PITCHBEND:
                this.handleMidiPitchbend (value);
                break;

            case GamepadConfiguration.FUNCTION_NOTE_REPEAT_ENABLE:
                this.noteRepeat.setActive (positive > 0.5);
                break;
            case GamepadConfiguration.FUNCTION_NOTE_REPEAT_PERIOD:
                final Resolution [] values = Resolution.values ();
                final int resolutionIndex = (int) Math.round (positive * (values.length - 1));
                this.noteRepeat.setPeriod (values[resolutionIndex].getValue ());
                break;
            case GamepadConfiguration.FUNCTION_NOTE_REPEAT_LENGTH:
                final Resolution [] values2 = Resolution.values ();
                final int resolutionIndex2 = (int) Math.round (positive * (values2.length - 1));
                this.noteRepeat.setNoteLength (values2[values2.length - 1 - resolutionIndex2].getValue ());
                break;

            case GamepadConfiguration.FUNCTION_PLAY_CLIP, GamepadConfiguration.FUNCTION_NEW_CLIP, GamepadConfiguration.FUNCTION_TRANSPORT_PLAY, GamepadConfiguration.FUNCTION_TRANSPORT_METRONOME:
                // Not supported
                break;

            default:
                // No more
                break;
        }
    }


    private void handleMidiNote (final int index, final double value)
    {
        final int note = index - GamepadConfiguration.FUNCTION_NOTE_0;

        // Prevent re-trigger with continuous control
        final boolean isOn = value > 0.9;
        if (isOn && this.playingNotes[note])
            return;
        final boolean isOff = value < 0.09;
        if (isOff && !this.playingNotes[note])
            return;

        this.playingNotes[note] = isOn;
        this.input.sendRawMidiEvent (isOn ? MidiConstants.CMD_NOTE_ON : MidiConstants.CMD_NOTE_OFF, note, isOn ? 127 : 0);
    }


    private void handleMidiCC (final int function, final int functionRange, final double value, final double positive)
    {
        final int midiValue;

        switch (functionRange)
        {
            case GamepadConfiguration.FUNCTION_RANGE_127:
                midiValue = positive < 0.09 ? 0 : (int) Math.min (127, Math.max (0, Math.round (positive * 127)));
                break;
            case GamepadConfiguration.FUNCTION_RANGE_CENTER_64:
                if (positive < 0.09)
                    midiValue = 64;
                else
                {
                    final int v = (int) Math.min (63, Math.max (0, Math.round (positive * 63)));
                    midiValue = value < 0 ? 63 - v : 64 + v;
                }
                break;
            case GamepadConfiguration.FUNCTION_RANGE_CENTER_64_FLIP:
                if (positive < 0.09)
                    midiValue = 64;
                else
                {
                    final int v = (int) Math.min (63, Math.max (0, Math.round (positive * 63)));
                    midiValue = value < 0 ? 64 + v : 63 - v;
                }
                break;
            default:
                // No more
                return;
        }

        this.model.getHost ().println ("" + midiValue);

        this.input.sendRawMidiEvent (MidiConstants.CMD_CC, function - GamepadConfiguration.FUNCTION_CC_0, midiValue);
    }


    /**
     * Send a MIDI pitchbend message.
     *
     * @param value Value is in the range of [-1,1]
     */
    private void handleMidiPitchbend (final float value)
    {
        final int midiValue = Math.abs (value) < 0.09 ? 8128 : (int) Math.max (Math.min (16383, Math.round ((1 + value) / 2.0 * 16383)), 0);
        final int msb = midiValue >> 7;
        final int lsb = midiValue & 0x7F;
        this.input.sendRawMidiEvent (MidiConstants.CMD_PITCHBEND, lsb, msb);
    }
}
