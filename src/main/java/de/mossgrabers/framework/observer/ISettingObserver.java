// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.observer;

/**
 * An observer interface for settings.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
@FunctionalInterface
public interface ISettingObserver
{
    /**
     * Will be called when the setting for which this observer was registered has changed.
     */
    void hasChanged ();
}
