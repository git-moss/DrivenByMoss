// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.configuration;

import de.mossgrabers.framework.controller.ValueChanger;
import de.mossgrabers.framework.scale.Scale;
import de.mossgrabers.framework.scale.ScaleLayout;
import de.mossgrabers.framework.scale.Scales;

import com.bitwig.extension.controller.api.Preferences;
import com.bitwig.extension.controller.api.SettableEnumValue;
import com.bitwig.extension.controller.api.SettableRangedValue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * Abstract base class for extension settings.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractConfiguration implements Configuration
{
    /** ID for scale setting. */
    public static final Integer   SCALES_SCALE                      = 0;
    /** ID for scale base note setting. */
    public static final Integer   SCALES_BASE                       = 1;
    /** ID for scale in-key setting. */
    public static final Integer   SCALES_IN_KEY                     = 2;
    /** ID for scale layout setting. */
    public static final Integer   SCALES_LAYOUT                     = 3;
    /** ID for enabling VU meters setting. */
    public static final Integer   ENABLE_VU_METERS                  = 4;
    /** ID for behaviour on stop setting. */
    public static final Integer   BEHAVIOUR_ON_STOP                 = 5;
    /** ID for displaying the crossfader in tracks setting. */
    public static final Integer   DISPLAY_CROSSFADER                = 6;
    /** ID for flipping the session grid setting. */
    public static final Integer   FLIP_SESSION                      = 7;
    /** ID for locking the flip the session grid setting. */
    public static final Integer   LOCK_FLIP_SESSION                 = 8;
    /** ID for selecting the clip on launch setting. */
    public static final Integer   SELECT_CLIP_ON_LAUNCH             = 9;
    /** ID for drawing record stripes setting. */
    public static final Integer   DRAW_RECORD_STRIPE                = 10;
    /** ID for converting the aftertouch data setting. */
    public static final Integer   CONVERT_AFTERTOUCH                = 11;
    /** ID for activating the fixed accent setting. */
    public static final Integer   ACTIVATE_FIXED_ACCENT             = 12;
    /** ID for the value of the fixed accent setting. */
    public static final Integer   FIXED_ACCENT_VALUE                = 13;
    /** ID for the quantize amount setting. */
    public static final Integer   QUANTIZE_AMOUNT                   = 14;
    /** ID for the flip recording setting. */
    public static final Integer   FLIP_RECORD                       = 15;
    /** Setting for automatic selecting the drum channel. */
    public static final Integer   AUTO_SELECT_DRUM                  = 16;
    /** Setting for new clip length. */
    public static final Integer   NEW_CLIP_LENGTH                   = 17;
    /** Setting for turning off empty drum pads (otherwise orange). */
    public static final Integer   TURN_OFF_EMPTY_DRUM_PADS          = 18;
    /** Setting for action for rec armed pad. */
    public static final Integer   ACTION_FOR_REC_ARMED_PAD          = 19;
    /** Setting for the footswitch functionality. */
    public static final Integer   FOOTSWITCH_2                      = 20;
    /** Setting for displaying browser column 1. */
    public static final Integer   BROWSER_DISPLAY_FILTER1           = 21;
    /** Setting for displaying browser column 2. */
    public static final Integer   BROWSER_DISPLAY_FILTER2           = 22;
    /** Setting for displaying browser column 3. */
    public static final Integer   BROWSER_DISPLAY_FILTER3           = 23;
    /** Setting for displaying browser column 4. */
    public static final Integer   BROWSER_DISPLAY_FILTER4           = 24;
    /** Setting for displaying browser column 5. */
    public static final Integer   BROWSER_DISPLAY_FILTER5           = 25;
    /** Setting for displaying browser column 6. */
    public static final Integer   BROWSER_DISPLAY_FILTER6           = 26;
    /** Setting for displaying browser column 7. */
    public static final Integer   BROWSER_DISPLAY_FILTER7           = 27;
    /** Setting for displaying browser column 8. */
    public static final Integer   BROWSER_DISPLAY_FILTER8           = 28;

    protected static final String CATEGORY_DRUMS                    = "Drum Sequencer";
    protected static final String CATEGORY_SCALES                   = "Scales";
    protected static final String CATEGORY_SESSION                  = "Session";
    protected static final String CATEGORY_TRANSPORT                = "Transport";
    protected static final String CATEGORY_WORKFLOW                 = "Workflow";
    protected static final String CATEGORY_PADS                     = "Pads";
    protected static final String CATEGORY_PLAY_AND_SEQUENCE        = "Play and Sequence";
    protected static final String CATEGORY_HARDWARE_SETUP           = "Hardware Setup";

    private static final String   SCALE_IN_KEY                      = "In Key";
    private static final String   SCALE_CHROMATIC                   = "Chromatic";

    /** Use footswitch 2 for toggling play. */
    public static final int       FOOTSWITCH_2_TOGGLE_PLAY          = 0;
    /** Use footswitch 2 for toggling record. */
    public static final int       FOOTSWITCH_2_TOGGLE_RECORD        = 1;
    /** Use footswitch 2 for stopping all clips. */
    public static final int       FOOTSWITCH_2_STOP_ALL_CLIPS       = 2;
    /** Use footswitch 2 for toggling clip overdub. */
    public static final int       FOOTSWITCH_2_TOGGLE_CLIP_OVERDUB  = 3;
    /** Use footswitch 2 for undo. */
    public static final int       FOOTSWITCH_2_UNDO                 = 4;
    /** Use footswitch 2 for tapping tempo. */
    public static final int       FOOTSWITCH_2_TAP_TEMPO            = 5;
    /** Use footswitch 2 as the new button. */
    public static final int       FOOTSWITCH_2_NEW_BUTTON           = 6;
    /** Use footswitch 2 as clip based looper. */
    public static final int       FOOTSWITCH_2_CLIP_BASED_LOOPER    = 7;
    /** Use footswitch 2 to trigger the arrange layout. */
    public static final int       FOOTSWITCH_2_PANEL_LAYOUT_ARRANGE = 8;
    /** Use footswitch 2 to trigger the mix layout. */
    public static final int       FOOTSWITCH_2_PANEL_LAYOUT_MIX     = 9;
    /** Use footswitch 2 to trigger the edit layout. */
    public static final int       FOOTSWITCH_2_PANEL_LAYOUT_EDIT    = 10;
    /** Use footswitch 2 to add a new instrument track. */
    public static final int       FOOTSWITCH_2_ADD_INSTRUMENT_TRACK = 11;
    /** Use footswitch 2 to add a new audio track. */
    public static final int       FOOTSWITCH_2_ADD_AUDIO_TRACK      = 12;
    /** Use footswitch 2 to add a new effect track. */
    public static final int       FOOTSWITCH_2_ADD_EFFECT_TRACK     = 13;

    /** The behaviour when the stop button is pressed. */
    public enum BehaviourOnStop
    {
        /** Keep the play cursor at the current position on stop. */
        MOVE_PLAY_CURSOR,
        /** Move the cursor back to zero on stop. */
        RETURN_TO_ZERO,
        /** Only pause on stop. */
        PAUSE
    }

    private static final String [] AFTERTOUCH_CONVERSION_VALUES = new String [131];
    static
    {
        AFTERTOUCH_CONVERSION_VALUES[0] = "Off";
        AFTERTOUCH_CONVERSION_VALUES[1] = "Poly Aftertouch";
        AFTERTOUCH_CONVERSION_VALUES[2] = "Channel Aftertouch";
        for (int i = 0; i < 128; i++)
            AFTERTOUCH_CONVERSION_VALUES[3 + i] = "CC " + i;
    }

    /** The names for clip lengths. */
    public static final String []                    NEW_CLIP_LENGTH_VALUES      =
    {
        "1 Beat",
        "2 Beat",
        "1 Bar",
        "2 Bars",
        "4 Bars",
        "8 Bars",
        "16 Bars",
        "32 Bars"
    };

    private static final String []                   BEHAVIOUR_ON_STOP_VALUES    =
    {
        "Move play cursor",
        "Return to Zero",
        "Pause"
    };

    private static final String []                   ACTIONS_REC_ARMED_PADS      =
    {
        "Start recording",
        "Create new clip",
        "Do nothing"
    };

    protected static final String []                 FOOTSWITCH_VALUES           =
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
        "Add effect track"
    };

    private static final String []                   BROWSER_FILTER_COLUMN_NAMES =
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

    private static final String []                   COLUMN_VALUES               =
    {
        "Hide",
        "Show"
    };

    protected static final String []                 ON_OFF_OPTIONS              =
    {
        "Off",
        "On"
    };

    private SettableEnumValue                        scaleBaseSetting;
    private SettableEnumValue                        scaleInKeySetting;
    private SettableEnumValue                        scaleLayoutSetting;
    private SettableEnumValue                        scaleSetting;
    private SettableEnumValue                        enableVUMetersSetting;
    private SettableEnumValue                        displayCrossfaderSetting;
    private SettableEnumValue                        flipSessionSetting;
    private SettableEnumValue                        lockFlipSessionSetting;
    private SettableEnumValue                        accentActiveSetting;
    private SettableRangedValue                      accentValueSetting;
    private SettableRangedValue                      quantizeAmountSetting;
    private SettableEnumValue                        newClipLengthSetting;

    private final Map<Integer, Set<SettingObserver>> observers                   = new HashMap<> ();
    protected ValueChanger                           valueChanger;

    private String                                   scale                       = "Major";
    private String                                   scaleBase                   = "C";
    private boolean                                  scaleInKey                  = true;
    private String                                   scaleLayout                 = "4th ^";
    private boolean                                  enableVUMeters              = false;
    private BehaviourOnStop                          behaviourOnStop             = BehaviourOnStop.MOVE_PLAY_CURSOR;
    private boolean                                  displayCrossfader           = true;
    private boolean                                  flipSession                 = false;
    private boolean                                  lockFlipSession             = false;
    private boolean                                  selectClipOnLaunch          = true;
    private boolean                                  drawRecordStripe            = true;
    private int                                      convertAftertouch           = 0;
    /** Accent button active. */
    private boolean                                  accentActive                = false;
    /** Fixed velocity value for accent. */
    private int                                      fixedAccentValue            = 127;
    private int                                      quantizeAmount              = 1;
    private boolean                                  flipRecord                  = false;
    private int                                      newClipLength               = 2;
    private boolean                                  autoSelectDrum              = false;
    private boolean                                  turnOffEmptyDrumPads        = false;
    private int                                      actionForRecArmedPad        = 0;
    private int                                      footswitch2                 = FOOTSWITCH_2_NEW_BUTTON;
    private boolean []                               browserDisplayFilter        =
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


    /**
     * Constructor.
     *
     * @param valueChanger The value changer
     */
    public AbstractConfiguration (final ValueChanger valueChanger)
    {
        this.valueChanger = valueChanger;
    }


    /** {@inheritDoc} */
    @Override
    public void addSettingObserver (final Integer settingID, final SettingObserver observer)
    {
        Set<SettingObserver> settingObservers = this.observers.get (settingID);
        if (settingObservers == null)
        {
            settingObservers = new HashSet<> ();
            this.observers.put (settingID, settingObservers);
        }
        settingObservers.add (observer);
    }


    /** {@inheritDoc} */
    @Override
    public void clearSettingObservers ()
    {
        this.observers.clear ();
    }


    /**
     * Set the scale by name.
     *
     * @param scale The name of a scale
     */
    public void setScale (final String scale)
    {
        this.scaleSetting.set (scale);
    }


    /** {@inheritDoc} */
    @Override
    public String getScale ()
    {
        return this.scale;
    }


    /**
     * Set the scale base note by name.
     *
     * @param scaleBase The name of a scale base note
     */
    public void setScaleBase (final String scaleBase)
    {
        this.scaleBaseSetting.set (scaleBase);
    }


    /** {@inheritDoc} */
    @Override
    public String getScaleBase ()
    {
        return this.scaleBase;
    }


    /**
     * Set the in-scale setting.
     *
     * @param inScale True if scale otherwise chromatic
     */
    public void setScaleInKey (final boolean inScale)
    {
        this.scaleInKeySetting.set (inScale ? SCALE_IN_KEY : SCALE_CHROMATIC);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isScaleInKey ()
    {
        return this.scaleInKey;
    }


    /**
     * Set the scale layout.
     *
     * @param scaleLayout The scale layout
     */
    public void setScaleLayout (final String scaleLayout)
    {
        this.scaleLayoutSetting.set (scaleLayout);
    }


    /** {@inheritDoc} */
    @Override
    public String getScaleLayout ()
    {
        return this.scaleLayout;
    }


    /** {@inheritDoc} */
    @Override
    public void setVUMetersEnabled (final boolean enabled)
    {
        this.setOnOffSetting (this.enableVUMetersSetting, enabled);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isEnableVUMeters ()
    {
        return this.enableVUMeters;
    }


    /**
     * Set the display crossfader setting.
     *
     * @param enabled True if visible
     */
    public void setDisplayCrossfader (final boolean enabled)
    {
        this.setOnOffSetting (this.displayCrossfaderSetting, enabled);
    }


    /** {@inheritDoc} */
    @Override
    public void setFlipSession (final boolean enabled)
    {
        this.setOnOffSetting (this.flipSessionSetting, enabled);
    }


    /** {@inheritDoc} */
    @Override
    public void setAccentEnabled (final boolean enabled)
    {
        this.setOnOffSetting (this.accentActiveSetting, enabled);
    }


    /** {@inheritDoc} */
    @Override
    public void setAccentValue (final double value)
    {
        this.accentValueSetting.setRaw (value);
    }


    /**
     * Change the quantize amount.
     *
     * @param control The change value
     */
    public void changeQuantizeAmount (final int control)
    {
        this.quantizeAmountSetting.setRaw (this.valueChanger.changeIntValue (control, this.quantizeAmount, 1, 101));
    }


    /**
     * Sets the quantize amount.
     *
     * @param value The amount (0 - 1)
     */
    public void setQuantizeAmount (final double value)
    {
        this.quantizeAmountSetting.setRaw (value);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isSelectClipOnLaunch ()
    {
        return this.selectClipOnLaunch;
    }


    /**
     * Sets an on/off setting.
     *
     * @param setting The setting
     * @param enabled On or off
     */
    protected void setOnOffSetting (final SettableEnumValue setting, final boolean enabled)
    {
        setting.set (enabled ? ON_OFF_OPTIONS[1] : ON_OFF_OPTIONS[0]);
    }


    /** {@inheritDoc} */
    @Override
    public BehaviourOnStop getBehaviourOnStop ()
    {
        return this.behaviourOnStop;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isDisplayCrossfader ()
    {
        return this.displayCrossfader;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isFlipSession ()
    {
        return this.flipSession;
    }


    /** {@inheritDoc} */
    @Override
    public int getConvertAftertouch ()
    {
        return this.convertAftertouch;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isAccentActive ()
    {
        return this.accentActive;
    }


    /** {@inheritDoc} */
    @Override
    public int getFixedAccentValue ()
    {
        return this.fixedAccentValue;
    }


    /** {@inheritDoc} */
    @Override
    public int getQuantizeAmount ()
    {
        return this.quantizeAmount;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isFlipRecord ()
    {
        return this.flipRecord;
    }


    /** {@inheritDoc} */
    @Override
    public int getNewClipLength ()
    {
        return this.newClipLength;
    }


    /** {@inheritDoc} */
    @Override
    public void setNewClipLength (final int value)
    {
        this.newClipLengthSetting.set (NEW_CLIP_LENGTH_VALUES[value]);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isAutoSelectDrum ()
    {
        return this.autoSelectDrum;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isTurnOffEmptyDrumPads ()
    {
        return this.turnOffEmptyDrumPads;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isLockFlipSession ()
    {
        return this.lockFlipSession;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isDrawRecordStripe ()
    {
        return this.drawRecordStripe;
    }


    /** {@inheritDoc} */
    @Override
    public int getActionForRecArmedPad ()
    {
        return this.actionForRecArmedPad;
    }


    /** {@inheritDoc} */
    @Override
    public int getFootswitch2 ()
    {
        return this.footswitch2;
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
     * Activate the scale setting.
     *
     * @param prefs The preferences
     */
    protected void activateScaleSetting (final Preferences prefs)
    {
        final String [] scaleNames = Scale.getNames ();
        this.scaleSetting = prefs.getEnumSetting ("Scale", CATEGORY_SCALES, scaleNames, scaleNames[0]);
        this.scaleSetting.addValueObserver (value -> {
            this.scale = value;
            this.notifyObservers (AbstractConfiguration.SCALES_SCALE);
        });
    }


    /**
     * Activate the scale base note setting.
     *
     * @param prefs The preferences
     */
    protected void activateScaleBaseSetting (final Preferences prefs)
    {
        this.scaleBaseSetting = prefs.getEnumSetting ("Base", CATEGORY_SCALES, Scales.BASES, Scales.BASES[0]);
        this.scaleBaseSetting.addValueObserver (value -> {
            this.scaleBase = value;
            this.notifyObservers (SCALES_BASE);
        });
    }


    /**
     * Activate the scale in-scale setting.
     *
     * @param prefs The preferences
     */
    protected void activateScaleInScaleSetting (final Preferences prefs)
    {
        this.scaleInKeySetting = prefs.getEnumSetting (SCALE_IN_KEY, CATEGORY_SCALES, new String []
        {
            SCALE_IN_KEY,
            SCALE_CHROMATIC
        }, SCALE_IN_KEY);
        this.scaleInKeySetting.addValueObserver (value -> {
            this.scaleInKey = SCALE_IN_KEY.equals (value);
            this.notifyObservers (AbstractConfiguration.SCALES_IN_KEY);
        });
    }


    /**
     * Activate the scale layout setting.
     *
     * @param prefs The preferences
     */
    protected void activateScaleLayoutSetting (final Preferences prefs)
    {
        final String [] names = ScaleLayout.getNames ();
        this.scaleLayoutSetting = prefs.getEnumSetting ("Layout", CATEGORY_SCALES, names, names[0]);
        this.scaleLayoutSetting.addValueObserver (value -> {
            this.scaleLayout = value;
            this.notifyObservers (AbstractConfiguration.SCALES_LAYOUT);
        });
    }


    /**
     * Activate the VU meters setting.
     *
     * @param prefs The preferences
     */
    protected void activateEnableVUMetersSetting (final Preferences prefs)
    {
        this.activateEnableVUMetersSetting (prefs, CATEGORY_WORKFLOW);
    }


    /**
     * Activate the VU meters setting.
     *
     * @param prefs The preferences
     * @param category The name for the category
     */
    protected void activateEnableVUMetersSetting (final Preferences prefs, final String category)
    {
        this.enableVUMetersSetting = prefs.getEnumSetting ("VU Meters", category, ON_OFF_OPTIONS, ON_OFF_OPTIONS[1]);
        this.enableVUMetersSetting.addValueObserver (value -> {
            this.enableVUMeters = "On".equals (value);
            this.notifyObservers (AbstractConfiguration.ENABLE_VU_METERS);
        });
    }


    /**
     * Activate the behaviour on stop setting.
     *
     * @param prefs The preferences
     */
    protected void activateBehaviourOnStopSetting (final Preferences prefs)
    {
        final SettableEnumValue behaviourOnStopSetting = prefs.getEnumSetting ("Behaviour on Stop", CATEGORY_TRANSPORT, BEHAVIOUR_ON_STOP_VALUES, BEHAVIOUR_ON_STOP_VALUES[0]);
        behaviourOnStopSetting.addValueObserver (value -> {
            for (int i = 0; i < BEHAVIOUR_ON_STOP_VALUES.length; i++)
            {
                if (BEHAVIOUR_ON_STOP_VALUES[i].equals (value))
                    this.behaviourOnStop = BehaviourOnStop.values ()[i];
            }
            this.notifyObservers (BEHAVIOUR_ON_STOP);
        });
    }


    /**
     * Activate the display crossfader setting.
     *
     * @param prefs The preferences
     */
    protected void activateDisplayCrossfaderSetting (final Preferences prefs)
    {
        this.displayCrossfaderSetting = prefs.getEnumSetting ("Display Crossfader on Track", CATEGORY_WORKFLOW, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        this.displayCrossfaderSetting.addValueObserver (value -> {
            this.displayCrossfader = "On".equals (value);
            this.notifyObservers (AbstractConfiguration.DISPLAY_CROSSFADER);
        });
    }


    /**
     * Activate the flip session setting.
     *
     * @param prefs The preferences
     */
    protected void activateFlipSessionSetting (final Preferences prefs)
    {
        this.flipSessionSetting = prefs.getEnumSetting ("Flip Session", CATEGORY_SESSION, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        this.flipSessionSetting.addValueObserver (value -> {
            this.flipSession = "On".equals (value);
            this.notifyObservers (AbstractConfiguration.FLIP_SESSION);
        });
    }


    /**
     * Activate the lock flip session setting.
     *
     * @param prefs The preferences
     */
    protected void activateLockFlipSessionSetting (final Preferences prefs)
    {
        this.lockFlipSessionSetting = prefs.getEnumSetting ("Lock flip Session", CATEGORY_SESSION, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        this.lockFlipSessionSetting.addValueObserver (value -> {
            this.lockFlipSession = "On".equals (value);
            this.notifyObservers (AbstractConfiguration.LOCK_FLIP_SESSION);
        });
    }


    /**
     * Activate the select clip on launch setting.
     *
     * @param prefs The preferences
     */
    protected void activateSelectClipOnLaunchSetting (final Preferences prefs)
    {
        final SettableEnumValue selectClipOnLaunchSetting = prefs.getEnumSetting ("Select clip on launch", CATEGORY_SESSION, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        selectClipOnLaunchSetting.addValueObserver (value -> {
            this.selectClipOnLaunch = "On".equals (value);
            this.notifyObservers (AbstractConfiguration.SELECT_CLIP_ON_LAUNCH);
        });
    }


    /**
     * Activate the draw record stripe setting.
     *
     * @param prefs The preferences
     */
    protected void activateDrawRecordStripeSetting (final Preferences prefs)
    {
        final SettableEnumValue drawRecordStripeSetting = prefs.getEnumSetting ("Display clips of record enabled tracks in red", CATEGORY_SESSION, ON_OFF_OPTIONS, ON_OFF_OPTIONS[1]);
        drawRecordStripeSetting.addValueObserver (value -> {
            this.drawRecordStripe = "On".equals (value);
            this.notifyObservers (AbstractConfiguration.DRAW_RECORD_STRIPE);
        });
    }


    /**
     * Activate action for rec armed pad setting.
     *
     * @param prefs The preferences
     */
    protected void activateActionForRecArmedPad (final Preferences prefs)
    {
        final SettableEnumValue actionForRecArmedPadSetting = prefs.getEnumSetting ("Action for pressing rec armed empty clip", CATEGORY_SESSION, ACTIONS_REC_ARMED_PADS, ACTIONS_REC_ARMED_PADS[0]);
        actionForRecArmedPadSetting.addValueObserver (value -> {
            for (int i = 0; i < ACTIONS_REC_ARMED_PADS.length; i++)
            {
                if (ACTIONS_REC_ARMED_PADS[i].equals (value))
                    this.actionForRecArmedPad = i;
            }
            this.notifyObservers (AbstractConfiguration.ACTION_FOR_REC_ARMED_PAD);
        });
    }


    /**
     * Activate the convert aftertouch setting.
     *
     * @param prefs The preferences
     */
    protected void activateConvertAftertouchSetting (final Preferences prefs)
    {
        final SettableEnumValue convertAftertouchSetting = prefs.getEnumSetting ("Convert Poly Aftertouch to", CATEGORY_PADS, AbstractConfiguration.AFTERTOUCH_CONVERSION_VALUES, AbstractConfiguration.AFTERTOUCH_CONVERSION_VALUES[1]);
        convertAftertouchSetting.addValueObserver (value -> {
            for (int i = 0; i < AbstractConfiguration.AFTERTOUCH_CONVERSION_VALUES.length; i++)
            {
                if (AbstractConfiguration.AFTERTOUCH_CONVERSION_VALUES[i].equals (value))
                {
                    this.convertAftertouch = i - 3;
                    break;
                }
            }
            this.notifyObservers (AbstractConfiguration.CONVERT_AFTERTOUCH);
        });
    }


    /**
     * Activate the accent active setting.
     *
     * @param prefs The preferences
     */
    protected void activateAccentActiveSetting (final Preferences prefs)
    {
        this.accentActiveSetting = prefs.getEnumSetting ("Activate Fixed Accent", CATEGORY_PLAY_AND_SEQUENCE, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        this.accentActiveSetting.addValueObserver (value -> {
            this.accentActive = "On".equals (value);
            this.notifyObservers (AbstractConfiguration.ACTIVATE_FIXED_ACCENT);
        });
    }


    /**
     * Activate the accent value setting.
     *
     * @param prefs The preferences
     */
    protected void activateAccentValueSetting (final Preferences prefs)
    {
        this.accentValueSetting = prefs.getNumberSetting ("Fixed Accent Value", CATEGORY_PLAY_AND_SEQUENCE, 1, 127, 1, "", 127);
        this.accentValueSetting.addValueObserver (127, value -> {
            this.fixedAccentValue = value + 1;
            this.notifyObservers (AbstractConfiguration.FIXED_ACCENT_VALUE);
        });
    }


    /**
     * Activate the accent value setting.
     *
     * @param prefs The preferences
     */
    protected void activateFlipRecordSetting (final Preferences prefs)
    {
        final SettableEnumValue flipRecordSetting = prefs.getEnumSetting ("Flip arranger and clip record / automation", CATEGORY_TRANSPORT, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        flipRecordSetting.addValueObserver (value -> {
            this.flipRecord = "On".equals (value);
            this.notifyObservers (FLIP_RECORD);
        });
    }


    /**
     * Activate the accent value setting.
     *
     * @param prefs The preferences
     */
    protected void activateNewClipLengthSetting (final Preferences prefs)
    {
        this.newClipLengthSetting = prefs.getEnumSetting ("New Clip Length", CATEGORY_WORKFLOW, NEW_CLIP_LENGTH_VALUES, NEW_CLIP_LENGTH_VALUES[2]);
        this.newClipLengthSetting.addValueObserver (value -> {
            for (int i = 0; i < NEW_CLIP_LENGTH_VALUES.length; i++)
            {
                if (NEW_CLIP_LENGTH_VALUES[i].equals (value))
                    this.newClipLength = i;
            }
            this.notifyObservers (NEW_CLIP_LENGTH);
        });
    }


    /**
     * Activate the quantize amount setting.
     *
     * @param prefs The preferences
     */
    protected void activateQuantizeAmountSetting (final Preferences prefs)
    {
        this.quantizeAmountSetting = prefs.getNumberSetting ("Quantize Amount", CATEGORY_PLAY_AND_SEQUENCE, 1, 100, 1, "%", 100);
        this.quantizeAmountSetting.addValueObserver (100, value -> {
            this.quantizeAmount = value + 1;
            this.notifyObservers (QUANTIZE_AMOUNT);
        });
    }


    /**
     * Activate the auto select drum setting.
     *
     * @param prefs The preferences
     */
    protected void activateAutoSelectDrumSetting (final Preferences prefs)
    {
        final SettableEnumValue autoSelectDrumSetting = prefs.getEnumSetting ("Auto-select drum settings", CATEGORY_DRUMS, new String []
        {
            "Off",
            "Channel"
        }, "Off");
        autoSelectDrumSetting.addValueObserver (value -> {
            this.autoSelectDrum = "Channel".equals (value);
            this.notifyObservers (AUTO_SELECT_DRUM);
        });
    }


    /**
     * Activate the turn off empty drum pads setting.
     *
     * @param prefs The preferences
     */
    protected void activateTurnOffEmptyDrumPadsSetting (final Preferences prefs)
    {
        final SettableEnumValue turnOffEmptyDrumPadsSetting = prefs.getEnumSetting ("Turn off empty drum pads", CATEGORY_DRUMS, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        turnOffEmptyDrumPadsSetting.addValueObserver (value -> {
            this.turnOffEmptyDrumPads = "On".equals (value);
            this.notifyObservers (TURN_OFF_EMPTY_DRUM_PADS);
        });
    }


    /**
     * Activate the footswitch setting.
     *
     * @param prefs The preferences
     */
    protected void activateFootswitchSetting (final Preferences prefs)
    {
        final SettableEnumValue footswitch2Setting = prefs.getEnumSetting ("Footswitch 2", CATEGORY_WORKFLOW, FOOTSWITCH_VALUES, FOOTSWITCH_VALUES[6]);
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
     * Activate the browser settings.
     *
     * @param prefs The preferences
     */
    protected void activateBrowserSettings (final Preferences prefs)
    {
        for (int i = 0; i < BROWSER_FILTER_COLUMN_NAMES.length; i++)
        {
            final SettableEnumValue browserDisplayFilterSetting = prefs.getEnumSetting (BROWSER_FILTER_COLUMN_NAMES[i], "Browser", COLUMN_VALUES, COLUMN_VALUES[1]);
            final int index = i;
            browserDisplayFilterSetting.addValueObserver (value -> {
                this.browserDisplayFilter[index] = COLUMN_VALUES[1].equals (value);
                this.notifyObservers (BROWSER_DISPLAY_FILTER1.intValue() + index);
            });
        }
    }


    /**
     * Notify all observers about the change of a setting.
     *
     * @param settingID The ID of the setting, which has changed
     */
    protected void notifyObservers (final Integer settingID)
    {
        final Set<SettingObserver> set = this.observers.get (settingID);
        if (set != null)
            set.forEach (SettingObserver::call);
    }
}
