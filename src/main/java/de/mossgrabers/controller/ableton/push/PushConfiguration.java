// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push;

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

import java.util.EnumSet;
import java.util.List;
import java.util.Set;


/**
 * The configuration settings for Push.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PushConfiguration extends AbstractConfiguration implements IGraphicsConfiguration
{
    /** Settings for different Mute and Solo behavior. */
    public enum TrackState
    {
        /** Use Mute, Solo for muting/soloing the current track. */
        NONE,
        /** Use all mode buttons for muting. */
        MUTE,
        /** Use all mode buttons for soloing. */
        SOLO
    }


    /** Setting for the ribbon mode. */
    public static final Integer     RIBBON_MODE                     = Integer.valueOf (50);
    /** Setting for the ribbon mode MIDI CC. */
    public static final Integer     RIBBON_MODE_CC_VAL              = Integer.valueOf (51);
    /** Setting for the ribbon mode note repeat. */
    public static final Integer     RIBBON_MODE_NOTE_REPEAT         = Integer.valueOf (52);

    /** Setting for the velocity curve. */
    public static final Integer     VELOCITY_CURVE                  = Integer.valueOf (53);
    /** Setting for the pad threshold. */
    public static final Integer     PAD_THRESHOLD                   = Integer.valueOf (54);
    /** Setting for the display brightness. */
    public static final Integer     DISPLAY_BRIGHTNESS              = Integer.valueOf (55);
    /** Setting for the pad LED brightness. */
    public static final Integer     LED_BRIGHTNESS                  = Integer.valueOf (56);
    /** Setting for the pad sensitivity. */
    public static final Integer     PAD_SENSITIVITY                 = Integer.valueOf (57);
    /** Setting for the pad gain. */
    public static final Integer     PAD_GAIN                        = Integer.valueOf (58);
    /** Setting for the pad dynamics. */
    public static final Integer     PAD_DYNAMICS                    = Integer.valueOf (59);

    /** Setting for stopping automation recording on knob release. */
    public static final Integer     STOP_AUTOMATION_ON_KNOB_RELEASE = Integer.valueOf (60);
    /** Mode debug. */
    public static final Integer     DEBUG_MODE                      = Integer.valueOf (61);
    /** Push 2 display debug window. */
    public static final Integer     DEBUG_WINDOW                    = Integer.valueOf (62);

    /** Background color of an element. */
    public static final Integer     COLOR_BACKGROUND                = Integer.valueOf (70);
    /** Border color of an element. */
    public static final Integer     COLOR_BORDER                    = Integer.valueOf (71);
    /** Text color of an element. */
    public static final Integer     COLOR_TEXT                      = Integer.valueOf (72);
    /** Fader color of an element. */
    public static final Integer     COLOR_FADER                     = Integer.valueOf (73);
    /** VU color of an element. */
    public static final Integer     COLOR_VU                        = Integer.valueOf (74);
    /** Edit color of an element. */
    public static final Integer     COLOR_EDIT                      = Integer.valueOf (75);
    /** Record color of an element. */
    public static final Integer     COLOR_RECORD                    = Integer.valueOf (76);
    /** Solo color of an element. */
    public static final Integer     COLOR_SOLO                      = Integer.valueOf (77);
    /** Mute color of an element. */
    public static final Integer     COLOR_MUTE                      = Integer.valueOf (78);
    /** Background color darker of an element. */
    public static final Integer     COLOR_BACKGROUND_DARKER         = Integer.valueOf (79);
    /** Background color lighter of an element. */
    public static final Integer     COLOR_BACKGROUND_LIGHTER        = Integer.valueOf (80);

    /** Session view options. */
    public static final Integer     SESSION_VIEW                    = Integer.valueOf (81);
    /** Display scenes or clips. */
    public static final Integer     DISPLAY_SCENES_CLIPS            = Integer.valueOf (82);

    /** Use ribbon for pitch bend. */
    public static final int         RIBBON_MODE_PITCH               = 0;
    /** Use ribbon for MIDI CC. */
    public static final int         RIBBON_MODE_CC                  = 1;
    /** Use ribbon for MIDI CC and pitch bend. */
    public static final int         RIBBON_MODE_CC_PB               = 2;
    /** Use ribbon for pitch bend and MIDI CC. */
    public static final int         RIBBON_MODE_PB_CC               = 3;
    /** Use ribbon as volume fader. */
    public static final int         RIBBON_MODE_FADER               = 4;
    /** Use ribbon to change the last touched parameter. */
    public static final int         RIBBON_MODE_LAST_TOUCHED        = 5;

    /** Use ribbon not for note repeat settings. */
    public static final int         NOTE_REPEAT_OFF                 = 0;
    /** Use ribbon for changing the note repeat period. */
    public static final int         NOTE_REPEAT_PERIOD              = 1;
    /** Use ribbon for changing the note repeat length. */
    public static final int         NOTE_REPEAT_LENGTH              = 2;

    private static final String     CATEGORY_RIBBON                 = "Ribbon";
    private static final String     CATEGORY_COLORS                 = "Display Colors";

    private static final String []  RIBBON_MODE_VALUES              =
    {
        "Pitch",
        "CC",
        "CC/Pitch",
        "Pitch/CC",
        "Fader",
        "Last Touched"
    };

    private static final String []  RIBBON_NOTE_REPEAT_VALUES       =
    {
        "Off",
        "Period",
        "Length"
    };

    private static final String []  SESSION_VIEW_OPTIONS            =
    {
        "Session",
        "Flipped",
        "Scenes"
    };

    private static final Views []   PREFERRED_NOTE_VIEWS            =
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

    /** Debug modes. */
    private static final Set<Modes> DEBUG_MODES                     = EnumSet.noneOf (Modes.class);

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

    private boolean         isSoloLongPressed           = false;
    private boolean         isMuteLongPressed           = false;
    private boolean         isMuteSoloLocked            = false;

    private boolean         displayScenesClips;
    private boolean         isScenesClipView;

    /** What does the ribbon send? **/
    private int             ribbonMode                  = RIBBON_MODE_PITCH;
    private int             ribbonModeCCVal             = 1;
    private int             ribbonModeNoteRepeat        = NOTE_REPEAT_PERIOD;

    private boolean         stopAutomationOnKnobRelease = false;
    private TrackState      trackState                  = TrackState.MUTE;
    private Modes           debugMode                   = Modes.TRACK;
    private Modes           layerMode                   = null;

    // Only Push 1
    private int             velocityCurve               = 1;
    private int             padThreshold                = 20;

    // Only Push 2
    private int             displayBrightness           = 255;
    private int             ledBrightness               = 127;
    private int             padSensitivity              = 5;
    private int             padGain                     = 5;
    private int             padDynamics                 = 5;
    private ColorEx         colorBackground             = DEFAULT_COLOR_BACKGROUND;
    private ColorEx         colorBorder                 = DEFAULT_COLOR_BORDER;
    private ColorEx         colorText                   = DEFAULT_COLOR_TEXT;
    private ColorEx         colorFader                  = DEFAULT_COLOR_FADER;
    private ColorEx         colorVU                     = DEFAULT_COLOR_VU;
    private ColorEx         colorEdit                   = DEFAULT_COLOR_EDIT;
    private ColorEx         colorRecord                 = DEFAULT_COLOR_RECORD;
    private ColorEx         colorSolo                   = DEFAULT_COLOR_SOLO;
    private ColorEx         colorMute                   = DEFAULT_COLOR_MUTE;
    private ColorEx         colorBackgroundDarker       = DEFAULT_COLOR_BACKGROUND_DARKER;
    private ColorEx         colorBackgroundLighter      = DEFAULT_COLOR_BACKGROUND_LIGHTER;

    private final boolean   isPush2;

    private IIntegerSetting displayBrightnessSetting;
    private IIntegerSetting ledBrightnessSetting;
    private IEnumSetting    ribbonModeSetting;
    private IIntegerSetting ribbonModeCCSetting;
    private IEnumSetting    ribbonModeNoteRepeatSetting;
    private IIntegerSetting padSensitivitySetting;
    private IIntegerSetting padGainSetting;
    private IIntegerSetting padDynamicsSetting;
    private IEnumSetting    velocityCurveSetting;
    private IEnumSetting    padThresholdSetting;
    private IEnumSetting    debugModeSetting;
    private IColorSetting   colorBackgroundSetting;
    private IColorSetting   colorBackgroundDarkerSetting;
    private IColorSetting   colorBackgroundLighterSetting;
    private IColorSetting   colorBorderSetting;
    private IColorSetting   colorTextSetting;
    private IColorSetting   colorFaderSetting;
    private IColorSetting   colorVUSetting;
    private IColorSetting   colorEditSetting;
    private IColorSetting   colorRecordSetting;
    private IColorSetting   colorSoloSetting;
    private IColorSetting   colorMuteSetting;
    private IEnumSetting    sessionViewSetting;
    private IEnumSetting    displayScenesClipsSetting;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param arpeggiatorModes The available arpeggiator modes
     * @param isPush2 Use Push 1 or Push 2 controller?
     */
    public PushConfiguration (final IHost host, final IValueChanger valueChanger, final List<ArpeggiatorMode> arpeggiatorModes, final boolean isPush2)
    {
        super (host, valueChanger, arpeggiatorModes);

        this.isPush2 = isPush2;
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
        this.activatePreferredNoteViewSetting (globalSettings, PREFERRED_NOTE_VIEWS);
        this.activateStartWithSessionViewSetting (globalSettings);
        this.activateMidiEditChannelSetting (documentSettings);

        ///////////////////////////
        // Drum Sequencer

        if (this.host.supports (Capability.HAS_DRUM_DEVICE))
        {
            this.activateAutoSelectDrumSetting (globalSettings);
            this.activateTurnOffEmptyDrumPadsSetting (globalSettings);
        }

        ///////////////////////////
        // Workflow

        this.activateExcludeDeactivatedItemsSetting (globalSettings);
        this.activateEnableVUMetersSetting (globalSettings);
        this.activateFootswitchSetting (globalSettings, 0, "Footswitch 2");
        this.activateStopAutomationOnKnobReleaseSetting (globalSettings);
        this.activateNewClipLengthSetting (globalSettings);
        this.activateKnobSpeedSetting (globalSettings);
        this.activateUserPageNamesSetting (documentSettings);

        ///////////////////////////
        // Add Track - Device Favorites

        this.activateDeviceFavorites (globalSettings, 7, 3, 3);

        ///////////////////////////
        // Ribbon

        this.activateRibbonSettings (globalSettings);

        ///////////////////////////
        // Pad Sensitivity

        if (this.isPush2)
            this.activatePush2PadSettings (globalSettings);
        else
            this.activatePush1PadSettings (globalSettings);

        this.activateConvertAftertouchSetting (globalSettings);

        ///////////////////////////
        // Browser

        this.activateBrowserSettings (globalSettings);

        ///////////////////////////
        // Push 2 Hardware

        this.activatePush2HardwareSettings (globalSettings);
        this.activatePush2DisplayColorsSettings (globalSettings);

        ///////////////////////////
        // Debugging

        this.activateDebugSettings (globalSettings);
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
        final int value = this.valueChanger.changeValue (control, this.padThreshold, -100, size);
        this.padThreshold = Math.max (0, Math.min (value, size - 1));
        this.padThresholdSetting.set (PushControlSurface.PUSH_PAD_THRESHOLDS_NAME.get (this.padThreshold));
    }


    /**
     * Change the velocity curve.
     *
     * @param control The control value
     */
    public void changeVelocityCurve (final int control)
    {
        final int size = PushControlSurface.PUSH_PAD_CURVES_NAME.size ();
        final int value = this.valueChanger.changeValue (control, this.velocityCurve, -100, size);
        this.velocityCurve = Math.max (0, Math.min (value, size - 1));
        this.velocityCurveSetting.set (PushControlSurface.PUSH_PAD_CURVES_NAME.get (this.velocityCurve));
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
        this.padSensitivitySetting.set (this.valueChanger.changeValue (control, this.padSensitivity, -100, 11));
    }


    /**
     * Change the pad gain.
     *
     * @param control The control value
     */
    public void changePadGain (final int control)
    {
        this.padGainSetting.set (this.valueChanger.changeValue (control, this.padGain, -100, 11));
    }


    /**
     * Change the pad dynamics.
     *
     * @param control The control value
     */
    public void changePadDynamics (final int control)
    {
        this.padDynamicsSetting.set (this.valueChanger.changeValue (control, this.padDynamics, -100, 11));
    }


    /**
     * Is this Push 1 or 2?
     *
     * @return True if Push 2
     */
    public boolean isPush2 ()
    {
        return this.isPush2;
    }


    /**
     * Get the velocity curve.
     *
     * @return The index of the velocity curve
     */
    public int getVelocityCurve ()
    {
        return this.velocityCurve;
    }


    /**
     * Set the velocity curve.
     *
     * @param velocityCurve The index of the velocity curve
     */
    public void setVelocityCurve (final int velocityCurve)
    {
        this.velocityCurve = velocityCurve;
    }


    /**
     * Get the pad threshold.
     *
     * @return The pad threshold
     */
    public int getPadThreshold ()
    {
        return this.padThreshold;
    }


    /**
     * Set the pad threshold.
     *
     * @param padThreshold The pad threshold
     */
    public void setPadThreshold (final int padThreshold)
    {
        this.padThreshold = padThreshold;
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
     * Is mute long pressed?
     *
     * @return True if mute is long pressed
     */
    public boolean isMuteLongPressed ()
    {
        return this.isMuteLongPressed;
    }


    /**
     * Set if mute is long pressed.
     *
     * @param isMuteLongPressed True if mute is long pressed
     */
    public void setIsMuteLongPressed (final boolean isMuteLongPressed)
    {
        this.isMuteLongPressed = isMuteLongPressed;
    }


    /**
     * Is solo long pressed?
     *
     * @return True if solo is long pressed
     */
    public boolean isSoloLongPressed ()
    {
        return this.isSoloLongPressed;
    }


    /**
     * Set if solo is long pressed.
     *
     * @param isSoloLongPressed True if solo is long pressed
     */
    public void setIsSoloLongPressed (final boolean isSoloLongPressed)
    {
        this.isSoloLongPressed = isSoloLongPressed;
    }


    /**
     * Is mute and solo locked (all mode buttons are used for solo or mute).
     *
     * @return True if locked
     */
    public boolean isMuteSoloLocked ()
    {
        return this.isMuteSoloLocked;
    }


    /**
     * Set if mute and solo is locked (all mode buttons are used for solo or mute).
     *
     * @param isMuteSoloLocked True if locked
     */
    public void setMuteSoloLocked (final boolean isMuteSoloLocked)
    {
        this.isMuteSoloLocked = isMuteSoloLocked;
    }


    /**
     * Get the pad sensitivity.
     *
     * @return The pad sensitivity
     */
    public int getPadSensitivity ()
    {
        return this.padSensitivity;
    }


    /**
     * Set the pad sensitivity.
     *
     * @param padSensitivity The pad sensitivity
     */
    public void setPadSensitivity (final int padSensitivity)
    {
        this.padSensitivitySetting.set (padSensitivity);
    }


    /**
     * Get the pad gain.
     *
     * @return The pad gain
     */
    public int getPadGain ()
    {
        return this.padGain;
    }


    /**
     * Set the pad gain.
     *
     * @param padGain The pad gain
     */
    public void setPadGain (final int padGain)
    {
        this.padGainSetting.set (padGain);
    }


    /**
     * Get the pad dynamics.
     *
     * @return The pad dynamics.
     */
    public int getPadDynamics ()
    {
        return this.padDynamics;
    }


    /**
     * Set the pad dynamics.
     *
     * @param padDynamics The pad dynamics.
     */
    public void setPadDynamics (final int padDynamics)
    {
        this.padDynamicsSetting.set (padDynamics);
    }


    /**
     * Use the 2nd row buttons for mute?
     *
     * @return True if used for mute
     */
    public boolean isMuteState ()
    {
        return this.trackState == TrackState.MUTE;
    }


    /**
     * Use the 2nd row buttons for solo?
     *
     * @return True if used for solo
     */
    public boolean isSoloState ()
    {
        return this.trackState == TrackState.SOLO;
    }


    /**
     * Set the track state.
     *
     * @param state The new track state
     */
    public void setTrackState (final TrackState state)
    {
        this.trackState = state;
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
        if (!this.isPush2 || currentMixMode == null)
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
    public Modes getDebugMode ()
    {
        return this.debugMode;
    }


    /**
     * Set the selected display mode for debugging.
     *
     * @param debugMode The ID of a mode
     */
    public void setDebugMode (final Modes debugMode)
    {
        this.debugModeSetting.set (debugMode.toString ());
    }


    /**
     * Activate the session view settings.
     *
     * @param settingsUI The settings
     */
    private void activateSessionView (final ISettingsUI settingsUI)
    {
        this.sessionViewSetting = settingsUI.getEnumSetting ("Session view", CATEGORY_SESSION, SESSION_VIEW_OPTIONS, SESSION_VIEW_OPTIONS[0]);
        this.sessionViewSetting.addValueObserver (value -> {
            this.flipSession = SESSION_VIEW_OPTIONS[1].equals (value);
            this.isScenesClipView = SESSION_VIEW_OPTIONS[2].equals (value);
            this.notifyObservers (AbstractConfiguration.FLIP_SESSION);
            this.notifyObservers (PushConfiguration.SESSION_VIEW);
        });

        this.displayScenesClipsSetting = settingsUI.getEnumSetting ("Display scenes/clips", CATEGORY_SESSION, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        this.displayScenesClipsSetting.addValueObserver (value -> {
            this.displayScenesClips = "On".equals (value);
            this.notifyObservers (PushConfiguration.DISPLAY_SCENES_CLIPS);
        });
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
     * Returns true if the session view should also switch to the scene/clip mode.
     *
     * @return True if the session view should also switch to the scene/clip mode.
     */
    public boolean shouldDisplayScenesOrClips ()
    {
        return this.displayScenesClips;
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
     * Toggles the mode display for scenes/clips in session view.
     */
    public void toggleScenesClipMode ()
    {
        this.displayScenesClipsSetting.set (this.displayScenesClips ? ON_OFF_OPTIONS[0] : ON_OFF_OPTIONS[1]);
    }


    /**
     * Activate the Push 2 hardware settings.
     *
     * @param settingsUI The settings
     */
    private void activatePush2HardwareSettings (final ISettingsUI settingsUI)
    {
        if (!this.isPush2)
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
            this.velocityCurve = lookupIndex (PushControlSurface.PUSH_PAD_CURVES_NAME, value);
            this.notifyObservers (VELOCITY_CURVE);
        });

        this.padThresholdSetting = settingsUI.getEnumSetting ("Pad Threshold", CATEGORY_PADS, PushControlSurface.PUSH_PAD_THRESHOLDS_NAME, PushControlSurface.PUSH_PAD_THRESHOLDS_NAME.get (20));
        this.padThresholdSetting.addValueObserver (value -> {
            this.padThreshold = lookupIndex (PushControlSurface.PUSH_PAD_THRESHOLDS_NAME, value);
            this.notifyObservers (PAD_THRESHOLD);
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
            this.padSensitivity = value.intValue ();
            this.notifyObservers (PAD_SENSITIVITY);
        });

        this.padGainSetting = settingsUI.getRangeSetting ("Gain", CATEGORY_PADS, 0, 10, 1, "", 5);
        this.padGainSetting.addValueObserver (value -> {
            this.padGain = value.intValue ();
            this.notifyObservers (PAD_GAIN);
        });

        this.padDynamicsSetting = settingsUI.getRangeSetting ("Dynamics", CATEGORY_PADS, 0, 10, 1, "", 5);
        this.padDynamicsSetting.addValueObserver (value -> {
            this.padDynamics = value.intValue ();
            this.notifyObservers (PAD_DYNAMICS);
        });
    }


    /**
     * Activate the color settings for the Push 2 display.
     *
     * @param settingsUI The settings
     */
    private void activatePush2DisplayColorsSettings (final ISettingsUI settingsUI)
    {
        if (!this.isPush2)
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

        if (!this.isPush2)
            return;

        settingsUI.getSignalSetting (" ", CATEGORY_DEBUG, "Display window").addSignalObserver (value -> this.notifyObservers (DEBUG_WINDOW));
    }
}
