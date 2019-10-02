// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command;

/**
 * IDs for trigger commands.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public enum TriggerCommandID
{
    /** The play command. */
    PLAY,
    /** The command for stop. */
    STOP,
    /** The Stop Clip command. */
    STOP_CLIP,
    /** The command for stopping all clips. */
    STOP_ALL_CLIPS,
    /** The record command. */
    RECORD,
    /** The rewind command. */
    REWIND,
    /** The forward command. */
    FORWARD,
    /** Button loop toggle command. */
    LOOP,
    /** Button to toggle the repeat command. */
    REPEAT,
    /** Button punch in command. */
    PUNCH_IN,
    /** Button punch out command. */
    PUNCH_OUT,
    /** Toggle overdub. */
    OVERDUB,
    /** The command for Nudge positive. */
    NUDGE_PLUS,
    /** The command for Nudge negative. */
    NUDGE_MINUS,
    /** The Tap Tempo button command. */
    TAP_TEMPO,
    /** The Metronome button command. */
    METRONOME,
    /** Toggle scrubbing. */
    SCRUB,

    /** The automation command. */
    AUTOMATION,
    /** The new command. */
    NEW,
    /** The fixed length command. */
    FIXED_LENGTH,
    /** The duplicate command. */
    DUPLICATE,
    /** The Delete button command. */
    DELETE,
    /** The Double button command. */
    DOUBLE,
    /** The Quantize button command. */
    QUANTIZE,
    /** The Audio conversion command. */
    CONVERT,

    /** The Undo button command. */
    UNDO,
    /** Button to redo a command. */
    REDO,
    /** The command to save the current project. */
    SAVE,

    /** The command for cursor arrow down. */
    ARROW_DOWN,
    /** The command for cursor arrow up. */
    ARROW_UP,
    /** The command for cursor arrow left. */
    ARROW_LEFT,
    /** The command for cursor arrow right. */
    ARROW_RIGHT,
    /** The Page left command. */
    PAGE_LEFT,
    /** The Page right command. */
    PAGE_RIGHT,

    /** The Device button command. */
    DEVICE,
    /** The Track button command. */
    TRACK,
    /** The Mastertrack command. */
    MASTERTRACK,
    /** The Volume button command. */
    VOLUME,
    /** The Pan and Send button command. */
    PAN_SEND,
    /** The command for sends. */
    SENDS,
    /** The Clip button command. */
    CLIP,
    /** The Browse button command. */
    BROWSE,
    /** The command to toggle markers. */
    MARKER,
    /** The command to toggle VU meters. */
    TOGGLE_VU,
    /** The zoom state command. */
    ZOOM,

    /** The Shift button command. */
    SHIFT,
    /** The Select button command. */
    SELECT,
    /** The Control button command. */
    CONTROL,
    /** The Alternate button command. */
    ALT,
    /** The Enter (confirm) button command. */
    ENTER,
    /** The Cancel button command. */
    CANCEL,

    /** The edit Scales command. */
    SCALES,
    /** The accent command. */
    ACCENT,
    /** The Layout button command. */
    LAYOUT,

    /** The command for User. */
    USER,
    /** The Setup command. */
    SETUP,

    /** The Mute command. */
    MUTE,
    /** The Solo command. */
    SOLO,
    /** The command for arming record. */
    REC_ARM,

    /** The add effect command. */
    ADD_EFFECT,
    /** The add track command. */
    ADD_TRACK,

    /** The select play view command. */
    SELECT_PLAY_VIEW,
    /** The select session view command. */
    SELECT_SESSION_VIEW,

    /** The command to execute scene 1. */
    SCENE1,
    /** The command to execute scene 2. */
    SCENE2,
    /** The command to execute scene 3. */
    SCENE3,
    /** The command to execute scene 4. */
    SCENE4,
    /** The command to execute scene 5. */
    SCENE5,
    /** The command to execute scene 6. */
    SCENE6,
    /** The command to execute scene 7. */
    SCENE7,
    /** The command to execute scene 8. */
    SCENE8,

    /** The command for octave down. */
    OCTAVE_DOWN,
    /** The command for octave up. */
    OCTAVE_UP,

    /** The command for Device on/off. */
    DEVICE_ON_OFF,
    /** The command to select the previous device. */
    DEVICE_LEFT,
    /** The command to select the next device. */
    DEVICE_RIGHT,
    /** The command to select the previous parameter page. */
    BANK_LEFT,
    /** The command to select the next parameter page. */
    BANK_RIGHT,

    /** The command to switch to the Arrange view. */
    LAYOUT_ARRANGE,
    /** The command to switch to the Mix view. */
    LAYOUT_MIX,
    /** The command to switch to the Edit view. */
    LAYOUT_EDIT,

    /** Toggle the display content. */
    TOGGLE_DISPLAY,
    /** Toggle displaying play cursor ticks. */
    TEMPO_TICKS,

    /** The command to toggle the devices pane. */
    TOGGLE_DEVICES_PANE,
    /** The command to toggle the mixer pane. */
    MIXER,
    /** The command to toggle the note editor pane. */
    NOTE_EDITOR,
    /** The command to toggle the automation editor pane. */
    AUTOMATION_EDITOR,
    /** The command to toggle the device. */
    TOGGLE_DEVICE,
    /** Toggle the groove parameters. */
    GROOVE,
    /** The Flip channels command. */
    FLIP,

    /** Button 1 of row 1 command. */
    ROW1_1,
    /** Button 2 of row 1 command. */
    ROW1_2,
    /** Button 3 of row 1 command. */
    ROW1_3,
    /** Button 4 of row 1 command. */
    ROW1_4,
    /** Button 5 of row 1 command. */
    ROW1_5,
    /** Button 6 of row 1 command. */
    ROW1_6,
    /** Button 7 of row 1 command. */
    ROW1_7,
    /** Button 8 of row 1 command. */
    ROW1_8,

    /** Button 1 of row 2 command. */
    ROW2_1,
    /** Button 2 of row 2 command. */
    ROW2_2,
    /** Button 3 of row 2 command. */
    ROW2_3,
    /** Button 4 of row 2 command. */
    ROW2_4,
    /** Button 5 of row 2 command. */
    ROW2_5,
    /** Button 6 of row 2 command. */
    ROW2_6,
    /** Button 7 of row 2 command. */
    ROW2_7,
    /** Button 8 of row 2 command. */
    ROW2_8,

    /** Button 1 of row 3 command. */
    ROW3_1,
    /** Button 2 of row 3 command. */
    ROW3_2,
    /** Button 3 of row 3 command. */
    ROW3_3,
    /** Button 4 of row 3 command. */
    ROW3_4,
    /** Button 5 of row 3 command. */
    ROW3_5,
    /** Button 6 of row 3 command. */
    ROW3_6,
    /** Button 7 of row 3 command. */
    ROW3_7,
    /** Button 8 of row 3 command. */
    ROW3_8,

    /** Button 1 of row 4 command. */
    ROW4_1,
    /** Button 2 of row 4 command. */
    ROW4_2,
    /** Button 3 of row 4 command. */
    ROW4_3,
    /** Button 4 of row 4 command. */
    ROW4_4,
    /** Button 5 of row 4 command. */
    ROW4_5,
    /** Button 6 of row 4 command. */
    ROW4_6,
    /** Button 7 of row 4 command. */
    ROW4_7,
    /** Button 8 of row 4 command. */
    ROW4_8,

    /** Button 1 of row 5 command. */
    ROW5_1,
    /** Button 2 of row 5 command. */
    ROW5_2,
    /** Button 3 of row 5 command. */
    ROW5_3,
    /** Button 4 of row 5 command. */
    ROW5_4,
    /** Button 5 of row 5 command. */
    ROW5_5,
    /** Button 6 of row 5 command. */
    ROW5_6,
    /** Button 7 of row 5 command. */
    ROW5_7,
    /** Button 8 of row 5 command. */
    ROW5_8,

    /** Button 1 of row 6 command. */
    ROW6_1,
    /** Button 2 of row 6 command. */
    ROW6_2,
    /** Button 3 of row 6 command. */
    ROW6_3,
    /** Button 4 of row 6 command. */
    ROW6_4,
    /** Button 5 of row 6 command. */
    ROW6_5,
    /** Button 6 of row 6 command. */
    ROW6_6,
    /** Button 7 of row 6 command. */
    ROW6_7,
    /** Button 8 of row 6 command. */
    ROW6_8,

    /** Button select row 1 command. */
    ROW_SELECT_1,
    /** Button select row 2 command. */
    ROW_SELECT_2,
    /** Button select row 3 command. */
    ROW_SELECT_3,
    /** Button select row 4 command. */
    ROW_SELECT_4,
    /** Button select row 5 command. */
    ROW_SELECT_5,
    /** Button select row 6 command. */
    ROW_SELECT_6,
    /** Button select row 7 command. */
    ROW_SELECT_7,
    /** Button select row 8 command. */
    ROW_SELECT_8,

    /** The automation read command. */
    AUTOMATION_READ,
    /** The automation write command. */
    AUTOMATION_WRITE,
    /** The automation trim command. */
    AUTOMATION_TRIM,
    /** The automation touch command. */
    AUTOMATION_TOUCH,
    /** The automation latch command. */
    AUTOMATION_LATCH,

    /** The move bank left command. */
    MOVE_BANK_LEFT,
    /** The move bank left command. */
    MOVE_BANK_RIGHT,
    /** The move bank left command. */
    MOVE_TRACK_LEFT,
    /** The move bank left command. */
    MOVE_TRACK_RIGHT,

    /** The command to touch a fader. */
    FADER_TOUCH_1,
    /** The command to touch a fader. */
    FADER_TOUCH_2,
    /** The command to touch a fader. */
    FADER_TOUCH_3,
    /** The command to touch a fader. */
    FADER_TOUCH_4,
    /** The command to touch a fader. */
    FADER_TOUCH_5,
    /** The command to touch a fader. */
    FADER_TOUCH_6,
    /** The command to touch a fader. */
    FADER_TOUCH_7,
    /** The command to touch a fader. */
    FADER_TOUCH_8,

    /** Knob 1 touched. */
    KNOB1_TOUCH,
    /** Knob 2 touched. */
    KNOB2_TOUCH,
    /** Knob 3 touched. */
    KNOB3_TOUCH,
    /** Knob 4 touched. */
    KNOB4_TOUCH,
    /** Knob 5 touched. */
    KNOB5_TOUCH,
    /** Knob 6 touched. */
    KNOB6_TOUCH,
    /** Knob 7 touched. */
    KNOB7_TOUCH,
    /** Knob 8 touched. */
    KNOB8_TOUCH,
    /** Mastertrack touched. */
    MASTERTRACK_TOUCH,

    /** Tempo button touched. */
    TEMPO_TOUCH,
    /** Playcursor button touched. */
    PLAYCURSOR_TOUCH,

    /** Configure pitchbend touched. */
    CONFIGURE_PITCHBEND,

    /** The command to select Send 1. */
    SEND1,
    /** The command to select Send 2. */
    SEND2,
    /** The command to select Send 3. */
    SEND3,
    /** The command to select Send 4. */
    SEND4,
    /** The command to select Send 5. */
    SEND5,
    /** The command to select Send 6. */
    SEND6,
    /** The command to select Send 7. */
    SEND7,
    /** The command to select Send 8. */
    SEND8,

    /** The first footswitch. */
    FOOTSWITCH1,
    /** The second footswitch. */
    FOOTSWITCH2,

    /** Function key 1. */
    F1,
    /** Function key 2. */
    F2,
    /** Function key 3. */
    F3,
    /** Function key 4. */
    F4,
    /** Function key 5. */
    F5,
    /** Function key 6. */
    F6,
    /** Function key 7. */
    F7,
    /** Function key 8. */
    F8,

    /** The command for a LED. */
    LED_1,
    /** The command for a LED. */
    LED_2;


    /**
     * Get an offset command, e.g. to get F4 set F1 and 3 as parameters.
     *
     * @param command The base command
     * @param offset The offset
     * @return The offset command
     */
    public static TriggerCommandID get (final TriggerCommandID command, final int offset)
    {
        return TriggerCommandID.values ()[command.ordinal () + offset];
    }
}
