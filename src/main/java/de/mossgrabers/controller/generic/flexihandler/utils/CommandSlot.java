// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.generic.flexihandler.utils;

import de.mossgrabers.controller.generic.controller.FlexiCommand;


/**
 * A command slot, which contains a button, knob or slider configuration triggered from CC, a note
 * or program change.
 *
 * @author Jürgen Moßgraber
 */
public class CommandSlot
{
    /** The Off type. */
    public static final int TYPE_OFF              = -1;
    /** The CC type. */
    public static final int TYPE_CC               = 0;
    /** The note type. */
    public static final int TYPE_NOTE             = 1;
    /** The program change type. */
    public static final int TYPE_PROGRAM_CHANGE   = 2;
    /** The pitch bend type. */
    public static final int TYPE_PITCH_BEND       = 3;
    /** The MMC type. */
    public static final int TYPE_MMC              = 4;

    private int             type                  = TYPE_OFF;
    private int             number                = 0;
    private int             midiChannel           = 0;
    private boolean         isHighRes             = false;
    private KnobMode        knobMode              = KnobMode.ABSOLUTE;
    private boolean         sendValue             = false;
    private boolean         sendValueWhenReceived = false;
    private FlexiCommand    command               = FlexiCommand.OFF;


    /**
     * Constructor.
     */
    public CommandSlot ()
    {
        // Intentionally empty
    }


    /**
     * Get the configured type.
     *
     * @return The type
     */
    public int getType ()
    {
        return this.type;
    }


    /**
     * Get the configured number.
     *
     * @return The number
     */
    public int getNumber ()
    {
        return this.number;
    }


    /**
     * Get the configured MIDI channel.
     *
     * @return The MIDI channel
     */
    public int getMidiChannel ()
    {
        return this.midiChannel;
    }


    /**
     * Get the configured MIDI resolution.
     *
     * @return True for high resolution (14-bit)
     */
    public boolean getResolution ()
    {
        return this.isHighRes;
    }


    /**
     * Get the knob mode (absolute or relative 1-3).
     *
     * @return The knob mode
     */
    public KnobMode getKnobMode ()
    {
        return this.knobMode;
    }


    /**
     * Get the configured command.
     *
     * @return The command
     */
    public FlexiCommand getCommand ()
    {
        return this.command;
    }


    /**
     * Set the type.
     *
     * @param value The index
     */
    public void setType (final int value)
    {
        this.type = value;
    }


    /**
     * Set the number.
     *
     * @param value The number
     */
    public void setNumber (final int value)
    {
        this.number = value;
    }


    /**
     * Set the MIDI channel.
     *
     * @param value The index
     */
    public void setMidiChannel (final int value)
    {
        this.midiChannel = value;
    }


    /**
     * Set the MIDI resolution (only for CC) to 7-bit or 14-bit (high resolution).
     *
     * @param isHighRes True to use 14-bi5
     */
    public void setResolution (final boolean isHighRes)
    {
        this.isHighRes = isHighRes;
    }


    /**
     * Set the knob mode.
     *
     * @param value The index
     */
    public void setKnobMode (final KnobMode value)
    {
        this.knobMode = value;
    }


    /**
     * Set the command.
     *
     * @param value The command name
     */
    public void setCommand (final FlexiCommand value)
    {
        this.command = value;
    }


    /**
     * Should the value send back to the device?
     *
     * @return True to send back
     */
    public boolean isSendValue ()
    {
        return this.sendValue;
    }


    /**
     * Set the send value.
     *
     * @param value The boolean
     */
    public void setSendValue (final boolean value)
    {
        this.sendValue = value;
    }


    /**
     * Should the value send back to the device?
     *
     * @return True to send back
     */
    public boolean isSendValueWhenReceived ()
    {
        return this.sendValueWhenReceived;
    }


    /**
     * Set the send value.
     *
     * @param value The boolean
     */
    public void setSendValueWhenReceived (final boolean value)
    {
        this.sendValueWhenReceived = value;
    }
}
