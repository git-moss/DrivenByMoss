// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.configuration;

import de.mossgrabers.framework.observer.IValueObserver;


/**
 * A signal setting.
 *
 * @author Jürgen Moßgraber
 */
public interface ISignalSetting extends ISetting
{
    /**
     * Add an observer for a signal.
     *
     * @param observer The observer
     */
    void addSignalObserver (IValueObserver<Void> observer);
}
