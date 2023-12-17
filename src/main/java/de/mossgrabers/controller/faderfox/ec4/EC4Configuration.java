// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.faderfox.ec4;

import java.util.List;

import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.IActionSetting;
import de.mossgrabers.framework.configuration.IEnumSetting;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.ArpeggiatorMode;


/**
 * The configuration settings for the Faderfox EC4.
 *
 * @author Jürgen Moßgraber
 */
public class EC4Configuration extends AbstractConfiguration
{
    /** Display log state. */
    public static final Integer    SETUP_SLOT                  = Integer.valueOf (50);

    private static final String [] SETUP_SLOTS                 = new String []
    {
        "1",
        "2",
        "3",
        "4",
        "5",
        "6",
        "7",
        "8",
        "9",
        "10",
        "11",
        "12",
        "13",
        "14",
        "15",
        "16"
    };

    private static final String    CATEGORY_ASSIGNABLE_BUTTONS = "Functions";

    private int                    setupSlot                   = 0;
    private final String []        assignableFunctionActions   = new String [4];


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param arpeggiatorModes The available arpeggiator modes
     */
    public EC4Configuration (final IHost host, final IValueChanger valueChanger, final List<ArpeggiatorMode> arpeggiatorModes)
    {
        super (host, valueChanger, arpeggiatorModes);
    }


    /** {@inheritDoc} */
    @Override
    public void init (final ISettingsUI globalSettings, final ISettingsUI documentSettings)
    {
        ///////////////////////////
        // Hardware

        this.activateHardwareSettings (globalSettings);

        ///////////////////////////
        // Transport

        this.activateBehaviourOnPauseSetting (globalSettings);

        ///////////////////////////
        // Assignable user functions

        this.activateAssignableSettings (globalSettings);

        ///////////////////////////
        // Workflow

        this.activateExcludeDeactivatedItemsSetting (globalSettings);
        this.activateNewClipLengthSetting (globalSettings);
        this.activateKnobSpeedSetting (globalSettings);
    }


    private void activateHardwareSettings (final ISettingsUI settingsUI)
    {
        final IEnumSetting setupSlotSetting = settingsUI.getEnumSetting ("Setup slot which contains the DrivenByMoss template", CATEGORY_HARDWARE_SETUP, SETUP_SLOTS, SETUP_SLOTS[0]);
        setupSlotSetting.addValueObserver (value -> {
            this.setupSlot = Integer.parseInt (value) - 1;
            this.notifyObservers (SETUP_SLOT);
        });

        this.isSettingActive.add (SETUP_SLOT);
    }


    private void activateAssignableSettings (final ISettingsUI settingsUI)
    {
        for (int i = 0; i < this.assignableFunctionActions.length; i++)
        {
            final int pos = i;
            final IActionSetting actionSetting = settingsUI.getActionSetting ("User " + (i + 1), CATEGORY_ASSIGNABLE_BUTTONS);
            actionSetting.addValueObserver (value -> this.assignableFunctionActions[pos] = actionSetting.get ());
        }
    }


    /**
     * Get the index of the setup slot.
     *
     * @return The setup slot, 0-15
     */
    public int getSetupSlot ()
    {
        return this.setupSlot;
    }


    /**
     * Get the selected action to execute.
     *
     * @param index The index of the assignable, 0-3
     * @return The ID of the action to execute
     */
    public String getAssignableAction (final int index)
    {
        return this.assignableFunctionActions[index];
    }
}
