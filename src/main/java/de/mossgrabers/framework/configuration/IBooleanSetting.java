// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.configuration;

/**
 * A boolean setting.
 *
 * @author Jürgen Moßgraber
 */
public interface IBooleanSetting extends IValueSetting<Boolean>
{
    /**
     * Set the boolean value.
     *
     * @param value The new value
     */
    void set (boolean value);
}
