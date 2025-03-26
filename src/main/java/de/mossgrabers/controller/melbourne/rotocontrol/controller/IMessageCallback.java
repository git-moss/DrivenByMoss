// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.melbourne.rotocontrol.controller;

/**
 * Callback for all commands received via system exclusive.
 *
 * @author Jürgen Moßgraber
 */
public interface IMessageCallback
{
    /**
     * Handle a message.
     *
     * @param rotoControlMessage The message to handle
     */
    void handle (RotoControlMessage rotoControlMessage);


    /**
     * Sends the state of all transport buttons to the ROTO Control.
     */
    void updateTransportStatus ();
}
