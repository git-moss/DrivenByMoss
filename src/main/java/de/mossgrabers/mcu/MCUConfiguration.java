// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.mcu;

import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.controller.ValueChanger;

import com.bitwig.extension.controller.api.Preferences;
import com.bitwig.extension.controller.api.SettableEnumValue;

import java.util.Arrays;


/**
 * The configuration settings for MCU.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MCUConfiguration extends AbstractConfiguration
{
    /** Zoom state. */
    public static final Integer    ZOOM_STATE                  = 30;
    /** Display mode tempo or ticks. */
    public static final Integer    DISPLAY_MODE_TICKS_OR_TEMPO = 31;
    /** Has a display. */
    public static final Integer    HAS_DISPLAY1                = 32;
    /** Has a second display. */
    public static final Integer    HAS_DISPLAY2                = 33;
    /** Has a segment display. */
    public static final Integer    HAS_SEGMENT_DISPLAY         = 34;
    /** Has an assignment display. */
    public static final Integer    HAS_ASSIGNMENT_DISPLAY      = 35;
    /** Has motor faders. */
    public static final Integer    HAS_MOTOR_FADERS            = 36;
    /** Display track names in 1st display. */
    public static final Integer    DISPLAY_TRACK_NAMES         = 37;

    private static final String    DEVICE_SELECT               = "<Select a profile>";
    private static final String    DEVICE_ICON_PLATFORM_M      = "icon Platform M";
    private static final String    DEVICE_ICON_QCON_PRO_X      = "icon QConPro X";
    private static final String    DEVICE_MACKIE_MCU_PRO       = "Mackie MCU Pro";
    private static final String    DEVICE_ZOOM_R16             = "Zoom R16";

    private static final String [] DEVICE_OPTIONS              = new String []
    {
        DEVICE_SELECT,
        DEVICE_ICON_PLATFORM_M,
        DEVICE_ICON_QCON_PRO_X,
        DEVICE_MACKIE_MCU_PRO,
        DEVICE_ZOOM_R16
    };

    private static final String [] ASSIGNABLE_BUTTON_NAMES     = new String []
    {
        "Footswitch 1",
        "Footswitch 2",
        "F1",
        "F2",
        "F3",
        "F4",
        "F5"
    };

    private static final String [] TEMPO_OR_TICKS_OPTIONS      = new String []
    {
        "Ticks",
        "Tempo"
    };

    private SettableEnumValue      zoomStateSetting;
    private SettableEnumValue      tempoOrTicksSetting;
    private SettableEnumValue      hasDisplay1Setting;
    private SettableEnumValue      hasDisplay2Setting;
    private SettableEnumValue      hasSegmentDisplaySetting;
    private SettableEnumValue      hasAssignmentDisplaySetting;
    private SettableEnumValue      hasMotorFadersSetting;
    private SettableEnumValue      displayTrackNamesSetting;

    private boolean                zoomState;
    private boolean                displayTicks;
    private boolean                hasDisplay1;
    private boolean                hasDisplay2;
    private boolean                hasSegmentDisplay;
    private boolean                hasAssignmentDisplay;
    private boolean                hasMotorFaders;
    private boolean                displayTrackNames;
    private int []                 assignableFunctions         = new int [7];


    /**
     * Constructor.
     *
     * @param valueChanger The value changer
     */
    public MCUConfiguration (final ValueChanger valueChanger)
    {
        super (valueChanger);
        Arrays.fill (this.assignableFunctions, 0);
    }


    /** {@inheritDoc} */
    @Override
    public void init (final Preferences preferences)
    {
        ///////////////////////////
        // Hardware

        this.activateHardwareSettings (preferences);
        this.activateEnableVUMetersSetting (preferences, CATEGORY_HARDWARE_SETUP);

        ///////////////////////////
        // Assignable buttons

        this.activateAssignableSettings (preferences);

        ///////////////////////////
        // Transport

        this.activateBehaviourOnStopSetting (preferences);
        this.activateFlipRecordSetting (preferences);

        ///////////////////////////
        // Play and Sequence

        this.activateQuantizeAmountSetting (preferences);

        ///////////////////////////
        // Workflow

        this.activateDisplayCrossfaderSetting (preferences);
        this.activateNewClipLengthSetting (preferences);
        this.activateZoomStateSetting (preferences);
        this.activateDisplayTempoOrTicksSetting (preferences);

        ///////////////////////////
        // Browser

        this.activateBrowserSettings (preferences);
    }


    private void activateHardwareSettings (final Preferences prefs)
    {
        final SettableEnumValue profileSetting = prefs.getEnumSetting ("Profile", CATEGORY_HARDWARE_SETUP, DEVICE_OPTIONS, DEVICE_OPTIONS[0]);
        profileSetting.addValueObserver (value -> {
            switch (value)
            {
                case DEVICE_ICON_PLATFORM_M:
                    this.hasDisplay1Setting.set (ON_OFF_OPTIONS[0]);
                    this.hasDisplay2Setting.set (ON_OFF_OPTIONS[0]);
                    this.hasSegmentDisplaySetting.set (ON_OFF_OPTIONS[0]);
                    this.hasAssignmentDisplaySetting.set (ON_OFF_OPTIONS[0]);
                    this.hasMotorFadersSetting.set (ON_OFF_OPTIONS[1]);
                    this.displayTrackNamesSetting.set (ON_OFF_OPTIONS[0]);
                    this.setVUMetersEnabled (false);
                    break;

                case DEVICE_ICON_QCON_PRO_X:
                    this.hasDisplay1Setting.set (ON_OFF_OPTIONS[1]);
                    this.hasDisplay2Setting.set (ON_OFF_OPTIONS[1]);
                    this.hasSegmentDisplaySetting.set (ON_OFF_OPTIONS[1]);
                    this.hasAssignmentDisplaySetting.set (ON_OFF_OPTIONS[0]);
                    this.hasMotorFadersSetting.set (ON_OFF_OPTIONS[1]);
                    this.displayTrackNamesSetting.set (ON_OFF_OPTIONS[0]);
                    this.setVUMetersEnabled (true);
                    break;

                case DEVICE_MACKIE_MCU_PRO:
                    this.hasDisplay1Setting.set (ON_OFF_OPTIONS[1]);
                    this.hasDisplay2Setting.set (ON_OFF_OPTIONS[0]);
                    this.hasSegmentDisplaySetting.set (ON_OFF_OPTIONS[1]);
                    this.hasAssignmentDisplaySetting.set (ON_OFF_OPTIONS[1]);
                    this.hasMotorFadersSetting.set (ON_OFF_OPTIONS[1]);
                    this.displayTrackNamesSetting.set (ON_OFF_OPTIONS[1]);
                    this.setVUMetersEnabled (true);
                    break;

                case DEVICE_ZOOM_R16:
                    this.hasDisplay1Setting.set (ON_OFF_OPTIONS[0]);
                    this.hasDisplay2Setting.set (ON_OFF_OPTIONS[0]);
                    this.hasSegmentDisplaySetting.set (ON_OFF_OPTIONS[0]);
                    this.hasAssignmentDisplaySetting.set (ON_OFF_OPTIONS[0]);
                    this.hasMotorFadersSetting.set (ON_OFF_OPTIONS[0]);
                    this.displayTrackNamesSetting.set (ON_OFF_OPTIONS[0]);
                    this.setVUMetersEnabled (false);
                    break;
            }

            profileSetting.set (DEVICE_SELECT);
        });

        this.hasDisplay1Setting = prefs.getEnumSetting ("Has a display", CATEGORY_HARDWARE_SETUP, ON_OFF_OPTIONS, ON_OFF_OPTIONS[1]);
        this.hasDisplay1Setting.addValueObserver (value -> {
            this.hasDisplay1 = "On".equals (value);
            this.notifyObservers (HAS_DISPLAY1);
        });

        this.hasDisplay2Setting = prefs.getEnumSetting ("Has a second display", CATEGORY_HARDWARE_SETUP, ON_OFF_OPTIONS, ON_OFF_OPTIONS[1]);
        this.hasDisplay2Setting.addValueObserver (value -> {
            this.hasDisplay2 = "On".equals (value);
            this.notifyObservers (HAS_DISPLAY2);
        });

        this.hasSegmentDisplaySetting = prefs.getEnumSetting ("Has a position/tempo display", CATEGORY_HARDWARE_SETUP, ON_OFF_OPTIONS, ON_OFF_OPTIONS[1]);
        this.hasSegmentDisplaySetting.addValueObserver (value -> {
            this.hasSegmentDisplay = "On".equals (value);
            this.notifyObservers (HAS_SEGMENT_DISPLAY);
        });

        this.hasAssignmentDisplaySetting = prefs.getEnumSetting ("Has an assignment display", CATEGORY_HARDWARE_SETUP, ON_OFF_OPTIONS, ON_OFF_OPTIONS[1]);
        this.hasAssignmentDisplaySetting.addValueObserver (value -> {
            this.hasAssignmentDisplay = "On".equals (value);
            this.notifyObservers (HAS_ASSIGNMENT_DISPLAY);
        });

        this.hasMotorFadersSetting = prefs.getEnumSetting ("Has motor faders", CATEGORY_HARDWARE_SETUP, ON_OFF_OPTIONS, ON_OFF_OPTIONS[1]);
        this.hasMotorFadersSetting.addValueObserver (value -> {
            this.hasMotorFaders = "On".equals (value);
            this.notifyObservers (HAS_MOTOR_FADERS);
        });

        this.displayTrackNamesSetting = prefs.getEnumSetting ("Display track names in 1st display", CATEGORY_HARDWARE_SETUP, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        this.displayTrackNamesSetting.addValueObserver (value -> {
            this.displayTrackNames = "On".equals (value);
            this.notifyObservers (DISPLAY_TRACK_NAMES);
        });
    }


    private void activateAssignableSettings (final Preferences prefs)
    {
        for (int i = 0; i < this.assignableFunctions.length; i++)
        {
            final int pos = i;
            final SettableEnumValue setting = prefs.getEnumSetting (ASSIGNABLE_BUTTON_NAMES[i], "Assignable buttons", FOOTSWITCH_VALUES, FOOTSWITCH_VALUES[6]);
            setting.addValueObserver (value -> {
                for (int f = 0; f < FOOTSWITCH_VALUES.length; f++)
                {
                    if (FOOTSWITCH_VALUES[f].equals (value))
                        this.assignableFunctions[pos] = f;
                }
            });
        }
    }


    /**
     * Activate the Zoom state setting.
     *
     * @param prefs The preferences
     */
    protected void activateZoomStateSetting (final Preferences prefs)
    {
        this.zoomStateSetting = prefs.getEnumSetting ("Zoom", CATEGORY_WORKFLOW, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        this.zoomStateSetting.addValueObserver (value -> {
            this.zoomState = "On".equals (value);
            this.notifyObservers (ZOOM_STATE);
        });
    }


    /**
     * Activate the display Tempo or Ticks setting.
     *
     * @param prefs The preferences
     */
    protected void activateDisplayTempoOrTicksSetting (final Preferences prefs)
    {
        this.tempoOrTicksSetting = prefs.getEnumSetting ("Display tempo or ticks", CATEGORY_WORKFLOW, TEMPO_OR_TICKS_OPTIONS, TEMPO_OR_TICKS_OPTIONS[0]);
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
     * Get the assignable function.
     *
     * @param index The index of the assignable
     * @return The function
     */
    public int getAssignable (final int index)
    {
        return this.assignableFunctions[index];
    }
}
