// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.slmkiii;

import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.IHost;


/**
 * The configuration settings for the Novation SL MkIII.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SLMkIIIConfiguration extends AbstractConfiguration
{
    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     */
    public SLMkIIIConfiguration (final IHost host, final IValueChanger valueChanger)
    {
        super (host, valueChanger);
    }


    /** {@inheritDoc} */
    @Override
    public void init (final ISettingsUI settingsUI)
    {
        ///////////////////////////
        // Workflow

        this.activateBehaviourOnStopSetting (settingsUI);
        this.activateNewClipLengthSetting (settingsUI);

        ///////////////////////////
        // Session
        if (this.host.hasClips ())
        {
            this.activateSelectClipOnLaunchSetting (settingsUI);
            this.activateDrawRecordStripeSetting (settingsUI);
            this.activateActionForRecArmedPad (settingsUI);
        }
    }
}
