// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.observer;

/**
 * An observer for indexed value change.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 *
 * @param <V> The type of the value
 */
@FunctionalInterface
public interface IIndexedValueObserver<V>
{
    /**
     * Called if the value has changed.
     *
     * @param index The index of the value
     * @param value The new value
     */
    void update (int index, V value);
}
