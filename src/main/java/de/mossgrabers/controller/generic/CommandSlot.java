// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
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
    private int          type        = 0;
    private int          number      = 0;
    private int          midiChannel = 0;
    private int          knobMode    = 0;
    private boolean      sendValue   = false;
    private FlexiCommand command     = FlexiCommand.OFF;


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
     * Should the value send back to the device?
     *
     * @return True to send back
     */
    public boolean isSendValue ()
    {
        return this.sendValue;
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
     * Set the send value.
     *
     * @param value The boolean
     */
    public void setSendValue (final boolean value)
    {
        this.sendValue = value;
    }
}
