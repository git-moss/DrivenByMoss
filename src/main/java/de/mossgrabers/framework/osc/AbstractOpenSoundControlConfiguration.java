// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.osc;

import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.IEnumSetting;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.ArpeggiatorMode;

import java.util.List;


/**
 * Base class for configurations which use OSC.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractOpenSoundControlConfiguration extends AbstractConfiguration implements IOpenSoundControlConfiguration
{
    /** ID for logging input commands option. */
    public static final Integer   LOG_INPUT_COMMANDS        = Integer.valueOf (50);
    /** ID for logging output commands option. */
    public static final Integer   LOG_OUTPUT_COMMANDS       = Integer.valueOf (51);
    /** ID for filtering heartbeat OSC messages from logging. */
    public static final Integer   FILTER_HEARTBEAT_COMMANDS = Integer.valueOf (52);

    protected static final String DEFAULT_SERVER            = "127.0.0.1";

    private boolean               logInputCommands          = false;
    private boolean               logOutputCommands         = false;
    private boolean               filterHeartbeatCommands   = false;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param arpeggiatorModes The available arpeggiator modes
     */
    protected AbstractOpenSoundControlConfiguration (final IHost host, final IValueChanger valueChanger, final List<ArpeggiatorMode> arpeggiatorModes)
    {
        super (host, valueChanger, arpeggiatorModes);
    }


    /**
     * Activate OSC logging properties.
     *
     * @param settingsUI The settings
     */
    protected void activateOSCLogging (final ISettingsUI settingsUI)
    {
        final IEnumSetting logInputCommandsSetting = settingsUI.getEnumSetting ("Log input commands", CATEGORY_DEBUG, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        logInputCommandsSetting.addValueObserver (value -> {
            this.logInputCommands = "On".equals (value);
            this.notifyObservers (LOG_INPUT_COMMANDS);
        });
        final IEnumSetting logOutputCommandsSetting = settingsUI.getEnumSetting ("Log output commands", CATEGORY_DEBUG, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        logOutputCommandsSetting.addValueObserver (value -> {
            this.logOutputCommands = "On".equals (value);
            this.notifyObservers (LOG_OUTPUT_COMMANDS);
        });
        final IEnumSetting filterHeartbeatCommandsSetting = settingsUI.getEnumSetting ("Filter heartbeat commands (ping etc.)", CATEGORY_DEBUG, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        filterHeartbeatCommandsSetting.addValueObserver (value -> {
            this.filterHeartbeatCommands = "On".equals (value);
            this.notifyObservers (FILTER_HEARTBEAT_COMMANDS);
        });

        this.isSettingActive.add (LOG_INPUT_COMMANDS);
        this.isSettingActive.add (LOG_OUTPUT_COMMANDS);
        this.isSettingActive.add (FILTER_HEARTBEAT_COMMANDS);
    }


    /** {@inheritDoc} */
    @Override
    public boolean shouldLogInputCommands ()
    {
        return this.logInputCommands;
    }


    /** {@inheritDoc} */
    @Override
    public boolean shouldLogOutputCommands ()
    {
        return this.logOutputCommands;
    }


    /** {@inheritDoc} */
    @Override
    public boolean filterHeartbeatMessages ()
    {
        return this.filterHeartbeatCommands;
    }
}
