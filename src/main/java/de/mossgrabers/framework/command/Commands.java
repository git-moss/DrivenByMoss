// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command;

/**
 * IDs for often used commands.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface Commands
{
    /** The play command. */
    Integer COMMAND_PLAY                     = 0;
    /** The record command. */
    Integer COMMAND_RECORD                   = 1;
    /** The rewind command. */
    Integer COMMAND_REWIND                   = 2;
    /** The forward command. */
    Integer COMMAND_FORWARD                  = 3;
    /** The new command. */
    Integer COMMAND_NEW                      = 4;
    /** The duplicate command. */
    Integer COMMAND_DUPLICATE                = 5;
    /** The automation command. */
    Integer COMMAND_AUTOMATION               = 6;
    /** The fixed length command. */
    Integer COMMAND_FIXED_LENGTH             = 7;
    /** The Quantize button command. */
    Integer COMMAND_QUANTIZE                 = 8;
    /** The Delete button command. */
    Integer COMMAND_DELETE                   = 9;
    /** The Double button command. */
    Integer COMMAND_DOUBLE                   = 10;
    /** The Undo button command. */
    Integer COMMAND_UNDO                     = 11;

    /** The Device button command. */
    Integer COMMAND_DEVICE                   = 12;
    /** The Browse button command. */
    Integer COMMAND_BROWSE                   = 13;
    /** The Track button command. */
    Integer COMMAND_TRACK                    = 14;
    /** The Clip button command. */
    Integer COMMAND_CLIP                     = 15;
    /** The Volume button command. */
    Integer COMMAND_VOLUME                   = 16;
    /** The Pan and Send button command. */
    Integer COMMAND_PAN_SEND                 = 17;

    /** Button 1 of row 1 command. */
    Integer COMMAND_ROW1_1                   = 18;
    /** Button 2 of row 1 command. */
    Integer COMMAND_ROW1_2                   = 19;
    /** Button 3 of row 1 command. */
    Integer COMMAND_ROW1_3                   = 20;
    /** Button 4 of row 1 command. */
    Integer COMMAND_ROW1_4                   = 21;
    /** Button 5 of row 1 command. */
    Integer COMMAND_ROW1_5                   = 22;
    /** Button 6 of row 1 command. */
    Integer COMMAND_ROW1_6                   = 23;
    /** Button 7 of row 1 command. */
    Integer COMMAND_ROW1_7                   = 24;
    /** Button 8 of row 1 command. */
    Integer COMMAND_ROW1_8                   = 25;

    /** Button 1 of row 2 command. */
    Integer COMMAND_ROW2_1                   = 26;
    /** Button 2 of row 2 command. */
    Integer COMMAND_ROW2_2                   = 27;
    /** Button 3 of row 2 command. */
    Integer COMMAND_ROW2_3                   = 28;
    /** Button 4 of row 2 command. */
    Integer COMMAND_ROW2_4                   = 29;
    /** Button 5 of row 2 command. */
    Integer COMMAND_ROW2_5                   = 30;
    /** Button 6 of row 2 command. */
    Integer COMMAND_ROW2_6                   = 31;
    /** Button 7 of row 2 command. */
    Integer COMMAND_ROW2_7                   = 32;
    /** Button 8 of row 2 command. */
    Integer COMMAND_ROW2_8                   = 33;

    /** Button 1 of row 3 command. */
    Integer COMMAND_ROW3_1                   = 34;
    /** Button 2 of row 3 command. */
    Integer COMMAND_ROW3_2                   = 35;
    /** Button 3 of row 3 command. */
    Integer COMMAND_ROW3_3                   = 36;
    /** Button 4 of row 3 command. */
    Integer COMMAND_ROW3_4                   = 37;
    /** Button 5 of row 3 command. */
    Integer COMMAND_ROW3_5                   = 38;
    /** Button 6 of row 3 command. */
    Integer COMMAND_ROW3_6                   = 39;
    /** Button 7 of row 3 command. */
    Integer COMMAND_ROW3_7                   = 40;
    /** Button 8 of row 3 command. */
    Integer COMMAND_ROW3_8                   = 41;

    /** Button 1 of row 4 command. */
    Integer COMMAND_ROW4_1                   = 42;
    /** Button 2 of row 4 command. */
    Integer COMMAND_ROW4_2                   = 43;
    /** Button 3 of row 4 command. */
    Integer COMMAND_ROW4_3                   = 44;
    /** Button 4 of row 4 command. */
    Integer COMMAND_ROW4_4                   = 45;
    /** Button 5 of row 4 command. */
    Integer COMMAND_ROW4_5                   = 46;
    /** Button 6 of row 4 command. */
    Integer COMMAND_ROW4_6                   = 47;
    /** Button 7 of row 4 command. */
    Integer COMMAND_ROW4_7                   = 48;
    /** Button 8 of row 4 command. */
    Integer COMMAND_ROW4_8                   = 49;

    /** The Layout button command. */
    Integer COMMAND_LAYOUT                   = 50;
    /** The Shift button command. */
    Integer COMMAND_SHIFT                    = 51;
    /** The Select button command. */
    Integer COMMAND_SELECT                   = 52;

    /** The Tap Tempo button command. */
    Integer COMMAND_TAP_TEMPO                = 53;
    /** The Metronome button command. */
    Integer COMMAND_METRONOME                = 54;
    /** The Mastertrack command. */
    Integer COMMAND_MASTERTRACK              = 55;
    /** The Stop Clip command. */
    Integer COMMAND_STOP_CLIP                = 56;

    /** The Setup command. */
    Integer COMMAND_SETUP                    = 57;
    /** The Audio conversion command. */
    Integer COMMAND_CONVERT                  = 58;

    /** The Page left command. */
    Integer COMMAND_PAGE_LEFT                = 59;
    /** The Page right command. */
    Integer COMMAND_PAGE_RIGHT               = 60;
    /** The Mute command. */
    Integer COMMAND_MUTE                     = 61;
    /** The Solo command. */
    Integer COMMAND_SOLO                     = 62;
    /** The edit Scales command. */
    Integer COMMAND_SCALES                   = 63;
    /** The accent command. */
    Integer COMMAND_ACCENT                   = 64;
    /** The add effect command. */
    Integer COMMAND_ADD_EFFECT               = 65;
    /** The add track command. */
    Integer COMMAND_ADD_TRACK                = 66;
    /** The select play view command. */
    Integer COMMAND_SELECT_PLAY_VIEW         = 67;
    /** The select session view command. */
    Integer COMMAND_SELECT_SESSION_VIEW      = 68;
    /** The command to execute scene 1. */
    Integer COMMAND_SCENE1                   = 69;
    /** The command to execute scene 2. */
    Integer COMMAND_SCENE2                   = 70;
    /** The command to execute scene 3. */
    Integer COMMAND_SCENE3                   = 71;
    /** The command to execute scene 4. */
    Integer COMMAND_SCENE4                   = 72;
    /** The command to execute scene 5. */
    Integer COMMAND_SCENE5                   = 73;
    /** The command to execute scene 6. */
    Integer COMMAND_SCENE6                   = 74;
    /** The command to execute scene 7. */
    Integer COMMAND_SCENE7                   = 75;
    /** The command to execute scene 8. */
    Integer COMMAND_SCENE8                   = 76;
    /** The command for cursor arrow down. */
    Integer COMMAND_ARROW_DOWN               = 77;
    /** The command for cursor arrow up. */
    Integer COMMAND_ARROW_UP                 = 78;
    /** The command for cursor arrow left. */
    Integer COMMAND_ARROW_LEFT               = 79;
    /** The command for cursor arrow right. */
    Integer COMMAND_ARROW_RIGHT              = 80;
    /** The command for octave down. */
    Integer COMMAND_OCTAVE_DOWN              = 81;
    /** The command for octave up. */
    Integer COMMAND_OCTAVE_UP                = 82;
    /** The command for arming record. */
    Integer COMMAND_REC_ARM                  = 83;
    /** The command for sends. */
    Integer COMMAND_SENDS                    = 84;

    /** The command for stop. */
    Integer COMMAND_STOP                     = 85;
    /** The command for Nudge positive. */
    Integer COMMAND_NUDGE_PLUS               = 86;
    /** The command for Nudge negative. */
    Integer COMMAND_NUDGE_MINUS              = 87;
    /** The command for Device on/off. */
    Integer COMMAND_DEVICE_ON_OFF            = 88;
    /** The command for User. */
    Integer COMMAND_USER                     = 89;
    /** The command for stopping all clips. */
    Integer COMMAND_STOP_ALL_CLIPS           = 90;

    /** Button select row 1 command. */
    Integer COMMAND_ROW_SELECT_1             = 91;
    /** Button select row 2 command. */
    Integer COMMAND_ROW_SELECT_2             = 92;
    /** Button select row 3 command. */
    Integer COMMAND_ROW_SELECT_3             = 93;
    /** Button select row 4 command. */
    Integer COMMAND_ROW_SELECT_4             = 94;
    /** Button select row 5 command. */
    Integer COMMAND_ROW_SELECT_5             = 95;
    /** Button select row 6 command. */
    Integer COMMAND_ROW_SELECT_6             = 96;
    /** Button select row 7 command. */
    Integer COMMAND_ROW_SELECT_7             = 97;
    /** Button select row 8 command. */
    Integer COMMAND_ROW_SELECT_8             = 98;

    /** Button loop toggle command. */
    Integer COMMAND_LOOP                     = 99;
    /** Button punch in command. */
    Integer COMMAND_PUNCH_IN                 = 100;
    /** Button punch out command. */
    Integer COMMAND_PUNCH_OUT                = 101;

    /** The automation read command. */
    Integer COMMAND_AUTOMATION_READ          = 110;
    /** The automation write command. */
    Integer COMMAND_AUTOMATION_WRITE         = 111;
    /** The automation trim command. */
    Integer COMMAND_AUTOMATION_TRIM          = 112;
    /** The automation touch command. */
    Integer COMMAND_AUTOMATION_TOUCH         = 113;
    /** The automation latch command. */
    Integer COMMAND_AUTOMATION_LATCH         = 114;
    /** The zoom state command. */
    Integer COMMAND_ZOOM                     = 115;

    /** The move bank left command. */
    Integer COMMAND_MOVE_BANK_LEFT           = 116;
    /** The move bank left command. */
    Integer COMMAND_MOVE_BANK_RIGHT          = 117;
    /** The move bank left command. */
    Integer COMMAND_MOVE_TRACK_LEFT          = 118;
    /** The move bank left command. */
    Integer COMMAND_MOVE_TRACK_RIGHT         = 119;
    /** The command to save the current project. */
    Integer COMMAND_SAVE                     = 120;
    /** The command to toggle markers. */
    Integer COMMAND_MARKER                   = 121;
    /** The command to toggle VU meters. */
    Integer COMMAND_TOGGLE_VU                = 122;

    /** Continuous knob 1. */
    Integer CONT_COMMAND_KNOB1               = 1;
    /** Continuous knob 2. */
    Integer CONT_COMMAND_KNOB2               = 2;
    /** Continuous knob 3. */
    Integer CONT_COMMAND_KNOB3               = 3;
    /** Continuous knob 4. */
    Integer CONT_COMMAND_KNOB4               = 4;
    /** Continuous knob 5. */
    Integer CONT_COMMAND_KNOB5               = 5;
    /** Continuous knob 6. */
    Integer CONT_COMMAND_KNOB6               = 6;
    /** Continuous knob 7. */
    Integer CONT_COMMAND_KNOB7               = 7;
    /** Continuous knob 8. */
    Integer CONT_COMMAND_KNOB8               = 8;

    /** Continuous master knob. */
    Integer CONT_COMMAND_MASTER_KNOB         = 9;
    /** Continuous tempo knob. */
    Integer CONT_COMMAND_TEMPO               = 10;
    /** Continuous play position knob. */
    Integer CONT_COMMAND_PLAY_POSITION       = 11;
    /** Footswitch command. */
    Integer COMMAND_FOOTSWITCH               = 12;
    /** Knob 1 touched. */
    Integer CONT_COMMAND_KNOB1_TOUCH         = 13;
    /** Knob 2 touched. */
    Integer CONT_COMMAND_KNOB2_TOUCH         = 14;
    /** Knob 3 touched. */
    Integer CONT_COMMAND_KNOB3_TOUCH         = 15;
    /** Knob 4 touched. */
    Integer CONT_COMMAND_KNOB4_TOUCH         = 16;
    /** Knob 5 touched. */
    Integer CONT_COMMAND_KNOB5_TOUCH         = 17;
    /** Knob 6 touched. */
    Integer CONT_COMMAND_KNOB6_TOUCH         = 18;
    /** Knob 7 touched. */
    Integer CONT_COMMAND_KNOB7_TOUCH         = 19;
    /** Knob 8 touched. */
    Integer CONT_COMMAND_KNOB8_TOUCH         = 20;
    /** Mastertrack touched. */
    Integer CONT_COMMAND_MASTERTRACK_TOUCH   = 21;
    /** Tempo button touched. */
    Integer CONT_COMMAND_TEMPO_TOUCH         = 22;
    /** Playcursor button touched. */
    Integer CONT_COMMAND_PLAYCURSOR_TOUCH    = 23;
    /** Configure pitchbend touched. */
    Integer CONT_COMMAND_CONFIGURE_PITCHBEND = 24;
    /** Change the crossfader. */
    Integer CONT_COMMAND_CROSSFADER          = 25;

    /** Continuous fader 1. */
    Integer CONT_COMMAND_FADER1              = 30;
    /** Continuous fader 2. */
    Integer CONT_COMMAND_FADER2              = 31;
    /** Continuous fader 3. */
    Integer CONT_COMMAND_FADER3              = 32;
    /** Continuous fader 4. */
    Integer CONT_COMMAND_FADER4              = 33;
    /** Continuous fader 5. */
    Integer CONT_COMMAND_FADER5              = 34;
    /** Continuous fader 6. */
    Integer CONT_COMMAND_FADER6              = 35;
    /** Continuous fader 7. */
    Integer CONT_COMMAND_FADER7              = 36;
    /** Continuous fader 8. */
    Integer CONT_COMMAND_FADER8              = 37;

    /** Continuous device knob 1. */
    Integer CONT_COMMAND_DEVICE_KNOB1        = 40;
    /** Continuous device knob 2. */
    Integer CONT_COMMAND_DEVICE_KNOB2        = 41;
    /** Continuous device knob 3. */
    Integer CONT_COMMAND_DEVICE_KNOB3        = 42;
    /** Continuous device knob 4. */
    Integer CONT_COMMAND_DEVICE_KNOB4        = 43;
    /** Continuous device knob 5. */
    Integer CONT_COMMAND_DEVICE_KNOB5        = 44;
    /** Continuous device knob 6. */
    Integer CONT_COMMAND_DEVICE_KNOB6        = 45;
    /** Continuous device knob 7. */
    Integer CONT_COMMAND_DEVICE_KNOB7        = 46;
    /** Continuous device knob 8. */
    Integer CONT_COMMAND_DEVICE_KNOB8        = 47;

    /** Continuous touch pad in x direction. */
    Integer CONT_COMMAND_TOUCHPAD_X          = 48;
    /** Continuous touch pad in y direction. */
    Integer CONT_COMMAND_TOUCHPAD_Y          = 49;
}
