// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.mcu;

import java.util.Arrays;
import java.util.List;

import de.mossgrabers.controller.mackie.mcu.controller.MCUDeviceType;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.IActionSetting;
import de.mossgrabers.framework.configuration.IEnumSetting;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.constants.Capability;
import de.mossgrabers.framework.daw.midi.ArpeggiatorMode;


/**
 * The configuration settings for MCU.
 *
 * @author Jürgen Moßgraber
 */
public class MCUConfiguration extends AbstractConfiguration
{
    /** Can display 1 show colors? */
    public enum DisplayColors
    {
        /** Colors are off. */
        OFF,
        /** Colors can be displayed with Asparion commands. */
        ASPARION,
        /** Colors can be displayed with Behringer commands. */
        BEHRINGER,
        /** Colors can be displayed with iCON commands. */
        ICON
    }


    /** Is a 1st Mackie display. */
    public enum MainDisplay
    {
        /** No display. */
        OFF,
        /** Asparion display with addition 3rd row and track numbers. */
        ASPARION,
        /** Uses Mackie protocol and 6 characters with a space. */
        MACKIE_6_CHARACTERS,
        /**
         * Uses Mackie protocol and 7 characters (for devices with separated displays for each
         * channel).
         */
        MACKIE_7_CHARACTERS
    }


    /** Is a 2nd iCON display present? */
    public enum SecondDisplay
    {
        /** No second display. */
        OFF,
        /** Asparion display with addition 3rd row and track numbers. */
        ASPARION,
        /** QCon Pro-X length including master. */
        QCON,
        /** V1-M length. */
        V1M
    }


    /** Are there VU meters? */
    public enum VUMeterStyle
    {
        /** VU Meters are off. */
        OFF,
        /** VU Meters are Asparion style (all stereo, no main). */
        ASPARION,
        /** VU Meters are iCON style (all mono but additional stereo main). */
        ICON,
        /** VU Meters are Mackie style (all mono, no main). */
        MACKIE
    }


    private static final String       MCU_DEVICE_1_LEFT                     = "MCU Device 1 - left";

    /** Zoom state. */
    public static final Integer       ZOOM_STATE                            = Integer.valueOf (50);
    /** Display time or beats. */
    public static final Integer       DISPLAY_MODE_TIME_OR_BEATS            = Integer.valueOf (51);
    /** Display mode tempo or ticks. */
    public static final Integer       DISPLAY_MODE_TICKS_OR_TEMPO           = Integer.valueOf (52);
    /** Has a main display? */
    public static final Integer       MAIN_DISPLAY                          = Integer.valueOf (53);
    /** Has a second display. */
    public static final Integer       SECOND_DISPLAY                        = Integer.valueOf (54);
    /** Has a segment display. */
    public static final Integer       HAS_SEGMENT_DISPLAY                   = Integer.valueOf (55);
    /** Has an assignment display. */
    public static final Integer       HAS_ASSIGNMENT_DISPLAY                = Integer.valueOf (56);
    /** Has motor faders. */
    public static final Integer       HAS_MOTOR_FADERS                      = Integer.valueOf (57);
    /** Has only 1 fader. */
    public static final Integer       HAS_ONLY_1_FADER                      = Integer.valueOf (58);
    /** Display track names in 1st display. */
    public static final Integer       DISPLAY_TRACK_NAMES                   = Integer.valueOf (59);
    /** Replace the vertical zoom with mode change. */
    public static final Integer       USE_VERT_ZOOM_FOR_MODES               = Integer.valueOf (60);
    /** Use the faders like the editing knobs. */
    public static final Integer       USE_FADERS_AS_KNOBS                   = Integer.valueOf (61);
    /** Select the channel when touching it's fader. */
    public static final Integer       TOUCH_SELECTS_CHANNEL                 = Integer.valueOf (62);
    /** Activate volume mode when touching a volume fader. */
    public static final Integer       TOUCH_CHANNEL_VOLUME_MODE             = Integer.valueOf (63);
    /** Always send VU meters even if values haven't changed. */
    public static final Integer       ALWAYS_SEND_VU_METERS                 = Integer.valueOf (64);
    /** iCON specific Master VU meter. */
    public static final Integer       ICON_VU_METER                         = Integer.valueOf (65);
    /** Pin FX tracks to last controller. */
    public static final Integer       PIN_FXTRACKS_TO_LAST_CONTROLLER       = Integer.valueOf (66);
    /** Support X-Touch display back-light colors. */
    public static final Integer       X_TOUCH_DISPLAY_COLORS                = Integer.valueOf (67);

    /** Use a Function button to switch to previous mode. */
    public static final int           FOOTSWITCH_PREV_MODE                  = 15;
    /** Use a Function button to switch to next mode. */
    public static final int           FOOTSWITCH_NEXT_MODE                  = 16;
    /** Use a Function button to switch to Marker mode. */
    public static final int           FOOTSWITCH_SHOW_MARKER_MODE           = 17;
    /** Toggle use faders like editing knobs. */
    public static final int           FOOTSWITCH_USE_FADERS_LIKE_EDIT_KNOBS = 18;
    /** Use a Function button to toggle motor faders on/off. */
    public static final int           FOOTSWITCH_TOGGLE_MOTOR_FADERS_ON_OFF = 19;
    /** Use a Function button to toggle punch in on/off. */
    public static final int           FOOTSWITCH_PUNCH_IN                   = 20;
    /** Use a Function button to toggle punch out on/off. */
    public static final int           FOOTSWITCH_PUNCH_OUT                  = 21;
    /** Use a Function button to toggle the selected device on/off. */
    public static final int           FOOTSWITCH_DEVICE_ON_OFF              = 22;
    /** Use a Function button to switch to previous channel. */
    public static final int           PREV_CHANNEL                          = 23;
    /** Use a Function button to switch to next channel. */
    public static final int           NEXT_CHANNEL                          = 24;
    /** Use a Function button to execute an action. */
    public static final int           FOOTSWITCH_ACTION                     = 25;

    private static final String       CATEGORY_EXTENDER_SETUP               = "Extender Setup (requires restart)";
    private static final String       CATEGORY_SEGMENT_DISPLAY              = "Segment Display";
    private static final String       CATEGORY_TRACKS                       = "Tracks (requires restart)";
    private static final String       CATEGORY_ASSIGNABLE_BUTTONS           = "Assignable buttons";

    private static final String       DEVICE_SELECT                         = "<Select a profile>";
    private static final String       DEVICE_ASPARION_D700                  = "Asparion D700";
    private static final String       DEVICE_BEHRINGER_X_TOUCH              = "Behringer X-Touch";
    private static final String       DEVICE_BEHRINGER_X_TOUCH_ONE          = "Behringer X-Touch One";
    private static final String       DEVICE_ICON_PLATFORM_M                = "iCON Platform M / M+";
    private static final String       DEVICE_ICON_QCON_PRO_X                = "iCON QConPro X";
    private static final String       DEVICE_ICON_QCON_V1M                  = "iCON V1-M";
    private static final String       DEVICE_MACKIE_MCU_PRO                 = "Mackie MCU Pro";
    private static final String       DEVICE_ZOOM_R16                       = "Zoom R16";

    private static final String []    DEVICE_OPTIONS                        =
    {
        DEVICE_SELECT,
        DEVICE_ASPARION_D700,
        DEVICE_BEHRINGER_X_TOUCH,
        DEVICE_BEHRINGER_X_TOUCH_ONE,
        DEVICE_ICON_PLATFORM_M,
        DEVICE_ICON_QCON_PRO_X,
        DEVICE_ICON_QCON_V1M,
        DEVICE_MACKIE_MCU_PRO,
        DEVICE_ZOOM_R16
    };

    private static final String []    ASSIGNABLE_VALUES                     =
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
        "Toggle use faders like editing knobs",
        "Toggle motor faders on/off",
        "Punch In",
        "Punch Out",
        "Device on/off",
        "Channel Prev",
        "Channel Next",
        "Action"
    };

    private static final String []    ASSIGNABLE_BUTTON_NAMES               =
    {
        "Footswitch 1",
        "Footswitch 2",
        "F1",
        "F2",
        "F3",
        "F4",
        "F5",
        "F6",
        "F7",
        "F8"
    };

    private static final int []       ASSIGNABLE_BUTTON_DEFAULTS            =
    {
        0,
        1,
        11,
        12,
        13,
        6,
        14,
        20,
        21,
        22
    };

    private static final String []    TIME_OR_BEATS_OPTIONS                 =
    {
        "Time",
        "Beats"
    };

    private static final String []    TEMPO_OR_TICKS_OPTIONS                =
    {
        "Ticks",
        "Tempo"
    };

    private static final String []    MCU_DEVICE_TYPE_OPTIONS               =
    {
        "Main",
        "Extender",
        "Mackie Extender"
    };

    private static final String [] [] MCU_DEVICE_DESCRIPTORS                =
    {
        {
            "MCU Device 1"
        },
        {
            MCU_DEVICE_1_LEFT,
            "MCU Device 2 - right",
        },
        {
            MCU_DEVICE_1_LEFT,
            "MCU Device 2 - center",
            "MCU Device 3 - right",
        },
        {
            MCU_DEVICE_1_LEFT,
            "MCU Device 2",
            "MCU Device 3",
            "MCU Device 4 - right",
        }
    };

    private static final String []    MAIN_DISPLAY_OPTIONS                  =
    {
        "Off",
        "Asparion",
        "Mackie - 6 characters",
        "Mackie - 7 characters"
    };

    private static final String []    DISPLAY_COLORS_OPTIONS                =
    {
        "Off",
        "Asparion",
        "Behringer",
        "iCON"
    };

    private static final String []    VU_METER_STYLES                       =
    {
        "Off",
        "Asparion",
        "iCON",
        "Mackie"
    };

    private static final String []    SECOND_DISPLAY_OPTIONS                =
    {
        "Off",
        "Asparion",
        "iCON QCon Pro-X",
        "iCON V1-M"
    };

    private IEnumSetting              zoomStateSetting;
    private IEnumSetting              displayTimeSetting;
    private IEnumSetting              tempoOrTicksSetting;
    private IEnumSetting              displayTrackNamesSetting;
    private IEnumSetting              useFadersAsKnobsSetting;
    private IEnumSetting              vuMeterStyleSetting;
    private IEnumSetting              hasMotorFadersSetting;

    private boolean                   zoomState;
    private boolean                   displayTime;
    private boolean                   displayTicks;
    private MainDisplay               mainDisplay;
    private SecondDisplay             secondDisplay;
    private boolean                   hasSegmentDisplay;
    private boolean                   hasAssignmentDisplay;
    private boolean                   hasMotorFaders;
    private boolean                   hasOnly1Fader;
    private boolean                   displayTrackNames;
    private boolean                   useVertZoomForModes;
    private boolean                   useFadersAsKnobs;
    private boolean                   alwaysSendVuMeters;
    private VUMeterStyle              vuMeterStyle;
    private DisplayColors             displayColors;
    private boolean                   touchSelectsChannel;
    private boolean                   touchChannelVolumeMode;
    private final int []              assignableFunctions                   = new int [ASSIGNABLE_BUTTON_NAMES.length];
    private final String []           assignableFunctionActions             = new String [ASSIGNABLE_BUTTON_NAMES.length];
    private final MCUDeviceType []    deviceTyes;
    private boolean                   includeFXTracksInTrackBank;
    private boolean                   pinFXTracksToLastController;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param numMCUDevices The number of MCU device (main device plus extenders) 1-4
     * @param arpeggiatorModes The available arpeggiator modes
     */
    public MCUConfiguration (final IHost host, final IValueChanger valueChanger, final int numMCUDevices, final List<ArpeggiatorMode> arpeggiatorModes)
    {
        super (host, valueChanger, arpeggiatorModes);

        Arrays.fill (this.assignableFunctions, 0);
        Arrays.fill (this.assignableFunctionActions, "");

        this.deviceTyes = new MCUDeviceType [numMCUDevices];
    }


    /** {@inheritDoc} */
    @Override
    public void init (final ISettingsUI globalSettings, final ISettingsUI documentSettings)
    {
        ///////////////////////////
        // Hardware

        this.activateHardwareSettings (globalSettings);
        this.activateExtenderSettings (globalSettings);

        ///////////////////////////
        // Segment display

        this.activateSegmentDisplaySettings (globalSettings);

        ///////////////////////////
        // Tracks setup

        this.activateTracksSettings (globalSettings);

        ///////////////////////////
        // Assignable buttons

        this.activateAssignableSettings (globalSettings);

        ///////////////////////////
        // Transport

        this.activateBehaviourOnStopSetting (globalSettings);
        this.activateBehaviourOnPauseSetting (globalSettings);
        this.activateFlipRecordSetting (globalSettings);

        ///////////////////////////
        // Play and Sequence

        this.activateQuantizeAmountSetting (globalSettings);

        ///////////////////////////
        // Workflow

        this.activateExcludeDeactivatedItemsSetting (globalSettings);
        this.activateNewClipLengthSetting (globalSettings);
        this.activateZoomStateSetting (globalSettings);
        this.activateChannelTouchSetting (globalSettings);
        this.activateKnobSpeedSetting (globalSettings);
    }


    private void activateHardwareSettings (final ISettingsUI settingsUI)
    {
        final IEnumSetting profileSetting = settingsUI.getEnumSetting ("Profile", CATEGORY_HARDWARE_SETUP, DEVICE_OPTIONS, DEVICE_OPTIONS[0]);

        final IEnumSetting mainDisplaySetting = settingsUI.getEnumSetting ("Main display", CATEGORY_HARDWARE_SETUP, MAIN_DISPLAY_OPTIONS, MAIN_DISPLAY_OPTIONS[2]);
        mainDisplaySetting.addValueObserver (value -> {

            if (MAIN_DISPLAY_OPTIONS[1].equals (value))
                this.mainDisplay = MainDisplay.ASPARION;
            else if (MAIN_DISPLAY_OPTIONS[2].equals (value))
                this.mainDisplay = MainDisplay.MACKIE_6_CHARACTERS;
            else if (MAIN_DISPLAY_OPTIONS[3].equals (value))
                this.mainDisplay = MainDisplay.MACKIE_7_CHARACTERS;
            else
                this.mainDisplay = MainDisplay.OFF;
            this.notifyObservers (MAIN_DISPLAY);

        });
        this.isSettingActive.add (MAIN_DISPLAY);

        final IEnumSetting secondDisplaySetting = settingsUI.getEnumSetting ("Has a second display", CATEGORY_HARDWARE_SETUP, SECOND_DISPLAY_OPTIONS, SECOND_DISPLAY_OPTIONS[0]);
        secondDisplaySetting.addValueObserver (value -> {

            if (SECOND_DISPLAY_OPTIONS[1].equals (value))
                this.secondDisplay = SecondDisplay.ASPARION;
            else if (SECOND_DISPLAY_OPTIONS[2].equals (value))
                this.secondDisplay = SecondDisplay.QCON;
            else if (SECOND_DISPLAY_OPTIONS[3].equals (value))
                this.secondDisplay = SecondDisplay.V1M;
            else
                this.secondDisplay = SecondDisplay.OFF;

            this.notifyObservers (SECOND_DISPLAY);
        });
        this.isSettingActive.add (SECOND_DISPLAY);

        final IEnumSetting hasSegmentDisplaySetting = settingsUI.getEnumSetting ("Has a segment display", CATEGORY_HARDWARE_SETUP, ON_OFF_OPTIONS, ON_OFF_OPTIONS[1]);
        hasSegmentDisplaySetting.addValueObserver (value -> {
            this.hasSegmentDisplay = "On".equals (value);
            this.notifyObservers (HAS_SEGMENT_DISPLAY);
        });
        this.isSettingActive.add (HAS_SEGMENT_DISPLAY);

        final IEnumSetting hasAssignmentDisplaySetting = settingsUI.getEnumSetting ("Has an assignment display", CATEGORY_HARDWARE_SETUP, ON_OFF_OPTIONS, ON_OFF_OPTIONS[1]);
        hasAssignmentDisplaySetting.addValueObserver (value -> {
            this.hasAssignmentDisplay = "On".equals (value);
            this.notifyObservers (HAS_ASSIGNMENT_DISPLAY);
        });
        this.isSettingActive.add (HAS_ASSIGNMENT_DISPLAY);

        this.hasMotorFadersSetting = settingsUI.getEnumSetting ("Has motor faders", CATEGORY_HARDWARE_SETUP, ON_OFF_OPTIONS, ON_OFF_OPTIONS[1]);
        this.hasMotorFadersSetting.addValueObserver (value -> {
            this.hasMotorFaders = "On".equals (value);
            this.notifyObservers (HAS_MOTOR_FADERS);
        });
        this.isSettingActive.add (HAS_MOTOR_FADERS);

        final IEnumSetting hasOnly1FaderSetting = settingsUI.getEnumSetting ("Has only 1 fader", CATEGORY_HARDWARE_SETUP, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        hasOnly1FaderSetting.addValueObserver (value -> {
            this.hasOnly1Fader = "On".equals (value);
            this.notifyObservers (HAS_ONLY_1_FADER);
        });
        this.isSettingActive.add (HAS_ONLY_1_FADER);

        this.displayTrackNamesSetting = settingsUI.getEnumSetting ("Display track names in 1st display", CATEGORY_HARDWARE_SETUP, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        this.displayTrackNamesSetting.addValueObserver (value -> {
            this.displayTrackNames = "On".equals (value);
            this.notifyObservers (DISPLAY_TRACK_NAMES);
        });
        this.isSettingActive.add (DISPLAY_TRACK_NAMES);

        final IEnumSetting useVertZoomForModesSetting = settingsUI.getEnumSetting ("Use vertical zoom to change modes", CATEGORY_HARDWARE_SETUP, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        useVertZoomForModesSetting.addValueObserver (value -> {
            this.useVertZoomForModes = "On".equals (value);
            this.notifyObservers (USE_VERT_ZOOM_FOR_MODES);
        });
        this.isSettingActive.add (USE_VERT_ZOOM_FOR_MODES);

        this.useFadersAsKnobsSetting = settingsUI.getEnumSetting ("Use faders like editing knobs", CATEGORY_HARDWARE_SETUP, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        this.useFadersAsKnobsSetting.addValueObserver (value -> {
            this.useFadersAsKnobs = "On".equals (value);
            this.notifyObservers (USE_FADERS_AS_KNOBS);
        });
        this.isSettingActive.add (USE_FADERS_AS_KNOBS);

        this.vuMeterStyleSetting = settingsUI.getEnumSetting ("VU Meters", CATEGORY_HARDWARE_SETUP, VU_METER_STYLES, VU_METER_STYLES[0]);
        this.vuMeterStyleSetting.addValueObserver (value -> {

            if (VU_METER_STYLES[1].equals (value))
                this.vuMeterStyle = VUMeterStyle.ASPARION;
            else if (VU_METER_STYLES[2].equals (value))
                this.vuMeterStyle = VUMeterStyle.ICON;
            else if (VU_METER_STYLES[3].equals (value))
                this.vuMeterStyle = VUMeterStyle.MACKIE;
            else
                this.vuMeterStyle = VUMeterStyle.OFF;

            this.notifyObservers (ENABLE_VU_METERS);
        });
        this.isSettingActive.add (ENABLE_VU_METERS);

        final IEnumSetting alwaysSendVuMetersSetting = settingsUI.getEnumSetting ("Always send VU Meters", CATEGORY_HARDWARE_SETUP, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        alwaysSendVuMetersSetting.addValueObserver (value -> {
            this.alwaysSendVuMeters = "On".equals (value);
            this.notifyObservers (ALWAYS_SEND_VU_METERS);
        });
        this.isSettingActive.add (ALWAYS_SEND_VU_METERS);

        final IEnumSetting displayColorsSetting = settingsUI.getEnumSetting ("Display colors", CATEGORY_HARDWARE_SETUP, DISPLAY_COLORS_OPTIONS, DISPLAY_COLORS_OPTIONS[0]);
        displayColorsSetting.addValueObserver (value -> {

            if (DISPLAY_COLORS_OPTIONS[1].equals (value))
                this.displayColors = DisplayColors.ASPARION;
            else if (DISPLAY_COLORS_OPTIONS[2].equals (value))
                this.displayColors = DisplayColors.BEHRINGER;
            else if (DISPLAY_COLORS_OPTIONS[3].equals (value))
                this.displayColors = DisplayColors.ICON;
            else
                this.displayColors = DisplayColors.OFF;

            this.notifyObservers (X_TOUCH_DISPLAY_COLORS);
        });
        this.isSettingActive.add (X_TOUCH_DISPLAY_COLORS);

        // Activate at the end, so all settings are created
        profileSetting.addValueObserver (value -> {
            final String on = ON_OFF_OPTIONS[1];
            final String off = ON_OFF_OPTIONS[0];
            switch (value)
            {
                case DEVICE_MACKIE_MCU_PRO:
                    mainDisplaySetting.set (MAIN_DISPLAY_OPTIONS[2]);
                    secondDisplaySetting.set (SECOND_DISPLAY_OPTIONS[0]);
                    hasSegmentDisplaySetting.set (on);
                    hasAssignmentDisplaySetting.set (on);
                    this.hasMotorFadersSetting.set (on);
                    hasOnly1FaderSetting.set (off);
                    this.displayTrackNamesSetting.set (on);
                    useVertZoomForModesSetting.set (off);
                    this.useFadersAsKnobsSetting.set (off);
                    alwaysSendVuMetersSetting.set (off);
                    this.vuMeterStyleSetting.set (VU_METER_STYLES[3]);
                    displayColorsSetting.set (DISPLAY_COLORS_OPTIONS[0]);
                    break;

                case DEVICE_ASPARION_D700:
                    mainDisplaySetting.set (MAIN_DISPLAY_OPTIONS[1]);
                    secondDisplaySetting.set (SECOND_DISPLAY_OPTIONS[1]);
                    hasSegmentDisplaySetting.set (off);
                    hasAssignmentDisplaySetting.set (off);
                    this.hasMotorFadersSetting.set (on);
                    hasOnly1FaderSetting.set (off);
                    this.displayTrackNamesSetting.set (on);
                    useVertZoomForModesSetting.set (off);
                    this.useFadersAsKnobsSetting.set (off);
                    alwaysSendVuMetersSetting.set (off);
                    this.vuMeterStyleSetting.set (VU_METER_STYLES[1]);
                    displayColorsSetting.set (DISPLAY_COLORS_OPTIONS[1]);
                    break;

                case DEVICE_BEHRINGER_X_TOUCH:
                    mainDisplaySetting.set (MAIN_DISPLAY_OPTIONS[3]);
                    secondDisplaySetting.set (SECOND_DISPLAY_OPTIONS[0]);
                    hasSegmentDisplaySetting.set (on);
                    hasAssignmentDisplaySetting.set (on);
                    this.hasMotorFadersSetting.set (on);
                    hasOnly1FaderSetting.set (off);
                    this.displayTrackNamesSetting.set (on);
                    useVertZoomForModesSetting.set (off);
                    this.useFadersAsKnobsSetting.set (off);
                    alwaysSendVuMetersSetting.set (off);
                    this.vuMeterStyleSetting.set (VU_METER_STYLES[3]);
                    displayColorsSetting.set (DISPLAY_COLORS_OPTIONS[2]);
                    break;

                case DEVICE_BEHRINGER_X_TOUCH_ONE:
                    mainDisplaySetting.set (MAIN_DISPLAY_OPTIONS[3]);
                    secondDisplaySetting.set (SECOND_DISPLAY_OPTIONS[0]);
                    hasSegmentDisplaySetting.set (on);
                    hasAssignmentDisplaySetting.set (on);
                    this.hasMotorFadersSetting.set (on);
                    hasOnly1FaderSetting.set (on);
                    this.displayTrackNamesSetting.set (on);
                    useVertZoomForModesSetting.set (off);
                    this.useFadersAsKnobsSetting.set (off);
                    alwaysSendVuMetersSetting.set (off);
                    this.vuMeterStyleSetting.set (VU_METER_STYLES[3]);
                    displayColorsSetting.set (DISPLAY_COLORS_OPTIONS[0]);
                    break;

                case DEVICE_ICON_PLATFORM_M:
                    mainDisplaySetting.set (MAIN_DISPLAY_OPTIONS[0]);
                    secondDisplaySetting.set (SECOND_DISPLAY_OPTIONS[0]);
                    hasSegmentDisplaySetting.set (off);
                    hasAssignmentDisplaySetting.set (off);
                    this.hasMotorFadersSetting.set (on);
                    hasOnly1FaderSetting.set (off);
                    this.displayTrackNamesSetting.set (off);
                    useVertZoomForModesSetting.set (on);
                    this.useFadersAsKnobsSetting.set (off);
                    alwaysSendVuMetersSetting.set (off);
                    this.vuMeterStyleSetting.set (VU_METER_STYLES[0]);
                    displayColorsSetting.set (DISPLAY_COLORS_OPTIONS[0]);
                    break;

                case DEVICE_ICON_QCON_PRO_X:
                    mainDisplaySetting.set (MAIN_DISPLAY_OPTIONS[2]);
                    secondDisplaySetting.set (SECOND_DISPLAY_OPTIONS[2]);
                    hasSegmentDisplaySetting.set (on);
                    hasAssignmentDisplaySetting.set (off);
                    this.hasMotorFadersSetting.set (on);
                    hasOnly1FaderSetting.set (off);
                    this.displayTrackNamesSetting.set (off);
                    useVertZoomForModesSetting.set (off);
                    this.useFadersAsKnobsSetting.set (off);
                    alwaysSendVuMetersSetting.set (off);
                    this.vuMeterStyleSetting.set (VU_METER_STYLES[2]);
                    displayColorsSetting.set (DISPLAY_COLORS_OPTIONS[0]);
                    break;

                case DEVICE_ICON_QCON_V1M:
                    mainDisplaySetting.set (MAIN_DISPLAY_OPTIONS[3]);
                    secondDisplaySetting.set (SECOND_DISPLAY_OPTIONS[3]);
                    hasSegmentDisplaySetting.set (on);
                    hasAssignmentDisplaySetting.set (off);
                    this.hasMotorFadersSetting.set (on);
                    hasOnly1FaderSetting.set (off);
                    this.displayTrackNamesSetting.set (off);
                    useVertZoomForModesSetting.set (off);
                    this.useFadersAsKnobsSetting.set (off);
                    alwaysSendVuMetersSetting.set (off);
                    this.vuMeterStyleSetting.set (VU_METER_STYLES[2]);
                    displayColorsSetting.set (DISPLAY_COLORS_OPTIONS[3]);
                    break;

                case DEVICE_ZOOM_R16:
                    mainDisplaySetting.set (MAIN_DISPLAY_OPTIONS[0]);
                    secondDisplaySetting.set (SECOND_DISPLAY_OPTIONS[0]);
                    hasSegmentDisplaySetting.set (off);
                    hasAssignmentDisplaySetting.set (off);
                    this.hasMotorFadersSetting.set (off);
                    hasOnly1FaderSetting.set (off);
                    this.displayTrackNamesSetting.set (off);
                    useVertZoomForModesSetting.set (off);
                    this.useFadersAsKnobsSetting.set (on);
                    alwaysSendVuMetersSetting.set (off);
                    this.vuMeterStyleSetting.set (VU_METER_STYLES[0]);
                    displayColorsSetting.set (DISPLAY_COLORS_OPTIONS[0]);
                    break;

                default:
                    return;
            }

            profileSetting.set (DEVICE_SELECT);
        });
    }


    private void activateExtenderSettings (final ISettingsUI settingsUI)
    {
        for (int i = 0; i < this.deviceTyes.length; i++)
        {
            final String label = MCU_DEVICE_DESCRIPTORS[this.deviceTyes.length - 1][i];
            final IEnumSetting setting = settingsUI.getEnumSetting (label, CATEGORY_EXTENDER_SETUP, MCU_DEVICE_TYPE_OPTIONS, MCU_DEVICE_TYPE_OPTIONS[i == this.deviceTyes.length - 1 ? 0 : 1]);
            final String value = setting.get ();
            if (MCU_DEVICE_TYPE_OPTIONS[0].equals (value))
                this.deviceTyes[i] = MCUDeviceType.MAIN;
            else if (MCU_DEVICE_TYPE_OPTIONS[1].equals (value))
                this.deviceTyes[i] = MCUDeviceType.EXTENDER;
            else if (MCU_DEVICE_TYPE_OPTIONS[2].equals (value))
                this.deviceTyes[i] = MCUDeviceType.MACKIE_EXTENDER;
        }
    }


    private void activateSegmentDisplaySettings (final ISettingsUI settingsUI)
    {
        this.displayTimeSetting = settingsUI.getEnumSetting ("Display time or beats", CATEGORY_SEGMENT_DISPLAY, TIME_OR_BEATS_OPTIONS, TIME_OR_BEATS_OPTIONS[0]);
        this.displayTimeSetting.addValueObserver (value -> {
            this.displayTime = TIME_OR_BEATS_OPTIONS[0].equals (value);
            this.notifyObservers (DISPLAY_MODE_TIME_OR_BEATS);
        });
        this.isSettingActive.add (DISPLAY_MODE_TIME_OR_BEATS);

        this.tempoOrTicksSetting = settingsUI.getEnumSetting ("Display tempo or ticks/milliseconds", CATEGORY_SEGMENT_DISPLAY, TEMPO_OR_TICKS_OPTIONS, TEMPO_OR_TICKS_OPTIONS[0]);
        this.tempoOrTicksSetting.addValueObserver (value -> {
            this.displayTicks = TEMPO_OR_TICKS_OPTIONS[0].equals (value);
            this.notifyObservers (DISPLAY_MODE_TICKS_OR_TEMPO);
        });
        this.isSettingActive.add (DISPLAY_MODE_TICKS_OR_TEMPO);
    }


    private void activateTracksSettings (final ISettingsUI settingsUI)
    {
        this.activateTrackNavigationSetting (settingsUI, CATEGORY_TRACKS, true);

        final IEnumSetting includeFXTracksSetting = settingsUI.getEnumSetting ("Include FX and master tracks in track bank", CATEGORY_TRACKS, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        this.includeFXTracksInTrackBank = "On".equals (includeFXTracksSetting.get ());

        if (this.deviceTyes.length > 1 && this.host.supports (Capability.HAS_EFFECT_BANK))
        {
            final IEnumSetting pinFXTracksToLastControllerSetting = settingsUI.getEnumSetting ("Pin FX tracks to last device", CATEGORY_TRACKS, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
            pinFXTracksToLastControllerSetting.addValueObserver (value -> {
                this.pinFXTracksToLastController = "On".equals (value);
                this.notifyObservers (PIN_FXTRACKS_TO_LAST_CONTROLLER);
            });
            this.isSettingActive.add (PIN_FXTRACKS_TO_LAST_CONTROLLER);
        }
    }


    private void activateAssignableSettings (final ISettingsUI settingsUI)
    {
        for (int i = 0; i < this.assignableFunctions.length; i++)
        {
            final int pos = i;
            final IEnumSetting assignableSetting = settingsUI.getEnumSetting (ASSIGNABLE_BUTTON_NAMES[i], CATEGORY_ASSIGNABLE_BUTTONS, ASSIGNABLE_VALUES, ASSIGNABLE_VALUES[ASSIGNABLE_BUTTON_DEFAULTS[i]]);
            assignableSetting.addValueObserver (value -> this.assignableFunctions[pos] = lookupIndex (ASSIGNABLE_VALUES, value));

            final IActionSetting actionSetting = settingsUI.getActionSetting (ASSIGNABLE_BUTTON_NAMES[i] + " - Action", CATEGORY_ASSIGNABLE_BUTTONS);
            actionSetting.addValueObserver (value -> this.assignableFunctionActions[pos] = actionSetting.get ());
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
        this.isSettingActive.add (ZOOM_STATE);
    }


    /**
     * Activate the channel touch select setting.
     *
     * @param settingsUI The settings
     */
    protected void activateChannelTouchSetting (final ISettingsUI settingsUI)
    {
        final IEnumSetting touchSelectsChannelSetting = settingsUI.getEnumSetting ("Select Channel on Fader Touch", CATEGORY_WORKFLOW, ON_OFF_OPTIONS, ON_OFF_OPTIONS[1]);
        touchSelectsChannelSetting.addValueObserver (value -> {
            this.touchSelectsChannel = "On".equals (value);
            this.notifyObservers (TOUCH_SELECTS_CHANNEL);
        });
        this.isSettingActive.add (TOUCH_SELECTS_CHANNEL);

        final IEnumSetting touchChannelVolumeModeSetting = settingsUI.getEnumSetting ("Activate Volume mode on Fader Touch", CATEGORY_WORKFLOW, ON_OFF_OPTIONS, ON_OFF_OPTIONS[1]);
        touchChannelVolumeModeSetting.addValueObserver (value -> {
            this.touchChannelVolumeMode = "On".equals (value);
            this.notifyObservers (TOUCH_CHANNEL_VOLUME_MODE);
        });
        this.isSettingActive.add (TOUCH_CHANNEL_VOLUME_MODE);
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
     * Display time in the segment display? Otherwise beats (measures).
     *
     * @return True if the time should be displayed
     */
    public boolean isDisplayTime ()
    {
        return this.displayTime;
    }


    /**
     * Toggle to display time or beats.
     */
    public void toggleDisplayTime ()
    {
        this.displayTimeSetting.set (this.displayTime ? TIME_OR_BEATS_OPTIONS[1] : TIME_OR_BEATS_OPTIONS[0]);
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
     * Returns the type of the main display.
     *
     * @return The type
     */
    public MainDisplay getMainDisplayType ()
    {
        return this.mainDisplay;
    }


    /**
     * Returns if it has a secondary display.
     *
     * @return The type of the 2nd display
     */
    public SecondDisplay getSecondDisplayType ()
    {
        return this.secondDisplay;
    }


    /**
     * Returns true if it has a segment display for tempo and position.
     *
     * @return True if it has a segment display
     */
    public boolean hasSegmentDisplay ()
    {
        return this.hasSegmentDisplay;
    }


    /**
     * Returns true if it has an assignment display for modes.
     *
     * @return True if it has an assignment display
     */
    public boolean hasAssignmentDisplay ()
    {
        return this.hasAssignmentDisplay;
    }


    /**
     * Returns true if it has motor faders.
     *
     * @return True if it has motor faders
     */
    public boolean hasMotorFaders ()
    {
        return this.hasMotorFaders;
    }


    /**
     * Toggles the has motor faders setting.
     */
    public void toggleMotorFaders ()
    {
        this.hasMotorFadersSetting.set (ON_OFF_OPTIONS[this.hasMotorFaders ? 0 : 1]);
    }


    /**
     * Returns true if it has only 1 fader.
     *
     * @return True if it has only 1 fader
     */
    public boolean hasOnly1Fader ()
    {
        return this.hasOnly1Fader;
    }


    /**
     * Returns true if the display names should be written in the 1st display.
     *
     * @return True if the display names should be written in the 1st display
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
     * @return True if vertical zoom buttons should be used to change modes
     */
    public boolean useVertZoomForModes ()
    {
        return this.useVertZoomForModes;
    }


    /**
     * Returns true if VU value updates should always be send even if the value hasn't changed.
     *
     * @return True if VU values should be always send
     */
    public boolean alwaysSendVuMeters ()
    {
        return this.alwaysSendVuMeters;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isEnableVUMeters ()
    {
        return this.vuMeterStyle != VUMeterStyle.OFF;
    }


    /**
     * Returns the style of VU meters.
     *
     * @return The style
     */
    public VUMeterStyle getVuMeterStyle ()
    {
        return this.vuMeterStyle;
    }


    /**
     * Returns if colors are available on the displays and how to set them.
     *
     * @return True if display back-light colors should be enabled
     */
    public DisplayColors hasDisplayColors ()
    {
        return this.displayColors;
    }


    /**
     * Returns true if faders should be used like the editing knobs.
     *
     * @return True if faders should be used like the editing knobs
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
     * If the assignable function is set to Action this method gets the selected action to execute.
     *
     * @param index The index of the assignable
     * @return The ID of the action to execute
     */
    public String getAssignableAction (final int index)
    {
        return this.assignableFunctionActions[index];
    }


    /**
     * Returns true if touching the channel fader should select the track.
     *
     * @return True if touching the channel fader should select the track
     */
    public boolean isTouchSelectsChannel ()
    {
        return this.touchSelectsChannel;
    }


    /**
     * Returns true if touching the channel fader should activate volume mode.
     *
     * @return True if touching the channel fader should activate volume mode
     */
    public boolean isTouchChannelVolumeMode ()
    {
        return this.touchChannelVolumeMode;
    }


    /**
     * Should FX and the master track included in the track bank?
     *
     * @return True to include
     */
    public boolean shouldIncludeFXTracksInTrackBank ()
    {
        return this.includeFXTracksInTrackBank;
    }


    /**
     * Should the FX tracks always be displayed on the last device.
     *
     * @return True to display FX tracks on last device
     */
    public boolean shouldPinFXTracksToLastController ()
    {
        return this.pinFXTracksToLastController;
    }


    /**
     * Get the type of the individual MCU devices.
     *
     * @param index The index of the device (0-3)
     * @return The configured device type
     */
    public MCUDeviceType getDeviceType (final int index)
    {
        return this.deviceTyes[index];
    }


    /**
     * Get the number of MCU devices.
     *
     * @return The number of configured MCU devices (1..N)
     */
    public int getNumMCUDevices ()
    {
        return this.deviceTyes.length;
    }
}
