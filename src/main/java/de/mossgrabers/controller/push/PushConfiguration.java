// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push;

import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.controller.push.mode.Modes;
import de.mossgrabers.controller.push.view.Views;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.IColorSetting;
import de.mossgrabers.framework.configuration.IEnumSetting;
import de.mossgrabers.framework.configuration.IIntegerSetting;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.IHost;

import java.util.Set;


/**
 * The configuration settings for Push.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PushConfiguration extends AbstractConfiguration
{
    /** Settings for different Mute and Solo behaviour. */
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
    public static final Integer    RIBBON_MODE                      = Integer.valueOf (30);
    /** Setting for the ribbon mode midi CC. */
    public static final Integer    RIBBON_MODE_CC_VAL               = Integer.valueOf (31);
    /** Setting for the velocity curve. */
    public static final Integer    VELOCITY_CURVE                   = Integer.valueOf (32);
    /** Setting for the pad threshold. */
    public static final Integer    PAD_THRESHOLD                    = Integer.valueOf (33);

    /** Setting for the display brightness. */
    public static final Integer    DISPLAY_BRIGHTNESS               = Integer.valueOf (34);
    /** Setting for the pad LED brightness. */
    public static final Integer    LED_BRIGHTNESS                   = Integer.valueOf (35);
    /** Setting for the pad sensitivity. */
    public static final Integer    PAD_SENSITIVITY                  = Integer.valueOf (36);
    /** Setting for the pad gain. */
    public static final Integer    PAD_GAIN                         = Integer.valueOf (37);
    /** Setting for the pad dynamics. */
    public static final Integer    PAD_DYNAMICS                     = Integer.valueOf (38);
    /** Setting for stopping automation recording on knob release. */
    public static final Integer    STOP_AUTOMATION_ON_KNOB_RELEASE  = Integer.valueOf (39);
    /** Setting for the default note view. */
    public static final Integer    DEFAULT_NOTE_VIEW                = Integer.valueOf (40);
    /** Mode debug. */
    public static final Integer    DEBUG_MODE                       = Integer.valueOf (41);
    /** Push 2 display debug window. */
    public static final Integer    DEBUG_WINDOW                     = Integer.valueOf (42);
    /** Background color of an element. */
    public static final Integer    COLOR_BACKGROUND                 = Integer.valueOf (50);
    /** Border color of an element. */
    public static final Integer    COLOR_BORDER                     = Integer.valueOf (51);
    /** Text color of an element. */
    public static final Integer    COLOR_TEXT                       = Integer.valueOf (52);
    /** Fader color of an element. */
    public static final Integer    COLOR_FADER                      = Integer.valueOf (53);
    /** VU color of an element. */
    public static final Integer    COLOR_VU                         = Integer.valueOf (54);
    /** Edit color of an element. */
    public static final Integer    COLOR_EDIT                       = Integer.valueOf (55);
    /** Record color of an element. */
    public static final Integer    COLOR_RECORD                     = Integer.valueOf (56);
    /** Solo color of an element. */
    public static final Integer    COLOR_SOLO                       = Integer.valueOf (57);
    /** Mute color of an element. */
    public static final Integer    COLOR_MUTE                       = Integer.valueOf (58);
    /** Background color darker of an element. */
    public static final Integer    COLOR_BACKGROUND_DARKER          = Integer.valueOf (59);
    /** Background color lighter of an element. */
    public static final Integer    COLOR_BACKGROUND_LIGHTER         = Integer.valueOf (60);
    /** Session view options. */
    public static final Integer    SESSION_VIEW                     = Integer.valueOf (61);
    /** Display scenes or clips. */
    public static final Integer    DISPLAY_SCENES_CLIPS             = Integer.valueOf (62);

    private static final ColorEx   DEFAULT_COLOR_BACKGROUND         = ColorEx.fromRGB (83, 83, 83);
    private static final ColorEx   DEFAULT_COLOR_BORDER             = ColorEx.BLACK;
    private static final ColorEx   DEFAULT_COLOR_TEXT               = ColorEx.WHITE;
    private static final ColorEx   DEFAULT_COLOR_FADER              = ColorEx.fromRGB (69, 44, 19);
    private static final ColorEx   DEFAULT_COLOR_VU                 = ColorEx.GREEN;
    private static final ColorEx   DEFAULT_COLOR_EDIT               = ColorEx.fromRGB (240, 127, 17);
    private static final ColorEx   DEFAULT_COLOR_RECORD             = ColorEx.RED;
    private static final ColorEx   DEFAULT_COLOR_SOLO               = ColorEx.YELLOW;
    private static final ColorEx   DEFAULT_COLOR_MUTE               = ColorEx.fromRGB (245, 129, 17);
    private static final ColorEx   DEFAULT_COLOR_BACKGROUND_DARKER  = ColorEx.fromRGB (58, 58, 58);
    private static final ColorEx   DEFAULT_COLOR_BACKGROUND_LIGHTER = ColorEx.fromRGB (118, 118, 118);

    /** Use ribbon for pitch bend. */
    public static final int        RIBBON_MODE_PITCH                = 0;
    /** Use ribbon for midi CC. */
    public static final int        RIBBON_MODE_CC                   = 1;
    /** Use ribbon for midi CC and pitch bend. */
    public static final int        RIBBON_MODE_CC_PB                = 2;
    /** Use ribbon for pitch bend and midi CC. */
    public static final int        RIBBON_MODE_PB_CC                = 3;
    /** Use ribbon as volume fader. */
    public static final int        RIBBON_MODE_FADER                = 4;

    private boolean                isSoloLongPressed                = false;
    private boolean                isMuteLongPressed                = false;
    private boolean                isMuteSoloLocked                 = false;

    private static final String    CATEGORY_RIBBON                  = "Ribbon";
    private static final String    CATEGORY_COLORS                  = "Display Colors";

    private static final String [] RIBBON_MODE_VALUES               =
    {
        "Pitch",
        "CC",
        "CC/Pitch",
        "Pitch/CC",
        "Fader"
    };

    private static final String [] SESSION_VIEW_OPTIONS             =
    {
        "Session",
        "Flipped",
        "Scenes"
    };

    private Integer                defaultNoteView                  = Views.VIEW_PLAY;
    private boolean                displayScenesClips;
    private boolean                isScenesClipView;

    /** What does the ribbon send? **/
    private int                    ribbonMode                       = RIBBON_MODE_PITCH;
    private int                    ribbonModeCCVal                  = 1;
    private boolean                stopAutomationOnKnobRelease      = false;
    private TrackState             trackState                       = TrackState.MUTE;
    private Integer                debugMode                        = Modes.MODE_TRACK;

    // Only Push 1
    private int                    velocityCurve                    = 1;
    private int                    padThreshold                     = 20;

    // Only Push 2
    private boolean                sendsAreToggled                  = false;
    private int                    displayBrightness                = 255;
    private int                    ledBrightness                    = 127;
    private int                    padSensitivity                   = 5;
    private int                    padGain                          = 5;
    private int                    padDynamics                      = 5;
    private ColorEx                colorBackground                  = DEFAULT_COLOR_BACKGROUND;
    private ColorEx                colorBorder                      = DEFAULT_COLOR_BORDER;
    private ColorEx                colorText                        = DEFAULT_COLOR_TEXT;
    private ColorEx                colorFader                       = DEFAULT_COLOR_FADER;
    private ColorEx                colorVU                          = DEFAULT_COLOR_VU;
    private ColorEx                colorEdit                        = DEFAULT_COLOR_EDIT;
    private ColorEx                colorRecord                      = DEFAULT_COLOR_RECORD;
    private ColorEx                colorSolo                        = DEFAULT_COLOR_SOLO;
    private ColorEx                colorMute                        = DEFAULT_COLOR_MUTE;
    private ColorEx                colorBackgroundDarker            = DEFAULT_COLOR_BACKGROUND_DARKER;
    private ColorEx                colorBackgroundLighter           = DEFAULT_COLOR_BACKGROUND_LIGHTER;

    private final IHost            host;
    private final boolean          isPush2;

    private IIntegerSetting        displayBrightnessSetting;
    private IIntegerSetting        ledBrightnessSetting;
    private IEnumSetting           ribbonModeSetting;
    private IIntegerSetting        ribbonModeCCSetting;
    private IIntegerSetting        padSensitivitySetting;
    private IIntegerSetting        padGainSetting;
    private IIntegerSetting        padDynamicsSetting;
    private IEnumSetting           velocityCurveSetting;
    private IEnumSetting           padThresholdSetting;
    private IEnumSetting           debugModeSetting;
    private IColorSetting          colorBackgroundSetting;
    private IColorSetting          colorBackgroundDarkerSetting;
    private IColorSetting          colorBackgroundLighterSetting;
    private IColorSetting          colorBorderSetting;
    private IColorSetting          colorTextSetting;
    private IColorSetting          colorFaderSetting;
    private IColorSetting          colorVUSetting;
    private IColorSetting          colorEditSetting;
    private IColorSetting          colorRecordSetting;
    private IColorSetting          colorSoloSetting;
    private IColorSetting          colorMuteSetting;
    private IEnumSetting           sessionViewSetting;
    private IEnumSetting           displayScenesClipsSetting;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param isPush2 Use Push 1 or Push 2 controller?
     */
    public PushConfiguration (final IHost host, final IValueChanger valueChanger, final boolean isPush2)
    {
        super (valueChanger);
        this.host = host;
        this.isPush2 = isPush2;

        Views.init (host);
    }


    /** {@inheritDoc} */
    @Override
    public void init (final ISettingsUI settingsUI)
    {
        ///////////////////////////
        // Scale

        this.activateScaleSetting (settingsUI);
        this.activateScaleBaseSetting (settingsUI);
        this.activateScaleInScaleSetting (settingsUI);
        this.activateScaleLayoutSetting (settingsUI);

        ///////////////////////////
        // Session
        if (this.host.hasClips ())
        {
            this.activateSessionView (settingsUI);
            this.activateLockFlipSessionSetting (settingsUI);
            this.activateSelectClipOnLaunchSetting (settingsUI);
            this.activateDrawRecordStripeSetting (settingsUI);
            this.activateActionForRecArmedPad (settingsUI);
        }

        ///////////////////////////
        // Transport

        this.activateBehaviourOnStopSetting (settingsUI);
        if (this.host.hasClips ())
            this.activateFlipRecordSetting (settingsUI);

        ///////////////////////////
        // Play and Sequence

        this.activateAccentActiveSetting (settingsUI);
        this.activateAccentValueSetting (settingsUI);
        this.activateQuantizeAmountSetting (settingsUI);
        this.activateDefaultNoteViewSetting (settingsUI);

        ///////////////////////////
        // Drum Sequencer
        if (this.host.hasDrumDevice ())
        {
            this.activateAutoSelectDrumSetting (settingsUI);
            this.activateTurnOffEmptyDrumPadsSetting (settingsUI);
        }

        ///////////////////////////
        // Workflow

        this.activateEnableVUMetersSetting (settingsUI);
        if (this.host.hasCrossfader ())
            this.activateDisplayCrossfaderSetting (settingsUI);
        else
            this.displayCrossfader = false;

        this.activateFootswitchSetting (settingsUI);
        this.activateStopAutomationOnKnobReleaseSetting (settingsUI);
        this.activateNewClipLengthSetting (settingsUI);

        ///////////////////////////
        // Ribbon

        this.activateRibbonSettings (settingsUI);

        ///////////////////////////
        // Pad Sensitivity

        if (this.isPush2)
            this.activatePush2PadSettings (settingsUI);
        else
            this.activatePush1PadSettings (settingsUI);

        this.activateConvertAftertouchSetting (settingsUI);

        ///////////////////////////
        // Browser

        this.activateBrowserSettings (settingsUI);

        ///////////////////////////
        // Push 2 Hardware

        this.activatePush2HardwareSettings (settingsUI);
        this.activatePush2DisplayColorsSettings (settingsUI);

        ///////////////////////////
        // Debugging

        this.activateDebugSettings (settingsUI);
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
     * Set the midi CC to use for the CC functionality of the ribbon.
     *
     * @param value The midi CC value
     */
    public void setRibbonModeCC (final int value)
    {
        this.ribbonModeCCSetting.set (value);
    }


    /**
     * Get the midi CC to use for the CC functionality of the ribbon.
     *
     * @return The midi CC value
     */
    public int getRibbonModeCCVal ()
    {
        return this.ribbonModeCCVal;
    }


    /**
     * Change the pad threshold.
     *
     * @param control The control value
     */
    public void changePadThreshold (final int control)
    {
        final int value = this.valueChanger.changeValue (control, this.padThreshold, 1, PushControlSurface.PUSH_PAD_THRESHOLDS_NAME.length);
        this.padThreshold = Math.max (0, Math.min (value, PushControlSurface.PUSH_PAD_THRESHOLDS_NAME.length - 1));
        this.padThresholdSetting.set (PushControlSurface.PUSH_PAD_THRESHOLDS_NAME[this.padThreshold]);
    }


    /**
     * Change the velocity curve.
     *
     * @param control The control value
     */
    public void changeVelocityCurve (final int control)
    {
        final int value = this.valueChanger.changeValue (control, this.velocityCurve, 1, PushControlSurface.PUSH_PAD_CURVES_NAME.length);
        this.velocityCurve = Math.max (0, Math.min (value, PushControlSurface.PUSH_PAD_CURVES_NAME.length - 1));
        this.velocityCurveSetting.set (PushControlSurface.PUSH_PAD_CURVES_NAME[this.velocityCurve]);
    }


    /**
     * Change the display brightness.
     *
     * @param control The control value
     */
    public void changeDisplayBrightness (final int control)
    {
        this.displayBrightnessSetting.set (this.valueChanger.changeValue (control, this.displayBrightness, 1, 101));
    }


    /**
     * Change the LED brightness.
     *
     * @param control The control value
     */
    public void changeLEDBrightness (final int control)
    {
        this.ledBrightnessSetting.set (this.valueChanger.changeValue (control, this.ledBrightness, 1, 101));
    }


    /**
     * Change the pad sensitivity.
     *
     * @param control The control value
     */
    public void changePadSensitivity (final int control)
    {
        this.padSensitivitySetting.set (this.valueChanger.changeValue (control, this.padSensitivity, 1, 11));
    }


    /**
     * Change the pad gain.
     *
     * @param control The control value
     */
    public void changePadGain (final int control)
    {
        this.padGainSetting.set (this.valueChanger.changeValue (control, this.padGain, 1, 11));
    }


    /**
     * Change the pad dynamics.
     *
     * @param control The control value
     */
    public void changePadDynamics (final int control)
    {
        this.padDynamicsSetting.set (this.valueChanger.changeValue (control, this.padDynamics, 1, 11));
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
        this.displayBrightness = displayBrightness;
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
    public void setLedBrightness (final int ledBrightness)
    {
        this.ledBrightness = ledBrightness;
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
     * Are the sends toggled (5-8)?
     *
     * @return True if toggled
     */
    public boolean isSendsAreToggled ()
    {
        return this.sendsAreToggled;
    }


    /**
     * Set that the sends are toggled (5-8).
     *
     * @param sendsAreToggled True if toggled
     */
    public void setSendsAreToggled (final boolean sendsAreToggled)
    {
        this.sendsAreToggled = sendsAreToggled;
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
        this.padSensitivity = padSensitivity;
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
        this.padGain = padGain;
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
        this.padDynamics = padDynamics;
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
    public Integer getCurrentMixMode ()
    {
        return Modes.isTrackMode (this.debugMode) ? this.debugMode : null;
    }


    /**
     * Get the default note view.
     *
     * @return The default note view
     */
    public Integer getDefaultNoteView ()
    {
        return this.defaultNoteView;
    }


    /**
     * Get the background color of an element.
     *
     * @return The background color of an element.
     */
    public ColorEx getColorBackground ()
    {
        return this.colorBackground;
    }


    /**
     * Get the background darker color of an element.
     *
     * @return The background color of an element.
     */
    public ColorEx getColorBackgroundDarker ()
    {
        return this.colorBackgroundDarker;
    }


    /**
     * Get the background lighter color of an element.
     *
     * @return The background color of an element.
     */
    public ColorEx getColorBackgroundLighter ()
    {
        return this.colorBackgroundLighter;
    }


    /**
     * Get the border color of an element.
     *
     * @return The border color of an element.
     */
    public ColorEx getColorBorder ()
    {
        return this.colorBorder;
    }


    /**
     * Get the text color of an element.
     *
     * @return The text color of an element.
     */
    public ColorEx getColorText ()
    {
        return this.colorText;
    }


    /**
     * Get the edit color of an element.
     *
     * @return The edit color of an element.
     */
    public ColorEx getColorEdit ()
    {
        return this.colorEdit;
    }


    /**
     * Get the fader color of an element.
     *
     * @return The fader color of an element.
     */
    public ColorEx getColorFader ()
    {
        return this.colorFader;
    }


    /**
     * Get the VU color of an element.
     *
     * @return The VU color of an element.
     */
    public ColorEx getColorVu ()
    {
        return this.colorVU;
    }


    /**
     * Get the record color of an element.
     *
     * @return The record color of an element.
     */
    public ColorEx getColorRecord ()
    {
        return this.colorRecord;
    }


    /**
     * Get the solo color of an element.
     *
     * @return The solo color of an element.
     */
    public ColorEx getColorSolo ()
    {
        return this.colorSolo;
    }


    /**
     * Get the mute color of an element.
     *
     * @return The border mute of an element.
     */
    public ColorEx getColorMute ()
    {
        return this.colorMute;
    }


    /**
     * Get the selected display mode for debugging.
     *
     * @return The ID of a mode
     */
    public Integer getDebugMode ()
    {
        return this.debugMode;
    }


    /**
     * Set the selected display mode for debugging.
     *
     * @param debugMode The ID of a mode
     */
    public void setDebugMode (final Integer debugMode)
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
        this.velocityCurveSetting = settingsUI.getEnumSetting ("Velocity Curve", CATEGORY_PADS, PushControlSurface.PUSH_PAD_CURVES_NAME, PushControlSurface.PUSH_PAD_CURVES_NAME[1]);
        this.velocityCurveSetting.addValueObserver (value -> {
            this.velocityCurve = lookupIndex (PushControlSurface.PUSH_PAD_CURVES_NAME, value);
            this.notifyObservers (VELOCITY_CURVE);
        });

        this.padThresholdSetting = settingsUI.getEnumSetting ("Pad Threshold", CATEGORY_PADS, PushControlSurface.PUSH_PAD_THRESHOLDS_NAME, PushControlSurface.PUSH_PAD_THRESHOLDS_NAME[20]);
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
     * Activate the default note view setting.
     *
     * @param settingsUI The settings
     */
    private void activateDefaultNoteViewSetting (final ISettingsUI settingsUI)
    {
        final String [] noteViewNames = Views.getNoteViewNames ();
        final IEnumSetting defaultNoteViewSetting = settingsUI.getEnumSetting ("Default note view", CATEGORY_PLAY_AND_SEQUENCE, noteViewNames, noteViewNames[0]);
        defaultNoteViewSetting.addValueObserver (value -> {
            for (int i = 0; i < noteViewNames.length; i++)
            {
                if (noteViewNames[i].equals (value))
                    this.defaultNoteView = Views.getNoteView (i);
            }
            this.notifyObservers (DEFAULT_NOTE_VIEW);
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

        settingsUI.getSignalSetting ("Reset colors to default", CATEGORY_COLORS, "Reset").addValueObserver (value -> {
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
            this.colorBackground = new ColorEx (color[0], color[1], color[2]);
            this.notifyObservers (COLOR_BACKGROUND);
        });

        this.colorBackgroundDarkerSetting = settingsUI.getColorSetting ("Background Darker", CATEGORY_COLORS, DEFAULT_COLOR_BACKGROUND_DARKER);
        this.colorBackgroundDarkerSetting.addValueObserver (color -> {
            this.colorBackgroundDarker = new ColorEx (color[0], color[1], color[2]);
            this.notifyObservers (COLOR_BACKGROUND_DARKER);
        });

        this.colorBackgroundLighterSetting = settingsUI.getColorSetting ("Background Selected", CATEGORY_COLORS, DEFAULT_COLOR_BACKGROUND_LIGHTER);
        this.colorBackgroundLighterSetting.addValueObserver (color -> {
            this.colorBackgroundLighter = new ColorEx (color[0], color[1], color[2]);
            this.notifyObservers (COLOR_BACKGROUND_LIGHTER);
        });

        this.colorBorderSetting = settingsUI.getColorSetting ("Border", CATEGORY_COLORS, DEFAULT_COLOR_BORDER);
        this.colorBorderSetting.addValueObserver (color -> {
            this.colorBorder = new ColorEx (color[0], color[1], color[2]);
            this.notifyObservers (COLOR_BORDER);
        });

        this.colorTextSetting = settingsUI.getColorSetting ("Text", CATEGORY_COLORS, DEFAULT_COLOR_TEXT);
        this.colorTextSetting.addValueObserver (color -> {
            this.colorText = new ColorEx (color[0], color[1], color[2]);
            this.notifyObservers (COLOR_TEXT);
        });

        this.colorFaderSetting = settingsUI.getColorSetting ("Fader", CATEGORY_COLORS, DEFAULT_COLOR_FADER);
        this.colorFaderSetting.addValueObserver (color -> {
            this.colorFader = new ColorEx (color[0], color[1], color[2]);
            this.notifyObservers (COLOR_FADER);
        });

        this.colorVUSetting = settingsUI.getColorSetting ("VU", CATEGORY_COLORS, DEFAULT_COLOR_VU);
        this.colorVUSetting.addValueObserver (color -> {
            this.colorVU = new ColorEx (color[0], color[1], color[2]);
            this.notifyObservers (COLOR_VU);
        });

        this.colorEditSetting = settingsUI.getColorSetting ("Edit", CATEGORY_COLORS, DEFAULT_COLOR_EDIT);
        this.colorEditSetting.addValueObserver (color -> {
            this.colorEdit = new ColorEx (color[0], color[1], color[2]);
            this.notifyObservers (COLOR_EDIT);
        });

        this.colorRecordSetting = settingsUI.getColorSetting ("Record", CATEGORY_COLORS, DEFAULT_COLOR_RECORD);
        this.colorRecordSetting.addValueObserver (color -> {
            this.colorRecord = new ColorEx (color[0], color[1], color[2]);
            this.notifyObservers (COLOR_RECORD);
        });

        this.colorSoloSetting = settingsUI.getColorSetting ("Solo", CATEGORY_COLORS, DEFAULT_COLOR_SOLO);
        this.colorSoloSetting.addValueObserver (color -> {
            this.colorSolo = new ColorEx (color[0], color[1], color[2]);
            this.notifyObservers (COLOR_SOLO);
        });

        this.colorMuteSetting = settingsUI.getColorSetting ("Mute", CATEGORY_COLORS, DEFAULT_COLOR_MUTE);
        this.colorMuteSetting.addValueObserver (color -> {
            this.colorMute = new ColorEx (color[0], color[1], color[2]);
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
        final Set<Integer> allModes = Modes.ALL_MODES;
        final String [] modes = new String [allModes.size ()];
        int i = 0;
        for (final Integer mode: allModes)
        {
            modes[i] = mode.toString ();
            i++;
        }

        this.debugModeSetting = settingsUI.getEnumSetting ("Display Mode", CATEGORY_DEBUG, modes, Modes.MODE_TRACK.toString ());
        this.debugModeSetting.addValueObserver (value -> {
            this.debugMode = Integer.valueOf (value);
            this.notifyObservers (DEBUG_MODE);
        });

        if (!this.isPush2)
            return;

        settingsUI.getSignalSetting (" ", CATEGORY_DEBUG, "Display window").addValueObserver (value -> this.notifyObservers (DEBUG_WINDOW));
    }
}
