// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.graphics.display;

import de.mossgrabers.framework.graphics.canvas.component.IComponent;

import java.util.ArrayList;
import java.util.List;


/**
 * Wrapper class for drawing data.
 *
 * @author Jürgen Moßgraber
 */
public class ModelInfo
{
    private final String           notification;
    private final List<IComponent> components;


    /**
     * Constructor.
     *
     * @param notification The notification message, if any
     * @param elements The elements
     */
    public ModelInfo (final String notification, final List<IComponent> elements)
    {
        this.notification = notification;
        this.components = new ArrayList<> (elements);
    }


    /**
     * Get the notification message.
     *
     * @return The notification message
     */
    public String getNotification ()
    {
        return this.notification;
    }


    /**
     * Get the canvas components.
     *
     * @return The components
     */
    public List<IComponent> getComponents ()
    {
        return this.components;
    }


    /** {@inheritDoc} */
    @Override
    public int hashCode ()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (this.components == null ? 0 : this.components.hashCode ());
        result = prime * result + (this.notification == null ? 0 : this.notification.hashCode ());
        return result;
    }


    /** {@inheritDoc} */
    @Override
    public boolean equals (final Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null || this.getClass () != obj.getClass ())
            return false;
        final ModelInfo other = (ModelInfo) obj;
        if (this.components == null)
        {
            if (other.components != null)
                return false;
        }
        else if (!this.components.equals (other.components))
            return false;
        if (this.notification == null)
        {
            if (other.notification != null)
                return false;
        }
        else if (!this.notification.equals (other.notification))
            return false;
        return true;
    }
}
