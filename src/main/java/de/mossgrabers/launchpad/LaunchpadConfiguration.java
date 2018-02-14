// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.launchpad;

import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.controller.ValueChanger;

import com.bitwig.extension.controller.api.Preferences;


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
        this.activateFlipSessionSetting (prefs);
        if (this.isPro)
            this.activateFlipRecordSetting (prefs);
        this.activateAutoSelectDrumSetting (prefs);
        this.activateTurnOffEmptyDrumPadsSetting (prefs);
        this.activateNewClipLengthSetting (prefs);

        ///////////////////////////
        // Pad Sensitivity

        this.activateConvertAftertouchSetting (prefs);
    }
}
