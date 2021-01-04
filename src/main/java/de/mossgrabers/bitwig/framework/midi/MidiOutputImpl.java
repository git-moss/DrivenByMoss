// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.midi;

import de.mossgrabers.framework.daw.midi.AbstractMidiOutputImpl;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.MidiOut;


/**
 * A midi output
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
class MidiOutputImpl extends AbstractMidiOutputImpl
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
    protected void sendMidiShort (final int status, final int data1, final int data2)
    {
        this.port.sendMidi (status, data1, data2);
    }
}