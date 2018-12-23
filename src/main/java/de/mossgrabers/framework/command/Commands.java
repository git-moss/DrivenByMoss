// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
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
    Integer COMMAND_PLAY                     = Integer.valueOf (0);
    /** The record command. */
    Integer COMMAND_RECORD                   = Integer.valueOf (1);
    /** The rewind command. */
    Integer COMMAND_REWIND                   = Integer.valueOf (2);
    /** The forward command. */
    Integer COMMAND_FORWARD                  = Integer.valueOf (3);
    /** The new command. */
    Integer COMMAND_NEW                      = Integer.valueOf (4);
    /** The duplicate command. */
    Integer COMMAND_DUPLICATE                = Integer.valueOf (5);
    /** The automation command. */
    Integer COMMAND_AUTOMATION               = Integer.valueOf (6);
    /** The fixed length command. */
    Integer COMMAND_FIXED_LENGTH             = Integer.valueOf (7);
    /** The Quantize button command. */
    Integer COMMAND_QUANTIZE                 = Integer.valueOf (8);
    /** The Delete button command. */
    Integer COMMAND_DELETE                   = Integer.valueOf (9);
    /** The Double button command. */
    Integer COMMAND_DOUBLE                   = Integer.valueOf (10);
    /** The Undo button command. */
    Integer COMMAND_UNDO                     = Integer.valueOf (11);

    /** The Device button command. */
    Integer COMMAND_DEVICE                   = Integer.valueOf (12);
    /** The Browse button command. */
    Integer COMMAND_BROWSE                   = Integer.valueOf (13);
    /** The Track button command. */
    Integer COMMAND_TRACK                    = Integer.valueOf (14);
    /** The Clip button command. */
    Integer COMMAND_CLIP                     = Integer.valueOf (15);
    /** The Volume button command. */
    Integer COMMAND_VOLUME                   = Integer.valueOf (16);
    /** The Pan and Send button command. */
    Integer COMMAND_PAN_SEND                 = Integer.valueOf (17);

    /** Button 1 of row 1 command. */
    Integer COMMAND_ROW1_1                   = Integer.valueOf (18);
    /** Button 2 of row 1 command. */
    Integer COMMAND_ROW1_2                   = Integer.valueOf (19);
    /** Button 3 of row 1 command. */
    Integer COMMAND_ROW1_3                   = Integer.valueOf (20);
    /** Button 4 of row 1 command. */
    Integer COMMAND_ROW1_4                   = Integer.valueOf (21);
    /** Button 5 of row 1 command. */
    Integer COMMAND_ROW1_5                   = Integer.valueOf (22);
    /** Button 6 of row 1 command. */
    Integer COMMAND_ROW1_6                   = Integer.valueOf (23);
    /** Button 7 of row 1 command. */
    Integer COMMAND_ROW1_7                   = Integer.valueOf (24);
    /** Button 8 of row 1 command. */
    Integer COMMAND_ROW1_8                   = Integer.valueOf (25);

    /** Button 1 of row 2 command. */
    Integer COMMAND_ROW2_1                   = Integer.valueOf (26);
    /** Button 2 of row 2 command. */
    Integer COMMAND_ROW2_2                   = Integer.valueOf (27);
    /** Button 3 of row 2 command. */
    Integer COMMAND_ROW2_3                   = Integer.valueOf (28);
    /** Button 4 of row 2 command. */
    Integer COMMAND_ROW2_4                   = Integer.valueOf (29);
    /** Button 5 of row 2 command. */
    Integer COMMAND_ROW2_5                   = Integer.valueOf (30);
    /** Button 6 of row 2 command. */
    Integer COMMAND_ROW2_6                   = Integer.valueOf (31);
    /** Button 7 of row 2 command. */
    Integer COMMAND_ROW2_7                   = Integer.valueOf (32);
    /** Button 8 of row 2 command. */
    Integer COMMAND_ROW2_8                   = Integer.valueOf (33);

    /** Button 1 of row 3 command. */
    Integer COMMAND_ROW3_1                   = Integer.valueOf (34);
    /** Button 2 of row 3 command. */
    Integer COMMAND_ROW3_2                   = Integer.valueOf (35);
    /** Button 3 of row 3 command. */
    Integer COMMAND_ROW3_3                   = Integer.valueOf (36);
    /** Button 4 of row 3 command. */
    Integer COMMAND_ROW3_4                   = Integer.valueOf (37);
    /** Button 5 of row 3 command. */
    Integer COMMAND_ROW3_5                   = Integer.valueOf (38);
    /** Button 6 of row 3 command. */
    Integer COMMAND_ROW3_6                   = Integer.valueOf (39);
    /** Button 7 of row 3 command. */
    Integer COMMAND_ROW3_7                   = Integer.valueOf (40);
    /** Button 8 of row 3 command. */
    Integer COMMAND_ROW3_8                   = Integer.valueOf (41);

    /** Button 1 of row 4 command. */
    Integer COMMAND_ROW4_1                   = Integer.valueOf (42);
    /** Button 2 of row 4 command. */
    Integer COMMAND_ROW4_2                   = Integer.valueOf (43);
    /** Button 3 of row 4 command. */
    Integer COMMAND_ROW4_3                   = Integer.valueOf (44);
    /** Button 4 of row 4 command. */
    Integer COMMAND_ROW4_4                   = Integer.valueOf (45);
    /** Button 5 of row 4 command. */
    Integer COMMAND_ROW4_5                   = Integer.valueOf (46);
    /** Button 6 of row 4 command. */
    Integer COMMAND_ROW4_6                   = Integer.valueOf (47);
    /** Button 7 of row 4 command. */
    Integer COMMAND_ROW4_7                   = Integer.valueOf (48);
    /** Button 8 of row 4 command. */
    Integer COMMAND_ROW4_8                   = Integer.valueOf (49);

    /** The Layout button command. */
    Integer COMMAND_LAYOUT                   = Integer.valueOf (50);
    /** The Shift button command. */
    Integer COMMAND_SHIFT                    = Integer.valueOf (51);
    /** The Select button command. */
    Integer COMMAND_SELECT                   = Integer.valueOf (52);

    /** The Tap Tempo button command. */
    Integer COMMAND_TAP_TEMPO                = Integer.valueOf (53);
    /** The Metronome button command. */
    Integer COMMAND_METRONOME                = Integer.valueOf (54);
    /** The Mastertrack command. */
    Integer COMMAND_MASTERTRACK              = Integer.valueOf (55);
    /** The Stop Clip command. */
    Integer COMMAND_STOP_CLIP                = Integer.valueOf (56);

    /** The Setup command. */
    Integer COMMAND_SETUP                    = Integer.valueOf (57);
    /** The Audio conversion command. */
    Integer COMMAND_CONVERT                  = Integer.valueOf (58);

    /** The Page left command. */
    Integer COMMAND_PAGE_LEFT                = Integer.valueOf (59);
    /** The Page right command. */
    Integer COMMAND_PAGE_RIGHT               = Integer.valueOf (60);
    /** The Mute command. */
    Integer COMMAND_MUTE                     = Integer.valueOf (61);
    /** The Solo command. */
    Integer COMMAND_SOLO                     = Integer.valueOf (62);
    /** The edit Scales command. */
    Integer COMMAND_SCALES                   = Integer.valueOf (63);
    /** The accent command. */
    Integer COMMAND_ACCENT                   = Integer.valueOf (64);
    /** The add effect command. */
    Integer COMMAND_ADD_EFFECT               = Integer.valueOf (65);
    /** The add track command. */
    Integer COMMAND_ADD_TRACK                = Integer.valueOf (66);
    /** The select play view command. */
    Integer COMMAND_SELECT_PLAY_VIEW         = Integer.valueOf (67);
    /** The select session view command. */
    Integer COMMAND_SELECT_SESSION_VIEW      = Integer.valueOf (68);
    /** The command to execute scene 1. */
    Integer COMMAND_SCENE1                   = Integer.valueOf (69);
    /** The command to execute scene 2. */
    Integer COMMAND_SCENE2                   = Integer.valueOf (70);
    /** The command to execute scene 3. */
    Integer COMMAND_SCENE3                   = Integer.valueOf (71);
    /** The command to execute scene 4. */
    Integer COMMAND_SCENE4                   = Integer.valueOf (72);
    /** The command to execute scene 5. */
    Integer COMMAND_SCENE5                   = Integer.valueOf (73);
    /** The command to execute scene 6. */
    Integer COMMAND_SCENE6                   = Integer.valueOf (74);
    /** The command to execute scene 7. */
    Integer COMMAND_SCENE7                   = Integer.valueOf (75);
    /** The command to execute scene 8. */
    Integer COMMAND_SCENE8                   = Integer.valueOf (76);
    /** The command for cursor arrow down. */
    Integer COMMAND_ARROW_DOWN               = Integer.valueOf (77);
    /** The command for cursor arrow up. */
    Integer COMMAND_ARROW_UP                 = Integer.valueOf (78);
    /** The command for cursor arrow left. */
    Integer COMMAND_ARROW_LEFT               = Integer.valueOf (79);
    /** The command for cursor arrow right. */
    Integer COMMAND_ARROW_RIGHT              = Integer.valueOf (80);
    /** The command for octave down. */
    Integer COMMAND_OCTAVE_DOWN              = Integer.valueOf (81);
    /** The command for octave up. */
    Integer COMMAND_OCTAVE_UP                = Integer.valueOf (82);
    /** The command for arming record. */
    Integer COMMAND_REC_ARM                  = Integer.valueOf (83);
    /** The command for sends. */
    Integer COMMAND_SENDS                    = Integer.valueOf (84);

    /** The command for stop. */
    Integer COMMAND_STOP                     = Integer.valueOf (85);
    /** The command for Nudge positive. */
    Integer COMMAND_NUDGE_PLUS               = Integer.valueOf (86);
    /** The command for Nudge negative. */
    Integer COMMAND_NUDGE_MINUS              = Integer.valueOf (87);
    /** The command for Device on/off. */
    Integer COMMAND_DEVICE_ON_OFF            = Integer.valueOf (88);
    /** The command for User. */
    Integer COMMAND_USER                     = Integer.valueOf (89);
    /** The command for stopping all clips. */
    Integer COMMAND_STOP_ALL_CLIPS           = Integer.valueOf (90);

    /** Button select row 1 command. */
    Integer COMMAND_ROW_SELECT_1             = Integer.valueOf (91);
    /** Button select row 2 command. */
    Integer COMMAND_ROW_SELECT_2             = Integer.valueOf (92);
    /** Button select row 3 command. */
    Integer COMMAND_ROW_SELECT_3             = Integer.valueOf (93);
    /** Button select row 4 command. */
    Integer COMMAND_ROW_SELECT_4             = Integer.valueOf (94);
    /** Button select row 5 command. */
    Integer COMMAND_ROW_SELECT_5             = Integer.valueOf (95);
    /** Button select row 6 command. */
    Integer COMMAND_ROW_SELECT_6             = Integer.valueOf (96);
    /** Button select row 7 command. */
    Integer COMMAND_ROW_SELECT_7             = Integer.valueOf (97);
    /** Button select row 8 command. */
    Integer COMMAND_ROW_SELECT_8             = Integer.valueOf (98);

    /** Button loop toggle command. */
    Integer COMMAND_LOOP                     = Integer.valueOf (99);
    /** Button punch in command. */
    Integer COMMAND_PUNCH_IN                 = Integer.valueOf (100);
    /** Button punch out command. */
    Integer COMMAND_PUNCH_OUT                = Integer.valueOf (101);

    /** Button to toggle the repeat command. */
    Integer COMMAND_REPEAT                   = Integer.valueOf (102);
    /** Button to redo a command. */
    Integer COMMAND_REDO                     = Integer.valueOf (103);

    /** The automation read command. */
    Integer COMMAND_AUTOMATION_READ          = Integer.valueOf (110);
    /** The automation write command. */
    Integer COMMAND_AUTOMATION_WRITE         = Integer.valueOf (111);
    /** The automation trim command. */
    Integer COMMAND_AUTOMATION_TRIM          = Integer.valueOf (112);
    /** The automation touch command. */
    Integer COMMAND_AUTOMATION_TOUCH         = Integer.valueOf (113);
    /** The automation latch command. */
    Integer COMMAND_AUTOMATION_LATCH         = Integer.valueOf (114);
    /** The zoom state command. */
    Integer COMMAND_ZOOM                     = Integer.valueOf (115);

    /** The move bank left command. */
    Integer COMMAND_MOVE_BANK_LEFT           = Integer.valueOf (116);
    /** The move bank left command. */
    Integer COMMAND_MOVE_BANK_RIGHT          = Integer.valueOf (117);
    /** The move bank left command. */
    Integer COMMAND_MOVE_TRACK_LEFT          = Integer.valueOf (118);
    /** The move bank left command. */
    Integer COMMAND_MOVE_TRACK_RIGHT         = Integer.valueOf (119);
    /** The command to save the current project. */
    Integer COMMAND_SAVE                     = Integer.valueOf (120);
    /** The command to toggle markers. */
    Integer COMMAND_MARKER                   = Integer.valueOf (121);
    /** The command to toggle VU meters. */
    Integer COMMAND_TOGGLE_VU                = Integer.valueOf (122);

    /** The command to touch a fader. */
    Integer COMMAND_FADER_TOUCH_1            = Integer.valueOf (123);
    /** The command to touch a fader. */
    Integer COMMAND_FADER_TOUCH_2            = Integer.valueOf (124);
    /** The command to touch a fader. */
    Integer COMMAND_FADER_TOUCH_3            = Integer.valueOf (125);
    /** The command to touch a fader. */
    Integer COMMAND_FADER_TOUCH_4            = Integer.valueOf (126);
    /** The command to touch a fader. */
    Integer COMMAND_FADER_TOUCH_5            = Integer.valueOf (127);
    /** The command to touch a fader. */
    Integer COMMAND_FADER_TOUCH_6            = Integer.valueOf (128);
    /** The command to touch a fader. */
    Integer COMMAND_FADER_TOUCH_7            = Integer.valueOf (129);
    /** The command to touch a fader. */
    Integer COMMAND_FADER_TOUCH_8            = Integer.valueOf (130);

    /** Continuous knob 1. */
    Integer CONT_COMMAND_KNOB1               = Integer.valueOf (1);
    /** Continuous knob 2. */
    Integer CONT_COMMAND_KNOB2               = Integer.valueOf (2);
    /** Continuous knob 3. */
    Integer CONT_COMMAND_KNOB3               = Integer.valueOf (3);
    /** Continuous knob 4. */
    Integer CONT_COMMAND_KNOB4               = Integer.valueOf (4);
    /** Continuous knob 5. */
    Integer CONT_COMMAND_KNOB5               = Integer.valueOf (5);
    /** Continuous knob 6. */
    Integer CONT_COMMAND_KNOB6               = Integer.valueOf (6);
    /** Continuous knob 7. */
    Integer CONT_COMMAND_KNOB7               = Integer.valueOf (7);
    /** Continuous knob 8. */
    Integer CONT_COMMAND_KNOB8               = Integer.valueOf (8);

    /** Continuous master knob. */
    Integer CONT_COMMAND_MASTER_KNOB         = Integer.valueOf (9);
    /** Continuous tempo knob. */
    Integer CONT_COMMAND_TEMPO               = Integer.valueOf (10);
    /** Continuous play position knob. */
    Integer CONT_COMMAND_PLAY_POSITION       = Integer.valueOf (11);
    /** Footswitch command. */
    Integer COMMAND_FOOTSWITCH               = Integer.valueOf (12);
    /** Knob 1 touched. */
    Integer CONT_COMMAND_KNOB1_TOUCH         = Integer.valueOf (13);
    /** Knob 2 touched. */
    Integer CONT_COMMAND_KNOB2_TOUCH         = Integer.valueOf (14);
    /** Knob 3 touched. */
    Integer CONT_COMMAND_KNOB3_TOUCH         = Integer.valueOf (15);
    /** Knob 4 touched. */
    Integer CONT_COMMAND_KNOB4_TOUCH         = Integer.valueOf (16);
    /** Knob 5 touched. */
    Integer CONT_COMMAND_KNOB5_TOUCH         = Integer.valueOf (17);
    /** Knob 6 touched. */
    Integer CONT_COMMAND_KNOB6_TOUCH         = Integer.valueOf (18);
    /** Knob 7 touched. */
    Integer CONT_COMMAND_KNOB7_TOUCH         = Integer.valueOf (19);
    /** Knob 8 touched. */
    Integer CONT_COMMAND_KNOB8_TOUCH         = Integer.valueOf (20);
    /** Mastertrack touched. */
    Integer CONT_COMMAND_MASTERTRACK_TOUCH   = Integer.valueOf (21);
    /** Tempo button touched. */
    Integer CONT_COMMAND_TEMPO_TOUCH         = Integer.valueOf (22);
    /** Playcursor button touched. */
    Integer CONT_COMMAND_PLAYCURSOR_TOUCH    = Integer.valueOf (23);
    /** Configure pitchbend touched. */
    Integer CONT_COMMAND_CONFIGURE_PITCHBEND = Integer.valueOf (24);
    /** Change the crossfader. */
    Integer CONT_COMMAND_CROSSFADER          = Integer.valueOf (25);

    /** Continuous fader 1. */
    Integer CONT_COMMAND_FADER1              = Integer.valueOf (30);
    /** Continuous fader 2. */
    Integer CONT_COMMAND_FADER2              = Integer.valueOf (31);
    /** Continuous fader 3. */
    Integer CONT_COMMAND_FADER3              = Integer.valueOf (32);
    /** Continuous fader 4. */
    Integer CONT_COMMAND_FADER4              = Integer.valueOf (33);
    /** Continuous fader 5. */
    Integer CONT_COMMAND_FADER5              = Integer.valueOf (34);
    /** Continuous fader 6. */
    Integer CONT_COMMAND_FADER6              = Integer.valueOf (35);
    /** Continuous fader 7. */
    Integer CONT_COMMAND_FADER7              = Integer.valueOf (36);
    /** Continuous fader 8. */
    Integer CONT_COMMAND_FADER8              = Integer.valueOf (37);

    /** Continuous device knob 1. */
    Integer CONT_COMMAND_DEVICE_KNOB1        = Integer.valueOf (40);
    /** Continuous device knob 2. */
    Integer CONT_COMMAND_DEVICE_KNOB2        = Integer.valueOf (41);
    /** Continuous device knob 3. */
    Integer CONT_COMMAND_DEVICE_KNOB3        = Integer.valueOf (42);
    /** Continuous device knob 4. */
    Integer CONT_COMMAND_DEVICE_KNOB4        = Integer.valueOf (43);
    /** Continuous device knob 5. */
    Integer CONT_COMMAND_DEVICE_KNOB5        = Integer.valueOf (44);
    /** Continuous device knob 6. */
    Integer CONT_COMMAND_DEVICE_KNOB6        = Integer.valueOf (45);
    /** Continuous device knob 7. */
    Integer CONT_COMMAND_DEVICE_KNOB7        = Integer.valueOf (46);
    /** Continuous device knob 8. */
    Integer CONT_COMMAND_DEVICE_KNOB8        = Integer.valueOf (47);

    /** Continuous touch pad in x direction. */
    Integer CONT_COMMAND_TOUCHPAD_X          = Integer.valueOf (48);
    /** Continuous touch pad in y direction. */
    Integer CONT_COMMAND_TOUCHPAD_Y          = Integer.valueOf (49);
}
