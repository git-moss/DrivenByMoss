// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.generic;

import de.mossgrabers.controller.generic.controller.FlexiCommand;


/**
 * A command slot, which contains a button, knob or slider configuration triggered from CC, a note
 * or program change.
 *
 * @author J&uuml;rgen Mo&szlig;graber
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

    private int             type                  = -1;
    private int             number                = 0;
    private int             midiChannel           = 0;
    private int             knobMode              = 0;
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
     * Get the configured midi channel.
     *
     * @return The midi channel
     */
    public int getMidiChannel ()
    {
        return this.midiChannel;
    }


    /**
     * Get the knob mode (absolute or relative 1-3).
     *
     * @return The knob mode
     */
    public int getKnobMode ()
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
     * Set the midi channel.
     *
     * @param value The index
     */
    public void setMidiChannel (final int value)
    {
        this.midiChannel = value;
    }


    /**
     * Set the knob mode.
     *
     * @param value The index
     */
    public void setKnobMode (final int value)
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
