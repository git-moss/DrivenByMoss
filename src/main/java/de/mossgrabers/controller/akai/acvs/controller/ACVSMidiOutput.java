// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.acvs.controller;

import de.mossgrabers.controller.akai.acvs.ACVSDevice;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.midi.IMidiOutput;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


/**
 * Wraps the MIDI output and adds helper methods for the ACVS system exclusive commands.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ACVSMidiOutput implements IMidiOutput
{
    /** The ID of the ping message. */
    public static final int                    MESSAGE_ID_PING  = 0x00;
    /** The ID of the pong message. */
    public static final int                    MESSAGE_ID_PONG  = 0x01;
    /** The ID of the text message. */
    public static final int                    MESSAGE_ID_TEXT  = 0x10;
    /** The ID of the color message. */
    public static final int                    MESSAGE_ID_COLOR = 0x11;

    private static final Map<Byte, ACVSDevice> ID_DEVICE_MAP    = new HashMap<> ();
    static
    {
        for (final ACVSDevice acvsDevice: ACVSDevice.values ())
            ID_DEVICE_MAP.put (Byte.valueOf (acvsDevice.getId ()), acvsDevice);
    }

    private final byte []     pingMessage   =
    {
        (byte) 0xF0,
        0x47,
        0x00,
        0x3B,
        MESSAGE_ID_PING,
        (byte) 0xF7
    };

    private final byte []     messageHeader =
    {
        (byte) 0xF0,
        0x47,
        0x00,
        0x3B
    };

    private final IMidiOutput output;


    /**
     * Constructor.
     *
     * @param output The MIDI output to wrap
     * @param acvsDevice The device to set as the active (connected) one
     */
    public ACVSMidiOutput (final IMidiOutput output, final ACVSDevice acvsDevice)
    {
        this.output = output;
        this.setActiveDeviceID (acvsDevice);
    }


    /**
     * Get the content from a ACVS message.
     *
     * @param data The system exclusive message from which to get the content
     * @return The ACVS message or null if it is not a ACVS message
     */
    public Optional<ACVSMessage> getMessageContent (final int [] data)
    {
        final int contentLength = data.length - this.messageHeader.length - 1;
        if (contentLength <= 0 || data[data.length - 1] != 0xF7)
            return Optional.empty ();

        ACVSDevice acvsDevice = ACVSDevice.MPC_LIVE_ONE;

        for (int i = 0; i < this.messageHeader.length; i++)
        {
            if (i == 3)
            {
                acvsDevice = ID_DEVICE_MAP.get (Byte.valueOf ((byte) data[i]));
                if (acvsDevice == null)
                    return Optional.empty ();
            }
            else if (this.messageHeader[i] != (byte) data[i])
                return Optional.empty ();
        }

        final int [] result = new int [contentLength - 1];
        System.arraycopy (data, this.messageHeader.length + 1, result, 0, contentLength - 1);
        return Optional.of (new ACVSMessage (acvsDevice, data[this.messageHeader.length], result));
    }


    /**
     * Send a ping message to the device.
     */
    public void sendPing ()
    {
        this.output.sendSysex (this.pingMessage);
    }


    /**
     * Send a text system exclusive message to the device.
     *
     * @param itemID The ID of the item for which the text is intended
     * @param text The text to send
     */
    public void sendText (final int itemID, final String text)
    {
        final byte [] textBytes = text.getBytes ();
        final int size = 4 + textBytes.length;
        final byte [] data = new byte [size];
        // Item ID MSB / LSB
        data[0] = (byte) (itemID >> 8 & 0x7F);
        data[1] = (byte) (itemID & 0x7F);
        // Text length MSB / LSB
        data[2] = (byte) (textBytes.length >> 8 & 0x7F);
        data[3] = (byte) (textBytes.length & 0x7F);
        System.arraycopy (textBytes, 0, data, 4, textBytes.length);
        this.sendSysex (MESSAGE_ID_TEXT, data);
    }


    /**
     * Send a color system exclusive message to the device.
     *
     * @param itemID The ID of the item for which the color is intended
     * @param color The color to send
     */
    public void sendColor (final int itemID, final ColorEx color)
    {
        final byte [] data = new byte [5];
        // Item ID MSB / LSB
        data[0] = (byte) (itemID >> 8 & 0x7F);
        data[1] = (byte) (itemID & 0x7F);
        // RGB
        final int [] rgb = color.toIntRGB127 ();
        data[2] = (byte) rgb[0];
        data[3] = (byte) rgb[1];
        data[4] = (byte) rgb[2];
        this.sendSysex (MESSAGE_ID_COLOR, data);
    }


    /**
     * Send a system exclusive message to the device.
     *
     * @param messageTypeID The ID of the message, see MESSAGE_ID_* constants
     * @param data The data to send
     */
    public void sendSysex (final int messageTypeID, final byte [] data)
    {
        final int size = this.messageHeader.length + data.length + 2;
        final byte [] message = new byte [size];
        System.arraycopy (this.messageHeader, 0, message, 0, this.messageHeader.length);
        message[this.messageHeader.length] = (byte) messageTypeID;
        System.arraycopy (data, 0, message, this.messageHeader.length + 1, data.length);
        message[message.length - 1] = (byte) 0xF7;
        this.output.sendSysex (message);
    }


    /** {@inheritDoc} */
    @Override
    public void sendCC (final int cc, final int value)
    {
        this.output.sendCC (cc, value);
    }


    /** {@inheritDoc} */
    @Override
    public void sendCCEx (final int channel, final int cc, final int value)
    {
        this.output.sendCCEx (channel, cc, value);
    }


    /** {@inheritDoc} */
    @Override
    public void sendNote (final int note, final int velocity)
    {
        this.output.sendNote (note, velocity);
    }


    /** {@inheritDoc} */
    @Override
    public void sendNoteEx (final int channel, final int note, final int velocity)
    {
        this.output.sendNoteEx (channel, note, velocity);
    }


    /** {@inheritDoc} */
    @Override
    public void sendPolyphonicAftertouch (final int data1, final int data2)
    {
        this.output.sendPolyphonicAftertouch (data1, data2);
    }


    /** {@inheritDoc} */
    @Override
    public void sendPolyphonicAftertouch (final int channel, final int data1, final int data2)
    {
        this.output.sendPolyphonicAftertouch (channel, data1, data2);
    }


    /** {@inheritDoc} */
    @Override
    public void sendChannelAftertouch (final int data1, final int data2)
    {
        this.output.sendChannelAftertouch (data1, data2);
    }


    /** {@inheritDoc} */
    @Override
    public void sendChannelAftertouch (final int channel, final int data1, final int data2)
    {
        this.output.sendChannelAftertouch (channel, data1, data2);
    }


    /** {@inheritDoc} */
    @Override
    public void sendPitchbend (final int data1, final int data2)
    {
        this.output.sendPitchbend (data1, data2);
    }


    /** {@inheritDoc} */
    @Override
    public void sendPitchbend (final int channel, final int data1, final int data2)
    {
        this.output.sendPitchbend (channel, data1, data2);
    }


    /** {@inheritDoc} */
    @Override
    public void sendProgramChange (final int bankMSB, final int bankLSB, final int value)
    {
        this.output.sendProgramChange (bankMSB, bankLSB, value);
    }


    /** {@inheritDoc} */
    @Override
    public void sendProgramChange (final int channel, final int bankMSB, final int bankLSB, final int value)
    {
        this.output.sendProgramChange (channel, bankMSB, bankLSB, value);
    }


    /** {@inheritDoc} */
    @Override
    public void sendSysex (final byte [] data)
    {
        this.output.sendSysex (data);
    }


    /** {@inheritDoc} */
    @Override
    public void sendSysex (final String data)
    {
        this.output.sendSysex (data);
    }


    /**
     * Set the ID of the currently active ACVS device on the messages.
     *
     * @param acvsDevice The device to set as the active (connected) one
     */
    private void setActiveDeviceID (final ACVSDevice acvsDevice)
    {
        final byte id = acvsDevice.getId ();
        this.pingMessage[3] = id;
        this.messageHeader[3] = id;
    }


    /** {@inheritDoc} */
    @Override
    public void configureMPE (final int zone, final int numberOfChannels)
    {
        // Not used
    }


    /** {@inheritDoc} */
    @Override
    public void sendMPEPitchbendRange (final int zone, final int range)
    {
        // Not used
    }
}
