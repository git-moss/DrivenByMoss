// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.slmkiii;

import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.IEnumSetting;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.ArpeggiatorMode;

import java.util.List;


/**
 * The configuration settings for the Novation SL MkIII.
 *
 * @author Jürgen Moßgraber
 */
public class SLMkIIIConfiguration extends AbstractConfiguration
{
    /** Setting for the ribbon mode. */
    public static final Integer ENABLE_FADERS     = Integer.valueOf (50);
    /** Setting for dis-/enabling the lightguide. */
    public static final Integer ENABLE_LIGHTGUIDE = Integer.valueOf (51);

    private boolean             enableFaders      = true;
    private boolean             enableLightguide  = true;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param arpeggiatorModes The available arpeggiator modes
     */
    public SLMkIIIConfiguration (final IHost host, final IValueChanger valueChanger, final List<ArpeggiatorMode> arpeggiatorModes)
    {
        super (host, valueChanger, arpeggiatorModes);
    }


    /** {@inheritDoc} */
    @Override
    public void init (final ISettingsUI globalSettings, final ISettingsUI documentSettings)
    {
        ///////////////////////////
        // Scale for light guide

        this.activateScaleBaseSetting (documentSettings);
        this.activateScaleSetting (documentSettings);
        this.activateScaleInScaleSetting (documentSettings);

        ///////////////////////////
        // Workflow

        this.activateExcludeDeactivatedItemsSetting (globalSettings);
        this.activateBehaviourOnStopSetting (globalSettings);
        this.activateBehaviourOnPauseSetting (globalSettings);
        this.activateNewClipLengthSetting (globalSettings);
        this.activateKnobSpeedSetting (globalSettings);

        final IEnumSetting enableFadersSetting = globalSettings.getEnumSetting ("Enable Faders", CATEGORY_WORKFLOW, ON_OFF_OPTIONS, ON_OFF_OPTIONS[1]);
        enableFadersSetting.addValueObserver (value -> {
            this.enableFaders = "On".equals (value);
            this.notifyObservers (ENABLE_FADERS);
        });
        this.isSettingActive.add (ENABLE_FADERS);

        final IEnumSetting enableLightguideSetting = globalSettings.getEnumSetting ("Enable Lightguide", CATEGORY_WORKFLOW, ON_OFF_OPTIONS, ON_OFF_OPTIONS[1]);
        enableLightguideSetting.addValueObserver (value -> {
            this.enableLightguide = "On".equals (value);
            this.notifyObservers (ENABLE_LIGHTGUIDE);
        });
        this.isSettingActive.add (ENABLE_LIGHTGUIDE);

        ///////////////////////////
        // Session

        this.activateSelectClipOnLaunchSetting (globalSettings);
        this.activateDrawRecordStripeSetting (globalSettings);
        this.activateActionForRecArmedPad (globalSettings);
    }


    /**
     * Check if the faders should be active.
     *
     * @return True if faders are enabled
     */
    public boolean areFadersEnabled ()
    {
        return this.enableFaders;
    }


    /**
     * Check if the light guide should be active.
     *
     * @return True if light guide is active
     */
    public boolean isLightEnabled ()
    {
        return this.enableLightguide;
    }
}
