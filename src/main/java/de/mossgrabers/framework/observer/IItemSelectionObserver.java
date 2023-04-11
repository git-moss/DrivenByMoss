// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.observer;

/**
 * Callback interface for observing item selection changes in a bank.
 *
 * @author Jürgen Moßgraber
 */
@FunctionalInterface
public interface IItemSelectionObserver
{
    /**
     * The callback function.
     *
     * @param index The index of the track
     * @param isSelected Has the item been selected?
     */
    void call (int index, boolean isSelected);
}
