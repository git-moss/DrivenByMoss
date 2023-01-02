// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.observer;

/**
 * An observer for a value change.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 *
 * @param <V> The type of the value
 */
@FunctionalInterface
public interface IValueObserver<V>
{
    /**
     * Called if the value has changed.
     *
     * @param value The new value
     */
    void update (V value);
}
