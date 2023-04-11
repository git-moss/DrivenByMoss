// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.configuration;

import de.mossgrabers.framework.configuration.IStringSetting;
import de.mossgrabers.framework.observer.IValueObserver;

import com.bitwig.extension.controller.api.SettableStringValue;
import com.bitwig.extension.controller.api.Setting;


/**
 * Bitwig implementation of a string setting.
 *
 * @author Jürgen Moßgraber
 */
public class StringSettingImpl extends AbstractSetting implements IStringSetting
{
    private final SettableStringValue stringValue;


    /**
     * Constructor.
     *
     * @param stringValue The string value
     */
    public StringSettingImpl (final SettableStringValue stringValue)
    {
        super ((Setting) stringValue);

        this.stringValue = stringValue;
    }


    /** {@inheritDoc} */
    @Override
    public void set (final String value)
    {
        this.stringValue.set (value);
    }


    /** {@inheritDoc} */
    @Override
    public String get ()
    {
        return this.stringValue.get ();
    }


    /** {@inheritDoc} */
    @Override
    public void addValueObserver (final IValueObserver<String> observer)
    {
        this.stringValue.addValueObserver (observer::update);

        // Directly fire the current value
        observer.update (this.stringValue.get ());
    }
}
