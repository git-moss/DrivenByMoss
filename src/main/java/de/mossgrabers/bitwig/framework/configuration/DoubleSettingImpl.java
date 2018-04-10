// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.configuration;

import de.mossgrabers.framework.configuration.IDoubleSetting;
import de.mossgrabers.framework.configuration.IValueObserver;

import com.bitwig.extension.controller.api.SettableRangedValue;


/**
 * Bitwig implementation of a double setting.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DoubleSettingImpl implements IDoubleSetting
{
    private SettableRangedValue rangedValue;


    /**
     * Constructor.
     *
     * @param rangedValue The range value
     */
    public DoubleSettingImpl (final SettableRangedValue rangedValue)
    {
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
    public void addValueObserver (final IValueObserver<Double> observer)
    {
        this.rangedValue.addValueObserver (value -> observer.update (Double.valueOf (value)));
    }
}
