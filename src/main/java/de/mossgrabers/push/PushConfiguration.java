// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push;

import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.controller.ValueChanger;
import de.mossgrabers.push.controller.PushControlSurface;

import com.bitwig.extension.controller.api.Preferences;
import com.bitwig.extension.controller.api.SettableEnumValue;
import com.bitwig.extension.controller.api.SettableRangedValue;


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
    public static final Integer    RIBBON_MODE                      = Integer.valueOf (20);
    /** Setting for the ribbon mode midi CC. */
    public static final Integer    RIBBON_MODE_CC_VAL               = Integer.valueOf (21);
    /** Setting for the velocity curve. */
    public static final Integer    VELOCITY_CURVE                   = Integer.valueOf (22);
    /** Setting for the pad threshold. */
    public static final Integer    PAD_THRESHOLD                    = Integer.valueOf (23);
    /** Setting for the footswitch functionality. */
    public static final Integer    FOOTSWITCH_2                     = Integer.valueOf (24);
    /** Setting for the display send port. */
    public static final Integer    SEND_PORT                        = Integer.valueOf (25);
    /** Setting for the display brightness. */
    public static final Integer    DISPLAY_BRIGHTNESS               = Integer.valueOf (26);
    /** Setting for the pad LED brightness. */
    public static final Integer    LED_BRIGHTNESS                   = Integer.valueOf (27);
    /** Setting for the pad sensitivity. */
    public static final Integer    PAD_SENSITIVITY                  = Integer.valueOf (28);
    /** Setting for the pad gain. */
    public static final Integer    PAD_GAIN                         = Integer.valueOf (29);
    /** Setting for the pad dynamics. */
    public static final Integer    PAD_DYNAMICS                     = Integer.valueOf (30);
    /** Setting for stopping automation recording on knob release. */
    public static final Integer    STOP_AUTOMATION_ON_KNOB_RELEASE  = Integer.valueOf (31);
    /** Setting for displaying browser column 1. */
    public static final Integer    BROWSER_DISPLAY_FILTER1          = Integer.valueOf (32);
    /** Setting for displaying browser column 2. */
    public static final Integer    BROWSER_DISPLAY_FILTER2          = Integer.valueOf (33);
    /** Setting for displaying browser column 3. */
    public static final Integer    BROWSER_DISPLAY_FILTER3          = Integer.valueOf (34);
    /** Setting for displaying browser column 4. */
    public static final Integer    BROWSER_DISPLAY_FILTER4          = Integer.valueOf (35);
    /** Setting for displaying browser column 5. */
    public static final Integer    BROWSER_DISPLAY_FILTER5          = Integer.valueOf (36);
    /** Setting for displaying browser column 6. */
    public static final Integer    BROWSER_DISPLAY_FILTER6          = Integer.valueOf (37);
    /** Setting for displaying browser column 7. */
    public static final Integer    BROWSER_DISPLAY_FILTER7          = Integer.valueOf (38);
    /** Setting for displaying browser column 8. */
    public static final Integer    BROWSER_DISPLAY_FILTER8          = Integer.valueOf (39);

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

    /** Use footswitch 2 for toggling play. */
    public static final int        FOOTSWITCH_2_TOGGLE_PLAY         = 0;
    /** Use footswitch 2 for toggling record. */
    public static final int        FOOTSWITCH_2_TOGGLE_RECORD       = 1;
    /** Use footswitch 2 for stopping all clips. */
    public static final int        FOOTSWITCH_2_STOP_ALL_CLIPS      = 2;
    /** Use footswitch 2 for toggling clip overdub. */
    public static final int        FOOTSWITCH_2_TOGGLE_CLIP_OVERDUB = 3;
    /** Use footswitch 2 for undo. */
    public static final int        FOOTSWITCH_2_UNDO                = 4;
    /** Use footswitch 2 for tapping tempo. */
    public static final int        FOOTSWITCH_2_TAP_TEMPO           = 5;
    /** Use footswitch 2 as the new button. */
    public static final int        FOOTSWITCH_2_NEW_BUTTON          = 6;
    /** Use footswitch 2 as clip based looper. */
    public static final int        FOOTSWITCH_2_CLIP_BASED_LOOPER   = 7;

    private boolean                isSoloLongPressed                = false;
    private boolean                isMuteSoloLocked                 = false;

    private static final String    SECTION_HARDWARE_SETUP           = "Hardware Setup";
    private static final String    SECTION_PADS                     = "Pads";
    private static final String    SECTION_WORKFLOW                 = "Workflow";
    private static final String    SECTION_RIBBON                   = "Ribbon";

    /** What does the ribbon send? **/
    private int                    ribbonMode                       = RIBBON_MODE_PITCH;
    private int                    ribbonModeCCVal                  = 1;
    private int                    footswitch2                      = FOOTSWITCH_2_NEW_BUTTON;
    private int                    sendPort                         = 7000;
    private boolean                stopAutomationOnKnobRelease      = false;
    private boolean []             browserDisplayFilter             =
    {
            true,
            true,
            true,
            true,
            true,
            true,
            true,
            true
    };
    private TrackState             trackState                       = TrackState.MUTE;

    // Push 1
    private int                    velocityCurve                    = 1;
    private int                    padThreshold                     = 20;

    // Push 2
    private boolean                sendsAreToggled                  = false;
    private int                    displayBrightness                = 255;
    private int                    ledBrightness                    = 127;
    private int                    padSensitivity                   = 5;
    private int                    padGain                          = 5;
    private int                    padDynamics                      = 5;

    private boolean                isPush2;

    private SettableRangedValue    displayBrightnessSetting;
    private SettableRangedValue    ledBrightnessSetting;
    private SettableEnumValue      ribbonModeSetting;
    private SettableRangedValue    ribbonModeCCSetting;
    private SettableRangedValue    padSensitivitySetting;
    private SettableRangedValue    padGainSetting;
    private SettableRangedValue    padDynamicsSetting;
    private SettableEnumValue      velocityCurveSetting;
    private SettableEnumValue      padThresholdSetting;

    private static final String [] BROWSER_FILTER_COLUMN_NAMES      =
    {
            "Collection",
            "Location",
            "File Type",
            "Category",
            "Tags",
            "Creator",
            "Device Type",
            "Device"
    };

    private static final String [] RIBBON_MODE_VALUES               =
    {
            "Pitch",
            "CC",
            "CC/Pitch",
            "Pitch/CC",
            "Fader"
    };

    private static final String [] FOOTSWITCH_VALUES                =
    {
            "Toggle Play",
            "Toggle Record",
            "Stop All Clips",
            "Toggle Clip Overdub",
            "Undo",
            "Tap Tempo",
            "New Button",
            "Clip Based Looper"
    };

    private static final String [] COLUMN_VALUES                    =
    {
            "Hide",
            "Show"
    };


    /**
     * Constructor.
     *
     * @param valueChanger The value changer
     * @param isPush2 Use Push 1 or Push 2 controller?
     */
    public PushConfiguration (final ValueChanger valueChanger, final boolean isPush2)
    {
        super (valueChanger);
        this.isPush2 = isPush2;
    }


    /** {@inheritDoc} */
    @Override
    public void init (final Preferences preferences)
    {
        ///////////////////////////
        // Scale

        this.activateScaleSetting (preferences);
        this.activateScaleBaseSetting (preferences);
        this.activateScaleInScaleSetting (preferences);
        this.activateScaleLayoutSetting (preferences);

        ///////////////////////////
        // Session

        this.activateFlipSessionSetting (preferences);
        this.activateLockFlipSessionSetting (preferences);
        this.activateSelectClipOnLaunchSetting (preferences);
        this.activateDrawRecordStripeSetting (preferences);
        this.activateActionForRecArmedPad (preferences);

        ///////////////////////////
        // Transport

        this.activateBehaviourOnStopSetting (preferences);
        this.activateFlipRecordSetting (preferences);

        ///////////////////////////
        // Play and Sequence

        this.activateAccentActiveSetting (preferences);
        this.activateAccentValueSetting (preferences);
        this.activateQuantizeAmountSetting (preferences);

        ///////////////////////////
        // Drum Sequencer

        this.activateAutoSelectDrumSetting (preferences);
        this.activateTurnOffEmptyDrumPadsSetting (preferences);

        ///////////////////////////
        // Workflow

        this.activateEnableVUMetersSetting (preferences);
        if (!this.isPush2)
            this.activateDisplayCrossfaderSetting (preferences);
        this.activateFootswitchSetting (preferences);
        this.activateStopAutomationOnKnobReleaseSetting (preferences);
        this.activateNewClipLengthSetting (preferences);

        ///////////////////////////
        // Ribbon

        this.activateRibbonSettings (preferences);

        ///////////////////////////
        // Pad Sensitivity

        if (this.isPush2)
            this.activatePush2PadSettings (preferences);
        else
            this.activatePush1PadSettings (preferences);

        this.activateConvertAftertouchSetting (preferences);

        ///////////////////////////
        // Browser

        this.activateBrowserSettings (preferences);

        ///////////////////////////
        // Push 2 Hardware

        this.activatePush2HardwareSettings (preferences);
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
        this.ribbonModeCCSetting.setRaw (value);
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
        final int value = this.valueChanger.changeIntValue (control, this.padThreshold, 1, PushControlSurface.PUSH_PAD_THRESHOLDS_NAME.length);
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
        final int value = this.valueChanger.changeIntValue (control, this.velocityCurve, 1, PushControlSurface.PUSH_PAD_CURVES_NAME.length);
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
        this.displayBrightnessSetting.setRaw (this.valueChanger.changeIntValue (control, this.displayBrightness, 1, 101));
    }


    /**
     * Change the LED brightness.
     *
     * @param control The control value
     */
    public void changeLEDBrightness (final int control)
    {
        this.ledBrightnessSetting.setRaw (this.valueChanger.changeIntValue (control, this.ledBrightness, 1, 101));
    }


    /**
     * Change the pad sensitivity.
     *
     * @param control The control value
     */
    public void changePadSensitivity (final int control)
    {
        this.padSensitivitySetting.setRaw (this.valueChanger.changeIntValue (control, this.padSensitivity, 1, 11));
    }


    /**
     * Change the pad gain.
     *
     * @param control The control value
     */
    public void changePadGain (final int control)
    {
        this.padGainSetting.setRaw (this.valueChanger.changeIntValue (control, this.padGain, 1, 11));
    }


    /**
     * Change the pad dynamics.
     *
     * @param control The control value
     */
    public void changePadDynamics (final int control)
    {
        this.padDynamicsSetting.setRaw (this.valueChanger.changeIntValue (control, this.padDynamics, 1, 11));
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
        return this.isSoloLongPressed;
    }


    /**
     * Set if mute is long pressed.
     *
     * @param isMuteLongPressed True if mute is long pressed
     */
    public void setIsMuteLongPressed (final boolean isMuteLongPressed)
    {
        this.isSoloLongPressed = isMuteLongPressed;
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
     * Get the send port
     *
     * @return The send port
     */
    public int getSendPort ()
    {
        return this.sendPort;
    }


    /**
     * Set the send port
     *
     * @param sendPort The send port
     */
    public void setSendPort (final int sendPort)
    {
        this.sendPort = sendPort;
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
     * Get the browser display filter.
     *
     * @return The array with states if a filter column should be displayed
     */
    public boolean [] getBrowserDisplayFilter ()
    {
        return this.browserDisplayFilter;
    }


    /**
     * Get the functionality of the footswitch 2.
     *
     * @return The functionality of the footswitch 2.
     */
    public int getFootswitch2 ()
    {
        return this.footswitch2;
    }


    /**
     * Activate the Push 2 hardware settings.
     *
     * @param prefs The preferences
     */
    private void activatePush2HardwareSettings (final Preferences prefs)
    {
        if (!this.isPush2)
            return;

        final SettableRangedValue sendPortSetting = prefs.getNumberSetting ("Display Port", SECTION_HARDWARE_SETUP, 1, 65535, 1, "", 7000);
        sendPortSetting.addValueObserver (65535, value -> {
            this.sendPort = value + 1;
            this.notifyObservers (SEND_PORT);
        });

        this.displayBrightnessSetting = prefs.getNumberSetting ("Display Brightness", SECTION_HARDWARE_SETUP, 0, 100, 1, "%", 100);
        this.displayBrightnessSetting.addValueObserver (101, value -> {
            this.displayBrightness = value;
            this.notifyObservers (DISPLAY_BRIGHTNESS);
        });

        this.ledBrightnessSetting = prefs.getNumberSetting ("LED Brightness", SECTION_HARDWARE_SETUP, 0, 100, 1, "%", 100);
        this.ledBrightnessSetting.addValueObserver (101, value -> {
            this.ledBrightness = value;
            this.notifyObservers (LED_BRIGHTNESS);
        });
    }


    /**
     * Activate the ribbon settings.
     *
     * @param prefs The preferences
     */
    private void activateRibbonSettings (final Preferences prefs)
    {
        this.ribbonModeSetting = prefs.getEnumSetting ("Mode", SECTION_RIBBON, RIBBON_MODE_VALUES, RIBBON_MODE_VALUES[0]);
        this.ribbonModeSetting.addValueObserver (value -> {
            for (int i = 0; i < RIBBON_MODE_VALUES.length; i++)
            {
                if (RIBBON_MODE_VALUES[i].equals (value))
                    this.ribbonMode = i;
            }
            this.notifyObservers (RIBBON_MODE);
        });

        this.ribbonModeCCSetting = prefs.getNumberSetting ("CC", SECTION_RIBBON, 0, 127, 1, "", 1);
        this.ribbonModeCCSetting.addValueObserver (128, value -> {
            this.ribbonModeCCVal = value;
            this.notifyObservers (RIBBON_MODE_CC_VAL);
        });
    }


    /**
     * Activate the browser settings.
     *
     * @param prefs The preferences
     */
    private void activateBrowserSettings (final Preferences prefs)
    {
        for (int i = 0; i < BROWSER_FILTER_COLUMN_NAMES.length; i++)
        {
            final SettableEnumValue browserDisplayFilterSetting = prefs.getEnumSetting (BROWSER_FILTER_COLUMN_NAMES[i], "Browser", COLUMN_VALUES, COLUMN_VALUES[1]);
            final int index = i;
            browserDisplayFilterSetting.addValueObserver (value -> {
                this.browserDisplayFilter[index] = COLUMN_VALUES[1].equals (value);
                this.notifyObservers (Integer.valueOf (BROWSER_DISPLAY_FILTER1.intValue () + index));
            });
        }
    }


    /**
     * Activate the footwswitch setting.
     *
     * @param prefs The preferences
     */
    private void activateFootswitchSetting (final Preferences prefs)
    {
        final SettableEnumValue footswitch2Setting = prefs.getEnumSetting ("Footswitch 2", SECTION_WORKFLOW, FOOTSWITCH_VALUES, FOOTSWITCH_VALUES[6]);
        footswitch2Setting.addValueObserver (value -> {
            for (int i = 0; i < FOOTSWITCH_VALUES.length; i++)
            {
                if (FOOTSWITCH_VALUES[i].equals (value))
                    this.footswitch2 = i;
            }
            this.notifyObservers (FOOTSWITCH_2);
        });
    }


    /**
     * Activate the stop automation on knob release setting.
     *
     * @param prefs The preferences
     */
    private void activateStopAutomationOnKnobReleaseSetting (final Preferences prefs)
    {
        final SettableEnumValue stopAutomationOnKnobReleaseSetting = prefs.getEnumSetting ("Stop automation recording on knob release", SECTION_WORKFLOW, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        stopAutomationOnKnobReleaseSetting.addValueObserver (value -> {
            this.stopAutomationOnKnobRelease = "On".equals (value);
            this.notifyObservers (STOP_AUTOMATION_ON_KNOB_RELEASE);
        });
    }


    /**
     * Activate the Push 1 pad settings.
     *
     * @param prefs The preferences
     */
    private void activatePush1PadSettings (final Preferences prefs)
    {
        this.velocityCurveSetting = prefs.getEnumSetting ("Velocity Curve", SECTION_PADS, PushControlSurface.PUSH_PAD_CURVES_NAME, PushControlSurface.PUSH_PAD_CURVES_NAME[1]);
        this.velocityCurveSetting.addValueObserver (value -> {
            for (int i = 0; i < PushControlSurface.PUSH_PAD_CURVES_NAME.length; i++)
            {
                if (PushControlSurface.PUSH_PAD_CURVES_NAME[i].equals (value))
                {
                    this.velocityCurve = i;
                    break;
                }
            }
            this.notifyObservers (VELOCITY_CURVE);
        });

        this.padThresholdSetting = prefs.getEnumSetting ("Pad Threshold", SECTION_PADS, PushControlSurface.PUSH_PAD_THRESHOLDS_NAME, PushControlSurface.PUSH_PAD_THRESHOLDS_NAME[20]);
        this.padThresholdSetting.addValueObserver (value -> {
            for (int i = 0; i < PushControlSurface.PUSH_PAD_THRESHOLDS_NAME.length; i++)
            {
                if (PushControlSurface.PUSH_PAD_THRESHOLDS_NAME[i].equals (value))
                {
                    this.padThreshold = i;
                    break;
                }
            }
            this.notifyObservers (PAD_THRESHOLD);
        });
    }


    /**
     * Activate the Push 2 pad settings.
     *
     * @param prefs The preferences
     */
    private void activatePush2PadSettings (final Preferences prefs)
    {
        this.padSensitivitySetting = prefs.getNumberSetting ("Sensitivity", SECTION_PADS, 0, 10, 1, "", 5);
        this.padSensitivitySetting.addValueObserver (11, value -> {
            this.padSensitivity = value;
            this.notifyObservers (PAD_SENSITIVITY);
        });

        this.padGainSetting = prefs.getNumberSetting ("Gain", SECTION_PADS, 0, 10, 1, "", 5);
        this.padGainSetting.addValueObserver (11, value -> {
            this.padGain = value;
            this.notifyObservers (PAD_GAIN);
        });

        this.padDynamicsSetting = prefs.getNumberSetting ("Dynamics", SECTION_PADS, 0, 10, 1, "", 5);
        this.padDynamicsSetting.addValueObserver (11, value -> {
            this.padDynamics = value;
            this.notifyObservers (PAD_DYNAMICS);
        });
    }
}
