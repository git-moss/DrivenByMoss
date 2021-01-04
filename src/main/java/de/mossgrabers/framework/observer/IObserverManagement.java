// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.observer;

/**
 * Interface for dis-/enabling observers.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IObserverManagement
{
    /**
     * Dis-/Enable all attributes. They are enabled by default. Use this function if values are
     * currently not needed to improve performance.
     *
     * @param enable True to enable
     */
    void enableObservers (final boolean enable);
}
