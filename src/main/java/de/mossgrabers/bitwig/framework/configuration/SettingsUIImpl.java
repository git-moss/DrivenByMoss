// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
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
import com.bitwig.extension.controller.api.Application;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.Settings;

import java.util.HashMap;
import java.util.Map;


/**
 * The Bitwig implementation to create user interface widgets for settings.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SettingsUIImpl implements ISettingsUI
{
    private final ControllerHost      host;
    private final Settings            preferences;
    private final Map<String, String> actionsList = new HashMap<> ();
    private String []                 formattedActions;


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
        // Has to be here since it must be executed in init()!
        if (this.formattedActions == null)
        {
            final Application application = this.host.createApplication ();
            final Action [] actions = application.getActions ();
            this.formattedActions = new String [actions.length];
            for (var i = 0; i < actions.length; i++)
            {
                this.formattedActions[i] = actions[i].getCategory ().getName () + ": " + actions[i].getName ();
                this.actionsList.put (this.formattedActions[i], actions[i].getId ());
            }
        }

        return new ActionSettingImpl (this.preferences.getEnumSetting (label, category, this.formattedActions, this.formattedActions[0]), this.actionsList);
    }
}
