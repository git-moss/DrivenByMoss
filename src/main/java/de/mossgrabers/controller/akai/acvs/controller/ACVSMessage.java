// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.acvs.controller;

import de.mossgrabers.controller.akai.acvs.ACVSDevice;


/**
 * A system exclusive message received from an ACVS device.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ACVSMessage
{
    private final ACVSDevice acvsDevice;
    private final int        messageID;
    private final int []     content;


    /**
     * Constructor.
     *
     * @param acvsDevice The ACVS device
     * @param messageID The ID of the message
     * @param content The data content
     */
    public ACVSMessage (final ACVSDevice acvsDevice, final int messageID, final int [] content)
    {
        this.acvsDevice = acvsDevice;
        this.messageID = messageID;
        this.content = content;
    }


    /**
     * Get the ID of the ACVS device which send the message
     *
     * @return The ID
     */
    public ACVSDevice getACVSDevice ()
    {
        return this.acvsDevice;
    }


    /**
     * Get the ID (type) of the message.
     *
     * @return The ID
     */
    public int getMessageID ()
    {
        return this.messageID;
    }


    /**
     * Get the data content of the message.
     *
     * @return The content
     */
    public int [] getContent ()
    {
        return this.content;
    }
}
