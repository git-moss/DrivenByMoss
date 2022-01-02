// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.configuration;

import de.mossgrabers.framework.configuration.IActionSetting;
import de.mossgrabers.framework.observer.IValueObserver;

import com.bitwig.extension.controller.api.SettableEnumValue;
import com.bitwig.extension.controller.api.Setting;

import java.util.Map;


/**
 * Bitwig implementation of an action setting.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ActionSettingImpl extends AbstractSetting implements IActionSetting
{
    private final SettableEnumValue   enumValue;
    private final Map<String, String> actionsMap;


    /**
     * Constructor.
     *
     * @param enumValue The enumeration value
     * @param actionsMap The action map for looking up the ID of the selected action
     */
    public ActionSettingImpl (final SettableEnumValue enumValue, final Map<String, String> actionsMap)
    {
        super ((Setting) enumValue);

        this.enumValue = enumValue;
        this.actionsMap = actionsMap;
    }


    /** {@inheritDoc} */
    @Override
    public void set (final String value)
    {
        for (final Map.Entry<String, String> entry: this.actionsMap.entrySet ())
        {
            if (entry.getValue ().equals (value))
            {
                this.enumValue.set (entry.getKey ());
                break;
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public String get ()
    {
        return this.actionsMap.get (this.enumValue.get ());
    }


    /** {@inheritDoc} */
    @Override
    public void addValueObserver (final IValueObserver<String> observer)
    {
        this.enumValue.addValueObserver (observer::update);

        // Directly fire the current value
        observer.update (this.enumValue.get ());
    }
}
