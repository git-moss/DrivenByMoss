// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.configuration;

/**
 * An integer setting.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IIntegerSetting
{
    /**
     * Set the integer value.
     *
     * @param value The new value
     */
    void set (int value);


    /**
     * Add an observer for a change of the value.
     *
     * @param observer The observer
     */
    void addValueObserver (IValueObserver<Integer> observer);
}
