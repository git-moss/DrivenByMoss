// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.configuration;

import de.mossgrabers.framework.configuration.IDoubleSetting;
import de.mossgrabers.framework.observer.IValueObserver;

import com.bitwig.extension.controller.api.SettableRangedValue;
import com.bitwig.extension.controller.api.Setting;


/**
 * Bitwig implementation of a double setting.
 *
 * @author Jürgen Moßgraber
 */
public class DoubleSettingImpl extends AbstractSetting implements IDoubleSetting
{
    private final SettableRangedValue rangedValue;


    /**
     * Constructor.
     *
     * @param rangedValue The range value
     */
    public DoubleSettingImpl (final SettableRangedValue rangedValue)
    {
        super ((Setting) rangedValue);

        this.rangedValue = rangedValue;
    }


    /** {@inheritDoc} */
    @Override
    public void set (final double value)
    {
        this.rangedValue.setRaw (value);
    }


    /** {@inheritDoc} */
    @Override
    public void set (final Double value)
    {
        this.set (value.doubleValue ());
    }


    /** {@inheritDoc} */
    @Override
    public Double get ()
    {
        return Double.valueOf (this.rangedValue.get ());
    }


    /** {@inheritDoc} */
    @Override
    public void addValueObserver (final IValueObserver<Double> observer)
    {
        this.rangedValue.addValueObserver (value -> observer.update (Double.valueOf (value)));

        // Directly fire the current value
        observer.update (Double.valueOf (this.rangedValue.get ()));
    }
}
