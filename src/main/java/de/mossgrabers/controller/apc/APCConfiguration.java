// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.apc;

import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.IHost;


/**
 * The configuration settings for APC.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class APCConfiguration extends AbstractConfiguration
{
    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     */
    public APCConfiguration (final IHost host, final IValueChanger valueChanger)
    {
        super (host, valueChanger);
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
        this.activateNewClipLengthSetting (settingsUI);
    }
}
