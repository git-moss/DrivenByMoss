// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.midi;

/**
 * A MIDI device inquiry as defined by the MIDI 1.0 specification page 40. The inquiry string is as
 * follows <pre>F0 7E &lt;device ID&gt; 06 01 F7</pre> The response is
 * <pre>F0 7E &lt;device ID&gt; 06 02 mm ff ff dd dd ss ss ss ss F7</pre>
 * <ul>
 * <li>F0 7E &lt;device ID&gt; - Universal System Exclusive Non-real time header
 * <li>06 - General Information (sub-ID#1)
 * <li>02 - Identity Reply (sub-ID#2)
 * <li>mm - Manufacturers System Exclusive id code
 * <li>ff ff - Device family code (14 bits, LSB first)
 * <li>dd dd - Device family member code (14 bits, LSB first)
 * <li>ss ss ss ss - Software revision level. Format device specific
 * <li>F7 EOX
 * </ul>
 * Note that if the manufacturers id code (mm) begins with 00H then the above message is extended by
 * two bytes to handle the additional manufacturers id code.
 *
 * @author Jürgen Moßgraber
 */
public class DeviceInquiry
{
    private static final int     LENGTH_RESULT_SHORT      = 15;
    private static final int     LENGTH_RESULT_LONG       = 17;
    private static final int     LENGTH_DEVICE_FAMILY     = 4;
    private static final int     LENGTH_SOFTWARE_REVISION = 4;

    private static final int     OFFSET_DEVICE_ID         = 2;
    private static final int     OFFSET_MANUFACTURER_ID   = 5;
    private static final int     OFFSET_CONTENT_SHORT     = 6;
    private static final int     OFFSET_CONTENT_LONG      = 8;

    private static final byte [] INQUIRY                  = new byte []
    {
        (byte) 0xF0,
        (byte) 0x7E,
        (byte) 0x7F,                                                   // Ignore device ID
        (byte) 0x06,
        (byte) 0x01,
        (byte) 0xF7
    };


    /** Possible results. */
    private enum ResponseType
    {
        /** A result with a short manufacturer ID. */
        SHORT,
        /** A result with a long manufacturer ID. */
        LONG,
        /**
         * A result which is longer data content than the allowed by the MIDI specification but
         * valid message bytes.
         */
        NOT_IN_SPEC,
        /** Not a valid device inquiry response message. */
        ERROR
    }


    private final int [] data;
    private ResponseType responseType;


    /**
     * Constructor.
     *
     * @param data The data of a device inquiry response
     */
    public DeviceInquiry (final int [] data)
    {
        this.data = data;

        if (data.length == LENGTH_RESULT_SHORT)
            this.responseType = ResponseType.SHORT;
        else if (data.length == LENGTH_RESULT_LONG)
            this.responseType = ResponseType.LONG;
        else if (data.length > LENGTH_RESULT_LONG)
            this.responseType = ResponseType.NOT_IN_SPEC;
        else
        {
            this.responseType = ResponseType.ERROR;
            return;
        }

        if (data[0] != 0xF0 || data[1] != 0x7E || data[3] != 0x06 || data[4] != 0x02 || data[data.length - 1] != 0xF7)
            this.responseType = ResponseType.ERROR;
    }


    /**
     * Test if the given data is a valid device inquiry response.
     *
     * @return True if it is a valid device inquiry response
     */
    public boolean isValid ()
    {
        return this.responseType != ResponseType.ERROR;
    }


    /**
     * Get the device ID.
     *
     * @return The device ID or -1 if data is not valid
     */
    public int getDeviceID ()
    {
        return this.isValid () ? this.data[OFFSET_DEVICE_ID] : -1;
    }


    /**
     * Get the manufacturers system exclusive ID code. It is 1 or 3 bytes long for newer IDs. If the
     * first ID is 0, it is a 3 byte code.
     *
     * @return The manufacturers system exclusive id code or an empty array if data is not valid
     */
    public int [] getManufacturer ()
    {
        if (!this.isValid ())
            return new int [0];

        // Old 1 byte ID
        if (this.responseType == ResponseType.SHORT)
        {
            return new int []
            {
                this.data[OFFSET_MANUFACTURER_ID]
            };
        }

        // Newer 3-byte ID if first byte is 0
        return new int []
        {
            this.data[OFFSET_MANUFACTURER_ID],
            this.data[OFFSET_MANUFACTURER_ID + 1],
            this.data[OFFSET_MANUFACTURER_ID + 2]
        };
    }


    /**
     * Get the device family code of the message.
     *
     * @return The code or -1 if data is not valid
     */
    public int [] getDeviceFamilyCode ()
    {
        if (!this.isValid ())
            return new int [0];

        final int contentStart = this.getContentStart ();
        return new int []
        {
            this.data[contentStart],
            this.data[contentStart + 1]
        };
    }


    /**
     * Get the device family member code of the message.
     *
     * @return The 2 byte code or an empty array if data is not valid
     */
    public int [] getDeviceFamilyMemberCode ()
    {
        if (!this.isValid ())
            return new int [0];

        final int contentStart = this.getContentStart ();
        return new int []
        {
            this.data[contentStart + 2],
            this.data[contentStart + 3]
        };
    }


    /**
     * Get the software revision level. The format and length is device specific.
     *
     * @return The Software revision level or an empty array if data is not valid
     */
    public int [] getRevisionLevel ()
    {
        if (!this.isValid ())
            return new int [0];

        final int start = this.getContentStart () + LENGTH_DEVICE_FAMILY;
        final int [] softwareData = new int [LENGTH_SOFTWARE_REVISION];
        System.arraycopy (this.data, start, softwareData, 0, LENGTH_SOFTWARE_REVISION);
        return softwareData;
    }


    /**
     * Get the data which starts at the software revision position but has more data bytes (> 4) as
     * specified by the MIDI specification.
     *
     * @return The data or an empty array if data is not valid
     */
    public int [] getUnspecifiedData ()
    {
        if (!this.isValid ())
            return new int [0];

        final int contentStart = this.getContentStart ();
        final int length = this.data.length - contentStart - LENGTH_DEVICE_FAMILY - 1;
        final int start = contentStart + LENGTH_DEVICE_FAMILY;
        final int [] softwareData = new int [length];
        System.arraycopy (this.data, start, softwareData, 0, length);
        return softwareData;
    }


    /**
     * Create inquiry system exclusive message. Ignores the device ID.
     *
     * @return The system exclusive query
     */
    public static byte [] createQuery ()
    {
        return INQUIRY;
    }


    /**
     * Get the start of the messages content depending on the length of the manufacturer ID.
     *
     * @return The start offset, zero based
     */
    private int getContentStart ()
    {
        return this.responseType == ResponseType.SHORT ? OFFSET_CONTENT_SHORT : OFFSET_CONTENT_LONG;
    }


    /**
     * Get the whole data packet.
     *
     * @return The data
     */
    public int [] getData ()
    {
        return this.data;
    }
}
