// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.configuration;

/**
 * An interface to a setting.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface ISetting
{
    /**
     * Disable/enable the setting.
     *
     * @param enable True to enable
     */
    void setEnabled (final boolean enable);


    /**
     * Make the widget of the setting visible or hide it.
     *
     * @param visible True to show
     */
    void setVisible (boolean visible);
}
