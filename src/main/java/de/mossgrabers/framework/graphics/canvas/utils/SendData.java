// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.graphics.canvas.utils;

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


    /** {@inheritDoc} */
    @Override
    public int hashCode ()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (this.edited ? 1231 : 1237);
        result = prime * result + this.modulatedValue;
        result = prime * result + (this.name == null ? 0 : this.name.hashCode ());
        result = prime * result + (this.text == null ? 0 : this.text.hashCode ());
        result = prime * result + this.value;
        return result;
    }


    /** {@inheritDoc} */
    @Override
    public boolean equals (final Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (this.getClass () != obj.getClass ())
            return false;
        final SendData other = (SendData) obj;
        if (this.edited != other.edited)
            return false;
        if (this.modulatedValue != other.modulatedValue)
            return false;
        if (this.name == null)
        {
            if (other.name != null)
                return false;
        }
        else if (!this.name.equals (other.name))
            return false;
        if (this.text == null)
        {
            if (other.text != null)
                return false;
        }
        else if (!this.text.equals (other.text))
            return false;
        return this.value == other.value;
    }
}
