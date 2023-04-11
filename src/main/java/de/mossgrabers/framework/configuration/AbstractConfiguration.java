// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.configuration;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.constants.Capability;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.IDeviceMetadata;
import de.mossgrabers.framework.daw.data.bank.IDeviceBank;
import de.mossgrabers.framework.daw.data.bank.IDrumPadBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.daw.midi.ArpeggiatorMode;
import de.mossgrabers.framework.observer.ISettingObserver;
import de.mossgrabers.framework.scale.Scale;
import de.mossgrabers.framework.scale.ScaleLayout;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.view.Views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Abstract base class for extension settings.
 *
 * @author Jürgen Moßgraber
 */
public abstract class AbstractConfiguration implements Configuration
{
    /** ID for scale setting. */
    public static final Integer      SCALES_SCALE                    = Integer.valueOf (0);
    /** ID for scale base note setting. */
    public static final Integer      SCALES_BASE                     = Integer.valueOf (1);
    /** ID for scale in-key setting. */
    public static final Integer      SCALES_IN_KEY                   = Integer.valueOf (2);
    /** ID for scale layout setting. */
    public static final Integer      SCALES_LAYOUT                   = Integer.valueOf (3);
    /** ID for enabling VU meters setting. */
    public static final Integer      ENABLE_VU_METERS                = Integer.valueOf (4);
    /** ID for behavior on stop setting. */
    public static final Integer      BEHAVIOUR_ON_STOP               = Integer.valueOf (5);
    /** ID for behavior on pause setting. */
    public static final Integer      BEHAVIOUR_ON_PAUSE              = Integer.valueOf (6);
    /** ID for flipping the session grid setting. */
    public static final Integer      FLIP_SESSION                    = Integer.valueOf (7);
    /** ID for selecting the clip on launch setting. */
    public static final Integer      SELECT_CLIP_ON_LAUNCH           = Integer.valueOf (8);
    /** ID for drawing record stripes setting. */
    public static final Integer      DRAW_RECORD_STRIPE              = Integer.valueOf (9);
    /** ID for converting the aftertouch data setting. */
    public static final Integer      CONVERT_AFTERTOUCH              = Integer.valueOf (10);
    /** ID for activating the fixed accent setting. */
    public static final Integer      ACTIVATE_FIXED_ACCENT           = Integer.valueOf (11);
    /** ID for the value of the fixed accent setting. */
    public static final Integer      FIXED_ACCENT_VALUE              = Integer.valueOf (12);
    /** ID for the quantize amount setting. */
    public static final Integer      QUANTIZE_AMOUNT                 = Integer.valueOf (13);
    /** ID for the flip recording setting. */
    public static final Integer      FLIP_RECORD                     = Integer.valueOf (14);
    /** Setting for new clip length. */
    public static final Integer      NEW_CLIP_LENGTH                 = Integer.valueOf (15);
    /** Setting for automatic selecting the drum channel. */
    public static final Integer      AUTO_SELECT_DRUM                = Integer.valueOf (16);
    /** Setting for turning off empty drum pads (otherwise orange). */
    public static final Integer      TURN_OFF_EMPTY_DRUM_PADS        = Integer.valueOf (17);
    /** Setting for sounding drum pads with or without Select combination. */
    public static final Integer      SOUND_DRUM_PADS                 = Integer.valueOf (18);
    /** Setting for action for record armed pad. */
    public static final Integer      ACTION_FOR_REC_ARMED_PAD        = Integer.valueOf (19);
    /** Setting for displaying browser column 1. */
    public static final Integer      BROWSER_DISPLAY_FILTER1         = Integer.valueOf (20);
    /** Setting for displaying browser column 2. */
    public static final Integer      BROWSER_DISPLAY_FILTER2         = Integer.valueOf (21);
    /** Setting for displaying browser column 3. */
    public static final Integer      BROWSER_DISPLAY_FILTER3         = Integer.valueOf (22);
    /** Setting for displaying browser column 4. */
    public static final Integer      BROWSER_DISPLAY_FILTER4         = Integer.valueOf (23);
    /** Setting for displaying browser column 5. */
    public static final Integer      BROWSER_DISPLAY_FILTER5         = Integer.valueOf (24);
    /** Setting for displaying browser column 6. */
    public static final Integer      BROWSER_DISPLAY_FILTER6         = Integer.valueOf (25);
    /** Setting for displaying browser column 7. */
    public static final Integer      BROWSER_DISPLAY_FILTER7         = Integer.valueOf (26);
    /** Setting for displaying browser column 8. */
    public static final Integer      BROWSER_DISPLAY_FILTER8         = Integer.valueOf (27);
    /** The speed of a knob. */
    public static final Integer      KNOB_SENSITIVITY_DEFAULT        = Integer.valueOf (28);
    /** The speed of a knob in slow mode. */
    public static final Integer      KNOB_SENSITIVITY_SLOW           = Integer.valueOf (29);
    /** Turn note repeat on/off. */
    public static final Integer      NOTEREPEAT_ACTIVE               = Integer.valueOf (30);
    /** The note repeat period. */
    public static final Integer      NOTEREPEAT_PERIOD               = Integer.valueOf (31);
    /** The note repeat length. */
    public static final Integer      NOTEREPEAT_LENGTH               = Integer.valueOf (32);
    /** The note repeat mode. */
    public static final Integer      NOTEREPEAT_MODE                 = Integer.valueOf (33);
    /** The note repeat octave. */
    public static final Integer      NOTEREPEAT_OCTAVE               = Integer.valueOf (34);
    /** The MIDI channel to use for editing sequencer notes. */
    public static final Integer      MIDI_EDIT_CHANNEL               = Integer.valueOf (35);
    /** Setting for excluding deactivated tracks. */
    public static final Integer      EXCLUDE_DEACTIVATED_ITEMS       = Integer.valueOf (36);
    /** Setting for different record button functions. */
    public static final Integer      RECORD_BUTTON_FUNCTION          = Integer.valueOf (37);
    /** Setting for different record button functions in combination with shift. */
    public static final Integer      SHIFTED_RECORD_BUTTON_FUNCTION  = Integer.valueOf (38);
    /** Show tracks hierarchical (instead of flat) if enabled. */
    public static final Integer      HIERARCHICAL_TRACKS             = Integer.valueOf (39);
    /** Setting for the footswitch functionality. */
    public static final Integer      FOOTSWITCH_1                    = Integer.valueOf (40);
    /** Setting for the footswitch functionality. */
    public static final Integer      FOOTSWITCH_2                    = Integer.valueOf (41);
    /** Setting for the footswitch functionality. */
    public static final Integer      FOOTSWITCH_3                    = Integer.valueOf (42);
    /** Setting for the footswitch functionality. */
    public static final Integer      FOOTSWITCH_4                    = Integer.valueOf (43);
    /** Preferred note view. */
    public static final Integer      PREFERRED_NOTE_VIEW             = Integer.valueOf (44);
    /** Start with session view if active. */
    public static final Integer      START_WITH_SESSION_VIEW         = Integer.valueOf (45);

    // Implementation IDs start at 50

    protected static final String    CATEGORY_DRUMS                  = "Drum Sequencer";
    protected static final String    CATEGORY_SCALES                 = "Scales";
    protected static final String    CATEGORY_SESSION                = "Session";
    protected static final String    CATEGORY_TRANSPORT              = "Transport";
    protected static final String    CATEGORY_WORKFLOW               = "Workflow";
    protected static final String    CATEGORY_PADS                   = "Pads";
    protected static final String    CATEGORY_PLAY_AND_SEQUENCE      = "Play and Sequence";
    protected static final String    CATEGORY_HARDWARE_SETUP         = "Hardware Setup";
    protected static final String    CATEGORY_DEBUG                  = "Debug";
    protected static final String    CATEGORY_NOTEREPEAT             = "Note Repeat";
    private static final String      CATEGORY_FAV_DEVICES            = "Add Track - favorite devices";

    private static final String      SCALE_IN_KEY                    = "In Key";
    private static final String      SCALE_CHROMATIC                 = "Chromatic";

    /** Use footswitch for toggling play. */
    public static final int          FOOTSWITCH_TOGGLE_PLAY          = 0;
    /** Use footswitch for toggling record. */
    public static final int          FOOTSWITCH_TOGGLE_RECORD        = 1;
    /** Use footswitch for stopping all clips. */
    public static final int          FOOTSWITCH_STOP_ALL_CLIPS       = 2;
    /** Use footswitch for toggling clip overdub. */
    public static final int          FOOTSWITCH_TOGGLE_CLIP_OVERDUB  = 3;
    /** Use footswitch for undo. */
    public static final int          FOOTSWITCH_UNDO                 = 4;
    /** Use footswitch for tapping tempo. */
    public static final int          FOOTSWITCH_TAP_TEMPO            = 5;
    /** Use footswitch as the new button. */
    public static final int          FOOTSWITCH_NEW_BUTTON           = 6;
    /** Use footswitch as clip based looper. */
    public static final int          FOOTSWITCH_CLIP_BASED_LOOPER    = 7;
    /** Use footswitch to trigger the arrange layout. */
    public static final int          FOOTSWITCH_PANEL_LAYOUT_ARRANGE = 8;
    /** Use footswitch to trigger the mix layout. */
    public static final int          FOOTSWITCH_PANEL_LAYOUT_MIX     = 9;
    /** Use footswitch to trigger the edit layout. */
    public static final int          FOOTSWITCH_PANEL_LAYOUT_EDIT    = 10;
    /** Use footswitch to add a new instrument track. */
    public static final int          FOOTSWITCH_ADD_INSTRUMENT_TRACK = 11;
    /** Use footswitch to add a new audio track. */
    public static final int          FOOTSWITCH_ADD_AUDIO_TRACK      = 12;
    /** Use footswitch to add a new effect track. */
    public static final int          FOOTSWITCH_ADD_EFFECT_TRACK     = 13;
    /** Use footswitch to quantize the selected clip. */
    public static final int          FOOTSWITCH_QUANTIZE             = 14;
    /** Use footswitch as sustain pedal. */
    public static final int          FOOTSWITCH_SUSTAIN_PEDAL        = 15;
    // Note: There are controllers who extend this list!

    protected static final String [] OPTIONS_MIDI_CHANNEL            = new String [16];
    protected static final String [] KNOB_SENSITIVITY                = new String [201];
    static
    {
        for (int i = 0; i < OPTIONS_MIDI_CHANNEL.length; i++)
            OPTIONS_MIDI_CHANNEL[i] = Integer.toString (i + 1);

        for (int i = 0; i < 100; i++)
        {
            KNOB_SENSITIVITY[i] = "-" + (100 - i);
            KNOB_SENSITIVITY[101 + i] = "+" + (i + 1);
        }
        KNOB_SENSITIVITY[100] = "Normal";
    }

    protected static final ColorEx DEFAULT_COLOR_BACKGROUND         = ColorEx.fromRGB (83, 83, 83);
    protected static final ColorEx DEFAULT_COLOR_BORDER             = ColorEx.BLACK;
    protected static final ColorEx DEFAULT_COLOR_TEXT               = ColorEx.WHITE;
    protected static final ColorEx DEFAULT_COLOR_FADER              = ColorEx.fromRGB (69, 44, 19);
    protected static final ColorEx DEFAULT_COLOR_VU                 = ColorEx.GREEN;
    protected static final ColorEx DEFAULT_COLOR_EDIT               = ColorEx.fromRGB (240, 127, 17);
    protected static final ColorEx DEFAULT_COLOR_RECORD             = ColorEx.RED;
    protected static final ColorEx DEFAULT_COLOR_SOLO               = ColorEx.YELLOW;
    protected static final ColorEx DEFAULT_COLOR_MUTE               = ColorEx.fromRGB (245, 129, 17);
    protected static final ColorEx DEFAULT_COLOR_BACKGROUND_DARKER  = ColorEx.fromRGB (39, 39, 39);
    protected static final ColorEx DEFAULT_COLOR_BACKGROUND_LIGHTER = ColorEx.fromRGB (118, 118, 118);


    /** The behavior when the stop button is pressed. */
    public enum TransportBehavior
    {
        /** Keep the play cursor at the current position on stop. */
        STOP,
        /** Move the cursor back to zero on stop. */
        RETURN_TO_ZERO,
        /** Only pause on stop. */
        PAUSE
    }


    /** Aftertouch conversion is set to off. */
    public static final int        AFTERTOUCH_CONVERT_OFF       = -3;
    /** Aftertouch conversion is set to poly aftertouch. */
    public static final int        AFTERTOUCH_CONVERT_POLY      = -2;
    /** Aftertouch conversion is set to channel aftertouch. */
    public static final int        AFTERTOUCH_CONVERT_CHANNEL   = -1;

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
    protected static final String [] NEW_CLIP_LENGTH_VALUES      =
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

    private static final String []   TRANSPORT_BEHAVIOUR_VALUES  =
    {
        "Stop",
        "Return to Zero",
        "Pause"
    };

    private static final String []   ACTIONS_REC_ARMED_PADS      =
    {
        "Start recording",
        "Create new clip",
        "Do nothing"
    };

    protected static final String [] FOOTSWITCH_VALUES           =
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
        "Sustain Pedal"
    };

    private static final int []      FOOTSWITCH_DEFAULTS         =
    {
        15,
        6,
        4,
        14
    };

    private static final String []   BROWSER_FILTER_COLUMN_NAMES =
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

    private static final String []   COLUMN_VALUES               =
    {
        "Hide",
        "Show"
    };

    /** The Off/On option. */
    protected static final String [] ON_OFF_OPTIONS              =
    {
        "Off",
        "On"
    };

    /** The Flat/Hierarchical tracks option. */
    protected static final String [] TRACK_NAVIGATION_OPTIONS    =
    {
        "Flat",
        "Hierarchical"
    };


    /** Different options for the record button. */
    public enum RecordFunction
    {
        /** Record in arranger. */
        RECORD_ARRANGER,
        /** Record in arranger and enable arranger automation writing. */
        RECORD_ARRANGER_AND_ENABLE_AUTOMATION,
        /** Record in clip. */
        RECORD_CLIP,
        /** Record in clip and enable clip automation writing. */
        RECORD_CLIP_AND_ENABLE_AUTOMATION,
        /** Create a new clip, enable overdub and start playback. */
        NEW_CLIP,
        /** ... and enable clip automation writing. */
        NEW_CLIP_AND_ENABLE_AUTOMATION,
        /** Toggle arranger overdub. */
        TOGGLE_ARRANGER_OVERDUB,
        /** Toggle clip overdub. */
        TOGGLE_CLIP_OVERDUB,
        /** Toggle clip overdub. */
        TOGGLE_REC_ARM
    }


    private static final String []                    RECORD_OPTIONS                      =
    {
        "Record arranger",
        "Record arranger + enable automation",
        "Record clip",
        "Record clip + enable automation",
        "New clip",
        "New clip + enable automation",
        "Toggle arranger overdub",
        "Toggle clip overdub",
        "Toggle rec arm",
    };

    private static final int                          NUMBER_OF_FOOTSWITCHES              = 4;

    protected final IHost                             host;

    private IEnumSetting                              scaleBaseSetting;
    private IEnumSetting                              scaleInKeySetting;
    private IEnumSetting                              scaleLayoutSetting;
    private IEnumSetting                              scaleSetting;
    private IEnumSetting                              enableVUMetersSetting;
    private IEnumSetting                              flipSessionSetting;
    private IEnumSetting                              accentActiveSetting;
    private IIntegerSetting                           accentValueSetting;
    private IIntegerSetting                           quantizeAmountSetting;
    private IEnumSetting                              newClipLengthSetting;
    private IEnumSetting                              noteRepeatActiveSetting;
    private IEnumSetting                              noteRepeatPeriodSetting;
    private IEnumSetting                              noteRepeatLengthSetting;
    private IEnumSetting                              noteRepeatModeSetting;
    private IEnumSetting                              noteRepeatOctaveSetting;
    private IEnumSetting                              midiEditChannelSetting;
    private final List<IEnumSetting>                  instrumentSettings                  = new ArrayList<> (7);
    private final List<IEnumSetting>                  audioSettings                       = new ArrayList<> (3);
    private final List<IEnumSetting>                  effectSettings                      = new ArrayList<> (3);

    private String []                                 effectNames;
    private String []                                 instrumentNames;

    private final Map<Integer, Set<ISettingObserver>> observers                           = new ConcurrentHashMap<> ();
    protected final Set<Integer>                      dontNotifyAll                       = new HashSet<> ();
    protected final Set<Integer>                      isSettingActive                     = new HashSet<> ();
    protected IValueChanger                           valueChanger;

    private String                                    scale                               = "Major";
    private String                                    scaleBase                           = "C";
    private boolean                                   scaleInKey                          = true;
    private String                                    scaleLayout                         = "4th ^";
    private boolean                                   enableVUMeters                      = false;
    private TransportBehavior                         behaviorOnStop                      = TransportBehavior.STOP;
    private TransportBehavior                         behaviorOnPause                     = TransportBehavior.PAUSE;
    protected boolean                                 flipSession                         = false;
    protected boolean                                 selectClipOnLaunch                  = true;
    private boolean                                   drawRecordStripe                    = true;
    private int                                       convertAftertouch                   = 0;
    /** Accent button active. */
    private boolean                                   accentActive                        = false;
    /** Fixed velocity value for accent. */
    private int                                       fixedAccentValue                    = 127;
    private int                                       quantizeAmount                      = 100;
    protected boolean                                 flipRecord                          = false;
    private int                                       newClipLength                       = 2;
    private boolean                                   autoSelectDrum                      = false;
    private boolean                                   turnOffEmptyDrumPads                = false;
    private int                                       actionForRecArmedPad                = 0;
    private final int []                              footswitch                          = new int [NUMBER_OF_FOOTSWITCHES];
    private final boolean []                          browserDisplayFilter                =
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
    private int                                       knobSpeedDefault                    = 0;
    private int                                       knobSpeedSlow                       = -40;

    private boolean                                   noteRepeatActive                    = false;
    private Resolution                                noteRepeatPeriod                    = Resolution.RES_1_8;
    private Resolution                                noteRepeatLength                    = Resolution.RES_1_8;
    private ArpeggiatorMode                           noteRepeatMode;
    private int                                       noteRepeatOctave                    = 0;
    private int                                       midiEditChannel                     = 0;
    private final List<ArpeggiatorMode>               arpeggiatorModes;

    private boolean                                   includeMaster                       = true;
    private boolean                                   excludeDeactivatedItems             = false;
    private boolean                                   isTrackNavigationFlat               = true;

    private final String []                           userPageNames                       = new String [8];

    private boolean                                   isDeleteActive                      = false;
    private boolean                                   isDuplicateActive                   = false;

    private RecordFunction                            recordButtonFunction                = RecordFunction.RECORD_ARRANGER;
    private RecordFunction                            shiftedRecordButtonFunction         = RecordFunction.NEW_CLIP;
    private Views                                     preferredNoteView                   = Views.PLAY;
    protected Views                                   preferredAudioView                  = Views.PLAY;
    private boolean                                   startWithSessionView                = false;
    private boolean                                   useCombinationButtonToSoundDrumPads = false;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param arpeggiatorModes The available arpeggiator modes
     */
    protected AbstractConfiguration (final IHost host, final IValueChanger valueChanger, final List<ArpeggiatorMode> arpeggiatorModes)
    {
        this.host = host;
        this.valueChanger = valueChanger;
        this.arpeggiatorModes = arpeggiatorModes;
        this.noteRepeatMode = arpeggiatorModes == null || arpeggiatorModes.isEmpty () ? null : arpeggiatorModes.get (0);

        for (int i = 0; i < this.userPageNames.length; i++)
            this.userPageNames[i] = "Page " + (i + 1);

        for (int i = 0; i < this.footswitch.length; i++)
            this.footswitch[i] = FOOTSWITCH_NEW_BUTTON;

        Views.init ();
    }


    /** {@inheritDoc} */
    @Override
    public void addSettingObserver (final Integer settingID, final ISettingObserver observer)
    {
        this.observers.computeIfAbsent (settingID, id -> new HashSet<> ()).add (observer);
    }


    /** {@inheritDoc} */
    @Override
    public void removeSettingObserver (final Integer settingID, final ISettingObserver observer)
    {
        final Set<ISettingObserver> settingObservers = this.observers.get (settingID);
        if (settingObservers != null)
            settingObservers.remove (observer);
    }


    /** {@inheritDoc} */
    @Override
    public void clearSettingObservers ()
    {
        this.observers.clear ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean canSettingBeObserved (final Integer settingID)
    {
        return this.isSettingActive.contains (settingID);
    }


    /** {@inheritDoc} */
    @Override
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


    /** {@inheritDoc} */
    @Override
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


    /** {@inheritDoc} */
    @Override
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


    /** {@inheritDoc} */
    @Override
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
    public void setFixedAccentValue (final int value)
    {
        this.accentValueSetting.set (value);
    }


    /** {@inheritDoc} */
    @Override
    public void changeQuantizeAmount (final int control)
    {
        if (this.quantizeAmountSetting != null)
            this.quantizeAmountSetting.set (this.valueChanger.changeValue (control, this.quantizeAmount, -100, 101));
    }


    /** {@inheritDoc} */
    @Override
    public void setQuantizeAmount (final int value)
    {
        if (this.quantizeAmountSetting != null)
            this.quantizeAmountSetting.set (value);
    }


    /** {@inheritDoc} */
    @Override
    public void resetQuantizeAmount ()
    {
        if (this.quantizeAmountSetting != null)
            this.quantizeAmountSetting.set (100);
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
    protected void setOnOffSetting (final IEnumSetting setting, final boolean enabled)
    {
        if (setting != null)
            setting.set (enabled ? ON_OFF_OPTIONS[1] : ON_OFF_OPTIONS[0]);
    }


    /** {@inheritDoc} */
    @Override
    public TransportBehavior getBehaviourOnStop ()
    {
        return this.behaviorOnStop;
    }


    /** {@inheritDoc} */
    @Override
    public TransportBehavior getBehaviourOnPause ()
    {
        return this.behaviorOnPause;
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
    public int getNewClipLenghthInBeats (final int quartersPerMeasure)
    {
        return (int) (this.newClipLength < 2 ? Math.pow (2, this.newClipLength) : Math.pow (2, this.newClipLength - 2.0) * quartersPerMeasure);
    }


    /** {@inheritDoc} */
    @Override
    public void setNewClipLength (final int index)
    {
        this.newClipLengthSetting.set (NEW_CLIP_LENGTH_VALUES[index]);
    }


    /** {@inheritDoc} */
    @Override
    public void nextNewClipLength ()
    {
        int index = this.newClipLength + 1;
        if (index >= NEW_CLIP_LENGTH_VALUES.length)
            index = 0;
        this.newClipLengthSetting.set (NEW_CLIP_LENGTH_VALUES[index]);
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
    public boolean isCombinationButtonToSoundDrumPads ()
    {
        return this.useCombinationButtonToSoundDrumPads;
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
    public int getFootswitch (final int index)
    {
        return this.footswitch[index];
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


    /** {@inheritDoc} */
    @Override
    public int getKnobSensitivityDefault ()
    {
        return this.knobSpeedDefault;
    }


    /** {@inheritDoc} */
    @Override
    public int getKnobSensitivitySlow ()
    {
        return this.knobSpeedSlow;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isNoteRepeatActive ()
    {
        return this.noteRepeatActive;
    }


    /** {@inheritDoc} */
    @Override
    public void setNoteRepeatActive (final boolean active)
    {
        this.setOnOffSetting (this.noteRepeatActiveSetting, active);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleNoteRepeatActive ()
    {
        this.setNoteRepeatActive (!this.isNoteRepeatActive ());
    }


    /** {@inheritDoc} */
    @Override
    public Resolution getNoteRepeatPeriod ()
    {
        return this.noteRepeatPeriod;
    }


    /** {@inheritDoc} */
    @Override
    public void setNoteRepeatPeriod (final Resolution noteRepeatPeriod)
    {
        this.noteRepeatPeriodSetting.set (noteRepeatPeriod.getName ());
    }


    /** {@inheritDoc} */
    @Override
    public Resolution getNoteRepeatLength ()
    {
        return this.noteRepeatLength;
    }


    /** {@inheritDoc} */
    @Override
    public void setNoteRepeatLength (final Resolution noteRepeatLength)
    {
        this.noteRepeatLengthSetting.set (noteRepeatLength.getName ());
    }


    /** {@inheritDoc} */
    @Override
    public ArpeggiatorMode getNoteRepeatMode ()
    {
        return this.noteRepeatMode;
    }


    /** {@inheritDoc} */
    @Override
    public void setNoteRepeatMode (final ArpeggiatorMode arpMode)
    {
        this.noteRepeatModeSetting.set (arpMode.getName ());
    }


    /** {@inheritDoc} */
    @Override
    public int getNoteRepeatOctave ()
    {
        return this.noteRepeatOctave;
    }


    /** {@inheritDoc} */
    @Override
    public void setNoteRepeatOctave (final int octave)
    {
        final int o = Math.max (0, Math.min (8, octave));
        this.noteRepeatOctaveSetting.set (Integer.toString (o));
    }


    /** {@inheritDoc} */
    @Override
    public int getMidiEditChannel ()
    {
        return this.midiEditChannel;
    }


    /** {@inheritDoc} */
    @Override
    public void setMidiEditChannel (final int midiChannel)
    {
        final int mc = Math.max (0, Math.min (midiChannel, 15));
        this.midiEditChannelSetting.set (OPTIONS_MIDI_CHANNEL[mc]);
    }


    /**
     * Activate the scale setting.
     *
     * @param settingsUI The settings
     */
    protected void activateScaleSetting (final ISettingsUI settingsUI)
    {
        final String [] scaleNames = Scale.getNames ();
        this.scaleSetting = settingsUI.getEnumSetting ("Scale", CATEGORY_SCALES, scaleNames, Scale.MAJOR.getName ());
        this.scaleSetting.addValueObserver (value -> {
            this.scale = value;
            this.notifyObservers (SCALES_SCALE);
        });

        this.isSettingActive.add (SCALES_SCALE);
    }


    /**
     * Activate the scale base note setting.
     *
     * @param settingsUI The settings
     */
    protected void activateScaleBaseSetting (final ISettingsUI settingsUI)
    {
        this.scaleBaseSetting = settingsUI.getEnumSetting ("Base", CATEGORY_SCALES, Scales.BASES, Scales.BASES.get (0));
        this.scaleBaseSetting.addValueObserver (value -> {
            this.scaleBase = value;
            this.notifyObservers (SCALES_BASE);
        });

        this.isSettingActive.add (SCALES_BASE);
    }


    /**
     * Activate the scale in-scale setting.
     *
     * @param settingsUI The settings
     */
    protected void activateScaleInScaleSetting (final ISettingsUI settingsUI)
    {
        this.scaleInKeySetting = settingsUI.getEnumSetting (SCALE_IN_KEY, CATEGORY_SCALES, new String []
        {
            SCALE_IN_KEY,
            SCALE_CHROMATIC
        }, SCALE_IN_KEY);
        this.scaleInKeySetting.addValueObserver (value -> {
            this.scaleInKey = SCALE_IN_KEY.equals (value);
            this.notifyObservers (SCALES_IN_KEY);
        });

        this.isSettingActive.add (SCALES_IN_KEY);
    }


    /**
     * Activate the scale layout setting.
     *
     * @param settingsUI The settings
     */
    protected void activateScaleLayoutSetting (final ISettingsUI settingsUI)
    {
        this.activateScaleLayoutSetting (settingsUI, ScaleLayout.FOURTH_UP.getName ());
    }


    /**
     * Activate the scale layout setting.
     *
     * @param settingsUI The settings
     * @param defaultScale The name of the default scale to set
     */
    protected void activateScaleLayoutSetting (final ISettingsUI settingsUI, final String defaultScale)
    {
        final String [] names = ScaleLayout.getNames ();
        this.scaleLayoutSetting = settingsUI.getEnumSetting ("Layout", CATEGORY_SCALES, names, defaultScale);
        this.scaleLayoutSetting.addValueObserver (value -> {
            this.scaleLayout = value;
            this.notifyObservers (SCALES_LAYOUT);
        });

        this.isSettingActive.add (SCALES_LAYOUT);
    }


    /**
     * Activate the VU meters setting.
     *
     * @param settingsUI The settings
     */
    protected void activateEnableVUMetersSetting (final ISettingsUI settingsUI)
    {
        this.activateEnableVUMetersSetting (settingsUI, CATEGORY_WORKFLOW);
    }


    /**
     * Activate the VU meters setting.
     *
     * @param settingsUI The settings
     * @param category The name for the category
     */
    protected void activateEnableVUMetersSetting (final ISettingsUI settingsUI, final String category)
    {
        this.enableVUMetersSetting = settingsUI.getEnumSetting ("VU Meters", category, ON_OFF_OPTIONS, ON_OFF_OPTIONS[1]);
        this.enableVUMetersSetting.addValueObserver (value -> {
            this.enableVUMeters = "On".equals (value);
            this.notifyObservers (ENABLE_VU_METERS);
        });

        this.isSettingActive.add (ENABLE_VU_METERS);
    }


    /**
     * Activate the behavior on stop setting.
     *
     * @param settingsUI The settings
     */
    protected void activateBehaviourOnStopSetting (final ISettingsUI settingsUI)
    {
        final IEnumSetting behaviourOnStopSetting = settingsUI.getEnumSetting ("Behaviour on Stop", CATEGORY_TRANSPORT, TRANSPORT_BEHAVIOUR_VALUES, TRANSPORT_BEHAVIOUR_VALUES[0]);
        behaviourOnStopSetting.addValueObserver (value -> {
            this.behaviorOnStop = TransportBehavior.values ()[lookupIndex (TRANSPORT_BEHAVIOUR_VALUES, value)];
            this.notifyObservers (BEHAVIOUR_ON_STOP);
        });

        this.isSettingActive.add (BEHAVIOUR_ON_STOP);
    }


    /**
     * Activate the behavior on pause setting.
     *
     * @param settingsUI The settings
     */
    protected void activateBehaviourOnPauseSetting (final ISettingsUI settingsUI)
    {
        final IEnumSetting behaviourOnPauseSetting = settingsUI.getEnumSetting ("Behaviour on Pause", CATEGORY_TRANSPORT, TRANSPORT_BEHAVIOUR_VALUES, TRANSPORT_BEHAVIOUR_VALUES[2]);
        behaviourOnPauseSetting.addValueObserver (value -> {
            this.behaviorOnPause = TransportBehavior.values ()[lookupIndex (TRANSPORT_BEHAVIOUR_VALUES, value)];
            this.notifyObservers (BEHAVIOUR_ON_PAUSE);
        });

        this.isSettingActive.add (BEHAVIOUR_ON_PAUSE);
    }


    /**
     * Activate the flip session setting.
     *
     * @param settingsUI The settings
     */
    protected void activateFlipSessionSetting (final ISettingsUI settingsUI)
    {
        this.flipSessionSetting = settingsUI.getEnumSetting ("Flip Session", CATEGORY_SESSION, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        this.flipSessionSetting.addValueObserver (value -> {
            this.flipSession = "On".equals (value);
            this.notifyObservers (FLIP_SESSION);
        });

        this.isSettingActive.add (FLIP_SESSION);
    }


    /**
     * Activate the select clip on launch setting.
     *
     * @param settingsUI The settings
     */
    protected void activateSelectClipOnLaunchSetting (final ISettingsUI settingsUI)
    {
        final IEnumSetting selectClipOnLaunchSetting = settingsUI.getEnumSetting ("Select clip/scene on launch", CATEGORY_SESSION, ON_OFF_OPTIONS, ON_OFF_OPTIONS[1]);
        selectClipOnLaunchSetting.addValueObserver (value -> {
            this.selectClipOnLaunch = "On".equals (value);
            this.notifyObservers (SELECT_CLIP_ON_LAUNCH);
        });

        this.isSettingActive.add (SELECT_CLIP_ON_LAUNCH);
    }


    /**
     * Activate the draw record stripe setting.
     *
     * @param settingsUI The settings
     */
    protected void activateDrawRecordStripeSetting (final ISettingsUI settingsUI)
    {
        final IEnumSetting drawRecordStripeSetting = settingsUI.getEnumSetting ("Display clips of record enabled tracks in red", CATEGORY_SESSION, ON_OFF_OPTIONS, ON_OFF_OPTIONS[1]);
        drawRecordStripeSetting.addValueObserver (value -> {
            this.drawRecordStripe = "On".equals (value);
            this.notifyObservers (DRAW_RECORD_STRIPE);
        });

        this.isSettingActive.add (DRAW_RECORD_STRIPE);
    }


    /**
     * Activate action for record armed pad setting.
     *
     * @param settingsUI The settings
     */
    protected void activateActionForRecArmedPad (final ISettingsUI settingsUI)
    {
        final IEnumSetting actionForRecArmedPadSetting = settingsUI.getEnumSetting ("Action for pressing rec armed empty clip", CATEGORY_SESSION, ACTIONS_REC_ARMED_PADS, ACTIONS_REC_ARMED_PADS[0]);
        actionForRecArmedPadSetting.addValueObserver (value -> {
            this.actionForRecArmedPad = lookupIndex (ACTIONS_REC_ARMED_PADS, value);
            this.notifyObservers (ACTION_FOR_REC_ARMED_PAD);
        });

        this.isSettingActive.add (ACTION_FOR_REC_ARMED_PAD);
    }


    /**
     * Activate the convert aftertouch setting.
     *
     * @param settingsUI The settings
     */
    protected void activateConvertAftertouchSetting (final ISettingsUI settingsUI)
    {
        final IEnumSetting convertAftertouchSetting = settingsUI.getEnumSetting ("Convert Poly Aftertouch to", CATEGORY_PADS, AbstractConfiguration.AFTERTOUCH_CONVERSION_VALUES, AbstractConfiguration.AFTERTOUCH_CONVERSION_VALUES[1]);
        convertAftertouchSetting.addValueObserver (value -> {
            this.convertAftertouch = lookupIndex (AbstractConfiguration.AFTERTOUCH_CONVERSION_VALUES, value) - 3;
            this.notifyObservers (CONVERT_AFTERTOUCH);
        });

        this.isSettingActive.add (CONVERT_AFTERTOUCH);
    }


    /**
     * Activate the accent active setting.
     *
     * @param settingsUI The settings
     */
    protected void activateAccentActiveSetting (final ISettingsUI settingsUI)
    {
        this.accentActiveSetting = settingsUI.getEnumSetting ("Activate Fixed Accent", CATEGORY_PLAY_AND_SEQUENCE, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        this.accentActiveSetting.addValueObserver (value -> {
            this.accentActive = "On".equals (value);
            this.notifyObservers (ACTIVATE_FIXED_ACCENT);
        });

        this.isSettingActive.add (ACTIVATE_FIXED_ACCENT);
    }


    /**
     * Activate the accent value setting.
     *
     * @param settingsUI The settings
     */
    protected void activateAccentValueSetting (final ISettingsUI settingsUI)
    {
        this.accentValueSetting = settingsUI.getRangeSetting ("Fixed Accent Value", CATEGORY_PLAY_AND_SEQUENCE, 1, 127, 1, "", 127);
        this.accentValueSetting.addValueObserver (value -> {
            this.fixedAccentValue = value.intValue ();
            this.notifyObservers (FIXED_ACCENT_VALUE);
        });

        this.isSettingActive.add (FIXED_ACCENT_VALUE);
    }


    /**
     * Activate the flip arranger and clip record setting.
     *
     * @param settingsUI The settings
     */
    protected void activateFlipRecordSetting (final ISettingsUI settingsUI)
    {
        final IEnumSetting flipRecordSetting = settingsUI.getEnumSetting ("Flip arranger and clip record / automation", CATEGORY_TRANSPORT, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        flipRecordSetting.addValueObserver (value -> {
            this.flipRecord = "On".equals (value);
            this.notifyObservers (FLIP_RECORD);
        });

        this.isSettingActive.add (FLIP_RECORD);
    }


    /**
     * Activate the include master setting.
     *
     * @param settingsUI The settings
     */
    protected void activateIncludeMasterSetting (final ISettingsUI settingsUI)
    {
        final IEnumSetting includeMasterSetting = settingsUI.getEnumSetting ("Include (Group-)Mastertrack (requires restart)", CATEGORY_WORKFLOW, ON_OFF_OPTIONS, ON_OFF_OPTIONS[1]);
        this.includeMaster = "On".equals (includeMasterSetting.get ());
    }


    /**
     * Activate the exclude deactivated tracks setting.
     *
     * @param settingsUI The settings
     */
    protected void activateExcludeDeactivatedItemsSetting (final ISettingsUI settingsUI)
    {
        final IEnumSetting excludeDeactivatedItemsSetting = settingsUI.getEnumSetting ("Exclude deactivated items (tracks, sends, devices, layers)", CATEGORY_WORKFLOW, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        excludeDeactivatedItemsSetting.addValueObserver (value -> {
            this.excludeDeactivatedItems = ON_OFF_OPTIONS[1].equals (value);
            this.notifyObservers (EXCLUDE_DEACTIVATED_ITEMS);
        });

        this.isSettingActive.add (EXCLUDE_DEACTIVATED_ITEMS);
    }


    /**
     * Activate the flat or hierarchical tracks setting.
     *
     * @param settingsUI The settings
     * @param category The category to add the setting to or null to use default
     */
    protected void activateTrackNavigationSetting (final ISettingsUI settingsUI, final String category)
    {
        final IEnumSetting trackNavigationSetting = settingsUI.getEnumSetting ("Track Navigation (requires restart)", category == null ? CATEGORY_WORKFLOW : category, TRACK_NAVIGATION_OPTIONS, TRACK_NAVIGATION_OPTIONS[0]);
        this.isTrackNavigationFlat = TRACK_NAVIGATION_OPTIONS[0].equals (trackNavigationSetting.get ());
    }


    /**
     * Activate the accent value setting.
     *
     * @param settingsUI The settings
     */
    protected void activateNewClipLengthSetting (final ISettingsUI settingsUI)
    {
        this.newClipLengthSetting = settingsUI.getEnumSetting ("New Clip Length", CATEGORY_WORKFLOW, NEW_CLIP_LENGTH_VALUES, NEW_CLIP_LENGTH_VALUES[2]);
        this.newClipLengthSetting.addValueObserver (value -> {
            this.newClipLength = lookupIndex (NEW_CLIP_LENGTH_VALUES, value);
            this.notifyObservers (NEW_CLIP_LENGTH);
        });

        this.isSettingActive.add (NEW_CLIP_LENGTH);
    }


    /**
     * Activate the quantize amount setting.
     *
     * @param settingsUI The settings
     */
    protected void activateQuantizeAmountSetting (final ISettingsUI settingsUI)
    {
        this.quantizeAmountSetting = settingsUI.getRangeSetting ("Quantize Amount", CATEGORY_PLAY_AND_SEQUENCE, 1, 100, 1, "%", 100);
        this.quantizeAmountSetting.addValueObserver (value -> {
            this.quantizeAmount = value.intValue ();
            this.notifyObservers (QUANTIZE_AMOUNT);
        });

        this.isSettingActive.add (QUANTIZE_AMOUNT);
    }


    /**
     * Activate the MIDI edit channel setting.
     *
     * @param settingsUI The settings
     */
    protected void activateMidiEditChannelSetting (final ISettingsUI settingsUI)
    {
        this.midiEditChannelSetting = settingsUI.getEnumSetting ("MIDI Edit/Insert note channel", CATEGORY_PLAY_AND_SEQUENCE, OPTIONS_MIDI_CHANNEL, OPTIONS_MIDI_CHANNEL[0]);
        this.midiEditChannelSetting.addValueObserver (value -> {
            this.midiEditChannel = Integer.parseInt (value) - 1;
            this.notifyObservers (MIDI_EDIT_CHANNEL);
        });

        this.isSettingActive.add (MIDI_EDIT_CHANNEL);
    }


    /**
     * Activate the auto select drum setting.
     *
     * @param settingsUI The settings
     */
    protected void activateAutoSelectDrumSetting (final ISettingsUI settingsUI)
    {
        final IEnumSetting autoSelectDrumSetting = settingsUI.getEnumSetting ("Auto-select drum settings", CATEGORY_DRUMS, new String []
        {
            "Off",
            "Channel"
        }, "Off");
        autoSelectDrumSetting.addValueObserver (value -> {
            this.autoSelectDrum = "Channel".equals (value);
            this.notifyObservers (AUTO_SELECT_DRUM);
        });

        this.isSettingActive.add (AUTO_SELECT_DRUM);
    }


    /**
     * Activate the turn off empty drum pads setting.
     *
     * @param settingsUI The settings
     */
    protected void activateTurnOffEmptyDrumPadsSetting (final ISettingsUI settingsUI)
    {
        final IEnumSetting turnOffEmptyDrumPadsSetting = settingsUI.getEnumSetting ("Turn off empty drum pads", CATEGORY_DRUMS, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        turnOffEmptyDrumPadsSetting.addValueObserver (value -> {
            this.turnOffEmptyDrumPads = "On".equals (value);
            this.notifyObservers (TURN_OFF_EMPTY_DRUM_PADS);
        });

        this.isSettingActive.add (TURN_OFF_EMPTY_DRUM_PADS);
    }


    /**
     * Activate the setting to sound drum pads with or without pressing Select button.
     *
     * @param settingsUI The settings
     */
    protected void activateUseCombinationButtonToSoundSetting (final ISettingsUI settingsUI)
    {
        final IEnumSetting useCombinationButtonToSoundSetting = settingsUI.getEnumSetting ("Use combination button to sound drum pads", CATEGORY_DRUMS, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        useCombinationButtonToSoundSetting.addValueObserver (value -> {
            this.useCombinationButtonToSoundDrumPads = "On".equals (value);
            this.notifyObservers (SOUND_DRUM_PADS);
        });

        this.isSettingActive.add (SOUND_DRUM_PADS);
    }


    /**
     * Activate a footswitch setting.
     *
     * @param settingsUI The settings
     * @param index The index of the footswitch (0-3)
     * @param label The label to use
     */
    protected void activateFootswitchSetting (final ISettingsUI settingsUI, final int index, final String label)
    {
        final Integer id = Integer.valueOf (FOOTSWITCH_1.intValue () + index);

        final IEnumSetting footswitchSetting = settingsUI.getEnumSetting (label, CATEGORY_WORKFLOW, FOOTSWITCH_VALUES, FOOTSWITCH_VALUES[FOOTSWITCH_DEFAULTS[index]]);
        footswitchSetting.addValueObserver (value -> {
            this.footswitch[index] = lookupIndex (FOOTSWITCH_VALUES, value);
            this.notifyObservers (id);
        });

        this.isSettingActive.add (id);
    }


    /**
     * Activate the browser settings.
     *
     * @param settingsUI The settings
     */
    protected void activateBrowserSettings (final ISettingsUI settingsUI)
    {
        for (int i = 0; i < BROWSER_FILTER_COLUMN_NAMES.length; i++)
        {
            final IEnumSetting browserDisplayFilterSetting = settingsUI.getEnumSetting (BROWSER_FILTER_COLUMN_NAMES[i], "Browser", COLUMN_VALUES, COLUMN_VALUES[1]);
            final int index = i;
            final Integer browserDisplayFilterIndex = Integer.valueOf (BROWSER_DISPLAY_FILTER1.intValue () + index);
            browserDisplayFilterSetting.addValueObserver (value -> {
                this.browserDisplayFilter[index] = COLUMN_VALUES[1].equals (value);
                this.notifyObservers (browserDisplayFilterIndex);
            });
            this.isSettingActive.add (browserDisplayFilterIndex);
        }
    }


    /**
     * Activate the knob speed settings.
     *
     * @param settingsUI The settings
     */
    protected void activateKnobSpeedSetting (final ISettingsUI settingsUI)
    {
        final IEnumSetting knobSpeedNormalSetting = settingsUI.getEnumSetting ("Knob Sensitivity Default", CATEGORY_WORKFLOW, KNOB_SENSITIVITY, KNOB_SENSITIVITY[100]);
        knobSpeedNormalSetting.addValueObserver (value -> {
            this.knobSpeedDefault = lookupIndex (KNOB_SENSITIVITY, value) - 100;
            this.notifyObservers (KNOB_SENSITIVITY_DEFAULT);
        });
        final IEnumSetting knobSpeedSlowSetting = settingsUI.getEnumSetting ("Knob Sensitivity Slow", CATEGORY_WORKFLOW, KNOB_SENSITIVITY, KNOB_SENSITIVITY[60]);
        knobSpeedSlowSetting.addValueObserver (value -> {
            this.knobSpeedSlow = lookupIndex (KNOB_SENSITIVITY, value) - 100;
            this.notifyObservers (KNOB_SENSITIVITY_SLOW);
        });

        this.isSettingActive.add (KNOB_SENSITIVITY_DEFAULT);
        this.isSettingActive.add (KNOB_SENSITIVITY_SLOW);
    }


    /**
     * Activate the note repeat settings.
     *
     * @param settingsUI The settings
     */
    protected void activateNoteRepeatSetting (final ISettingsUI settingsUI)
    {
        this.noteRepeatActiveSetting = settingsUI.getEnumSetting ("Active", CATEGORY_NOTEREPEAT, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        this.noteRepeatActiveSetting.addValueObserver (value -> {
            this.noteRepeatActive = "On".equals (value);
            this.notifyObservers (NOTEREPEAT_ACTIVE);
        });

        final String [] names = Resolution.getNames ();

        this.noteRepeatPeriodSetting = settingsUI.getEnumSetting ("Period", CATEGORY_NOTEREPEAT, names, names[4]);
        this.noteRepeatPeriodSetting.addValueObserver (value -> {
            this.noteRepeatPeriod = Resolution.getByName (value);
            this.notifyObservers (NOTEREPEAT_PERIOD);
        });

        this.isSettingActive.add (NOTEREPEAT_ACTIVE);
        this.isSettingActive.add (NOTEREPEAT_PERIOD);

        if (this.host.supports (Capability.NOTE_REPEAT_LENGTH))
        {
            this.noteRepeatLengthSetting = settingsUI.getEnumSetting ("Length", CATEGORY_NOTEREPEAT, names, names[4]);
            this.noteRepeatLengthSetting.addValueObserver (value -> {
                this.noteRepeatLength = Resolution.getByName (value);
                this.notifyObservers (NOTEREPEAT_LENGTH);
            });

            this.isSettingActive.add (NOTEREPEAT_LENGTH);
        }

        if (this.host.supports (Capability.NOTE_REPEAT_MODE))
        {
            final String [] arpModeNames = new String [this.arpeggiatorModes.size ()];
            for (int i = 0; i < this.arpeggiatorModes.size (); i++)
                arpModeNames[i] = this.arpeggiatorModes.get (i).getName ();

            this.noteRepeatModeSetting = settingsUI.getEnumSetting ("Mode", CATEGORY_NOTEREPEAT, arpModeNames, arpModeNames[1]);
            this.noteRepeatModeSetting.addValueObserver (value -> {
                this.noteRepeatMode = ArpeggiatorMode.lookupByName (value);
                this.notifyObservers (NOTEREPEAT_MODE);
            });

            this.isSettingActive.add (NOTEREPEAT_MODE);
        }

        if (this.host.supports (Capability.NOTE_REPEAT_OCTAVES))
        {
            final String [] octaves =
            {
                "0",
                "1",
                "2",
                "3",
                "4",
                "5",
                "6",
                "7",
                "8"
            };

            this.noteRepeatOctaveSetting = settingsUI.getEnumSetting ("Octave", CATEGORY_NOTEREPEAT, octaves, octaves[1]);
            this.noteRepeatOctaveSetting.addValueObserver (value -> {
                this.noteRepeatOctave = Integer.parseInt (value);
                this.notifyObservers (NOTEREPEAT_OCTAVE);
            });

            this.isSettingActive.add (NOTEREPEAT_OCTAVE);
        }
    }


    /**
     * Activate the settings for naming the user pages.
     *
     * @param settingsUI The settings
     */
    protected void activateUserPageNamesSetting (final ISettingsUI settingsUI)
    {
        for (int i = 0; i < 8; i++)
        {
            final int index = i;
            settingsUI.getStringSetting ("User Page " + (i + 1), CATEGORY_WORKFLOW, 10, "Page " + (i + 1)).addValueObserver (value -> this.userPageNames[index] = value);
        }
    }


    /**
     * Activate the settings for the record button.
     *
     * @param settingsUI The settings
     */
    protected void activateRecordButtonSetting (final ISettingsUI settingsUI)
    {
        final IEnumSetting recordButtonSetting = settingsUI.getEnumSetting ("Record button", CATEGORY_TRANSPORT, RECORD_OPTIONS, RECORD_OPTIONS[1]);
        recordButtonSetting.addValueObserver (value -> {
            for (int i = 0; i < RECORD_OPTIONS.length; i++)
            {
                if (RECORD_OPTIONS[i].equals (value))
                    this.recordButtonFunction = RecordFunction.values ()[i];
            }
            this.notifyObservers (RECORD_BUTTON_FUNCTION);
        });

        this.isSettingActive.add (RECORD_BUTTON_FUNCTION);
    }


    /**
     * Activate the settings for the record button in combination with shift.
     *
     * @param settingsUI The settings
     */
    protected void activateShiftedRecordButtonSetting (final ISettingsUI settingsUI)
    {
        final IEnumSetting shiftedRecordButtonSetting = settingsUI.getEnumSetting ("Shift + Record button", CATEGORY_TRANSPORT, RECORD_OPTIONS, RECORD_OPTIONS[5]);
        shiftedRecordButtonSetting.addValueObserver (value -> {
            for (int i = 0; i < RECORD_OPTIONS.length; i++)
            {
                if (RECORD_OPTIONS[i].equals (value))
                    this.shiftedRecordButtonFunction = RecordFunction.values ()[i];
            }
            this.notifyObservers (SHIFTED_RECORD_BUTTON_FUNCTION);
        });

        this.isSettingActive.add (SHIFTED_RECORD_BUTTON_FUNCTION);
    }


    /**
     * Activate the add track device favorites.
     *
     * @param settingsUI The settings
     * @param numFavInstruments The number of favorite instrument track devices
     * @param numFavAudio The number of favorite audio tracks devices
     * @param numFavEffects The number of favorite effect tracks devices
     */
    protected void activateDeviceFavorites (final ISettingsUI settingsUI, final int numFavInstruments, final int numFavAudio, final int numFavEffects)
    {
        this.instrumentNames = getDeviceNames (this.host.getInstrumentMetadata ());
        for (int i = 0; i < numFavInstruments; i++)
        {
            final IEnumSetting favSetting = settingsUI.getEnumSetting ("Instrument " + (i + 1), CATEGORY_FAV_DEVICES, this.instrumentNames, this.instrumentNames[Math.min (this.instrumentNames.length - 1, i)]);
            this.instrumentSettings.add (favSetting);
        }

        this.effectNames = getDeviceNames (this.host.getAudioEffectMetadata ());
        for (int i = 0; i < numFavAudio; i++)
        {
            final IEnumSetting favSetting = settingsUI.getEnumSetting ("Audio " + (i + 1), CATEGORY_FAV_DEVICES, this.effectNames, this.effectNames[Math.min (this.effectNames.length - 1, i)]);
            this.audioSettings.add (favSetting);
        }
        for (int i = 0; i < numFavEffects; i++)
        {
            final IEnumSetting favSetting = settingsUI.getEnumSetting ("Effect " + (i + 1), CATEGORY_FAV_DEVICES, this.effectNames, this.effectNames[Math.min (this.effectNames.length - 1, i)]);
            this.effectSettings.add (favSetting);
        }
    }


    /**
     * Activate the preferred note view setting.
     *
     * @param settingsUI The settings
     * @param views The available views for selection
     */
    protected void activatePreferredNoteViewSetting (final ISettingsUI settingsUI, final Views [] views)
    {
        final String [] labels = new String [views.length];
        for (int i = 0; i < views.length; i++)
            labels[i] = Views.getViewName (views[i]);

        final IEnumSetting preferredNoteViewSetting = settingsUI.getEnumSetting ("Default note view", CATEGORY_PLAY_AND_SEQUENCE, labels, labels[0]);
        preferredNoteViewSetting.addValueObserver (value -> {
            this.preferredNoteView = Views.getViewByName (value);
            this.notifyObservers (PREFERRED_NOTE_VIEW);
        });

        this.isSettingActive.add (PREFERRED_NOTE_VIEW);
    }


    /**
     * Activate the start with session view setting.
     *
     * @param settingsUI The settings
     */
    protected void activateStartWithSessionViewSetting (final ISettingsUI settingsUI)
    {
        final IEnumSetting startWithSessionViewSetting = settingsUI.getEnumSetting ("Start with session view", CATEGORY_PLAY_AND_SEQUENCE, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        startWithSessionViewSetting.addValueObserver (value -> {
            this.startWithSessionView = "On".equals (value);
            this.notifyObservers (START_WITH_SESSION_VIEW);
        });

        this.isSettingActive.add (START_WITH_SESSION_VIEW);
    }


    /** {@inheritDoc} */
    @Override
    public void notifyAllObservers ()
    {
        for (final Entry<Integer, Set<ISettingObserver>> entry: this.observers.entrySet ())
        {
            if (!this.dontNotifyAll.contains (entry.getKey ()))
                entry.getValue ().forEach (ISettingObserver::hasChanged);
        }
    }


    /**
     * Notify all observers about the change of a setting.
     *
     * @param settingID The ID of the setting, which has changed
     */
    protected void notifyObservers (final Integer settingID)
    {
        final Set<ISettingObserver> set = this.observers.get (settingID);
        if (set != null)
            set.forEach (ISettingObserver::hasChanged);
    }


    /**
     * Register a handler for the 'exclude deactivated items' setting.
     *
     * @param model The model for getting the banks to configure
     */
    public void registerDeactivatedItemsHandler (final IModel model)
    {
        this.addSettingObserver (AbstractConfiguration.EXCLUDE_DEACTIVATED_ITEMS, () -> {
            final boolean exclude = this.areDeactivatedItemsExcluded ();
            final ITrackBank trackBank = model.getTrackBank ();
            trackBank.setSkipDisabledItems (exclude);
            for (int i = 0; i < trackBank.getPageSize (); i++)
                trackBank.getItem (i).getSendBank ().setSkipDisabledItems (exclude);
            final ITrackBank effectTrackBank = model.getEffectTrackBank ();
            if (effectTrackBank != null)
                effectTrackBank.setSkipDisabledItems (exclude);
            final ICursorDevice cursorDevice = model.getCursorDevice ();
            final IDeviceBank deviceBank = cursorDevice.getDeviceBank ();
            deviceBank.setSkipDisabledItems (exclude);
            cursorDevice.getLayerBank ().setSkipDisabledItems (exclude);
            final IDrumPadBank drumPadBank = cursorDevice.getDrumPadBank ();
            if (drumPadBank != null)
                drumPadBank.setSkipDisabledItems (exclude);
        });
    }


    /**
     * Lookup the index of the value in the given options array.
     *
     * @param options The options in which to search for the value
     * @param value The value to search for
     * @return The index or 0 if not found
     */
    public static int lookupIndex (final String [] options, final String value)
    {
        return lookupIndex (Arrays.asList (options), value);
    }


    /**
     * Lookup the index of the value in the given options array.
     *
     * @param options The options in which to search for the value
     * @param value The value to search for
     * @return The index or 0 if not found
     */
    public static int lookupIndex (final List<String> options, final String value)
    {
        return Math.max (0, options.indexOf (value));
    }


    /**
     * Get a new clip length value string.
     *
     * @param index The index
     * @return The text
     */
    public static String getNewClipLengthValue (final int index)
    {
        return NEW_CLIP_LENGTH_VALUES[index];
    }


    /** {@inheritDoc} */
    @Override
    public int lookupArpeggiatorModeIndex (final ArpeggiatorMode arpMode)
    {
        return Math.max (0, this.arpeggiatorModes.indexOf (arpMode));
    }


    /**
     * Get the next arpeggiator mode.
     *
     * @return The next
     */
    public ArpeggiatorMode nextArpeggiatorMode ()
    {
        final ArpeggiatorMode arpMode = this.getNoteRepeatMode ();
        final int index = this.lookupArpeggiatorModeIndex (arpMode) + 1;
        return this.arpeggiatorModes.get (index < this.arpeggiatorModes.size () ? index : 0);
    }


    /**
     * Get the previous arpeggiator mode.
     *
     * @return The previous
     */
    public ArpeggiatorMode prevArpeggiatorMode ()
    {
        final ArpeggiatorMode arpMode = this.getNoteRepeatMode ();
        final int index = this.lookupArpeggiatorModeIndex (arpMode) - 1;
        return this.arpeggiatorModes.get (index < 0 ? this.arpeggiatorModes.size () - 1 : index);
    }


    /** {@inheritDoc} */
    @Override
    public List<ArpeggiatorMode> getArpeggiatorModes ()
    {
        return this.arpeggiatorModes;
    }


    /** {@inheritDoc} */
    @Override
    public RecordFunction getRecordButtonFunction ()
    {
        return this.recordButtonFunction;
    }


    /** {@inheritDoc} */
    @Override
    public RecordFunction getShiftedRecordButtonFunction ()
    {
        return this.shiftedRecordButtonFunction;
    }


    /** {@inheritDoc} */
    @Override
    public Views getPreferredNoteView ()
    {
        return this.preferredNoteView;
    }


    /** {@inheritDoc} */
    @Override
    public Views getPreferredAudioView ()
    {
        return this.preferredAudioView;
    }


    /** {@inheritDoc} */
    @Override
    public boolean shouldStartWithSessionView ()
    {
        return this.startWithSessionView;
    }


    /**
     * Should the master track and group-master tracks be included in the track list?
     *
     * @return True if they should be included
     */
    public boolean areMasterTracksIncluded ()
    {
        return this.includeMaster;
    }


    /**
     * Should deactivated tracks be included in the track list?
     *
     * @return False if they should be included
     */
    public boolean areDeactivatedItemsExcluded ()
    {
        return this.excludeDeactivatedItems;
    }


    /**
     * Get the user page names.
     *
     * @return The user page names
     */
    public String [] getUserPageNames ()
    {
        return this.userPageNames;
    }


    /**
     * Returns true if the delete mode is active.
     *
     * @return True if active
     */
    public boolean isDeleteModeActive ()
    {
        return this.isDeleteActive;
    }


    /**
     * Toggle the delete mode.
     */
    public void toggleDeleteModeActive ()
    {
        this.isDeleteActive = !this.isDeleteActive;
        if (this.isDeleteActive)
            this.isDuplicateActive = false;
    }


    /**
     * Returns true if the duplicate mode is active.
     *
     * @return True if active
     */
    public boolean isDuplicateModeActive ()
    {
        return this.isDuplicateActive;
    }


    /**
     * Toggle the duplicate mode.
     */
    public void toggleDuplicateModeActive ()
    {
        this.isDuplicateActive = !this.isDuplicateActive;
        if (this.isDuplicateActive)
            this.isDeleteActive = false;
    }


    /**
     * Returns true if the track navigation should be hierarchical.
     *
     * @return True if the track navigation should be hierarchical otherwise flat
     */
    public boolean isTrackNavigationFlat ()
    {
        return this.isTrackNavigationFlat;
    }


    /**
     * Get one of the favorite instrument devices.
     *
     * @param index The index
     * @return The devices' metadata or null if none existing
     */
    public Optional<IDeviceMetadata> getInstrumentFavorite (final int index)
    {
        if (index >= this.instrumentSettings.size ())
            return Optional.empty ();
        final String sel = this.instrumentSettings.get (index).get ();
        final int lookupIndex = lookupIndex (this.instrumentNames, sel);
        final List<IDeviceMetadata> instrumentMetadata = this.host.getInstrumentMetadata ();
        return Optional.ofNullable (lookupIndex >= instrumentMetadata.size () ? null : instrumentMetadata.get (lookupIndex));
    }


    /**
     * Get one of the favorite audio devices.
     *
     * @param index The index
     * @return The devices' metadata or null if none existing
     */
    public Optional<IDeviceMetadata> getAudioFavorite (final int index)
    {
        if (index >= this.audioSettings.size ())
            return Optional.empty ();
        final String sel = this.audioSettings.get (index).get ();
        final int lookupIndex = lookupIndex (this.effectNames, sel);
        final List<IDeviceMetadata> effectMetadata = this.host.getAudioEffectMetadata ();
        return Optional.ofNullable (lookupIndex >= effectMetadata.size () ? null : effectMetadata.get (lookupIndex));
    }


    /**
     * Get one of the favorite effect devices.
     *
     * @param index The index
     * @return The devices' metadata or null if none existing
     */
    public Optional<IDeviceMetadata> getEffectFavorite (final int index)
    {
        if (index >= this.effectSettings.size ())
            return Optional.empty ();
        final String sel = this.effectSettings.get (index).get ();
        final int lookupIndex = lookupIndex (this.effectNames, sel);
        final List<IDeviceMetadata> effectMetadata = this.host.getAudioEffectMetadata ();
        return Optional.ofNullable (lookupIndex >= effectMetadata.size () ? null : effectMetadata.get (lookupIndex));
    }


    private static String [] getDeviceNames (final List<IDeviceMetadata> deviceMetadata)
    {
        final String [] deviceNames = new String [deviceMetadata.size ()];
        for (int i = 0; i < deviceNames.length; i++)
            deviceNames[i] = deviceMetadata.get (i).fullName ();
        return deviceNames;
    }
}
