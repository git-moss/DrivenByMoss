// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller;

/**
 * IDs for common buttons.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public enum ButtonID
{
    /** The play button. */
    PLAY,
    /** The button for stop. */
    STOP,
    /** The Stop Clip button. */
    STOP_CLIP,
    /** The button for stopping all clips. */
    STOP_ALL_CLIPS,
    /** The record button. */
    RECORD,
    /** The rewind button. */
    REWIND,
    /** The forward button. */
    FORWARD,
    /** Button loop toggle button. */
    LOOP,
    /** Button to toggle the repeat button. */
    REPEAT,
    /** Button punch in button. */
    PUNCH_IN,
    /** Button punch out button. */
    PUNCH_OUT,
    /** Toggle overdub. */
    OVERDUB,
    /** The button for Nudge positive. */
    NUDGE_PLUS,
    /** The button for Nudge negative. */
    NUDGE_MINUS,
    /** The Tap Tempo button button. */
    TAP_TEMPO,
    /** The Metronome button button. */
    METRONOME,
    /** Toggle scrubbing. */
    SCRUB,
    /** Move cursor to start of arranger. */
    RETURN_TO_ZERO,

    /** The automation button. */
    AUTOMATION,
    /** The new button. */
    NEW,
    /** The fixed length button. */
    FIXED_LENGTH,
    /** The duplicate button. */
    DUPLICATE,
    /** The Delete button button. */
    DELETE,
    /** The Double button button. */
    DOUBLE,
    /** The Quantize button button. */
    QUANTIZE,
    /** The Audio conversion button. */
    CONVERT,

    /** The Undo button button. */
    UNDO,
    /** Button to redo a button. */
    REDO,
    /** The button to save the current project. */
    SAVE,

    /** The button for cursor arrow down. */
    ARROW_DOWN,
    /** The button for cursor arrow up. */
    ARROW_UP,
    /** The button for cursor arrow left. */
    ARROW_LEFT,
    /** The button for cursor arrow right. */
    ARROW_RIGHT,
    /** The Page left button. */
    PAGE_LEFT,
    /** The Page right button. */
    PAGE_RIGHT,

    /** The Device button button. */
    DEVICE,
    /** The Track button button. */
    TRACK,
    /** The Mastertrack button. */
    MASTERTRACK,
    /** The Volume button button. */
    VOLUME,
    /** The Pan and Send button button. */
    PAN_SEND,
    /** The button for sends. */
    SENDS,
    /** The Clip button button. */
    CLIP,
    /** The Browse button button. */
    BROWSE,
    /** The button to toggle markers. */
    MARKER,
    /** The button to toggle VU meters. */
    TOGGLE_VU,
    /** The zoom state button. */
    ZOOM,

    /** The Shift button button. */
    SHIFT,
    /** The Select button button. */
    SELECT,
    /** The Control button button. */
    CONTROL,
    /** The Alternate button button. */
    ALT,
    /** The Enter (confirm) button button. */
    ENTER,
    /** The Cancel button button. */
    CANCEL,

    /** The edit Scales button. */
    SCALES,
    /** The accent button. */
    ACCENT,
    /** The Layout button button. */
    LAYOUT,

    /** The button for User. */
    USER,
    /** The Setup button. */
    SETUP,
    /** The Project button. */
    PROJECT,

    /** The Mute button. */
    MUTE,
    /** The Solo button. */
    SOLO,
    /** The button for arming record. */
    REC_ARM,

    /** The add effect button. */
    ADD_EFFECT,
    /** The add track button. */
    ADD_TRACK,

    /** The select note view button. */
    NOTE,
    /** The select session view button. */
    SESSION,
    /** The select sequencer view button. */
    SEQUENCER,
    /** The select drum sequencer view button. */
    DRUM,

    /** The button to execute scene 1. */
    SCENE1,
    /** The button to execute scene 2. */
    SCENE2,
    /** The button to execute scene 3. */
    SCENE3,
    /** The button to execute scene 4. */
    SCENE4,
    /** The button to execute scene 5. */
    SCENE5,
    /** The button to execute scene 6. */
    SCENE6,
    /** The button to execute scene 7. */
    SCENE7,
    /** The button to execute scene 8. */
    SCENE8,

    /** The button for octave down. */
    OCTAVE_DOWN,
    /** The button for octave up. */
    OCTAVE_UP,

    /** The button for Device on/off. */
    DEVICE_ON_OFF,
    /** The button to select the previous device. */
    DEVICE_LEFT,
    /** The button to select the next device. */
    DEVICE_RIGHT,
    /** The button to select the previous parameter page. */
    BANK_LEFT,
    /** The button to select the next parameter page. */
    BANK_RIGHT,

    /** The button to switch to the Arrange view. */
    LAYOUT_ARRANGE,
    /** The button to switch to the Mix view. */
    LAYOUT_MIX,
    /** The button to switch to the Edit view. */
    LAYOUT_EDIT,

    /** Toggle the display content. */
    TOGGLE_DISPLAY,
    /** Toggle displaying play cursor ticks. */
    TEMPO_TICKS,

    /** The button to toggle the devices pane. */
    TOGGLE_DEVICES_PANE,
    /** The button to toggle the mixer pane. */
    MIXER,
    /** The button to toggle the note editor pane. */
    NOTE_EDITOR,
    /** The button to toggle the automation editor pane. */
    AUTOMATION_EDITOR,
    /** The button to toggle the device. */
    TOGGLE_DEVICE,
    /** Toggle the groove parameters. */
    GROOVE,
    /** The Flip channels button. */
    FLIP,

    /** Toggle the audio engine on/off. */
    AUDIO_ENGINE,

    /** Button 1 of row 1 button. */
    ROW1_1,
    /** Button 2 of row 1 button. */
    ROW1_2,
    /** Button 3 of row 1 button. */
    ROW1_3,
    /** Button 4 of row 1 button. */
    ROW1_4,
    /** Button 5 of row 1 button. */
    ROW1_5,
    /** Button 6 of row 1 button. */
    ROW1_6,
    /** Button 7 of row 1 button. */
    ROW1_7,
    /** Button 8 of row 1 button. */
    ROW1_8,

    /** Button 1 of row 2 button. */
    ROW2_1,
    /** Button 2 of row 2 button. */
    ROW2_2,
    /** Button 3 of row 2 button. */
    ROW2_3,
    /** Button 4 of row 2 button. */
    ROW2_4,
    /** Button 5 of row 2 button. */
    ROW2_5,
    /** Button 6 of row 2 button. */
    ROW2_6,
    /** Button 7 of row 2 button. */
    ROW2_7,
    /** Button 8 of row 2 button. */
    ROW2_8,

    /** Button 1 of row 3 button. */
    ROW3_1,
    /** Button 2 of row 3 button. */
    ROW3_2,
    /** Button 3 of row 3 button. */
    ROW3_3,
    /** Button 4 of row 3 button. */
    ROW3_4,
    /** Button 5 of row 3 button. */
    ROW3_5,
    /** Button 6 of row 3 button. */
    ROW3_6,
    /** Button 7 of row 3 button. */
    ROW3_7,
    /** Button 8 of row 3 button. */
    ROW3_8,

    /** Button 1 of row 4 button. */
    ROW4_1,
    /** Button 2 of row 4 button. */
    ROW4_2,
    /** Button 3 of row 4 button. */
    ROW4_3,
    /** Button 4 of row 4 button. */
    ROW4_4,
    /** Button 5 of row 4 button. */
    ROW4_5,
    /** Button 6 of row 4 button. */
    ROW4_6,
    /** Button 7 of row 4 button. */
    ROW4_7,
    /** Button 8 of row 4 button. */
    ROW4_8,

    /** Button 1 of row 5 button. */
    ROW5_1,
    /** Button 2 of row 5 button. */
    ROW5_2,
    /** Button 3 of row 5 button. */
    ROW5_3,
    /** Button 4 of row 5 button. */
    ROW5_4,
    /** Button 5 of row 5 button. */
    ROW5_5,
    /** Button 6 of row 5 button. */
    ROW5_6,
    /** Button 7 of row 5 button. */
    ROW5_7,
    /** Button 8 of row 5 button. */
    ROW5_8,

    /** Button 1 of row 6 button. */
    ROW6_1,
    /** Button 2 of row 6 button. */
    ROW6_2,
    /** Button 3 of row 6 button. */
    ROW6_3,
    /** Button 4 of row 6 button. */
    ROW6_4,
    /** Button 5 of row 6 button. */
    ROW6_5,
    /** Button 6 of row 6 button. */
    ROW6_6,
    /** Button 7 of row 6 button. */
    ROW6_7,
    /** Button 8 of row 6 button. */
    ROW6_8,

    /** Button select row 1 button. */
    ROW_SELECT_1,
    /** Button select row 2 button. */
    ROW_SELECT_2,
    /** Button select row 3 button. */
    ROW_SELECT_3,
    /** Button select row 4 button. */
    ROW_SELECT_4,
    /** Button select row 5 button. */
    ROW_SELECT_5,
    /** Button select row 6 button. */
    ROW_SELECT_6,
    /** Button select row 7 button. */
    ROW_SELECT_7,
    /** Button select row 8 button. */
    ROW_SELECT_8,

    /** The automation off button. */
    AUTOMATION_OFF,
    /** The automation read button. */
    AUTOMATION_READ,
    /** The automation write button. */
    AUTOMATION_WRITE,
    /** The automation group button. */
    AUTOMATION_GROUP,
    /** The automation trim button. */
    AUTOMATION_TRIM,
    /** The automation touch button. */
    AUTOMATION_TOUCH,
    /** The automation latch button. */
    AUTOMATION_LATCH,

    /** The move bank left button. */
    MOVE_BANK_LEFT,
    /** The move bank left button. */
    MOVE_BANK_RIGHT,
    /** The move bank left button. */
    MOVE_TRACK_LEFT,
    /** The move bank left button. */
    MOVE_TRACK_RIGHT,

    /** The button to touch a fader. */
    FADER_TOUCH_1,
    /** The button to touch a fader. */
    FADER_TOUCH_2,
    /** The button to touch a fader. */
    FADER_TOUCH_3,
    /** The button to touch a fader. */
    FADER_TOUCH_4,
    /** The button to touch a fader. */
    FADER_TOUCH_5,
    /** The button to touch a fader. */
    FADER_TOUCH_6,
    /** The button to touch a fader. */
    FADER_TOUCH_7,
    /** The button to touch a fader. */
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

    /** The button to select Send 1. */
    SEND1,
    /** The button to select Send 2. */
    SEND2,
    /** The button to select Send 3. */
    SEND3,
    /** The button to select Send 4. */
    SEND4,
    /** The button to select Send 5. */
    SEND5,
    /** The button to select Send 6. */
    SEND6,
    /** The button to select Send 7. */
    SEND7,
    /** The button to select Send 8. */
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

    /** Left button. */
    LEFT,
    /** Right button. */
    RIGHT,
    /** Up button. */
    UP,
    /** Down button. */
    DOWN,

    /** Pad 1. */
    PAD1,
    /** Pad 2. */
    PAD2,
    /** Pad 3. */
    PAD3,
    /** Pad 4. */
    PAD4,
    /** Pad 5. */
    PAD5,
    /** Pad 6. */
    PAD6,
    /** Pad 7. */
    PAD7,
    /** Pad 8. */
    PAD8,
    /** Pad 9. */
    PAD9,
    /** Pad 10. */
    PAD10,
    /** Pad 11. */
    PAD11,
    /** Pad 12. */
    PAD12,
    /** Pad 13. */
    PAD13,
    /** Pad 14. */
    PAD14,
    /** Pad 15. */
    PAD15,
    /** Pad 16. */
    PAD16,
    /** Pad 17. */
    PAD17,
    /** Pad 18. */
    PAD18,
    /** Pad 19. */
    PAD19,
    /** Pad 20. */
    PAD20,
    /** Pad 21. */
    PAD21,
    /** Pad 22. */
    PAD22,
    /** Pad 23. */
    PAD23,
    /** Pad 24. */
    PAD24,
    /** Pad 25. */
    PAD25,
    /** Pad 26. */
    PAD26,
    /** Pad 27. */
    PAD27,
    /** Pad 28. */
    PAD28,
    /** Pad 29. */
    PAD29,
    /** Pad 30. */
    PAD30,
    /** Pad 31. */
    PAD31,
    /** Pad 32. */
    PAD32,
    /** Pad 33. */
    PAD33,
    /** Pad 34. */
    PAD34,
    /** Pad 35. */
    PAD35,
    /** Pad 36. */
    PAD36,
    /** Pad 37. */
    PAD37,
    /** Pad 38. */
    PAD38,
    /** Pad 39. */
    PAD39,
    /** Pad 40. */
    PAD40,
    /** Pad 41. */
    PAD41,
    /** Pad 42. */
    PAD42,
    /** Pad 43. */
    PAD43,
    /** Pad 44. */
    PAD44,
    /** Pad 45. */
    PAD45,
    /** Pad 46. */
    PAD46,
    /** Pad 47. */
    PAD47,
    /** Pad 48. */
    PAD48,
    /** Pad 49. */
    PAD49,
    /** Pad 50. */
    PAD50,
    /** Pad 51. */
    PAD51,
    /** Pad 52. */
    PAD52,
    /** Pad 53. */
    PAD53,
    /** Pad 54. */
    PAD54,
    /** Pad 55. */
    PAD55,
    /** Pad 56. */
    PAD56,
    /** Pad 57. */
    PAD57,
    /** Pad 58. */
    PAD58,
    /** Pad 59. */
    PAD59,
    /** Pad 60. */
    PAD60,
    /** Pad 61. */
    PAD61,
    /** Pad 62. */
    PAD62,
    /** Pad 63. */
    PAD63,
    /** Pad 64. */
    PAD64,
    /** Pad 65. */
    PAD65,
    /** Pad 66. */
    PAD66,
    /** Pad 67. */
    PAD67,
    /** Pad 68. */
    PAD68,
    /** Pad 69. */
    PAD69,
    /** Pad 70. */
    PAD70,
    /** Pad 71. */
    PAD71,
    /** Pad 72. */
    PAD72,
    /** Pad 73. */
    PAD73,
    /** Pad 74. */
    PAD74,
    /** Pad 75. */
    PAD75,
    /** Pad 76. */
    PAD76,
    /** Pad 77. */
    PAD77,
    /** Pad 78. */
    PAD78,
    /** Pad 79. */
    PAD79,
    /** Pad 80. */
    PAD80,
    /** Pad 81. */
    PAD81,
    /** Pad 82. */
    PAD82,
    /** Pad 83. */
    PAD83,
    /** Pad 84. */
    PAD84,
    /** Pad 85. */
    PAD85,
    /** Pad 86. */
    PAD86,
    /** Pad 87. */
    PAD87,
    /** Pad 88. */
    PAD88;


    /**
     * Get an offset button ID, e.g. to get F4 set F1 and 3 as parameters.
     *
     * @param bid The base button ID
     * @param offset The offset
     * @return The offset button
     */
    public static ButtonID get (final ButtonID bid, final int offset)
    {
        return ButtonID.values ()[bid.ordinal () + offset];
    }


    /**
     * Test if the given button ID belongs to a scene button.
     *
     * @param bid The button ID to test
     * @return True if it is a scene button
     */
    public static boolean isSceneButton (final ButtonID bid)
    {
        if (bid == null)
            return false;
        final int ordinal = bid.ordinal ();
        return ordinal >= SCENE1.ordinal () && ordinal <= SCENE8.ordinal ();
    }


    /**
     * Test if the given button ID belongs to a pad.
     *
     * @param bid The button ID to test
     * @return True if it is a pad
     */
    public static boolean isPad (final ButtonID bid)
    {
        if (bid == null)
            return false;
        final int ordinal = bid.ordinal ();
        return ordinal >= PAD1.ordinal () && ordinal <= PAD88.ordinal ();
    }
}
