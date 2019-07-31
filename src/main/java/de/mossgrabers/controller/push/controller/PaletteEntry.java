// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.controller;

/**
 * A color palette entry of the Push 2.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PaletteEntry
{
    private static final int PALETTE_MESSAGE_OUT_ID = 0x03;
    private static final int PALETTE_MESSAGE_IN_ID  = 0x04;

    private static final int MESSAGE_LENGTH         = 17;
    private static final int MAX_NUMBER_OF_RETRIES  = 10;

    private int              red                    = -1;
    private int              green                  = -1;
    private int              blue                   = -1;
    private int              white                  = -1;

    private int              retries                = 0;


    /**
     * Constructor.
     *
     * @param data The SysEx data of a received color palette entry. Must be 17 characters long.
     */
    public PaletteEntry (final int [] data)
    {
        this.red = data[8] + (data[9] << 7);
        this.green = data[10] + (data[11] << 7);
        this.blue = data[12] + (data[13] << 7);
        this.white = data[14] + (data[15] << 7);
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
     * Update the color data in this object and increases the number of retries. Does not change the
     * white value.
     *
     * @param color The color consisting of three integers for red, green and blue
     * @return True if the given color is different than the color already stored in this object
     */
    public boolean update (final int [] color)
    {
        if (color[0] == this.red && color[1] == this.green && color[2] == this.blue)
            return false;

        this.red = color[0];
        this.green = color[1];
        this.blue = color[2];

        this.retries++;

        return true;
    }


    /**
     * Creates a sysex message which contains the current color.
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
     * Test if the maximum number of retries to send the color to the device has already been
     * reached.
     *
     * @return True if number of retries has exceeded
     */
    public boolean hasMaxNumberOfRetriesReached ()
    {
        return this.retries > MAX_NUMBER_OF_RETRIES;
    }


    /**
     * Get the current numner of attempts to update the value on the device.
     *
     * @return The number of retries
     */
    public int getRetries ()
    {
        return this.retries;
    }
}
