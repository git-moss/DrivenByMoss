// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.apc;

import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.controller.ValueChanger;

import com.bitwig.extension.controller.api.Preferences;


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
     * @param valueChanger The value changer
     */
    public APCConfiguration (final ValueChanger valueChanger)
    {
        super (valueChanger);
    }


    /** {@inheritDoc} */
    @Override
    public void init (final Preferences prefs)
    {
        ///////////////////////////
        // Play and Sequence

        this.activateQuantizeAmountSetting (prefs);

        ///////////////////////////
        // Scale

        this.activateScaleSetting (prefs);
        this.activateScaleBaseSetting (prefs);
        this.activateScaleInScaleSetting (prefs);
        this.activateScaleLayoutSetting (prefs);

        ///////////////////////////
        // Workflow

        this.activateBehaviourOnStopSetting (prefs);
        this.activateSelectClipOnLaunchSetting (prefs);
        this.activateNewClipLengthSetting (prefs);
    }
}
