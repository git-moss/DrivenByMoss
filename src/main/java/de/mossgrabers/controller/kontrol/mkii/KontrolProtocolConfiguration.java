// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.mkii;

import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.IEnumSetting;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;


/**
 * The configuration settings for Komplete Kontrol MkII.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class KontrolProtocolConfiguration extends AbstractConfiguration
{
    private static final Integer RECORD_BUTTON_FUNCTION         = Integer.valueOf (50);
    private static final Integer SHIFTED_RECORD_BUTTON_FUNCTION = Integer.valueOf (51);
    private static final Integer FLIP_TRACK_CLIP_NAVIGATION     = Integer.valueOf (52);
    private static final Integer FLIP_CLIP_SCENE_NAVIGATION     = Integer.valueOf (53);


    /** Different options for the record button. */
    public enum RecordFunction
    {
        /** Record in arranger. */
        RECORD_ARRANGER,
        /** Record in clip. */
        RECORD_CLIP,
        /** Create a new clip, enable overdub and start playback. */
        NEW_CLIP,
        /** Toggle arranger overdub. */
        TOGGLE_ARRANGER_OVERDUB,
        /** Toggle clip overdub. */
        TOGGLE_CLIP_OVERDUB,
        /** Toggle clip overdub. */
        TOGGLE_REC_ARM
    }


    private static final String    CATEGORY_NAVIGATION         = "Navigation";

    private static final String [] RECORD_OPTIONS              =
    {
        "Record arranger",
        "Record clip",
        "New clip",
        "Toggle arranger overdub",
        "Toggle clip overdub",
        "Toggle rec arm",
    };

    private RecordFunction         recordButtonFunction        = RecordFunction.RECORD_ARRANGER;
    private RecordFunction         shiftedRecordButtonFunction = RecordFunction.NEW_CLIP;
    private boolean                flipTrackClipNavigation     = false;
    private boolean                flipClipSceneNavigation     = false;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     */
    public KontrolProtocolConfiguration (final IHost host, final IValueChanger valueChanger)
    {
        super (host, valueChanger);
    }


    /** {@inheritDoc} */
    @Override
    public void init (final ISettingsUI globalSettings, final ISettingsUI documentSettings)
    {
        ///////////////////////////
        // Transport

        this.activateBehaviourOnStopSetting (globalSettings);

        final IEnumSetting recordButtonSetting = globalSettings.getEnumSetting ("Record button", CATEGORY_TRANSPORT, RECORD_OPTIONS, RECORD_OPTIONS[1]);
        recordButtonSetting.addValueObserver (value -> {
            for (int i = 0; i < RECORD_OPTIONS.length; i++)
            {
                if (RECORD_OPTIONS[i].equals (value))
                    this.recordButtonFunction = RecordFunction.values ()[i];
            }
            this.notifyObservers (RECORD_BUTTON_FUNCTION);
        });

        final IEnumSetting shiftedRecordButtonSetting = globalSettings.getEnumSetting ("Shift + Record button", CATEGORY_TRANSPORT, RECORD_OPTIONS, RECORD_OPTIONS[0]);
        shiftedRecordButtonSetting.addValueObserver (value -> {
            for (int i = 0; i < RECORD_OPTIONS.length; i++)
            {
                if (RECORD_OPTIONS[i].equals (value))
                    this.shiftedRecordButtonFunction = RecordFunction.values ()[i];
            }
            this.notifyObservers (SHIFTED_RECORD_BUTTON_FUNCTION);
        });

        ///////////////////////////
        // Navigation

        final IEnumSetting flipTrackClipNavigationSetting = globalSettings.getEnumSetting ("Flip track/clip navigation", CATEGORY_NAVIGATION, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        flipTrackClipNavigationSetting.addValueObserver (value -> {
            this.flipTrackClipNavigation = ON_OFF_OPTIONS[1].equals (value);
            this.notifyObservers (FLIP_TRACK_CLIP_NAVIGATION);
        });

        final IEnumSetting flipClipSceneNavigationSetting = globalSettings.getEnumSetting ("Flip clip/scene navigation", CATEGORY_NAVIGATION, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        flipClipSceneNavigationSetting.addValueObserver (value -> {
            this.flipClipSceneNavigation = ON_OFF_OPTIONS[1].equals (value);
            this.notifyObservers (FLIP_CLIP_SCENE_NAVIGATION);
        });

        ///////////////////////////
        // Workflow

        this.activateNewClipLengthSetting (globalSettings);
        this.activateKnobSpeedSetting (globalSettings, 4);
    }


    /**
     * Get the selected function for the record button.
     *
     * @return The function index
     */
    public RecordFunction getRecordButtonFunction ()
    {
        return this.recordButtonFunction;
    }


    /**
     * Get the selected function for the shifted record button.
     *
     * @return The function index
     */
    public RecordFunction getShiftedRecordButtonFunction ()
    {
        return this.shiftedRecordButtonFunction;
    }


    /**
     * Returns true if track and clip navigation should be flipped
     *
     * @return True if track and clip navigation should be flipped
     */
    public boolean isFlipTrackClipNavigation ()
    {
        return this.flipTrackClipNavigation;
    }


    /**
     * Returns true if clip and scene navigation should be flipped
     *
     * @return True if clip and scene navigation should be flipped
     */
    public boolean isFlipClipSceneNavigation ()
    {
        return this.flipClipSceneNavigation;
    }
}
