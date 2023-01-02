// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.configuration;

/**
 * A double setting.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IDoubleSetting extends IValueSetting<Double>
{
    /**
     * Set the double value.
     *
     * @param value The new value
     */
    void set (double value);
}
