// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.fire;

import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.IEnumSetting;
import de.mossgrabers.framework.configuration.IIntegerSetting;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.constants.Capability;
import de.mossgrabers.framework.daw.midi.ArpeggiatorMode;
import de.mossgrabers.framework.view.Views;

import java.util.List;


/**
 * The configuration settings for Fire.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class FireConfiguration extends AbstractConfiguration
{
    private static final Views [] PREFERRED_NOTE_VIEWS =
    {
        Views.PLAY,
        Views.PIANO,
        Views.DRUM64,
        Views.DRUM4,
        Views.SEQUENCER,
        Views.POLY_SEQUENCER
    };

    /** Setting for the pad brightness. */
    public static final Integer   PAD_BRIGHTNESS       = Integer.valueOf (50);
    /** Setting for the pad color saturation. */
    public static final Integer   PAD_SATURATION       = Integer.valueOf (51);

    private int                   padBrightness        = 100;
    private int                   padSaturation        = 100;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param arpeggiatorModes The available arpeggiator modes
     */
    public FireConfiguration (final IHost host, final IValueChanger valueChanger, final List<ArpeggiatorMode> arpeggiatorModes)
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
        // Session

        this.activateSelectClipOnLaunchSetting (globalSettings);
        this.activateDrawRecordStripeSetting (globalSettings);
        this.activateActionForRecArmedPad (globalSettings);

        ///////////////////////////
        // Transport

        this.activateBehaviourOnStopSetting (globalSettings);
        this.activateBehaviourOnPauseSetting (globalSettings);

        // Corrected label (removed automation)
        final IEnumSetting flipRecordSetting = globalSettings.getEnumSetting ("Flip arranger and clip record", CATEGORY_TRANSPORT, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        flipRecordSetting.addValueObserver (value -> {
            this.flipRecord = "On".equals (value);
            this.notifyObservers (FLIP_RECORD);
        });
        this.isSettingActive.add (FLIP_RECORD);

        ///////////////////////////
        // Play and Sequence

        this.activateAccentActiveSetting (globalSettings);
        this.activateAccentValueSetting (globalSettings);
        this.activateQuantizeAmountSetting (globalSettings);
        this.activateMidiEditChannelSetting (documentSettings);
        this.activatePreferredNoteViewSetting (globalSettings, PREFERRED_NOTE_VIEWS);
        this.activateStartWithSessionViewSetting (globalSettings);

        ///////////////////////////
        // Drum Sequencer

        if (this.host.supports (Capability.HAS_DRUM_DEVICE))
            this.activateTurnOffEmptyDrumPadsSetting (globalSettings);
        this.activateUseCombinationButtonToSoundSetting (globalSettings);

        ///////////////////////////
        // Workflow

        this.activateExcludeDeactivatedItemsSetting (globalSettings);
        this.activateNewClipLengthSetting (globalSettings);
        this.activateKnobSpeedSetting (globalSettings);

        ///////////////////////////
        // Hardware

        final IIntegerSetting padBrightnessSetting = globalSettings.getRangeSetting ("Pad Brightness", CATEGORY_HARDWARE_SETUP, 0, 100, 1, "%", 100);
        padBrightnessSetting.addValueObserver (value -> {
            this.padBrightness = value.intValue ();
            this.notifyObservers (PAD_BRIGHTNESS);
        });
        this.isSettingActive.add (PAD_BRIGHTNESS);

        final IIntegerSetting padSaturationSetting = globalSettings.getRangeSetting ("Pad Saturation", CATEGORY_HARDWARE_SETUP, 0, 100, 1, "%", 100);
        padSaturationSetting.addValueObserver (value -> {
            this.padSaturation = value.intValue ();
            this.notifyObservers (PAD_SATURATION);
        });
        this.isSettingActive.add (PAD_SATURATION);
    }


    /**
     * Get the brightness for the pad LEDs.
     *
     * @return The brightness in the range of [0, 100]
     */
    public int getPadBrightness ()
    {
        return this.padBrightness;
    }


    /**
     * Get the saturation for the pad LEDs.
     *
     * @return The saturation in the range of [0, 100]
     */
    public int getPadSaturation ()
    {
        return this.padSaturation;
    }
}
