// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.configuration;

/**
 * An integer setting.
 *
 * @author Jürgen Moßgraber
 */
public interface IIntegerSetting extends IValueSetting<Integer>
{
    /**
     * Set the integer value.
     *
     * @param value The new value
     */
    void set (int value);
}
