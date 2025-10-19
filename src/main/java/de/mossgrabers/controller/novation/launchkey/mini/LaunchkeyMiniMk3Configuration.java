// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchkey.mini;

import java.util.List;

import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.ArpeggiatorMode;
import de.mossgrabers.framework.mode.Modes;


/**
 * The configuration settings for Launchkey Mini Mk3.
 *
 * @author Jürgen Moßgraber
 */
public class LaunchkeyMiniMk3Configuration extends AbstractConfiguration
{
    private static final Modes [] STARTUP_MODES =
    {
        Modes.VOLUME,
        Modes.PAN,
        Modes.SEND1,
        Modes.SEND2,
        Modes.DEVICE_PARAMS,
        Modes.USER
    };


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param arpeggiatorModes The available arpeggiator modes
     */
    public LaunchkeyMiniMk3Configuration (final IHost host, final IValueChanger valueChanger, final List<ArpeggiatorMode> arpeggiatorModes)
    {
        super (host, valueChanger, arpeggiatorModes);
    }


    /** {@inheritDoc} */
    @Override
    public void init (final ISettingsUI globalSettings, final ISettingsUI documentSettings)
    {
        ///////////////////////////
        // Transport

        this.activateBehaviourOnPauseSetting (globalSettings);
        this.activateRecordButtonSetting (globalSettings);
        this.activateShiftedRecordButtonSetting (globalSettings);
        this.activateNewClipLengthSetting (globalSettings);

        ///////////////////////////
        // Workflow

        this.activateExcludeDeactivatedItemsSetting (globalSettings);
        this.activateStartupModeSetting (globalSettings, STARTUP_MODES);
        this.activateIncludeMasterSetting (globalSettings);
        this.activateSelectClipOnLaunchSetting (globalSettings);
    }
}
