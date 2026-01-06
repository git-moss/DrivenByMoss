// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.jam;

import java.util.List;

import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.IEnumSetting;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.constants.Capability;
import de.mossgrabers.framework.daw.midi.ArpeggiatorMode;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.view.Views;


/**
 * The configuration settings for Maschine Jam.
 *
 * @author Jürgen Moßgraber
 */
public class MaschineJamConfiguration extends AbstractConfiguration
{
    private static final Views [] STARTUP_VIEWS =
    {
        Views.PLAY,
        Views.CHORDS,
        Views.PIANO,
        Views.DRUM64,
        Views.DRUM,
        Views.DRUM4,
        Views.DRUM8,
        Views.SEQUENCER,
        Views.RAINDROPS,
        Views.POLY_SEQUENCER
    };

    private static final Modes [] STARTUP_MODES =
    {
        Modes.VOLUME,
        Modes.PAN,
        Modes.TRACK,
        Modes.SEND1,
        Modes.SEND2,
        Modes.SEND3,
        Modes.SEND4,
        Modes.SEND5,
        Modes.SEND6,
        Modes.SEND7,
        Modes.SEND8,
        Modes.DEVICE_PARAMS,
        Modes.USER
    };

    private IEnumSetting          slowFaderChangeSetting;
    private boolean               slowFaderChange;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param arpeggiatorModes The available arpeggiator modes
     */
    public MaschineJamConfiguration (final IHost host, final IValueChanger valueChanger, final List<ArpeggiatorMode> arpeggiatorModes)
    {
        super (host, valueChanger, arpeggiatorModes);
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
        // Note Repeat

        this.activateNoteRepeatSetting (documentSettings);

        ///////////////////////////
        // Transport

        this.activateRecordButtonSetting (globalSettings);
        this.activateShiftedRecordButtonSetting (globalSettings);
        this.activateBehaviourOnPauseSetting (globalSettings);

        ///////////////////////////
        // Play and Sequence

        this.activateAccentActiveSetting (globalSettings);
        this.activateAccentValueSetting (globalSettings);
        this.activateQuantizeAmountSetting (globalSettings);
        this.activateStartupViewSetting (globalSettings, STARTUP_VIEWS);
        this.activateStartWithSessionViewSetting (globalSettings);
        this.activateTurnOffScalePadsSetting (globalSettings);
        this.activateShowPlayedChordsSetting (globalSettings);

        ///////////////////////////
        // Drum Sequencer

        if (this.host.supports (Capability.HAS_DRUM_DEVICE))
            this.activateTurnOffEmptyDrumPadsSetting (globalSettings);

        ///////////////////////////
        // Session

        this.activateSelectClipOnLaunchSetting (globalSettings);
        this.activateActionForRecArmedPad (globalSettings);
        this.activateDrawRecordStripeSetting (globalSettings);
        this.activateFlipSessionSetting (globalSettings);

        ///////////////////////////
        // Workflow

        this.activateExcludeDeactivatedItemsSetting (globalSettings);
        this.activateSlowFaderChangeSetting (globalSettings);
        this.activateStartupModeSetting (globalSettings, STARTUP_MODES);
        this.activateNewClipLengthSetting (globalSettings);
        this.activateKnobSpeedSetting (globalSettings);
        this.activateFootswitchSetting (globalSettings, 0, "Footswitch (Tip)");
        this.activateFootswitchSetting (globalSettings, 1, "Footswitch (Ring)");
    }


    /**
     * Activate the slow fader change setting.
     *
     * @param settingsUI The settings
     */
    protected void activateSlowFaderChangeSetting (final ISettingsUI settingsUI)
    {
        this.slowFaderChangeSetting = settingsUI.getEnumSetting ("Slow Fader Change", CATEGORY_WORKFLOW, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        this.slowFaderChangeSetting.addValueObserver (value -> this.slowFaderChange = "On".equals (value));
    }


    /**
     * Is the slow fader change option enabled?
     *
     * @return True if enabled
     */
    public boolean isSlowFaderChange ()
    {
        return this.slowFaderChange;
    }


    /**
     * Toggle the slow fader change option.
     */
    public void toggleSlowFaderChange ()
    {
        this.slowFaderChangeSetting.set (ON_OFF_OPTIONS[this.slowFaderChange ? 0 : 1]);
    }
}
