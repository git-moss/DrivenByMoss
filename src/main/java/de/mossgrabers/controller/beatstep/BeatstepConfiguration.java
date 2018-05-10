// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.beatstep;

import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.IValueChanger;


/**
 * The configuration settings for Beatstep.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class BeatstepConfiguration extends AbstractConfiguration
{
    private final boolean isPro;


    /**
     * Constructor.
     *
     * @param valueChanger The value changer
     * @param isPro Is Pro or MkII?
     */
    public BeatstepConfiguration (final IValueChanger valueChanger, final boolean isPro)
    {
        super (valueChanger);
        this.isPro = isPro;
    }


    /**
     * Is it Beatstep Pro?
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
        // Scale

        this.activateScaleSetting (settingsUI);
        this.activateScaleBaseSetting (settingsUI);
        this.activateScaleInScaleSetting (settingsUI);
        this.activateScaleLayoutSetting (settingsUI);

        ///////////////////////////
        // Pad Sensitivity

        this.activateConvertAftertouchSetting (settingsUI);
    }
}
