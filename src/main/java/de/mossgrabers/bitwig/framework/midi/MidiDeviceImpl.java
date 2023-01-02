// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.midi;

import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;

import com.bitwig.extension.controller.api.ControllerHost;


/**
 * Bitwig implementation to access MIDI input and outputs.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MidiDeviceImpl implements IMidiAccess
{
    private final ControllerHost host;


    /**
     * Constructor.
     *
     * @param host The Bitwig host
     */
    public MidiDeviceImpl (final ControllerHost host)
    {
        this.host = host;
    }


    /** {@inheritDoc} */
    @Override
    public IMidiOutput createOutput ()
    {
        return new MidiOutputImpl (this.host);
    }


    /** {@inheritDoc} */
    @Override
    public IMidiOutput createOutput (final int index)
    {
        return new MidiOutputImpl (this.host, index);
    }


    /** {@inheritDoc} */
    @Override
    public IMidiInput createInput (final String name, final String... filters)
    {
        return this.createInput (0, name, filters);
    }


    /** {@inheritDoc} */
    @Override
    public IMidiInput createInput (final int index, final String name, final String... filters)
    {
        return new MidiInputImpl (index, this.host, name, filters);
    }
}
