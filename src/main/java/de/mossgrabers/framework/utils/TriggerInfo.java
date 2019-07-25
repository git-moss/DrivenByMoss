// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.utils;

/**
 * All information about a button and its state.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TriggerInfo
{
    private ButtonEvent state      = ButtonEvent.UP;
    private boolean     isConsumed = false;
    private int         ledValue   = -1;


    /**
     * Get the state of the button.
     *
     * @return The state
     */
    public ButtonEvent getState ()
    {
        return this.state;
    }


    /**
     * Set the state of the button.
     *
     * @param state The state
     */
    public void setState (final ButtonEvent state)
    {
        this.state = state;
    }


    /**
     * Check if the button is consumed (which means that the button UP event should not be handled).
     *
     * @return The consumed flag
     */
    public boolean isConsumed ()
    {
        return this.isConsumed;
    }


    /**
     * Check if the button is consumed (which means that the button UP event should not be handled).
     *
     * @param isConsumed The consumed flag
     */
    public void setConsumed (final boolean isConsumed)
    {
        this.isConsumed = isConsumed;
    }


    /**
     * Get the cached value which was last sent to the device to set the LED of the button.
     *
     * @return The value
     */
    public int getLedValue ()
    {
        return this.ledValue;
    }


    /**
     * Set the cached value which was last sent to the device to set the LED of the button.
     *
     * @param ledValue The value
     */
    public void setLedValue (final int ledValue)
    {
        this.ledValue = ledValue;
    }
}