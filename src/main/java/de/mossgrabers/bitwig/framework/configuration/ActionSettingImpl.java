// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
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
public class ActionSettingImpl implements IActionSetting
{
    private final SettableEnumValue              categorySetting;
    private final Map<String, String>            actionsMap;
    private final Map<String, SettableEnumValue> categoryActionsSettings;
    private final Map<String, String>            actionCategories;
    private IValueObserver<String>               observer;


    /**
     * Constructor.
     *
     * @param categorySetting The action category enumeration
     * @param categoryActionsSettings The category to action name map
     * @param actionsMap The action map for looking up the ID of the selected action
     * @param actionCategories
     */
    public ActionSettingImpl (final SettableEnumValue categorySetting, final Map<String, SettableEnumValue> categoryActionsSettings, final Map<String, String> actionsMap, final Map<String, String> actionCategories)
    {
        this.categorySetting = categorySetting;
        this.categoryActionsSettings = categoryActionsSettings;
        this.actionsMap = actionsMap;
        this.actionCategories = actionCategories;

        this.categorySetting.addValueObserver (category -> {

            final SettableEnumValue categoryActionsSetting = this.categoryActionsSettings.get (category);
            if (categoryActionsSetting == null)
                return;

            // Only show the actions list of the category
            for (final SettableEnumValue setting: this.categoryActionsSettings.values ())
            {
                if (setting == categoryActionsSetting)
                    ((Setting) setting).show ();
                else
                    ((Setting) setting).hide ();
            }

        });
    }


    /** {@inheritDoc} */
    @Override
    public void set (final String actionID)
    {
        final String category = this.actionCategories.get (actionID);
        final String actionName = this.actionsMap.get (actionID);

        // Could only happen if actions would be removed
        if (category == null || actionName == null)
            return;

        // Select the category of the action
        this.categorySetting.set (category);

        final SettableEnumValue categoryActionsSetting = this.categoryActionsSettings.get (category);
        if (categoryActionsSetting == null)
            return;

        // Only show the actions list of the category
        for (final SettableEnumValue setting: this.categoryActionsSettings.values ())
        {
            if (setting == categoryActionsSetting)
                ((Setting) setting).show ();
            else
                ((Setting) setting).hide ();
        }

        // Finally select the action
        categoryActionsSetting.set (actionName);
    }


    /** {@inheritDoc} */
    @Override
    public String get ()
    {
        // Get the setting for the selected category
        final String selectedCategory = this.categorySetting.get ();
        final SettableEnumValue setting = this.categoryActionsSettings.get (selectedCategory);
        if (setting == null)
            return this.actionsMap.keySet ().iterator ().next ();

        // Get and return the ID of the selected action
        final String actionName = setting.get ();
        for (final Map.Entry<String, String> e: this.actionsMap.entrySet ())
        {
            if (e.getValue ().equals (actionName))
                return e.getKey ();
        }
        return this.actionsMap.keySet ().iterator ().next ();
    }


    /** {@inheritDoc} */
    @Override
    public void addValueObserver (final IValueObserver<String> observer)
    {
        this.observer = observer;

        this.categorySetting.addValueObserver (value -> this.notifyOberserver ());
        for (final SettableEnumValue setting: this.categoryActionsSettings.values ())
            setting.addValueObserver (value -> this.notifyOberserver ());

        // Directly fire the current value
        observer.update (this.get ());
    }


    private void notifyOberserver ()
    {
        this.observer.update (this.get ());
    }


    /** {@inheritDoc} */
    @Override
    public void setEnabled (final boolean enable)
    {
        // Not used, implement if required
    }


    /** {@inheritDoc} */
    @Override
    public void setVisible (final boolean visible)
    {
        // Not used, implement if required
    }
}
