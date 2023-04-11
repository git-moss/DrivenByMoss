// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.configuration;

import de.mossgrabers.framework.configuration.ISignalSetting;
import de.mossgrabers.framework.observer.IValueObserver;

import com.bitwig.extension.controller.api.Setting;
import com.bitwig.extension.controller.api.Signal;


/**
 * Bitwig implementation of a signal setting.
 *
 * @author Jürgen Moßgraber
 */
public class SignalSettingImpl extends AbstractSetting implements ISignalSetting
{
    private final Signal signalValue;


    /**
     * Constructor.
     *
     * @param signalValue The signal value
     */
    public SignalSettingImpl (final Signal signalValue)
    {
        super ((Setting) signalValue);

        this.signalValue = signalValue;
    }


    /** {@inheritDoc} */
    @Override
    public void addSignalObserver (final IValueObserver<Void> observer)
    {
        this.signalValue.addSignalObserver ( () -> observer.update (null));
    }
}
