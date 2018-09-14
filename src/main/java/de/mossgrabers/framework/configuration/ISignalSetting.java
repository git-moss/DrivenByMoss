// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.configuration;

/**
 * A signal setting.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface ISignalSetting
{
    /**
     * Set the string value.
     *
     * @param value The new value
     */
    void set (Void value);


    /**
     * Add an observer for a change of the value.
     *
     * @param observer The observer
     */
    void addValueObserver (IValueObserver<Void> observer);
}
