// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.observer;

/**
 * Callback interface for observing the adjustment of the current bank page.
 *
 * @author Jürgen Moßgraber
 */
@FunctionalInterface
public interface IBankPageObserver
{
    /**
     * The callback function.
     */
    void pageAdjusted ();
}
