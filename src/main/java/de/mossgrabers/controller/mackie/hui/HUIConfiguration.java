// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.hui;

import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.IEnumSetting;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.ArpeggiatorMode;

import java.util.Arrays;
import java.util.List;


/**
 * The configuration settings for MCU.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class HUIConfiguration extends AbstractConfiguration
{
    /** Zoom state. */
    public static final Integer    ZOOM_STATE              = Integer.valueOf (50);
    /** Has a display. */
    public static final Integer    HAS_DISPLAY1            = Integer.valueOf (51);
    /** Should send ping. */
    public static final Integer    SEND_PING               = Integer.valueOf (52);
    /** Has a segment display. */
    public static final Integer    HAS_SEGMENT_DISPLAY     = Integer.valueOf (53);
    /** Has motor faders. */
    public static final Integer    HAS_MOTOR_FADERS        = Integer.valueOf (54);
    /** Select the channel when touching it's fader. */
    private static final Integer   TOUCH_CHANNEL           = Integer.valueOf (55);

    /** Use a Function button to switch to previous mode. */
    public static final int        FOOTSWITCH_2_PREV_MODE  = 15;
    /** Use a Function button to switch to next mode. */
    public static final int        FOOTSWITCH_2_NEXT_MODE  = 16;
    /** Use a Function button to switch to Marker mode. */
    public static final int        SHOW_MARKER_MODE        = 17;

    private static final String    DEVICE_SELECT           = "<Select a profile>";
    private static final String    DEVICE_ICON_QCON_PRO_X  = "icon QConPro X";
    private static final String    DEVICE_MACKIE_HUI       = "Mackie HUI";
    private static final String    DEVICE_NOVATION_SLMKIII = "Novation MkIII";

    private static final String [] DEVICE_OPTIONS          =
    {
        DEVICE_SELECT,
        DEVICE_ICON_QCON_PRO_X,
        DEVICE_MACKIE_HUI,
        DEVICE_NOVATION_SLMKIII
    };

    private static final String [] ASSIGNABLE_BUTTON_NAMES =
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

    private IEnumSetting           zoomStateSetting;
    private IEnumSetting           hasDisplay1Setting;
    private IEnumSetting           hasSegmentDisplaySetting;
    private IEnumSetting           hasMotorFadersSetting;

    private boolean                zoomState;
    private boolean                hasDisplay1;
    private boolean                hasSegmentDisplay;
    private boolean                hasMotorFaders;
    private boolean                touchChannel;
    private boolean                sendPing;

    private final int []           assignableFunctions     = new int [10];


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param arpeggiatorModes The available arpeggiator modes
     */
    public HUIConfiguration (final IHost host, final IValueChanger valueChanger, final List<ArpeggiatorMode> arpeggiatorModes)
    {
        super (host, valueChanger, arpeggiatorModes);

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
        this.activateBehaviourOnPauseSetting (globalSettings);

        ///////////////////////////
        // Workflow

        this.activateExcludeDeactivatedItemsSetting (globalSettings);
        this.activateZoomStateSetting (globalSettings);
        this.activateChannelTouchSetting (globalSettings);
        this.activateNewClipLengthSetting (globalSettings);
    }


    private void activateHardwareSettings (final ISettingsUI settingsUI)
    {
        final IEnumSetting profileSetting = settingsUI.getEnumSetting ("Profile", CATEGORY_HARDWARE_SETUP, DEVICE_OPTIONS, DEVICE_OPTIONS[0]);
        profileSetting.addValueObserver (value -> {
            switch (value)
            {
                case DEVICE_MACKIE_HUI:
                case DEVICE_ICON_QCON_PRO_X:
                    this.hasDisplay1Setting.set (ON_OFF_OPTIONS[1]);
                    this.hasSegmentDisplaySetting.set (ON_OFF_OPTIONS[1]);
                    this.hasMotorFadersSetting.set (ON_OFF_OPTIONS[1]);
                    this.setVUMetersEnabled (true);
                    break;

                case DEVICE_NOVATION_SLMKIII:
                    this.hasDisplay1Setting.set (ON_OFF_OPTIONS[1]);
                    this.hasSegmentDisplaySetting.set (ON_OFF_OPTIONS[0]);
                    this.hasMotorFadersSetting.set (ON_OFF_OPTIONS[0]);
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

        this.hasSegmentDisplaySetting = settingsUI.getEnumSetting ("Has a position/tempo display", CATEGORY_HARDWARE_SETUP, ON_OFF_OPTIONS, ON_OFF_OPTIONS[1]);
        this.hasSegmentDisplaySetting.addValueObserver (value -> {
            this.hasSegmentDisplay = "On".equals (value);
            this.notifyObservers (HAS_SEGMENT_DISPLAY);
        });

        this.hasMotorFadersSetting = settingsUI.getEnumSetting ("Has motor faders", CATEGORY_HARDWARE_SETUP, ON_OFF_OPTIONS, ON_OFF_OPTIONS[1]);
        this.hasMotorFadersSetting.addValueObserver (value -> {
            this.hasMotorFaders = "On".equals (value);
            this.notifyObservers (HAS_MOTOR_FADERS);
        });

        final IEnumSetting sendPingSetting = settingsUI.getEnumSetting ("Send ping", CATEGORY_HARDWARE_SETUP, ON_OFF_OPTIONS, ON_OFF_OPTIONS[1]);
        sendPingSetting.addValueObserver (value -> {
            this.sendPing = "On".equals (value);
            this.notifyObservers (HAS_MOTOR_FADERS);
        });

        this.isSettingActive.add (HAS_DISPLAY1);
        this.isSettingActive.add (HAS_SEGMENT_DISPLAY);
        this.isSettingActive.add (HAS_MOTOR_FADERS);
        this.isSettingActive.add (SEND_PING);
    }


    private void activateAssignableSettings (final ISettingsUI settingsUI)
    {
        for (int i = 0; i < this.assignableFunctions.length; i++)
        {
            final int pos = i;
            final IEnumSetting setting = settingsUI.getEnumSetting (ASSIGNABLE_BUTTON_NAMES[i], "Assignable buttons", FOOTSWITCH_VALUES, FOOTSWITCH_VALUES[6]);
            setting.addValueObserver (value -> this.assignableFunctions[pos] = lookupIndex (FOOTSWITCH_VALUES, value));
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
        final IEnumSetting touchChannelSetting = settingsUI.getEnumSetting ("Select Channel on Fader Touch", CATEGORY_WORKFLOW, ON_OFF_OPTIONS, ON_OFF_OPTIONS[1]);
        touchChannelSetting.addValueObserver (value -> {
            this.touchChannel = "On".equals (value);
            this.notifyObservers (TOUCH_CHANNEL);
        });
        this.isSettingActive.add (TOUCH_CHANNEL);
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
     * Returns true if it has a main display.
     *
     * @return True if it has a main display
     */
    public boolean hasDisplay1 ()
    {
        return this.hasDisplay1;
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
     * Returns true if it has motor faders.
     *
     * @return True if it has motor faders
     */
    public boolean hasMotorFaders ()
    {
        return this.hasMotorFaders;
    }


    /**
     * Returns true if a ping message should be send every second to the HUI device.
     *
     * @return True if it should send a ping
     */
    public boolean shouldSendPing ()
    {
        return this.sendPing;
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
