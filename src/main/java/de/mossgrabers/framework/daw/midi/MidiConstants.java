// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.midi;

/**
 * MIDI constants.
 *
 * @author Jürgen Moßgraber
 */
public class MidiConstants
{
    /** Note off. */
    public static final int        CMD_NOTE_OFF           = 0x80;
    /** Note on. */
    public static final int        CMD_NOTE_ON            = 0x90;
    /** Polyphonic Aftertouch. */
    public static final int        CMD_POLY_AFTERTOUCH    = 0xA0;
    /** Continuous Control. */
    public static final int        CMD_CC                 = 0xB0;
    /** Program Change. */
    public static final int        CMD_PROGRAM_CHANGE     = 0xC0;
    /** Channel Aftertouch. */
    public static final int        CMD_CHANNEL_AFTERTOUCH = 0xD0;
    /** Pitchbend. */
    public static final int        CMD_PITCHBEND          = 0xE0;
    /** System. */
    public static final int        CMD_SYSTEM             = 0xF0;

    private static final String [] SYSEX_NAMES            =
    {
        "F0: Start of System Exclusive Status Byte",
        "F1: MIDI Time Code Quarter Frame",
        "F2: Song Position Pointer",
        "F3: Song Select",
        "F4: Undefined",
        "F5: Undefined",
        "F6: Tune Request",
        "F7: End of System Exclusive",
        "F8: Timing Clock",
        "F9: Undefined",
        "FA: Start",
        "FB: Continue",
        "FC: Stop",
        "FD: Undefined",
        "FE: Active Sensing",
        "FF: System Reset"
    };

    private static final String [] CC_NAMES               =
    {
        "00 Bank Select (MSB)",
        "01 Modulation Wheel (MSB)",
        "02 Breath Controller (MSB)",
        "03 Undefined (MSB)",
        "04 Foot Controller (MSB)",
        "05 Portamento Time (MSB)",
        "06 Data Entry (MSB)",
        "07 Channel Volume (MSB)",
        "08 Balance (MSB)",
        "09 Undefined (MSB)",
        "10 Pan (MSB)",
        "11 Expression (MSB)",
        "12 Effect Control 1 (MSB)",
        "13 Effect Control 2 (MSB)",
        "14 Undefined (MSB)",
        "15 Undefined (MSB)",
        "16 General Purpose Controller 1 (MSB)",
        "17 General Purpose Controller 2 (MSB)",
        "18 General Purpose Controller 3 (MSB)",
        "19 General Purpose Controller 4 (MSB)",
        "20 Undefined (MSB)",
        "21 Undefined (MSB)",
        "22 Undefined (MSB)",
        "23 Undefined (MSB)",
        "24 Undefined (MSB)",
        "25 Undefined (MSB)",
        "26 Undefined (MSB)",
        "27 Undefined (MSB)",
        "28 Undefined (MSB)",
        "29 Undefined (MSB)",
        "30 Undefined (MSB)",
        "31 Undefined (MSB)",
        "32 Bank Select (LSB)",
        "33 Modulation Wheel (LSB)",
        "34 Breath Controller (LSB)",
        "35 Undefined (LSB)",
        "36 Foot Controller (LSB)",
        "37 Portamento Time (LSB)",
        "38 Data Entry (LSB)",
        "39 Channel Volume (LSB)",
        "30 Balance (LSB)",
        "41 Undefined (LSB)",
        "42 Pan (LSB)",
        "43 Expression (LSB)",
        "44 Effect Control 1 (LSB)",
        "45 Effect Control 2 (LSB)",
        "46 Undefined (LSB)",
        "47 Undefined (LSB)",
        "48 General Purpose Controller 1 (LSB)",
        "49 General Purpose Controller 2 (LSB)",
        "50 General Purpose Controller 3 (LSB)",
        "51 General Purpose Controller 4 (LSB)",
        "52 Undefined (LSB)",
        "53 Undefined (LSB)",
        "54 Undefined (LSB)",
        "55 Undefined (LSB)",
        "56 Undefined (LSB)",
        "57 Undefined (LSB)",
        "58 Undefined (LSB)",
        "59 Undefined (LSB)",
        "60 Undefined (LSB)",
        "61 Undefined (LSB)",
        "62 Undefined (LSB)",
        "63 Undefined (LSB)",
        "64 Sustain Pedal",
        "65 Portamento On/Off",
        "66 Sostenuto",
        "67 Soft Pedal",
        "68 Legato Footswitch",
        "69 Hold 2",
        "70 Sound Controller 1 - Sound Variation",
        "71 Sound Controller 2 - Timbre/Harmonic Intensity",
        "72 Sound Controller 3 - Release Time",
        "73 Sound Controller 4 - Attack Time",
        "74 Sound Controller 5 - Brightness",
        "75 Sound Controller 6 - Decay Time",
        "76 Sound Controller 7 - Vibrato Rate",
        "77 Sound Controller 8 - Vibrato Depth",
        "78 Sound Controller 9 - Vibrato Delay",
        "79 Sound Controller 10",
        "80 General Purpose 5",
        "81 General Purpose 6",
        "82 General Purpose 7",
        "83 General Purpose 8",
        "84 Portamento Control",
        "85 Undefined",
        "86 Undefined",
        "87 Undefined",
        "88 High Resolution Velocity Prefix",
        "89 Undefined",
        "90 Undefined",
        "91 Effect 1 Depth - Reverb Send Level",
        "92 Effect 2 Depth - Tremolo Depth",
        "93 Effect 3 Depth - Chorus Send Level",
        "94 Effect 4 Depth - Celeste [Detune] Depth",
        "95 Effect 5 Depth - Phaser Depth",
        "96 Data Increment",
        "97 Data Decrement",
        "98 Non-Registered Parameter Number (NRPN) LSB",
        "99 Non-Registered Parameter Number (NRPN) MSB",
        "100 Registered Parameter Number (RPN) LSB",
        "101 Registered Parameter Number (RPN) MSB",
        "102 Undefined",
        "103 Undefined",
        "104 Undefined",
        "105 Undefined",
        "106 Undefined",
        "107 Undefined",
        "108 Undefined",
        "109 Undefined",
        "110 Undefined",
        "111 Undefined",
        "112 Undefined",
        "113 Undefined",
        "114 Undefined",
        "115 Undefined",
        "116 Undefined",
        "117 Undefined",
        "118 Undefined",
        "119 Undefined",
        "120 All Sound Off",
        "121 Reset All Controllers",
        "122 Local On/Off Switch",
        "123 All Notes Off",
        "124 Omni Mode Off",
        "125 Omni Mode On",
        "126 Mono Mode",
        "127 Poly Mode"
    };

    private static final String [] MMC_NAMES              = new String [128];
    static
    {
        MMC_NAMES[0x00] = "Reserved for extensions";
        MMC_NAMES[0x01] = "Stop";
        MMC_NAMES[0x02] = "Play";
        MMC_NAMES[0x03] = "Deferred play";
        MMC_NAMES[0x04] = "Fast forward";
        MMC_NAMES[0x05] = "Rewind";
        MMC_NAMES[0x06] = "Record strobe";
        MMC_NAMES[0x07] = "Record exit";
        MMC_NAMES[0x08] = "Record pause";
        MMC_NAMES[0x09] = "Pause";
        MMC_NAMES[0x0a] = "Eject";
        MMC_NAMES[0x0b] = "Chase";
        MMC_NAMES[0x0c] = "Command error reset";
        MMC_NAMES[0x0d] = "MMC reset";
        MMC_NAMES[0x40] = "Write";
        MMC_NAMES[0x41] = "Masked write";
        MMC_NAMES[0x42] = "Read";
        MMC_NAMES[0x43] = "Update";
        MMC_NAMES[0x44] = "Locate";
        MMC_NAMES[0x45] = "Variable play";
        MMC_NAMES[0x46] = "Search";
        MMC_NAMES[0x47] = "Shuttle";
        MMC_NAMES[0x48] = "Step";
        MMC_NAMES[0x49] = "Assign system master";
        MMC_NAMES[0x4a] = "Generator command";
        MMC_NAMES[0x4b] = "Midi time code command";
        MMC_NAMES[0x4c] = "Move";
        MMC_NAMES[0x4d] = "Add";
        MMC_NAMES[0x4e] = "Subtract";
        MMC_NAMES[0x4f] = "Drop frame adjust";
        MMC_NAMES[0x50] = "Procedure";
        MMC_NAMES[0x51] = "Event";
        MMC_NAMES[0x52] = "Group";
        MMC_NAMES[0x53] = "Command segment";
        MMC_NAMES[0x54] = "Deferred variable play";
        MMC_NAMES[0x55] = "Record strobe variable";
        MMC_NAMES[0x7c] = "Wait";
        MMC_NAMES[0x7f] = "Resume";

        for (int i = 0; i < MMC_NAMES.length; i++)
        {
            if (MMC_NAMES[i] == null)
                MMC_NAMES[i] = "-";
        }
    }


    /**
     * Get the System Exclusive names.
     *
     * @return The names
     */
    public static String [] getSysexNames ()
    {
        return SYSEX_NAMES;
    }


    /**
     * Get the Continuous Control names.
     *
     * @return The names
     */
    public static String [] getCCNames ()
    {
        return CC_NAMES;
    }


    /**
     * Get the MMC names.
     *
     * @return The names
     */
    public static String [] getMMCNames ()
    {
        return MMC_NAMES;
    }


    /**
     * Due to constant class.
     */
    private MidiConstants ()
    {
        // Intentionally empty
    }
}
