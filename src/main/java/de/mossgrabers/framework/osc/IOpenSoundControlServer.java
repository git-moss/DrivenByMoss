// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.osc;

import java.io.IOException;


/**
 * Interface for sending messages to an OSC server.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IOpenSoundControlServer
{
    /**
     * Starts the server on the given port. If it was already running it is restarted.
     *
     * @param port The port to start the server on
     * @throws IOException Could not start the server
     */
    void start (int port) throws IOException;
}
