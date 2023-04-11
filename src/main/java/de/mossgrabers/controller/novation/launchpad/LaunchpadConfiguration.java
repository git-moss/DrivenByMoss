// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchpad;

import de.mossgrabers.controller.novation.launchpad.definition.ILaunchpadControllerDefinition;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.IEnumSetting;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.ArpeggiatorMode;
import de.mossgrabers.framework.view.Views;

import java.util.List;


/**
 * The configuration settings for Launchpad.
 *
 * @author Jürgen Moßgraber
 */
public class LaunchpadConfiguration extends AbstractConfiguration
{
    private static final Views []  PREFERRED_NOTE_VIEWS  =
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

    /** Setting for the brightness of the pad LEDs. */
    public static final Integer    PAD_BRIGHTNESS        = Integer.valueOf (50);

    private static final String [] PAD_BRIGHTNESS_VALUES = new String [128];
    static
    {
        for (int i = 0; i < 128; i++)
            PAD_BRIGHTNESS_VALUES[i] = Integer.toString (i);
    }

    private final ILaunchpadControllerDefinition definition;
    private int                                  padBrightness;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param arpeggiatorModes The available arpeggiator modes
     * @param definition The Launchpad definitions
     */
    public LaunchpadConfiguration (final IHost host, final IValueChanger valueChanger, final List<ArpeggiatorMode> arpeggiatorModes, final ILaunchpadControllerDefinition definition)
    {
        super (host, valueChanger, arpeggiatorModes);

        this.definition = definition;
        this.preferredAudioView = Views.CLIP_LENGTH;
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
        // Play and Sequence

        this.activateAccentActiveSetting (globalSettings);
        this.activateAccentValueSetting (globalSettings);
        this.activateQuantizeAmountSetting (globalSettings);
        this.activatePreferredNoteViewSetting (globalSettings, PREFERRED_NOTE_VIEWS);
        this.activateStartWithSessionViewSetting (globalSettings);

        ///////////////////////////
        // Transport

        this.activateBehaviourOnPauseSetting (globalSettings);
        this.activateRecordButtonSetting (globalSettings);
        this.activateShiftedRecordButtonSetting (globalSettings);

        ///////////////////////////
        // Session

        this.activateFlipSessionSetting (globalSettings);
        this.activateSelectClipOnLaunchSetting (globalSettings);
        this.activateActionForRecArmedPad (globalSettings);

        ///////////////////////////
        // Drum Sequencer

        this.activateAutoSelectDrumSetting (globalSettings);
        this.activateTurnOffEmptyDrumPadsSetting (globalSettings);

        ///////////////////////////
        // Workflow

        this.activateExcludeDeactivatedItemsSetting (globalSettings);
        this.activateIncludeMasterSetting (globalSettings);
        this.activateNewClipLengthSetting (globalSettings);

        ///////////////////////////
        // Pad Sensitivity

        this.activateConvertAftertouchSetting (globalSettings);
        if (!this.definition.getBrightnessSysex ().isEmpty ())
            this.activatePadBrightnessSetting (globalSettings);
    }


    protected void activatePadBrightnessSetting (final ISettingsUI settingsUI)
    {
        final IEnumSetting padBrightnessSetting = settingsUI.getEnumSetting ("Brightness", CATEGORY_PADS, PAD_BRIGHTNESS_VALUES, PAD_BRIGHTNESS_VALUES[127]);
        padBrightnessSetting.addValueObserver (value -> {
            this.padBrightness = lookupIndex (PAD_BRIGHTNESS_VALUES, value);
            this.notifyObservers (PAD_BRIGHTNESS);
        });

        this.isSettingActive.add (PAD_BRIGHTNESS);
    }


    /**
     * Get the pad brightness.
     *
     * @return The brightness (0-127)
     */
    public int getPadBrightness ()
    {
        return this.padBrightness;
    }
}
