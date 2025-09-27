// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.IColorSetting;
import de.mossgrabers.framework.configuration.IEnumSetting;
import de.mossgrabers.framework.configuration.IIntegerSetting;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.constants.Capability;
import de.mossgrabers.framework.daw.midi.ArpeggiatorMode;
import de.mossgrabers.framework.graphics.IGraphicsConfiguration;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.view.Views;


/**
 * The configuration settings for Push.
 *
 * @author Jürgen Moßgraber
 */
public class PushConfiguration extends AbstractConfiguration implements IGraphicsConfiguration
{
    /** A lock state for the mode buttons. */
    public enum LockState
    {
        /** No lock state. */
        OFF,
        /** Locked to mute. */
        MUTE,
        /** Locked to solo. */
        SOLO,
        /** Locked to clip stop. */
        CLIP_STOP,
    }


    /** Setting for the ribbon mode. */
    public static final Integer     RIBBON_MODE                                = Integer.valueOf (NEXT_SETTING_ID);
    /** Setting for the ribbon mode MIDI CC. */
    public static final Integer     RIBBON_MODE_CC_VAL                         = Integer.valueOf (NEXT_SETTING_ID + 1);
    /** Setting for the ribbon mode note repeat. */
    public static final Integer     RIBBON_MODE_NOTE_REPEAT                    = Integer.valueOf (NEXT_SETTING_ID + 2);

    /** Setting for the velocity curve. */
    public static final Integer     VELOCITY_CURVE                             = Integer.valueOf (NEXT_SETTING_ID + 3);
    /** Setting for the Push 1 pad threshold. */
    public static final Integer     PAD_PUSH1_THRESHOLD                        = Integer.valueOf (NEXT_SETTING_ID + 4);
    /** Setting for the display brightness. */
    public static final Integer     DISPLAY_BRIGHTNESS                         = Integer.valueOf (NEXT_SETTING_ID + 5);
    /** Setting for the pad LED brightness. */
    public static final Integer     LED_BRIGHTNESS                             = Integer.valueOf (NEXT_SETTING_ID + 6);
    /** Setting for the Push 2 pad sensitivity. */
    public static final Integer     PAD_PUSH2_SENSITIVITY                      = Integer.valueOf (NEXT_SETTING_ID + 7);
    /** Setting for the Push 2 pad gain. */
    public static final Integer     PAD_PUSH2_GAIN                             = Integer.valueOf (NEXT_SETTING_ID + 8);
    /** Setting for the Push 2 pad dynamics. */
    public static final Integer     PAD_PUSH2_DYNAMICS                         = Integer.valueOf (NEXT_SETTING_ID + 9);
    /** Setting for the Push 3 pad velocity curve - threshold parameter. */
    public static final Integer     PAD_PUSH3_THRESHOLD                        = Integer.valueOf (NEXT_SETTING_ID + 10);
    /** Setting for the Push 3 pad velocity curve - drive parameter. */
    public static final Integer     PAD_PUSH3_DRIVE                            = Integer.valueOf (NEXT_SETTING_ID + 11);
    /** Setting for the Push 3 pad velocity curve - compand parameter. */
    public static final Integer     PAD_PUSH3_COMPAND                          = Integer.valueOf (NEXT_SETTING_ID + 12);
    /** Setting for the Push 3 pad velocity curve - range parameter. */
    public static final Integer     PAD_PUSH3_RANGE                            = Integer.valueOf (NEXT_SETTING_ID + 14);

    /** Setting for stopping automation recording on knob release. */
    public static final Integer     STOP_AUTOMATION_ON_KNOB_RELEASE            = Integer.valueOf (NEXT_SETTING_ID + 15);
    /** Mode debug. */
    public static final Integer     DEBUG_MODE                                 = Integer.valueOf (NEXT_SETTING_ID + 16);
    /** Push 2 display debug window. */
    public static final Integer     DEBUG_WINDOW                               = Integer.valueOf (NEXT_SETTING_ID + 17);

    /** Background color of an element. */
    public static final Integer     COLOR_BACKGROUND                           = Integer.valueOf (NEXT_SETTING_ID + 20);
    /** Border color of an element. */
    public static final Integer     COLOR_BORDER                               = Integer.valueOf (NEXT_SETTING_ID + 21);
    /** Text color of an element. */
    public static final Integer     COLOR_TEXT                                 = Integer.valueOf (NEXT_SETTING_ID + 22);
    /** Fader color of an element. */
    public static final Integer     COLOR_FADER                                = Integer.valueOf (NEXT_SETTING_ID + 23);
    /** VU color of an element. */
    public static final Integer     COLOR_VU                                   = Integer.valueOf (NEXT_SETTING_ID + 24);
    /** Edit color of an element. */
    public static final Integer     COLOR_EDIT                                 = Integer.valueOf (NEXT_SETTING_ID + 25);
    /** Record color of an element. */
    public static final Integer     COLOR_RECORD                               = Integer.valueOf (NEXT_SETTING_ID + 26);
    /** Solo color of an element. */
    public static final Integer     COLOR_SOLO                                 = Integer.valueOf (NEXT_SETTING_ID + 27);
    /** Mute color of an element. */
    public static final Integer     COLOR_MUTE                                 = Integer.valueOf (NEXT_SETTING_ID + 28);
    /** Background color darker of an element. */
    public static final Integer     COLOR_BACKGROUND_DARKER                    = Integer.valueOf (NEXT_SETTING_ID + 29);
    /** Background color lighter of an element. */
    public static final Integer     COLOR_BACKGROUND_LIGHTER                   = Integer.valueOf (NEXT_SETTING_ID + 30);

    /** Session view options. */
    public static final Integer     SESSION_VIEW                               = Integer.valueOf (NEXT_SETTING_ID + 31);
    /** Display scenes or clips. */
    public static final Integer     DISPLAY_SCENES_CLIPS                       = Integer.valueOf (NEXT_SETTING_ID + 32);

    /** MPE - Per-pad pitchbend. */
    public static final Integer     PER_PAD_PITCHBEND                          = Integer.valueOf (NEXT_SETTING_ID + 33);
    /** MPE - Pad in-tune location. */
    public static final Integer     IN_TUNE_LOCATION                           = Integer.valueOf (NEXT_SETTING_ID + 34);
    /** MPE - Pad in-tune location width. */
    public static final Integer     IN_TUNE_WIDTH                              = Integer.valueOf (NEXT_SETTING_ID + 35);
    /** MPE - Pad in-tune location height. */
    public static final Integer     IN_TUNE_SLIDE_HEIGHT                       = Integer.valueOf (NEXT_SETTING_ID + 36);

    /** Audio Interface - Footswitch or CV 1. */
    public static final Integer     PEDAL_1                                    = Integer.valueOf (NEXT_SETTING_ID + 37);
    /** Audio Interface - Footswitch or CV 2. */
    public static final Integer     PEDAL_2                                    = Integer.valueOf (NEXT_SETTING_ID + 38);
    /** Audio Interface - Pre-amp type 1. */
    public static final Integer     PREAMP_TYPE_1                              = Integer.valueOf (NEXT_SETTING_ID + 39);
    /** Audio Interface - Pre-amp type 2. */
    public static final Integer     PREAMP_TYPE_2                              = Integer.valueOf (NEXT_SETTING_ID + 40);
    /** Audio Interface - Pre-amp gain 1. */
    public static final Integer     PREAMP_GAIN_1                              = Integer.valueOf (NEXT_SETTING_ID + 41);
    /** Audio Interface - Pre-amp gain 2. */
    public static final Integer     PREAMP_GAIN_2                              = Integer.valueOf (NEXT_SETTING_ID + 42);
    /** Audio Interface - Output configuration. */
    public static final Integer     AUDIO_OUTPUTS                              = Integer.valueOf (NEXT_SETTING_ID + 43);

    /** Use ribbon for pitch bend. */
    public static final int         RIBBON_MODE_PITCH                          = 0;
    /** Use ribbon for MIDI CC. */
    public static final int         RIBBON_MODE_CC                             = 1;
    /** Use ribbon for MIDI CC and pitch bend. */
    public static final int         RIBBON_MODE_CC_PB                          = 2;
    /** Use ribbon for pitch bend and MIDI CC. */
    public static final int         RIBBON_MODE_PB_CC                          = 3;
    /** Use ribbon as volume fader. */
    public static final int         RIBBON_MODE_FADER                          = 4;
    /** Use ribbon to change the last touched parameter. */
    public static final int         RIBBON_MODE_LAST_TOUCHED                   = 5;

    /** Use ribbon not for note repeat settings. */
    public static final int         NOTE_REPEAT_OFF                            = 0;
    /** Use ribbon for changing the note repeat period. */
    public static final int         NOTE_REPEAT_PERIOD                         = 1;
    /** Use ribbon for changing the note repeat length. */
    public static final int         NOTE_REPEAT_LENGTH                         = 2;

    private static final String     CATEGORY_RIBBON                            = "Ribbon";
    private static final String     CATEGORY_AUDIO                             = "Audio Interface";
    private static final String     CATEGORY_COLORS                            = "Display Colors";

    private static final String []  RIBBON_MODE_VALUES                         =
    {
        "Pitch",
        "CC",
        "CC/Pitch",
        "Pitch/CC",
        "Fader",
        "Last Touched"
    };

    private static final String []  RIBBON_NOTE_REPEAT_VALUES                  =
    {
        "Off",
        "Period",
        "Length"
    };

    private static final String []  SESSION_VIEW_OPTIONS                       =
    {
        "Clips",
        "Flipped",
        "Scenes"
    };

    /** ID for moving the track bank by a page. */
    public static final int         CURSOR_KEYS_TRACK_OPTION_MOVE_BANK_BY_PAGE = 0;
    /** ID for moving the track bank by 1. */
    public static final int         CURSOR_KEYS_TRACK_OPTION_MOVE_BANK_BY_1    = 1;
    /** ID for swapping the track with the previous/next track. */
    public static final int         CURSOR_KEYS_TRACK_OPTION_SWAP              = 2;

    private static final String []  CURSOR_KEYS_TRACK_OPTIONS                  =
    {
        "Scroll track bank by page",
        "Scroll track bank by 1",
        "Swap tracks"
    };

    /** ID for moving the scene bank by a page. */
    public static final int         CURSOR_KEYS_SCENE_OPTION_MOVE_BANK_BY_PAGE = 0;
    /** ID for moving the scene bank by 1. */
    public static final int         CURSOR_KEYS_SCENE_OPTION_MOVE_BANK_BY_1    = 1;

    private static final String []  CURSOR_KEYS_SCENE_OPTIONS                  =
    {
        "Scroll scene bank by page",
        "Scroll scene bank by 1",
    };

    private static final Views []   PREFERRED_NOTE_VIEWS                       =
    {
        Views.PLAY,
        Views.CHORDS,
        Views.PIANO,
        Views.DRUM64,
        Views.DRUM,
        Views.DRUM4,
        Views.DRUM8,
        Views.DRUM_XOX,
        Views.SEQUENCER,
        Views.RAINDROPS,
        Views.POLY_SEQUENCER
    };

    /** Debug modes. */
    private static final Set<Modes> DEBUG_MODES                                = EnumSet.noneOf (Modes.class);

    static
    {
        DEBUG_MODES.add (Modes.TRACK);
        DEBUG_MODES.add (Modes.TRACK_DETAILS);
        DEBUG_MODES.add (Modes.VOLUME);
        DEBUG_MODES.add (Modes.CROSSFADER);
        DEBUG_MODES.add (Modes.PAN);
        DEBUG_MODES.add (Modes.SEND1);
        DEBUG_MODES.add (Modes.SEND2);
        DEBUG_MODES.add (Modes.SEND3);
        DEBUG_MODES.add (Modes.SEND4);
        DEBUG_MODES.add (Modes.SEND5);
        DEBUG_MODES.add (Modes.SEND6);
        DEBUG_MODES.add (Modes.SEND7);
        DEBUG_MODES.add (Modes.SEND8);
        DEBUG_MODES.add (Modes.MASTER);
        DEBUG_MODES.add (Modes.MASTER_TEMP);
        DEBUG_MODES.add (Modes.DEVICE_PARAMS);
        DEBUG_MODES.add (Modes.DEVICE_CHAINS);
        DEBUG_MODES.add (Modes.DEVICE_LAYER);
        DEBUG_MODES.add (Modes.DEVICE_LAYER_VOLUME);
        DEBUG_MODES.add (Modes.DEVICE_LAYER_PAN);
        DEBUG_MODES.add (Modes.DEVICE_LAYER_SEND1);
        DEBUG_MODES.add (Modes.DEVICE_LAYER_SEND2);
        DEBUG_MODES.add (Modes.DEVICE_LAYER_SEND3);
        DEBUG_MODES.add (Modes.DEVICE_LAYER_SEND4);
        DEBUG_MODES.add (Modes.DEVICE_LAYER_SEND5);
        DEBUG_MODES.add (Modes.DEVICE_LAYER_SEND6);
        DEBUG_MODES.add (Modes.DEVICE_LAYER_SEND7);
        DEBUG_MODES.add (Modes.DEVICE_LAYER_SEND8);
        DEBUG_MODES.add (Modes.DEVICE_LAYER_DETAILS);
        DEBUG_MODES.add (Modes.BROWSER);
        DEBUG_MODES.add (Modes.CLIP);
        DEBUG_MODES.add (Modes.NOTE);
        DEBUG_MODES.add (Modes.FRAME);
        DEBUG_MODES.add (Modes.GROOVE);
        DEBUG_MODES.add (Modes.REC_ARM);
        DEBUG_MODES.add (Modes.ACCENT);
        DEBUG_MODES.add (Modes.SCALES);
        DEBUG_MODES.add (Modes.SCALE_LAYOUT);
        DEBUG_MODES.add (Modes.FIXED);
        DEBUG_MODES.add (Modes.RIBBON);
        DEBUG_MODES.add (Modes.VIEW_SELECT);
        DEBUG_MODES.add (Modes.AUTOMATION);
        DEBUG_MODES.add (Modes.TRANSPORT);
        DEBUG_MODES.add (Modes.MARKERS);
        DEBUG_MODES.add (Modes.USER);
        DEBUG_MODES.add (Modes.SETUP);
        DEBUG_MODES.add (Modes.INFO);
        DEBUG_MODES.add (Modes.CONFIGURATION);
        DEBUG_MODES.add (Modes.SESSION);
        DEBUG_MODES.add (Modes.SESSION_VIEW_SELECT);
        DEBUG_MODES.add (Modes.REPEAT_NOTE);
    }

    /** Options for the MPE in-tune location options. */
    public static final String [] IN_TUNE_LOCATION_OPTIONS     =
    {
        "Pad",
        "Finger"
    };

    /** Options for the MPE in-tune width options. */
    public static final String [] IN_TUNE_WIDTH_OPTIONS        =
    {
        "0",
        "1",
        "2",
        "2.5",
        "3",
        "4",
        "5",
        "6",
        "7",
        "10",
        "13",
        "20"
    };

    /** Options for the MPE slide height options. */
    public static final String [] SLIDE_HEIGHT_OPTIONS         =
    {
        "10",
        "11",
        "12",
        "13",
        "14",
        "15",
        "16"
    };

    /** Options for footswitch or CV out setting. */
    public static final String [] FOOT_CV_OPTIONS              =
    {
        "Footswitch",
        "CV Out"
    };

    /** Options for pre-amp type setting. */
    public static final String [] PREAMP_TYPE_OPTIONS          =
    {
        "Line",
        "Instrument",
        "High"
    };

    /** Options for pre-amp gain setting. */
    public static final String [] PREAMP_GAIN_OPTIONS          =
    {
        "0dB",
        "1dB",
        "2dB",
        "3dB",
        "4dB",
        "5dB",
        "6dB",
        "7dB",
        "8dB",
        "9dB",
        "10dB",
        "11dB",
        "12dB",
        "13dB",
        "14dB",
        "15dB",
        "16dB",
        "17dB",
        "18dB",
        "19dB",
        "20dB"
    };

    /** Options for output configuration setting. */
    public static final String [] OUTPUT_CONFIGURATION         =
    {
        "Headphones 1/2 - Speaker 1/2",
        "Headphones 3/4 - Speaker 1/2",
        "Headphones 1/2 - Speaker 3/4"
    };
    /** Options for output configuration setting - short. */
    public static final String [] OUTPUT_CONFIGURATION_SHORT   =
    {
        "H1/2-S1/2",
        "H3/4-S1/2",
        "H1/2-S3/4"
    };

    private LockState             lockState                    = LockState.OFF;
    private int                   cursorKeysTrackOption        = 0;
    private int                   cursorKeysTrackShiftedOption = 2;
    private int                   cursorKeysSceneOption        = 1;
    private int                   cursorKeysSceneShiftedOption = 0;
    private boolean               isScenesClipView;

    /** What does the ribbon send? **/
    private int                   ribbonMode                   = RIBBON_MODE_PITCH;
    private int                   ribbonModeCCVal              = 1;
    private int                   ribbonModeNoteRepeat         = NOTE_REPEAT_PERIOD;

    private boolean               stopAutomationOnKnobRelease  = false;
    private Modes                 debugMode                    = Modes.TRACK;
    private Modes                 layerMode                    = null;

    // Only Push 1
    private int                   velocityCurvePush1           = 1;
    private int                   padThresholdPush1            = 20;

    // Only Push 2
    private int                   displayBrightness            = 255;
    private int                   ledBrightness                = 127;
    private int                   padSensitivityPush2          = 5;
    private int                   padGainPush2                 = 5;
    private int                   padDynamicsPush2             = 5;
    private ColorEx               colorBackground              = DEFAULT_COLOR_BACKGROUND;
    private ColorEx               colorBorder                  = DEFAULT_COLOR_BORDER;
    private ColorEx               colorText                    = DEFAULT_COLOR_TEXT;
    private ColorEx               colorFader                   = DEFAULT_COLOR_FADER;
    private ColorEx               colorVU                      = DEFAULT_COLOR_VU;
    private ColorEx               colorEdit                    = DEFAULT_COLOR_EDIT;
    private ColorEx               colorRecord                  = DEFAULT_COLOR_RECORD;
    private ColorEx               colorSolo                    = DEFAULT_COLOR_SOLO;
    private ColorEx               colorMute                    = DEFAULT_COLOR_MUTE;
    private ColorEx               colorBackgroundDarker        = DEFAULT_COLOR_BACKGROUND_DARKER;
    private ColorEx               colorBackgroundLighter       = DEFAULT_COLOR_BACKGROUND_LIGHTER;

    // Only Push 3
    private boolean               perPadPitchbend              = true;
    private int                   inTuneLocation;
    private int                   inTuneWidth;
    private int                   slideHeight;
    private int                   pedal1;
    private int                   pedal2;
    private int                   preamp1Type;
    private int                   preamp2Type;
    private int                   preamp1Gain;
    private int                   preamp2Gain;
    private int                   audioOutputs;

    private int                   padCurveThresholdPush3       = 7;
    private int                   padCurveDrivePush3           = 4;
    private int                   padCurveCompandPush3         = -26;
    private int                   padCurveRangePush3           = 35;

    private final PushVersion     pushVersion;

    private IIntegerSetting       displayBrightnessSetting;
    private IIntegerSetting       ledBrightnessSetting;
    private IEnumSetting          ribbonModeSetting;
    private IIntegerSetting       ribbonModeCCSetting;
    private IEnumSetting          ribbonModeNoteRepeatSetting;

    // Push 1
    private IEnumSetting          velocityCurveSetting;
    private IEnumSetting          padThresholdSetting;
    // Push 2
    private IIntegerSetting       padSensitivitySetting;
    private IIntegerSetting       padGainSetting;
    private IIntegerSetting       padDynamicsSetting;
    // Push 3
    private IIntegerSetting       padCurveThresholdSetting;
    private IIntegerSetting       padCurveDriveSetting;
    private IIntegerSetting       padCurveCompandSetting;
    private IIntegerSetting       padCurveRangeSetting;

    private IEnumSetting          debugModeSetting;
    private IColorSetting         colorBackgroundSetting;
    private IColorSetting         colorBackgroundDarkerSetting;
    private IColorSetting         colorBackgroundLighterSetting;
    private IColorSetting         colorBorderSetting;
    private IColorSetting         colorTextSetting;
    private IColorSetting         colorFaderSetting;
    private IColorSetting         colorVUSetting;
    private IColorSetting         colorEditSetting;
    private IColorSetting         colorRecordSetting;
    private IColorSetting         colorSoloSetting;
    private IColorSetting         colorMuteSetting;
    private IEnumSetting          sessionViewSetting;
    private IEnumSetting          perPadPitchbendSetting;
    private IEnumSetting          inTuneLocationSetting;
    private IEnumSetting          inTuneWidthSetting;
    private IEnumSetting          slideHeightSetting;
    private IEnumSetting          pedal1Setting;
    private IEnumSetting          pedal2Setting;
    private IEnumSetting          preamp1Setting;
    private IEnumSetting          preamp2Setting;
    private IEnumSetting          preamp1GainSetting;
    private IEnumSetting          preamp2GainSetting;
    private IEnumSetting          audioOutputSetting;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param arpeggiatorModes The available arpeggiator modes
     * @param pushVersion The version of Push
     */
    public PushConfiguration (final IHost host, final IValueChanger valueChanger, final List<ArpeggiatorMode> arpeggiatorModes, final PushVersion pushVersion)
    {
        super (host, valueChanger, arpeggiatorModes);

        this.pushVersion = pushVersion;
        this.preferredAudioView = Views.CLIP_LENGTH;

        this.dontNotifyAll.add (DEBUG_WINDOW);
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

        this.activateSessionView (globalSettings);
        this.activateSelectClipOnLaunchSetting (globalSettings);
        this.activateDrawRecordStripeSetting (globalSettings);
        this.activateActionForRecArmedPad (globalSettings);

        ///////////////////////////
        // Transport

        this.activateBehaviourOnPauseSetting (globalSettings);
        this.activateFlipRecordSetting (globalSettings);

        ///////////////////////////
        // Play and Sequence

        this.activateAccentActiveSetting (globalSettings);
        this.activateAccentValueSetting (globalSettings);
        this.activateQuantizeAmountSetting (globalSettings);
        this.activateStartupViewSetting (globalSettings, PREFERRED_NOTE_VIEWS);
        this.activateStartWithSessionViewSetting (globalSettings);
        this.activateMidiEditChannelSetting (documentSettings);
        this.activateTurnOffScalePadsSetting (globalSettings);
        this.activateShowPlayedChordsSetting (globalSettings);

        ///////////////////////////
        // Drum Sequencer

        if (this.host.supports (Capability.HAS_DRUM_DEVICE))
        {
            this.activateAutoSelectDrumSetting (globalSettings);
            this.activateTurnOffEmptyDrumPadsSetting (globalSettings);
        }

        ///////////////////////////
        // Workflow

        this.activateTrackNavigationSetting (globalSettings, CATEGORY_WORKFLOW, false);
        this.activateCursorKeysSettings (globalSettings);
        this.activateIncludeMasterSetting (globalSettings);
        this.activateExcludeDeactivatedItemsSetting (globalSettings);
        this.activateEnableVUMetersSetting (globalSettings);
        this.activateFootswitchSetting (globalSettings, 0, "Footswitch 2");
        this.activateStopAutomationOnKnobReleaseSetting (globalSettings);
        this.activateNewClipLengthSetting (globalSettings);
        this.activateKnobSpeedSetting (globalSettings);

        ///////////////////////////
        // Add Track - Device Favorites

        this.activateDeviceFavorites (globalSettings, 7, 7, 7, 7);

        ///////////////////////////
        // Ribbon

        this.activateRibbonSettings (globalSettings);

        ///////////////////////////
        // Hardware configuration

        switch (this.pushVersion)
        {
            case PushVersion.VERSION_1:
                this.activatePush1PadSettings (globalSettings);
                this.activateConvertAftertouchSetting (globalSettings);
                break;

            case PushVersion.VERSION_2:
                this.activatePush2PadSettings (globalSettings);
                this.activateConvertAftertouchSetting (globalSettings);
                break;

            case PushVersion.VERSION_3:
                this.activatePush3PadSettings (globalSettings);
                this.activatePush3MPESettings (globalSettings);
                this.convertAftertouch = AbstractConfiguration.AFTERTOUCH_CONVERT_OFF;
                this.activatePush3AudioSettings (globalSettings);
                break;
        }

        ///////////////////////////
        // Push 2 Hardware

        this.activatePush2HardwareSettings (globalSettings);
        this.activatePush2DisplayColorsSettings (globalSettings);

        ///////////////////////////
        // Debugging

        this.activateDebugSettings (globalSettings);
    }


    /**
     * Get the track function to execute for left/right cursor keys.
     *
     * @return The index of the option
     */
    public int getCursorKeysTrackOption ()
    {
        return this.cursorKeysTrackOption;
    }


    /**
     * Get the track function to execute for shifted left/right cursor keys.
     *
     * @return The index of the option
     */
    public int getCursorKeysTrackShiftedOption ()
    {
        return this.cursorKeysTrackShiftedOption;
    }


    /**
     * Get the scene function to execute for up/down cursor keys.
     *
     * @return The index of the option
     */
    public int getCursorKeysSceneOption ()
    {
        return this.cursorKeysSceneOption;
    }


    /**
     * Get the scene function to execute for shifted up/down cursor keys.
     *
     * @return The index of the option
     */
    public int getCursorKeysSceneShiftedOption ()
    {
        return this.cursorKeysSceneShiftedOption;
    }


    /**
     * Set the ribbon mode.
     *
     * @param mode The functionality for the ribbon
     */
    public void setRibbonMode (final int mode)
    {
        this.ribbonModeSetting.set (RIBBON_MODE_VALUES[mode]);
    }


    /**
     * Get the ribbon mode.
     *
     * @return The functionality for the ribbon
     */
    public int getRibbonMode ()
    {
        return this.ribbonMode;
    }


    /**
     * Set the MIDI CC to use for the CC functionality of the ribbon.
     *
     * @param value The MIDI CC value
     */
    public void setRibbonModeCC (final int value)
    {
        this.ribbonModeCCSetting.set (value);
    }


    /**
     * Get the MIDI CC to use for the CC functionality of the ribbon.
     *
     * @return The MIDI CC value
     */
    public int getRibbonModeCCVal ()
    {
        return this.ribbonModeCCVal;
    }


    /**
     * Set the ribbon mode note repeat.
     *
     * @param mode The functionality for the ribbon in note repeat mode
     */
    public void setRibbonNoteRepeat (final int mode)
    {
        this.ribbonModeNoteRepeatSetting.set (RIBBON_NOTE_REPEAT_VALUES[mode]);
    }


    /**
     * Get the ribbon mode note repeat.
     *
     * @return The functionality for the ribbon in note repeat mode
     */
    public int getRibbonNoteRepeat ()
    {
        return this.ribbonModeNoteRepeat;
    }


    /**
     * Change the pad threshold.
     *
     * @param control The control value
     */
    public void changePadThreshold (final int control)
    {
        final int size = PushControlSurface.PUSH_PAD_THRESHOLDS_NAME.size ();
        final int value = this.valueChanger.changeValue (control, this.padThresholdPush1, -100, size);
        this.padThresholdPush1 = Math.max (0, Math.min (value, size - 1));
        this.padThresholdSetting.set (PushControlSurface.PUSH_PAD_THRESHOLDS_NAME.get (this.padThresholdPush1));
    }


    /**
     * Change the velocity curve.
     *
     * @param control The control value
     */
    public void changeVelocityCurve (final int control)
    {
        final int size = PushControlSurface.PUSH_PAD_CURVES_NAME.size ();
        final int value = this.valueChanger.changeValue (control, this.velocityCurvePush1, -100, size);
        this.velocityCurvePush1 = Math.max (0, Math.min (value, size - 1));
        this.velocityCurveSetting.set (PushControlSurface.PUSH_PAD_CURVES_NAME.get (this.velocityCurvePush1));
    }


    /**
     * Change the display brightness.
     *
     * @param control The control value
     */
    public void changeDisplayBrightness (final int control)
    {
        this.displayBrightnessSetting.set (this.valueChanger.changeValue (control, this.displayBrightness, -100, 101));
    }


    /**
     * Change the LED brightness.
     *
     * @param control The control value
     */
    public void changeLEDBrightness (final int control)
    {
        this.ledBrightnessSetting.set (this.valueChanger.changeValue (control, this.ledBrightness, -100, 101));
    }


    /**
     * Change the pad sensitivity.
     *
     * @param control The control value
     */
    public void changePadSensitivity (final int control)
    {
        this.padSensitivitySetting.set (this.valueChanger.changeValue (control, this.padSensitivityPush2, -100, 11));
    }


    /**
     * Change the pad gain.
     *
     * @param control The control value
     */
    public void changePadGain (final int control)
    {
        this.padGainSetting.set (this.valueChanger.changeValue (control, this.padGainPush2, -100, 11));
    }


    /**
     * Change the pad dynamics.
     *
     * @param control The control value
     */
    public void changePadDynamics (final int control)
    {
        this.padDynamicsSetting.set (this.valueChanger.changeValue (control, this.padDynamicsPush2, -100, 11));
    }


    /**
     * Change the pad curve threshold.
     *
     * @param control The control value
     */
    public void changePadCurveThresholdPush3 (final int control)
    {
        this.padCurveThresholdSetting.set (this.valueChanger.changeValue (control, this.padCurveThresholdPush3, -100, 101));
    }


    /**
     * Change the pad curve drive.
     *
     * @param control The control value
     */
    public void changePadCurveDrivePush3 (final int control)
    {
        this.padCurveDriveSetting.set (this.valueChanger.changeValue (control, this.padCurveDrivePush3 + 50, -100, 101) - 50);
    }


    /**
     * Change the pad curve compand.
     *
     * @param control The control value
     */
    public void changePadCurveCompandPush3 (final int control)
    {
        this.padCurveCompandSetting.set (this.valueChanger.changeValue (control, this.padCurveCompandPush3 + 50, -100, 101) - 50);
    }


    /**
     * Change the pad curve range.
     *
     * @param control The control value
     */
    public void changePadCurveRangePush3 (final int control)
    {
        this.padCurveRangeSetting.set (this.valueChanger.changeValue (control, this.padCurveRangePush3, -100, 101));
    }


    /**
     * Get the push version.
     *
     * @return The version
     */
    public PushVersion getPushVersion ()
    {
        return this.pushVersion;
    }


    /**
     * Is this a modern Push version (2 or 3)?
     *
     * @return True if Push 2 or 3
     */
    public boolean isPushModern ()
    {
        return this.pushVersion != PushVersion.VERSION_1;
    }


    /**
     * Get the velocity curve.
     *
     * @return The index of the velocity curve
     */
    public int getVelocityCurve ()
    {
        return this.velocityCurvePush1;
    }


    /**
     * Set the velocity curve.
     *
     * @param velocityCurve The index of the velocity curve
     */
    public void setVelocityCurve (final int velocityCurve)
    {
        this.velocityCurvePush1 = velocityCurve;
    }


    /**
     * Get the pad threshold.
     *
     * @return The pad threshold
     */
    public int getPadThresholdPush1 ()
    {
        return this.padThresholdPush1;
    }


    /**
     * Set the pad threshold.
     *
     * @param padThreshold The pad threshold
     */
    public void setPadThresholdPush1 (final int padThreshold)
    {
        this.padThresholdPush1 = padThreshold;
    }


    /**
     * Get the display brightness.
     *
     * @return The display brightness.
     */
    public int getDisplayBrightness ()
    {
        return this.displayBrightness;
    }


    /**
     * Set the display brightness.
     *
     * @param displayBrightness The display brightness.
     */
    public void setDisplayBrightness (final int displayBrightness)
    {
        this.displayBrightnessSetting.set (displayBrightness);
    }


    /**
     * Get the LED brightness.
     *
     * @return The LED brightness
     */
    public int getLedBrightness ()
    {
        return this.ledBrightness;
    }


    /**
     * Set the LED brightness.
     *
     * @param ledBrightness The LED brightness
     */
    public void setLEDBrightness (final int ledBrightness)
    {
        this.ledBrightnessSetting.set (ledBrightness);
    }


    /**
     * Stop automation recording on knob release?
     *
     * @return True if should be stopped
     */
    public boolean isStopAutomationOnKnobRelease ()
    {
        return this.stopAutomationOnKnobRelease;
    }


    /**
     * Returns true if it is either a Push 1, solo button is long pressed or solo mode is locked.
     *
     * @param isSoloLongPressed True if solo is long pressed
     * @return As explained above
     */
    public boolean isSoloState (final boolean isSoloLongPressed)
    {
        return isSoloLongPressed || this.lockState == LockState.SOLO;
    }


    /**
     * Returns true if it is either a Push 1, mute button is long pressed or mute mode is locked.
     *
     * @param isMuteLongPressed True if mute is long pressed
     * @return As explained above
     */
    public boolean isMuteState (final boolean isMuteLongPressed)
    {
        return isMuteLongPressed || this.lockState == LockState.MUTE;
    }


    /**
     * Returns true if it is either a Push 1, clip stop button is long pressed or clip stop mode is
     * locked.
     *
     * @param isClipStopLongPressed True if clip stop is long pressed
     * @return As explained above
     */
    public boolean isClipStopState (final boolean isClipStopLongPressed)
    {
        return isClipStopLongPressed || this.lockState == LockState.CLIP_STOP;
    }


    /**
     * Is mute, solo or clip state locked (all mode buttons are used for solo or mute)?
     *
     * @return The state
     */
    public LockState getLockState ()
    {
        return this.lockState;
    }


    /**
     * Set if mute, solo or clip stop is locked (all mode buttons are used for solo or mute).
     *
     * @param lockState The new lock state
     */
    public void setLockState (final LockState lockState)
    {
        this.lockState = lockState;
    }


    /**
     * Get the pad sensitivity for Push 2.
     *
     * @return The pad sensitivity
     */
    public int getPadSensitivityPush2 ()
    {
        return this.padSensitivityPush2;
    }


    /**
     * Set the pad sensitivity for Push 2.
     *
     * @param padSensitivity The pad sensitivity
     */
    public void setPadSensitivityPush2 (final int padSensitivity)
    {
        this.padSensitivitySetting.set (padSensitivity);
    }


    /**
     * Get the pad gain for Push 2.
     *
     * @return The pad gain
     */
    public int getPadGainPush2 ()
    {
        return this.padGainPush2;
    }


    /**
     * Set the pad gain for Push 2.
     *
     * @param padGain The pad gain
     */
    public void setPadGainPush2 (final int padGain)
    {
        this.padGainSetting.set (padGain);
    }


    /**
     * Get the pad dynamics for Push 2.
     *
     * @return The pad dynamics.
     */
    public int getPadDynamicsPush2 ()
    {
        return this.padDynamicsPush2;
    }


    /**
     * Set the pad dynamics for Push 2.
     *
     * @param padDynamics The pad dynamics.
     */
    public void setPadDynamicsPush2 (final int padDynamics)
    {
        this.padDynamicsSetting.set (padDynamics);
    }


    /**
     * Get the threshold parameter of the pad curve for Push 3.
     *
     * @return The threshold parameter value in the range of [0..100]
     */
    public int getPadCurveThresholdPush3 ()
    {
        return this.padCurveThresholdPush3;
    }


    /**
     * Set the threshold parameter of the pad curve for Push 3.
     *
     * @param threshold The threshold parameter value in the range of [0..100]
     */
    public void setPadCurveThresholdPush3 (final int threshold)
    {
        this.padCurveThresholdSetting.set (threshold);
    }


    /**
     * Get the drive parameter of the pad curve for Push 3.
     *
     * @return The drive parameter value in the range of [-50..50]
     */
    public int getPadCurveDrivePush3 ()
    {
        return this.padCurveDrivePush3;
    }


    /**
     * Set the drive parameter of the pad curve for Push 3.
     *
     * @param drive The drive parameter value in the range of [-50..50]
     */
    public void setPadCurveDrivePush3 (final int drive)
    {
        this.padCurveDriveSetting.set (drive);
    }


    /**
     * Get the compand parameter of the pad curve for Push 3.
     *
     * @return The compand parameter value in the range of [-50..50]
     */
    public int getPadCurveCompandPush3 ()
    {
        return this.padCurveCompandPush3;
    }


    /**
     * Set the compand parameter of the pad curve for Push 3.
     *
     * @param compand The compand parameter value in the range of [-50..50]
     */
    public void setPadCurveCompandPush3 (final int compand)
    {
        this.padCurveCompandSetting.set (compand);
    }


    /**
     * Get the range parameter of the pad curve for Push 3.
     *
     * @return The range parameter value in the range of [0..100]
     */
    public int getPadCurveRangePush3 ()
    {
        return this.padCurveRangePush3;
    }


    /**
     * Set the range parameter of the pad curve for Push 3.
     *
     * @param range The range parameter value in the range of [0..100]
     */
    public void setPadCurveRangePush3 (final int range)
    {
        this.padCurveRangeSetting.set (range);
    }


    /**
     * Get the current mode which is selected for mixing.
     *
     * @return The ID of the current mode which is selected for mixing.
     */
    public Modes getCurrentMixMode ()
    {
        return Modes.isTrackMode (this.debugMode) ? this.debugMode : null;
    }


    /**
     * Set the current mode which is selected for layer mixing.
     *
     * @param layerMode The ID of a layer mode
     */
    public void setLayerMixMode (final Modes layerMode)
    {
        this.layerMode = layerMode;
    }


    /**
     * Get the current mode which is selected for layer mixing.
     *
     * @return The ID of the current mode which is selected for layer mixing.
     */
    public Modes getCurrentLayerMixMode ()
    {
        if (this.layerMode != null)
            return this.layerMode;

        final Modes currentMixMode = this.getCurrentMixMode ();
        if (!this.isPushModern () || currentMixMode == null)
            this.layerMode = Modes.DEVICE_LAYER;
        else
        {
            switch (currentMixMode)
            {
                case VOLUME:
                    this.layerMode = Modes.DEVICE_LAYER_VOLUME;
                    break;

                case PAN:
                    this.layerMode = Modes.DEVICE_LAYER_PAN;
                    break;

                case SEND1:
                    this.layerMode = Modes.DEVICE_LAYER_SEND1;
                    break;
                case SEND2:
                    this.layerMode = Modes.DEVICE_LAYER_SEND2;
                    break;
                case SEND3:
                    this.layerMode = Modes.DEVICE_LAYER_SEND3;
                    break;
                case SEND4:
                    this.layerMode = Modes.DEVICE_LAYER_SEND4;
                    break;
                case SEND5:
                    this.layerMode = Modes.DEVICE_LAYER_SEND5;
                    break;
                case SEND6:
                    this.layerMode = Modes.DEVICE_LAYER_SEND6;
                    break;
                case SEND7:
                    this.layerMode = Modes.DEVICE_LAYER_SEND7;
                    break;
                case SEND8:
                    this.layerMode = Modes.DEVICE_LAYER_SEND8;
                    break;

                case TRACK:
                default:
                    this.layerMode = Modes.DEVICE_LAYER;
                    break;
            }
        }
        return this.layerMode;
    }


    /** {@inheritDoc} */
    @Override
    public ColorEx getColorBackground ()
    {
        return this.colorBackground;
    }


    /** {@inheritDoc} */
    @Override
    public ColorEx getColorBackgroundDarker ()
    {
        return this.colorBackgroundDarker;
    }


    /** {@inheritDoc} */
    @Override
    public ColorEx getColorBackgroundLighter ()
    {
        return this.colorBackgroundLighter;
    }


    /** {@inheritDoc} */
    @Override
    public ColorEx getColorBorder ()
    {
        return this.colorBorder;
    }


    /** {@inheritDoc} */
    @Override
    public ColorEx getColorText ()
    {
        return this.colorText;
    }


    /** {@inheritDoc} */
    @Override
    public ColorEx getColorEdit ()
    {
        return this.colorEdit;
    }


    /** {@inheritDoc} */
    @Override
    public ColorEx getColorFader ()
    {
        return this.colorFader;
    }


    /** {@inheritDoc} */
    @Override
    public ColorEx getColorVu ()
    {
        return this.colorVU;
    }


    /** {@inheritDoc} */
    @Override
    public ColorEx getColorRecord ()
    {
        return this.colorRecord;
    }


    /** {@inheritDoc} */
    @Override
    public ColorEx getColorSolo ()
    {
        return this.colorSolo;
    }


    /** {@inheritDoc} */
    @Override
    public ColorEx getColorMute ()
    {
        return this.colorMute;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isAntialiasEnabled ()
    {
        return true;
    }


    /**
     * Get the selected display mode for debugging.
     *
     * @return The ID of a mode
     */
    public Modes getMixerMode ()
    {
        return this.debugMode;
    }


    /**
     * Set the selected display mode for debugging.
     *
     * @param debugMode The ID of a mode
     */
    public void setMixerMode (final Modes debugMode)
    {
        this.debugModeSetting.set (debugMode.toString ());
    }


    /**
     * Is per-pad pitchbend enabled?
     *
     * @return True if enabled
     */
    public boolean isPerPadPitchbend ()
    {
        return this.perPadPitchbend;
    }


    /**
     * Change the MPE per-pad pitchbend enabled setting.
     *
     * @param control The control value
     */
    public void changePerPadPitchbendEnabled (final int control)
    {
        if (this.perPadPitchbendSetting != null)
            this.perPadPitchbendSetting.set (ON_OFF_OPTIONS[this.valueChanger.isIncrease (control) ? 1 : 0]);
    }


    /**
     * Set the MPE per-pad pitchbend enabled setting.
     *
     * @param enable True to enable
     */
    public void setPerPadPitchbendEnabled (final boolean enable)
    {
        if (this.perPadPitchbendSetting != null)
            this.perPadPitchbendSetting.set (ON_OFF_OPTIONS[enable ? 1 : 0]);
    }


    /**
     * Get the in-tune location setting.
     *
     * @return The in-tune setting (1 = Finger, 0 = Pad)
     */
    public int getInTuneLocation ()
    {
        return this.inTuneLocation;
    }


    /**
     * Change the MPE in-tune location setting.
     *
     * @param control The control value
     */
    public void changeInTuneLocation (final int control)
    {
        final int index = this.valueChanger.changeValue (control, this.inTuneLocation, -100, IN_TUNE_LOCATION_OPTIONS.length);
        this.inTuneLocationSetting.set (IN_TUNE_LOCATION_OPTIONS[index]);
    }


    /**
     * Set the MPE in-tune location setting.
     *
     * @param value The value in the range of [0..1]
     */
    public void setInTuneLocation (final int value)
    {
        this.inTuneLocationSetting.set (IN_TUNE_LOCATION_OPTIONS[value == 0 ? 0 : 1]);
    }


    /**
     * Get the in-tune width.
     *
     * @return The index of the selected in-tune width option
     */
    public int getInTuneWidth ()
    {
        return this.inTuneWidth;
    }


    /**
     * Change the MPE in-tune width setting.
     *
     * @param control The control value
     */
    public void changeInTuneWidth (final int control)
    {
        final int index = this.valueChanger.changeValue (control, this.inTuneWidth, -100, IN_TUNE_WIDTH_OPTIONS.length);
        this.inTuneWidthSetting.set (IN_TUNE_WIDTH_OPTIONS[index]);
    }


    /**
     * Set the MPE in-tune width setting.
     *
     * @param value The value
     */
    public void setInTuneWidth (final int value)
    {
        this.inTuneWidthSetting.set (IN_TUNE_WIDTH_OPTIONS[Math.min (IN_TUNE_WIDTH_OPTIONS.length - 1, Math.max (0, value))]);
    }


    /**
     * Get the slide height.
     *
     * @return The index of the selected slide height option
     */
    public int getInTuneSlideHeight ()
    {
        return this.slideHeight;
    }


    /**
     * Change the MPE slide height setting.
     *
     * @param control The control value
     */
    public void changeSlideHeight (final int control)
    {
        final int index = this.valueChanger.changeValue (control, this.slideHeight, -100, SLIDE_HEIGHT_OPTIONS.length);
        this.slideHeightSetting.set (SLIDE_HEIGHT_OPTIONS[index]);
    }


    /**
     * Set the MPE slide height setting.
     *
     * @param value The value
     */
    public void setSlideHeight (final int value)
    {
        this.slideHeightSetting.set (SLIDE_HEIGHT_OPTIONS[Math.min (SLIDE_HEIGHT_OPTIONS.length - 1, Math.max (0, value))]);
    }


    /**
     * Use CV or footswitch on 1st connector?
     *
     * @return 1 if CV is used otherwise 0
     */
    public int getPedal1 ()
    {
        return this.pedal1;
    }


    /**
     * Set use CV output or foot switch 1.
     *
     * @param pedal 0 = Footswitch, 1 = CV Out
     */
    public void setPedal1 (final int pedal)
    {
        this.pedal1Setting.set (FOOT_CV_OPTIONS[pedal]);
    }


    /**
     * Change use CV output or foot switch 1.
     *
     * @param control The control value
     */
    public void changePedal1 (final int control)
    {
        this.pedal1Setting.set (FOOT_CV_OPTIONS[this.valueChanger.isIncrease (control) ? 1 : 0]);
    }


    /**
     * Use CV or footswitch on 2nd connector?
     *
     * @return 1 if CV is used otherwise 0
     */
    public int getPedal2 ()
    {
        return this.pedal2;
    }


    /**
     * Set use CV output or foot switch 1.
     *
     * @param pedal 0 = Footswitch, 1 = CV Out
     */
    public void setPedal2 (final int pedal)
    {
        this.pedal1Setting.set (FOOT_CV_OPTIONS[pedal]);
    }


    /**
     * Change use CV output or foot switch 2.
     *
     * @param control The control value
     */
    public void changePedal2 (final int control)
    {
        this.pedal2Setting.set (FOOT_CV_OPTIONS[this.valueChanger.isIncrease (control) ? 1 : 0]);
    }


    /**
     * Get the pre-amp type 1.
     *
     * @return The pre-amp type (0-2)
     */
    public int getPreamp1Type ()
    {
        return this.preamp1Type;
    }


    /**
     * Set the type for pre-amp 1.
     *
     * @param preampType The pre-amp Type
     */
    public void setPreamp1Type (final int preampType)
    {
        this.preamp1Setting.set (PREAMP_TYPE_OPTIONS[Math.min (PREAMP_TYPE_OPTIONS.length - 1, Math.max (0, preampType))]);
    }


    /**
     * Change the type for pre-amp 1.
     *
     * @param control The control value
     */
    public void changePreamp1Type (final int control)
    {
        final int index = this.valueChanger.changeValue (control, this.preamp1Type, -100, PREAMP_TYPE_OPTIONS.length);
        this.preamp1Setting.set (PREAMP_TYPE_OPTIONS[index]);
    }


    /**
     * Get the pre-amp type 2.
     *
     * @return The pre-amp type (0-2)
     */
    public int getPreamp2Type ()
    {
        return this.preamp2Type;
    }


    /**
     * Set the type for pre-amp 2.
     *
     * @param preampType The pre-amp Type
     */
    public void setPreamp2Type (final int preampType)
    {
        this.preamp2Setting.set (PREAMP_TYPE_OPTIONS[Math.min (PREAMP_TYPE_OPTIONS.length - 1, Math.max (0, preampType))]);
    }


    /**
     * Change the type for pre-amp 2.
     *
     * @param control The control value
     */
    public void changePreamp2Type (final int control)
    {
        final int index = this.valueChanger.changeValue (control, this.preamp2Type, -100, PREAMP_TYPE_OPTIONS.length);
        this.preamp2Setting.set (PREAMP_TYPE_OPTIONS[index]);
    }


    /**
     * Get the gain for pre-amp 1.
     *
     * @return The gain
     */
    public int getPreamp1Gain ()
    {
        return this.preamp1Gain;
    }


    /**
     * Set the gain for pre-amp 1.
     *
     * @param preampGain The gain
     */
    public void setPreamp1Gain (final int preampGain)
    {
        this.preamp1GainSetting.set (PREAMP_GAIN_OPTIONS[Math.min (PREAMP_GAIN_OPTIONS.length - 1, Math.max (0, preampGain))]);
    }


    /**
     * Change the gain for pre-amp 1.
     *
     * @param control The control value
     */
    public void changePreamp1Gain (final int control)
    {
        final int index = this.valueChanger.changeValue (control, this.preamp1Gain, -100, PREAMP_GAIN_OPTIONS.length);
        this.preamp1GainSetting.set (PREAMP_GAIN_OPTIONS[index]);
    }


    /**
     * Get the gain for pre-amp 2.
     *
     * @return The gain
     */
    public int getPreamp2Gain ()
    {
        return this.preamp2Gain;
    }


    /**
     * Set the gain for pre-amp 2.
     *
     * @param preampGain The gain
     */
    public void setPreamp2Gain (final int preampGain)
    {
        this.preamp2GainSetting.set (PREAMP_GAIN_OPTIONS[Math.min (PREAMP_GAIN_OPTIONS.length - 1, Math.max (0, preampGain))]);
    }


    /**
     * Change the gain for pre-amp 2.
     *
     * @param control The control value
     */
    public void changePreamp2Gain (final int control)
    {
        final int index = this.valueChanger.changeValue (control, this.preamp2Gain, -100, PREAMP_GAIN_OPTIONS.length);
        this.preamp2GainSetting.set (PREAMP_GAIN_OPTIONS[index]);
    }


    /**
     * Get the index of the audio output configuration.
     *
     * @return The configuration index
     */
    public int getAudioOutputs ()
    {
        return this.audioOutputs;
    }


    /**
     * Set the output configuration.
     *
     * @param audioOutputs The index of the audio output configuration to set
     */
    public void setAudioOutputs (final int audioOutputs)
    {
        this.audioOutputSetting.set (OUTPUT_CONFIGURATION[audioOutputs]);
    }


    /**
     * Change the output configuration.
     *
     * @param control The control value
     */
    public void changeAudioOutputs (final int control)
    {
        final int index = this.valueChanger.changeValue (control, this.audioOutputs, -100, OUTPUT_CONFIGURATION.length);
        this.audioOutputSetting.set (OUTPUT_CONFIGURATION[index]);
    }


    /**
     * Activate the session view settings.
     *
     * @param settingsUI The settings
     */
    private void activateSessionView (final ISettingsUI settingsUI)
    {
        this.sessionViewSetting = settingsUI.getEnumSetting ("Pads", CATEGORY_SESSION, SESSION_VIEW_OPTIONS, SESSION_VIEW_OPTIONS[0]);
        this.sessionViewSetting.addValueObserver (value -> {
            this.flipSession = SESSION_VIEW_OPTIONS[1].equals (value);
            this.isScenesClipView = SESSION_VIEW_OPTIONS[2].equals (value);
            this.notifyObservers (AbstractConfiguration.FLIP_SESSION);
            this.notifyObservers (PushConfiguration.SESSION_VIEW);
        });
    }


    /**
     * Activate the cursor keys track option.
     *
     * @param settingsUI The settings
     */
    private void activateCursorKeysSettings (final ISettingsUI settingsUI)
    {
        final IEnumSetting cursorKeysTrackSetting = settingsUI.getEnumSetting ("Cursor Keys Track Option", CATEGORY_WORKFLOW, CURSOR_KEYS_TRACK_OPTIONS, CURSOR_KEYS_TRACK_OPTIONS[0]);
        cursorKeysTrackSetting.addValueObserver (value -> this.cursorKeysTrackOption = lookupIndex (CURSOR_KEYS_TRACK_OPTIONS, value));
        final IEnumSetting cursorKeysTrackShiftedSetting = settingsUI.getEnumSetting ("Shifted Cursor Keys Track Option", CATEGORY_WORKFLOW, CURSOR_KEYS_TRACK_OPTIONS, CURSOR_KEYS_TRACK_OPTIONS[2]);
        cursorKeysTrackShiftedSetting.addValueObserver (value -> this.cursorKeysTrackShiftedOption = lookupIndex (CURSOR_KEYS_TRACK_OPTIONS, value));

        final IEnumSetting cursorKeysSceneSetting = settingsUI.getEnumSetting ("Cursor Keys Scene Option", CATEGORY_WORKFLOW, CURSOR_KEYS_SCENE_OPTIONS, CURSOR_KEYS_SCENE_OPTIONS[1]);
        cursorKeysSceneSetting.addValueObserver (value -> this.cursorKeysSceneOption = lookupIndex (CURSOR_KEYS_SCENE_OPTIONS, value));
        final IEnumSetting cursorKeysSceneShiftedSetting = settingsUI.getEnumSetting ("Shifted Cursor Keys Scene Option", CATEGORY_WORKFLOW, CURSOR_KEYS_SCENE_OPTIONS, CURSOR_KEYS_SCENE_OPTIONS[0]);
        cursorKeysSceneShiftedSetting.addValueObserver (value -> this.cursorKeysSceneShiftedOption = lookupIndex (CURSOR_KEYS_SCENE_OPTIONS, value));
    }


    /** {@inheritDoc} */
    @Override
    public void setFlipSession (final boolean enabled)
    {
        this.sessionViewSetting.set (enabled ? SESSION_VIEW_OPTIONS[1] : SESSION_VIEW_OPTIONS[0]);
    }


    /**
     * Set the scene view.
     */
    public void setSceneView ()
    {
        this.sessionViewSetting.set (SESSION_VIEW_OPTIONS[2]);
    }


    /**
     * Returns true if the scene/clip view is enabled (otherwise the normal session view).
     *
     * @return True if the scene/clip view is enabled
     */
    public boolean isScenesClipViewSelected ()
    {
        return this.isScenesClipView;
    }


    /**
     * Activate the Push 2 hardware settings.
     *
     * @param settingsUI The settings
     */
    private void activatePush2HardwareSettings (final ISettingsUI settingsUI)
    {
        if (this.pushVersion == PushVersion.VERSION_1)
            return;

        this.displayBrightnessSetting = settingsUI.getRangeSetting ("Display Brightness", CATEGORY_HARDWARE_SETUP, 0, 100, 1, "%", 100);
        this.displayBrightnessSetting.addValueObserver (value -> {
            this.displayBrightness = value.intValue ();
            this.notifyObservers (DISPLAY_BRIGHTNESS);
        });

        this.ledBrightnessSetting = settingsUI.getRangeSetting ("LED Brightness", CATEGORY_HARDWARE_SETUP, 0, 100, 1, "%", 100);
        this.ledBrightnessSetting.addValueObserver (value -> {
            this.ledBrightness = value.intValue ();
            this.notifyObservers (LED_BRIGHTNESS);
        });
    }


    /**
     * Activate the ribbon settings.
     *
     * @param settingsUI The settings
     */
    private void activateRibbonSettings (final ISettingsUI settingsUI)
    {
        this.ribbonModeSetting = settingsUI.getEnumSetting ("Mode", CATEGORY_RIBBON, RIBBON_MODE_VALUES, RIBBON_MODE_VALUES[0]);
        this.ribbonModeSetting.addValueObserver (value -> {
            this.ribbonMode = lookupIndex (RIBBON_MODE_VALUES, value);
            this.notifyObservers (RIBBON_MODE);
        });

        this.ribbonModeCCSetting = settingsUI.getRangeSetting ("CC", CATEGORY_RIBBON, 0, 127, 1, "", 1);
        this.ribbonModeCCSetting.addValueObserver (value -> {
            this.ribbonModeCCVal = value.intValue ();
            this.notifyObservers (RIBBON_MODE_CC_VAL);
        });

        this.ribbonModeNoteRepeatSetting = settingsUI.getEnumSetting ("Function if Note Repeat is active", CATEGORY_RIBBON, RIBBON_NOTE_REPEAT_VALUES, RIBBON_NOTE_REPEAT_VALUES[1]);
        this.ribbonModeNoteRepeatSetting.addValueObserver (value -> {
            this.ribbonModeNoteRepeat = lookupIndex (RIBBON_NOTE_REPEAT_VALUES, value);
            this.notifyObservers (RIBBON_MODE_NOTE_REPEAT);
        });
    }


    /**
     * Activate the stop automation on knob release setting.
     *
     * @param settingsUI The settings
     */
    private void activateStopAutomationOnKnobReleaseSetting (final ISettingsUI settingsUI)
    {
        settingsUI.getEnumSetting ("Stop automation recording on knob release", CATEGORY_WORKFLOW, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]).addValueObserver (value -> {
            this.stopAutomationOnKnobRelease = "On".equals (value);
            this.notifyObservers (STOP_AUTOMATION_ON_KNOB_RELEASE);
        });
    }


    /**
     * Activate the Push 1 pad settings.
     *
     * @param settingsUI The settings
     */
    private void activatePush1PadSettings (final ISettingsUI settingsUI)
    {
        this.velocityCurveSetting = settingsUI.getEnumSetting ("Velocity Curve", CATEGORY_PADS, PushControlSurface.PUSH_PAD_CURVES_NAME, PushControlSurface.PUSH_PAD_CURVES_NAME.get (1));
        this.velocityCurveSetting.addValueObserver (value -> {
            this.velocityCurvePush1 = lookupIndex (PushControlSurface.PUSH_PAD_CURVES_NAME, value);
            this.notifyObservers (VELOCITY_CURVE);
        });

        this.padThresholdSetting = settingsUI.getEnumSetting ("Pad Threshold", CATEGORY_PADS, PushControlSurface.PUSH_PAD_THRESHOLDS_NAME, PushControlSurface.PUSH_PAD_THRESHOLDS_NAME.get (20));
        this.padThresholdSetting.addValueObserver (value -> {
            this.padThresholdPush1 = lookupIndex (PushControlSurface.PUSH_PAD_THRESHOLDS_NAME, value);
            this.notifyObservers (PAD_PUSH1_THRESHOLD);
        });
    }


    /**
     * Activate the Push 2 pad settings.
     *
     * @param settingsUI The settings
     */
    private void activatePush2PadSettings (final ISettingsUI settingsUI)
    {
        this.padSensitivitySetting = settingsUI.getRangeSetting ("Sensitivity", CATEGORY_PADS, 0, 10, 1, "", 5);
        this.padSensitivitySetting.addValueObserver (value -> {
            this.padSensitivityPush2 = value.intValue ();
            this.notifyObservers (PAD_PUSH2_SENSITIVITY);
        });

        this.padGainSetting = settingsUI.getRangeSetting ("Gain", CATEGORY_PADS, 0, 10, 1, "", 5);
        this.padGainSetting.addValueObserver (value -> {
            this.padGainPush2 = value.intValue ();
            this.notifyObservers (PAD_PUSH2_GAIN);
        });

        this.padDynamicsSetting = settingsUI.getRangeSetting ("Dynamics", CATEGORY_PADS, 0, 10, 1, "", 5);
        this.padDynamicsSetting.addValueObserver (value -> {
            this.padDynamicsPush2 = value.intValue ();
            this.notifyObservers (PAD_PUSH2_DYNAMICS);
        });
    }


    /**
     * Activate the Push 3 pad settings.
     *
     * @param settingsUI The settings
     */
    private void activatePush3PadSettings (final ISettingsUI settingsUI)
    {
        this.padCurveThresholdSetting = settingsUI.getRangeSetting ("Threshold", CATEGORY_PADS, 0, 100, 1, "", 7);
        this.padCurveThresholdSetting.addValueObserver (value -> {
            this.padCurveThresholdPush3 = value.intValue ();
            this.notifyObservers (PAD_PUSH3_THRESHOLD);
        });

        this.padCurveDriveSetting = settingsUI.getRangeSetting ("Drive", CATEGORY_PADS, -50, 50, 1, "", 4);
        this.padCurveDriveSetting.addValueObserver (value -> {
            this.padCurveDrivePush3 = value.intValue ();
            this.notifyObservers (PAD_PUSH3_DRIVE);
        });

        this.padCurveCompandSetting = settingsUI.getRangeSetting ("Compand", CATEGORY_PADS, -50, 50, 1, "", -26);
        this.padCurveCompandSetting.addValueObserver (value -> {
            this.padCurveCompandPush3 = value.intValue ();
            this.notifyObservers (PAD_PUSH3_COMPAND);
        });

        this.padCurveRangeSetting = settingsUI.getRangeSetting ("Range", CATEGORY_PADS, 0, 100, 1, "", 35);
        this.padCurveRangeSetting.addValueObserver (value -> {
            this.padCurveRangePush3 = value.intValue ();
            this.notifyObservers (PAD_PUSH3_RANGE);
        });
    }


    /**
     * Activate the Push 3 MPE pad settings.
     *
     * @param settingsUI The settings
     */
    private void activatePush3MPESettings (final ISettingsUI settingsUI)
    {
        this.activateMPESetting (settingsUI, CATEGORY_PADS, true);

        this.perPadPitchbendSetting = settingsUI.getEnumSetting ("Per-Pad Pitchbend", CATEGORY_PADS, ON_OFF_OPTIONS, ON_OFF_OPTIONS[1]);
        this.perPadPitchbendSetting.addValueObserver (value -> {
            this.perPadPitchbend = ON_OFF_OPTIONS[1].equals (value);
            this.notifyObservers (PER_PAD_PITCHBEND);
        });

        this.inTuneLocationSetting = settingsUI.getEnumSetting ("In Tune Location", CATEGORY_PADS, IN_TUNE_LOCATION_OPTIONS, IN_TUNE_LOCATION_OPTIONS[1]);
        this.inTuneLocationSetting.addValueObserver (value -> {
            this.inTuneLocation = lookupIndex (IN_TUNE_LOCATION_OPTIONS, value);
            this.notifyObservers (IN_TUNE_LOCATION);
        });

        this.inTuneWidthSetting = settingsUI.getEnumSetting ("In Tune Width (mm)", CATEGORY_PADS, IN_TUNE_WIDTH_OPTIONS, IN_TUNE_WIDTH_OPTIONS[9]);
        this.inTuneWidthSetting.addValueObserver (value -> {
            this.inTuneWidth = lookupIndex (IN_TUNE_WIDTH_OPTIONS, value);
            this.notifyObservers (IN_TUNE_WIDTH);
        });

        this.slideHeightSetting = settingsUI.getEnumSetting ("Slide Height (mm)", CATEGORY_PADS, SLIDE_HEIGHT_OPTIONS, SLIDE_HEIGHT_OPTIONS[3]);
        this.slideHeightSetting.addValueObserver (value -> {
            this.slideHeight = lookupIndex (SLIDE_HEIGHT_OPTIONS, value);
            this.notifyObservers (IN_TUNE_SLIDE_HEIGHT);
        });
    }


    /**
     * Activate the Push 3 audio interface settings.
     *
     * @param settingsUI The settings
     */
    private void activatePush3AudioSettings (final ISettingsUI settingsUI)
    {
        this.pedal1Setting = settingsUI.getEnumSetting ("Pedal & CV 1", CATEGORY_AUDIO, FOOT_CV_OPTIONS, FOOT_CV_OPTIONS[0]);
        this.pedal1Setting.addValueObserver (value -> {
            this.pedal1 = lookupIndex (FOOT_CV_OPTIONS, value);
            this.notifyObservers (PEDAL_1);
        });

        this.pedal2Setting = settingsUI.getEnumSetting ("Pedal & CV 2", CATEGORY_AUDIO, FOOT_CV_OPTIONS, FOOT_CV_OPTIONS[0]);
        this.pedal2Setting.addValueObserver (value -> {
            this.pedal2 = lookupIndex (FOOT_CV_OPTIONS, value);
            this.notifyObservers (PEDAL_2);
        });

        this.preamp1Setting = settingsUI.getEnumSetting ("Preamp 1", CATEGORY_AUDIO, PREAMP_TYPE_OPTIONS, PREAMP_TYPE_OPTIONS[0]);
        this.preamp1Setting.addValueObserver (value -> {
            this.preamp1Type = lookupIndex (PREAMP_TYPE_OPTIONS, value);
            this.notifyObservers (PREAMP_TYPE_1);
        });

        this.preamp2Setting = settingsUI.getEnumSetting ("Preamp 2", CATEGORY_AUDIO, PREAMP_TYPE_OPTIONS, PREAMP_TYPE_OPTIONS[0]);
        this.preamp2Setting.addValueObserver (value -> {
            this.preamp2Type = lookupIndex (PREAMP_TYPE_OPTIONS, value);
            this.notifyObservers (PREAMP_TYPE_2);
        });

        this.preamp1GainSetting = settingsUI.getEnumSetting ("Gain 1", CATEGORY_AUDIO, PREAMP_GAIN_OPTIONS, PREAMP_GAIN_OPTIONS[0]);
        this.preamp1GainSetting.addValueObserver (value -> {
            this.preamp1Gain = lookupIndex (PREAMP_GAIN_OPTIONS, value);
            this.notifyObservers (PREAMP_GAIN_1);
        });

        this.preamp2GainSetting = settingsUI.getEnumSetting ("Gain 2", CATEGORY_AUDIO, PREAMP_GAIN_OPTIONS, PREAMP_GAIN_OPTIONS[0]);
        this.preamp2GainSetting.addValueObserver (value -> {
            this.preamp2Gain = lookupIndex (PREAMP_GAIN_OPTIONS, value);
            this.notifyObservers (PREAMP_GAIN_2);
        });

        this.audioOutputSetting = settingsUI.getEnumSetting ("Outputs", CATEGORY_AUDIO, OUTPUT_CONFIGURATION, OUTPUT_CONFIGURATION[0]);
        this.audioOutputSetting.addValueObserver (value -> {
            this.audioOutputs = lookupIndex (OUTPUT_CONFIGURATION, value);
            this.notifyObservers (AUDIO_OUTPUTS);
        });
    }


    /**
     * Activate the color settings for the Push 2 display.
     *
     * @param settingsUI The settings
     */
    private void activatePush2DisplayColorsSettings (final ISettingsUI settingsUI)
    {
        if (this.pushVersion == PushVersion.VERSION_1)
            return;

        settingsUI.getSignalSetting ("Reset colors to default", CATEGORY_COLORS, "Reset").addSignalObserver (value -> {
            this.colorBackgroundSetting.set (DEFAULT_COLOR_BACKGROUND);
            this.colorBackgroundDarkerSetting.set (DEFAULT_COLOR_BACKGROUND_DARKER);
            this.colorBackgroundLighterSetting.set (DEFAULT_COLOR_BACKGROUND_LIGHTER);
            this.colorBorderSetting.set (DEFAULT_COLOR_BORDER);
            this.colorTextSetting.set (DEFAULT_COLOR_TEXT);
            this.colorFaderSetting.set (DEFAULT_COLOR_FADER);
            this.colorVUSetting.set (DEFAULT_COLOR_VU);
            this.colorEditSetting.set (DEFAULT_COLOR_EDIT);
            this.colorRecordSetting.set (DEFAULT_COLOR_RECORD);
            this.colorSoloSetting.set (DEFAULT_COLOR_SOLO);
            this.colorMuteSetting.set (DEFAULT_COLOR_MUTE);
        });

        this.colorBackgroundSetting = settingsUI.getColorSetting ("Background", CATEGORY_COLORS, DEFAULT_COLOR_BACKGROUND);
        this.colorBackgroundSetting.addValueObserver (color -> {
            this.colorBackground = color;
            this.notifyObservers (COLOR_BACKGROUND);
        });

        this.colorBackgroundDarkerSetting = settingsUI.getColorSetting ("Background Darker", CATEGORY_COLORS, DEFAULT_COLOR_BACKGROUND_DARKER);
        this.colorBackgroundDarkerSetting.addValueObserver (color -> {
            this.colorBackgroundDarker = color;
            this.notifyObservers (COLOR_BACKGROUND_DARKER);
        });

        this.colorBackgroundLighterSetting = settingsUI.getColorSetting ("Background Selected", CATEGORY_COLORS, DEFAULT_COLOR_BACKGROUND_LIGHTER);
        this.colorBackgroundLighterSetting.addValueObserver (color -> {
            this.colorBackgroundLighter = color;
            this.notifyObservers (COLOR_BACKGROUND_LIGHTER);
        });

        this.colorBorderSetting = settingsUI.getColorSetting ("Border", CATEGORY_COLORS, DEFAULT_COLOR_BORDER);
        this.colorBorderSetting.addValueObserver (color -> {
            this.colorBorder = color;
            this.notifyObservers (COLOR_BORDER);
        });

        this.colorTextSetting = settingsUI.getColorSetting ("Text", CATEGORY_COLORS, DEFAULT_COLOR_TEXT);
        this.colorTextSetting.addValueObserver (color -> {
            this.colorText = color;
            this.notifyObservers (COLOR_TEXT);
        });

        this.colorFaderSetting = settingsUI.getColorSetting ("Fader", CATEGORY_COLORS, DEFAULT_COLOR_FADER);
        this.colorFaderSetting.addValueObserver (color -> {
            this.colorFader = color;
            this.notifyObservers (COLOR_FADER);
        });

        this.colorVUSetting = settingsUI.getColorSetting ("VU", CATEGORY_COLORS, DEFAULT_COLOR_VU);
        this.colorVUSetting.addValueObserver (color -> {
            this.colorVU = color;
            this.notifyObservers (COLOR_VU);
        });

        this.colorEditSetting = settingsUI.getColorSetting ("Edit", CATEGORY_COLORS, DEFAULT_COLOR_EDIT);
        this.colorEditSetting.addValueObserver (color -> {
            this.colorEdit = color;
            this.notifyObservers (COLOR_EDIT);
        });

        this.colorRecordSetting = settingsUI.getColorSetting ("Record", CATEGORY_COLORS, DEFAULT_COLOR_RECORD);
        this.colorRecordSetting.addValueObserver (color -> {
            this.colorRecord = color;
            this.notifyObservers (COLOR_RECORD);
        });

        this.colorSoloSetting = settingsUI.getColorSetting ("Solo", CATEGORY_COLORS, DEFAULT_COLOR_SOLO);
        this.colorSoloSetting.addValueObserver (color -> {
            this.colorSolo = color;
            this.notifyObservers (COLOR_SOLO);
        });

        this.colorMuteSetting = settingsUI.getColorSetting ("Mute", CATEGORY_COLORS, DEFAULT_COLOR_MUTE);
        this.colorMuteSetting.addValueObserver (color -> {
            this.colorMute = color;
            this.notifyObservers (COLOR_MUTE);
        });
    }


    /**
     * Activate the debug settings.
     *
     * @param settingsUI The settings
     */
    private void activateDebugSettings (final ISettingsUI settingsUI)
    {
        final String [] modes = new String [DEBUG_MODES.size ()];
        int i = 0;
        for (final Modes mode: DEBUG_MODES)
        {
            modes[i] = mode.toString ();
            i++;
        }

        this.debugModeSetting = settingsUI.getEnumSetting ("Display Mode", CATEGORY_DEBUG, modes, Modes.TRACK.toString ());
        this.debugModeSetting.addValueObserver (value -> {
            try
            {
                this.debugMode = Modes.valueOf (value);
            }
            catch (final IllegalArgumentException ex)
            {
                this.debugMode = Modes.TRACK;
            }
            this.notifyObservers (DEBUG_MODE);
        });

        if (this.pushVersion == PushVersion.VERSION_1)
            return;

        settingsUI.getSignalSetting (" ", CATEGORY_DEBUG, "Display window").addSignalObserver (value -> this.notifyObservers (DEBUG_WINDOW));
    }
}
