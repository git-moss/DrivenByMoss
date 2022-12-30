// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.utilities.midimonitor.controller;

import de.mossgrabers.controller.utilities.midimonitor.MidiMonitorConfiguration;
import de.mossgrabers.framework.controller.AbstractControlSurface;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.MidiConstants;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * The Midi Monitor control surface.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MidiMonitorControlSurface extends AbstractControlSurface<MidiMonitorConfiguration>
{
    private static final String [] SPACES           =
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

    private boolean                printHeader      = true;
    private boolean                printSysexHeader = true;
    private int                    sysexLengthCount = 0;


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
            case MidiConstants.CMD_NOTE_OFF:
                this.log (status, data1, data2, channel, "Note Off " + Scales.formatNoteAndOctave (data1, -2));
                break;

            case MidiConstants.CMD_NOTE_ON:
                this.log (status, data1, data2, channel, "Note On  " + Scales.formatNoteAndOctave (data1, -2));
                break;

            case MidiConstants.CMD_POLY_AFTERTOUCH:
                this.log (status, data1, data2, channel, "Polyphonic Key Pressure " + Scales.formatNoteAndOctave (data1, -2));
                break;

            case MidiConstants.CMD_CC:
                this.log (status, data1, data2, channel, "CC " + MidiConstants.getCCNames ()[data1]);
                break;

            case MidiConstants.CMD_PROGRAM_CHANGE:
                this.log (status, data1, data2, channel, "Program Change");
                break;

            case MidiConstants.CMD_CHANNEL_AFTERTOUCH:
                this.log (status, data1, data2, channel, "Channel Pressure (Aftertouch)");
                break;

            case MidiConstants.CMD_PITCHBEND:
                this.log (status, data1, data2, channel, "Pitchbend");
                break;

            case MidiConstants.CMD_SYSTEM:
                if (!this.configuration.isFilterSystemRealtimeEnabled ())
                    this.log (status, data1, data2, channel, MidiConstants.getSysexNames ()[channel]);
                break;

            default:
                this.host.println ("Unhandled MIDI status: " + status);
                break;
        }
    }


    /**
     * Handle MIDI system exclusive messages. Note: this can be called multiple times in chunks of
     * 1024 bytes!
     *
     * @param dataStr The data formatted as hexadecimal numbers
     */
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
        if (data.length == 0)
            return;

        if (data[0] == 0xF0)
            this.sysexLengthCount = 0;
        this.sysexLengthCount += data.length;

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

        if (data[data.length - 1] == 0xF7)
        {
            if (this.sysexLengthCount == data.length && data.length == 6 && data[0] == 0xF0 && data[1] == 0x7F && data[3] == 0x06 && data[5] == 0xF7)
                sb.append (" - MMC ").append (MidiConstants.getMMCNames ()[data[4]]);
            else
                sb.append ("(" + this.sysexLengthCount + " bytes)");
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