// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.osc;

import de.mossgrabers.framework.osc.IOpenSoundControlServer;

// Requires API 9
// import com.bitwig.extension.api.opensoundcontrol.OscServer;

import java.io.IOException;


/**
 * Implementation of an OSC server.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class OpenSoundControlServerImpl implements IOpenSoundControlServer
{
    // TODO Requires API 9
    // private final OscServer server;

    /**
     * Constructor.
     *
     * @par am server The Bitwig OSC server implementation
     */
    public OpenSoundControlServerImpl (/* final OscServer server */)
    {
        // TODO Requires API 9
        // this.server = server;
    }


    /** {@inheritDoc} */
    @Override
    public void start (final int port) throws IOException
    {
        // TODO Requires API 9
        // this.server.start (port);
    }
}
