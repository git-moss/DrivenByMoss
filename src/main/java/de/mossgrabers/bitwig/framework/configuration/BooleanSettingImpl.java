// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.configuration;

import de.mossgrabers.framework.configuration.IBooleanSetting;
import de.mossgrabers.framework.observer.IValueObserver;

import com.bitwig.extension.controller.api.SettableBooleanValue;
import com.bitwig.extension.controller.api.Setting;


/**
 * Bitwig implementation of a boolean setting.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class BooleanSettingImpl extends AbstractSetting implements IBooleanSetting
{
    private final SettableBooleanValue booleanValue;


    /**
     * Constructor.
     *
     * @param booleanValue The ranged value
     */
    public BooleanSettingImpl (final SettableBooleanValue booleanValue)
    {
        super ((Setting) booleanValue);

        this.booleanValue = booleanValue;
    }


    /** {@inheritDoc} */
    @Override
    public void set (final boolean value)
    {
        this.booleanValue.set (value);
    }


    /** {@inheritDoc} */
    @Override
    public void set (final Boolean value)
    {
        this.set (value.booleanValue ());
    }


    /** {@inheritDoc} */
    @Override
    public Boolean get ()
    {
        return Boolean.valueOf (this.booleanValue.get ());
    }


    /** {@inheritDoc} */
    @Override
    public void addValueObserver (final IValueObserver<Boolean> observer)
    {
        this.booleanValue.addValueObserver (value -> observer.update (Boolean.valueOf (value)));

        // Directly fire the current value
        observer.update (Boolean.valueOf (this.booleanValue.get ()));
    }
}
