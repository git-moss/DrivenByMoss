// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.electra.one;

import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.IEnumSetting;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.ArpeggiatorMode;

import java.util.List;


/**
 * The configuration settings for Electra.One.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ElectraOneConfiguration extends AbstractConfiguration
{
    /** Display log state. */
    public static final Integer LOG_TO_CONSOLE        = Integer.valueOf (50);

    private boolean             isLogToConsoleEnabled = false;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param arpeggiatorModes The available arpeggiator modes
     */
    public ElectraOneConfiguration (final IHost host, final IValueChanger valueChanger, final List<ArpeggiatorMode> arpeggiatorModes)
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

        this.activateBehaviourOnStopSetting (globalSettings);

        ///////////////////////////
        // Workflow

        this.activateExcludeDeactivatedItemsSetting (globalSettings);
        this.activateNewClipLengthSetting (globalSettings);
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


    /**
     * Is logging to console enabled?
     *
     * @return True if enabled
     */
    public boolean isLogToConsoleEnabled ()
    {
        return this.isLogToConsoleEnabled;
    }
}
