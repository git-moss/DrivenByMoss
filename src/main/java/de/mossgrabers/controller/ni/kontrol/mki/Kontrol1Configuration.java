// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.kontrol.mki;

import java.util.List;

import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.IEnumSetting;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.ArpeggiatorMode;


/**
 * The configuration settings for Kontrol 1.
 *
 * @author Jürgen Moßgraber
 */
public class Kontrol1Configuration extends AbstractConfiguration
{
    /** ID for enable scale setting. */
    public static final Integer SCALE_IS_ACTIVE = Integer.valueOf (NEXT_SETTING_ID);

    private IEnumSetting        scaleIsActiveSetting;
    private boolean             scaleIsActive;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param arpeggiatorModes The available arpeggiator modes
     */
    public Kontrol1Configuration (final IHost host, final IValueChanger valueChanger, final List<ArpeggiatorMode> arpeggiatorModes)
    {
        super (host, valueChanger, arpeggiatorModes);
    }


    /** {@inheritDoc} */
    @Override
    public void init (final ISettingsUI globalSettings, final ISettingsUI documentSettings)
    {
        ///////////////////////////
        // Scale

        this.scaleIsActiveSetting = documentSettings.getEnumSetting ("Is active", CATEGORY_SCALES, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        this.scaleIsActiveSetting.addValueObserver (value -> {
            this.scaleIsActive = "On".equals (value);
            this.notifyObservers (SCALE_IS_ACTIVE);
        });
        this.isSettingActive.add (SCALE_IS_ACTIVE);

        this.activateScaleSetting (documentSettings);
        this.activateScaleBaseSetting (documentSettings);

        ///////////////////////////
        // Play and Sequence

        this.activateShowPlayedChordsSetting (globalSettings);

        ///////////////////////////
        // Transport

        this.activateBehaviourOnStopSetting (globalSettings);
        this.activateBehaviourOnPauseSetting (globalSettings);
        this.activateFlipRecordSetting (globalSettings);

        ///////////////////////////
        // Workflow

        this.activateExcludeDeactivatedItemsSetting (globalSettings);
        this.activateEnableVUMetersSetting (globalSettings);
        this.activateKnobSpeedSetting (globalSettings);
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
