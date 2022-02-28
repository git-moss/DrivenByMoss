// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.utilities.midimonitor.controller;

import de.mossgrabers.controller.utilities.midimonitor.MidiMonitorConfiguration;
import de.mossgrabers.framework.controller.AbstractControlSurface;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * The Midi Monitor control surface.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MidiMonitorControlSurface extends AbstractControlSurface<MidiMonitorConfiguration>
{
    private static final String [] SPACES      =
    {
        "",
        " ",
        "  ",
        "   ",
        "    ",
        "     ",
        "      ",
        "       ",
        "        ",
        "         ",
        "          ",
        "           ",
        "            ",
        "             "
    };

    private static final String [] SYSEX_NAMES =
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

    private static final String [] CC_NAMES    =
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

    static final String []         MMC_NAMES   = new String [128];
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

    private boolean printHeader      = true;
    private boolean printSysexHeader = true;


    /**
     * Constructor.
     *
     * @param host The host
     * @param configuration The configuration
     * @param input The MIDI input
     */
    public MidiMonitorControlSurface (final IHost host, final MidiMonitorConfiguration configuration, final IMidiInput input)
    {
        super (host, configuration, null, null, input, null, 10, 10);

        this.input.setSysexCallback (this::handleSysEx);
    }


    /** {@inheritDoc} */
    @Override
    protected void handleMidi (final int status, final int data1, final int data2)
    {
        final int code = status & 0xF0;
        final int channel = status & 0xF;

        switch (code)
        {
            // Note off
            case 0x80:
                this.log (status, data1, data2, channel, "Note Off " + Scales.formatNoteAndOctave (data1, -2));
                break;

            // Note on
            case 0x90:
                this.log (status, data1, data2, channel, "Note On  " + Scales.formatNoteAndOctave (data1, -2));
                break;

            // Polyphonic After-touch
            case 0xA0:
                this.log (status, data1, data2, channel, "Polyphonic Key Pressure " + Scales.formatNoteAndOctave (data1, -2));
                break;

            // CC
            case 0xB0:
                this.log (status, data1, data2, channel, "CC " + CC_NAMES[data1]);
                break;

            // Program Change
            case 0xC0:
                this.log (status, data1, data2, channel, "Program Change");
                break;

            // Channel Aftertouch
            case 0xD0:
                this.log (status, data1, data2, channel, "Channel Pressure (Aftertouch)");
                break;

            // Pitch Bend
            case 0xE0:
                this.log (status, data1, data2, channel, "Pitchbend");
                break;

            // System Realtime
            case 0xF0:
                if (!this.configuration.isFilterSystemRealtimeEnabled ())
                    this.log (status, data1, data2, channel, SYSEX_NAMES[channel]);
                break;

            default:
                this.host.println ("Unhandled MIDI status: " + status);
                break;
        }
    }


    private void handleSysEx (final String dataStr)
    {
        if (this.printSysexHeader)
        {
            this.printHeader = true;
            this.printSysexHeader = false;
            this.host.println ("");
            this.host.println ("| SYSEX");
            this.host.println ("|-----------------------------------------------------------------------");
        }

        final int [] data = StringUtils.fromHexStr (dataStr);
        StringBuilder sb = new StringBuilder ("| ");
        for (int i = 0; i < data.length; i++)
        {
            sb.append (String.format ("%02X", Integer.valueOf (data[i])));

            if (i != 0 && (i + 1) % 16 == 0)
            {
                this.host.println (sb.toString ());
                sb = new StringBuilder ("| ");
            }
            else
                sb.append (' ');
        }

        if (sb.length () > 2)
        {
            if (data.length == 6 && data[0] == 0xF0 && data[1] == 0x7F && data[3] == 0x06 && data[5] == 0xF7)
                sb.append (" - MMC ").append (MMC_NAMES[data[4]]);
            this.host.println (sb.toString ());
        }
    }


    private void log (final int status, final int data1, final int data2, final int channel, final String text)
    {
        if (this.printHeader)
        {
            this.printSysexHeader = true;
            this.printHeader = false;
            this.host.println ("");
            this.host.println ("| STATUS | DATA 1 | DATA 2 | CHAN | EVENT");
            this.host.println ("|--------|--------|--------|------|-------------------------------------");
        }
        this.host.println ("| " + padNumber (status, 2, true) + " " + padNumber (status, 3, false) + " | " + padNumber (data1, 2, true) + " " + padNumber (data1, 3, false) + " | " + padNumber (data2, 2, true) + " " + padNumber (data2, 3, false) + " | " + padNumber (channel + 1, 4, false) + " | " + text);
    }


    /**
     * Format the given number as text and pad it so the string has the given length.
     *
     * @param number The number to format (as a hex number)
     * @param length The maximum length
     * @param asHex If true format as hex otherwise as decimal
     * @return The padded text
     */
    private static String padNumber (final int number, final int length, final boolean asHex)
    {
        final String text = asHex ? String.format ("%02X", Integer.valueOf (number)) : Integer.toString (number);
        final int diff = length - text.length ();
        if (diff < 0)
            return text.substring (0, length);
        if (diff > 0)
            return SPACES[diff] + text;
        return text;
    }
}