// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.maschine;

/**
 * The supported Maschine models.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public enum Maschine
{
    /** Maschine Mk3. */
    MK3("Maschine Mk3", true, true, true, 800),
    /** Maschine Mikro Mk3. */
    MIKRO_MK3("Maschine Mikro Mk3", false, false, false, 440);


    private final String  name;
    private final boolean hasMCUDisplay;
    private final boolean hasBankButtons;
    private final boolean hasCursorKeys;
    private final int     height;


    /**
     * Constructor.
     *
     * @param name The name of the Maschine
     * @param hasMCUDisplay DSoes it support a MCU protocol display?
     * @param hasBankButtons Does it have bank buttons?
     * @param hasCursorKeys Does the device have cursor keys?
     * @param height The height of the simulator window
     */
    private Maschine (final String name, final boolean hasMCUDisplay, final boolean hasBankButtons, final boolean hasCursorKeys, final int height)
    {
        this.name = name;
        this.hasMCUDisplay = hasMCUDisplay;
        this.hasBankButtons = hasBankButtons;
        this.hasCursorKeys = hasCursorKeys;
        this.height = height;
    }


    /**
     * Get the name of the specific Maschine.
     *
     * @return The name
     */
    public String getName ()
    {
        return this.name;
    }


    /**
     * Does it have a display that supports the MCU protocol?
     *
     * @return True if supported
     */
    public boolean hasMCUDisplay ()
    {
        return this.hasMCUDisplay;
    }


    /**
     * Does it have bank buttons?
     *
     * @return True if supported
     */
    public boolean hasBankButtons ()
    {
        return this.hasBankButtons;
    }


    /**
     * Does the device have cursor keys?
     *
     * @return True if it has cursor keys
     */
    public boolean hasCursorKeys ()
    {
        return this.hasCursorKeys;
    }


    /**
     * Get the height of the simulator window
     *
     * @return The height
     */
    public int getHeight ()
    {
        return this.height;
    }
}
