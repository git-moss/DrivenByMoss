// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.osc;

import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.clip.IClip;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.scale.Scales;


/**
 * Abstract base class for handling OSC messages.
 *
 * @author Jürgen Moßgraber
 */
public abstract class AbstractOpenSoundControlParser implements IOpenSoundControlCallback
{
    protected final IHost                          host;
    protected final IModel                         model;
    protected final ITransport                     transport;
    protected final IMasterTrack                   masterTrack;
    protected final IClip                          clip;

    protected final Scales                         scales;
    protected final IMidiInput                     midiInput;
    protected final IOpenSoundControlConfiguration configuration;
    protected final IOpenSoundControlWriter        writer;


    /**
     * Constructor.
     *
     * @param host The host
     * @param model The model
     * @param midiInput The MIDI input
     * @param configuration The configuration
     * @param writer The writer
     */
    protected AbstractOpenSoundControlParser (final IHost host, final IModel model, final IMidiInput midiInput, final IOpenSoundControlConfiguration configuration, final IOpenSoundControlWriter writer)
    {
        this.host = host;
        this.model = model;
        this.transport = model.getTransport ();
        this.masterTrack = model.getMasterTrack ();
        this.scales = model.getScales ();
        this.clip = model.getNoteClip (8, 128);

        this.midiInput = midiInput;
        this.configuration = configuration;
        this.writer = writer;
    }


    /**
     * Log a OSC message.
     *
     * @param message The message to log
     */
    protected void logMessage (final IOpenSoundControlMessage message)
    {
        if (!this.configuration.shouldLogInputCommands ())
            return;

        final String address = message.getAddress ();
        if (this.configuration.filterHeartbeatMessages () && this.isHeartbeatMessage (address))
            return;

        final StringBuilder sb = new StringBuilder ("Receiving: ").append (address).append (" [ ");
        final Object [] values = message.getValues ();
        for (int i = 0; i < values.length; i++)
        {
            if (i > 0)
                sb.append (", ");
            sb.append (values[i]);
        }
        sb.append (" ]");
        this.model.getHost ().println (sb.toString ());
    }


    /**
     * Hook to ignore specific messages from logging.
     *
     * @param address The OSC address
     * @return Return true to ignore the message
     */
    protected boolean isHeartbeatMessage (final String address)
    {
        return false;
    }
}
