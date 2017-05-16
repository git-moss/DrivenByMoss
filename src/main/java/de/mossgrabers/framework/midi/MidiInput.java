// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.midi;

import com.bitwig.extension.callback.ShortMidiDataReceivedCallback;
import com.bitwig.extension.callback.SysexMidiDataReceivedCallback;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.MidiIn;
import com.bitwig.extension.controller.api.NoteInput;


/**
 * A midi input.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class MidiInput
{
    private final int portNumber;
    private MidiIn    port;


    /**
     * Default constructor. Uses port number 0.
     */
    public MidiInput ()
    {
        this (0);
    }


    /**
     * Constructor.
     *
     * @param portNumber The number of the midi input port
     */
    public MidiInput (final int portNumber)
    {
        this.portNumber = portNumber;
    }


    /**
     * Create a note input. Use createNoteInputBase in the implementation.
     *
     * @return The note input
     */
    public abstract NoteInput createNoteInput ();


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
     * Set a callback for midi short messages coming from this input.
     *
     * @param callback The callback
     */
    public void setMidiCallback (final ShortMidiDataReceivedCallback callback)
    {
        this.port.setMidiCallback (callback);
    }


    /**
     * Set a callback for midi system exclusive messages coming from this input.
     *
     * @param callback The callback
     */
    public void setSysexCallback (final SysexMidiDataReceivedCallback callback)
    {
        this.port.setSysexCallback (callback);
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
     * @return the object representing the requested note input
     */
    protected NoteInput createNoteInputBase (final String name, final String... filters)
    {
        final NoteInput noteInput = this.port.createNoteInput (name, filters);
        noteInput.setShouldConsumeEvents (false);
        return noteInput;
    }
}
