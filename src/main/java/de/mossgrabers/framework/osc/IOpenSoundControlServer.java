// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.osc;

import java.io.IOException;


/**
 * Interface for sending messages to an OSC server.
 *
 * @author Jürgen Moßgraber
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


    /**
     * Returns the port on which the OSC server is listening.
     *
     * @return The port or -1 if the server has not been started
     */
    int getListeningPort ();
}
