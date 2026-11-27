// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.oxi.one;

import java.util.List;

import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.IEnumSetting;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.constants.Capability;
import de.mossgrabers.framework.daw.midi.ArpeggiatorMode;
import de.mossgrabers.framework.view.Views;


/**
 * The configuration settings for OXI One.
 *
 * @author Jürgen Moßgraber
 */
public class OxiOneConfiguration extends AbstractConfiguration
{
    /** The pad brightness. */
    public static final Integer       PAD_BRIGHTNESS           = Integer.valueOf (NEXT_SETTING_ID);

    private static final List<String> BRIGHTNESS_OPTIONS       = List.of ("10%", "25%", "50%", "65%", "75%", "90%", "100%");
    private static final double []    BRIGHTNESS_VALUES        = new double []
    {
        0.1,
        0.25,
        0.5,
        0.65,
        0.75,
        0.9,
        1
    };

    private static final Views []     PREFERRED_NOTE_VIEWS_MK1 =
    {
        Views.MIX,
        Views.DRUM_XOX,
        Views.PLAY,
        Views.DRUM64,
        Views.DRUM8,
        Views.SEQUENCER,
        Views.POLY_SEQUENCER,
        Views.RAINDROPS
    };

    private static final Views []     PREFERRED_NOTE_VIEWS_MK2 =
    {
        Views.MIX,
        Views.SESSION,
        Views.DRUM_XOX,
        Views.PLAY,
        Views.DRUM64,
        Views.DRUM8,
        Views.SEQUENCER,
        Views.POLY_SEQUENCER,
        Views.RAINDROPS
    };

    private double                    padBrightness            = 0.5;
    private final boolean             isMk2;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param arpeggiatorModes The available arpeggiator modes
     * @param isMk2 True if it is the Mk2 otherwise the Mk1
     */
    public OxiOneConfiguration (final IHost host, final IValueChanger valueChanger, final List<ArpeggiatorMode> arpeggiatorModes, final boolean isMk2)
    {
        super (host, valueChanger, arpeggiatorModes);

        this.isMk2 = isMk2;

        Views.setViewName (Views.DRUM64, "Drum 128");

        // Force note on velocity to 127 since the pads only send 1
        this.accentActive = true;
    }


    /** {@inheritDoc} */
    @Override
    public void init (final ISettingsUI globalSettings, final ISettingsUI documentSettings)
    {
        // ----------------------------------------------------------------
        // Scale

        this.activateScaleSetting (documentSettings);
        this.activateScaleBaseSetting (documentSettings);
        this.activateScaleInScaleSetting (documentSettings);
        this.activateScaleLayoutSetting (documentSettings);

        // ----------------------------------------------------------------
        // Note Repeat

        this.activateNoteRepeatSetting (documentSettings);

        // ----------------------------------------------------------------
        // Hardware

        final IEnumSetting padBrightnessSetting = globalSettings.getEnumSetting ("Grid Pad Brightness", CATEGORY_HARDWARE_SETUP, BRIGHTNESS_OPTIONS, BRIGHTNESS_OPTIONS.get (2));
        padBrightnessSetting.addValueObserver (value -> {
            this.padBrightness = BRIGHTNESS_VALUES[lookupIndex (BRIGHTNESS_OPTIONS, value)];
            this.notifyObservers (PAD_BRIGHTNESS);
        });

        // ----------------------------------------------------------------
        // Session

        this.activateSelectClipOnLaunchSetting (globalSettings);
        this.activateDrawRecordStripeSetting (globalSettings);
        this.activateActionForRecArmedPad (globalSettings);

        // ----------------------------------------------------------------
        // Transport

        this.activateBehaviourOnStopSetting (globalSettings);
        this.activateBehaviourOnPauseSetting (globalSettings);
        this.activateRecordButtonSetting (globalSettings);
        this.activateShiftedRecordButtonSetting (globalSettings);
        this.activateDoubleClickPlayButton (globalSettings);
        this.activateDoubleClickStopButton (globalSettings);

        // ----------------------------------------------------------------
        // Play and Sequence

        this.activateQuantizeAmountSetting (globalSettings);
        this.activateMidiEditChannelSetting (documentSettings);
        this.activateTurnOffScalePadsSetting (globalSettings);
        this.activateShowPlayedChordsSetting (globalSettings);
        this.activateStartupViewSetting (globalSettings, this.isMk2 ? PREFERRED_NOTE_VIEWS_MK2 : PREFERRED_NOTE_VIEWS_MK1);

        // ----------------------------------------------------------------
        // Drum Sequencer

        if (this.host.supports (Capability.HAS_DRUM_DEVICE))
        {
            this.activateTurnOffEmptyDrumPadsSetting (globalSettings);
            this.activateShowSelectedDrumPadSetting (globalSettings);
        }

        // ----------------------------------------------------------------
        // Workflow

        this.activateExcludeDeactivatedItemsSetting (globalSettings);
        this.activateNewClipLengthSetting (globalSettings);
        this.activateKnobSpeedSetting (globalSettings);
        this.activateColorTrackStates (globalSettings);
    }


    /**
     * Get the pad brightness.
     *
     * @return The pad brightness in the range of [0..1]
     */
    public double getPadBrightness ()
    {
        return this.padBrightness;
    }
}
