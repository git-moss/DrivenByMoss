// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data.bank;

/**
 * Interface to a parameter page bank.
 *
 * @author Jürgen Moßgraber
 */
public interface IParameterPageBank extends IBank<String>
{
    /**
     * Get the position of the selected item.
     *
     * @return The position
     */
    int getSelectedItemPosition ();


    /**
     * Get the index of the selected item in the current page.
     *
     * @return The index
     */
    int getSelectedItemIndex ();


    /**
     * Set the index of the selected item in the current page.
     *
     * @param index The index
     */
    void selectPage (int index);
}