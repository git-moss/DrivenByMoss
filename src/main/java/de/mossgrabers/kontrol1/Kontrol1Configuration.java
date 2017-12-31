// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.kontrol1;

import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.controller.ValueChanger;

import com.bitwig.extension.controller.api.Preferences;
import com.bitwig.extension.controller.api.SettableEnumValue;


/**
 * The configuration settings for Kontrol1.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Kontrol1Configuration extends AbstractConfiguration
{
    private static final Integer SCALE_IS_ACTIVE = Integer.valueOf (40);

    private SettableEnumValue    scaleIsActiveSetting;
    private boolean              scaleIsActive;


    /**
     * Constructor.
     *
     * @param valueChanger The value changer
     */
    public Kontrol1Configuration (final ValueChanger valueChanger)
    {
        super (valueChanger);
    }


    /** {@inheritDoc} */
    @Override
    public void init (final Preferences preferences)
    {
        ///////////////////////////
        // Scale

        this.scaleIsActiveSetting = preferences.getEnumSetting ("Is active", CATEGORY_SCALES, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        this.scaleIsActiveSetting.addValueObserver (value -> {
            this.scaleIsActive = "On".equals (value);
            this.notifyObservers (SCALE_IS_ACTIVE);
        });

        this.activateScaleSetting (preferences);
        this.activateScaleBaseSetting (preferences);
        this.activateScaleInScaleSetting (preferences);

        ///////////////////////////
        // Transport

        this.activateBehaviourOnStopSetting (preferences);
        this.activateFlipRecordSetting (preferences);

        ///////////////////////////
        // Workflow

        this.activateEnableVUMetersSetting (preferences);
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
