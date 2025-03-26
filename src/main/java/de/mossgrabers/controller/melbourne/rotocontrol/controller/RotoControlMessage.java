// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.melbourne.rotocontrol.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;


/**
 * A system exclusive message received from the Roto Control.
 *
 * @author Jürgen Moßgraber
 */
public class RotoControlMessage
{
    /** General commands. */
    public static final int      GENERAL                      = 0x0A;
    /** Plug-in related commands. */
    public static final int      PLUGIN                       = 0x0B;
    /** Mix related commands. */
    public static final int      MIX                          = 0x0C;

    //////////////////////////////////////////////////////////////////////
    // General Sub-Types

    /** The DAW has started a session with ROTO-CONTROL. Sent to ROTO. */
    public static final int      TR_DAW_STARTED               = 0x01;
    /** Ping to check if the DAW is running. From ROTO. */
    public static final int      RCV_PING_DAW                 = 0x02;
    /** Ping response from the DAW. Sent to ROTO. */
    public static final int      TR_DAW_PING_RESPONSE         = 0x03;
    /** Number of tracks. Sent to ROTO. */
    public static final int      TR_NUM_TRACKS                = 0x04;
    /** First track page index. Sent to ROTO. */
    public static final int      TR_FIRST_TRACK               = 0x05;
    /** Set the first track page index. From ROTO. */
    public static final int      RCV_SET_FIRST_TRACK          = 0x06;
    /** Track details in the current track page. Sent to ROTO. */
    public static final int      TR_TRACK_DETAILS             = 0x07;
    /** Finished sending track details. Sent to ROTO. */
    public static final int      TR_TRACK_DETAILS_END         = 0x08;
    /** ROTO-Control has selected a track. From ROTO. */
    public static final int      RCV_SELECT_TRACK             = 0x09;
    /** Request the current transport status. From ROTO. */
    public static final int      RCV_REQUEST_TRANSPORT_STATUS = 0x0A;
    /** The current transport status. Sent to ROTO. */
    public static final int      TR_TRANSPORT_STATUS          = 0x0B;

    //////////////////////////////////////////////////////////////////////
    // Plug-in Sub-Types

    /** Set the DAW in PLUGIN mode. Sent to ROTO. */
    public static final int      RCV_SET_PLUGIN_MODE          = 0x01;
    /** Returns the number of PLUGINs on the current track. Sent to ROTO. */
    public static final int      TR_NUM_PLUGINS               = 0x02;
    /** First PLUGIN page index. Sent to ROTO. */
    public static final int      TR_FIRST_PLUGIN              = 0x03;
    /** Set the first PLUGIN page index. From ROTO. */
    public static final int      RCV_SET_FIRST_PLUGIN         = 0x04;
    /** Details of a PLUGIN. Sent to ROTO. */
    public static final int      TR_PLUGIN_DETAILS            = 0x05;
    /** Finished sending PLUGIN details. Sent to ROTO. */
    public static final int      TR_PLUGIN_DETAILS_END        = 0x06;
    /** The ROTO-CONTROL has selected a PLUGIN. Sent to ROTO. */
    public static final int      RCV_ROTO_SELECT_PLUGIN       = 0x07;
    /** The DAW has selected a PLUGIN. Sent to ROTO. */
    public static final int      TR_DAW_SELECT_PLUGIN         = 0x08;
    /** Put the DAW in learn mode. From ROTO. */
    public static final int      RCV_SET_PLUGIN_LEARN         = 0x09;
    /** A param has been learned. Sent to ROTO. */
    public static final int      TR_LEARN_PARAM               = 0x0A;
    /** Learned param details. From ROTO. */
    public static final int      RCV_PARAM_LEARNED            = 0x0B;
    /** Enable/disable a PLUGIN. From ROTO. */
    public static final int      RCV_SET_PLUGIN_ENABLE        = 0x0C;
    /** Lock/unlock PLUGINs. From ROTO. */
    public static final int      RCV_SET_PLUGIN_LOCK          = 0x0D;
    /** Un-map a learned control. Sent to ROTO. */
    public static final int      TR_UNMAP_CONTROL             = 0x0E;

    //////////////////////////////////////////////////////////////////////
    // Mix Sub-Types

    /** Set the MIX into all tracks mode. From ROTO. */
    public static final int      RCV_SET_MIX_ALL_TRACKS_MODE  = 0x01;
    /** Set the MIX into single track mode. From ROTO. */
    public static final int      RCV_SET_MIX_TRACK_MODE       = 0x02;
    /** The number of available sends. Sent to ROTO. */
    public static final int      TR_NUM_SENDS                 = 0x03;
    /** The DAW has selected a track. Sent to ROTO. */
    public static final int      TR_DAW_SELECT_TRACK          = 0x04;
    /** Set the all tracks mode. From ROTO. */
    public static final int      RCV_SET_ALL_TRACKS_MODE      = 0x05;

    private static final byte [] MESSAGE_HEADER               =
    {
        (byte) 0xF0,
        0x00,
        0x22,
        0x03,
        0x02
    };

    private final int            messageType;
    private final int            messageSubType;
    private final int []         content;
    private final byte []        byteContent;


    /**
     * Constructor.
     *
     * @param messageType The main type of the message
     * @param messageSubType The sub-type of the message
     * @param content The data content for the message
     */
    public RotoControlMessage (final int messageType, final int messageSubType, final int [] content)
    {
        this.messageType = messageType;
        this.messageSubType = messageSubType;
        this.content = content;
        this.byteContent = null;
    }


    /**
     * Constructor.
     *
     * @param messageType The main type of the message
     * @param messageSubType The sub-type of the message
     * @param byteContent The data content for the message
     */
    public RotoControlMessage (final int messageType, final int messageSubType, final byte [] byteContent)
    {
        this.messageType = messageType;
        this.messageSubType = messageSubType;
        this.content = null;
        this.byteContent = byteContent;
    }


    /**
     * Get the type (ID) of the message.
     *
     * @return The type
     */
    public int getType ()
    {
        return this.messageType;
    }


    /**
     * Get the sub-type of the message.
     *
     * @return The type
     */
    public int getSubType ()
    {
        return this.messageSubType;
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


    /**
     * Get the content from a Roto Control system exclusive message.
     *
     * @param data The system exclusive message from which to get the content
     * @return The ACVS message or null if it is not a ACVS message
     */
    public static Optional<RotoControlMessage> getMessageContent (final int [] data)
    {
        final int contentLength = data.length - MESSAGE_HEADER.length - 1;
        if (contentLength <= 0 || data[data.length - 1] != 0xF7)
            return Optional.empty ();

        for (int i = 0; i < MESSAGE_HEADER.length; i++)
        {
            if (MESSAGE_HEADER[i] != (byte) data[i])
                return Optional.empty ();
        }

        final int [] result = new int [contentLength - 2];
        System.arraycopy (data, MESSAGE_HEADER.length + 2, result, 0, contentLength - 2);
        return Optional.of (new RotoControlMessage (data[MESSAGE_HEADER.length], data[MESSAGE_HEADER.length + 1], result));
    }


    /**
     * Create the system exclusive data for a message.
     *
     * @return The system exclusive data bytes
     */
    public byte [] createMessage ()
    {
        final ByteArrayOutputStream out = new ByteArrayOutputStream ();
        try
        {
            out.write (MESSAGE_HEADER);
        }
        catch (final IOException ex)
        {
            // Can never happen
        }
        out.write (this.messageType);
        out.write (this.messageSubType);
        if (this.content != null)
        {
            for (final int val: this.content)
                out.write (val);
        }
        else if (this.byteContent != null)
        {
            for (final int val: this.byteContent)
                out.write (val);
        }
        out.write (0xF7);
        return out.toByteArray ();
    }
}
