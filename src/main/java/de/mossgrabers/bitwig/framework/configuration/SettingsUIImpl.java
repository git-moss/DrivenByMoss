// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.configuration;

import de.mossgrabers.framework.configuration.IActionSetting;
import de.mossgrabers.framework.configuration.IBooleanSetting;
import de.mossgrabers.framework.configuration.IColorSetting;
import de.mossgrabers.framework.configuration.IDoubleSetting;
import de.mossgrabers.framework.configuration.IEnumSetting;
import de.mossgrabers.framework.configuration.IIntegerSetting;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.configuration.ISignalSetting;
import de.mossgrabers.framework.configuration.IStringSetting;
import de.mossgrabers.framework.controller.color.ColorEx;

import com.bitwig.extension.controller.api.Action;
import com.bitwig.extension.controller.api.ActionCategory;
import com.bitwig.extension.controller.api.Application;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.SettableEnumValue;
import com.bitwig.extension.controller.api.Settings;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


/**
 * The Bitwig implementation to create user interface widgets for settings.
 *
 * @author Jürgen Moßgraber
 */
public class SettingsUIImpl implements ISettingsUI
{
    private final ControllerHost         host;
    private final Settings               preferences;
    private String []                    categoryNames;
    private final Map<String, String []> categoriesActionIDs = new HashMap<> ();
    private final Map<String, String>    actionCategories    = new HashMap<> ();
    private final Map<String, String>    actionIDsNames      = new HashMap<> ();


    /**
     * Constructor.
     *
     * @param host The Bitwig controller host
     * @param settings The Bitwig preferences
     */
    public SettingsUIImpl (final ControllerHost host, final Settings settings)
    {
        this.host = host;
        this.preferences = settings;
    }


    /** {@inheritDoc} */
    @Override
    public IEnumSetting getEnumSetting (final String label, final String category, final String [] options, final String initialValue)
    {
        return new EnumSettingImpl (this.preferences.getEnumSetting (label, category, options, initialValue));
    }


    /** {@inheritDoc} */
    @Override
    public IBooleanSetting getBooleanSetting (final String label, final String category, final boolean initialValue)
    {
        return new BooleanSettingImpl (this.preferences.getBooleanSetting (label, category, initialValue));
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
        return new IntegerSettingImpl (this.preferences.getNumberSetting (label, category, minValue, maxValue, stepResolution, unit, initialValue), minValue, maxValue - minValue + 1);
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


    /** {@inheritDoc} */
    @Override
    public IActionSetting getActionSetting (final String label, final String category)
    {
        // Has to be here since it must be executed in the initialize method!
        this.prepareActions ();

        final String cat = category + " - " + label;

        final SettableEnumValue categorySetting = this.preferences.getEnumSetting (label + ": Category", cat, this.categoryNames, this.categoryNames[0]);
        final Map<String, SettableEnumValue> categoryActionsSettings = new TreeMap<> ();

        for (final String categoryName: this.categoryNames)
        {
            final String [] actionNames = this.categoriesActionIDs.get (categoryName);
            if (actionNames.length > 1)
                categoryActionsSettings.put (categoryName, this.preferences.getEnumSetting (label + ": " + categoryName + " Actions", cat, actionNames, actionNames[0]));
        }

        return new ActionSettingImpl (categorySetting, categoryActionsSettings, this.actionIDsNames, this.actionCategories);
    }


    private synchronized void prepareActions ()
    {
        if (this.categoryNames != null)
            return;

        final Application application = this.host.createApplication ();
        final ActionCategory [] categories = application.getActionCategories ();
        this.categoryNames = new String [categories.length];

        for (int i = 0; i < categories.length; i++)
        {
            final String categoryName = categories[i].getName ();
            this.categoryNames[i] = categoryName;

            final Action [] actions = categories[i].getActions ();
            final String [] actionNames = new String [actions.length];

            for (int j = 0; j < actions.length; j++)
            {
                actionNames[j] = actions[j].getName ();
                final String id = actions[j].getId ();
                this.actionIDsNames.put (id, actionNames[j]);
                this.actionCategories.put (id, categoryName);
            }

            this.categoriesActionIDs.put (categoryName, actionNames);
        }
    }
}
