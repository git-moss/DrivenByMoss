// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.configuration;

import de.mossgrabers.framework.configuration.ISignalSetting;
import de.mossgrabers.framework.configuration.IValueObserver;

import com.bitwig.extension.controller.api.Signal;


/**
 * Bitwig implementation of a signal setting.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SignalSettingImpl implements ISignalSetting
{
    private Signal signalValue;


    /**
     * Constructor.
     *
     * @param signalValue The signal value
     */
    public SignalSettingImpl (final Signal signalValue)
    {
        this.signalValue = signalValue;
    }


    /** {@inheritDoc} */
    @Override
    public void set (final Void value)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void addValueObserver (final IValueObserver<Void> observer)
    {
        this.signalValue.addSignalObserver ( () -> observer.update (null));
    }
}
