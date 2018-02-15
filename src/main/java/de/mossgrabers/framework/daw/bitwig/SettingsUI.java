// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.bitwig;

import de.mossgrabers.framework.configuration.IDoubleSetting;
import de.mossgrabers.framework.configuration.IEnumSetting;
import de.mossgrabers.framework.configuration.IIntegerSetting;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.configuration.IStringSetting;
import de.mossgrabers.framework.daw.bitwig.configuration.DoubleSettingImpl;
import de.mossgrabers.framework.daw.bitwig.configuration.EnumSettingImpl;
import de.mossgrabers.framework.daw.bitwig.configuration.IntegerSettingImpl;
import de.mossgrabers.framework.daw.bitwig.configuration.StringSettingImpl;

import com.bitwig.extension.controller.api.Preferences;


/**
 * The Bitwig implementation to create user interface widgets for settings.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SettingsUI implements ISettingsUI
{
    private Preferences preferences;


    /**
     * Constructor.
     *
     * @param preferences The Bitwig preferences
     */
    public SettingsUI (final Preferences preferences)
    {
        this.preferences = preferences;
    }


    /** {@inheritDoc} */
    @Override
    public IEnumSetting getEnumSetting (final String label, final String category, final String [] options, final String initialValue)
    {
        return new EnumSettingImpl (this.preferences.getEnumSetting (label, category, options, initialValue));
    }


    /** {@inheritDoc} */
    @Override
    public IStringSetting getStringSetting (String label, String category, int numChars, String initialText)
    {
        return new StringSettingImpl (this.preferences.getStringSetting (label, category, numChars, initialText));
    }


    /** {@inheritDoc} */
    @Override
    public IDoubleSetting getNumberSetting (String label, String category, double minValue, double maxValue, double stepResolution, String unit, double initialValue)
    {
        return new DoubleSettingImpl (this.preferences.getNumberSetting (label, category, minValue, maxValue, stepResolution, unit, initialValue));
    }


    /** {@inheritDoc} */
    @Override
    public IIntegerSetting getRangeSetting (final String label, final String category, final int minValue, final int maxValue, final int stepResolution, final String unit, final int initialValue)

    {
        return new IntegerSettingImpl (this.preferences.getNumberSetting (label, category, minValue, maxValue, stepResolution, unit, initialValue), maxValue - minValue + 1);
    }
}
