// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.graphics.display;

import de.mossgrabers.framework.graphics.canvas.component.IComponent;

import java.util.ArrayList;
import java.util.List;


/**
 * Wrapper class for drawing data.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ModelInfo
{
    private String                 notification;
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
}
