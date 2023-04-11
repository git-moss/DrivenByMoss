// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data;

/**
 * Interface to a browser column entry.
 *
 * @author Jürgen Moßgraber
 */
public interface IBrowserColumnItem extends IItem
{
    /**
     * Get the hit count.
     *
     * @return The hit count
     */
    int getHitCount ();
}