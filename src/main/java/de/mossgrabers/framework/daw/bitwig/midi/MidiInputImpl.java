// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.bitwig.midi;

import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.MidiShortCallback;
import de.mossgrabers.framework.daw.midi.MidiSysExCallback;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.MidiIn;
import com.bitwig.extension.controller.api.NoteInput;


/**
 * A midi input.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
class MidiInputImpl implements IMidiInput
{
    private final int portNumber;
    private MidiIn    port;
    private NoteInput noteInput;


    /**
     * Default constructor. Uses port number 0.
     */
    public MidiInputImpl ()
    {
        this (0);
    }


    /**
     * Constructor.
     *
     * @param portNumber The number of the midi input port
     */
    public MidiInputImpl (final int portNumber)
    {
        this.portNumber = portNumber;
    }


    /**
     * Initialise the input.
     *
     * @param host The host
     */
    public void init (final ControllerHost host)
    {
        this.port = host.getMidiInPort (this.portNumber);
    }


    /**
     * Create a note input.
     *
     * @param name the name of the note input as it appears in the track input choosers in Bitwig
     *            Studio
     * @param filters a filter string formatted as hexadecimal value with `?` as wildcard. For
     *            example `80????` would match note-off on channel 1 (0). When this parameter is
     *            {@null}, a standard filter will be used to forward note-related messages on
     *            channel 1 (0).
     */
    protected void createNoteInputBase (final String name, final String... filters)
    {
        this.noteInput = this.port.createNoteInput (name, filters);
        this.noteInput.setShouldConsumeEvents (false);
    }


    /** {@inheritDoc} */
    @Override
    public void createNoteInput (final String name, final String... filters)
    {
        final NoteInput ni = this.port.createNoteInput (name, filters);
        ni.setShouldConsumeEvents (false);
    }


    /** {@inheritDoc} */
    @Override
    public void setMidiCallback (final MidiShortCallback callback)
    {
        this.port.setMidiCallback (callback::handleMidi);
    }


    /** {@inheritDoc} */
    @Override
    public void setSysexCallback (final MidiSysExCallback callback)
    {
        this.port.setSysexCallback (callback::handleMidi);
    }


    /** {@inheritDoc} */
    @Override
    public void setKeyTranslationTable (final Object [] table)
    {
        if (this.noteInput != null)
            this.noteInput.setKeyTranslationTable (table);
    }


    /** {@inheritDoc} */
    @Override
    public void setVelocityTranslationTable (final Object [] table)
    {
        if (this.noteInput != null)
            this.noteInput.setVelocityTranslationTable (table);
    }


    /** {@inheritDoc} */
    @Override
    public void sendRawMidiEvent (final int status, final int data1, final int data2)
    {
        if (this.noteInput != null)
            this.noteInput.sendRawMidiEvent (status, data1, data2);
    }
}
