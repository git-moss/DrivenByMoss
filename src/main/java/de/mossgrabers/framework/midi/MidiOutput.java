// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.midi;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.MidiOut;


/**
 * A midi output
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MidiOutput
{
    private MidiOut port;


    /**
     * Constructor.
     *
     * @param host The host
     */
    public MidiOutput (final ControllerHost host)
    {
        this.port = host.getMidiOutPort (0);
    }


    /**
     * Send a midi CC to the output on midi channel 1.
     *
     * @param cc The CC
     * @param value The value
     */
    public void sendCC (final int cc, final int value)
    {
        this.port.sendMidi (0xB0, cc, value);
    }


    /**
     * Send a midi CC to the output.
     *
     * @param channel The midi channel
     * @param cc The CC
     * @param value The value
     */
    public void sendCCEx (final int channel, final int cc, final int value)
    {
        this.port.sendMidi (0xB0 + channel, cc, value);
    }


    /**
     * Send a midi note to the output on midi channel 1.
     *
     * @param note The note
     * @param velocity The velocity
     */
    public void sendNote (final int note, final int velocity)
    {
        this.port.sendMidi (0x90, note, velocity);
    }


    /**
     * Send a midi note to the output.
     *
     * @param channel The midi channel
     * @param note The note
     * @param velocity The velocity
     */
    public void sendNoteEx (final int channel, final int note, final int velocity)
    {
        this.port.sendMidi (0x90 + channel, note, velocity);
    }


    /**
     * Send pitchbend to the output on midi channel 1.
     *
     * @param data1 First data byte
     * @param data2 Second data byte
     */
    public void sendPitchbend (final int data1, final int data2)
    {
        this.port.sendMidi (0xE0, data1, data2);
    }


    /**
     * Send a system exclusive message to the output.
     *
     * @param data The data to send
     */
    public void sendSysex (final byte [] data)
    {
        this.port.sendSysex (data);
    }


    /**
     * Send a system exclusive message to the output.
     *
     * @param data The data to send, formatted as a hex string, e.g. F0 7E 7F 06 01 F7
     */
    public void sendSysex (final String data)
    {
        this.port.sendSysex (data);
    }


    /**
     * Sends an identity request to the controller which is connected to this output.
     */
    public void sendIdentityRequest ()
    {
        this.sendSysex ("F0 7E 7F 06 01 F7");
    }


    /**
     * Convert the bytes to a hex string
     *
     * @param data The data to convert
     * @return The hex string
     */
    public static String toHexStr (final int [] data)
    {
        final StringBuilder sysex = new StringBuilder ();
        for (final int d: data)
            sysex.append (toHexStr (d)).append (' ');
        return sysex.toString ();
    }


    /**
     * Convert the byte to a hex string
     *
     * @param number The value to convert
     * @return The hex string
     */
    public static String toHexStr (final int number)
    {
        final String v = Integer.toHexString (number).toUpperCase ();
        return v.length () < 2 ? '0' + v : v;
    }
}