// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.configuration;

/**
 * An interface to create user interface widgets for settings.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface ISettingsUI
{
    /**
     * Returns an enumeration setting that is shown either as a chooser or as a button group,
     * depending on the number of provided options.
     *
     * @param label The name of the setting, must not be null
     * @param category The name of the category, may not be null
     * @param options The string array that defines the allowed options for the button group or
     *            chooser
     * @param initialValue The initial string value, must be one of the items specified with the
     *            option argument
     * @return The object that encapsulates the requested enum setting
     */
    IEnumSetting getEnumSetting (final String label, final String category, final String [] options, final String initialValue);


    /**
     * Returns a string setting.
     *
     * @param label The name of the setting, must not be null
     * @param category The name of the category, may not be null
     * @param numChars The maximum length of the value
     * @param initialText The initial string value
     * @return The object that encapsulates the requested string setting
     */
    IStringSetting getStringSetting (final String label, final String category, final int numChars, final String initialText);


    /**
     * Returns a range integer setting.
     *
     * @param label The name of the setting, must not be null
     * @param category The name of the category, may not be null
     * @param minValue The minimum value
     * @param maxValue The maximum value
     * @param stepResolution The step size
     * @param unit The unit to display
     * @param initialValue The initial value
     * @return The object that encapsulates the requested setting
     */
    IIntegerSetting getRangeSetting (final String label, final String category, final int minValue, final int maxValue, final int stepResolution, final String unit, final int initialValue);


    /**
     * Returns a double setting.
     *
     * @param label The name of the setting, must not be null
     * @param category The name of the category, may not be null
     * @param minValue The minimum value
     * @param maxValue The maximum value
     * @param stepResolution The step size
     * @param unit The unit to display
     * @param initialValue The initial value
     * @return The object that encapsulates the requested setting
     */
    IDoubleSetting getNumberSetting (final String label, final String category, final double minValue, final double maxValue, final double stepResolution, final String unit, final double initialValue);
}
