// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.controller;

/**
 * A color palette entry of the Push 2/3.
 *
 * @author Jürgen Moßgraber
 */
public class ColorPaletteEntry
{
    static enum State
    {
        READ,
        READ_REQUESTED,
        WRITE,
        DONE
    }


    /** The maximum number of read/write attempts. */
    public static final int  MAX_NUMBER_OF_RETRIES  = 10;

    private static final int PALETTE_MESSAGE_OUT_ID = 0x03;
    private static final int PALETTE_MESSAGE_IN_ID  = 0x04;

    private static final int MESSAGE_LENGTH         = 17;

    private final int        index;
    private final int        red;
    private final int        green;
    private final int        blue;
    private int              white;

    private State            state                  = State.READ;
    private int              readRetries            = 0;
    private int              writeRetries           = 0;
    private long             sendTimestamp;


    /**
     * Constructor.
     *
     * @param index The index of the entry
     * @param color The default palette color consisting of three integers for red, green and blue
     */
    public ColorPaletteEntry (final int index, final int [] color)
    {
        this.index = index;
        this.red = color[0];
        this.green = color[1];
        this.blue = color[2];
    }


    /**
     * Test if the given data is a valid palette entry message.
     *
     * @param data The data to test
     * @return True if valid
     */
    public static boolean isValid (final int [] data)
    {
        return data.length == MESSAGE_LENGTH && data[6] == PALETTE_MESSAGE_IN_ID;
    }


    /**
     * Test if the received data is the same as the already stored one.
     *
     * @param data The SysEx data of a received color palette entry. Must be 17 characters long.
     * @return True if the given color is different than the color already stored in this object
     */
    public boolean requiresUpdate (final int [] data)
    {
        this.white = data[14] + (data[15] << 7);
        return this.red != data[8] + (data[9] << 7) || this.green != data[10] + (data[11] << 7) || this.blue != data[12] + (data[13] << 7);
    }


    /**
     * Increases the read request for this palette entry.
     *
     * @return True if another attempt is allowed, false if the maximum number of retries has been
     *         reached
     */
    public boolean incReadRetries ()
    {
        this.readRetries++;
        if (this.readRetries > MAX_NUMBER_OF_RETRIES)
        {
            this.state = State.DONE;
            return false;
        }

        this.state = State.READ_REQUESTED;
        this.sendTimestamp = System.currentTimeMillis ();
        return true;
    }


    /**
     * Increases the write request for this palette entry.
     *
     * @return True if another attempt is allowed, false if the maximum number of retries has been
     *         reached
     */
    public boolean incWriteRetries ()
    {
        this.writeRetries++;

        if (this.writeRetries > MAX_NUMBER_OF_RETRIES)
        {
            this.state = State.DONE;
            return false;
        }

        // Set to read for confirmation check
        this.state = State.READ;
        this.sendTimestamp = System.currentTimeMillis ();
        return true;
    }


    /**
     * Sets the state to WRITE.
     */
    public void setWrite ()
    {
        this.state = State.WRITE;
    }


    /**
     * Set the entry to be OK, which means identical to the device.
     */
    public void setDone ()
    {
        this.state = State.DONE;
    }


    /**
     * Get the state.
     *
     * @return The state
     */
    public State getState ()
    {
        return this.state;
    }


    /**
     * Creates a system exclusive message which contains the current color.
     *
     * @return The created message
     */
    public int [] createUpdateMessage ()
    {
        final int [] data = new int [10];
        data[0] = PALETTE_MESSAGE_OUT_ID;
        data[1] = this.index;
        data[2] = this.red % 128;
        data[3] = this.red / 128;
        data[4] = this.green % 128;
        data[5] = this.green / 128;
        data[6] = this.blue % 128;
        data[7] = this.blue / 128;
        data[8] = this.white % 128;
        data[9] = this.white / 128;
        return data;
    }


    /**
     * Get the time in milliseconds when the read/write request was sent.
     *
     * @return the sendTimestamp The milliseconds
     */
    public long getSendTimestamp ()
    {
        return this.sendTimestamp;
    }
}
