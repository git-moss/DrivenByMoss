// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.beatstep;

import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.IHost;


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
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param isPro Is Pro or MkII?
     */
    public BeatstepConfiguration (final IHost host, final IValueChanger valueChanger, final boolean isPro)
    {
        super (host, valueChanger);
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
    public void init (final ISettingsUI globalSettings, final ISettingsUI documentSettings)
    {
        ///////////////////////////
        // Scale

        this.activateScaleSetting (documentSettings);
        this.activateScaleBaseSetting (documentSettings);
        this.activateScaleInScaleSetting (documentSettings);
        this.activateScaleLayoutSetting (documentSettings);

        ///////////////////////////
        // Pad Sensitivity

        this.activateConvertAftertouchSetting (globalSettings);
    }
}
