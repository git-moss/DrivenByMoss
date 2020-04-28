// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.slmkiii;

import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.IEnumSetting;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.ArpeggiatorMode;


/**
 * The configuration settings for the Novation SL MkIII.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SLMkIIIConfiguration extends AbstractConfiguration
{
    /** Setting for the ribbon mode. */
    public static final Integer ENABLE_FADERS = Integer.valueOf (50);

    private boolean             enableFaders  = true;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param arpeggiatorModes The available arpeggiator modes
     */
    public SLMkIIIConfiguration (final IHost host, final IValueChanger valueChanger, final ArpeggiatorMode [] arpeggiatorModes)
    {
        super (host, valueChanger, arpeggiatorModes);
    }


    /** {@inheritDoc} */
    @Override
    public void init (final ISettingsUI globalSettings, final ISettingsUI documentSettings)
    {
        ///////////////////////////
        // Workflow

        this.activateExcludeDeactivatedItemsSetting (globalSettings);
        this.activateBehaviourOnStopSetting (globalSettings);
        this.activateNewClipLengthSetting (globalSettings);

        final IEnumSetting enableFadersSetting = globalSettings.getEnumSetting ("Enable Faders", CATEGORY_WORKFLOW, ON_OFF_OPTIONS, ON_OFF_OPTIONS[1]);
        enableFadersSetting.addValueObserver (value -> {
            this.enableFaders = "On".equals (value);
            this.notifyObservers (ENABLE_FADERS);
        });

        this.activateUserPageNamesSetting (globalSettings);

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
}
