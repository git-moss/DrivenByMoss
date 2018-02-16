// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.bitwig.configuration;

import de.mossgrabers.framework.configuration.IIntegerSetting;
import de.mossgrabers.framework.configuration.IValueObserver;

import com.bitwig.extension.controller.api.SettableRangedValue;


/**
 * Bitwig implementation of a integer setting.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class IntegerSettingImpl implements IIntegerSetting
{
    private SettableRangedValue rangedValue;
    private int                 range;


    /**
     * Constructor.
     * 
     * @param rangedValue The ranged value
     * @param range The range
     */
    public IntegerSettingImpl (final SettableRangedValue rangedValue, final int range)
    {
        this.rangedValue = rangedValue;
        this.range = range;
    }


    /** {@inheritDoc} */
    @Override
    public void set (final int value)
    {
        this.rangedValue.setRaw (value);
    }


    /** {@inheritDoc} */
    @Override
    public void addValueObserver (final IValueObserver<Integer> observer)
    {
        this.rangedValue.addValueObserver (this.range, value -> observer.update (Integer.valueOf (value)));
    }
}
