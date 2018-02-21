// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.bitwig.configuration;

import de.mossgrabers.framework.configuration.IEnumSetting;
import de.mossgrabers.framework.configuration.IValueObserver;

import com.bitwig.extension.controller.api.SettableEnumValue;


/**
 * Bitwig implementation of an enum setting.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class EnumSettingImpl implements IEnumSetting
{
    private SettableEnumValue enumValue;


    /**
     * Constructor.
     * 
     * @param enumValue The enum value
     */
    public EnumSettingImpl (final SettableEnumValue enumValue)
    {
        this.enumValue = enumValue;
    }


    /** {@inheritDoc} */
    @Override
    public void set (final String value)
    {
        this.enumValue.set (value);
    }


    /** {@inheritDoc} */
    @Override
    public void addValueObserver (final IValueObserver<String> observer)
    {
        this.enumValue.addValueObserver (observer::update);
    }
}
