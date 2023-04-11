// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.acvs;

/**
 * The supported Akai devices which supported the ACVS protocol.
 *
 * @author Jürgen Moßgraber
 */
public enum ACVSDevice
{
    /** Akai MPC X (ACV5). */
    MPC_X("MPC X", (byte) 0x3A),
    /** Akai MPC Live I and II (ACV8). */
    MPC_LIVE_ONE("MPC Live / Live II / One", (byte) 0x3B),
    /** Akai Force (ADA2). */
    FORCE("Force", (byte) 0x40);


    private final String name;
    private final byte   id;


    /**
     * Constructor.
     *
     * @param name The name of the device
     * @param id The system exclusive ID of the device
     */
    private ACVSDevice (final String name, final byte id)
    {
        this.name = name;
        this.id = id;
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
}
