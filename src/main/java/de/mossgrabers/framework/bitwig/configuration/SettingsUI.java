// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.bitwig.configuration;

import de.mossgrabers.framework.configuration.IColorSetting;
import de.mossgrabers.framework.configuration.IDoubleSetting;
import de.mossgrabers.framework.configuration.IEnumSetting;
import de.mossgrabers.framework.configuration.IIntegerSetting;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.configuration.ISignalSetting;
import de.mossgrabers.framework.configuration.IStringSetting;
import de.mossgrabers.framework.controller.color.ColorEx;

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
    public IStringSetting getStringSetting (final String label, final String category, final int numChars, final String initialText)
    {
        return new StringSettingImpl (this.preferences.getStringSetting (label, category, numChars, initialText));
    }


    /** {@inheritDoc} */
    @Override
    public IDoubleSetting getNumberSetting (final String label, final String category, final double minValue, final double maxValue, final double stepResolution, final String unit, final double initialValue)
    {
        return new DoubleSettingImpl (this.preferences.getNumberSetting (label, category, minValue, maxValue, stepResolution, unit, initialValue));
    }


    /** {@inheritDoc} */
    @Override
    public IIntegerSetting getRangeSetting (final String label, final String category, final int minValue, final int maxValue, final int stepResolution, final String unit, final int initialValue)

    {
        return new IntegerSettingImpl (this.preferences.getNumberSetting (label, category, minValue, maxValue, stepResolution, unit, initialValue), maxValue - minValue + 1);
    }


    /** {@inheritDoc} */
    @Override
    public ISignalSetting getSignalSetting (final String label, final String category, final String action)
    {
        return new SignalSettingImpl (this.preferences.getSignalSetting (label, category, action));
    }


    /** {@inheritDoc} */
    @Override
    public IColorSetting getColorSetting (final String label, final String category, final ColorEx defaultColor)
    {
        final com.bitwig.extension.api.Color color = com.bitwig.extension.api.Color.fromRGB (defaultColor.getRed (), defaultColor.getGreen (), defaultColor.getBlue ());
        return new ColorSettingImpl (this.preferences.getColorSetting (label, category, color));
    }
}
