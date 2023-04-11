// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.utils;

/**
 * An callback interface for a timer.
 *
 * @author Jürgen Moßgraber
 */
@FunctionalInterface
public interface TimerCallback
{
    /**
     * Will be called when the timer fires.
     */
    void call ();
}
