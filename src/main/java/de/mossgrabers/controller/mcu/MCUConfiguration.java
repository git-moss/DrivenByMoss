// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mcu;

import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.IEnumSetting;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;

import java.util.Arrays;


/**
 * The configuration settings for MCU.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MCUConfiguration extends AbstractConfiguration
{
    /** Zoom state. */
    public static final Integer    ZOOM_STATE                              = Integer.valueOf (50);
    /** Display mode tempo or ticks. */
    public static final Integer    DISPLAY_MODE_TICKS_OR_TEMPO             = Integer.valueOf (51);
    /** Has a display. */
    public static final Integer    HAS_DISPLAY1                            = Integer.valueOf (52);
    /** Has a second display. */
    public static final Integer    HAS_DISPLAY2                            = Integer.valueOf (53);
    /** Has a segment display. */
    public static final Integer    HAS_SEGMENT_DISPLAY                     = Integer.valueOf (54);
    /** Has an assignment display. */
    public static final Integer    HAS_ASSIGNMENT_DISPLAY                  = Integer.valueOf (55);
    /** Has motor faders. */
    public static final Integer    HAS_MOTOR_FADERS                        = Integer.valueOf (56);
    /** Display track names in 1st display. */
    public static final Integer    DISPLAY_TRACK_NAMES                     = Integer.valueOf (57);
    /** Replace the vertical zoom withmode change. */
    public static final Integer    USE_VERT_ZOOM_FOR_MODES                 = Integer.valueOf (58);
    /** Use the faders like the editing knobs. */
    public static final Integer    USE_FADERS_AS_KNOBS                     = Integer.valueOf (59);
    /** Select the channel when touching it's fader. */
    private static final Integer   TOUCH_CHANNEL                           = Integer.valueOf (60);

    /** Use a Function button to switch to previous mode. */
    public static final int        FOOTSWITCH_2_PREV_MODE                  = 15;
    /** Use a Function button to switch to next mode. */
    public static final int        FOOTSWITCH_2_NEXT_MODE                  = 16;
    /** Use a Function button to switch to Marker mode. */
    public static final int        FOOTSWITCH_2_SHOW_MARKER_MODE           = 17;
    /** Toggle use faders like editing knobs. */
    public static final int        FOOTSWITCH_2_USE_FADERS_LIKE_EDIT_KNOBS = 18;

    private static final String    DEVICE_SELECT                           = "<Select a profile>";
    private static final String    DEVICE_BEHRINGER_X_TOUCH_ONE            = "Behringer X-Touch One";
    private static final String    DEVICE_ICON_PLATFORM_M                  = "icon Platform M / M+";
    private static final String    DEVICE_ICON_QCON_PRO_X                  = "icon QConPro X";
    private static final String    DEVICE_MACKIE_MCU_PRO                   = "Mackie MCU Pro";
    private static final String    DEVICE_ZOOM_R16                         = "Zoom R16";

    private static final String [] DEVICE_OPTIONS                          =
    {
        DEVICE_SELECT,
        DEVICE_BEHRINGER_X_TOUCH_ONE,
        DEVICE_ICON_PLATFORM_M,
        DEVICE_ICON_QCON_PRO_X,
        DEVICE_MACKIE_MCU_PRO,
        DEVICE_ZOOM_R16
    };

    private static final String [] ASSIGNABLE_VALUES                       =
    {
        "Toggle Play",
        "Toggle Record",
        "Stop All Clips",
        "Toggle Clip Overdub",
        "Undo",
        "Tap Tempo",
        "New Button",
        "Clip Based Looper",
        "Panel layout arrange",
        "Panel layout mix",
        "Panel layout edit",
        "Add instrument track",
        "Add audio track",
        "Add effect track",
        "Quantize",
        "Previous mode",
        "Next mode",
        "Marker mode",
        "Toggle use faders like editing knobs"
    };

    private static final String [] ASSIGNABLE_BUTTON_NAMES                 =
    {
        "Footswitch 1",
        "Footswitch 2",
        "F1",
        "F2",
        "F3",
        "F4",
        "F5"
    };

    private static final String [] TEMPO_OR_TICKS_OPTIONS                  =
    {
        "Ticks",
        "Tempo"
    };

    private IEnumSetting           zoomStateSetting;
    private IEnumSetting           tempoOrTicksSetting;
    private IEnumSetting           hasDisplay1Setting;
    private IEnumSetting           hasDisplay2Setting;
    private IEnumSetting           hasSegmentDisplaySetting;
    private IEnumSetting           hasAssignmentDisplaySetting;
    private IEnumSetting           hasMotorFadersSetting;
    private IEnumSetting           displayTrackNamesSetting;
    private IEnumSetting           useVertZoomForModesSetting;
    private IEnumSetting           useFadersAsKnobsSetting;

    private boolean                zoomState;
    private boolean                displayTicks;
    private boolean                hasDisplay1;
    private boolean                hasDisplay2;
    private boolean                hasSegmentDisplay;
    private boolean                hasAssignmentDisplay;
    private boolean                hasMotorFaders;
    private boolean                displayTrackNames;
    private boolean                useVertZoomForModes;
    private boolean                useFadersAsKnobs;
    private boolean                touchChannel;
    private int []                 assignableFunctions                     = new int [7];


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     */
    public MCUConfiguration (final IHost host, final IValueChanger valueChanger)
    {
        super (host, valueChanger);
        Arrays.fill (this.assignableFunctions, 0);
    }


    /** {@inheritDoc} */
    @Override
    public void init (final ISettingsUI globalSettings, final ISettingsUI documentSettings)
    {
        ///////////////////////////
        // Hardware

        this.activateHardwareSettings (globalSettings);
        this.activateEnableVUMetersSetting (globalSettings, CATEGORY_HARDWARE_SETUP);

        ///////////////////////////
        // Assignable buttons

        this.activateAssignableSettings (globalSettings);

        ///////////////////////////
        // Transport

        this.activateBehaviourOnStopSetting (globalSettings);
        this.activateFlipRecordSetting (globalSettings);

        ///////////////////////////
        // Play and Sequence

        this.activateQuantizeAmountSetting (globalSettings);

        ///////////////////////////
        // Workflow

        this.activateDisplayCrossfaderSetting (globalSettings);
        this.activateNewClipLengthSetting (globalSettings);
        this.activateZoomStateSetting (globalSettings);
        this.activateDisplayTempoOrTicksSetting (globalSettings);
        this.activateChannelTouchSetting (globalSettings);

        ///////////////////////////
        // Browser

        this.activateBrowserSettings (globalSettings);
    }


    private void activateHardwareSettings (final ISettingsUI settingsUI)
    {
        final IEnumSetting profileSetting = settingsUI.getEnumSetting ("Profile", CATEGORY_HARDWARE_SETUP, DEVICE_OPTIONS, DEVICE_OPTIONS[0]);
        profileSetting.addValueObserver (value -> {
            switch (value)
            {
                case DEVICE_BEHRINGER_X_TOUCH_ONE:
                    this.hasDisplay1Setting.set (ON_OFF_OPTIONS[1]);
                    this.hasDisplay2Setting.set (ON_OFF_OPTIONS[0]);
                    this.hasSegmentDisplaySetting.set (ON_OFF_OPTIONS[1]);
                    this.hasAssignmentDisplaySetting.set (ON_OFF_OPTIONS[1]);
                    this.hasMotorFadersSetting.set (ON_OFF_OPTIONS[1]);
                    this.displayTrackNamesSetting.set (ON_OFF_OPTIONS[1]);
                    this.useVertZoomForModesSetting.set (ON_OFF_OPTIONS[0]);
                    this.useFadersAsKnobsSetting.set (ON_OFF_OPTIONS[0]);
                    this.setVUMetersEnabled (true);
                    break;

                case DEVICE_ICON_PLATFORM_M:
                    this.hasDisplay1Setting.set (ON_OFF_OPTIONS[0]);
                    this.hasDisplay2Setting.set (ON_OFF_OPTIONS[0]);
                    this.hasSegmentDisplaySetting.set (ON_OFF_OPTIONS[0]);
                    this.hasAssignmentDisplaySetting.set (ON_OFF_OPTIONS[0]);
                    this.hasMotorFadersSetting.set (ON_OFF_OPTIONS[1]);
                    this.displayTrackNamesSetting.set (ON_OFF_OPTIONS[0]);
                    this.useVertZoomForModesSetting.set (ON_OFF_OPTIONS[1]);
                    this.useFadersAsKnobsSetting.set (ON_OFF_OPTIONS[0]);
                    this.setVUMetersEnabled (false);
                    break;

                case DEVICE_ICON_QCON_PRO_X:
                    this.hasDisplay1Setting.set (ON_OFF_OPTIONS[1]);
                    this.hasDisplay2Setting.set (ON_OFF_OPTIONS[1]);
                    this.hasSegmentDisplaySetting.set (ON_OFF_OPTIONS[1]);
                    this.hasAssignmentDisplaySetting.set (ON_OFF_OPTIONS[0]);
                    this.hasMotorFadersSetting.set (ON_OFF_OPTIONS[1]);
                    this.displayTrackNamesSetting.set (ON_OFF_OPTIONS[0]);
                    this.useVertZoomForModesSetting.set (ON_OFF_OPTIONS[0]);
                    this.useFadersAsKnobsSetting.set (ON_OFF_OPTIONS[0]);
                    this.setVUMetersEnabled (true);
                    break;

                case DEVICE_MACKIE_MCU_PRO:
                    this.hasDisplay1Setting.set (ON_OFF_OPTIONS[1]);
                    this.hasDisplay2Setting.set (ON_OFF_OPTIONS[0]);
                    this.hasSegmentDisplaySetting.set (ON_OFF_OPTIONS[1]);
                    this.hasAssignmentDisplaySetting.set (ON_OFF_OPTIONS[1]);
                    this.hasMotorFadersSetting.set (ON_OFF_OPTIONS[1]);
                    this.displayTrackNamesSetting.set (ON_OFF_OPTIONS[1]);
                    this.useVertZoomForModesSetting.set (ON_OFF_OPTIONS[0]);
                    this.useFadersAsKnobsSetting.set (ON_OFF_OPTIONS[0]);
                    this.setVUMetersEnabled (true);
                    break;

                case DEVICE_ZOOM_R16:
                    this.hasDisplay1Setting.set (ON_OFF_OPTIONS[0]);
                    this.hasDisplay2Setting.set (ON_OFF_OPTIONS[0]);
                    this.hasSegmentDisplaySetting.set (ON_OFF_OPTIONS[0]);
                    this.hasAssignmentDisplaySetting.set (ON_OFF_OPTIONS[0]);
                    this.hasMotorFadersSetting.set (ON_OFF_OPTIONS[0]);
                    this.displayTrackNamesSetting.set (ON_OFF_OPTIONS[0]);
                    this.useVertZoomForModesSetting.set (ON_OFF_OPTIONS[0]);
                    this.useFadersAsKnobsSetting.set (ON_OFF_OPTIONS[1]);
                    this.setVUMetersEnabled (false);
                    break;

                default:
                    return;
            }

            profileSetting.set (DEVICE_SELECT);
        });

        this.hasDisplay1Setting = settingsUI.getEnumSetting ("Has a display", CATEGORY_HARDWARE_SETUP, ON_OFF_OPTIONS, ON_OFF_OPTIONS[1]);
        this.hasDisplay1Setting.addValueObserver (value -> {
            this.hasDisplay1 = "On".equals (value);
            this.notifyObservers (HAS_DISPLAY1);
        });

        this.hasDisplay2Setting = settingsUI.getEnumSetting ("Has a second display", CATEGORY_HARDWARE_SETUP, ON_OFF_OPTIONS, ON_OFF_OPTIONS[1]);
        this.hasDisplay2Setting.addValueObserver (value -> {
            this.hasDisplay2 = "On".equals (value);
            this.notifyObservers (HAS_DISPLAY2);
        });

        this.hasSegmentDisplaySetting = settingsUI.getEnumSetting ("Has a position/tempo display", CATEGORY_HARDWARE_SETUP, ON_OFF_OPTIONS, ON_OFF_OPTIONS[1]);
        this.hasSegmentDisplaySetting.addValueObserver (value -> {
            this.hasSegmentDisplay = "On".equals (value);
            this.notifyObservers (HAS_SEGMENT_DISPLAY);
        });

        this.hasAssignmentDisplaySetting = settingsUI.getEnumSetting ("Has an assignment display", CATEGORY_HARDWARE_SETUP, ON_OFF_OPTIONS, ON_OFF_OPTIONS[1]);
        this.hasAssignmentDisplaySetting.addValueObserver (value -> {
            this.hasAssignmentDisplay = "On".equals (value);
            this.notifyObservers (HAS_ASSIGNMENT_DISPLAY);
        });

        this.hasMotorFadersSetting = settingsUI.getEnumSetting ("Has motor faders", CATEGORY_HARDWARE_SETUP, ON_OFF_OPTIONS, ON_OFF_OPTIONS[1]);
        this.hasMotorFadersSetting.addValueObserver (value -> {
            this.hasMotorFaders = "On".equals (value);
            this.notifyObservers (HAS_MOTOR_FADERS);
        });

        this.displayTrackNamesSetting = settingsUI.getEnumSetting ("Display track names in 1st display", CATEGORY_HARDWARE_SETUP, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        this.displayTrackNamesSetting.addValueObserver (value -> {
            this.displayTrackNames = "On".equals (value);
            this.notifyObservers (DISPLAY_TRACK_NAMES);
        });

        this.useVertZoomForModesSetting = settingsUI.getEnumSetting ("Use vertical zoom to change tracks", CATEGORY_HARDWARE_SETUP, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        this.useVertZoomForModesSetting.addValueObserver (value -> {
            this.useVertZoomForModes = "On".equals (value);
            this.notifyObservers (USE_VERT_ZOOM_FOR_MODES);
        });

        this.useFadersAsKnobsSetting = settingsUI.getEnumSetting ("Use faders like editing knobs", CATEGORY_HARDWARE_SETUP, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        this.useFadersAsKnobsSetting.addValueObserver (value -> {
            this.useFadersAsKnobs = "On".equals (value);
            this.notifyObservers (USE_FADERS_AS_KNOBS);
        });
    }


    private void activateAssignableSettings (final ISettingsUI settingsUI)
    {
        for (int i = 0; i < this.assignableFunctions.length; i++)
        {
            final int pos = i;
            final IEnumSetting setting = settingsUI.getEnumSetting (ASSIGNABLE_BUTTON_NAMES[i], "Assignable buttons", ASSIGNABLE_VALUES, ASSIGNABLE_VALUES[6]);
            setting.addValueObserver (value -> this.assignableFunctions[pos] = lookupIndex (ASSIGNABLE_VALUES, value));
        }
    }


    /**
     * Activate the Zoom state setting.
     *
     * @param settingsUI The settings
     */
    protected void activateZoomStateSetting (final ISettingsUI settingsUI)
    {
        this.zoomStateSetting = settingsUI.getEnumSetting ("Zoom", CATEGORY_WORKFLOW, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        this.zoomStateSetting.addValueObserver (value -> {
            this.zoomState = "On".equals (value);
            this.notifyObservers (ZOOM_STATE);
        });
    }


    /**
     * Activate the channel touch select setting.
     *
     * @param settingsUI The settings
     */
    protected void activateChannelTouchSetting (final ISettingsUI settingsUI)
    {
        final IEnumSetting touchChannelSetting = settingsUI.getEnumSetting ("Select Channel on Fader Touch", CATEGORY_WORKFLOW, ON_OFF_OPTIONS, ON_OFF_OPTIONS[1]);
        touchChannelSetting.addValueObserver (value -> {
            this.touchChannel = "On".equals (value);
            this.notifyObservers (TOUCH_CHANNEL);
        });
    }


    /**
     * Activate the display Tempo or Ticks setting.
     *
     * @param settingsUI The settings
     */
    protected void activateDisplayTempoOrTicksSetting (final ISettingsUI settingsUI)
    {
        this.tempoOrTicksSetting = settingsUI.getEnumSetting ("Display tempo or ticks", CATEGORY_WORKFLOW, TEMPO_OR_TICKS_OPTIONS, TEMPO_OR_TICKS_OPTIONS[0]);
        this.tempoOrTicksSetting.addValueObserver (value -> {
            this.displayTicks = TEMPO_OR_TICKS_OPTIONS[0].equals (value);
            this.notifyObservers (DISPLAY_MODE_TICKS_OR_TEMPO);
        });
    }


    /**
     * Is zoom active?
     *
     * @return True if zoom is active
     */
    public boolean isZoomState ()
    {
        return this.zoomState;
    }


    /**
     * Toggles the zoom state.
     */
    public void toggleZoomState ()
    {
        this.zoomStateSetting.set (this.zoomState ? ON_OFF_OPTIONS[0] : ON_OFF_OPTIONS[1]);
    }


    /**
     * Display ticks in the segment display? Otherwise the tempo.
     *
     * @return True if the ticks should be displayed
     */
    public boolean isDisplayTicks ()
    {
        return this.displayTicks;
    }


    /**
     * Toggle to display tempo or ticks.
     */
    public void toggleDisplayTicks ()
    {
        this.tempoOrTicksSetting.set (this.displayTicks ? TEMPO_OR_TICKS_OPTIONS[1] : TEMPO_OR_TICKS_OPTIONS[0]);
    }


    /**
     * Returns true if it has a main display.
     *
     * @return True if it has a main display.
     */
    public boolean hasDisplay1 ()
    {
        return this.hasDisplay1;
    }


    /**
     * Returns true if it has a secondary display.
     *
     * @return True if it has a secondary display.
     */
    public boolean hasDisplay2 ()
    {
        return this.hasDisplay2;
    }


    /**
     * Returns true if it has a segment display for tempo and position.
     *
     * @return True if it has a segment display.
     */
    public boolean hasSegmentDisplay ()
    {
        return this.hasSegmentDisplay;
    }


    /**
     * Returns true if it has an assignment display for modes.
     *
     * @return True if it has an assignment display.
     */
    public boolean hasAssignmentDisplay ()
    {
        return this.hasAssignmentDisplay;
    }


    /**
     * Returns true if it has motor faders.
     *
     * @return True if it has motor faders.
     */
    public boolean hasMotorFaders ()
    {
        return this.hasMotorFaders;
    }


    /**
     * Returns true if the display names should be written in the 1st display.
     *
     * @return True if the display names should be written in the 1st display.
     */
    public boolean isDisplayTrackNames ()
    {
        return this.displayTrackNames;
    }


    /**
     * Toggles if the display names should be written in the 1st display.
     */
    public void toggleDisplayTrackNames ()
    {
        this.displayTrackNamesSetting.set (ON_OFF_OPTIONS[this.displayTrackNames ? 0 : 1]);
    }


    /**
     * Returns true if vertical zoom buttons should be used to change modes.
     *
     * @return True if vertical zoom buttons should be used to change modes.
     */
    public boolean useVertZoomForModes ()
    {
        return this.useVertZoomForModes;
    }


    /**
     * Returns true if faders should be used like the editing knobs.
     *
     * @return True if faders should be used like the editing knobs.
     */
    public boolean useFadersAsKnobs ()
    {
        return this.useFadersAsKnobs;
    }


    /**
     * Toggle if faders should be used like the editing knobs.
     */
    public void toggleUseFadersAsKnobs ()
    {
        this.useFadersAsKnobsSetting.set (ON_OFF_OPTIONS[this.useFadersAsKnobs ? 0 : 1]);
    }


    /**
     * Get the assignable function.
     *
     * @param index The index of the assignable
     * @return The function
     */
    public int getAssignable (final int index)
    {
        return this.assignableFunctions[index];
    }


    /**
     * Returns true if touching the channel fader should select the track.
     *
     * @return True if touching the channel fader should select the track.
     */
    public boolean isTouchChannel ()
    {
        return this.touchChannel;
    }
}
