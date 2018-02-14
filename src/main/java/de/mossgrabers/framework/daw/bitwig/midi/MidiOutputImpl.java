// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.bitwig.midi;

import de.mossgrabers.framework.daw.midi.IMidiOutput;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.MidiOut;


/**
 * A midi output
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
class MidiOutputImpl implements IMidiOutput
{
    private MidiOut port;


    /**
     * Constructor.
     *
     * @param host The host
     */
    public MidiOutputImpl (final ControllerHost host)
    {
        this (host, 0);
    }


    /**
     * Constructor.
     *
     * @param host The host
     * @param portNumber The number of the midi output port
     */
    public MidiOutputImpl (final ControllerHost host, final int portNumber)
    {
        this.port = host.getMidiOutPort (portNumber);
    }


    /** {@inheritDoc} */
    @Override
    public void sendCC (final int cc, final int value)
    {
        this.port.sendMidi (0xB0, cc, value);
    }


    /** {@inheritDoc} */
    @Override
    public void sendCCEx (final int channel, final int cc, final int value)
    {
        this.port.sendMidi (0xB0 + channel, cc, value);
    }


    /** {@inheritDoc} */
    @Override
    public void sendNote (final int note, final int velocity)
    {
        this.port.sendMidi (0x90, note, velocity);
    }


    /** {@inheritDoc} */
    @Override
    public void sendNoteEx (final int channel, final int note, final int velocity)
    {
        this.port.sendMidi (0x90 + channel, note, velocity);
    }


    /** {@inheritDoc} */
    @Override
    public void sendChannelAftertouch (final int data1, final int data2)
    {
        this.port.sendMidi (0xD0, data1, data2);
    }


    /** {@inheritDoc} */
    @Override
    public void sendChannelAftertouch (final int channel, final int data1, final int data2)
    {
        this.port.sendMidi (0xD0 + channel, data1, data2);
    }


    /** {@inheritDoc} */
    @Override
    public void sendPitchbend (final int data1, final int data2)
    {
        this.port.sendMidi (0xE0, data1, data2);
    }


    /** {@inheritDoc} */
    @Override
    public void sendPitchbend (final int channel, final int data1, final int data2)
    {
        this.port.sendMidi (0xE0 + channel, data1, data2);
    }


    /** {@inheritDoc} */
    @Override
    public void sendSysex (final byte [] data)
    {
        this.port.sendSysex (data);
    }


    /** {@inheritDoc} */
    @Override
    public void sendSysex (final String data)
    {
        this.port.sendSysex (data);
    }


    /** {@inheritDoc} */
    @Override
    public void sendIdentityRequest ()
    {
        this.sendSysex ("F0 7E 7F 06 01 F7");
    }
}