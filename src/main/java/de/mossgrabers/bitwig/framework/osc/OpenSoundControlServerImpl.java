// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.osc;

import de.mossgrabers.framework.osc.IOpenSoundControlMessage;
import de.mossgrabers.framework.osc.IOpenSoundControlServer;

import com.bitwig.extension.api.opensoundcontrol.OscConnection;

import java.io.IOException;
import java.util.List;


/**
 * Implementation of a OSC server connection.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class OpenSoundControlServerImpl implements IOpenSoundControlServer
{
    private OscConnection connection;


    /**
     * Constructor.
     *
     * @param connection The OSC connection
     */
    public OpenSoundControlServerImpl (final OscConnection connection)
    {
        this.connection = connection;
    }


    /** {@inheritDoc} */
    @Override
    public void sendMessage (final IOpenSoundControlMessage message) throws IOException
    {
        final String address = message.getAddress ();
        final Object [] values = message.getValues ();
        this.connection.sendMessage (address, values);
    }


    /** {@inheritDoc} */
    @Override
    public void sendBundle (final List<IOpenSoundControlMessage> messages) throws IOException
    {
        int pos = 0;
        this.connection.startBundle ();
        for (final IOpenSoundControlMessage message: messages)
        {
            this.sendMessage (message);
            pos++;
            // We cannot get the exact size of the message due to the API, so let's try to stay
            // below 64K, which is the maximum of an UDP message
            if (pos > 100)
            {
                pos = 0;
                this.connection.endBundle ();
                this.connection.startBundle ();
            }
        }
        this.connection.endBundle ();
    }
}
