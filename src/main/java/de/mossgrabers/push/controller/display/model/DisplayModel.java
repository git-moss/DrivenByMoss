// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.controller.display.model;

import de.mossgrabers.push.controller.display.model.grid.GridChangeListener;
import de.mossgrabers.push.controller.display.model.grid.GridElement;

import java.util.ArrayList;
import java.util.List;


/**
 * Contains the data for the display content.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DisplayModel
{
    private final List<GridElement>        gridElements  = new ArrayList<> ();
    private final List<GridChangeListener> gridListeners = new ArrayList<> ();


    /**
     * Adds a listener for grid element changes
     *
     * @param listener A listener
     */
    public void addGridElementChangeListener (final GridChangeListener listener)
    {
        this.gridListeners.add (listener);
    }


    /**
     * Sets the grid elements.
     *
     * @param elements The elements to set
     */
    public void setGridElements (final List<GridElement> elements)
    {
        this.gridElements.clear ();
        this.gridElements.addAll (elements);

        for (final GridChangeListener listener: this.gridListeners)
            listener.gridHasChanged ();
    }


    /**
     * Get the grid elements.
     *
     * @return The elements
     */
    public List<GridElement> getGridElements ()
    {
        return new ArrayList<> (this.gridElements);
    }
}
