// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.usb.mkii;

import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.IEnumSetting;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.IValueChanger;


/**
 * The configuration settings for Kontrol 2.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Kontrol2Configuration extends AbstractConfiguration
{
    private static final Integer SCALE_IS_ACTIVE = Integer.valueOf (40);

    private IEnumSetting         scaleIsActiveSetting;
    private boolean              scaleIsActive;


    /**
     * Constructor.
     *
     * @param valueChanger The value changer
     */
    public Kontrol2Configuration (final IValueChanger valueChanger)
    {
        super (valueChanger);
    }


    /** {@inheritDoc} */
    @Override
    public void init (final ISettingsUI settingsUI)
    {
        ///////////////////////////
        // Scale

        this.scaleIsActiveSetting = settingsUI.getEnumSetting ("Is active", CATEGORY_SCALES, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        this.scaleIsActiveSetting.addValueObserver (value -> {
            this.scaleIsActive = "On".equals (value);
            this.notifyObservers (SCALE_IS_ACTIVE);
        });

        this.activateScaleSetting (settingsUI);
        this.activateScaleBaseSetting (settingsUI);
        this.activateScaleInScaleSetting (settingsUI);

        ///////////////////////////
        // Transport

        this.activateBehaviourOnStopSetting (settingsUI);
        this.activateFlipRecordSetting (settingsUI);

        ///////////////////////////
        // Workflow

        this.activateEnableVUMetersSetting (settingsUI);
    }


    /**
     * True if the scale is active.
     *
     * @return True if the scale is active.
     */
    public boolean isScaleIsActive ()
    {
        return this.scaleIsActive;
    }


    /**
     * Toggle if the scale is active.
     */
    public void toggleScaleIsActive ()
    {
        this.scaleIsActiveSetting.set (ON_OFF_OPTIONS[this.scaleIsActive ? 0 : 1]);
    }
}
