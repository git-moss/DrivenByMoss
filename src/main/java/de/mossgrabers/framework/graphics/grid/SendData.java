// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.graphics.grid;

/**
 * Wraps some send info.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SendData
{
    private final String  name;
    private final String  text;
    private final int     value;
    private final int     modulatedValue;
    private final boolean edited;


    /**
     * Constructor.
     *
     * @param name THe name of the send
     * @param text The description text
     * @param value The value
     * @param modulatedValue THe modulated value
     * @param edited Is selected for editing
     */
    public SendData (final String name, final String text, final int value, final int modulatedValue, final boolean edited)
    {
        this.name = name;
        this.text = text;
        this.value = value;
        this.modulatedValue = modulatedValue;
        this.edited = edited;
    }


    /**
     * Get the name of the send.
     *
     * @return The name
     */
    public String getName ()
    {
        return this.name;
    }


    /**
     * Get the description of the send.
     *
     * @return The description
     */
    public String getText ()
    {
        return this.text;
    }


    /**
     * Get the value of the send.
     *
     * @return The value
     */
    public int getValue ()
    {
        return this.value;
    }


    /**
     * Get the modulated value of the send.
     *
     * @return The modulated value
     */
    public int getModulatedValue ()
    {
        return this.modulatedValue;
    }


    /**
     * Is the sendselected for editing.
     *
     * @return True if selected
     */
    public boolean isEdited ()
    {
        return this.edited;
    }
}
