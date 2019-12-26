// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchpad;

import de.mossgrabers.controller.launchpad.definition.ILaunchpadControllerDefinition;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;


/**
 * The configuration settings for Launchpad.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class LaunchpadConfiguration extends AbstractConfiguration
{
    private final ILaunchpadControllerDefinition definition;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param definition The Launchpad definition
     */
    public LaunchpadConfiguration (final IHost host, final IValueChanger valueChanger, final ILaunchpadControllerDefinition definition)
    {
        super (host, valueChanger);
        this.definition = definition;
    }


    /** {@inheritDoc} */
    @Override
    public void init (final ISettingsUI globalSettings, final ISettingsUI documentSettings)
    {
        ///////////////////////////
        // Scale

        this.activateScaleSetting (documentSettings);
        this.activateScaleBaseSetting (documentSettings);
        this.activateScaleInScaleSetting (documentSettings);
        this.activateScaleLayoutSetting (documentSettings);

        ///////////////////////////
        // Play and Sequence

        this.activateQuantizeAmountSetting (globalSettings);

        ///////////////////////////
        // Workflow

        this.activateBehaviourOnStopSetting (globalSettings);
        this.activateSelectClipOnLaunchSetting (globalSettings);
        this.activateFlipSessionSetting (globalSettings);
        if (this.definition.isPro ())
            this.activateFlipRecordSetting (globalSettings);
        this.activateAutoSelectDrumSetting (globalSettings);
        this.activateTurnOffEmptyDrumPadsSetting (globalSettings);
        this.activateNewClipLengthSetting (globalSettings);

        ///////////////////////////
        // Pad Sensitivity

        this.activateConvertAftertouchSetting (globalSettings);
    }
}
