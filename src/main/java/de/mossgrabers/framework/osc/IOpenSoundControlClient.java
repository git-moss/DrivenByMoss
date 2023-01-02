// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.osc;

import java.io.IOException;
import java.util.List;


/**
 * Interface for sending messages to an OSC server.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IOpenSoundControlClient
{
    /**
     * Send a message to the server.
     *
     * @param message The message to send
     * @throws IOException Could not send the message
     */
    void sendMessage (IOpenSoundControlMessage message) throws IOException;


    /**
     * Send several messages to the server as an OSC bundle.
     *
     * @param messages The messages to send
     * @throws IOException Could not send the messages
     */
    void sendBundle (List<IOpenSoundControlMessage> messages) throws IOException;
}
