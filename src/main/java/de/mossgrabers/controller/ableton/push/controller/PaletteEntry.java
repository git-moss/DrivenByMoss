// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.controller;

/**
 * A color palette entry of the Push 2.
 *
 * @author Jürgen Moßgraber
 */
public class PaletteEntry
{
    enum State
    {
        INIT,
        READ,
        WRITE,
        OK
    }


    /** The maximum number of read/write attempts. */
    public static final int  MAX_NUMBER_OF_RETRIES  = 10;

    private static final int PALETTE_MESSAGE_OUT_ID = 0x03;
    private static final int PALETTE_MESSAGE_IN_ID  = 0x04;

    private static final int MESSAGE_LENGTH         = 17;

    private int              red                    = -1;
    private int              green                  = -1;
    private int              blue                   = -1;
    private int              white                  = -1;

    private State            state                  = State.INIT;
    private int              readRetries            = 0;
    private int              writeRetries           = 0;


    /**
     * Constructor.
     *
     * @param color The default palette color consisting of three integers for red, green and blue
     */
    public PaletteEntry (final int [] color)
    {
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
     * Increase the number of read attempts.
     */
    public void incReadRetries ()
    {
        this.readRetries++;
        this.state = State.READ;
    }


    /**
     * Increase the number of write attempts.
     */
    public void incWriteRetries ()
    {
        this.writeRetries++;
        this.state = State.WRITE;
    }


    /**
     * Set the entry to be OK, which means identical to the device.
     */
    public void setOK ()
    {
        this.state = State.OK;
    }


    /**
     * Check if the read request should be sent
     *
     * @return True if it needs to be send
     */
    public boolean requiresRead ()
    {
        return this.state == State.INIT || this.state == State.READ;
    }


    /**
     * Creates a system exclusive message which contains the current color.
     *
     * @param index The palette index where to store the color
     * @return The created message
     */
    public int [] createUpdateMessage (final int index)
    {
        final int [] data = new int [10];

        data[0] = PALETTE_MESSAGE_OUT_ID;
        data[1] = index;
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
     * Test if the maximum number of read retries to send the color to the device has already been
     * reached.
     *
     * @return True if number of retries has exceeded
     */
    public boolean hasMaxNumberOfReadRetriesReached ()
    {
        return this.readRetries > MAX_NUMBER_OF_RETRIES;
    }


    /**
     * Test if the maximum number of write retries to send the color to the device has already been
     * reached.
     *
     * @return True if number of retries has exceeded
     */
    public boolean hasMaxNumberOfWriteRetriesReached ()
    {
        return this.writeRetries > MAX_NUMBER_OF_RETRIES;
    }


    /**
     * Get the current number of attempts to read the value on the device.
     *
     * @return The number of retries
     */
    public int getReadRetries ()
    {
        return this.readRetries;
    }


    /**
     * Get the current number of attempts to update the value on the device.
     *
     * @return The number of retries
     */
    public int getWriteRetries ()
    {
        return this.writeRetries;
    }
}
