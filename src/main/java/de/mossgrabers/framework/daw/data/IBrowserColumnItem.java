// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data;

import de.mossgrabers.framework.daw.ObserverManagement;


/**
 * Interface to a browser column entry.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IBrowserColumnItem extends ObserverManagement
{
    /**
     * Get the index.
     *
     * @return The index
     */
    int getIndex ();


    /**
     * Does the item exist?
     *
     * @return True if it exists
     */
    boolean doesExist ();


    /**
     * Get the name of the item.
     *
     * @return The name of the item
     */
    String getName ();


    /**
     * Returns true if the item is selected.
     *
     * @return True if the item is selected.
     */
    boolean isSelected ();


    /**
     * Get the hit count.
     *
     * @return The hit count
     */
    int getHitCount ();
}