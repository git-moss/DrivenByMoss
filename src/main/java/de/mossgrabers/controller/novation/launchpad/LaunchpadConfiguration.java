// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchpad;

import java.util.List;

import de.mossgrabers.controller.novation.launchpad.definition.ILaunchpadControllerDefinition;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.IEnumSetting;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.ArpeggiatorMode;
import de.mossgrabers.framework.view.Views;


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
    public static final Integer    PAD_BRIGHTNESS        = Integer.valueOf (NEXT_SETTING_ID);

    private static final String [] PAD_BRIGHTNESS_VALUES = new String [128];
    static
    {
        for (int i = 0; i < 128; i++)
            PAD_BRIGHTNESS_VALUES[i] = Integer.toString (i);
    }

    private static final String [] SCENE_BUTTON_OPTIONS   =
    {
        "Scale Layout",
        "Previous/Next Track"
    };

    private final ILaunchpadControllerDefinition definition;
    private int                                  padBrightness;
    private boolean                              sceneButtonsAsTrackNavigation;


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
        // Play and Sequence

        this.activateAccentActiveSetting (globalSettings);
        this.activateAccentValueSetting (globalSettings);
        this.activateQuantizeAmountSetting (globalSettings);
        this.activateStartupViewSetting (globalSettings, PREFERRED_NOTE_VIEWS);
        this.activateStartWithSessionViewSetting (globalSettings);
        this.activateTurnOffScalePadsSetting (globalSettings);
        this.activateShowPlayedChordsSetting (globalSettings);
        this.activateSceneButtonsSetting (globalSettings);

        // ----------------------------------------------------------------
        // Transport

        this.activateBehaviourOnPauseSetting (globalSettings);
        this.activateRecordButtonSetting (globalSettings);
        this.activateShiftedRecordButtonSetting (globalSettings);

        // ----------------------------------------------------------------
        // Session

        this.activateFlipSessionSetting (globalSettings);
        this.activateSelectClipOnLaunchSetting (globalSettings);
        this.activateActionForRecArmedPad (globalSettings);

        // ----------------------------------------------------------------
        // Drum Sequencer

        this.activateAutoSelectDrumSetting (globalSettings);
        this.activateTurnOffEmptyDrumPadsSetting (globalSettings);

        // ----------------------------------------------------------------
        // Workflow

        this.activateExcludeDeactivatedItemsSetting (globalSettings);
        this.activateIncludeMasterSetting (globalSettings);
        this.activateNewClipLengthSetting (globalSettings);

        // ----------------------------------------------------------------
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


    protected void activateSceneButtonsSetting (final ISettingsUI settingsUI)
    {
        final IEnumSetting sceneButtonsSetting = settingsUI.getEnumSetting ("Scene 1/2 buttons", CATEGORY_PLAY_AND_SEQUENCE, SCENE_BUTTON_OPTIONS, SCENE_BUTTON_OPTIONS[0]);
        sceneButtonsSetting.addValueObserver (value -> this.sceneButtonsAsTrackNavigation = SCENE_BUTTON_OPTIONS[1].equals (value));
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


    /**
     * Are the first two scene buttons used to select the previous/next track instead of changing the
     * scale layout?
     *
     * @return True if used for track navigation
     */
    public boolean isSceneButtonsAsTrackNavigation ()
    {
        return this.sceneButtonsAsTrackNavigation;
    }
}
