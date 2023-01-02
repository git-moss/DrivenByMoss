// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data;

import de.mossgrabers.framework.observer.IObserverManagement;
import de.mossgrabers.framework.observer.IValueObserver;


/**
 * Interface to an item. An item is an object with a name and exists state. It also can be selected
 * and put in banks.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IItem extends IObserverManagement
{
    /**
     * Returns true if the item exits.
     *
     * @return True if the item exits.
     */
    boolean doesExist ();


    /**
     * Get the index of the item in the current bank page.
     *
     * @return The index of the item in the current bank page
     */
    int getIndex ();


    /**
     * Get the position of the item in all items of the bank.
     *
     * @return The position
     */
    int getPosition ();


    /**
     * True if the item is selected.
     *
     * @return True if the item is selected.
     */
    boolean isSelected ();


    /**
     * Set the selected state of the item.
     *
     * @param isSelected True if the item is selected
     */
    void setSelected (boolean isSelected);


    /**
     * Select the item. Removes the selection state from other items.
     */
    void select ();


    /**
     * Toggles the selection state of the item. Keeps selection state of other items.
     */
    void toggleMultiSelect ();


    /**
     * Get the name of the item.
     *
     * @return The name
     */
    String getName ();


    /**
     * Get the name of the item.
     *
     * @param limit Limit the text to this length
     * @return The name
     */
    String getName (int limit);


    /**
     * Add an observer for the name.
     *
     * @param observer The observer to notify on a name change
     */
    void addNameObserver (final IValueObserver<String> observer);


    /**
     * Set the name of the item.
     *
     * @param name The new name
     */
    void setName (String name);
}
