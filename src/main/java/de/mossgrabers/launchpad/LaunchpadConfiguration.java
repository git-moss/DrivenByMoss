// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.launchpad;

import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.ValueChanger;


/**
 * The configuration settings for Launchpad.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class LaunchpadConfiguration extends AbstractConfiguration
{
    private final boolean isPro;


    /**
     * Constructor.
     *
     * @param valueChanger The value changer
     * @param isPro Is Pro or MkII?
     */
    public LaunchpadConfiguration (final ValueChanger valueChanger, final boolean isPro)
    {
        super (valueChanger);
        this.isPro = isPro;
    }


    /**
     * Is Launchpad Pro or MkII?
     *
     * @return True if Pro
     */
    public boolean isPro ()
    {
        return this.isPro;
    }


    /** {@inheritDoc} */
    @Override
    public void init (final ISettingsUI settingsUI)
    {
        ///////////////////////////
        // Play and Sequence

        this.activateQuantizeAmountSetting (settingsUI);

        ///////////////////////////
        // Scale

        this.activateScaleSetting (settingsUI);
        this.activateScaleBaseSetting (settingsUI);
        this.activateScaleInScaleSetting (settingsUI);
        this.activateScaleLayoutSetting (settingsUI);

        ///////////////////////////
        // Workflow

        this.activateBehaviourOnStopSetting (settingsUI);
        this.activateSelectClipOnLaunchSetting (settingsUI);
        this.activateFlipSessionSetting (settingsUI);
        if (this.isPro)
            this.activateFlipRecordSetting (settingsUI);
        this.activateAutoSelectDrumSetting (settingsUI);
        this.activateTurnOffEmptyDrumPadsSetting (settingsUI);
        this.activateNewClipLengthSetting (settingsUI);

        ///////////////////////////
        // Pad Sensitivity

        this.activateConvertAftertouchSetting (settingsUI);
    }
}
