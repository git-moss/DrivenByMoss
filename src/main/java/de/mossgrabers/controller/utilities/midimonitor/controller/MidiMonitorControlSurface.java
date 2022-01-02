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
    private static final String    GENERAL_PURPOSE = "General Purpose";

    private static final String [] SPACES          =
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

    static final String []         CC_NAMES        =
    {
        "Bank Select",
        "Modulation",
        "Breath Controller",
        "Deferred Play",
        "Foot Controller",
        "Portamento Time",
        "Data Entry MSB",
        "Volume",
        "Balance",
        "Play Pause",
        "Pan",
        "Expression",
        "Effect Controller 1",
        "Effect Controller 2",
        "14",
        "15",
        "16",
        "17",
        "18",
        "19",
        "20",
        "21",
        "22",
        "23",
        "24",
        "25",
        "26",
        "27",
        "28",
        "29",
        "30",
        "31",
        "32",
        "33",
        "34",
        "35",
        "36",
        "37",
        "38",
        "39",
        "30",
        "41",
        "42",
        "43",
        "44",
        "45",
        "46",
        "47",
        "48",
        "49",
        "50",
        "51",
        "52",
        "53",
        "54",
        "55",
        "56",
        "57",
        "58",
        "59",
        "60",
        "61",
        "62",
        "63",
        "Damper Pedal",
        "Portamento On/Off Switch",
        "Sostenuto On/Off Switch",
        "Soft Pedal On/Off Switch",
        "Legato Footswitch",
        "Hold 2",
        "Sound Controller 1",
        "Sound Controller 2",
        "Sound Controller 3",
        "Sound Controller 4",
        "Sound Controller 5",
        "Sound Controller 6",
        "Sound Controller 7",
        "Sound Controller 8",
        "Sound Controller 9",
        "Sound Controller 10",
        GENERAL_PURPOSE,
        GENERAL_PURPOSE,
        GENERAL_PURPOSE,
        GENERAL_PURPOSE,
        "Portamento",
        "85",
        "86",
        "87",
        "88",
        "89",
        "90",
        "Effect 1 Depth",
        "Effect 2 Depth",
        "Effect 3 Depth",
        "Effect 4 Depth",
        "Effect 5 Depth",
        "(+1) Data Increment",
        "(-1) Data Decrement",
        "NRPN LSB",
        "NRPN MSB",
        "RPN LSB",
        "RPN MSB",
        "102",
        "103",
        "104",
        "105",
        "106",
        "107",
        "108",
        "109",
        "110",
        "111",
        "112",
        "113",
        "114",
        "115",
        "116",
        "117",
        "118",
        "119",
        "All Sound Off",
        "Reset All Controllers",
        "Local On/Off Switch",
        "All Notes Off",
        "Omni Mode Off",
        "Omni Mode On",
        "Mono Mode",
        "Poly Mode"
    };

    static final String []         MMC_NAMES       = new String [128];
    static
    {
        MMC_NAMES[0x00] = "Reserved for extensions";
        MMC_NAMES[0x01] = "Stop";
        MMC_NAMES[0x02] = "Play ";
        MMC_NAMES[0x03] = "Deferred play ";
        MMC_NAMES[0x04] = "Fast forward ";
        MMC_NAMES[0x05] = "Rewind ";
        MMC_NAMES[0x06] = "Record strobe";
        MMC_NAMES[0x07] = "Recordexit";
        MMC_NAMES[0x08] = "Record pause";
        MMC_NAMES[0x09] = "Pause ";
        MMC_NAMES[0x0a] = "Eject ";
        MMC_NAMES[0x0b] = "Chase";
        MMC_NAMES[0x0c] = "Command error reset";
        MMC_NAMES[0x0d] = "MMC reset";
        MMC_NAMES[0x40] = "Write";
        MMC_NAMES[0x41] = "Masked write";
        MMC_NAMES[0x42] = "Read";
        MMC_NAMES[0x43] = "Update";
        MMC_NAMES[0x44] = "Locate";
        MMC_NAMES[0x45] = "Variable play ";
        MMC_NAMES[0x46] = "Search ";
        MMC_NAMES[0x47] = "Shuttle ";
        MMC_NAMES[0x48] = "Step ";
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
        MMC_NAMES[0x54] = "Deferred variable play ";
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

            // Channel After-touch
            case 0xD0:
                this.log (status, data1, data2, channel, "Channel Pressure (Aftertouch)");
                break;

            // Pitch Bend
            case 0xE0:
                this.log (status, data1, data2, channel, "Pitchbend");
                break;

            // System Realtime - Active Sense
            case 0xF0:
                if (!this.configuration.isFilterSystemRealtimeEnabled ())
                {
                    if (channel == 14)
                        this.log (status, data1, data2, channel, "System Realtime - Active Sense");
                    else
                        this.log (status, data1, data2, channel, "System Realtime");
                }
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