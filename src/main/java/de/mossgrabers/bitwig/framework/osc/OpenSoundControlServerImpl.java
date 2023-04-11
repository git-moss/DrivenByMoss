// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.osc;

import de.mossgrabers.framework.osc.IOpenSoundControlServer;

import com.bitwig.extension.api.opensoundcontrol.OscServer;

import java.io.IOException;


/**
 * Implementation of an OSC server.
 *
 * @author Jürgen Moßgraber
 */
public class OpenSoundControlServerImpl implements IOpenSoundControlServer
{
    private final OscServer server;


    /**
     * Constructor.
     *
     * @param server The Bitwig OSC server implementation
     */
    public OpenSoundControlServerImpl (final OscServer server)
    {
        this.server = server;
    }


    /** {@inheritDoc} */
    @Override
    public void start (final int port) throws IOException
    {
        this.server.start (port);
    }
}
