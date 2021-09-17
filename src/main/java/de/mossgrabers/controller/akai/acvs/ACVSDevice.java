// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.acvs;

import java.util.UUID;


/**
 * The supported Akai devices which supported the ACVS protocol.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public enum ACVSDevice
{
    /** Akai MPC X (ACV5). */
    MPC_X("MPC X", (byte) 0x3A, "71B4382F-F3B3-4CA0-A2A0-9A185120C975"),
    /** Akai MPC Live I and II (ACV8). */
    MPC_LIVE("MPC Live I / II", (byte) 0x3B, "CE97B67C-FFB7-4309-AFF2-45193C0C87A3"),
    /** Akai Force (ADA2). */
    FORCE("Force", (byte) 0x40, "18102024-BEDA-4B32-B964-980796EF31B4");


    private final String name;
    private final byte   id;
    private final UUID   uuid;


    /**
     * Constructor.
     *
     * @param name The name of the device
     * @param id The system exclusive ID of the device
     * @param uuid The UUID to assigned to the device for the definition
     */
    private ACVSDevice (final String name, final byte id, final String uuid)
    {
        this.name = name;
        this.id = id;
        this.uuid = UUID.fromString (uuid);
    }


    /**
     * Get the name of the specific device.
     *
     * @return The name
     */
    public String getName ()
    {
        return this.name;
    }


    /**
     * Get the system exclusive ID of the device
     *
     * @return the id
     */
    public byte getId ()
    {
        return this.id;
    }


    /**
     * Get the UUID.
     *
     * @return The UUID
     */
    public UUID getUuid ()
    {
        return this.uuid;
    }
}
