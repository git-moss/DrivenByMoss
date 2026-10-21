// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.electra.one.base;

import java.util.Arrays;
import java.util.List;

import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.IActionSetting;
import de.mossgrabers.framework.configuration.IEnumSetting;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.ArpeggiatorMode;


/**
 * The base configuration settings for all Electra One models.
 *
 * @author Jürgen Moßgraber
 */
public abstract class AbstractElectraOneConfiguration extends AbstractConfiguration
{
    /** A knob touch combination configuration change. */
    public static final Integer   KNOB_TOUCH_COMBINATION_COMMANDS = Integer.valueOf (NEXT_SETTING_ID);

    protected static final String CATEGORY_ASSIGNABLE_BUTTONS     = "Assignable button touches";

    /** Display log state. */
    public static final Integer   LOG_TO_CONSOLE                  = Integer.valueOf (50);

    protected boolean             isLogToConsoleEnabled           = false;
    protected final int []        assignableFunctions;
    protected final String []     assignableFunctionActions;

    private final String []       assignableButtonNames;
    private final int []          assignableButtonDefaults;
    private final String []       assignableValues;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param arpeggiatorModes The available arpeggiator-modes
     * @param assignableButtonNames The names of the assignable button combinations
     * @param assignableButtonDefaults The default values of the assignable button combinations
     * @param assignableValues The assignable value names
     */
    protected AbstractElectraOneConfiguration (final IHost host, final IValueChanger valueChanger, final List<ArpeggiatorMode> arpeggiatorModes, final String [] assignableButtonNames, final int [] assignableButtonDefaults, final String [] assignableValues)
    {
        super (host, valueChanger, arpeggiatorModes);

        this.assignableButtonNames = assignableButtonNames;
        this.assignableButtonDefaults = assignableButtonDefaults;
        this.assignableValues = assignableValues;

        this.assignableFunctions = new int [assignableButtonNames.length];
        this.assignableFunctionActions = new String [assignableButtonNames.length];

        Arrays.fill (this.assignableFunctions, 0);
        Arrays.fill (this.assignableFunctionActions, "");
    }


    /** {@inheritDoc} */
    @Override
    public void init (final ISettingsUI globalSettings, final ISettingsUI documentSettings)
    {
        // ----------------------------------------------------------------
        // Hardware

        this.activateHardwareSettings (globalSettings);
        this.activateKnobSpeedSetting (globalSettings, 100, 20);

        // ----------------------------------------------------------------
        // Transport

        this.activateBehaviourOnPauseSetting (globalSettings);

        // ----------------------------------------------------------------
        // Workflow

        this.activateExcludeDeactivatedItemsSetting (globalSettings);
        this.activateNewClipLengthSetting (globalSettings);

        this.activateAssignableSettings (globalSettings);
    }


    private void activateHardwareSettings (final ISettingsUI settingsUI)
    {
        final IEnumSetting logToConsoleSetting = settingsUI.getEnumSetting ("Enable Electra.One logging (written to Controller Script Console)", CATEGORY_HARDWARE_SETUP, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        logToConsoleSetting.addValueObserver (value -> {
            this.isLogToConsoleEnabled = "On".equals (value);
            this.notifyObservers (LOG_TO_CONSOLE);
        });

        this.isSettingActive.add (LOG_TO_CONSOLE);
    }


    private void activateAssignableSettings (final ISettingsUI settingsUI)
    {
        for (int i = 0; i < this.assignableFunctions.length; i++)
        {
            final int pos = i;
            final IEnumSetting assignableSetting = settingsUI.getEnumSetting (this.assignableButtonNames[i], CATEGORY_ASSIGNABLE_BUTTONS, this.assignableValues, this.assignableValues[this.assignableButtonDefaults[i]]);
            assignableSetting.addValueObserver (value -> {
                this.assignableFunctions[pos] = lookupIndex (this.assignableValues, value);
                this.notifyObservers (KNOB_TOUCH_COMBINATION_COMMANDS);
            });

            final IActionSetting actionSetting = settingsUI.getActionSetting (this.assignableButtonNames[i] + " - Action", CATEGORY_ASSIGNABLE_BUTTONS);
            actionSetting.addValueObserver (value -> this.assignableFunctionActions[pos] = actionSetting.get ());
        }

        this.isSettingActive.add (KNOB_TOUCH_COMBINATION_COMMANDS);
    }


    /**
     * Is logging to console enabled?
     *
     * @return True if enabled
     */
    public boolean isLogToConsoleEnabled ()
    {
        return this.isLogToConsoleEnabled;
    }


    /**
     * Get the assignable function.
     *
     * @param index The index of the assignable
     * @return The function
     */
    public int getAssignable (final int index)
    {
        return this.assignableFunctions[index];
    }


    /**
     * If the assignable function is set to Action this method gets the selected action to execute.
     *
     * @param index The index of the assignable
     * @return The ID of the action to execute
     */
    public String getAssignableAction (final int index)
    {
        return this.assignableFunctionActions[index];
    }
}
