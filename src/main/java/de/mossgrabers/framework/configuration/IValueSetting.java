// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.configuration;

import de.mossgrabers.framework.observer.IValueObserver;


/**
 * An interface to a setting which edits a value.
 *
 * @param <T> The type of the settings value
 *
 * @author Jürgen Moßgraber
 */
public interface IValueSetting<T> extends ISetting
{
    /**
     * Add an observer for a change of the value.
     *
     * @param observer The observer
     */
    void addValueObserver (IValueObserver<T> observer);


    /**
     * Set the value of the setting.
     *
     * @param value The value
     */
    void set (T value);


    /**
     * Get the current value of the setting.
     *
     * @return The value
     */
    T get ();
}
