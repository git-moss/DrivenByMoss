// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.midi;

/**
 * A MIDI device inquiry.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DeviceInquiry
{
    private static final byte [] INQUIRY = new byte []
    {
        (byte) 0xF0,
        (byte) 0x7E,
        (byte) 0x7F,                                  // Ignore device ID
        (byte) 0x06,
        (byte) 0x01,
        (byte) 0xF7
    };

    private boolean              isResult;
    private final int []         data;


    /**
     * Constructor.
     *
     * @param data The data of a device inquiry response
     */
    public DeviceInquiry (final int [] data)
    {
        this.isResult = data.length >= 15;
        if (data[0] != 0xF0 || data[1] != 0x7E || data[3] != 0x06 || data[4] != 0x02 || data[data.length - 1] != 0xF7)
            this.isResult = false;
        this.data = this.isResult ? data : null;
    }


    /**
     * Test if the given data is a valid device inquiry response.
     *
     * @return True if it is a valid device inquiry response
     */
    public boolean isValid ()
    {
        return this.isResult;
    }


    /**
     * Get the device ID.
     *
     * @return The device ID
     */
    public int getDeviceID ()
    {
        return this.data == null ? -1 : this.data[2];
    }


    /**
     * Get the manufacturers system exclusive id code.
     *
     * @return The manufacturers system exclusive id code.
     */
    public int getManufacturer ()
    {
        if (this.data == null)
            return -1;
        return this.data[5];
    }


    /**
     * Get the software revision level. The format and length is device specific.
     *
     * @return The Software revision level
     */
    public int [] getRevisionLevel ()
    {
        if (this.data == null)
            return new int [0];

        final int start = this.getManufacturer () == 0 ? 12 : 10;
        final int end = this.data.length - 1;
        final int length = end - start;
        final int [] softwareData = new int [length];
        System.arraycopy (this.data, start, softwareData, 0, length);
        return softwareData;
    }


    /**
     * Create inquiry sysex message. Ignores the device ID.
     *
     * @return The sysex query
     */
    public static byte [] createQuery ()
    {
        return INQUIRY;
    }
}
