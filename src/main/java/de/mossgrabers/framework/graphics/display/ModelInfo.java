// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.graphics.display;

import de.mossgrabers.framework.graphics.grid.IGridElement;

import java.util.ArrayList;
import java.util.List;


/**
 * Wrapper class for drawing data.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ModelInfo
{
    private String                  notification;
    private final List<IGridElement> elements;


    /**
     * Constructor.
     *
     * @param notification The notification message, if any
     * @param elements The elements
     */
    public ModelInfo (final String notification, final List<IGridElement> elements)
    {
        this.notification = notification;
        this.elements = new ArrayList<> (elements);
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
     * Get the grid elements.
     *
     * @return The elements
     */
    public List<IGridElement> getElements ()
    {
        return this.elements;
    }
}
