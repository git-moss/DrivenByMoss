// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.configuration;

import de.mossgrabers.framework.configuration.IIntegerSetting;
import de.mossgrabers.framework.observer.IValueObserver;

import com.bitwig.extension.controller.api.SettableRangedValue;
import com.bitwig.extension.controller.api.Setting;


/**
 * Bitwig implementation of a integer setting.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class IntegerSettingImpl extends AbstractSetting implements IIntegerSetting
{
    private final SettableRangedValue rangedValue;
    private final int                 range;
    private final int                 minimum;


    /**
     * Constructor.
     *
     * @param rangedValue The ranged value
     * @param minimum The minimum value
     * @param range The range
     */
    public IntegerSettingImpl (final SettableRangedValue rangedValue, final int minimum, final int range)
    {
        super ((Setting) rangedValue);

        this.rangedValue = rangedValue;
        this.minimum = minimum;
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
    public void set (final Integer value)
    {
        this.set (value.intValue ());
    }


    /** {@inheritDoc} */
    @Override
    public Integer get ()
    {
        return Integer.valueOf ((int) this.rangedValue.getRaw ());
    }


    /** {@inheritDoc} */
    @Override
    public void addValueObserver (final IValueObserver<Integer> observer)
    {
        this.rangedValue.addValueObserver (this.range, value -> observer.update (Integer.valueOf (this.minimum + value)));

        // Directly fire the current value
        final int value = (int) this.rangedValue.getRaw ();
        observer.update (Integer.valueOf (value));
    }
}
