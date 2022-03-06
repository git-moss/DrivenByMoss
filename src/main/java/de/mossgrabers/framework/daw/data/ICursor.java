// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data;

/**
 * Interface to a Cursor.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface ICursor extends IPinnable
{
    /**
     * Is there a previous item?
     *
     * @return True if there is a previous item
     */
    boolean canSelectPrevious ();


    /**
     * Is there a next item?
     *
     * @return True if there is a next item
     */
    boolean canSelectNext ();


    /**
     * Select the previous item.
     */
    void selectPrevious ();


    /**
     * Select the next item.
     */
    void selectNext ();
}